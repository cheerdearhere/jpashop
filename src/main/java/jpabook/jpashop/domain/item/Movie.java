package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@DiscriminatorValue("M") // 상속관계를 한 테이블로 지정한 경우 구분을 위한 타입(dtype)에 들어갈 값을 지정할 수 있다. default는 해당 class 명
public class Movie extends Item{
    protected Movie(){}
    private String director;
    private String actor;
}
