# 카페 키오스크 주문 및 회원 포인트·등급 관리 시스템

> 객체지향프로그래밍 팀 프로젝트 (9~15주차)
> **(TODO)** 아래 `(팀원 이름)`, `(TODO)` 표시는 팀에서 채운다. 제출 전에 `TODO`를 검색해서 남은 곳이 없는지 확인한다.

## 1. 프로젝트 개요

| 항목 | 내용 |
|---|---|
| 프로젝트명 | 카페 키오스크 주문 및 회원 포인트·등급 관리 시스템 |
| 개발 목적 | 객체지향 4대 원리(은닉성·상속성·다형성·추상성)를 실제 프로그램 구조에 적용 |
| 주제 구분 | 팀별 자유 주제 |
| 구현 형태 | Java 콘솔 애플리케이션 (여건이 되면 웹 확장) |

### 주요 기능

1. **메뉴 조회**: 커피, 차, 스무디, 디저트
2. **주문 생성**: 사이즈, 온도, 수량, 옵션 조합(샷 추가, 시럽, 휘핑크림)
3. **회원 관리**: 가입·조회, 등급별 할인·포인트 적립, 누적 결제 금액에 따른 자동 승급 (BRONZE → SILVER → GOLD)
4. **할인·결제**: 등급 할인, 쿠폰(정액/정률), 카드·포인트 결제
5. **주문 내역·영수증·취소**: 취소 시 환불과 적립 포인트 환원

### 실행 화면 예시 (GOLD 회원, 아이스 아메리카노 L + 샷 추가 + 시럽, 쿠폰 사용)

```
======================================
주문번호: O-0002   2026-11-05 05:41
고객: 이골드 (GOLD)
--------------------------------------
아이스 아메리카노 L              4,000
  + 샷 추가                        500
  + 시럽                           300
--------------------------------------
소계                             4,800
GOLD 등급 할인                    -240
쿠폰 (WELCOME500)                 -500
--------------------------------------
결제 금액 (카드)                 4,060
적립 포인트                        203
======================================
```

## 진행 현황

> 현재 13주차. 상태는 팀이 작업할 때마다 직접 갱신한다. 세부 작업은 [Issues](../../issues)에서 관리한다.

| 주차 | 계획 | 상태 |
|---|---|---|
| 9주차 | 주제 선정, 핵심 기능 정의, 역할 분담 | 완료 |
| 10주차 | 개발 환경 결정, 기본 클래스와 초기 기능 구현 | 완료 (초안 코드를 저장소에 업로드) |
| 11주차 | 클래스 다이어그램 작성(초기 설계 보관), 상속·추상 클래스·인터페이스 적용 | 다이어그램 초안 있음 (팀 검토 후 갱신) |
| 12주차 | SOLID 설계 검토와 코드 개선, 개선 내용 README 기록 | 진행 중 |
| 13주차 | 핵심 기능 완성, 기능 연계, 중간 점검(웹 확장 여부 결정) | 진행 중 |
| 14주차 | 기능 테스트, 오류 수정, 발표자료 준비 | 예정 |
| 15주차 | 최종 발표, 시연 | 예정 |

## 2. 팀 구성

| 담당 | 팀원 | GitHub | 역할 | 주요 클래스 |
|---|---|---|---|---|
| A | 조민규 | (TODO) | 메뉴·옵션 | `MenuItem` 계층, `OptionDecorator` 계층 |
| B | 신준서 | (TODO) | 회원·등급·포인트 | `Member`, `GuestMember`, `MembershipGrade` 구현체 |
| C | 심은지 | `eunz-711` | 할인·결제 | `DiscountPolicy` 구현체, `Payment` 계층 |
| D | 정건우 | `wjdrjsdn032607-creator` | 주문·연결·콘솔 UI | `Order`, `OrderService`, `Receipt`, `ConsoleApp` |

공통 책임: 자기 파트 JUnit 테스트, 예외 처리, README의 OOP 적용 이유 작성, AI 활용 기록, 발표 시 자기 파트 설명.

## 3. 개발 환경

| 항목 | 내용 |
|---|---|
| 언어 | Java 17 이상 (초안은 JDK 21로 확인) |
| 개발 도구 | IntelliJ IDEA |
| 빌드 | Gradle 8.14 (Wrapper 포함, 별도 설치 불필요) |
| 라이브러리 | 실행 코드는 표준 라이브러리만 사용, 테스트는 JUnit 5 |

## 4. 프로젝트 폴더 구조

```
cafe-kiosk/
├─ build.gradle, settings.gradle, gradlew(.bat)
├─ README.md
├─ docs/
│   ├─ class-diagram.md        클래스 다이어그램 (Mermaid)
│   └─ ai-usage-log.md         생성형 AI 활용 기록
└─ src/
    ├─ main/java/cafe/
    │   ├─ Main.java           객체 조립(의존성 주입) 후 실행
    │   ├─ SampleData.java     시연용 샘플 메뉴·회원·쿠폰
    │   ├─ common/             CafeException (업무 규칙 위반 예외)
    │   ├─ domain/
    │   │   ├─ menu/           MenuItem, Beverage, Coffee, Tea, Smoothie, Dessert, OptionDecorator, ExtraShot, Syrup, WhippedCream, Option
    │   │   ├─ member/         Customer, Member, GuestMember, MembershipGrade, RateBasedGrade, Bronze, Silver, Gold, GuestGrade
    │   │   ├─ discount/       DiscountPolicy, GradeDiscount, Coupon, AmountCoupon, PercentCoupon
    │   │   ├─ payment/        Payment, CardPayment, PointPayment, PaymentType
    │   │   └─ order/          Order, OrderLine, OrderStatus, DiscountLine, Receipt
    │   ├─ repository/         저장소 인터페이스 4개와 메모리 구현
    │   ├─ service/            OrderService, MemberService, MenuService, 요청 객체
    │   └─ ui/console/         ConsoleApp, ConsoleInput, MenuScreen, OrderScreen, MemberScreen
    └─ test/java/cafe/         JUnit 5 테스트 (domain, service)
```

| 패키지 | 역할 | 규칙 |
|---|---|---|
| `domain` | 규칙과 계산을 가진 객체 | UI·저장 방식을 모른다. `System.out`을 쓰지 않는다. |
| `repository` | 저장 방식(현재는 메모리) | 인터페이스로 분리해서 DB로 바꿔도 서비스는 그대로 |
| `service` | 객체를 연결하는 흐름 | 계산이나 종류별 `if/else`를 두지 않는다. |
| `ui.console` | 입력과 출력 | 계산하지 않는다. 웹을 얹을 때는 `ui.web`을 추가한다. |

클래스별 담당과 설명은 [`docs/class-owners.md`](docs/class-owners.md)에 정리되어 있다.

## 5. 실행 방법

### 방법 1: IntelliJ IDEA

1. `File > Open`으로 `cafe-kiosk` 폴더를 열고 Gradle 프로젝트로 불러온다. (JDK 17 이상 선택)
2. `src/main/java/cafe/Main.java`를 열고 `main` 옆의 실행 버튼을 누른다.
3. 아래쪽 **Run 창을 클릭한 뒤** 번호를 입력하고 Enter를 누른다.

한글이 `???`로 보이면 `Help > Edit Custom VM Options`에 `-Dfile.encoding=UTF-8`을 추가하고 IntelliJ를 재시작한다.

> **Windows 주의:** 프로젝트를 OneDrive 폴더나 한글이 들어간 경로에 두면 테스트가 `ClassNotFoundException`으로 실패하거나 `build` 폴더를 지우지 못하는 오류가 난다. `C:\Users\<사용자>\cafe-kiosk`처럼 영문 경로에 두고, 옮긴 뒤에는 `build`, `.gradle` 폴더를 지우고 다시 빌드한다.

### 방법 2: 터미널 (Gradle)

```bash
./gradlew run          # Windows: gradlew.bat run
./gradlew test         # 테스트 실행
```

Windows 터미널에서 한글이 깨지면 먼저 `chcp 65001`을 실행한다.

### 시연용 샘플 데이터

| 구분 | 내용 |
|---|---|
| 회원 | `M001` 홍길동(BRONZE), `M002` 김실버(SILVER, 누적 12만 원), `M003` 이골드(GOLD, 누적 35만 원, 8,000P) |
| 쿠폰 | `WELCOME500`(500원 할인), `SPECIAL10`(10% 할인), 각각 1회만 사용 가능 |

### 시연 시나리오 (예시)

1. `2` 주문하기 → `1` 아메리카노 → `3` L → `1` 아이스 → `샷추가, 시럽` → 수량 `1` → `0` 확정
2. 회원 번호 `M003`, 쿠폰 `WELCOME500`, 결제 `1` 카드 → 영수증 확인
3. `3` → `2` → `M003`으로 포인트·누적 금액 확인
4. `4` 주문 내역 → 방금 주문번호 입력해서 취소 → 포인트 환원 확인

## 6. OOP 적용 내용

### 은닉성

| 클래스 | 보호하는 상태 | 변경 방법 | 이유 |
|---|---|---|---|
| `Member` | `points`, `totalSpent`, `grade` | `earn()`, `usePoints()`, `restorePoints()`, `reverseEarn()` | 포인트 부족·음수를 객체가 스스로 막고, 승급이 `earn()` 안에서만 일어나게 한다. |
| `Order` | `status`, `discountLines`, `items` | `checkout()`, `cancel()`, `addItem()` | 결제 전 취소, 이중 취소, 결제 후 메뉴 추가 같은 잘못된 상태 전이를 막는다. |
| `Coupon` | `used` | `onApplied()`, `onCancelled()` | 결제가 성공했을 때만 사용 처리되고, 실패하면 쿠폰이 소모되지 않는다. 주문을 취소하면 다시 쓸 수 있게 돌아온다. |
| `MenuItem` 계층 | 가격·이름 | 생성자 검증 | 음수 가격, 빈 이름으로는 객체를 만들 수 없다. |

### 상속성

| 부모 | 자식 | 부모가 제공하는 공통 부분 | 자식이 다르게 하는 부분 |
|---|---|---|---|
| `MenuItem` | `Beverage`, `Dessert`, `OptionDecorator` | id, 이름, 기본 가격, 검증 | 가격 계산, 사이즈·옵션 지원 여부 |
| `Beverage` | `Coffee`, `Tea`, `Smoothie` | 사이즈, 온도, 가격 공식 | 가능한 온도, 사이즈 추가 요금(`Smoothie`는 L에 +500) |
| `OptionDecorator` | `ExtraShot`, `Syrup`, `WhippedCream` | 감싸기, 가격 합산, 영수증 줄 | 옵션 이름과 요금 |
| `RateBasedGrade` | `Bronze`, `Silver`, `Gold`, `GuestGrade` | 할인율·적립율 계산 | 승급 조건, `Gold`의 보너스 포인트 |
| `Coupon` | `AmountCoupon`, `PercentCoupon` | 유효기간·사용 여부 검사 | 할인 금액 계산 |
| `Payment` | `CardPayment`, `PointPayment` | 금액 검증 흐름(Template Method) | 실제 결제·환불 처리 |

### 다형성

| 위치 | 코드 | 설명 |
|---|---|---|
| `Order.subtotal()` | `items.stream().mapToInt(MenuItem::price)` | 커피, 디저트, 옵션이 붙은 메뉴 모두 같은 호출로 계산 |
| `Order.checkout()` | `for (DiscountPolicy policy : policies) policy.discount(running)` | 등급 할인과 쿠폰을 구분하지 않고 순서대로 적용 |
| `Order.checkout()` | `payment.pay(running)` | 카드인지 포인트인지 모르고 결제 |
| `Member.earn()` | `grade.earnPoints(paidAmount)` | 등급마다 다른 적립 규칙 (`Gold`는 보너스) |
| `OrderService.buildItem()` | `option.applyTo(item)` | 옵션 종류를 `if/else`로 나누지 않고 Decorator가 처리 |

### 추상성

| 추상 타입 | 종류 | 규약 | 구현체 |
|---|---|---|---|
| `MenuItem` | 추상 클래스 | `price()`, `description()` | `Coffee`, `Tea`, `Smoothie`, `Dessert`, `ExtraShot` ... |
| `MembershipGrade` | 인터페이스 | `discount()`, `earnPoints()`, `evaluate()` | `Bronze`, `Silver`, `Gold`, `GuestGrade` |
| `DiscountPolicy` | 인터페이스 | `name()`, `discount()`, `onApplied()`, `onCancelled()` (default) | `GradeDiscount`, `Coupon` 계열 (`AmountCoupon`, `PercentCoupon`) |
| `Payment` | 추상 클래스 | `doPay()`, `doRefund()` | `CardPayment`, `PointPayment` |
| `Customer` | 인터페이스 | `earn()`, `usePoints()` 등 | `Member`, `GuestMember` (비회원을 `null`이 아닌 객체로) |

### 새 유형을 추가하려면? (발표 질문 대비)

| 추가할 것 | 만들 클래스 | 수정할 곳 |
|---|---|---|
| 새 등급 (예: VIP) | `RateBasedGrade`를 상속한 `Vip` | `Gold.evaluate()`의 반환 한 줄 (또는 승급 조건) |
| 새 옵션 (예: 오트밀크) | `OptionDecorator`를 상속한 `OatMilk` | `Option` enum에 한 줄 |
| 새 메뉴 종류 (예: 샌드위치) | `MenuItem`을 상속한 `Sandwich` | `SampleData`에 메뉴 추가 |
| 새 할인 (예: 생일 할인) | `DiscountPolicy`를 구현한 클래스 | `Main`의 `automaticPolicies`에 추가 |
| 새 결제 수단 (예: 간편결제) | `Payment`를 상속한 클래스 | `PaymentType` enum에 한 줄 |

`Order`, `OrderService`는 수정하지 않는다. (개방-폐쇄 원칙)

### 그 외 학습 내용 활용

| 내용 | 사용한 곳 |
|---|---|
| 생성자(값 검증) | `MenuItem`, `Member`, `Coupon` |
| 메소드 오버라이딩 | `Smoothie.sizeSurcharge()`, `Gold.earnPoints()`, 각 `price()` |
| 컬렉션 | `List<MenuItem>`, `Map`을 쓰는 `InMemory*Repository`, `EnumSet` |
| 패키지 | `domain`, `repository`, `service`, `ui.console` |
| 제네릭·함수형 | `Optional<T>` 반환 저장소, enum의 `UnaryOperator`·`Function` 팩토리, 스트림 |

## 7. 생성형 AI 활용

| 항목 | 내용 |
|---|---|
| 사용 도구 | Claude (Anthropic) |
| 활용 수준 | **(TODO) 팀에서 확정.** 초안 생성 시점은 3단계에 해당 |
| 주요 활용 내용 | 주제 선정 논의, 클래스 설계, 코드·테스트·문서 초안 생성 |
| 팀에서 검토·수정한 부분 | (심은지) 해피아워 기능 제외: 실제 시각에 따라 결과가 달라져 시연·테스트가 불안정해서 팀 결정으로 뺌. 콘솔로 직접 주문해 보며 AI 초안의 문제 7개를 찾아 고침 (결제 실패 시 주문번호 소비, 영수증 수량 표시, 포인트 결제 적립, 취소 시 등급·쿠폰 복구, 회원번호·쿠폰 입력 오류 처리, 영수증 등급 고정. 자세한 내용은 9장). 고칠 때마다 JUnit 테스트를 추가해 확인 (46개 → 53개)<br>**(TODO)** 다른 팀원이 검토·수정한 내용을 이어서 적는다 |
| 팀에서 직접 결정한 설계 | **(TODO)** 근거는 `docs/ai-usage-log.md`의 3번 표에 작성 |

자세한 주차별 기록은 [`docs/ai-usage-log.md`](docs/ai-usage-log.md)에 있다.

## 8. 기능 테스트 결과

### 자동 테스트 (JUnit 5, `./gradlew test`)

| 테스트 클래스 | 개수 | 확인 내용 |
|---|---|---|
| `MenuItemTest` | 10 | 사이즈·옵션 가격, 스무디 온도 제한, 디저트 옵션 금지, 옵션 겹치기 |
| `MemberTest` | 10 | 적립, 자동 승급, 골드 보너스, 포인트 부족, 적립 취소, 취소 시 등급 재계산, 비회원 |
| `DiscountPolicyTest` | 5 | 등급 할인, 쿠폰(정액/정률/만료/재사용) |
| `OrderTest` | 13 | 할인 순서와 금액, 취소·환원, 취소 시 쿠폰 복구, 결제 실패 시 상태 유지, 포인트 결제 적립 제외, 영수증 등급 고정 |
| `OrderServiceTest` | 15 | 주문 전체 흐름, 없는 회원·메뉴·쿠폰, 취소 후 같은 쿠폰 재사용, 내역 조회, 결제 실패 시 주문번호 유지, 수량 표시, 결제 전 회원·쿠폰 확인 |
| **합계** | **53** | 53개 모두 통과 |

> 팀 환경 실행 결과: 2026-11-25, Windows 11 · JDK 21 · IntelliJ에서 `src/test` 전체 실행, **53개 모두 통과** (심은지)

### 콘솔 기능 점검 (수동)

| 기능 | 확인 방법 | 결과 |
|---|---|---|
| 메뉴 조회 | 메인 `1` | 정상 |
| 옵션 조합 주문 | 아메리카노 L + 샷 추가 + 시럽 | 정상 (4,800원) |
| 등급 할인·쿠폰·적립 | GOLD + `WELCOME500` | 정상 (4,060원 결제, 203P 적립) |
| 포인트 결제 / 취소 환원 | GOLD가 쿠키를 포인트로 결제 후 취소 | 정상 (8,203P로 복구) |
| 취소 후 쿠폰 재사용 | `WELCOME500`으로 주문 → 취소 → 같은 쿠폰으로 다시 주문 | 정상 (다시 500원 할인) |
| 회원 조회 | `M003` | 정상 |
| 잘못된 입력 | 글자·범위 밖 숫자 | 다시 입력 요청 (프로그램 종료 없음) |
| 회원번호·쿠폰 오타 | `M0003`, `WELCOM500` | 그 자리에서 다시 입력 요청 (엔터는 비회원·쿠폰 없음) |
| 결제 실패 | 포인트 0인 회원이 포인트 결제 | 결제 수단 다시 고르기 또는 주문 취소 선택 |
| 같은 메뉴 여러 개 | 초코 쿠키 수량 3 | 영수증에 `초코 쿠키 x3  7,500` 한 줄 |

### 예외 처리 목록

| 상황 | 처리 | 담당 |
|---|---|---|
| 포인트 부족 | `CafeException` ("포인트가 부족해요") | B |
| 비회원이 포인트 결제 | `CafeException` | B |
| 존재하지 않는 회원·메뉴·쿠폰·주문 | `CafeException` | D |
| 만료·사용 완료 쿠폰 | `CafeException` | C |
| 카드 한도 초과 | `CafeException` | C |
| 스무디 핫 선택, 디저트 옵션 | `IllegalArgumentException` | A |
| 알 수 없는 옵션·사이즈 이름 | `IllegalArgumentException` (콘솔은 다시 입력 요청) | A |
| 이미 취소된 주문 재취소, 결제 전 취소 | `CafeException` | D |
| 메뉴 없는 주문 결제 | `CafeException` | D |

### 개선 후보

- 데이터는 메모리에만 저장되어 프로그램을 종료하면 사라진다.

## 9. 객체지향 설계 개선 내용

초기 설계는 [`docs/class-diagram.md`](docs/class-diagram.md)에 있다. 개선할 때마다 아래 표에 기록한다.

| 시점 | 초기 설계 | 변경 | 변경 이유 (SOLID 원칙 등) |
|---|---|---|---|
| 초안 (9주차) | `OrderService`가 아닌 도메인 객체가 계산 | - | 설계 시작점 |
| 11~12주차 | 등급 3개(`Bronze`, `Silver`, `Gold`)가 `MembershipGrade`를 바로 구현. 초안에서도 "값만 다르면 형식적 상속"이라고 경고함 | `RateBasedGrade` 추상 클래스에 비율 계산을 모으고, 등급마다 승급 규칙 `evaluate()`를 다르게 구현. `Gold`는 1만 원 이상 결제 시 보너스 100P를 더함 | 같은 계산은 부모에 한 번만 두고(중복 제거), 자식은 실제로 다른 규칙만 가짐. **개방-폐쇄**: 새 등급(예: Platinum)은 클래스 하나만 추가 |
| 11~12주차 | `Member`, `GuestMember`가 등급과 같이 묶여 있고 비회원 처리 방법이 정해지지 않음 | `Customer` 인터페이스를 두고 `Member`, `GuestMember`가 구현. 비회원은 할인·적립이 0인 `GuestMember` 객체(Null Object) | `Order`에 `if (비회원)` 분기가 없음. **리스코프 치환**: 회원 자리에 비회원을 넣어도 그대로 동작. (`GuestMember`의 빈 메소드는 **인터페이스 분리** 관점에서 이슈 2로 검토) |
| 11~12주차 | 쿠폰은 `CouponDiscount` 하나, 등급 할인은 `MembershipGrade`에서 따로 계산 | `Coupon` 추상 클래스가 유효기간·사용 여부를 검사하고(`discount()`는 `final`), `AmountCoupon`, `PercentCoupon`은 `calculate()`만 구현. 등급 할인도 `GradeDiscount`로 만들어 같은 할인 목록에서 등급 → 쿠폰 순서로 적용 | **단일 책임**: 공통 검사는 `Coupon` 한 곳. **개방-폐쇄**: 새 쿠폰 종류는 `calculate()`만 구현하면 되고, 새 할인은 목록에 넣기만 하면 `Order`는 그대로 |
| 11~12주차 | 메뉴·회원·주문·쿠폰을 어디에 보관할지 설계에 없음 | `MenuRepository` 등 저장소 인터페이스 4개와 `InMemory…` 구현을 만들고, `OrderService`는 인터페이스에만 의존 | **의존성 역전**: 서비스가 "메모리에 저장"이라는 구체 방식을 모름. 나중에 파일·DB 저장으로 바꿔도 서비스는 그대로 |
| 13주차 | 해피아워(14~16시 10%)를 자동 할인으로 적용 | 해피아워 제외, `automaticPolicies`는 빈 목록으로 유지 | 실제 시각에 따라 결과가 달라져 시연과 테스트가 불안정. 팀 결정으로 제외했고, 자동 할인을 넣는 자리는 남겨서 새 할인(예: 생일 할인)은 `Main`에 한 줄 추가로 가능 (**개방-폐쇄**) |
| 13주차 | 결제가 실패해도 주문번호가 하나 소비됨 | `nextId()`는 다음 번호를 보기만 하고, 번호는 `save()`할 때 확정 | 실패한 주문 때문에 번호가 건너뛰면 기록이 헷갈림. **단일 책임**: 번호를 정하는 일은 저장소 한 곳만 맡음 |
| 13주차 | 같은 메뉴를 여러 개 시키면 영수증에 같은 줄이 반복됨 | `OrderLine`(메뉴, 수량) record를 만들어 `x3`처럼 한 줄로 표시 | 영수증 가독성. **단일 책임**: 영수증용으로 묶어 보여주는 일은 `OrderLine`이 맡고, `Order`의 저장 방식은 그대로 둠 |
| 13주차 | 포인트로 낸 금액에도 포인트가 적립됨, 취소해도 등급이 그대로 | `Payment.earnableAmount()`를 두고 `PointPayment`만 0을 돌려줌. 취소 시 누적 금액으로 등급을 다시 계산 | `Order`에 `if (포인트 결제)`를 넣지 않고 결제 클래스가 스스로 결정 (**다형성**, **개방-폐쇄**: 새 결제 수단은 클래스만 추가하면 `Order`는 그대로) |
| 13주차 | 회원번호·쿠폰 오타가 결제 단계에서야 실패해서 처음부터 다시 주문 | `OrderService.checkMember()`, `checkCoupon()`으로 입력 즉시 확인, 결제 실패 시 결제 수단 다시 선택 | 사용자 입력 실수 처리. **단일 책임**: 쿠폰을 쓸 수 있는지 판단하는 규칙은 `Coupon.checkUsable()` 한 곳에만 두고, 입력 확인과 결제가 같은 규칙을 씀 |
| 13주차 | 영수증 등급이 지금 등급을 보여줘서 승급 뒤 예전 영수증이 바뀜 | 결제할 때 등급을 `Order`에 저장(`gradeAtOrder`) | 영수증은 그 시점 기록이어야 함. **은닉성**: 주문 당시 정보는 `Order`가 결제 순간에 직접 기록하고, 밖에서는 바꿀 수 없음 |
| 13주차 | 주문을 취소해도 쓴 쿠폰이 "사용됨"으로 남음 | `DiscountPolicy`에 `onCancelled()`(기본은 아무 일 안 함)를 추가하고 `Coupon`만 오버라이딩해서 사용 여부를 되돌림. `Order`는 적용한 할인 목록을 기억했다가 취소 때 호출 | `onApplied()`와 짝을 맞춤. `Order`는 쿠폰인지 묻지 않고 모든 할인에 같은 메소드를 호출 (**다형성**). 새 할인이 생겨도 `Order`는 그대로 (**개방-폐쇄**) |

> 11~12주차 행은 설계 초안(11/5)과 팀 기준 코드(11/19)를 비교해서 바뀐 점을 적었다.

12주차 SOLID 점검 질문 (개선 후보 찾기):

- **단일 책임**: `Order`가 너무 많은 일을 하지 않는가? (할인 계산을 별도 객체로 뺄 수 있는가)
- **개방-폐쇄**: 새 등급·옵션·할인을 추가할 때 기존 클래스를 수정하는가?
- **리스코프 치환**: 자식 클래스가 부모의 약속(예: `price()`는 0 이상)을 깨지 않는가?
- **인터페이스 분리**: 인터페이스에 쓰지 않는 메소드를 구현하는 클래스가 있는가? (예: `GuestMember`의 빈 메소드 두 개)
- **의존성 역전**: 서비스가 구체 저장소가 아니라 인터페이스에만 의존하는가?

## 10. 웹 확장 계획 (선택, 13주차 판단)

- 13주차 중간 점검에서 **콘솔의 핵심 기능이 모두 동작하고 테스트가 통과하면** 웹을 시작하고, 아니면 콘솔로 마무리한다.
- 구조: `ui/web` 패키지만 추가한다. `domain`, `service`, `repository`는 수정하지 않는다.
- 기술: Javalin + HTML 한 장, API는 메뉴 조회·주문 생성·회원 조회 3개로 제한한다.
- 응답은 DTO로 변환해서 내보내고, 도메인 필드를 공개하지 않는다.
- 14주차 초까지 메뉴 조회 화면이 동작하지 않으면 웹을 접는다.
