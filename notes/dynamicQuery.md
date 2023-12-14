동적쿼리 사용방법

권장: QueryDsl 사용
### 1. 문자열로 직접 조건 추가  - 복잡, 어려움, 에러가 발생할 가능성이 곳곳에 있음
```java
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
```
### 2. JPA Criteria(JPA가 제공하는 동적쿼리용 표준라이브러리) - 쿼리를 연상하기 어려워 유지보수가 어려움 => 운영x
```java
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
```
### 3.(권장)QueryDsl 처리 : 왠만한 복잡한 내용은 QueryDsl 권장
```java
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
```