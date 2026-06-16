type DatabaseWebhookPayload = {
  type?: string;
  table?: string;
  schema?: string;
  record?: PottyRecord;
  new?: PottyRecord;
};

type PottyRecord = {
  id: string;
  device_id: string;
  record_type: "VISIT" | "URINE" | "STOOL" | string;
  occurred_at?: string;
};

type PushTokenRow = {
  id: string;
  fcm_token: string;
  platform: "android" | "ios" | string;
};

type ServiceAccount = {
  client_email: string;
  private_key: string;
  project_id?: string;
};

type OAuthToken = {
  access_token: string;
  expires_in?: number;
};

type CachedAccessToken = {
  token: string;
  expiresAt: number;
};

let cachedAccessToken: CachedAccessToken | null = null;

Deno.serve(async (request) => {
  try {
    console.log("notify-potty-record-insert request", {
      method: request.method,
      timestamp: new Date().toISOString(),
    });

    if (request.method === "OPTIONS") {
      return new Response(null, { status: 204 });
    }

    if (request.method !== "POST") {
      return json({ error: "METHOD_NOT_ALLOWED" }, 405);
    }

    if (!isValidWebhookSecret(request)) {
      console.warn("notify-potty-record-insert invalid webhook secret");
      return json({ error: "INVALID_WEBHOOK_SECRET" }, 401);
    }

    const payload = await request.json() as DatabaseWebhookPayload;
    const eventType = payload.type?.toUpperCase();
    console.log("notify-potty-record-insert payload", {
      type: payload.type,
      table: payload.table,
      schema: payload.schema,
      hasRecord: payload.record != null,
      hasNew: payload.new != null,
    });

    if (eventType != null && eventType !== "INSERT") {
      console.log("notify-potty-record-insert skipped non-insert event", { eventType });
      return json({ skipped: true, reason: "NOT_INSERT_EVENT" });
    }

    const record = payload.record ?? payload.new ?? (payload as PottyRecord);

    if (record == null || record.device_id == null || record.id == null) {
      console.warn("notify-potty-record-insert invalid payload", payload);
      return json({ error: "INVALID_WEBHOOK_PAYLOAD" }, 400);
    }

    const tokens = await getPushTokens(record.device_id);
    console.log("notify-potty-record-insert tokens", {
      recordId: record.id,
      deviceId: record.device_id,
      tokenCount: tokens.length,
    });

    if (tokens.length === 0) {
      return json({ sent: 0, failed: 0, removed: 0, reason: "NO_PUSH_TOKENS" });
    }

    const accessToken = await getFcmAccessToken();
    const results = await Promise.all(
      tokens.map((pushToken) => sendNotification(accessToken, pushToken, record)),
    );

    const invalidTokens = results
      .filter((result) => result.shouldRemoveToken)
      .map((result) => result.pushTokenId);

    await Promise.all(invalidTokens.map(deletePushToken));

    console.log("notify-potty-record-insert result", {
      sent: results.filter((result) => result.ok).length,
      failed: results.filter((result) => !result.ok).length,
      removed: invalidTokens.length,
    });

    return json({
      sent: results.filter((result) => result.ok).length,
      failed: results.filter((result) => !result.ok).length,
      removed: invalidTokens.length,
    });
  } catch (error) {
    console.error(error);
    return json(
      { error: error instanceof Error ? error.message : "UNKNOWN_ERROR" },
      500,
    );
  }
});

function isValidWebhookSecret(request: Request): boolean {
  const webhookSecret = Deno.env.get("WEBHOOK_SECRET");
  if (webhookSecret == null || webhookSecret.length === 0) return true;

  const headerSecret = request.headers.get("x-webhook-secret");
  const bearerSecret = request.headers.get("authorization")?.replace(/^Bearer\s+/i, "");
  const providedSecret = headerSecret ?? bearerSecret;

  return providedSecret === webhookSecret;
}

async function getPushTokens(deviceId: string): Promise<PushTokenRow[]> {
  const supabaseUrl = getRequiredEnv("SUPABASE_URL");
  const serviceRoleKey = getRequiredEnv("SUPABASE_SERVICE_ROLE_KEY");
  const url = new URL("/rest/v1/device_push_tokens", supabaseUrl);

  url.searchParams.set("select", "id,fcm_token,platform");
  url.searchParams.set("device_id", `eq.${deviceId}`);

  const response = await fetch(url, {
    headers: {
      apikey: serviceRoleKey,
      authorization: `Bearer ${serviceRoleKey}`,
    },
  });

  if (!response.ok) {
    throw new Error(`PUSH_TOKEN_QUERY_FAILED: ${await response.text()}`);
  }

  return await response.json() as PushTokenRow[];
}

async function sendNotification(
  accessToken: string,
  pushToken: PushTokenRow,
  record: PottyRecord,
) {
  const projectId = getFirebaseProjectId();
  const label = getRecordTypeLabel(record.record_type);
  const response = await fetch(
    `https://fcm.googleapis.com/v1/projects/${projectId}/messages:send`,
    {
      method: "POST",
      headers: {
        authorization: `Bearer ${accessToken}`,
        "content-type": "application/json; charset=utf-8",
      },
      body: JSON.stringify({
        message: {
          token: pushToken.fcm_token,
          notification: {
            title: "새 배변 기록",
            body: `${label} 기록이 감지됐어요.`,
          },
          data: {
            record_id: record.id,
            device_id: record.device_id,
            record_type: record.record_type,
            occurred_at: record.occurred_at ?? "",
          },
          android: {
            priority: "HIGH",
            notification: {
              channel_id: "dognal_events",
              sound: "default",
            },
          },
          apns: {
            headers: {
              "apns-priority": "10",
            },
            payload: {
              aps: {
                sound: "default",
              },
            },
          },
        },
      }),
    },
  );

  const responseBody = await response.text();

  if (!response.ok) {
    console.error("FCM_SEND_FAILED", response.status, responseBody);
  }

  return {
    ok: response.ok,
    pushTokenId: pushToken.id,
    shouldRemoveToken: !response.ok && isInvalidTokenResponse(response.status, responseBody),
  };
}

async function deletePushToken(tokenId: string) {
  const supabaseUrl = getRequiredEnv("SUPABASE_URL");
  const serviceRoleKey = getRequiredEnv("SUPABASE_SERVICE_ROLE_KEY");
  const url = new URL("/rest/v1/device_push_tokens", supabaseUrl);

  url.searchParams.set("id", `eq.${tokenId}`);

  const response = await fetch(url, {
    method: "DELETE",
    headers: {
      apikey: serviceRoleKey,
      authorization: `Bearer ${serviceRoleKey}`,
    },
  });

  if (!response.ok) {
    console.error("PUSH_TOKEN_DELETE_FAILED", await response.text());
  }
}

async function getFcmAccessToken(): Promise<string> {
  const now = Math.floor(Date.now() / 1000);

  if (cachedAccessToken != null && cachedAccessToken.expiresAt - 60 > now) {
    return cachedAccessToken.token;
  }

  const serviceAccount = getServiceAccount();
  const jwt = await createJwt(serviceAccount, now);
  const body = new URLSearchParams({
    grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer",
    assertion: jwt,
  });

  const response = await fetch("https://oauth2.googleapis.com/token", {
    method: "POST",
    headers: {
      "content-type": "application/x-www-form-urlencoded",
    },
    body,
  });

  if (!response.ok) {
    throw new Error(`FCM_OAUTH_FAILED: ${await response.text()}`);
  }

  const token = await response.json() as OAuthToken;
  cachedAccessToken = {
    token: token.access_token,
    expiresAt: now + (token.expires_in ?? 3600),
  };

  return token.access_token;
}

async function createJwt(
  serviceAccount: ServiceAccount,
  issuedAt: number,
): Promise<string> {
  const header = base64UrlEncodeJson({
    alg: "RS256",
    typ: "JWT",
  });
  const payload = base64UrlEncodeJson({
    iss: serviceAccount.client_email,
    scope: "https://www.googleapis.com/auth/firebase.messaging",
    aud: "https://oauth2.googleapis.com/token",
    iat: issuedAt,
    exp: issuedAt + 3600,
  });
  const unsignedJwt = `${header}.${payload}`;
  const privateKey = await importPrivateKey(serviceAccount.private_key);
  const signature = await crypto.subtle.sign(
    "RSASSA-PKCS1-v1_5",
    privateKey,
    new TextEncoder().encode(unsignedJwt),
  );

  return `${unsignedJwt}.${base64UrlEncode(signature)}`;
}

async function importPrivateKey(privateKey: string): Promise<CryptoKey> {
  const keyData = pemToArrayBuffer(privateKey);

  return await crypto.subtle.importKey(
    "pkcs8",
    keyData,
    {
      name: "RSASSA-PKCS1-v1_5",
      hash: "SHA-256",
    },
    false,
    ["sign"],
  );
}

function pemToArrayBuffer(pem: string): ArrayBuffer {
  const base64 = pem
    .replace("-----BEGIN PRIVATE KEY-----", "")
    .replace("-----END PRIVATE KEY-----", "")
    .replaceAll(/\s/g, "");
  const binary = atob(base64);
  const bytes = new Uint8Array(binary.length);

  for (let i = 0; i < binary.length; i += 1) {
    bytes[i] = binary.charCodeAt(i);
  }

  return bytes.buffer;
}

function getServiceAccount(): ServiceAccount {
  return JSON.parse(getRequiredEnv("FCM_SERVICE_ACCOUNT_JSON")) as ServiceAccount;
}

function getFirebaseProjectId(): string {
  const projectId = Deno.env.get("FIREBASE_PROJECT_ID") ?? getServiceAccount().project_id;

  if (projectId == null || projectId.length === 0) {
    throw new Error("MISSING_FIREBASE_PROJECT_ID");
  }

  return projectId;
}

function getRecordTypeLabel(recordType: string): string {
  switch (recordType) {
    case "VISIT":
      return "방문";
    case "URINE":
      return "소변";
    case "STOOL":
      return "대변";
    default:
      return "배변";
  }
}

function isInvalidTokenResponse(
  status: number,
  responseBody: string,
): boolean {
  if (status === 404 && responseBody.includes("UNREGISTERED")) return true;
  if (responseBody.includes('"errorCode":"UNREGISTERED"')) return true;
  return responseBody.includes("Requested entity was not found");
}

function getRequiredEnv(name: string): string {
  const value = Deno.env.get(name);

  if (value == null || value.length === 0) {
    throw new Error(`MISSING_ENV_${name}`);
  }

  return value;
}

function base64UrlEncodeJson(value: unknown): string {
  return base64UrlEncode(new TextEncoder().encode(JSON.stringify(value)));
}

function base64UrlEncode(value: ArrayBuffer | Uint8Array): string {
  const bytes = value instanceof Uint8Array ? value : new Uint8Array(value);
  let binary = "";

  for (const byte of bytes) {
    binary += String.fromCharCode(byte);
  }

  return btoa(binary)
    .replaceAll("+", "-")
    .replaceAll("/", "_")
    .replaceAll("=", "");
}

function json(
  body: Record<string, unknown>,
  status = 200,
): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: {
      "content-type": "application/json; charset=utf-8",
    },
  });
}
