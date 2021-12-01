/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.DAOs;

import com.moklyak.Game.server.entities.QueueEntity;
import java.util.List;

/**
 *
 * @author Пользователь
 */
public interface QueueDao {
    QueueEntity save(QueueEntity qe);
    
    QueueEntity findByUserId(Long userId);
    
    List<QueueEntity> findAll();
}
