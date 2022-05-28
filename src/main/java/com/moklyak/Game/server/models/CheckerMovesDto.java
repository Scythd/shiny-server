/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.moklyak.Game.server.models;

import lombok.Data;

/**
 *
 * @author MSI
 */
@Data
public class CheckerMovesDto {

    CheckerMove[] moves;

    @Data
    static public class CheckerMove {
        int indexNumber;
        char indexCharacter;
    }
}
