# jpashop
    JPA를 Spring boot에 적용하는 프로젝트  
        - JPA
        - Spring boot
        - gradle
        - lombok
        - h2 db
        - thymeleaf

# jar에서 테스트 진행
    \reactWorkspace\jpashop> ./gradlew clean build
    BUILD SUCCESSFUL in 43s
    8 actionable tasks: 8 executed
    \reactWorkspace\jpashop\build> java -jar jpashop-0.0.1-SNAPSHOT.jar
    JRE java 버전이 맞지 않으면 에러

#  remain query parameter log
    기본 로그 추가(application.yml 파일)
    logging:
        level:
            org.hibernate:
                sql: debug # 로그 수준 지정
                type: trace # parameter 정보 표시
    외부 라이브러리 적용
    spring-boot-data-source-decorator library 사용
    implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0' //부트에 미리 입력된 라이브러리가 아니라 버전 연결
    운영에서는 성능 저하의 원인이 될수있으므로 꼭 체크

# Getter와 Setter에 대하여
    Getter는 사용하지만 Setter를 마구 열어놓으면 유지보수의 입장에서 좋지않다.
    꼭 필요한 경우 필요한 만큼의 비즈니스 메서드를 구현해 호출하도록 하는것이 좋음
    어디서 어떻게 데이터를 변경시키는지 관리를 명확하게 하기위해서 Setter는 가급적 사용하지 않는다.

# ManyToMany
    다:다의 양방향성 매핑은 성능, 유지 그 어떤 면으로 봤을때도 좋지 않다. 가능하면 1:다, 다:1을 단방향으로 설정해 사용하는 것을 권장

# 값 타입(VO)의 constructor
    emutable하게 만드는 것이 좋으므로 Setter없이 하는 것을 권장. 
    단 refresh, proxy를 사용하는 경우를 위해 기본 constructor를 protected로 사용(물론 public도 가능)
    
ex) Address class
    @Embeddable 
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