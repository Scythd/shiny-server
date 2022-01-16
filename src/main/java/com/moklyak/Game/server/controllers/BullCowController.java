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
import com.moklyak.Game.server.models.BullCowGameDto;
import com.moklyak.Game.server.models.GameState;
import com.moklyak.Game.server.models.WinSide;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Пользователь
 */
@RestController
@RequestMapping(value = "/api/game/bullcow")
public class BullCowController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDAO userDao;
    private final GameDAO gameDao;

    public BullCowController(JwtTokenProvider jwtTokenProvider, UserDAO userDao, GameDAO gameDao) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDao = userDao;
        this.gameDao = gameDao;
    }

    @GetMapping("/getInfo")
    public ResponseEntity<BullCowGameDto> getInfo(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        User user = userDao.findByUsername(username);

        GameEntity ge = gameDao.findByUserId(user.getId());
        BullCowGameDto cgd = ge.toBullCowGameDto(user.getId());
        if (Objects.equals(user.getId(), cgd.getPlayerFirst())) {
            cgd.setPlayerNum(1);
        } else {
            cgd.setPlayerNum(2);
        }
        return new ResponseEntity<>(cgd, HttpStatus.OK);
    }

    @PostMapping("/setAnswer")
    public ResponseEntity<BullCowGameDto> setAnswer(@RequestHeader("Authorization") String token, @RequestBody String answer) {
        String username = jwtTokenProvider.getUsername(token);
        User user = userDao.findByUsername(username);

        GameEntity ge = gameDao.findByUserId(user.getId());

        int ans = Integer.valueOf(answer);
        if (answer.length() != 4 || answer.chars().distinct().count() != 4) {
            BullCowGameDto cgd = ge.toBullCowGameDto(user.getId());
            return new ResponseEntity<>(cgd, HttpStatus.NOT_ACCEPTABLE);
        }

        int[][] info = ge.getGameInfo();
        if (Objects.equals(ge.getPlayerFirst(), user.getId())) {

            if (info[0][0] == -1) {
                info[0][0] = ans;
            }
            // else erroe alredy set?
        } else if (Objects.equals(ge.getPlayerSecond(), user.getId())) {

            if (info[0][1] == -1) {
                info[0][1] = ans;
            }
            // else erroe alredy set?
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
            // error user not from this game
        }
        if (info[0][0] != -1 && info[0][1] != -1) {
            ge.setTurn(1);
            ge.setGameState(GameState.RUNNING);
        }
        ge.setGameInfo(info);
        ge = gameDao.saveGame(ge);
        BullCowGameDto cgd = ge.toBullCowGameDto(user.getId());
        if (Objects.equals(user.getId(), cgd.getPlayerFirst())) {
            cgd.setPlayerNum(1);
        } else {
            cgd.setPlayerNum(2);
        }
        return new ResponseEntity<>(cgd, HttpStatus.OK);
    }

    @PostMapping("/playerGuess")
    public ResponseEntity<BullCowGameDto> playerGuess(@RequestHeader("Authorization") String token, @RequestBody String guess) {
        String username = jwtTokenProvider.getUsername(token);
        User user = userDao.findByUsername(username);

        GameEntity ge = gameDao.findByUserId(user.getId());
        int playerGuess = Integer.valueOf(guess);
        // validate guess : distinct numbers in guess and number count is 4
        if (guess.length() != 4 || guess.chars().distinct().count() != 4) {
            BullCowGameDto cgd = ge.toBullCowGameDto(user.getId());
            return new ResponseEntity<>(cgd, HttpStatus.NOT_ACCEPTABLE);
        }

        //resolve indexes for info field 
        int playerNum = -1;
        int iAnswer;
        int iGuessHist;
        if (Objects.equals(ge.getPlayerFirst(), user.getId())) {
            playerNum = 1;
            if (ge.getTurn() % 2 != 1) {
                BullCowGameDto cgd = ge.toBullCowGameDto(user.getId());
                return new ResponseEntity<>(cgd, HttpStatus.NOT_ACCEPTABLE);
            }
            iAnswer = 1;
            iGuessHist = 1;
            // else erroe alredy set?
        } else if (Objects.equals(ge.getPlayerSecond(), user.getId())) {
            playerNum = 2;
            if (ge.getTurn() % 2 != 0) {
                BullCowGameDto cgd = ge.toBullCowGameDto(user.getId());
                return new ResponseEntity<>(cgd, HttpStatus.NOT_ACCEPTABLE);
            }
            iAnswer = 0;
            iGuessHist = 4;
            // else erroe alredy set?
        } else {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }

        int[][] info = ge.getGameInfo();
        String answer = String.valueOf(info[0][iAnswer]);
        byte[] ansNumbers = answer.getBytes();
        byte[] guessNumbers = guess.getBytes();

        // count bulls ans cows
        int bulls = 0, cows = 0;
        for (int i = 0; i < 4; i++) {
            
            for (int j = 0; j < 4; j++) {
                if (ansNumbers[i] == guessNumbers[j]) {
                    if (i == j) {
                        bulls++;
                    } else {
                        cows++;
                    }
                    break;
                }
            }
        }
        // add to info //
        // resize hist
        info[iGuessHist] = Arrays.copyOf(info[iGuessHist], info[iGuessHist].length + 1);
        info[iGuessHist + 1] = Arrays.copyOf(info[iGuessHist + 1], info[iGuessHist + 1].length + 1);
        info[iGuessHist + 2] = Arrays.copyOf(info[iGuessHist + 2], info[iGuessHist + 2].length + 1);
        // write to hist
        info[iGuessHist][info[iGuessHist].length - 1] = playerGuess;
        info[iGuessHist + 1][info[iGuessHist + 1].length - 1] = bulls;
        info[iGuessHist + 2][info[iGuessHist + 2].length - 1] = cows;
        // write to entity
        ge.setGameInfo(info);
        ge.setTurn(ge.getTurn() + 1);
        // resolve win

        if (playerNum == 2) {
            if (bulls == 4) {
                if (info[2][info[2].length - 1] != 4) {
                    ge.setWinPlayer(WinSide.SECOND_PLAYER);
                } else {
                    ge.setWinPlayer(WinSide.DRAW);
                }
                ge.setGameState(GameState.ENDED);
            }
            if (info[2][info[2].length - 1] == 4) {
                ge.setWinPlayer(WinSide.FIRST_PLAYER);
                ge.setGameState(GameState.ENDED);
            }
        }

        // update in db
        ge = gameDao.saveGame(ge);
        BullCowGameDto cgd = ge.toBullCowGameDto(user.getId());
        cgd.setPlayerNum(playerNum);
        return new ResponseEntity<>(cgd, HttpStatus.OK);
    }
}
