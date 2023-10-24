package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {
    @Id @GeneratedValue
    @Column(name="member_id") //column 관련 설정
    private Long id;
    private String name;

    @Embedded // 내장 타입으로 사용했음(Embeddable와 Embedded 둘 중 하나만 있으면 되지만 가독성을 위해 둘다 표시하는 것 권장)
    private Address address;

    @OneToMany(mappedBy = "member") // member와 orders의 관계
    private List<Order> orders = new ArrayList<>();
    // mappedBy는 연관관계의 주인이 아니며 주가되는 객체의 어떤 인자를 기준으로 매핑되는지를 표시한다
}
