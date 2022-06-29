package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController extends SessionConst{

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    // @GetMapping("/")
    public String home() {
        return "home";
    }

    //@GetMapping("/")
    public String homeLogin(@CookieValue(value = "memberId", required = false) Long memberId, Model model) {
        if (memberId == null) {
            return "home";
        }

        // 로그인
        Member loginMember = memberRepository.findById(memberId);

        if (loginMember == null) {
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homeLoginV2(HttpServletRequest req, Model model) {
        // 세션 관리자에 저장된 회원 정보
        Member member = (Member)sessionManager.getSession(req);

        // 로그인
        if (member == null) {
            return "home";
        }

        model.addAttribute("member", member);
        return "loginHome";
    }

    @GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = LOGIN_MEMBER, required = false) Member loginMember, Model model) {

        // 아래 코드를 @SessionAttribute로 대체
        // HttpSession session = req.getSession(false);

        /*if (session == null) {
            return "home";
        }*/

        // Member loginMember = (Member) session.getAttribute(LOGIN_MEMBER);

        // @SessionAttribute(name = LOGIN_MEMBER, required = false) Member loginMember를 통해 
        // Member 객체에 로그인한 사용자 정보를 담았음
        // 세션에 회원정보가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}