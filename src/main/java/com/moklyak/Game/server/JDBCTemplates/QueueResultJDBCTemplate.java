/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.JDBCTemplates;

import com.moklyak.Game.server.entities.QueueResultEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import com.moklyak.Game.server.DAOs.QueueResultDao;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Пользователь
 */
@Repository
public class QueueResultJDBCTemplate implements QueueResultDao {

    private final JdbcTemplate jdbcTemplateObject;

    public QueueResultJDBCTemplate(JdbcTemplate jdbcTemplateObject) {
        this.jdbcTemplateObject = jdbcTemplateObject;
    }

    @Override
    public QueueResultEntity findByUserId(Long id) {
        String sql = "select * from queueresults where playerFirst = ? or playerSecond = ?";

        return jdbcTemplateObject.query(sql, (ps) -> {
            ps.setLong(1, id);
            ps.setLong(2, id);
        }, new QRRSExtractor());

    }

    @Override
    public QueueResultEntity findByUsersIds(Long id1, Long id2) {
        String sql = "select * from queueresults where "
                + " (playerFirst = ? and playerSecond = ?) or"
                + " (playerSecond = ? and playerFirst = ?)";

        return jdbcTemplateObject.query(sql, (ps) -> {
            ps.setLong(1, id1);
            ps.setLong(2, id2);
            ps.setLong(3, id1);
            ps.setLong(4, id2);
        }, new QRRSExtractor());

    }

    @Override
    public QueueResultEntity findInResolved(Long userId) {
        String sql = "select playerFirst"
                + ", playerSecond"
                + ", game_Type"
                + ", true as readyFirst"
                + ", true as readySecond "
                + " from games where "
                + " (playerFirst = ? or playerSecond = ?)";

        return jdbcTemplateObject.query(sql, (ps) -> {
            ps.setLong(1, userId);
            ps.setLong(2, userId);
        }, new QRRSExtractor());

    }

    @Override
    public boolean setPlayerReady(Long userId) {
        String sql = "update queueresults set readyFirst = true where playerFirst = ? ;";
        int sqlres = jdbcTemplateObject.update(sql, (ps) -> ps.setLong(1, userId));
        sql = "update queueresults set readySecond = true where playerSecond = ? ;";
        sqlres += jdbcTemplateObject.update(sql, (ps) -> ps.setLong(1, userId));
        if (sqlres > 1) {
            sql = "update queueresults set readyFirst = false, readySecond = false where playerFirst = ? or playerSecond = ? ;";
            jdbcTemplateObject.update(sql, (ps) -> {
                ps.setLong(1, userId);
                ps.setLong(1, userId);
            });
            throw new RuntimeException("Database in wrong state!!!");
        }
        sql = "select resolvePendedQueueResults(?);";
        jdbcTemplateObject.query(sql, ps -> ps.setLong(1, userId), rs -> rs.next() ? rs.getInt(1) : -1);
        return sqlres == 1;
    }

}

class QRRSExtractor implements ResultSetExtractor<QueueResultEntity> {

    @Override
    public QueueResultEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
            QueueResultEntity qre = new QueueResultEntity();
            qre.setPlayerFirst(rs.getLong("playerFirst"));
            qre.setPlayerSecond(rs.getLong("playerSecond"));
            qre.setGameType(rs.getString("game_type"));
            qre.setReadyFirst(rs.getBoolean("readyFirst"));
            qre.setReadySecond(rs.getBoolean("readySecond"));
            return qre;
        } else {
            return null;
        }
    }

}


