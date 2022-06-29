package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    public static final String SESSION_COOKIE_NAME = "mysessionId";
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();

    /**
     * 세션 생성
     */
    public void createSession(Object value, HttpServletResponse response) {

        // 세션 id를 생성하고, 값을 세션에 저장
        String sessionid = UUID.randomUUID().toString(); // 클라이언트(브라우저)에 보낼 sessionid
        sessionStore.put(sessionid, value); // 세션을 보관 (key = 랜덤으로 생성된 sessionid, value = 로그인한 사용자 정보 (member 객체)

        // 쿠키 생성 (key = 상수(loginMember), value = 랜덤으로 생성된 sessionid)
        Cookie mysessionCookil = new Cookie(SESSION_COOKIE_NAME, sessionid);
        response.addCookie(mysessionCookil);
    }

    /**
     * 세션 조회
     */
    public Object getSession(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);
        if (sessionCookie == null) {
            return null;
        }
        return sessionStore.get(sessionCookie.getValue());
    }

    /**
     * 세션 만료
     */
    public void expire(HttpServletRequest request) {
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);

        if (sessionCookie != null) {
            sessionStore.remove(sessionCookie.getValue());
        }
    }

    public Cookie findCookie(HttpServletRequest req, String cookieName) {

        if (req.getCookies() == null) {
            return null;
        }

        return Arrays.stream(req.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny() // findAny는 순서와 상관없이 제일 먼저 추출된 value
                .orElse(null);
    }

}
