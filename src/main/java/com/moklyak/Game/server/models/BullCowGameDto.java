/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.models;

import com.moklyak.Game.server.entities.GameEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Пользователь
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BullCowGameDto extends GameDto{
    int yourAskedNumber;
    int[] yourAnswerHistory;
    int[] yourBullsHistory;
    int[] yourCowsHistory;
    int[] opponentAnswerHistory;
    int[] opponentBullsHistory;
    int[] opponentCowsHistory;

    @Override
    GameEntity toGameEntity() {
        GameEntity res = super.toGameEntity();
        int[][] info = {
            {yourAskedNumber},
            yourAnswerHistory,
            yourBullsHistory,
            yourCowsHistory,
            opponentAnswerHistory,
            opponentBullsHistory,
            opponentCowsHistory
        };
        res.setGameInfo(info);
        return res;
    }
}
