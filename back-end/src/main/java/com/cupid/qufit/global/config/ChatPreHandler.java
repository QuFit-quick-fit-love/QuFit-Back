package com.cupid.qufit.global.config;

import com.cupid.qufit.global.security.util.JWTUtil;
import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatPreHandler implements ChannelInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // ! 연결 요청시 JWT 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // ! Authorization 헤더 추출
            String token = accessor.getFirstNativeHeader("Authorization");
            log.info("token = {}", token);
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // ! 추출
                try {
                    // ! JWT 토큰 검증
                    Map<String, Object> claims = jwtUtil.validateToken(token);
//                    log.info("claims = {}", claims); // ! 전체 클레임 로깅
                    Long memberId = Long.parseLong(claims.get("id").toString());
//                    log.info("memberId = {}", memberId);
                    accessor.setUser(new Principal() {
                        @Override
                        public String getName() {
                            return memberId.toString();
                        }
                    });
                } catch (Exception e) {
                    // ! 토큰 검증 실패
                    log.info("error = {}", e.getMessage());
                    return null;
                }
            }
        } else {
            return null; // ! 토큰 없거나 형식 잘못됨
        }

        return message;

    }
}
