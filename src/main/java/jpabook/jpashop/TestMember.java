package jpabook.jpashop;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity //db에 영구화 처리할 대상임을 표시(인스턴스를 컬럼으로)
@Getter @Setter //==Data
public class TestMember {

    @Id //Primary Key
    @GeneratedValue // autoIncreasement
    private long id;

    private String userName;
}
