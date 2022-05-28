/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moklyak.Game.server.DAOs.GameDAO;
import com.moklyak.Game.server.DAOs.KafkaDao;
import com.moklyak.Game.server.DAOs.KafkaDao2;
import com.moklyak.Game.server.DAOs.UserDAO;
import com.moklyak.Game.server.configurations.security.JwtTokenProvider;
import com.moklyak.Game.server.entities.GameEntity;
import com.moklyak.Game.server.entities.User;
import com.moklyak.Game.server.models.BullCowGameDto;
import com.moklyak.Game.server.models.GameState;
import com.moklyak.Game.server.models.WinSide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.*;

/**
 *
 * @author Пользователь
 */
@RestController
@RequestMapping(value = "/api/game/bullcow")
public class BullCowController {

    static final private Random r = new Random(3001);

    public static final Map<Long, ResponseEntity> responses = new HashMap<>();
    static ObjectMapper objectMapper = new ObjectMapper();
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDAO userDao;
    private final GameDAO gameDao;



    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(KafkaDao2 msg) {
        try {
            kafkaTemplate.send("BULL-COW-IN", objectMapper.writeValueAsString(msg));
        } catch (Exception ex){

        }
    }



    public BullCowController(JwtTokenProvider jwtTokenProvider, UserDAO userDao, GameDAO gameDao) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDao = userDao;
        this.gameDao = gameDao;
    }

    @GetMapping("/getInfo")
    public ResponseEntity<BullCowGameDto> getInfo(@RequestHeader("Authorization") String token) {
        KafkaDao2 kd = new KafkaDao2();
        Long hash;
        hash = r.nextLong();
        kd.setId(hash);
        kd.setMethod("getInfo");
        kd.setToken(token);
        kd.setArg("");
        sendMessage(kd);

        ResponseEntity cgd = null;
        while (responses.get(hash) == null){
            try {
                Thread.sleep(100);

            } catch (Exception ex){
            }
        }
        cgd = responses.get(hash);
        if (cgd == null){
            cgd = ResponseEntity.status(295).body("{\"length\":\"0\"}");
        }
        responses.remove(r);
        return cgd;
    }

    @PostMapping("/setAnswer")
    public ResponseEntity<BullCowGameDto> setAnswer(@RequestHeader("Authorization") String token, @RequestBody String answer) {
        KafkaDao2 kd = new KafkaDao2();
        Long hash;
        hash = r.nextLong();
        kd.setId(hash);
        kd.setMethod("setAnswer");
        kd.setToken(token);
        kd.setArg(answer);
        sendMessage(kd);

        ResponseEntity cgd = null;
        while (responses.get(hash) == null){
            try {
                Thread.sleep(100);

            } catch (Exception ex){
            }
        }
        cgd = responses.get(hash);
        if (cgd == null){
            cgd = ResponseEntity.status(295).body("{\"length\":\"0\"}");
        }
        responses.remove(r);
        return cgd;
    }

    @PostMapping("/playerGuess")
    public ResponseEntity<BullCowGameDto> playerGuess(@RequestHeader("Authorization") String token, @RequestBody String guess) {
        KafkaDao2 kd = new KafkaDao2();
        Long hash;
        hash = r.nextLong();
        kd.setId(hash);
        kd.setMethod("playerGuess");
        kd.setToken(token);
        kd.setArg(guess);
        sendMessage(kd);

        ResponseEntity cgd = null;
        while (responses.get(hash) == null){
            try {
                Thread.sleep(100);

            } catch (Exception ex){
            }
        }
        cgd = responses.get(hash);
        if (cgd == null){
            cgd = ResponseEntity.status(295).body("{\"length\":\"0\"}");
        }
        responses.remove(r);
        return cgd;
    }
}
