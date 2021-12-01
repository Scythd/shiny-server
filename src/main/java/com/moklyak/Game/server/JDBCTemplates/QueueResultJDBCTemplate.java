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

/**
 *
 * @author Пользователь
 */
public class QueueResultJDBCTemplate implements QueueResultDao {

    private final JdbcTemplate jdbcTemplateObject;

    public QueueResultJDBCTemplate(JdbcTemplate jdbcTemplateObject) {
        this.jdbcTemplateObject = jdbcTemplateObject;
    }

    @Override
    public QueueResultEntity findByUserId(Long id) {
        String sql = "select * from queueresults where player1 = ? or player2 = ?";

        return jdbcTemplateObject.query(sql, (ps) -> {
            ps.setLong(1, id);
            ps.setLong(2, id);
        }, new QRRSExtractor());

    }

    @Override
    public QueueResultEntity findByUsersIds(Long id1, Long id2) {
        String sql = "select * from queueresults where "
                + " (player1 = ? and player2 = ?) or"
                + " (player2 = ? and player1 = ?)";

        return jdbcTemplateObject.query(sql, (ps) -> {
            ps.setLong(1, id1);
            ps.setLong(2, id2);
            ps.setLong(3, id1);
            ps.setLong(4, id2);
        }, new QRRSExtractor());

    }

    @Override
    public boolean setPlayerReady(Long userId) {
        String sql = "update queueresult set ready1 = true where player1 = ? ;";
        int sqlres = jdbcTemplateObject.update(sql, (ps) -> ps.setLong(1, userId));
        sql = "update queueresult set ready2 = true where player2 = ? ;";
        sqlres += jdbcTemplateObject.update(sql, (ps) -> ps.setLong(1, userId));
        if (sqlres > 1) {
            sql = "update queueresult set ready1 = false, ready2 = false where player1 = ? or player2 = ? ;";
            jdbcTemplateObject.update(sql, (ps) -> {
                ps.setLong(1, userId);
                ps.setLong(1, userId);
            });
            throw new RuntimeException("Database in wrong state!!!");
        }
        sql = "select resolvePendedQueueResults(?);";
        jdbcTemplateObject.update(sql, ps->ps.setLong(1, userId));
        return sqlres == 1;
    }


}

class QRRSExtractor implements ResultSetExtractor<QueueResultEntity> {

    @Override
    public QueueResultEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
            QueueResultEntity qre = new QueueResultEntity();
            qre.setPlayer1(rs.getLong("player1"));
            qre.setPlayer2(rs.getLong("player2"));
            qre.setGameType(rs.getString("game_type"));
            qre.setReady1(rs.getBoolean("ready1"));
            qre.setReady2(rs.getBoolean("ready2"));
            return qre;
        } else {
            return null;
        }
    }

}
