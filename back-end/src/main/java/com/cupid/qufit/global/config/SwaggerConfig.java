package com.cupid.qufit.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("블라인드 소개팅 API 명세서")
                .description(
                        "<h2>공통프로젝트</h2>" +
                                "<h3>Swagger를 이용한 API 명세서</h3><br>" +
                                "<img src=\"/images/QuFit_Logo.png\" alt = '프로젝트 로고'  width=\"250\">" +
                                "<h3>WebSocket 정보</h3>" +
                                "WebSocket 연결 엔드포인트: ws://i11a209.p.ssafy.io:8080/stomp/chat<br>" +
                                "STOMP 사용<br>" +
                                "메시지 매핑:<br>" +
                                "- 메시지 전송: /pub/chat.sendMessage/{chatRoomId}<br>" +
                                "- 세션 종료: /pub/chat.sessionClose<br>" +
                                "- 채팅방 입장: /pub/chat.enterRoom/{chatRoomId}<br>" +
                                "- 채팅방 퇴장: /pub/chat.leaveRoom/{chatRoomId}<br>" +
                                "- 이전 메시지 로드: /pub/chat.loadPreviousMessages/{chatRoomId}<br>" +
                                "- 다음 메시지 로드: /pub/chat.loadNextMessages/{chatRoomId}<br>" +
                                "구독 주제:<br>" +
                                "- 채팅방 메시지: /sub/chatroom.{chatRoomId}<br>" +
                                "- 개인화된 메시지: /user/{memberId}/sub/chat.messages.{chatRoomId}<br>" +
                                "WebSocket을 통한 실시간 채팅 기능은 swagger에서 테스트 못함.<br> 기능의 존재와 사용방법을 알리기 위한 용도")
                .version("v1.0.0")
                .contact(new Contact()
                                 .name("조현수")
                                 .email("ssafyhyunsoo@gmail.com")
                                 .url("http://i11a209.p.ssafy.io:8080")
                );

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }

    // ! auth 관련 API 모음
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                             .group("auth")
                             .pathsToMatch("/qufit/auth/**")
                             .build();
    }

    // ! member 관련 API 모음
    @Bean
    public GroupedOpenApi memberApi() {
        return GroupedOpenApi.builder()
                             .group("member")
                             .pathsToMatch("/qufit/member/**")
                             .build();
    }

    // ! chat 관련 API 모음
    @Bean
    public GroupedOpenApi chatApi() {
        return GroupedOpenApi.builder()
                             .group("chat")
                             .pathsToMatch("/qufit/chat/**")
                             .build();
    }

    // ! admin 관련 API 모음
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                             .group("admin")
                             .pathsToMatch("/qufit/admin/**")
                             .build();
    }

    // ! WebSocket 관련 API 모음
    public GroupedOpenApi websocketApi() {
        return GroupedOpenApi.builder()
                             .group("websocket")
                             .pathsToMatch("/stomp/chat")
                             .build();
    }
}
