package jpabook.jpashop.domain;

import lombok.Getter;

@Getter
public enum TestResponseCode {
    //enum으로 사용할 데이터
    RESPONSE01("0001","테스트용");
    //데이터에 들어갈 속성 지정
    private String code;
    private String message;
    //생성자로 enum의 형식 처리
    TestResponseCode(String code,String message){
        this.code = code;
        this.message = message;
    }
}
