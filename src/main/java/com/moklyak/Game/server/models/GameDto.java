/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.models;

import com.moklyak.Game.server.entities.GameEntity;
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
    Date startDate;
    Date endDate;
    Long player1;
    Long player2;
    Integer turn;
    
    GameEntity toGameEntity(){
        GameEntity res = new GameEntity();
        res.setEndDate(endDate);
        res.setStartDate(startDate);
        res.setTurn(turn);
        res.setWinPlayer(winPlayer);
        res.setGameState(gameState);
        res.setId(id);
        res.setPlayer1(player1);
        res.setPlayer2(player2);
        return res;
    }
    
}
