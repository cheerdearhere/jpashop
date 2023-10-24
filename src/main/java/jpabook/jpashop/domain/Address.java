package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable // 다른 것에 내장될 수 있음
@Getter //Setter없이 사용
public class Address {
    private String city;
    private String street;
    private String zipcode;

    //JPA에서 refresh나 proxy로 사용할 수 있으므로 기본생성자는 있으면 좋음. 단, protected로 보안성을 높이는 것도 좋음
    protected Address(){}
    //특정 생성자가 필요한 경우 기본생성자를 protected로 선언한 뒤 지정하는 것을 권장
    //사용되는 용도와 데이터를 체크할 수 있어 유용
    public Address(String city,String street,String zipcode){
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
