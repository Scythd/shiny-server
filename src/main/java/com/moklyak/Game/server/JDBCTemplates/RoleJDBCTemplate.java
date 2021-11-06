/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.JDBCTemplates;

import com.moklyak.Game.server.DAOs.RoleDAO;
import com.moklyak.Game.server.entities.Role;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Пользователь
 */
@Repository
public class RoleJDBCTemplate implements RoleDAO {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObject;

    public RoleJDBCTemplate(DataSource dataSource, JdbcTemplate jdbcTemplateObject) {
        this.dataSource = dataSource;
        this.jdbcTemplateObject = jdbcTemplateObject;
    }

    @Override
    public Role findByName(String name) {
        String query = "select id, name "
                + "from roles "
                + "where name = ? ;";

        Role result = jdbcTemplateObject.query(query,
                ps -> ps.setString(1, name),
                rs -> {
                    if (rs.next()) {
                        Role res = new Role();
                        res = res.withId(rs.getLong("id"));
                        res.setName(rs.getString("name"));
                        return res;
                    } else {
                        return null;
                    }
                });

        return result;
    }

    @Override
    public List<Role> findByUserName(String username) {
        String query = "select r.id as id, r.name as name "
                + "from roles "
                + "join user_roles ur "
                + "on ur.role_id = r.id "
                + "join users u "
                + "on u.id = ur.user_id "
                + "where u.name = ? ;";
        List<Role> res;
        res = jdbcTemplateObject.query(query,
                ps -> ps.setString(0, username), new RoleMapper());
        return res;
    }

    @Override
    public List<Role> findByUserId(Long uid) {
        String query = "select r.id as id, r.name as name "
                + "from roles "
                + "join user_roles ur "
                + "on ur.role_id = r.id "
                + "where ur.user_id = ? ;";
        List<Role> res;
        res = jdbcTemplateObject.query(query, ps -> ps.setLong(1, uid), new RoleMapper());
        return res;
    }

    @Override
    public Role save(Role role) {
        String query = "insert into roles (name) value (?); ";
        int qr = jdbcTemplateObject.update(query,
                ps -> ps.setString(1, role.getName()));
        if (qr == 0) {
            return null;
        }
        query = "select id from roles where name = ? ;";
        Role res = jdbcTemplateObject.query(query,
                ps -> ps.setString(1, role.getName()),
                rs -> {
                    if (rs.next()) {
                        return role.withId(rs.getLong("id"));
                    } else {
                        return null;
                    }
                });
        return res;
    }

}

class RoleMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        Role role = new Role();
        role = role.withId(rs.getLong("id"));
        role.setName(rs.getString("name"));
        return role;
    }

}
