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
import com.moklyak.Game.server.models.CheckerMovesDto;
import com.moklyak.Game.server.models.CheckersGameDto;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.util.Chars;
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
@RequestMapping(value = "/api/game/checkers")
public class CheckersController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDAO userDao;
    private final GameDAO gameDao;

    public CheckersController(JwtTokenProvider jwtTokenProvider, UserDAO userDao, GameDAO gameDao) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDao = userDao;
        this.gameDao = gameDao;
    }

    @GetMapping("/getInfo")
    public ResponseEntity<CheckersGameDto> getInfo(@RequestHeader("Authorization") String token) {
        String username = jwtTokenProvider.getUsername(token);
        User user = userDao.findByUsername(username);

        GameEntity ge = gameDao.findByUserId(user.getId());
        CheckersGameDto cgd = ge.toCheckersGameDto(user.getId());
        if (Objects.equals(user.getId(), cgd.getPlayerFirst())) {
            cgd.setPlayerNum(1);
        } else {
            cgd.setPlayerNum(2);
        }

        return new ResponseEntity<>(cgd, HttpStatus.OK);
    }

    @GetMapping("/move")
    public ResponseEntity<CheckersGameDto> move(@RequestHeader("Authorization") String token, CheckerMovesDto dto) {
        String username = jwtTokenProvider.getUsername(token);
        User user = userDao.findByUsername(username);

        if (dto.getMoves().length < 2) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        }

        GameEntity ge = gameDao.findByUserId(user.getId());
        int playerNum;
        if (Objects.equals(user.getId(), ge.getPlayerFirst())) {
            playerNum = 1;
        } else {
            playerNum = 2;
        }

        int[] iNums = Stream.of(dto.getMoves())
                .mapToInt(x -> x.getIndexNumber())
                .toArray();
        int[] iChars = Stream.of(dto.getMoves())
                .map(x -> Character.toLowerCase(x.getIndexCharacter()))
                .mapToInt(x -> (x - 'a'))
                .toArray();
        // avoid IndexOutOfBound
        for (int i = 0; i < iNums.length; i++) {
            if (iNums[i] > 7 || iNums[i] < 0 || iChars[i] > 7 || iChars[i] < 0) {
                return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
            }
        }

        // check if selected checker is legal
        int[][] desk = ge.getGameInfo();
        int currentPos = desk[iNums[0]][iChars[0]];
        int nextPos = desk[iNums[1]][iChars[1]];
        int enemyMod = (playerNum + 1) % 2;
        if (currentPos == 0 || currentPos % 2 == enemyMod) {
            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
        } else {
            if (nextPos == 0) {
                // need to resolve if simple step or attack
                // if only 1 step
                if (dto.getMoves().length == 2) {
                    int diffNum, diffChar;
                    diffNum = iNums[1] - iNums[0];
                    diffChar = iChars[1] - iChars[0];

                    if (playerNum == 1) {
                        // simple
                        if (currentPos == 1) {
                            if ((diffNum == -1) && (diffChar == 1 || diffChar == -1)) {
                                desk[iNums[0]][iChars[0]] = 0;
                                desk[iNums[1]][iChars[1]] = currentPos;
                                ge.setGameInfo(desk);
                                ge = gameDao.saveGame(ge);
                            } else {
                                return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
                            }
                        } else if (currentPos == 3) {
                            if (Math.abs(diffChar) == Math.abs(diffNum)) {
                                boolean clear = true;
                                int numStep = diffNum / Math.abs(diffNum);
                                int charStep = diffChar / Math.abs(diffChar);
                                for (int i = 1; i < Math.abs(diffChar); i++) {
                                    if (desk[iNums[0]][iChars[0]] == 0) { // == ?

                                    }
                                }
                                desk[iNums[0]][iChars[0]] = 0;
                                desk[iNums[1]][iChars[1]] = currentPos;
                                ge.setGameInfo(desk);
                                ge = gameDao.saveGame(ge);
                            } else {
                                return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
                            }
                        }

                    } else {
                        if ((diffNum == 1) && (diffChar == 1 || diffChar == -1)) {
                            desk[iNums[0]][iChars[0]] = 0;
                            desk[iNums[1]][iChars[1]] = currentPos;
                            ge.setGameInfo(desk);
                            ge = gameDao.saveGame(ge);
                        } else {
                            return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
                        }
                    }

                } // else must be attack
                else {
                    for (int i = 1; i < iNums.length; i++) {
                        if (playerNum == 1) {
                            // upper left
                            //if (desk[][] % 2 == enemyMod) // upper right
                            // lower right
                            // lower left
                            {

                            }
                        } else {

                        }
                    }
                }
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
            }
        }

        CheckersGameDto cgd = ge.toCheckersGameDto(user.getId());
        cgd.setPlayerNum(playerNum);
        return new ResponseEntity<>(cgd, HttpStatus.OK);
    }

    /**
     * resolve if enemy in  <br>
     * 1) upper left square  <br>
     * 2) upper right <br>
     * 3) lower right <br>
     * 4) lower left <br>
     * ------- <br>
     * e * e <br>
     * * f * <br>
     * e * n <br>
     * ------- <br>
     * if there is sutiation like before the function return [true, true, false,
     * true]<br>
     * if index out of bound also return false in that place
     *
     * @param gameInfo  the checkers 8x8 desk
     * @param iNumeric  numeric part of standart index representation (for 'd2'
     *                  is (2-1))
     * @param iChar     char part of standart index representation (for 'd2' is
     *                  (4('d')-1))
     * @param playerNum player number in game to resolve enemy and friendly
     *                  checkers
     * @return the 4 element bool array
     * @see com.moklyak.Game.server.JDBCTemplates.GameJDBCTemplate for detailed desk representation
     */
    // draw matrix for checkers
    // upper left corenr is white!!!
    // checkers is on black
    // 0 - empty, 1 - first player checker, 2 - second player checker
    // inverted int indexes (8 = 1(0))
    // if will be 1 - f p adv, 4 - second player adv
    // int[][] desk = new int[][]{
    //    {0, 2, 0, 2, 0, 2, 0, 2}, 8   (0)
    //    {2, 0, 2, 0, 2, 0, 2, 0}, 7   (1)
    //    {0, 2, 0, 2, 0, 2, 0, 2}, 6   (2)
    //    {0, 0, 0, 0, 0, 0, 0, 0}, 5   (3)
    //    {0, 0, 0, 0, 0, 0, 0, 0}, 4   (4)
    //    {1, 0, 1, 0, 1, 0, 1, 0}, 3   (5) 
    //    {0, 1, 0, 1, 0, 1, 0, 1}, 2   (6)
    //    {1, 0, 1, 0, 1, 0, 1, 0}  1   (7)
    //     A  B  C  D  E  F  G  H
    //
    //     0  1  2  3  4  5  6  7 
    //};
    //toInit.setGameInfo(desk);
    private boolean[] isEnemyAround(int[][] gameInfo, int iNumeric, int iChar, int playerNum) {
        boolean[] res = new boolean[]{false, false, false, false};
        int[] sqs = new int[]{0, 0, 0, 0};
        // avoid IndexOutOfBoundException
        if (iNumeric > 0) {
            if (iChar > 0) {
                sqs[0] = gameInfo[iNumeric - 1][iChar - 1];
            }
            if (iChar < 7) {
                sqs[1] = gameInfo[iNumeric - 1][iChar + 1];
            }
        }
        if (iNumeric < 7) {
            if (iChar < 7) {
                sqs[2] = gameInfo[iNumeric + 1][iChar + 1];
            }
            if (iChar > 0) {
                sqs[3] = gameInfo[iNumeric + 1][iChar - 1];
            }

        }
        //
        // resolve checkers and filling res
        // to check i use mod cause 
        // first player checkers is 1 and 3 adv
        // second player checkers is 2 and 4 adv
        // 
        int enemyMod = (playerNum + 1) % 2;
        for (int i = 0; i < 4; i++) {
            if ((sqs[i]) != 0 && (sqs[i] % 2 == enemyMod)) {
                res[i] = true;
            }
        }

        return res;
    }
}
