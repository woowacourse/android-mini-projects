# 누렁이 공통 UI 컴포넌트 설계 기록

`docs/ui/RULE.md`, `AGENTS.md`, `docs/ui` 스크린샷을 기준으로
`nureongi/shared/src/commonMain/kotlin/com/woowa/nureongi/ui/` 아래에
공통 컴포넌트를 새로 작성하면서 내린 판단과 그 근거, 장단점을 정리한다.

## 1. 패키지 구조: theme / model / component 분리

**결정**: `ui/theme`(디자인 토큰), `ui/model`(UI 전용 DTO), `ui/component`(공통 컴포넌트)
세 패키지로 나누었다.

- **근거**: RULE.md 1번 규칙("공통 컴포넌트는 비즈니스 로직을 포함하지 않는다")을 지키려면
  "표시할 값의 형태(모델)"와 "그리는 방법(컴포넌트)"을 물리적으로도 분리해 두는 편이
  서로 섞이는 것을 막기 쉽다고 판단했다. 컬러·타이포 같은 디자인 토큰도 컴포넌트
  내부에 흩어지면 다크 테마/접근성 대비 기준을 일괄로 바꾸기 어려워지므로 별도 패키지로
  뺐다.
- **장점**: 새로운 화면을 만들 때 "이 값을 UI 모델로 변환했는가", "이 색은 토큰에서
  가져왔는가"를 패키지 단위로 점검할 수 있다. RULE.md에 적은 grep 검증
  (`domain.` 패키지 import 검색 등)도 패키지 경계가 분명해야 의미가 있다.
- **단점/트레이드오프**: 아직 화면(스크린)이 하나도 없는 시점에 패키지부터 나누는 것은
  과설계로 보일 수 있다. 다만 "공통 컴포넌트를 먼저 만든다"는 이번 작업의 목적 자체가
  구조를 먼저 잡고 화면에서 재사용하는 흐름이라, 지금 단계에서 분리해 두는 것이
  나중에 한꺼번에 옮기는 비용보다 싸다고 판단했다.

## 2. 디자인 토큰: 색상 16진수 값을 직접 명시

**결정**: `NureongiColors`에 스크린샷에서 추정한 16진수 컬러 값을
(`Background = 0xFF121212`, `Accent = 0xFFFFC400` 등) 직접 정의했다.

- **근거**: 디자이너가 정한 정확한 컬러 값(피그마 등)을 받지 못한 상태였다.
  스크린샷을 보고 추정한 값을 쓰되, 한 곳(`NureongiColors`)에만 정의해 두면
  나중에 정확한 값으로 교체할 때 컴포넌트 코드를 건드릴 필요가 없다.
- **장점**: 색이 한 곳에 모여 있어 "명도 대비가 WCAG AA를 만족하는가"를 한 파일만
  보고 검토할 수 있다. RULE.md 2번 접근성 규칙과 직접 연결된다.
- **단점**: 지금 정의한 값은 추정치이므로, 실제 디자인 산출물이 나오면 반드시
  교체가 필요하다. (의도적으로 "추정값"이라는 사실을 숨기지 않기 위해, 이 문서와
  `RULE.md`에도 스크린샷 기준이라는 점을 명시해 두었다.)

## 3. UI 모델(DTO)을 기본 타입 조합으로 최소화

**결정**: `PlaceUiModel(name, location)`, `RouteNodeUiModel(row, column, label, state)`,
`StatItemUiModel(value, label)` 처럼 화면에 "그대로 표시할 문자열/열거형"만 담는
작은 DTO를 만들었다. 도메인의 장소·경로·거리 계산 모델은 참조하지 않는다.

- **근거**: RULE.md 1번 규칙에서 사용자가 별도로 강조한 "도메인 모델보다 기본값/DTO
  위주로 공통 컴포넌트를 만들 것"을 그대로 반영했다. 변환(도메인 → UI 모델)은
  화면/매퍼 계층의 책임으로 미뤄, 공통 컴포넌트는 도메인이 바뀌어도 영향을 받지 않게
  했다.
- **장점**: 컴포넌트의 `@Preview`를 ViewModel 없이 더미 DTO만으로 그릴 수 있다
  (실제로 모든 컴포넌트의 프리뷰가 이렇게 동작한다 — "Preview가 추가 설정 없이
  렌더링되는가"가 곧 로직 분리 검증 수단이 된다는 RULE.md의 테스트 방법을 그대로
  실천한 것).
- **단점/트레이드오프**: 화면에서 도메인 모델 → UI 모델 매핑 코드를 별도로 작성해야
  하는 비용이 생긴다. 다만 그 비용은 "공통 컴포넌트가 도메인 변화에 흔들리지 않는다"는
  이득과 맞바꾸는 것이라 판단했다.

## 4. 상태 호이스팅: 선택·활성화 상태를 모두 파라미터로 받음

**결정**: `PlaceListItem`의 `selected`, `CtaButton`의 `enabled`,
`NavigationTopBar`의 `currentStep` 등 모든 "현재 상태"를 컴포넌트 내부
`remember`로 갖지 않고 파라미터로 받았다. 내부에 상태를 둔 곳은 프리뷰 코드뿐이다.

- **근거**: RULE.md 1번 규칙의 "로직을 위로 끌어올리기(state hoisting)"를 위함이다.
  공통 컴포넌트가 상태를 가지면, 화면이 그 상태를 알아야 할 때 다시 끌어올려야 하는
  이중 작업이 생긴다.
- **장점**: 화면(ViewModel)이 단일 진실 공급원이 되고, 컴포넌트는 순수 함수에 가깝게
  유지된다. Compose UI 테스트에서 `selected = true/false`를 직접 주입해 모든 상태를
  손쉽게 검증할 수 있다.
- **단점**: 호출하는 쪽(화면)에서 상태 관리 코드가 늘어난다. 그러나 이는 Compose의
  권장 패턴(Now in Android 등 공식 샘플)과 일치하므로 RULE.md 3번 규칙과도
  부합한다고 판단했다.

## 5. 접근성: 장식 vs 정보 아이콘을 구분해서 시맨틱 처리

**결정**: `BrailleIcon`처럼 순수 장식인 요소는 `clearAndSetSemantics {}`로 스크린
리더에서 제외하고, `PlaceListItem`의 선택 상태는 `selected`, `CtaButton`의
비활성 상태는 `disabled()`, `SegmentedProgressIndicator`는 `progressBarRangeInfo`,
`DirectionGuideCard`/`StatInfoCard`는 `mergeDescendants` + `contentDescription`으로
정보를 하나의 의미 단위로 묶어 전달하도록 했다.

- **근거**: RULE.md 2번 규칙("시각장애인을 위한 접근성을 최우선으로 고려")을
  구체적인 코드로 옮긴 것이다. 정보가 없는 장식 요소까지 스크린 리더가 읽으면
  오히려 사용자의 탐색을 방해하므로, "읽어야 할 것"과 "읽지 않아도 될 것"을
  명확히 나누는 것이 중요하다고 판단했다.
- **장점**: TalkBack 등으로 테스트했을 때 "2번 출구, 지상 광장 방면, 선택됨"처럼
  자연스러운 문장으로 읽히고, 불필요한 "이미지" 같은 잡음이 줄어든다.
- **단점/한계**: 실제 기기에서 TalkBack으로 들어보지 않으면 의미 단위가 자연스러운지
  확신하기 어렵다. 이번 작업은 컴파일 검증까지만 했고, 실기기 음성 검증은
  RULE.md에 적은 "테스트 방법"으로 남겨 두었다 — 화면이 만들어진 뒤 반드시
  진행해야 한다.

## 6. CTA 버튼의 "강조 스타일"을 enum으로 분리

**결정**: 옐로우 배경(기본 CTA)과 화이트 배경("새 목적지 안내" 같은 보조 CTA)을
`CtaButtonEmphasis { ACCENT, NEUTRAL }` enum 파라미터로 표현했다.

- **근거**: 스크린샷상 두 버튼은 모양(둥근 모서리, 전체 너비, 텍스트 중앙 정렬)은
  같고 색상 의미만 다르다. 별도 컴포넌트로 쪼개면 모양이 바뀔 때 두 곳을 고쳐야
  하므로, "같은 모양 + 다른 의미"를 enum으로 표현하는 쪽을 택했다.
- **장점**: 새로운 강조 단계가 필요해지면 enum 값만 추가하면 되고, 레이아웃
  코드는 한 곳에만 있다.
- **단점**: enum 분기 로직(`when`)이 컴포넌트 내부에 생긴다. 강조 단계가
  지금처럼 2~3가지를 넘어 다양해지면, enum보다 색상을 직접 파라미터로 받는
  방식으로 리팩터링하는 편이 나을 수 있다 — 지금은 과설계를 피하기 위해
  최소 단위(enum)로 시작했다.

> **후기(16번 항목 참고)**: 실제로 `emphasis` 값은 끝까지 `ACCENT`/`NEUTRAL`
> 두 가지를 넘지 않았고, 병렬 리뷰에서 "실사용처가 1곳뿐인 enum"이라는
> 지적을 받아 `CtaButtonEmphasis` enum을 제거하고 `containerColor`/
> `contentColor`를 직접 받는 방식으로 리팩터링했다. 이 절의 "단점"에서
> 예상한 방향대로 바뀐 셈이라, 결정 자체보다는 "언제 다음 단계로 넘어갈
> 신호를 볼 것인가"를 미리 적어 둔 기록으로 남긴다.

## 7. RouteMapCard: 좌표 계산 없이 "이미 계산된 노드"만 그림

**결정**: `RouteMapCard`는 경로 탐색이나 좌표 변환을 하지 않고,
`RouteNodeUiModel(row, column, label, state)` 목록을 받아 `Canvas`로 점-선
다이어그램을 그리기만 한다. 배경 격자(점선)는 `rows x columns` 크기로 항상
그려지는 장식이고, 실제 경로는 `path` 목록 순서대로 선으로 이어 그린다.

- **근거**: 이 카드가 가장 "로직처럼 보이기 쉬운" 컴포넌트였다 — 실제 역사 구조,
  최단 경로 계산 등은 명백히 도메인 책임이다. RULE.md 1번 규칙을 지키려면
  "이미 계산된 결과를 어떻게 배치해서 그릴지"만 컴포넌트가 맡고, "어떤 경로가
  최적인지"는 절대 들어오지 않도록 경계를 분명히 그어야 한다고 판단했다.
- **장점**: 경로 탐색 알고리즘이 바뀌어도 이 컴포넌트는 전혀 영향을 받지 않는다.
  `Canvas` 기반이라 노드 수·격자 크기가 달라져도 같은 코드로 그릴 수 있다.
- **단점/한계**: `Canvas`로 직접 그리다 보니 다른 컴포넌트보다 코드가 길고,
  접근성 정보를 시각 요소 각각에 줄 수 없어 카드 전체를 하나의
  `contentDescription`(예: "한빛역 점자 블럭 지도 경로: 개찰구 → 갈림길 → 2번 출구")
  으로 뭉뚱그렸다. 더 정교한 음성 안내가 필요하면, 지도 자체보다는
  `DirectionGuideCard`의 문장형 안내에 정보를 싣는 쪽이 맞다고 본다.

## 8. Preview를 컴포넌트 파일에 private으로 동봉 (별도 파일 → 인라인으로 변경)

**결정**: 처음에는 `ComponentPreviews.kt` 한 파일에 모든 프리뷰를 모았다가,
사용자 요청에 따라 각 컴포넌트 파일 하단에 `private @Preview` 함수로 옮겼다.

- **근거**: "공통 컴포넌트가 ViewModel 없이 더미 상태만으로 그려지는가"를 검증하는
  수단이 프리뷰이므로, 검증 코드는 검증 대상 옆에 있는 것이 더 직관적이다
  (다른 사람이 컴포넌트를 고칠 때 프리뷰도 함께 보면서 고치게 된다).
- **장점**: 컴포넌트와 프리뷰가 같은 파일에 있어 변경 시 누락하기 어렵고,
  `private`이라 다른 곳에서 참조되지 않는다는 것이 코드만 봐도 드러난다.
- **단점**: 프리뷰끼리 공유하던 작은 헬퍼(공통 배경 Modifier 등)를 각 파일에서
  살짝씩 중복 작성하게 됐다. 중복은 각 파일이 3~5줄 수준으로 작아 추상화를
  도입할 정도는 아니라고 판단해 그대로 두었다("세 줄의 비슷한 코드가 섣부른
  추상화보다 낫다").

## 9. 빌드 검증 범위: commonMain 메타데이터 + Android 컴파일까지만

**결정**: `./gradlew :shared:compileCommonMainKotlinMetadata` 와
`:shared:compileAndroidHostTestSources` 두 태스크로 컴파일 성공만 확인했다.
실제 기기·에뮬레이터 실행이나 TalkBack 검증은 하지 않았다.

- **근거**: 이번 작업은 "화면이 아직 없는 상태에서 공통 컴포넌트를 먼저 만드는 것"이라
  실행 가능한 화면이 없다. 컴파일 검증으로 타입·시그니처 오류를 우선 잡고,
  RULE.md에 적어 둔 실기기 검증(Accessibility Scanner, TalkBack)은 화면이 붙는
  다음 단계에서 진행하는 것이 합리적이라고 판단했다.
- **장점**: 빠르게 반복하며 컴파일 오류(예: `Arrangement.spacedBy` 임포트 실수,
  `weight`가 `RowScope`/`ColumnScope` 확장 함수라 잘못 임포트하면 충돌하는 문제 등)를
  먼저 걷어낼 수 있었다.
- **한계**: 실제 다크 테마 명도 대비, 터치 타겟 크기, TalkBack 발화 순서는
  코드만으로는 확인할 수 없다. **이 부분은 화면을 조립한 뒤 RULE.md의 테스트
  방법대로 반드시 추가 검증이 필요하다.**

## 10. Preview 렌더링 오류 수정: 벡터 드로어블의 프레임워크 색상 참조 제거

**결정**: `ic_volume.xml`의 두 `path`에 쓰인 `android:fillColor="@android:color/transparent"`를
리터럴 ARGB 값 `#00000000`으로 교체했다.

- **근거**: `VoiceGuideButton`의 `@Preview`를 렌더링할 때
  `IllegalArgumentException: Invalid color value @android:color/transparent`가 발생했다.
  Compose Multiplatform 리소스 시스템(`org.jetbrains.compose.resources`)은 벡터
  드로어블을 자체 파서로 해석하기 때문에 `@android:color/...` 같은 안드로이드
  프레임워크 리소스 참조를 해석하지 못한다. 프레임워크에 의존하지 않는 리터럴
  hex 값으로 바꾸면 동일한 "완전 투명" 의미를 유지하면서 파싱 문제를 없앨 수 있다.
- **장점**: 추가 설정 없이 `painterResource`로 그대로 그릴 수 있고, 다른 플랫폼
  (iOS 등)에서도 동일하게 동작한다 — 애초에 멀티플랫폼 리소스에는 플랫폼 종속
  참조를 쓰지 않는 편이 안전하다.
- **단점/주의**: 새 벡터 드로어블을 추가할 때도 `@android:color/...`,
  `@color/...`처럼 프레임워크·앱 리소스를 참조하는 속성이 없는지 확인해야 한다는
  규칙이 하나 더 생긴 셈이다.

## 11. 네이밍 정비: `title`/`description` 같은 범용 이름을 역할이 드러나는 이름으로 교체

**결정**: 사용자가 지적한 대로, "표시 위치(제목/설명)"만 가리키는 범용 이름을
"무엇을 담는 값인지" 드러내는 이름으로 바꿨다.

- `PlaceUiModel`: `title → name`(장소 이름, 예: "1번 출구"),
  `description → location`(위치 정보, 예: "지상 · 버스정류장 방면")
- `DirectionGuideCard`: `title → instruction`(핵심 안내 문구, 예: "8m 직진"),
  `subtitle → landmark`(보충 설명·주변 지형지물, 예: "다음 점형 블럭 · 출구 갈림길"),
  `description → guideMessage`(전체 안내 문장)
- `PrimaryActionButton` → `CtaButton` (파일명 포함), `PrimaryActionButtonEmphasis` →
  `CtaButtonEmphasis`: "Primary"라는 이름이 `NEUTRAL`(보조 CTA) 변형과 모순되고,
  실제 역할은 "화면 하단에 고정되는 전체 너비 CTA 버튼"이므로 그 역할을 그대로
  담은 이름으로 바꿨다.
- `CurrentLocationBadge` → `CurrentLocationBar` (파일명 포함): "Badge"는 보통
  작은 상태 표시 칩을 가리키는데, 이 컴포넌트는 `fillMaxWidth` + `Row`로 가로
  전체를 차지하며 "현재 위치" 라벨 칩 + 위치 이름 + "변경 ›" 액션을 함께 담은
  정보 바에 가깝다(정작 "배지"라 부를 만한 건 내부의 작은 "현재 위치" 텍스트
  칩이다). 실제 크기·구성과 어긋나는 이름을 "전체 너비 정보 바"라는 역할에
  맞춰 바꿨다.

- **근거**: `title`/`subtitle`/`description`은 Compose 컴포넌트에서 흔히 쓰는
  슬롯 이름이라, 한 컴포넌트 안에 여러 개가 함께 있으면(`DirectionGuideCard`처럼)
  각각이 어떤 역할인지 이름만 보고는 구분할 수 없었다. 또한 `PrimaryActionButton`은
  이름이 "주(主) 액션"을 약속하지만 실제로는 보조 CTA(`NEUTRAL`)도 그리므로
  이름과 동작이 어긋났다.
- **장점**: 코드를 처음 보는 사람도 `instruction`/`landmark`/`guideMessage`처럼
  이름만으로 "이 값이 화면에서 어떤 역할을 하는지" 추론할 수 있다. `CtaButton`은
  강조 단계(emphasis)와 무관하게 "이 버튼은 CTA다"라는 사실만 약속하므로
  이름과 enum 옵션(`ACCENT`/`NEUTRAL`) 사이에 모순이 사라진다.
- **단점/주의**: `RouteNodeUiModel.label`처럼 "값이 가리키는 대상이 하나뿐이고
  이름이 그 역할을 그대로 드러내는" 경우는 바꾸지 않았다 — 모든 `title`/`label`을
  기계적으로 바꾸기보다는, "한 컴포넌트 안에서 여러 개의 범용 이름이 충돌하는가",
  "이름이 실제 동작과 모순되는가"를 기준으로 선별했다.

## 12. RouteMapCard: Canvas 그리기 로직을 의미 단위 private 함수로 분리

**결정**: `RouteMapCard`의 `Canvas` 람다 안에 한 덩어리로 있던 그리기 코드를
"배경 격자"와 "강조 경로"라는 두 책임으로 나눠 `DrawScope` 확장 함수
`drawBackgroundGrid`/`drawHighlightedRoute`로 추출했다. 두 함수가 공통으로
쓰는 좌표 변환은 최상위 `cellOffset` 함수로 뺐다.

- **근거**: 사용자가 "RouteMapCard가 너무 어렵다"고 지적한 부분으로, 60줄 가까운
  그리기 코드가 한 람다에 들어 있어 "지금 격자를 그리는 중인지 경로를 그리는
  중인지"를 코드만 보고 구분하기 어려웠다. 이름이 있는 함수로 쪼개면 컴포저블
  본문이 "격자를 그리고 → 경로를 그린다"는 두 줄의 흐름으로 요약된다.
- **장점**: 각 함수가 자기 책임(배경/경로)에만 집중해 더 짧고 읽기 쉬워졌고,
  필요하면 단위 테스트나 별도 프리뷰로 각 그리기 단계를 독립적으로 검증할 수
  있는 여지가 생겼다.
- **단점**: `DrawScope` 확장 함수로 빼면서 `cellWidth`/`cellHeight`/`textMeasurer`
  등을 매개변수로 명시적으로 넘겨야 해 호출부 코드가 살짝 길어졌다. 그러나
  "어떤 정보로 무엇을 그리는지"가 시그니처에 그대로 드러나는 이점이 더 크다고
  판단했다.

## 13. StatInfoCard/StatItemUiModel 제거, StatTile로 대체

**결정**: "여러 항목을 가로로 나눠 배치하는 책임"과 "값+라벨 한 쌍을 어떻게
그릴지(스타일·시맨틱)"가 `StatInfoCard` 한 함수에 묶여 있던 것을, 사용자
요청대로 후자만 `StatTile(value, label)`로 분리하고 전자(`StatInfoCard`)와
이를 위한 DTO(`StatItemUiModel`)는 통째로 삭제했다. 여러 타일을 가로로
늘어놓는 배치는 호출 측 화면이 `Row` + `Modifier.weight(1f)`로 직접 조립하도록
남겨 둔다.

- **근거**: `StatInfoCard`는 "남은 거리/남은 점형 블럭"이라는 특정 화면의
  레이아웃 형태(가로 2분할)를 공통 컴포넌트 안에 고정해 버렸다. 그런데 이
  레이아웃은 이미 Compose 표준 도구(`Row` + `weight`)만으로 화면에서 직접
  조립할 수 있는 단순한 조합이라, 그 위에 한 겹 더 감싸는 것이 "강제로 묶은
  느낌"(사용자 표현)을 줬다. 공통 컴포넌트는 "재사용 가치가 있는 가장 작은
  단위"(타일 한 장)까지만 책임지고, 배치는 화면의 자유에 맡기는 편이 RULE.md
  1번 규칙(불필요한 추상화를 끌어올리지 않는다)과도 맞는다고 판단했다.
- **장점**: `StatTile` 하나만 있으면 2분할이든 3분할이든, 가로든 세로든 화면이
  원하는 대로 배치할 수 있다. DTO(`StatItemUiModel`)도 사라져 "문자열 두 개를
  굳이 데이터 클래스로 감싸야 하는가"라는 간접 비용이 없어졌다.
- **단점/주의**: 화면마다 `Row`/`weight` 배치 코드를 반복해서 작성해야 한다.
  다만 그 코드는 2~3줄 수준이라("세 줄의 비슷한 코드가 섣부른 추상화보다
  낫다") 지금 단계에서는 감수할 만하다고 봤다 — 만약 나중에 동일한 배치가
  여러 화면에서 반복되면 그때 다시 레이아웃 컴포넌트로 끌어올리면 된다.

## 14. 네이밍 검토: `SegmentedProgressIndicator`는 그대로 유지

**결정**: "그냥 `ProgressIndicator`로 줄이는 게 어떤가"라는 검토 의견에 대해,
이름을 바꾸지 않고 `Segmented`를 유지하기로 했다.

- **근거**: Compose Material3에는 이미 `LinearProgressIndicator`/
  `CircularProgressIndicator`처럼 **연속적인 퍼센트 진행률**을 나타내는 표준
  컴포넌트가 있다. 반면 이 컴포넌트는 "3단계 중 1단계"처럼 **이산적인 단계를
  분할된 막대로** 보여주는 단계형 표시줄이다(`SegmentedProgressIndicator.kt`
  문서 주석 참고). `Segmented`는 바로 이 차이 — 연속 진행률이 아니라 단계형
  진행 표시라는 점 — 를 정확히 짚어주는 핵심 수식어이므로, 빼면 오히려
  Material3의 표준 진행률 표시와 혼동을 줄 위험이 있다고 판단했다.
- **장점**: 11번 항목에서 정한 "범용 이름이 충돌하거나 실제 동작과 모순될 때만
  바꾼다"는 기준을 그대로 적용한 사례다 — 모든 이름을 짧게 줄이는 것이 목표가
  아니라, 이름이 실제 역할을 정확히 드러내는지가 기준임을 재확인했다.
- **단점/주의**: 없음 — 이번 검토는 "바꾸지 않기로 한 결정"이므로 코드 변경은
  없었다.


## 15. 유지보수성 리뷰 반영: 컬러 토큰 정리 + Canvas 매직 넘버 상수화

**결정**: 15번 리뷰의 ④(유지보수성) 항목 중 "이름과 실제 용도가 어긋나거나
미사용인 토큰", "Canvas 기반 컴포넌트의 매직 넘버 클러스터" 두 가지를 바로 반영했다.

- **`NureongiColors.OnAccentSurface` → `NeutralSurface` 로 이름 변경**
  (`NureongiColors.kt`, `CtaButton.kt`): "On*" 접두사는 이 파일에서
  "그 위에 올라가는 전경색"을 뜻하는데(`OnAccent`, `OnDisabled`),
  `OnAccentSurface`는 실제로는 보조 CTA의 *컨테이너(배경)* 색으로 쓰여
  네이밍 컨벤션과 어긋났다. 역할이 드러나는 `NeutralSurface`로 바꾸고,
  KDoc에 "보조 CTA에서 강조색 대신 쓰는 화이트 컨테이너 색상"이라고 명시했다.
- **`NureongiColors.SurfaceSelected`, `NureongiTypography.ScreenTitle` 제거**:
  둘 다 어떤 컴포넌트·`MaterialTheme` 매핑에서도 실제로 읽히지 않는 죽은
  토큰이었다(`ScreenTitle`은 `titleLarge`에 매핑만 되어 있을 뿐, `titleLarge`를
  읽는 곳이 없었다). "나중에 쓸 건지 버려진 건지 판단할 수 없다"는 지적을
  반영해, 필요해지면 그때 다시 추가하기로 하고 제거했다.
- **`RouteMapCard`/`BrailleIcon`의 매직 넘버를 `private const val` 로 추출**:
  `RouteMapCard.kt`에는 격자 점 반지름·투명도·강조 노드 반지름·내부 구멍
  비율·라벨 오프셋 등 `GRID_DOT_RADIUS`/`HIGHLIGHTED_NODE_RADIUS`/
  `HIGHLIGHTED_NODE_HOLE_RADIUS_RATIO` 같은 이름의 상수 10개를 파일 상단에
  모았다. `BrailleIcon.kt`에는 `PADDING_RATIO`/`DOT_RADIUS_RATIO`(둘 다
  0.22f)를 분리해, "같은 값이지만 우연일 뿐 서로 무관하다"는 점을 KDoc으로
  명시했다.
- **근거**: 이름이 컨벤션과 어긋나거나 죽어 있는 토큰, 그리고 "하나를 바꾸면
  다른 곳도 따라 바뀌는지 알 수 없는" 매직 넘버 클러스터는 다음에 코드를
  만지는 사람이 가장 먼저 걸려 넘어지는 지점이라고 판단했다. 이 세 가지는
  동작 변경 없이 이름/구조만 정리하는 작업이라 위험이 작고, 따로 검증할
  화면도 필요 없어 바로 반영하기로 했다.
- **장점**: `OnAccentSurface`/`NeutralSurface`처럼 이름만 보고 용도를
  오해할 가능성이 줄었고, 죽은 토큰이 사라져 디자인 시스템을 살펴볼 때
  "이게 실제로 쓰이는 값인가"를 다시 확인할 필요가 없어졌다. `RouteMapCard`의
  노드 반지름·내부 구멍 비율처럼 서로 곱셈으로 얽혀 있던 값들도 이름이
  생기면서 관계가 코드에 드러난다(예: `radius * HIGHLIGHTED_NODE_HOLE_RADIUS_RATIO`).
- **단점/주의**: `NeutralSurface`는 여전히 `TextPrimary`와 같은 흰색
  값(`0xFFFFFFFF`)을 갖는다. 이름을 분리한 것은 "지금 같은 색이라도 보조
  CTA 배경과 본문 글자색은 서로 다른 의미로 바뀔 수 있다"는 의도를 코드에
  남기기 위함이며, 두 토큰을 하나로 합치는 것은 오히려 우연한 값의 일치를
  의미적 결합으로 착각하게 만들 수 있어 피했다.


## 5가지 관점 병렬 리뷰 결과 및 후속 조치

**결정**: `ui/component`, `ui/theme`, `ui/model` 전체를 다섯 가지 관점
— ① state ownership & flow, ② composition & reusability,
③ recomposition cost & stability, ④ 유지보수성, ⑤ 과설계 —
으로 나눠 병렬로 리뷰받고, 그중 일부를 실제 코드에 반영했다.

### 리뷰 결과 요약

- **① State ownership & flow**: 모든 production composable이 stateless하며
  단방향 데이터 흐름을 지킴. 다만 `RouteMapCard`의 `routeDescription`이
  매 recomposition마다 재계산되는 점, `NavigationTopBar`가 `currentStep`을
  그대로 `completedSteps`로 전달하는 암묵적 변환을 지적받았다.
- **② Composition & reusability**: 개별 컴포넌트는 단일 책임을 잘 지키지만,
  카드 컨테이너 스타일(`clip + background(Surface) + padding`)과
  "클릭 가능한 강조 텍스트 + 제목" 패턴이 `DirectionGuideCard`/`RouteMapCard`/
  `PlaceListItem`/`StatTile`/`CurrentLocationBar`/`NavigationTopBar` 등
  여러 파일에서 구조적으로 중복된다는 지적을 받았다. `BrailleIcon`의
  `rows`/`columns`, `CtaButton`의 `emphasis` when 매트릭스도 같이 언급되었다.
- **③ Recomposition cost & stability**: 대부분 컴포넌트가 primitive/소형
  data class만 받아 안정적이다. 유일하게 실질적인 지점은 `RouteMapCard`로,
  `List<RouteNodeUiModel>`이 컴파일러에 unstable 타입으로 인식되고
  `routeDescription`/`labelStyle`이 매번 재생성된다는 지적을 받았다.
- **④ 유지보수성**: `NureongiColors.OnAccentSurface`가 "On*" 네이밍
  컨벤션과 달리 컨테이너색으로 쓰이는 점, `SurfaceSelected`/`ScreenTitle`
  같은 미사용 토큰, `RouteMapCard`/`BrailleIcon`의 매직 넘버 클러스터,
  `NavigationTopBar`↔`SegmentedProgressIndicator` 간 `currentStep`/
  `completedSteps` 암묵적 변환이 지적되었다.
- **⑤ 과설계**: 화면이 아직 하나도 없는 시점에서, 실사용처가 없는
  `RouteNodeUiModel.State.NEUTRAL`, `NureongiColors.SurfaceSelected`,
  `BrailleIcon`의 `size`/`rows`/`columns`, `CtaButtonEmphasis` enum,
  `BackNavigationTopBar`, `DirectionGuideCard`의 `leadingIcon` 슬롯 등이
  "나중에 필요할 것 같아서" 미리 추가된 추측성 일반화로 지적되었다.

### 반영한 것 / 반영하지 않은 것

- **반영**: `CtaButton`의 `emphasis: CtaButtonEmphasis` 파라미터를 제거하고
  `containerColor`/`contentColor`를 직접 받도록 바꿨다. `enabled`에 따른
  색 보정만 `if` 식으로 남기고 `when` 분기를 없앴다. 이는 ②(emphasis
  when 매트릭스)와 ⑤(실사용 1곳뿐인 enum) 지적을 함께 해소한다.
- **반영**: `NavigationTopBar`의 `currentStep` KDoc과 `SegmentedProgressIndicator`
  호출부에 "현재 단계까지 채워진 것으로 표시한다(`currentStep == completedSteps`)"
  라는 설명을 추가해, ①·④에서 지적된 암묵적 변환을 문서화했다.
- **시도했다가 되돌림**: "클릭 가능한 강조 텍스트 + 제목" 중복(②)을 없애려고
  공유 `TappableLabel` 컴포저블을 추출했다가, 사용자 판단으로 다시 각 파일의
  인라인 구현으로 되돌렸다. 작은 보일러플레이트 3곳을 위해 새 공개 API를
  만드는 비용이 더 크다고 본 것으로, ⑤(과설계) 리뷰의 결론과도 같은 방향이다.
- **보류**: `RouteMapCard`의 `ImmutableList` 전환·`remember` 적용(③),
  카드 컨테이너 공통화(②), `RouteNodeUiModel.State.NEUTRAL`/`BrailleIcon`의
  `size`/`rows`/`columns`/`BackNavigationTopBar`/`leadingIcon` 슬롯 같은
  추측성 일반화 정리(⑤)는 이번에는 손대지 않았다. 화면이 만들어지면서 실제
  재사용·가변성 패턴이 드러난 뒤에 처리하는 편이, 지금 추측만으로 정리하는
  것보다 안전하다고 판단했다 (이는 ⑤ 리뷰가 짚은 "화면이 생기기 전에
  정리하라"는 제안과 다소 배치되지만, 최소한 신규 추가는 멈추고 기존 코드
  정리는 화면이 생긴 뒤로 미루는 절충을 택했다). 다만 이름이 실제 용도와
  어긋나거나 완전히 미사용인 토큰(④)은 16번 항목에서 바로 정리했다.
