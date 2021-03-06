package com.olbimacoojam.heaven.controller;

import com.olbimacoojam.heaven.domain.UserSession;
import com.olbimacoojam.heaven.dto.GameStartResponseDto;
import com.olbimacoojam.heaven.dto.RoomResponseDto;
import com.olbimacoojam.heaven.service.RoomService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;

import static com.olbimacoojam.heaven.HttpHandshakeInterceptor.HTTP_SESSION;

@Controller
public class WebsocketRoomController {

    private final RoomService roomService;

    public WebsocketRoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @MessageMapping("/rooms/{roomId}")
    @SendTo("/topic/rooms/{roomId}")
    public RoomResponseDto enterRoom(@DestinationVariable int roomId, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        HttpSession httpSession = (HttpSession) (simpMessageHeaderAccessor.getSessionAttributes().get(HTTP_SESSION));
        UserSession userSession = (UserSession) httpSession.getAttribute(UserSession.USER_SESSION);
        Long userId = userSession.getId();
        return roomService.subscribe(roomId, userId);
    }

    @MessageMapping("/rooms/{roomId}/leave/{userId}")
    @SendTo("/topic/rooms/{roomId}/leave")
    public RoomResponseDto leaveRoom(@DestinationVariable int roomId, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        HttpSession httpSession = (HttpSession) (simpMessageHeaderAccessor.getSessionAttributes().get(HTTP_SESSION));
        UserSession userSession = (UserSession) httpSession.getAttribute(UserSession.USER_SESSION);
        Long userId = userSession.getId();
        return roomService.unsubscribe(roomId, userId);
    }

    @MessageMapping("/rooms/{roomId}/start")
    @SendTo("/topic/rooms/{roomId}/start")
    public GameStartResponseDto startGame(@DestinationVariable int roomId) {
        return roomService.startGame(roomId);
    }
}
