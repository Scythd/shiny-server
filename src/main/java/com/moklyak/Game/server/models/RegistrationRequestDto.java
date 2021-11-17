/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.models;

import lombok.Data;

/**
 *
 * @author Пользователь
 */
@Data
public class RegistrationRequestDto {
    String username;
    String password;
    String nickname;
    String email;
}
