/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.configurations.security;

/**
 *
 * @author Пользователь
 */
public enum Status {
    ACTIVE("ACTIVE"), INACTIVE("INACTIVE"), DELETED("DELETED"), BANNED("BANNED");

    private final String status;
    
    private Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
    
    
    
}
