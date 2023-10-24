package jpabook.jpashop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import org.springframework.stereotype.Repository;

@Repository //component
public class TestMemberRepository {
    @PersistenceContext //entity manager 생성(DAO 역할)
    private EntityManager em;

    public Long saveMember(TestMember member){
        em.persist(member); //instance member에 persistance 추가(영구화 > db에 저장)
        return member.getId(); //그 값 중 id 가져오기 (selectKey)
    }

    public TestMember findOne(Long id){
        return em.find(TestMember.class, id); // id값을 근거로 Member instance 호출
    }
}
