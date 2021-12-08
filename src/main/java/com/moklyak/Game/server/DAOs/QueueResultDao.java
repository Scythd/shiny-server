/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.DAOs;

import com.moklyak.Game.server.entities.QueueResultEntity;

/**
 *
 * @author Пользователь
 */
public interface QueueResultDao {
    QueueResultEntity findByUserId(Long id);
    
    QueueResultEntity findByUsersIds(Long id1, Long id2);
    
    boolean setPlayerReady(Long userId);
    
    QueueResultEntity findInResolved(Long userId);
}
