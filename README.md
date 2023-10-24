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

# 엔티티 설계시 주의사항

## 가급적 Setter는 안쓴다
    필요에 따라 비즈니스 메서드를 지정 (교육에서는 그냥 쓸예정)
## 모든 연관관계는 지연로딩(LAZY)를 사용
    즉시 로딩(EAGER)은 연관된 데이터를 다 가져와 추적이 어렵다. 
    JPQL관련 N+1문제는 대다수 EAGER일때 발생
    연관된 엔티티를 함게 조회해야한다면 fetch join 또는 엔티티 그래프 기능을 사용.  
    LAZY 로딩이 transaction 관련 에러가 나는 경우: 
        - transaction을 빠르게 가져온다
        - Open session inview
        - fetch join(일반적)
    x:many는 LAZY가 default
    @...ToOne(x:1)관계는 default가 EAGER이므로 직접 LAZY로 지정
ex)     @ManyToOne(fetch = FetchType.LAZY)
## 컬렉션은 필드에서 초기화
    리터럴이나 참조 데이터를 갖는 Collection의 경우 필드에서 바로 초기화하는 것이 안전하다. 
    하이버네이트가 영속성을 지정할때 컬렉션을 한번 감싸기때문에 객체가 달라진다. 
    컬렉션은 특히 하이버네이트가 지정해놓고 쓰므로 중간에 변경하지 않는다.
ex)
    class java.util.ArrayList > class org.hibernate.collection.internal.PersistentBag
## 테이블, 컬럼명 생성 전략
    spring boot default: implicitNamingStrategy(camelCase를 snake_case로 변경)
    DBA들이 주로 사용하는 방식이 스네이크 방식.
    만약 바꿀경우 
        논리명 생성: spring.jpa.hibernate.naming.implicit-strategey(사용자가 비명시한 경우만 적용)
        물리명 적용: spring.jpa.hibernate.naming.physical-strategey(전체 테이블의 컬럼에 적용)
    에서 조작 가능(사내 전략에따라 진행해야할 경우)
## casecade = CasecadeType.ALL처리하기
    영속성 처리할때 각자 하던 것을 하위 Entity를 함께 처리하도록 함.
    cascade 적용 전 
        persist(orderItemA)
        persist(orderItemB)
        persist(orderItemC) 
            ...
        persist(order)
    casecade를 ALL로 한경우 한번에 하위 엔터티도 적용
        persist(order)
ex) Order class
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
## 연관 관계(편입) 메서드
    양방향 연관관계가 있는 엔티티의 속성이 Set될때 관련 데이터도 함께 관리할 연관관계 편입 메서드가 있으면 유용하다. 위치는 주 위치
    자체 참조의 경우에도 연관관계 편입 메서드 적용
ex) 
Order class
    public void setMember(Member member){
        this.member = member; //일반적인 setter
        member.getOrders().add(this); // mapped 객체에 추가
    }
Category class
    public void addChildCategory(Category child){
        this.children.add(child);
        child.setParent(this);
    }