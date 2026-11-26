# 클래스별 담당 및 설명

> 담당은 팀 역할 분담(A~D)을 따른다. 담당을 바꾸면 이 표의 이름만 수정한다.
> 발표 질문 대비: "OOP 원리" 열은 *왜 이렇게 설계했나요?*, "핵심 메소드" 열은 *실행하면 어떤 메소드가 호출되나요?*에 답할 근거다.

## 담당 요약

| 담당 | 이름 | 영역 | 클래스 수 | 패키지 |
|---|---|---|---|---|
| A | 조민규 | 메뉴·옵션 | 14 | `domain.menu` |
| B | 신준서 | 회원·등급·포인트 | 9 | `domain.member` |
| C | 심은지 | 할인·결제 | 9 | `domain.discount`, `domain.payment` |
| D | 정건우 | 주문·서비스·저장소·UI | 25 | `domain.order`, `service`, `repository`, `ui.console`, `common`, 루트 |

> 분량 메모: D의 클래스 수가 많다. 저장소·화면 클래스를 각 파트로 나누는 방안(예: A가 `MenuService`·`MenuRepository`·`MenuScreen`, B가 `MemberService`·`MemberRepository`·`MemberScreen`, C가 `CouponRepository`)을 팀에서 정하면 이 문서의 담당만 바꾸면 된다.

---

## A. 조민규: 메뉴·옵션 (`domain.menu`, 14개)

메뉴 계층은 상속과 추상 클래스, 옵션은 Decorator 패턴을 보여주는 파트다. 주문 쪽은 이 클래스들을 전부 `MenuItem` 타입으로만 다룬다.

| 클래스 | 역할 | OOP 원리 | 핵심 메소드·특징 |
|---|---|---|---|
| `MenuItem` | 모든 메뉴의 추상 부모. id·이름·기본 가격을 보관하고 생성자에서 검증 | 추상성, 은닉성 | `price()`, `description()` (추상), `priceLines()`, `configure()` |
| `Beverage` | 음료 공통 부모. 사이즈·온도를 가지고 가격 공식을 제공 | 상속성 | `sizeSurcharge()` (자식이 오버라이딩 가능), `create()` |
| `Coffee` | 커피. 아이스·핫 모두 가능 | 상속성 | `create()` |
| `Tea` | 차. 아이스·핫 모두 가능 | 상속성 | `create()` |
| `Smoothie` | 스무디. 아이스만 가능, L 사이즈는 500원 추가 | 상속성, 오버라이딩 | `sizeSurcharge()` 오버라이딩 |
| `Dessert` | 디저트. 사이즈·온도·옵션 없음 | 상속성 | `supportsOptions()`가 `false` |
| `OptionDecorator` | 옵션의 추상 부모. 메뉴를 감싸서 가격·설명을 더함 | 추상성, 다형성 | `price()`, `description()`, `priceLines()` |
| `ExtraShot` | 샷 추가 (+500원) | 상속성 | 생성자에서 이름·요금만 지정 |
| `Syrup` | 시럽 (+300원) | 상속성 | 생성자에서 이름·요금만 지정 |
| `WhippedCream` | 휘핑크림 (+500원) | 상속성 | 생성자에서 이름·요금만 지정 |
| `Option` | 사용자가 입력한 옵션 이름과 Decorator 클래스를 연결하는 enum | 추상성 | `fromLabel()`, `applyTo()` |
| `Size` | 사이즈(S/M/L)와 추가 요금 | 은닉성 | `from()`, `getSurcharge()` |
| `Temperature` | 아이스·핫 | - | 값 목록 |
| `PriceLine` | 영수증 한 줄(이름, 금액)을 담는 record | - | 값 객체 |

**실행 흐름 예:** 아이스 아메리카노 L에 샷 추가와 시럽을 고르면 `Coffee`를 `ExtraShot`이 감싸고, 그걸 다시 `Syrup`이 감싼다. `price()`를 호출하면 안쪽부터 4,000 + 500 + 300 = 4,800원이 계산된다.

**새 유형 추가:** 새 옵션은 `OptionDecorator`를 상속한 클래스를 만들고 `Option`에 한 줄 추가한다. 새 메뉴는 `MenuItem`을 상속한 클래스를 만든다.

**테스트:** `MenuItemTest` (10개)

---

## B. 신준서: 회원·등급·포인트 (`domain.member`, 9개)

은닉성(포인트·등급 보호)과 다형성(등급별 할인·적립)을 보여주는 파트다. 등급을 `enum`이 아니라 객체로 두어서, 등급마다 다른 규칙을 그 등급 클래스가 직접 가진다.

| 클래스 | 역할 | OOP 원리 | 핵심 메소드·특징 |
|---|---|---|---|
| `Customer` | 주문하는 사람의 규약(인터페이스). 회원과 비회원이 구현 | 추상성, 다형성 | `earn()`, `usePoints()`, `restorePoints()`, `reverseEarn()` |
| `Member` | 회원. 포인트·누적 금액·등급을 `private`으로 보호하고 승급을 자동 처리 | 은닉성 | `earn()`이 적립·누적·승급을 한 번에 처리, `usePoints()`는 부족 시 `CafeException`, `reverseEarn()`은 취소 시 누적 금액으로 등급을 다시 계산 |
| `GuestMember` | 비회원. `null` 대신 쓰는 객체(Null Object). 할인·적립 없음, 포인트 사용은 예외 | 다형성 | `usePoints()` 예외, `earn()`은 0 반환 |
| `MembershipGrade` | 등급 규약(인터페이스). 할인·적립·승급 판단을 등급이 직접 계산 | 추상성, 다형성 | `discount()`, `earnPoints()`, `evaluate()`, `gradeFor()` |
| `RateBasedGrade` | 할인율·적립률(%)로 계산하는 등급의 공통 부모 | 상속성 | `discount()`, `earnPoints()` 공통 계산 |
| `Bronze` | 할인 0%, 적립 1%. 누적 10만 원이면 Silver, 30만 원이면 Gold | 상속성 | `evaluate()` |
| `Silver` | 할인 3%, 적립 3%. 누적 30만 원이면 Gold | 상속성 | `evaluate()` |
| `Gold` | 할인 5%, 적립 5%, 1만 원 이상 결제 시 보너스 100P. 최고 등급 | 상속성, 오버라이딩 | `earnPoints()` 오버라이딩 |
| `GuestGrade` | 비회원 등급. 할인·적립 0% | 상속성 | `evaluate()`가 자기 자신 반환 |

**실행 흐름 예:** 결제가 끝나면 `Order`가 `customer.earn(결제금액)`을 호출한다. `Member`가 `grade.earnPoints()`로 적립량을 구하고, 누적 금액을 더한 뒤 `grade.evaluate()`로 승급 여부를 판단해서 새 등급을 받는다.

**새 유형 추가:** 새 등급(예: VIP)은 `RateBasedGrade`를 상속한 클래스를 만들고 `Gold.evaluate()`의 반환값을 한 줄 바꾼다.

**테스트:** `MemberTest` (10개)

---

## C. 심은지: 할인·결제 (`domain.discount`, `domain.payment`, 9개)

할인은 인터페이스 하나를 여러 클래스가 다르게 구현하는 다형성, 결제는 공통 흐름을 부모가 잡고 실제 처리만 자식이 구현하는 구조다.

| 클래스 | 역할 | OOP 원리 | 핵심 메소드·특징 |
|---|---|---|---|
| `DiscountPolicy` | 할인 규약(인터페이스). 등급 할인·쿠폰이 각자 구현 | 추상성, 다형성 | `name()`, `discount()`, `onApplied()`, `onCancelled()` (default) |
| `GradeDiscount` | 회원 등급 할인. 계산은 등급 객체에게 위임 | 다형성 | `discount()`가 `grade.discount()` 호출 |
| `Coupon` | 쿠폰 공통 부모. 유효기간·사용 여부 검사, 사용 여부는 `private` | 상속성, 추상성, 은닉성 | `checkUsable()` (사용 가능 여부 확인, 결제 전 입력 확인에도 씀), `discount()` (final), `calculate()` (추상), `onApplied()`, `onCancelled()` |
| `AmountCoupon` | 정액 쿠폰 (예: 500원 할인) | 상속성 | `calculate()` |
| `PercentCoupon` | 정률 쿠폰 (예: 10% 할인) | 상속성 | `calculate()` |
| `Payment` | 결제 수단 추상 클래스(Template Method). 금액 검증 흐름은 부모가 잡음 | 추상성, 상속성 | `pay()`·`refund()` (final), `doPay()`·`doRefund()` (추상), `earnableAmount()` (적립 기준 금액, 기본은 결제 금액 전부) |
| `CardPayment` | 카드 결제(모의). 한도를 넘으면 거절 | 상속성 | `doPay()` |
| `PointPayment` | 포인트 결제. 차감·환원은 `Customer`가 검증. 포인트로 낸 금액은 적립하지 않음 | 상속성, 다형성 | `doPay()`, `doRefund()`, `earnableAmount()`가 0 반환 |
| `PaymentType` | 사용자가 고른 결제 수단과 `Payment` 클래스를 연결하는 enum | 추상성 | `create()` |

**실행 흐름 예:** `Order.checkout()`이 할인 정책들을 등급, 쿠폰 순서로 돌며 `discount()`를 호출한다. 이어서 `payment.pay()`가 성공하면 `onApplied()`로 쿠폰이 사용 처리된다. 결제가 실패하면 쿠폰은 그대로 남는다. 주문을 취소하면 `Order.cancel()`이 `onCancelled()`를 호출해서 쿠폰을 다시 쓸 수 있게 돌려놓는다.

**새 유형 추가:** 새 할인(예: 생일 할인)은 `DiscountPolicy`를 구현해 `Main`의 `automaticPolicies`에 넣으면 되고, 새 결제 수단은 `Payment`를 상속해 `PaymentType`에 한 줄 추가한다.

**테스트:** `DiscountPolicyTest` (5개). 결제 관련 시나리오는 `OrderTest`에 포함되어 있다.

---

## D. 정건우: 주문·서비스·저장소·UI (26개)

다른 세 파트를 연결하는 파트다. 주문의 규칙(계산·상태 전이)은 `Order`가 직접 가지고, 서비스는 객체를 연결만 한다.

| 클래스 | 역할 | OOP 원리 | 핵심 메소드·특징 |
|---|---|---|---|
| `Order` | 주문. 금액 합산, 할인 적용, 결제, 적립, 취소를 직접 처리. `status`는 `private` | 은닉성, 다형성 | `subtotal()`, `checkout()`, `cancel()`(환불·적립·쿠폰 되돌리기), `addItem()`, `getLines()`(수량 묶기), `getGradeAtOrder()`(주문 당시 등급) |
| `OrderStatus` | 주문 상태(생성, 결제 완료, 취소) | 은닉성 | 값 목록 |
| `DiscountLine` | 적용된 할인 한 줄(이름, 금액) record | - | 값 객체 |
| `OrderLine` | 영수증에 보여줄 한 줄(메뉴, 수량) record | - | `lineTotal()` |
| `Receipt` | 영수증. 출력은 하지 않고 문자열만 만들어 줌 | 책임 분리 | `format()` |
| `OrderService` | 주문 흐름 연결(회원·메뉴·쿠폰 확인 → 주문 생성 → 결제). 계산은 하지 않음 | 책임 분리 | `placeOrder()`, `cancelOrder()`, `findOrders()`, `checkMember()`·`checkCoupon()`(결제 전 입력 확인) |
| `MemberService` | 회원 가입·조회 | 책임 분리 | `register()`, `find()` |
| `MenuService` | 메뉴 목록 조회 | 책임 분리 | `findAll()` |
| `OrderRequest`, `OrderItemRequest` | UI가 입력받은 값을 서비스로 전달하는 record. 생성 시 검증 | 은닉성 | 생성자(compact) 검증 |
| `MenuRepository`, `MemberRepository`, `OrderRepository`, `CouponRepository` | 저장소 규약(인터페이스) 4개 | 추상성 | `save()`, `findById()` 등 |
| `InMemoryMenuRepository` 외 3개 | 메모리(`Map`)에 저장하는 구현. DB로 바꿔도 서비스는 그대로 | 다형성 | `nextId()` (회원 M001, 주문 O-0001). 주문번호는 `save()`할 때 확정되어 결제 실패 시 번호가 넘어가지 않음 |
| `ConsoleApp` | 메인 메뉴 반복, 번호에 따라 화면으로 연결 | 책임 분리 | `run()` |
| `ConsoleInput` | `Scanner`를 감싸 입력 형식(숫자, 범위)만 확인 | 은닉성 | `readLine()`, `readInt()` |
| `MenuScreen`, `OrderScreen`, `MemberScreen` | 기능별 화면. 입력받고 서비스를 호출해 결과 출력 | 책임 분리 | `show()`, `start()`, `history()`. `OrderScreen`은 회원번호·쿠폰 오타와 결제 실패 시 다시 입력받음 |
| `Main` | 객체를 조립(의존성 주입)하고 실행 | 추상성 | `main()` |
| `SampleData` | 시연용 샘플 메뉴·회원·쿠폰 로딩 | - | `load()` |
| `CafeException` | 업무 규칙 위반 예외(포인트 부족, 만료 쿠폰 등) | - | 예외 메시지 |

**주문 한 건의 실행 흐름:**

1. `OrderScreen.start()`가 메뉴·옵션·회원·쿠폰·결제 수단을 입력받아 `OrderRequest`를 만든다. 회원번호·쿠폰은 입력하자마자 `checkMember()`, `checkCoupon()`으로 확인한다.
2. `OrderService.placeOrder()`가 회원, 메뉴(옵션 적용), 쿠폰을 확인하고 `Order`를 만든다.
3. `Order.checkout()`이 할인을 순서대로 적용하고 `Payment.pay()`로 결제한다.
4. 결제가 성공하면 쿠폰이 사용 처리되고 `Customer.earn()`으로 포인트가 적립된다. 적립 기준 금액은 `Payment.earnableAmount()`가 정한다 (포인트 결제는 0원).
5. 주문이 저장소에 저장되면서 주문번호가 확정되고, `Receipt.format()`이 만든 문자열을 `OrderScreen`이 출력한다. 같은 메뉴는 `OrderLine`으로 묶여 `x3`처럼 한 줄로 나온다.
6. 취소하면 `Order.cancel()`이 환불, 적립 포인트·누적 금액·등급 되돌리기, 쿠폰 복구(`onCancelled()`)를 한다.

**새 유형 추가:** 핵심 원칙은 `Order`와 `OrderService`를 고치지 않는 것이다. 새 메뉴·등급·할인·결제가 늘어도 이 클래스들은 바뀔 필요가 없어야 한다.

**테스트:** `OrderTest` (13개), `OrderServiceTest` (15개)
