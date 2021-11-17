/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.JDBCTemplates;

import com.moklyak.Game.server.DAOs.RoleDAO;
import com.moklyak.Game.server.DAOs.UserDAO;
import com.moklyak.Game.server.configurations.security.Status;
import com.moklyak.Game.server.entities.Role;
import com.moklyak.Game.server.entities.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Пользователь
 */
@Repository
public class UserJDBCTemplate implements UserDAO {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;
    private RoleDAO roleDAO;

    public UserJDBCTemplate(DataSource dataSource, JdbcTemplate jdbcTemplateObject, RoleDAO roleDAO) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = jdbcTemplateObject;
        this.roleDAO = roleDAO;
    }

    @Override
    public User findByUsername(String username) {
        String query = "select u.*, r.id as roles_id, r.name as roles "
                + "from users u "
                + "left join user_roles ur "
                + "on ur.user_id = u.id "
                + "left join roles r "
                + "on ur.role_id = r.id "
                + "where username = ? ;";
        User result = jdbcTemplateObject.query(query, ps -> ps.setString(1, username),
                new UserWithRolesExtractor());
        return result;
    }

    @Override
    public List<User> findAll() {
        String query = "select u.*, r.id as roles_id, r.name as roles "
                + "from users u "
                + "left join user_roles ur "
                + "on ur.user_id = u.id "
                + "left join roles r "
                + "on ur.role_id = r.id "
                + "order by id ;";
        List<User> result;
        result = jdbcTemplateObject.query(query, (rs) -> {
            List<User> users = new ArrayList<>();
            List<Role> currentUserRoles = new ArrayList<>();
            if (rs.next()) {
                User u = new User();
                u = u.withId(rs.getLong("id"));
                u.setEmail(rs.getString("email"));
                u.setNickname(rs.getString("nickname"));
                u.setPassword(rs.getString("password"));
                u.setStatus(Status.valueOf(rs.getString("status")));
                u.setUsername(rs.getString("username"));
                users.add(u);
                Role r = new Role(rs.getLong("roles_id"), rs.getString("roles"));
                currentUserRoles.add(r);
            }
            while (rs.next()) {
                if (!Objects.equals(rs.getLong("id"), users.get(users.size() - 1).getId())) {
                    users.get(users.size() - 1).setRoles(currentUserRoles);
                    currentUserRoles = new ArrayList<>();

                    User u = new User();
                    u = u.withId(rs.getLong("id"));
                    u.setEmail(rs.getString("email"));
                    u.setNickname(rs.getString("nickname"));
                    u.setPassword(rs.getString("password"));
                    u.setStatus(Status.valueOf(rs.getString("status")));
                    u.setUsername(rs.getString("username"));
                    users.add(u);
                }
                Role r = new Role(rs.getLong("roles_id"), rs.getString("roles"));
                currentUserRoles.add(r);
            }
            users.get(users.size() - 1).setRoles(currentUserRoles);
            return users;
        });
        return result;
    }

    @Override
    public User findById(Long id) {
        String query = "select u.*, r.id as roles_id, r.name as roles "
                + "from users u "
                + "left join user_roles ur "
                + "on ur.user_id = u.id "
                + "left join roles r "
                + "on ur.role_id = r.id "
                + "where u.id = ? ;";
        User result = jdbcTemplateObject.query(query, ps -> ps.setLong(1, id),
                new UserWithRolesExtractor());
        return result;
    }

    @Override
    public int deleteById(Long id) {
        String query = "delete from user_roles where user_id = ? ;"
                + "delte from user where id = ? ;";
        int result = jdbcTemplateObject.update(query,
                ps -> {
                    ps.setLong(1, id);
                    ps.setLong(2, id);
                });
        return result;
    }

    @Override
    public User save(User user) {
        List<Role> roles = new ArrayList<>(user.getRoles());
        Role temp;
        for (int i = 0; i < roles.size(); i++) {
            temp = roleDAO.findByName(roles.get(i).getName());
            if (temp == null) {
                temp = roleDAO.save(roles.get(i));
            }
            roles.set(i, temp);
        }

        user.setRoles(roles);

        StringBuilder query = new StringBuilder();

        query.append("insert into users ")
                .append("(username, password, email, nickname, status) ")
                .append(" values ")
                .append("(?, ?, ?, ?, ?);");
        PreparedStatementSetter pss = (ps) -> {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getNickname());
            ps.setString(5, user.getStatus().getStatus());
        };
        int qr;
        qr = jdbcTemplateObject.update(query.toString(), pss);
        if (qr == 0) {
            return null;
        }
        User res = findByUsername(user.getUsername());
        query = new StringBuilder();
        query.append("insert into user_roles (user_id, role_id) values ");
        for (Role r : roles) {
            query.append("(")
                    .append(res.getId())
                    .append(",")
                    .append(r.getId())
                    .append("),");
        }
        query.deleteCharAt(query.length() - 1);
        query.append(";");
        qr = jdbcTemplateObject.update(query.toString());
        return res;
    }
}

@Slf4j
class UserWithRolesExtractor implements ResultSetExtractor<User> {

    @Override
    public User extractData(ResultSet rs) throws SQLException, DataAccessException {
        User user = new User();
        List<Role> roles = new ArrayList<>();
        if (rs.next()) {
            user = user.withId(rs.getLong("id"));
            user.setEmail(rs.getString("email"));
            user.setNickname(rs.getString("nickname"));
            user.setPassword(rs.getString("password"));
            user.setStatus(Status.valueOf(rs.getString("status")));
            user.setUsername(rs.getString("username"));
            roles.add(new Role(rs.getLong("roles_id"), rs.getString("roles")));
        } else {
            return null;
        }
        while (rs.next()) {
            if (!Objects.equals(rs.getLong("id"), user.getId())) {
                log.info("IN UserWithRolesExtractor {} selected more then 1 user");
            } else {
                roles.add(new Role(rs.getLong("roles_id"), rs.getString("roles")));
            }
        }
        user.setRoles(roles);
        return user;
    }

}
