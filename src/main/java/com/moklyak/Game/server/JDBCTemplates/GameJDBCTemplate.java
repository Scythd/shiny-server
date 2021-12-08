/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.JDBCTemplates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.moklyak.Game.server.DAOs.GameDAO;
import static com.moklyak.Game.server.JDBCTemplates.GameRSExtractor.objectMapper;
import com.moklyak.Game.server.entities.GameEntity;
import com.moklyak.Game.server.models.GameState;
import com.moklyak.Game.server.models.WinSide;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.springframework.dao.DataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Пользователь
 */
@Repository
public class GameJDBCTemplate implements GameDAO {

    private static ObjectMapper objectMapper1 = new ObjectMapper();

    private final JdbcTemplate jdbcTemplateObject;

    public GameJDBCTemplate(JdbcTemplate jdbcTemplateObject) {
        this.jdbcTemplateObject = jdbcTemplateObject;
    }

    @Override
    public GameEntity findById(Long id) {
        String query = "select * from Games where id = ? ;";
        GameEntity res = jdbcTemplateObject.query(query, ((ps) -> {
            ps.setLong(1, id);
        }), new GameRSExtractor());
        return res;
    }

    @Override
    public GameEntity findByUserId(Long userID) {
        String query = "select * from Games where playerFirst = ? or playerSecond = ? ;";
        GameEntity res = jdbcTemplateObject.query(query, ((ps) -> {
            ps.setLong(1, userID);
            ps.setLong(2, userID);
        }), new GameRSExtractor());
        return res;
    }

    @Override
    public GameEntity saveGame(GameEntity game) {
        GameEntity inBase = null;

        if (game.getId() == null) {
            String query = "insert into Games(playerFirst, "
                    + "playerSecond, "
                    + "win_player, "
                    + "state, "
                    + "turn, "
                    + "startdatetime, "
                    + "enddatetime, "
                    + "game_info, "
                    + "game_type) "
                    + "values (?,?,?,?,?,?,?,?,?)";
            jdbcTemplateObject.update(query, (ps) -> {
                ps.setLong(1, game.getPlayerFirst());
                ps.setLong(2, game.getPlayerSecond());
                ps.setLong(3, game.getPlayerFirst());
                ps.setLong(4, game.getWinPlayer().getSide());
                ps.setString(5, game.getGameState().getState());
                ps.setTimestamp(6, new Timestamp(game.getStartDate().toInstant().toEpochMilli()));
                ps.setTimestamp(7, new Timestamp(game.getEndDate().toInstant().toEpochMilli()));
                ps.setString(9, game.getGameType());
                try {
                    ps.setString(8, objectMapper1.writeValueAsString(game.getGameInfo()));
                } catch (Exception ex) {
                    throw new RuntimeException(ex.getMessage(), ex.getCause());
                }
            });
        } else {
            String query = "update Games "
                    + "set playerFirst = ? , "
                    + "playerSecond = ? , "
                    + "win_player = ? , "
                    + "state = ? , "
                    + "turn = ? , "
                    + "startdatetime = ? , "
                    + "enddatetime = ? , "
                    + "game_info = ? "
                    + "where id = ?";
            jdbcTemplateObject.update(query, (ps) -> {
                ps.setLong(1, game.getPlayerFirst());
                ps.setLong(2, game.getPlayerSecond());
                ps.setLong(3, game.getPlayerFirst());
                ps.setLong(4, game.getWinPlayer().getSide());
                ps.setString(5, game.getGameState().getState());
                ps.setTimestamp(6, new Timestamp(game.getStartDate().toInstant().toEpochMilli()));
                ps.setTimestamp(7, new Timestamp(game.getEndDate().toInstant().toEpochMilli()));
                try {
                    ps.setString(8, objectMapper1.writeValueAsString(game.getGameInfo()));
                } catch (Exception ex) {
                    throw new RuntimeException(ex.getMessage(), ex.getCause());
                }
                ps.setLong(9, game.getId());
            });
        }
        inBase = findByUserId(game.getPlayerFirst());
        if (inBase == null) {
            inBase = findByUserId(game.getPlayerSecond());
        }
        if (inBase == null) {
            //error
        }
        return inBase;
    }

    @Override
    public List<GameEntity> findAll() {
        String query = "select * from Games;";
        List<GameEntity> res = jdbcTemplateObject.query(query, new GameRowMapper());
        return res;
    }

}

class GameRSExtractor implements ResultSetExtractor<GameEntity> {

    static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public GameEntity extractData(ResultSet rs) throws SQLException, DataAccessException {

        GameEntity r = new GameEntity();
        if (rs.next()) {
            try {
                r.setGameInfo(objectMapper.readValue(rs.getString("game_info"), int[][].class));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex.getMessage(), ex.getCause());
            } catch (SQLException ex) {
                throw ex;
            }
            r.setEndDate(Date.from(Instant.ofEpochSecond(rs.getLong("endDateTime"))));
            r.setStartDate(Date.from(Instant.ofEpochSecond(rs.getLong("startDateTime"))));
            r.setGameState(GameState.valueOf(rs.getString("state")));
            r.setId(rs.getLong("id"));
            r.setPlayerFirst(rs.getLong("playerFirst"));
            r.setPlayerSecond(rs.getLong("playerSecond"));
            r.setTurn(rs.getInt("turn"));
            r.setWinPlayer(WinSide.valueOf(rs.getString("win_player")));
            r.setGameType(rs.getString("game_type"));
        } else {
            return null;
        }

        return r;
    }

}

class GameRowMapper implements RowMapper<GameEntity> {

    static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public GameEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        GameEntity r = new GameEntity();

        try {
            r.setGameInfo(objectMapper.readValue(rs.getString("bull_cow"), int[][].class));
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex.getMessage(), ex.getCause());
        } catch (SQLException ex) {
            throw ex;
        }
        r.setEndDate(Date.from(Instant.ofEpochSecond(rs.getLong("endDateTime"))));
        r.setStartDate(Date.from(Instant.ofEpochSecond(rs.getLong("startDateTime"))));
        r.setGameState(GameState.valueOf(rs.getString("state")));
        r.setId(rs.getLong("id"));
        r.setPlayerFirst(rs.getLong("playerFirst"));
        r.setPlayerSecond(rs.getLong("playerSecond"));
        r.setTurn(rs.getInt("turn"));
        r.setWinPlayer(WinSide.valueOf(rs.getString("win_player")));
        r.setGameType(rs.getString("game_type"));

        return r;
    }

}
