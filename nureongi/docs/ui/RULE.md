# UI 규칙

## 0. 주석은 최대한 작성하지 않는다

- UI 코드는 컴포저블 이름, 파라미터 이름, 상태 이름만으로 의도가 드러나게 작성하고,
  설명용 주석은 최대한 달지 않는다.
- 주석은 복잡한 접근성 처리, 플랫폼 제약, 비직관적인 우회 구현처럼 코드만으로 이유를
  알기 어려운 경우에만 짧게 작성한다.
- 단순한 레이아웃 구조, 상태 호이스팅, 콜백 전달, Preview 설명을 반복하는 KDoc이나
  인라인 주석은 작성하지 않는다.

## 1. 공통 컴포넌트는 비즈니스 로직을 포함하지 않는다

- 공통(재사용) 컴포저블은 상태를 직접 들고 있거나 ViewModel·Repository·UseCase 등을
  참조하지 않는다. 필요한 값과 콜백은 모두 파라미터로 받는다(상태 호이스팅).
- 컴포저블 선언부는 다음을 지킨다.
  - 상태는 `value: T`, 이벤트는 `onXxx: () -> Unit` 형태로 분리해서 받는다.
  - `Modifier`는 기본값 `Modifier`로 받아 가장 먼저 선언하고, 호출 측에서 레이아웃을
    제어할 수 있게 한다.
  - 화면(스크린) 단위 컴포저블에서만 ViewModel을 참조하고, 그 아래 컴포넌트들은
    화면에서 내려준 상태/콜백만 사용한다(로직을 위로 끌어올리기, state hoisting).
  - 파라미터 타입은 도메인 모델을 직접 받지 않고, 화면에 필요한 값만 모은
    기본 타입(`String`, `Int`, `Boolean` 등)이나 UI 전용 DTO(`XxxUiModel`,
    `XxxUiState` 등)로 받는다. 도메인 모델 → UI 모델 변환은 화면/매퍼 계층의
    책임으로 두고, 공통 컴포넌트는 도메인 모델의 존재 자체를 몰라야 한다.
- 테스트 방법
  - 공통 컴포넌트가 정의된 패키지(`*.ui.component`, `*.designsystem` 등)에서
    `ViewModel`, `Repository`, `UseCase`, `inject`, `koinViewModel`, `hiltViewModel` 등의
    참조가 없는지 검색해 확인한다.
    예: `grep -rn "ViewModel\|Repository\|UseCase" shared/src/commonMain/kotlin/.../component`
  - 공통 컴포넌트의 함수 시그니처에 `domain` 패키지의 타입(예: `import ...domain.model...`)이
    파라미터로 들어오는지 검색해 확인한다.
    예: `grep -rn "fun .*(.*: .*domain\." shared/src/commonMain/kotlin/.../component`
    검색 결과가 있다면 해당 파라미터를 기본 타입 또는 UI 전용 DTO로 바꾼다.
  - 공통 컴포넌트에 대한 `@Preview`(또는 Compose Multiplatform Preview)를 작성해,
    ViewModel 의존성 없이 더미 상태값만으로 모든 상태(기본/선택/비활성/에러 등)를
    렌더링할 수 있는지 확인한다. Preview가 추가 설정 없이 바로 그려지면 로직 분리가
    잘 된 것이다.
  - Compose UI 테스트에서 실제 ViewModel 대신 가짜(fake) 상태와 람다만 주입해
    컴포넌트를 단독으로 테스트할 수 있는지 확인한다(테스트 작성이 어렵다면 컴포넌트가
    여전히 외부 상태에 의존하고 있다는 신호다).

## 2. 시각장애인을 위한 접근성을 최우선으로 고려한다

- 모든 상호작용 요소(`Button`, `IconButton`, `Card`, 리스트 아이템 등)에는
  `Modifier.semantics`/`contentDescription`/`role`을 통해 스크린 리더가 읽을 수 있는
  한글 설명을 제공한다. 의미 없는 장식 요소는 `clearAndSetSemantics {}` 또는
  `contentDescription = null` 로 명시적으로 제외한다.
- 안내 문구, 상태 변화(예: 도착, 다음 점형 블럭 안내)는 화면 표시와 동시에 TalkBack 등
  접근성 서비스로도 전달되도록 `liveRegion`(`LiveRegionMode.Polite`/`Assertive`) 등을
  활용한다.
- 색상만으로 정보를 구분하지 않는다(예: 선택 상태를 옐로우 보더만으로 표현하지 않고
  텍스트나 아이콘 변화도 함께 제공). 명도 대비는 WCAG AA 기준(최소 4.5:1, 큰 텍스트는
  3:1)을 만족해야 한다.
- 터치 타겟은 최소 48dp x 48dp를 확보한다.
- 테스트 방법
  - Android Studio의 **Accessibility Scanner**(또는 Layout Inspector의 접근성 패널)로
    각 화면을 검사해 `contentDescription` 누락, 터치 타겟 크기, 명도 대비 경고가
    없는지 확인한다.
  - 실제 기기/에뮬레이터에서 **TalkBack**을 켜고 화면을 끝까지 탐색하며, 모든 정보가
    한글 음성으로 정확하게 안내되는지, 포커스 이동 순서가 자연스러운지 확인한다.
  - Compose UI 테스트에서 `onNodeWithContentDescription(...)`,
    `assertIsDisplayed()`, `SemanticsMatcher` 등을 사용해 접근성 속성이 의도대로
    설정되어 있는지 검증하는 테스트를 작성한다.

## 3. 구글이 권장하는 샘플 앱을 참고해서 작성한다

- 새로운 화면/컴포넌트를 작성하기 전에 Google의 공식 아키텍처 가이드와 샘플 앱
  (예: [Now in Android](https://github.com/android/nowinandroid),
  [Compose 공식 샘플](https://github.com/android/compose-samples),
  [Android Architecture Samples](https://github.com/android/architecture-samples))의
  화면 구조, 상태 관리(UiState), 폴더 구조, 네이밍 컨벤션을 참고한다.
- 새로운 패턴을 도입할 때는 "이 구조가 공식 샘플의 어떤 패턴과 비슷한가"를 PR
  설명이나 커밋 메시지에 한 줄이라도 남긴다.
- 테스트 방법
  - PR 리뷰 체크리스트에 "참고한 공식 샘플/문서 링크"를 명시하는 항목을 추가하고,
    리뷰어가 해당 샘플의 구조와 비교해서 리뷰할 수 있도록 한다.
  - 화면의 상태 클래스(`UiState`), 이벤트 처리 방식(`onEvent`/`Action` sealed
    interface 등)이 공식 샘플과 동일한 패턴을 따르는지 코드 리뷰에서 직접 비교해
    확인한다.
