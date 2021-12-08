/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.entities;

import com.moklyak.Game.server.models.QueueResultDto;
import lombok.*;

/**
 *
 * @author Пользователь
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueEntity {
    

    Long userID;
    Integer position;
    String gameType;
    
    public QueueResultDto toDto(){
        QueueResultDto res = new QueueResultDto();
        res.setPosition(position);
        res.setReadyFirst(false);
        res.setReadySecond(false);
        res.setGameType(gameType);
        res.setQueueState("queueing");
        return res;
    }
}
