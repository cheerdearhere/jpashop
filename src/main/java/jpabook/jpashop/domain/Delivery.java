package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Delivery {
    @Id @GeneratedValue
    @Column(name="delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery") //1:1인경우 조회를 자주하는 객체에서 FK를 지정
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)//default하면 에러가 나므로 반드시 STRING으로 지정
    private DeliveryStatus status; //READY, COMP
}
