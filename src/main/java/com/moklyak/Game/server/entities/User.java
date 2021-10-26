/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.entities;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author Пользователь
 */

@Entity(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String login;
    private String password;
    private String firstname;
    private String lastname;
    private Integer age;
}
