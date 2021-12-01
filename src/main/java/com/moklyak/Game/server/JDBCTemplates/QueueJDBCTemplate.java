/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.JDBCTemplates;

import com.moklyak.Game.server.DAOs.QueueDao;
import com.moklyak.Game.server.entities.GameEntity;
import com.moklyak.Game.server.entities.QueueEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author Пользователь
 */
public class QueueJDBCTemplate implements QueueDao {

    private final JdbcTemplate jdbcTemplateObject;

    public QueueJDBCTemplate(JdbcTemplate jdbcTemplateObject) {
        this.jdbcTemplateObject = jdbcTemplateObject;
    }

    @Override
    public QueueEntity save(QueueEntity qe) {
        QueueEntity inBase;
        inBase = findByUserId(qe.getUserID());
        if (inBase == null){
            String sql = "insert into queue(user_id, game_type) values( ? , ? )";
            jdbcTemplateObject.update(sql, (ps) -> {
                ps.setLong(1, qe.getUserID());
                ps.setString(2, qe.getGameType());
            });
        } else {
//            String sql = "update queue set user_id = ? , game_type = ? where user_id = ?";
//            jdbcTemplateObject.update(sql, (ps) -> {
//                ps.setLong(1, qe.getUserID());
//                ps.setString(2, qe.getGameType());
//                ps.setLong(3, qe.getUserID());
//            });
            throw new RuntimeException(qe.getUserID() + " user has alredy entered " + qe.getGameType() + " game queued");
        }
        inBase = findByUserId(qe.getUserID());
        return inBase;
    }

    @Override
    public QueueEntity findByUserId(Long userId) {
        String sql = "select user_id as uid, "
                + " game_type as gt, "
                + " row_number() over (date_created) as pos "
                + " from queue"
                + " where user_id = ?"
                + " order by date_created asc ";
        return jdbcTemplateObject.query(sql, ps -> ps.setLong(1, userId), new QueueResultExtractor());
    }

    @Override
    public List<QueueEntity> findAll() {
       String sql = "select user_id as uid, "
                + " game_type as gt, "
                + " row_number() over (date_created) as pos "
                + " from queue"
               + " order by date_created asc ";
        return jdbcTemplateObject.query(sql, new QueueRowMapper());
    }

}

class QueueRowMapper implements RowMapper<QueueEntity> {

    @Override
    public QueueEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        QueueEntity res = new QueueEntity();
        res.setUserID(rs.getLong("uid"));
        res.setPosition(rs.getInt("pos"));
        res.setGameType(rs.getString("gt"));
        return res;
    }

}

class QueueResultExtractor implements ResultSetExtractor<QueueEntity> {

    @Override
    public QueueEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        QueueEntity res = new QueueEntity();
        if (rs.next()) {
            res.setUserID(rs.getLong("uid"));
            res.setPosition(rs.getInt("pos"));
            res.setGameType(rs.getString("gt"));
            return res;
        } else {
            return null;
        }        
    }
}
