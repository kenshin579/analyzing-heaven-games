package com.olbimacoojam.heaven.controller;

import com.olbimacoojam.heaven.game.GameKind2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoomApiControllerTests {

    public static final String ROOMS_URL = "/rooms";
    private static final String JSESSIONID = "JSESSIONID";

    private final FieldDescriptor[] roomResponseFields = {
            fieldWithPath("id").description("room 고유 식별자"),
            fieldWithPath("roomState").description("room 상태 (게임 진행 중 여부)"),
            fieldWithPath("players").description("room에 참여하고 있는 user 목록")
    };

    @LocalServerPort
    private String port;
    private WebTestClient webTestClient;
    private String jsessionid;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .filter(documentationConfiguration(restDocumentation))
                .build();

        jsessionid = mockLogin(111111L);
    }

    @Test
    @DisplayName("게임방 생성테스트")
    @DirtiesContext
    void create_room_test() {
        webTestClient.post()
                .uri(ROOMS_URL + getGameKindQueryString(GameKind2.YUTNORI))
                .cookie(JSESSIONID, jsessionid)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader()
                .exists("location")
                .expectBody()
                .consumeWith(document("room-api/create-room",
                        responseHeaders(
                                headerWithName("Location").description("created room uri")
                        )));
    }

    @Test
    void list_room_test() {
        int roomId1 = createRoom(GameKind2.MAFIA);
        int roomId2 = createRoom(GameKind2.YUTNORI);
        int roomId3 = createRoom(GameKind2.MAFIA);

        webTestClient.get()
                .uri(ROOMS_URL + getGameKindQueryString(GameKind2.MAFIA))
                .cookie(JSESSIONID, jsessionid)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").isEqualTo(roomId1)
                .jsonPath("$[1].id").isEqualTo(roomId3)
                .consumeWith(document("room-api/list-room",
                        responseFields(fieldWithPath("[]").description("room 목록"))
                                .andWithPrefix("[].", roomResponseFields)
                ));
    }

    private int createRoom(GameKind2 gameKind) {
        String location = webTestClient.post()
                .uri(ROOMS_URL + getGameKindQueryString(gameKind))
                .cookie(JSESSIONID, jsessionid)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseHeaders()
                .get("location").get(0);
        return Integer.parseInt(location.substring(ROOMS_URL.length() + 1));
    }

    private String getGameKindQueryString(GameKind2 gameKind) {
        return String.format("?gameKind=%s", gameKind);
    }

    private String mockLogin(Long kakaoId) {
        return webTestClient.post()
                .uri("/mock/login")
                .body(Mono.just(kakaoId), Long.class)
                .exchange()
                .returnResult(String.class)
                .getResponseCookies()
                .get(JSESSIONID).get(0).getValue();
    }
}
