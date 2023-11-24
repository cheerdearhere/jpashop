## 가급적 Setter는 안쓴다
필요에 따라 비즈니스 메서드를 지정 (교육에서는 그냥 쓸예정)
## 모든 연관관계는 지연로딩(LAZY)를 사용
즉시 로딩(EAGER)은 연관된 데이터를 다 가져와 추적이 어렵다. 

```asciidoc
JPQL관련 N+1문제는 대다수 EAGER일때 발생
연관된 엔티티를 함게 조회해야한다면 fetch join 또는 엔티티 그래프 기능을 사용.  
LAZY 로딩이 transaction 관련 에러가 나는 경우: 
    - transaction을 빠르게 가져온다
    - Open session inview
    - fetch join(일반적)
x:many는 LAZY가 default
@...ToOne(x:1)관계는 default가 EAGER이므로 직접 LAZY로 지정
```
    ex)     
```java
    @ManyToOne(fetch = FetchType.LAZY)
```
## 컬렉션은 필드에서 초기화
```asciidoc
리터럴이나 참조 데이터를 갖는 Collection의 경우 필드에서 바로 초기화하는 것이 안전하다. 
하이버네이트가 영속성을 지정할때 컬렉션을 한번 감싸기때문에 객체가 달라진다. 
컬렉션은 특히 하이버네이트가 지정해놓고 쓰므로 중간에 변경하지 않는다.
```
    ex)
```java
    class java.util.ArrayList >>> class org.hibernate.collection.internal.PersistentBag
```
## 테이블, 컬럼명 생성 전략
```asciidoc
spring boot default: implicitNamingStrategy(camelCase를 snake_case로 변경)
DBA들이 주로 사용하는 방식이 스네이크 방식.
만약 바꿀경우 
    논리명 생성: spring.jpa.hibernate.naming.implicit-strategey(사용자가 비명시한 경우만 적용)
    물리명 적용: spring.jpa.hibernate.naming.physical-strategey(전체 테이블의 컬럼에 적용)
에서 조작 가능(사내 전략에따라 진행해야할 경우)
```
## casecade = CasecadeType.ALL처리하기
영속성 처리할때 각자 하던 것을 하위 Entity를 함께 처리하도록 함.

cascade 적용 전
```asciidoc
persist(orderItemA)
persist(orderItemB)
persist(orderItemC) 
    ...
persist(order)
```
casecade를 ALL로 한경우 한번에 하위 엔터티도 적용
ex)
```java
//    persist(order)
//    Order class
        @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
        private List<OrderItem> orderItems = new ArrayList<>();
        //주문 저장
        orderRepository.save(order);// cascade를 했기 대문에 따로 persist를 할 필요가 없다.
        //만약 order가 다른 곳에서 다른 용도로 사용된다면 cascade를 하지말고 각각 따로 작성
```
## 연관 관계(편입) 메서드
양방향 연관관계가 있는 엔티티의 속성이 Set될때 관련 데이터도 함께 관리할 연관관계 편입 메서드가 있으면 유용하다. 

위치는 주 위치

자체 참조의 경우에도 연관관계 편입 메서드 적용

ex) setter+list 추가
```java
            public void setMember(Member member){
                this.member = member; 
                member.getOrders().add(this);
            }
```
list 추가 후 setter 
```java
public void addOrderItem(OrderItem orderItem){
                orderItems.add(orderItem);
                orderItem.setOrder(this);
            }
```
자체 참조(계층형 데이터)
```java
public void addChildCategory(Category child){
                this.children.add(child);
                child.setParent(this);
            }
```