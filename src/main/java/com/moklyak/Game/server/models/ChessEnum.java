/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package com.moklyak.Game.server.models;

import java.util.Objects;

/**
 *
 * @author MSI
 */
public enum ChessEnum {
    EMPTY(0),
    W_KING(11), W_QUEEN(12), W_ROOK(13), W_BISHOP(14), W_KNIGHT(15), W_PAWN(16),
    B_KING(21), B_QUEEN(22), B_ROOK(23), B_BISHOP(24), B_KNIGHT(25), B_PAWN(26);

    int num;

    private ChessEnum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    @Override
    public String toString() {
        return Objects.toString(num);
    }

}
