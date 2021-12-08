/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.entities;

import com.moklyak.Game.server.models.BullCowGameDto;
import com.moklyak.Game.server.models.CheckersGameDto;
import com.moklyak.Game.server.models.ChessGameDto;
import com.moklyak.Game.server.models.GameDto;
import com.moklyak.Game.server.models.GameState;
import com.moklyak.Game.server.models.QueueResultDto;
import com.moklyak.Game.server.models.WinSide;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

/**
 *
 * @author Пользователь
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameEntity {
    
    @With
    Long id;
    GameState gameState;
    WinSide winPlayer;
    Date startDate;
    Date endDate;
    Long playerFirst;
    Long playerSecond;
    Integer turn;
    int[][] gameInfo;
    String gameType;
    
    /**
     * Warning: this function use side effect!!!
    */
    private GameDto toCommonDto(GameDto toSet){
        toSet.setTurn(turn);
        toSet.setEndDate(endDate);
        toSet.setStartDate(startDate);
        toSet.setGameState(gameState);
        toSet.setWinPlayer(winPlayer);
        toSet.setId(id);
        toSet.setPlayerFirst(playerFirst);
        toSet.setPlayerSecond(playerSecond);
        return toSet;
    }
    
    public BullCowGameDto tuBullCowGameDto(){
        BullCowGameDto res = new BullCowGameDto();
        res = (BullCowGameDto)toCommonDto(res);
        res.setYourAskedNumber(gameInfo[0][0]);
        res.setYourAnswerHistory(gameInfo[1]);
        res.setYourBullsHistory(gameInfo[2]);
        res.setYourCowsHistory(gameInfo[3]);
        res.setOpponentAnswerHistory(gameInfo[4]);
        res.setOpponentBullsHistory(gameInfo[5]);
        res.setOpponentCowsHistory(gameInfo[6]);
        return res;
    }
    
    public CheckersGameDto toCheckersGameDto(){
        CheckersGameDto res = new CheckersGameDto();
        res = (CheckersGameDto)toCommonDto(res);
        res.setDesk(gameInfo);
        return res;
    }
    
    public ChessGameDto toChessGameDto(){
        ChessGameDto res = new ChessGameDto();
        res = (ChessGameDto)toCommonDto(res);
        res.setDesk(gameInfo);
        return res;
    }
    
    public QueueResultDto toQueueResultDto(Long userId){
        QueueResultDto qrd = new QueueResultDto();
        qrd.setGameType(gameType);
        qrd.setPosition(-1);
        qrd.setReadyFirst(true);
        qrd.setReadySecond(true);
        if (userId != null){
            if (userId == playerFirst){
                qrd.setPlayerNum(1);
            } else {
                qrd.setPlayerNum(2);
            }
        }
        qrd.setQueueState("resolved");
        return qrd;
    }
}
