package com.olbimacoojam.heaven.controller.yutnori;

import com.olbimacoojam.heaven.domain.UserSession;
import com.olbimacoojam.heaven.dto.GameStartResponseDtos;
import com.olbimacoojam.heaven.dto.MoveRequestDto;
import com.olbimacoojam.heaven.dto.YutResponse;
import com.olbimacoojam.heaven.service.RoomService;
import com.olbimacoojam.heaven.yutnori.piece.moveresult.MoveResults;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpSession;

import static com.olbimacoojam.heaven.HttpHandshakeInterceptor.HTTP_SESSION;

@Controller
public class YutnoriGameController {
    private RoomService roomService;

    public YutnoriGameController(RoomService roomService) {
        this.roomService = roomService;
    }

    @MessageMapping("/yutnori/{roomId}/move-piece")
    @SendTo("/topic/yutnori/{roomId}")
    public MoveResults movePiece(@DestinationVariable int roomId, @Payload MoveRequestDto moveRequestDto, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        HttpSession httpSession = (HttpSession) (simpMessageHeaderAccessor.getSessionAttributes().get(HTTP_SESSION));
        UserSession userSession = (UserSession) httpSession.getAttribute(UserSession.USER_SESSION);
        Long userId = userSession.getId();
        return roomService.movePiece(roomId, userId, moveRequestDto);
    }

    @MessageMapping("/yutnori/{roomId}/yut-throw")
    @SendTo("/topic/yutnori/{roomId}/yut-throw")
    public YutResponse throwYut(@DestinationVariable int roomId, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        HttpSession httpSession = (HttpSession) (simpMessageHeaderAccessor.getSessionAttributes().get(HTTP_SESSION));
        UserSession userSession = (UserSession) httpSession.getAttribute(UserSession.USER_SESSION);
        Long userId = userSession.getId();
        return roomService.throwYut(roomId, userId);
    }

    @MessageMapping("/yutnori/{roomId}")
    @SendTo("/topic/yutnori/{roomId}")
    public GameStartResponseDtos initiateGame(@DestinationVariable int roomId) {
        return roomService.initiateGame(roomId);
    }
}
