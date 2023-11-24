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

# [엔티티 설계시 주의사항](./notes/EntityCheckList.md)



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

# JPA에서 Join 하기
    마찬가지로 Entity로 조인(left, right 등은 SQL과 동일)
    ex) order + 그 주문과 관련된 member 조인
    public List<Order> findAll(OrderSearch orderSearch){
        return em.createQuery("select o from Order o join o.member m", Order.class)
                .getResultList();
    }

# JPA에서 페이징 처리
    - param을 담을 Object를 생성(Entity)
    - createQuery할때 인수로 입력
    필수값이 반드시 넘어오는 경우는 편리함
        ex) 값이 반드시 있는 경우
       public List<Order> findAll(OrderSearch orderSearch){
        // order + 그 주문과 관련된 member 조인
        return em.createQuery("select o from Order o join o.member m"+
                        " where o.status = :status "+ //동적으로 쿼리를 추가하는 경우
                        " and m.name like :name", Order.class)
                .setParameter("status",orderSearch.getOrderStatus())//동적쿼리에 파라미터 바인딩
                .setParameter("name",orderSearch.getMemberName())
                .setFirstResult(100)//paging할 때 처음 표시할 rowNum
                .setMaxResults(1000)//최대 호출 데이터 수
                .getResultList();
        }

# JPA에서 동적쿼리는 어떻게 수행할 것인가
    권장: QueryDsl 사용
### 1. 문자열로 직접 조건 추가  - 복잡, 어려움, 에러가 발생할 가능성이 곳곳에 있음
    public List<Order> findAllByStr(OrderSearch orderSearch){
        StringBuilder jpqlBuilder = new StringBuilder("select o from Order o join o.member m");
        boolean isFirstCondition = true;

        //주문 상태
        if(orderSearch.getOrderStatus() != null){
            if(isFirstCondition){
                jpqlBuilder.append(" where");
                isFirstCondition = false;
            }else{
                jpqlBuilder.append(" and");
            }
            jpqlBuilder.append(" o.status = :status");
        }

        //회원 이름
        if(StringUtils.hasText(orderSearch.getMemberName())){
            if(isFirstCondition){
                jpqlBuilder.append(" where");
                isFirstCondition = false;
            }else{
                jpqlBuilder.append(" and");
            }
            jpqlBuilder.append(" m.name like :name");
        }

        //parameter binding
        TypedQuery<Order> query = em.createQuery(jpqlBuilder.toString(), Order.class)
                .setMaxResults(1000);
        if(orderSearch.getOrderStatus() != null){
            query = query.setParameter("status",orderSearch.getOrderStatus());
        }
        if(StringUtils.hasText(orderSearch.getMemberName())){
            query = query.setParameter("name",orderSearch.getMemberName());
        }

        return query.getResultList();
    }
### 2. JPA Criteria(JPA가 제공하는 동적쿼리용 표준라이브러리) - 쿼리를 연상하기 어려워 유지보수가 어려움 => 운영x
    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object,Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();
        //주문상태
        if(orderSearch.getOrderStatus() != null){
            Predicate status = cb.equal(o.get("status"),orderSearch.getOrderStatus());//조건절 =
            criteria.add(status);
        }
        //회원이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())){
            Predicate name = cb.like(m.<String>get("name"),"%"+orderSearch.getMemberName()+"%");
            criteria.add(name);
        }
        //쿼리에 붙임
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        //마지막 조건
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        //데이터 조형
        return query.getResultList();
    }
### 3.(권장)QueryDsl 처리 : 왠만한 복잡한 내용은 QueryDsl 권장
    public List<Order> findAll(OrderSearch orderSearch){//queryDsl 사용
        QOrder order = QOrder.order;
        QMemger member = QMember.member;

        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()),
                        nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }
    private BooleanExpression statusEq(OrderStatus statusCond){
        if(statusCond == null){
            return null;
        }
        return order.status.eq(statusCond);
    }
    private BooleanExpression nameLike (String nameCond){
        if(!StringUtils.hasText(nameCond)){
            return null;
        }
        return order.name.like(nameCond);
    }

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

# 복잡한 Entity의 경우 생성 메서드를 만드는 것이좋다. ex) order
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

# Stream interface 사용
    public  int getTotalPrice(){
        //        int totalPrice=0;
        //        for(OrderItem orderItem: orderItems){
        //            totalPrice += orderItem.getTotalPrice();
        //        }
        //        return totalPrice;
        return orderItems.stream().mapToInt(OrderItem :: getTotalPrice).sum();
    }

# 함부로 생성자를 만들어 set방식을 불규칙하게 하지 못하게 하려면 default constructor를 protected로 선언 
    생성 메서드를 따로 만든 뒤 

    protected Order(){}
    @NoArgsConstructor(access = AccessLevel.PROTECTED)로도 사용가능

# JPA의 강점: 로직이 바뀌면 다른 데이터들의 유지보수가 편리해진다
    /**
     * 이전에 작업해 놓은 경우 편리
     * @param orderId
     */
    @Transactional
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findOne(orderId);
        order.orderCancel();
    }
    따로 변경 내역을 다시 적용, 지정할 필요가 없음(dirty check)

# Entity를 다루는 두가지 패턴: 
    양립 가능.
    둘중 해당 서비스에 알맞는 것 선택
### domain model pattern: 
    핵심 비즈니스 로직을 가능한 해당 엔티티에 몰아 넣는 것. 자동화가 장점
    ex) Order
### transactional script pattern:
    기존 방식. Entity에는 비즈니스 로직이 거의 없고 서비스 계층에서 대부분의 비즈니스 로직을 처리하는 것. 개별 처리가 강점

# 일반적인 단위테스트때는 @SpringBootTest 없이 사용이 좋음
    단위테스트에서 스프링 서버와 db까지 태우는 것은 낭비가 클수 있음
    여러 단계를 거치는 경우 InvalidDataAccessApiUsageException.class가 custom exception을 대체할 수 있어 RuntimeException으로 걸음
    도메인 모델 패턴의 장점은 핵심 메서드가 모여있는 entity를 테스트 할 수 있다는 장점

# System.out.println 말고 logger 쓰기
    Logger log = LoggerFactory.getLogger(getClass());로 지정하거나
    @Slf4j로 표시해서
    
    log.info/debug/error/trace... 등등 사용

# thymeleaf 
    th:replace  : jsp의 include
        <div th:replace="fragments/footer"></div>
    ${}         : el문
    *{}         : feild의 name(dto와 form의 바인딩처리)
    th:object   : dto 바인딩
    th:class    : class 동적 처리 
        th:class="${#fields.hasErrors('name')}
            ? 'form-control fieldError'
            :'form-control'"
    th:if       : 조건문 처리
    th:errors   : field의 error감지
        th:if="${#fields.hasErrors('name')}" th:errors="*{name}"
    th:each     : enhancedFor문과 같은 방식
         th:each="member:${members}
    th:href     : 
        url 이동
            href="#" th:href="@{/items/{id}/edit (id=${item.id})}" ...
        javascript 실행
            href="#" th:href="'javascript:cancel('+${item.id}+')'"
# thymeleaf와 스프링 에러 처리 : validation
    - Spring boot 2.3 이후부터는 직접 의존성 주입 필요
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    - @Valid 파라미터, @NotEmpty(message="") VO의 property 지정
    에러화면을 다시 작성하지 않고 타임리프가 적용하는 에러화면을 표시

# Entity와 DTO 구별..
    view를 처리할 Object와 핵심 비지니스 로직은 구별할 필요가 있다.
    최대한 서로 오염되지 않도록 따로 작성하는 것을 권장한다. 
    화면에서 받는 경우, 비즈니스 로직을 처리하는 경우, 화면에 보내는 경우를 구별하는 것이 좋다. 
    특히 API를 개발할때는 절대 Entity를 반환해서는 안된다. 

# 중요!! JPA에서 수정처리하기(변경 감지와 병합)
    - id값이 조작될 수 있으므로 이에대한 보안 준비도 필요함
    - 습관적으로 merge를 하는데 JPA 표준은 변경감지하도록하는 것 권장
    영속 entity: setter만 처리해도 변경을 감지해 저장
    ex) order cancel() method
        //기존 객체
        Book book = em.find(Book.class,1L);
        //in transaction
        book.setName("newName");
        //dirty checking : 변경 감지
        // tx commit
        별도로 update 로직을 짜지 않고 변경을 감지해서 저장하도록 함
    준영속 entity : 더이상 EntityManager가 관리하지 않는 데이터 
        = db를 다녀와서 이미 식별자(id: primary key)를 가지고 있는 데이터
        ex) 상품 정보 변경때 사용하는 book 
### 준영속 entity에 영속성을 입히는 방법
    1. merge 사용하기: 준영속 상태의 entity를 영속 상태로 변경
        a. 준영속상태의 entity에서 식별자(id)를 받아 1차 캐시에서 entity를 조회
            a-1. 1차 캐시에 해당 id의 entity가 없으면 db에서 조회해 1차 캐시에 저장
        b. 조회된 entity에 준영속 상태의 entity의 모든 properties 값을 set.
        c. update된 1차 캐시의 entity를 반환함
    ex) Item mergedItem = em.merge(item);
    mergedItem은 영속성 엔티티, item은 여전히 준영속 엔티티
    프로세스 로직은 아래 코드라고 생각하면됨. 사실상 변경감지와 같음. 필요없는 데이터도 다 업데이트해야함.
    @Transactional
    public Item updateItem(Long itemId, Book bookParam){ 
        Item foundItem = itemRepository.findOne(itemId); 
        if(foundItem.getType==="B") Book foundBook = (Book)foundItem;
        foundItem.setId(bookParam.getId()); 
        foundItem.setName(bookParam.getName());
        foundItem.setPrice(bookParam.getPrice());
        foundItem.setStockQuantity(bookParam.getStockQuantity());
        foundBook.setAuthor(bookParam.getAuthor());
        foundBook.setIsbn(bookParam.getIsbn());
        return foundItem;
    }

    2. 변경감지 처리하기: 영속성을 지닌 entity를 호출해 데이터를 입력
    @Transactional
    public void updateItem(Long itemId, Book bookParam){ //parameter로 넘어온 준영속 상태의 entity
        Item foundItem = itemRepository.findOne(itemId); //같은 id를 지닌 영속성 entity 조회
        //        if(foundItem.getType==="B") //실제에서는 타입 분류...
        Book foundBook = (Book)foundItem;
        foundItem.setId(bookParam.getId()); //데이터 수정 > 변경 감지 > 데이터 저장됨
        foundItem.setName(bookParam.getName());
        foundItem.setPrice(bookParam.getPrice());
        foundItem.setStockQuantity(bookParam.getStockQuantity());
        foundBook.setAuthor(bookParam.getAuthor());
        foundBook.setIsbn(bookParam.getIsbn());
    }
### 왜 병합(merge)이 아닌 변경감지인가?
    물론 merge가 더 편하다. 하지만 병합되는 필드중 값이 없는 필드는 null로 처리된다. 
    원하는 속성만 선택해서 변경할 수 있는 변경감지 방식과 달리 
    모든 필드를 업데이트 시켜야만하는 병합(merge)은 데이터의 소실을 야기할 수 있으므로 주의해야한다.
    개발자의 실수로 필드가 누락되면 데이터 소실이 발생생할 수 있어 위험하다.
    추가적으로 설정할 수 있으나 가독성, 안정성을 고민하면 merge보다는 변경감지가 권장된다. 

### set을 사용하기보다는 변경감지를 처리할 method를 만들어 추적하도록 하는 것이 권장된다. 
    setter를 쓰기보다 의미있는 method를 만들어 가능한 추적이 쉽도록만드는 것을 권장
    @Transactional
    public void updateItem(Long itemId, Book bookParam){ //parameter로 넘어온 준영속 상태의 entity
        Book persistBook =  changeItemProperties(itemId, bookParam);
    }
    /**
     * 준영속 엔티티에 영속성 추가해서 리턴
     */
    private Book changeItemProperties(Long itemId, Book bookParam) {
        Item foundItem = itemRepository.findOne(itemId); //같은 id를 지닌 영속성 entity 조회
        //        if(foundItem.getType==="B")
        Book foundBook = (Book)foundItem;
        foundItem.setId(bookParam.getId());
        foundItem.setName(bookParam.getName());
        foundItem.setPrice(bookParam.getPrice());
        foundItem.setStockQuantity(bookParam.getStockQuantity());
        foundBook.setAuthor(bookParam.getAuthor());
        foundBook.setIsbn(bookParam.getIsbn());
        return foundBook;
    }

# 데이터를 처리할때 주의사항
    - entity 변경시에는 항상 변경감지 사용
    - 컨트롤러에서는 어설프게 엔티티를 생성하지 말기()
    - 트랜잭션이 있는 서비스 계층에 식별자와 변경할 데이터를 명확하게 전달하기(id, dto 구분)
    - 트랜잭션이 있는 서비스 계층에서 영속 상태의 엔티티를 조회하고 그 엔티티의 데이터를 직접 변경
    - 트랜잭션의 커밋 실행지점에서 데이터의 변경감지가 실행되어 저장됨.

### Controller
        @PostMapping("/{itemId}/edit")
    public String editItem(@PathVariable("itemId") Long itemId, @ModelAttribute("form") BookForm form){
    //  Book book = new Book(); ... 중략 ... 어설프게 컨트롤러에서 데이터 객체 만들지 말고
        itemService.updateItem(itemId, form);//그냥 form 객체를 @Transaction service로 넘기기
        //별도로 update 로직을 짜지 않고 변경을 감지해서 저장하도록 함
        return "redirect:/items";
    }
### Service
    @Transactional  
    public void updateItem(Long itemId, BookForm bookParam){
        //변경감지를 통핸 데이터 변경을 명시한 method(setter 사용 x)
        Book persistBook =  changeItemProperties(itemId, bookParam.getName(), bookParam.getPrice(),bookParam.getStockQuantity(),bookParam.getAuthor(),bookParam.getIsbn());
    }
    private Book changeItemProperties(Long itemId, String name, int price, int stockQuantity, String author, String isbn) {
        Item foundItem = itemRepository.findOne(itemId); //같은 id를 지닌 영속성 entity 조회
        // entity의 데이터를 직접 변경
        foundItem.setName(name);
        foundItem.setPrice(price);
        ... 중략 ...
        return foundBook;
    }

    + 너무 parameter가 많으면 그 용도의 DTO를 사용하면 됨
    @Getter @Setter
    class UpdateItemDto ... 