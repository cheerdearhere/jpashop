package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter
public class Category {
    @Id @GeneratedValue
    @Column(name="category_id")
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name="category_item",
            joinColumns = @JoinColumn(name="category_id"),
            inverseJoinColumns = @JoinColumn(name="item_id"))
    private List<Item> items = new ArrayList<>();
    /**
     * 주의) 다:다는 실무에서는 거의 사용하지 않음(특히 운영 환경에서)
     * 사이에 테이블을 지정해서 1:다, 다:1을 단방향으로 생성하는 경우가 대다수
     * 양방향은 거의사용안함
     */

//  자신 참조(계층형 데이터인 경우)
    @ManyToOne
    @JoinColumn(name="parent_id") //id 값 기준
    private Category parent;
    @OneToMany(mappedBy = "parent")// parent(상위 객체)
    private List<Category> children = new ArrayList<>();
}
