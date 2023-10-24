package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class TestMemberRepositoryTest {
    @Autowired TestMemberRepository testMemberRepository;

//    test용 템플릿으로 만들어서 사용하는 것도 유용
//    @Test
//    public void testThat() throws Exception{
//        //given
//
//        //when
//
//        //then
//    }
    @Test
    @Transactional  // Spring의 기본 Transactional이 없으면 에러
                    // Test 코드에 Transactional이 있으면 자동 Rollback
//    @Rollback(false) // 테스트 코드를 롤백하지 않고 저장하고 싶으면 false로
    public void testMember() throws Exception{
        //given
        TestMember member = new TestMember();
        member.setUserName("memberA");
        //when
        Long savedId = testMemberRepository.saveMember(member);
        TestMember findMember = testMemberRepository.findOne(savedId);
        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUserName()).isEqualTo(member.getUserName());

        //같은 트랜잭션 안에서 영속성을 처리하는 경우 id값이 같으면 같은 것으로 처리된다.
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}

