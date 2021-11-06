/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.moklyak.Game.server.entities.Role;
import com.moklyak.Game.server.entities.User;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Пользователь
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String token;
    
    public User toUser(){
        User user = new User();
        user.setEmail(email);
        user.setNickname(nickname);
        
        return user.withId(id);
    }
    
    public static UserDto fromUser(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setId(user.getId());
        userDto.setNickname(user.getNickname());
        userDto.setUsername(user.getUsername());
        
        return userDto;
    }
}
