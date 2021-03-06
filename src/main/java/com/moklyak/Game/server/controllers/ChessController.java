/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.controllers;

import com.moklyak.Game.server.DAOs.GameDAO;
import com.moklyak.Game.server.DAOs.UserDAO;
import com.moklyak.Game.server.configurations.security.JwtTokenProvider;
import com.moklyak.Game.server.entities.GameEntity;
import com.moklyak.Game.server.entities.User;
import com.moklyak.Game.server.models.ChessGameDto;
import com.moklyak.Game.server.models.GameDto;
import com.moklyak.Game.server.models.QueueResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Пользователь
 */
@RestController
@RequestMapping(value = "/api/game/chess")
public class ChessController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDAO userDao;
    private final GameDAO gameDao;

    public ChessController(JwtTokenProvider jwtTokenProvider, UserDAO userDao, GameDAO gameDao) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDao = userDao;
        this.gameDao = gameDao;
    }

    @GetMapping("/getInfo")
    public ResponseEntity<ChessGameDto> getInfo(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        User user = userDao.findByUsername(username);
        
        GameEntity ge = gameDao.findByUserId(user.getId());
        ChessGameDto cgd = ge.toChessGameDto(user.getId());
        
        
        return new ResponseEntity<>(cgd, HttpStatus.OK);
    }

}
