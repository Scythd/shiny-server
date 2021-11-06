/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.DAOs;

import com.moklyak.Game.server.configurations.security.Status;
import com.moklyak.Game.server.entities.Role;
import com.moklyak.Game.server.entities.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 *
 * @author Пользователь
 */
@org.springframework.stereotype.Repository
public interface UserDAO {

//    @Query(value = "select u.*, r.id as roles_id, r.name as roles"
//            + "from users u "
//            + "left join user_roles ur "
//            + "on ur.user_id = u.id "
//            + "left join roles r "
//            + "on ur.role_id r.id "
//            + "where username = :username;",
//            resultSetExtractorClass = UserWithRolesExtractor.class)
    public User findByUsername(String username);

//    @Query(value = "select u.*, r.id as roles_id, r.name as roles"
//            + "from users u "
//            + "left join user_roles ur "
//            + "on ur.user_id = u.id "
//            + "left join roles r "
//            + "on ur.role_id r.id;",
//            resultSetExtractorClass = UserWithRolesExtractor.class)
    public List<User> findAll();

//    @Query(value = "select u.*, r.id as roles_id, r.name as roles"
//            + "from users u "
//            + "left join user_roles ur "
//            + "on ur.user_id = u.id "
//            + "left join roles r "
//            + "on ur.role_id r.id "
//            + "where u.id = :id",
//            resultSetExtractorClass = UserWithRolesExtractor.class)
    public User findById(Long id);

//    @Query(value = "selete from users where id = :id")
    public int deleteById(Long id);

    public User save(User user);
}


