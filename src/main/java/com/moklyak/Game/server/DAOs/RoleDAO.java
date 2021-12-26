/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.DAOs;

import com.moklyak.Game.server.entities.Role;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Пользователь
 */
@Repository
public interface RoleDAO{

//    @Query("""
//           select id, name 
//           from roles 
//           where name = :name
//           """)
    public Role findByName(String name);

//    @Query("""
//           select r.id r.name 
//           from roles r 
//           join user_roles ur on ur.role_id 
//           join users u on u.id = ur.user_id
//           where ur.user_id = :username""")
    public List<Role> findByUserName(String username);

//    @Query("""
//           select r.id r.name 
//           from roles r 
//           join user_roles ur on ur.role_id 
//           where ur.user_id = :uid
//           """)
    public List<Role> findByUserId(Long uid);
    
    public Role save(Role role);
    
}
