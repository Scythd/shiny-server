/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.services;

import com.moklyak.Game.server.entities.Role;
import com.moklyak.Game.server.entities.User;
import com.moklyak.Game.server.configurations.security.Status;
import com.moklyak.Game.server.repositories.RoleRepository;
import com.moklyak.Game.server.repositories.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



/**
 *
 * @author Пользователь
 */
@Service
@Slf4j
public class UserService{
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User register(User user){
        Role roleUser = roleRepository.findByName("ROLE_USER");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);
        user.setStatus(Status.ACTIVE);
        
        User registeredUser = userRepository.save(user);
        log.info("IN register - user; {} succesfully registered", registeredUser);
        
        return registeredUser;
    }
    
    public List<User> getAll(){
        List<User> result = userRepository.findAll();
        log.info("IN getALL - {} users found", result.size());
        return result;
    }
    
    public User findByUsername(String username){
        User result = userRepository.findByUsername(username);
        log.info("IN findByUsername - user {} found by username: {} ", result, username);
        return result;
    }
    
    public User findById(Long id){
        User result = userRepository.getById(id);
        
        log.info("IN findById - user: {} found by id: {}", result, id);
        return result;
    }
    
    public void Delete(Long id){
        userRepository.deleteById(id);
        log.info("IN delete - user with id: {} succesfully deleted user by id: {}", id);
    }
}
