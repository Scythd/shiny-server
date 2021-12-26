/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.DAOs;

import com.moklyak.Game.server.entities.GameEntity;
import java.util.List;

/**
 *
 * @author Пользователь
 */
public interface GameDAO {
    
    public GameEntity findById(Long id);
    
    public GameEntity findByUserId(Long userID);
    
    public GameEntity saveGame(GameEntity game);
    
    public List<GameEntity> findAll();
}
