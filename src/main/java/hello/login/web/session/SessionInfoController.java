package hello.login.web.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SessionInfoController {

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            return "세션이 없습니다.";
        }

        // 세션 데이터 출력
        session.getAttributeNames().asIterator()
                .forEachRemaining(name -> log.info("session = {}, value = {}", name, session.getAttribute(name)));

        log.info("sessionId = {}", session.getId());                                    // 브라우저 세션 id에 사용된 jsessionId
        log.info("getMaxInactiveInterval = {} ", session.getMaxInactiveInterval());     // 세션 유효시간
        log.info("creationTime = {} ", new Date(session.getCreationTime()));            // 세션 생성 시간
        log.info("lastAccessedTime = {} ", new Date(session.getLastAccessedTime()));    // 세션 마지막 접근 시간
        log.info("isNew = {} ", session.isNew());                                       // 세션이 지금 만들어 졌냐, 아니면 이전에 만든것이냐 확인

        return "세션 출력";
    }
}
