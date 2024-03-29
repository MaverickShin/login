package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController extends SessionConst{

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    // @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse res) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리 TODO

        // 쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 모두 종료)
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        res.addCookie(idCookie);

        // 쿠키와 보안 문제
        // 쿠키를 사용해서 로그인Id를 전달해서 로그인을 유지할 수 있었다. 그런데 여기에는 심각한 보안 문제가있다.

        //보안 문제
        //쿠키 값은 임의로 변경할 수 있다.
        //클라이언트가 쿠키를 강제로 변경하면 다른 사용자가 된다.
        //실제 웹브라우저 개발자모드 Application Cookie 변경으로 확인
        //Cookie: memberId=1 Cookie: memberId=2 (다른 사용자의 이름이 보임)
        //쿠키에 보관된 정보는 훔쳐갈 수 있다.
        //만약 쿠키에 개인정보나, 신용카드 정보가 있다면?
        //이 정보가 웹 브라우저에도 보관되고, 네트워크 요청마다 계속 클라이언트에서 서버로 전달된다.
        //쿠키의 정보가 나의 로컬 PC가 털릴 수도 있고, 네트워크 전송 구간에서 털릴 수도 있다.
        //해커가 쿠키를 한번 훔쳐가면 평생 사용할 수 있다.
        //해커가 쿠키를 훔쳐가서 그 쿠키로 악의적인 요청을 계속 시도할 수 있다.

        //대안
        //쿠키에 중요한 값을 노출하지 않고, 사용자 별로 예측 불가능한 임의의 토큰(랜덤 값)을 노출하고, 서버에서
        //토큰과 사용자 id를 매핑해서 인식한다. 그리고 서버에서 토큰을 관리한다.
        //토큰은 해커가 임의의 값을 넣어도 찾을 수 없도록 예상 불가능 해야 한다.
        //해커가 토큰을 털어가도 시간이 지나면 사용할 수 없도록 서버에서 해당 토큰의 만료시간을 짧게(예: 30분)
        //유지한다. 또는 해킹이 의심되는 경우 서버에서 해당 토큰을 강제로 제거하면 된다.

        return "redirect:/";
    }

    // @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookil(response, "memberId");
        return "redirect:/";
    }

    //@PostMapping("/login")
    public String loginV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse res) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리 TODO

        // 세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
        // 쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 모두 종료)
        sessionManager.createSession(loginMember,res);

        return "redirect:/";
    }

    //@PostMapping("/logout")
    public String logoutV2(HttpServletRequest req) {
        // expireCookil(response, "memberId");
        sessionManager.expire(req);
        return "redirect:/";
    }


    // @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest req) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리 TODO


        // 세션의 create 옵션에 대해 알아보자.
        // request.getSession(true)
        // 세션이 있으면 기존 세션을 반환한다.
        // 세션이 없으면 새로운 세션을 생성해서 반환한다.
        // request.getSession(false)
        // 세션이 있으면 기존 세션을 반환한다.
        // 세션이 없으면 새로운 세션을 생성하지 않는다. null 을 반환한다

        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = req.getSession(true);
        session.setAttribute(LOGIN_MEMBER, loginMember); // 세션에 로그인 회원정보 저장


        // 세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
        // sessionManager.createSession(loginMember,res);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest req) {
        // expireCookil(response, "memberId");
        // sessionManager.expire(req);

        // request.getSession(false)
        // 세션이 있으면 기존 세션을 반환한다.
        // 세션이 없으면 새로운 세션을 생성하지 않는다. null 을 반환한다
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 정보를 모두 삭제
        }

        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV4(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
                          @RequestParam(defaultValue = "/") String redirectURL,
                          HttpServletRequest req) {

        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        //로그인 성공 처리 TODO


        // 세션의 create 옵션에 대해 알아보자.
        // request.getSession(true)
        // 세션이 있으면 기존 세션을 반환한다.
        // 세션이 없으면 새로운 세션을 생성해서 반환한다.
        // request.getSession(false)
        // 세션이 있으면 기존 세션을 반환한다.
        // 세션이 없으면 새로운 세션을 생성하지 않는다. null 을 반환한다

        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = req.getSession(true);
        session.setAttribute(LOGIN_MEMBER, loginMember); // 세션에 로그인 회원정보 저장


        // 세션 관리자를 통해 세션을 생성하고, 회원 데이터 보관
        // sessionManager.createSession(loginMember,res);

        return "redirect:"+redirectURL;
    }

    private void expireCookil(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
