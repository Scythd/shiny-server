/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.models;

/**
 *
 * @author Пользователь
 */
public enum WinSide {
    FIRST_PLAYER(1), SECOND_PLAYER(2), DRAW(0), NA(-1);

    int side;

    private WinSide(int side) {
        this.side = side;
    }

    private WinSide(String side) {
        switch (side) {
            case "first" ->
                this.side = 1;
            case "second" ->
                this.side = 2;
            case "draw" ->
                this.side = 0;
            default ->
                this.side = -1;
        }
    }

    public int getSide() {
        return side;
    }

    
    
}
