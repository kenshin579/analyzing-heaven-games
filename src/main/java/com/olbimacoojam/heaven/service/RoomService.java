package com.olbimacoojam.heaven.service;

import com.olbimacoojam.heaven.domain.Room;
import com.olbimacoojam.heaven.domain.RoomFactory;
import com.olbimacoojam.heaven.domain.RoomRepository;
import com.olbimacoojam.heaven.domain.User;
import com.olbimacoojam.heaven.dto.GameStartResponseDtos;
import com.olbimacoojam.heaven.dto.RoomResponseDto;
import com.olbimacoojam.heaven.dto.YutResponse;
import com.olbimacoojam.heaven.game.Game;
import com.olbimacoojam.heaven.yutnori.YutnoriGame;
import com.olbimacoojam.heaven.yutnori.yut.RandomYutThrowStrategy;
import com.olbimacoojam.heaven.yutnori.yut.Yut;
import com.olbimacoojam.heaven.yutnori.yut.YutThrowStrategy;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {
    private final RoomFactory roomFactory;
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;
    private final YutThrowStrategy yutThrowStrategy;

    @Autowired
    public RoomService(RoomFactory roomFactory, ModelMapper modelMapper, RoomRepository roomRepository) {
        this.roomFactory = roomFactory;
        this.modelMapper = modelMapper;
        this.roomRepository = roomRepository;
        this.yutThrowStrategy = new RandomYutThrowStrategy();
    }

    public RoomResponseDto createRoom() {
        Room room = roomFactory.makeNextRoom();
        roomRepository.save(room);
        return modelMapper.map(room, RoomResponseDto.class);
    }

    public List<RoomResponseDto> findAll() {
        List<Room> rooms = roomRepository.findAll();
        return rooms.stream()
                .map(room -> modelMapper.map(room, RoomResponseDto.class))
                .collect(Collectors.toList());
    }

    public RoomResponseDto subscribe(int roomId, User user) {
        Room room = roomRepository.findById(roomId);
        room.join(user);
        return modelMapper.map(room, RoomResponseDto.class);
    }

    public RoomResponseDto unsubscribe(int roomId) {
        Room room = roomRepository.findById(roomId);
        room.leave();
        return modelMapper.map(room, RoomResponseDto.class);
    }

    public Room findById(int roomId) {
        return roomRepository.findById(roomId);
    }

    public int startGame(int roomId) {
        Room room = roomRepository.findById(roomId);
        room.startGame();
        List<User> players = room.getPlayers();
        Game game = room.getGame();
        game.initialize(players);

        return players.size();
    }

    public GameStartResponseDtos initiateGame(int roomId) {
        Room room = roomRepository.findById(roomId);
        GameStartResponseDtos gameStartResponseDtos = room.initiateGame();
        return gameStartResponseDtos;
    }

    public YutResponse throwYut(int roomId, User thrower) {
        Room room = roomRepository.findById(roomId);
        YutnoriGame yutnoriGame = (YutnoriGame) room.getGame();
        Yut yut = yutnoriGame.throwYut(thrower, yutThrowStrategy);
        return new YutResponse(yut.name());
    }
}
