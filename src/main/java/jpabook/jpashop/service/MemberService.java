package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
// JPA는 기본적으로 Transactional 필수
// javax보다는 spring annotaion 사용 권장
// 데이터를 변경하지 않는 경우를 기본으로 readOnly 처리
@RequiredArgsConstructor //생성자 인젝션 자동화
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional// 데이터가 수정되는 경우 readOnly default인 false가 적용되도록 함
    public Long join(Member member){
        validateDuplicateMember(member); // 검증
        memberRepository.save(member); // 저장
        return member.getId(); //반환 : persistance를 처리하면 db 저장 전에 id값은 보장이 됨
    }

    private void validateDuplicateMember(Member member) {
        //검증 로직이 서버단에 있더라도 다중 접속과 처리 상황이 있을 수 있으므로 검증이 필요한 경우 unique 처리
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다");
        }
    }

    /**
     * 회원 전체 조회
     */
    @Transactional(readOnly = true)
    public List<Member> findMemebers(){
        return memberRepository.findAll();
    }
    /**
     * ID로 단건 조회
     */
    public Member findOne(Long id){
        return memberRepository.findOne(id);
    }
}
