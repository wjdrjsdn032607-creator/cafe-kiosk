# 클래스 다이어그램 (초기 설계 v1)

## 1. 메뉴·옵션 (담당 A)

```mermaid
classDiagram
    direction TB
    class MenuItem {
        <<abstract>>
        -String id
        -String name
        -int basePrice
        +price() int*
        +description() String*
        +priceLines() List~PriceLine~
        +configure(Size, Temperature) MenuItem
    }
    class Beverage {
        <<abstract>>
        -Size size
        -Temperature temperature
        #sizeSurcharge() int
        #create(Size, Temperature) Beverage*
    }
    class Coffee
    class Tea
    class Smoothie {
        #sizeSurcharge() int
    }
    class Dessert
    class OptionDecorator {
        <<abstract>>
        -MenuItem inner
        -String optionName
        -int surcharge
    }
    class ExtraShot
    class Syrup
    class WhippedCream

    MenuItem <|-- Beverage
    MenuItem <|-- Dessert
    MenuItem <|-- OptionDecorator
    Beverage <|-- Coffee
    Beverage <|-- Tea
    Beverage <|-- Smoothie
    OptionDecorator <|-- ExtraShot
    OptionDecorator <|-- Syrup
    OptionDecorator <|-- WhippedCream
    OptionDecorator o-- MenuItem : inner
```

## 2. 회원·등급·포인트 (담당 B)

```mermaid
classDiagram
    direction TB
    class Customer {
        <<interface>>
        +getGrade() MembershipGrade
        +earn(int) int
        +usePoints(int)
        +restorePoints(int)
        +reverseEarn(int, int)
    }
    class Member {
        -int points
        -long totalSpent
        -MembershipGrade grade
    }
    class GuestMember
    class MembershipGrade {
        <<interface>>
        +name() String
        +discount(int) int
        +earnPoints(int) int
        +evaluate(long) MembershipGrade
    }
    class RateBasedGrade {
        <<abstract>>
        -int discountPercent
        -int pointPercent
    }
    class GuestGrade
    class Bronze
    class Silver
    class Gold {
        +earnPoints(int) int
    }

    Customer <|.. Member
    Customer <|.. GuestMember
    Member --> MembershipGrade
    GuestMember --> MembershipGrade
    MembershipGrade <|.. RateBasedGrade
    RateBasedGrade <|-- GuestGrade
    RateBasedGrade <|-- Bronze
    RateBasedGrade <|-- Silver
    RateBasedGrade <|-- Gold
```

## 3. 할인·결제 (담당 C)

```mermaid
classDiagram
    direction TB
    class DiscountPolicy {
        <<interface>>
        +name() String
        +discount(int) int
        +onApplied()
    }
    class GradeDiscount
    class HappyHourDiscount
    class Coupon {
        <<abstract>>
        -boolean used
        #calculate(int) int*
    }
    class AmountCoupon
    class PercentCoupon
    class Payment {
        <<abstract>>
        +pay(int)
        +refund(int)
        #doPay(int)*
        #doRefund(int)*
    }
    class CardPayment
    class PointPayment

    DiscountPolicy <|.. GradeDiscount
    DiscountPolicy <|.. HappyHourDiscount
    DiscountPolicy <|.. Coupon
    Coupon <|-- AmountCoupon
    Coupon <|-- PercentCoupon
    Payment <|-- CardPayment
    Payment <|-- PointPayment
    PointPayment --> Customer
```

## 4. 주문·서비스 (담당 D)

```mermaid
classDiagram
    direction LR
    class Order {
        -OrderStatus status
        +checkout(Payment, List~DiscountPolicy~) Receipt
        +cancel()
        +subtotal() int
    }
    class Receipt {
        +format() String
    }
    class OrderService {
        +placeOrder(OrderRequest) Receipt
        +cancelOrder(String) Receipt
    }
    class ConsoleApp
    class OrderScreen

    Order --> Customer
    Order o-- MenuItem : items
    Order ..> DiscountPolicy
    Order ..> Payment
    Order ..> Receipt
    OrderService --> Order
    OrderService --> MenuRepository
    OrderService --> MemberRepository
    OrderService --> OrderRepository
    OrderService --> CouponRepository
    ConsoleApp --> OrderScreen
    OrderScreen --> OrderService
```

## 설계 요약

| 구분 | 설명 |
|---|---|
| `domain` | 규칙과 계산을 가진 객체. UI와 저장 방식을 모른다 (`System.out` 사용 금지). |
| `repository` | 저장소 인터페이스와 메모리 구현. DB로 바꿔도 서비스는 그대로. |
| `service` | 객체를 연결만 한다. 계산이나 종류별 `if/else`를 두지 않는다. |
| `ui.console` | 입력을 받고 결과를 출력한다. 웹을 얹을 때는 `ui.web`을 추가한다. |
