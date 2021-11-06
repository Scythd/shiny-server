/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moklyak.Game.server.configurations.security.Status;
import java.util.ArrayList;
import lombok.Data;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.With;

/**
 *
 * @author Пользователь
 */
//@Entity(name = "users")
@Data
@AllArgsConstructor
public class User {

    @With
    private final Long id;

    private String username;

    private String email;

    private String password;

    private String nickname;

    @JsonManagedReference

    private List<Role> roles = new ArrayList<>();

    private Status status;

    public User() {
        this.id = null;
    }

}
