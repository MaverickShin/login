package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;


    /**
     * @return null 로그인 실패
     */
    public Member login(String loginId, String password) {
        Optional<Member> optional = memberRepository.findByLoginId(loginId);

        /*Member member= optional.get();
        if (member.getPassword().equals(password)) {
            return member;
        } else {
            return null;
        }*/

        return optional.filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}
