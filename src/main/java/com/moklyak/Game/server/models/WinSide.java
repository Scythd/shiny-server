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

    public static WinSide getBySide(String side) {
        switch (side) {
            case "first" -> {
                return WinSide.FIRST_PLAYER;
            }
            case "second" -> {
                return WinSide.SECOND_PLAYER;
            }
            case "draw" -> {
                return WinSide.DRAW;
            }
            default -> {
                return WinSide.NA;
            }
        }
    }

    public int getSide() {
        return side;
    }

}
