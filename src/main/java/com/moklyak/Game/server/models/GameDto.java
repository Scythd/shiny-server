/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.models;

import com.moklyak.Game.server.entities.GameEntity;
import java.sql.Timestamp;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Пользователь
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class GameDto {
    GameState gameState;
    Long id;
    WinSide winPlayer;
    Timestamp startDate;
    Timestamp endDate;
    Long playerFirst;
    Long playerSecond;
    Integer turn;
    
    GameEntity toGameEntity(){
        GameEntity res = new GameEntity();
        res.setEndDate(endDate);
        res.setStartDate(startDate);
        res.setTurn(turn);
        res.setWinPlayer(winPlayer);
        res.setGameState(gameState);
        res.setId(id);
        res.setPlayerFirst(playerFirst);
        res.setPlayerSecond(playerSecond);
        return res;
    }
    
}
