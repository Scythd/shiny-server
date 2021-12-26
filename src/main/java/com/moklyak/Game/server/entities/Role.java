/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;

/**
 *
 * @author Пользователь
 */

@Data
@AllArgsConstructor
public class Role {

    @With
    private final Long id;


    private String name;

    public Role() {
        this.id = null;
    }
    
}
