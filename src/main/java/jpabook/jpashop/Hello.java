package jpabook.jpashop;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hello {
    private String data;
/*  H2 database 연결 관련
    H2 database 설치
    h2/bin의 실행파일로 실행 > memory
    연결:  jdbc:h2:~/databaseName (최초 연결시 파일모드 연결)
        >  jdbc:h2:tcp://localhost/~/databaseName (네트워크 연결)

        ex) 연결:  jdbc:h2:~/jpashop >  jdbc:h2:tcp://localhost/~/jpashop
*/
}
