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

    Long player1;
    Long player2;
    String gameType;
    boolean ready1;
    boolean ready2;
    
    public QueueResultDto toDto(){
        QueueResultDto res = new QueueResultDto();
        res.setPosition(-1);
        res.setReady1(ready1);
        res.setReady2(ready2);
        res.setGameType(gameType);
        res.setQueueState("waitingReady");
        return res;
    }
}
