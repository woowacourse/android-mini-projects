#ifndef CONFIG_EXAMPLE_H
#define CONFIG_EXAMPLE_H

// 이 파일은 예시 파일이다.
// 실제 빌드에는 config.h를 사용한다.

// Supabase Project URL
// 예: https://xxxxxxxxxxxxxxxxxxxx.supabase.co
const char* SUPABASE_URL = "https://YOUR_PROJECT_REF.supabase.co";

// Supabase anon key 또는 publishable key
// service_role key는 절대 ESP32에 넣지 않는다.
const char* SUPABASE_API_KEY = "YOUR_SUPABASE_ANON_OR_PUBLISHABLE_KEY";

// Supabase devices.device_code와 일치해야 한다.
const char* DEVICE_CODE = "device-code";

// Supabase devices.device_secret_hash 생성에 사용한 원문 secret과 일치해야 한다.
const char* DEVICE_SECRET = "pad-secret";

// HX711 calibration factor.
// 실제 로드셀/증폭기 조합에 맞춰 보정 후 수정한다.
const float LOAD_CELL_CALIBRATION_FACTOR = -7050.0f;

// 부팅 시 tare에 사용할 샘플 수.
const int LOAD_CELL_TARE_SAMPLES = 20;

#endif
