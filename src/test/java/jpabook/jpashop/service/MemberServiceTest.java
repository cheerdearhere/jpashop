package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest // 단위테스트를 넘어 db 연동까지 스프링 전체 포괄 테스트
@Transactional // 롤백
class MemberServiceTest {
//    관련 인젝션
    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em; // persistance 관리할경우 injection
    @Test
    //@Rollback(false) //db에서 결과를 볼경우
    void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("kim");
        //when
        Long savedId =  memberService.join(member);
        //then
//        em.flush();//롤백은 하지만 insert문은 표시
        assertEquals(member, memberRepository.findOne(savedId));
    }
    @Test
    void 중복_가입_예외() throws Exception{
        //given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");
        //when
        memberService.join(member1);
        //then
        Assertions.assertThrows(IllegalStateException.class, ()->{
            memberService.join(member2);
        });
    }

//    메모리 DB 사용하기

}