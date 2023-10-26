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
        setter+list 추가
            public void setMember(Member member){
                this.member = member; 
                member.getOrders().add(this);
            }
        list 추가 후 setter 
            public void addOrderItem(OrderItem orderItem){
                orderItems.add(orderItem);
                orderItem.setOrder(this);
            }
        자체 참조(계층형 데이터)
            public void addChildCategory(Category child){
                this.children.add(child);
                child.setParent(this);
            }

# 학습을 위해 복잡한 기능 제외(필수)
    - 로그인과 권한 관리 제외
    - 파라미터 검증과 예외처리 단순화
    - 상품은 도서만 진행
    - 카테고리 사용 x
    - 배송 정보 사용x
    - 서비스, 레포지토리 계층 개발 > 테스트케이스 작성 > 웹 계층 적용

# 학습을 위한 아키텍처
    controller > service > repository > db
                > domain <

# 구조
    jpabook.jpashop
        domain : 엔티티가 모여있는 계층으로 모든 계층에서 사용
        exception : 예외처리
        api : 페이징 외 데이터처리
        controller, service, repository : 일반적 구조

# SQL과 JPQL
     SQL  : DB의 table에 접근
    JPQL : SQL과 거의 유사하나 Entity 객체에 대해 접근하는 것이 다름
        return em.createQuery("select m from Member m",Member.class)
                .getResultList();
        return em.createQuery("select m from Member m where m.name = :searchKey",Member.class)
                .setParameter("searchKey",name)
                .getResultList();

# @Transactional
    - JPA에서 DB처리하는 serivce/command는 Transacational처리가 필수
    - 두가지 어노테이션중 javax보다는 spring 사용 권장
    - 데이터를 변경하지 않는 경우를 기본으로 readOnly 처리, 쓰는 경우 default인 readOnly false 처리
    - 각 서비스의 기능에 따라 기능 제한 거는 것도 최적화에 도움 됨(조회 기능은 readOnly) : 리소스 소모를 줄일 수 있음
    - 검증 로직이 서버단에 있더라도 다중 접속과 처리 상황이 있을 수 있으므로 검증이 필요한 경우 unique 처리 

# bean injection 방법(service, repository)
    JPA의 @PersistenceContext도 spring boot에서 @Autowired로 처리해줌(버전이 낮을 경우 안될 수 있음에 주의)
    - Autowired : 변경이 쉽지 않아 유연한 테스트가 어려움
        @Autowired
        private MemberRepository memberRepository;
    - setter : 주입의 변경이 간단함, 운영환경에서 setter로 인해 보안 이슈가 생길 수 있음
        private MemberRepository memberRepository;
        @Autowired
        public void setMemberRepository(MemberRepository memberRepository){
            this.memberRepository = memberRepository;
        }
    - constructor(권장) : 여전히 보안상의 이슈가 있을 수 있으나 최초의 생성 이후의 개입을 막을 수 있음
        Spring 최신 버전에서는 생성자가 하나인 경우에는 @Autowired를 하지 않아도 자동 처리됨
        private final MemberRepository memberRepository;
        @Autowired
        public MemberService (MemberRepository memberRepository){
            this.memberRepository = memberRepository;
        }
    - Lombok의 어노테이션 사용(constructor 자동화)
            private final MemberRepository memberRepository; 선언 후 
        @AllArgsConstructor : 모든 속성에 대한 생성자
        @RequiredArgsConstructor(권장) : final로 선언된 필 수 생성자

# Test 코드 작성
    - @Transactional로 롤백을 기본으로 하게 하고 필요한 경우 다음을 사용
        @Rollback(false) //db에서 결과를 볼경우
        entityManager.flush();//롤백은 하지만 insert문은 표시
    - 에러 발생시 테스트
            @Test
            void 중복_가입_예외() throws Exception{
                //given
                Member member1 = new Member();
                member1.setName("kim");
                Member member2 = new Member();
                member2.setName("kim");
                //when
                memberService.join(member1);
                try{
                    memberService.join(member2); //예외가 나와야 함
                }
                //then
                catch(IllegalStateException ie){
                    return;
                }
        
                fail("중복 검증 예외가 발생하지 않음");
            }
    - 예외 테스트에서 try-catch 대체하기: Junit 버전에 따라 다름
        junit4 : 
            @Test(expected = IllegalStateException.class)
        junit5 : 
            Assertions.assertThrows(IllegalStateException.class, ()->{
                memberService.join(member2);
            });
# 메모리 database 사용하기
    test 폴더에 reseources 작성, yml파일 생성
    datasource: 메모리에서 자바로 띄움(외부 db와 연계 x)
        url: jdbc:h2:mem:test #memory mode
    Spring boot는 기본 설정없으면 기본으로 메모리모드를 사용해 테스트 가능