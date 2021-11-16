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
public class ChessGameDto extends GameDto{
    int[][] desk;

    @Override
    GameEntity toGameEntity() {
        GameEntity res = super.toGameEntity();
        res.setGameInfo(desk);
        return res;
    }
}
