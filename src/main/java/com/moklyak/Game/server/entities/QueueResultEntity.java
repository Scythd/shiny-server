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
public class QueueResultEntity {

    Long playerFirst;
    Long playerSecond;
    String gameType;
    boolean readyFirst;
    boolean readySecond;
    
    public QueueResultDto toDto(){
        QueueResultDto res = new QueueResultDto();
        res.setPosition(-1);
        res.setReadyFirst(readyFirst);
        res.setReadySecond(readySecond);
        res.setGameType(gameType);
        res.setQueueState("waitingReady");
        return res;
    }
}
