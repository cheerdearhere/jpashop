package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {
    //    @PersistenceContext : @RequiredArgConstructor가 처리
    private final EntityManager em;

    public void save(Member member){
        em.persist(member);
    }
    public Member findOne(Long id){
        return em.find(Member.class, id);
    }
    public List<Member> findAll(){
        return em.createQuery("select m from Member m",Member.class)
                .getResultList();
        /*
            SQL  : DB의 table에 접근
            JPQL : SQL과 거의 유사하나 Entity 객체에 대해 접근하는 것이 다름
         */
    }
    public List<Member> findByName(String name){
        return em.createQuery("select m from Member m where m.name = :searchKey",Member.class)
                .setParameter("searchKey",name)
                .getResultList();
    }
}
