/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.controllers;

import com.moklyak.Game.server.DAOs.QueueDao;
import com.moklyak.Game.server.DAOs.QueueResultDao;
import com.moklyak.Game.server.DAOs.UserDAO;
import com.moklyak.Game.server.JDBCTemplates.QueueJDBCTemplate;
import com.moklyak.Game.server.JDBCTemplates.UserJDBCTemplate;
import com.moklyak.Game.server.configurations.security.JwtTokenProvider;
import com.moklyak.Game.server.entities.QueueEntity;
import com.moklyak.Game.server.entities.QueueResultEntity;
import com.moklyak.Game.server.entities.User;
import com.moklyak.Game.server.models.EnterQueueDto;
import com.moklyak.Game.server.models.QueueResultDto;
import com.moklyak.Game.server.services.JWTUserDetailsService;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

//    private final Deque<User> gameBullCowQueue = new ArrayDeque<>();
//    private final Deque<User> gameCheckersQueue = new ArrayDeque<>();
//    private final Deque<User> gameChessQueue = new ArrayDeque<>();
//    private final List<QueueResults> queueResults = new ArrayList<>();
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDAO userDao;
    private final QueueDao queueDao;
    private final QueueResultDao queueResultDao;
    
    public GameQueueControler(JwtTokenProvider jwtTokenProvider, UserDAO userDao, QueueDao queueDao, QueueResultDao queueResultDao) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDao = userDao;
        this.queueDao = queueDao;
        this.queueResultDao = queueResultDao;
    }
    
    @PostMapping("/enter")
    public ResponseEntity<QueueResultDto> enterQueue(@RequestBody EnterQueueDto eqd, @RequestHeader("Authorization") String token) {
        // resolve asked user
        String username = jwtTokenProvider.getUsername(token);
        User user = userDao.findByUsername(username);
        // crete and add queueentity
        QueueEntity qe = new QueueEntity(user.getId(), -1, eqd.getGameType());
        queueDao.save(qe);
        return queueResult(token);
    }
    
    @GetMapping("/result")
    public ResponseEntity<QueueResultDto> queueResult(@RequestHeader("Authorization") String token) {
        // resolve asked user
        String username = jwtTokenProvider.getUsername(token);
        User user = userDao.findByUsername(username);
        // weird thing
        // first go to queue table and watch if user in it
        // if not 
        // go to queueresults and watch here
        // watch queu again (cause of some sort of bad realisation here)
        // if not send back error smth like user not in queue anymore
        QueueResultDto qrd;
        QueueResultEntity qre = null;
        QueueEntity qe = queueDao.findByUserId(user.getId());
        if (qe == null) {
            qre = queueResultDao.findByUserId(user.getId());
            if (qre == null) {
                qe = queueDao.findByUserId(user.getId());
                if (qe == null) {
                    qrd = new QueueResultDto();
                    qrd.setQueueState("noPos");
                } else {
                    qrd = qe.toDto();
                    qrd.setPlayerNum(0);
                }
            } else {
                qrd = qre.toDto();
                if (Objects.equals(user.getId(), qre.getPlayer1())) {
                    qrd.setPlayerNum(1);
                } else {
                    qrd.setPlayerNum(2);
                }
            }
        } else {
            qrd = qe.toDto();
            qrd.setPlayerNum(0);
        }
        
        return new ResponseEntity<>(qrd, HttpStatus.OK);
    }
    
    @GetMapping("/becomeready")
    public ResponseEntity<QueueResultDto> becomeReady(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        User user = userDao.findByUsername(username);
        queueResultDao.setPlayerReady(user.getId());
        QueueResultEntity qre = queueResultDao.findByUserId(user.getId());
        QueueResultDto qrd;
        if (qre != null) {
            qrd = qre.toDto();
        } else {
            qrd = new QueueResultDto();
            qrd.setQueueState("noPos");
        }
        return new ResponseEntity<>(qrd, HttpStatus.OK);
    }
    
    @GetMapping("/leavequeue")
    public ResponseEntity<QueueResultDto> leaveQueue(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        User user = userDao.findByUsername(username);
        queueDao.leaveQueue(user.getId());
        QueueResultDto qrd = new QueueResultDto();
        qrd.setQueueState("noPos");
        return new ResponseEntity<>(qrd, HttpStatus.OK);
    }
}
