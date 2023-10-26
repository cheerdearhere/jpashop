package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="orders") // order는 예약어 중 하나이므로 명칭을 변경해 사용한다.
public class Order {
    @Id @GeneratedValue
    @Column(name="order_id")
    private Long id;

//양방향 연관관계(member-orders)인 경우 FK가 있는 곳을 연관관계의 주인으로 잡는다.
    @ManyToOne(fetch = FetchType.LAZY) //다대 일 관계
    @JoinColumn(name = "member_id")//FK
    private Member member;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)//casecade All : order를 저장하면 orderItems도 저장
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) //1:1인경우 조회를 자주하는 객체에서 FK를 지정
    @JoinColumn(name="delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // Date를 쓸때는 처리를 해야함 LocalDateTime을 쓰면 hibernate가 처리

    @Enumerated(EnumType.STRING) //default가 ORDINAL임. 숫자형 외의 다른 값이 들어가면 에러
    private OrderStatus status; //주문상태 ORDER, CANCEL

    //==연관관계 메서드==//
    public void setMember(Member member){
        this.member = member; //일반적인 setter
        member.getOrders().add(this); // mapped 객체에 추가
    }
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);//list인경우
        orderItem.setOrder(this);
    }
    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;//양방향
        delivery.setOrder(this);
    }

    /* constructor method*/
    /**
     *     복잡한 Entity의 경우 별도의 생성 메서드를 만드는 것이 좋다
     * @param member
     * @param delivery
     * @param orderItems
     * @return
     */
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    /* business logic*/
    public void orderCancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송이 완료된 상품은 취소가 불가능 합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);

        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    /* search logic*/
    public  int getTotalPrice(){
        return orderItems.stream().mapToInt(OrderItem :: getTotalPrice).sum();
    }
}
