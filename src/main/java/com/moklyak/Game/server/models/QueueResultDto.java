/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.models;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Пользователь
 */

@Data
@NoArgsConstructor
public class QueueResultDto {  
    Integer playerNum;
    Integer position;
    String gameType;
    boolean ready1;
    boolean ready2;
    String queueState;
}
