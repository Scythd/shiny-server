/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.controllers;

import com.moklyak.Game.server.JDBCTemplates.UserJDBCTemplate;
import com.moklyak.Game.server.configurations.security.JwtTokenProvider;
import com.moklyak.Game.server.entities.User;
import com.moklyak.Game.server.models.QueueResultDto;
import com.moklyak.Game.server.services.JWTUserDetailsService;
import java.util.ArrayDeque;
import java.util.Deque;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

/**
 *
 * @author Пользователь
 */
@RestController
@RequestMapping(value = "/api/game/queue")
public class GameQueueControler {
    
    
    private final Deque<User> gameBullCowQueue = new ArrayDeque<>();
    private final Deque<User> gameCheckersQueue = new ArrayDeque<>();
    private final Deque<User> gameChessQueue = new ArrayDeque<>();
    
    private final JwtTokenProvider jwtTokenProvider;
    private final UserJDBCTemplate userJDBCTemplate;

    public GameQueueControler(JwtTokenProvider jwtTokenProvider, UserJDBCTemplate userJDBCTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userJDBCTemplate = userJDBCTemplate;
    }

    @PostMapping("/queue/enter")
    public void enterQueue(@RequestParam("gameType") String gameType, @RequestHeader("Authorization") String token){
        String username = jwtTokenProvider.getUsername(token);
        User user = userJDBCTemplate.findByUsername(username);
        
        switch (gameType){
            case "BullCow" -> {
                gameBullCowQueue.addLast(user);
            }
            case "Checkers" -> {
                gameCheckersQueue.addLast(user);
            }
            case "Chess" -> {
                gameChessQueue.addLast(user);
            }
            default -> throw new RuntimeException("Game type not supported");
        }
        queueResult();
    }
    
    public ResponseEntity<QueueResultDto> queueResult(){
        // упёрся в асинхронность, хз как это разрешить
        
        
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
    
}
