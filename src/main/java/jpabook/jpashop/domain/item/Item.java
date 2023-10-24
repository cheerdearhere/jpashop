package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 상속 관계 매핑을 하는 경우 테이블을 하나에 쓸지, 각각 쓸지, Join(정규화된 상태) 등의 전략을 표기해야한다.
@DiscriminatorColumn(name="dtype") //type 구분용 컬럼(상속관계 매핑시 사용)
public abstract class Item {
    protected Item(){}

    @Id @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")//FK가 위치한 것을 주로 둠
    private List<Category> categories = new ArrayList<>();
    /**
     * 주의) 다:다는 실무에서는 거의 사용하지 않음(특히 운영 환경에서)
     * 사이에 테이블을 지정해서 1:다, 다:1을 단방향으로 생성하는 경우가 대다수
     * 양방향은 거의사용안함
     */
}
