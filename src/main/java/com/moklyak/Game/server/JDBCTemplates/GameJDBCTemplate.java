/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.JDBCTemplates;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moklyak.Game.server.DAOs.GameDAO;
import com.moklyak.Game.server.entities.GameEntity;
import com.moklyak.Game.server.models.ChessEnum;
import com.moklyak.Game.server.models.GameState;
import com.moklyak.Game.server.models.WinSide;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.springframework.dao.DataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
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
        String query = "select * from Games where (not state = 'ended') and (playerFirst = ? or playerSecond = ? );";
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
                ps.setLong(3, game.getWinPlayer().getSide());
                ps.setString(4, game.getGameState().getState());
                ps.setInt(5, game.getTurn());
                ps.setTimestamp(6, game.getStartDate());
                if (game.getEndDate() == null) {
                    ps.setTimestamp(7, null);
                } else {
                    ps.setTimestamp(7, game.getEndDate());
                }
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
                ps.setLong(3, game.getWinPlayer().getSide());
                ps.setString(4, game.getGameState().getState());
                ps.setInt(5, game.getTurn());
                ps.setTimestamp(6, game.getStartDate());
                if (game.getEndDate() == null) {
                    ps.setTimestamp(7, null);
                } else {
                    ps.setTimestamp(7, game.getEndDate());
                }
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

    private GameEntity initialize(GameEntity toInit) {
        if (toInit.getGameInfo() != null) {
            return toInit;
        }
        switch (toInit.getGameType()) {
            case ("Chess") -> {
                // draw matrix for chess with enum numbers
                // linnes when columns
                // upper left corenr is white!!!
                // inverted int indexes (8 = 1(0))
                int[][] desk = new int[8][];
                desk[0] = new int[]{
                    ChessEnum.B_ROOK.getNum(),
                    ChessEnum.B_KING.getNum(),
                    ChessEnum.B_BISHOP.getNum(),
                    ChessEnum.B_QUEEN.getNum(),
                    ChessEnum.B_KING.getNum(),
                    ChessEnum.B_BISHOP.getNum(),
                    ChessEnum.B_KING.getNum(),
                    ChessEnum.B_ROOK.getNum()};
                desk[7] = new int[]{
                    ChessEnum.W_ROOK.getNum(),
                    ChessEnum.W_KING.getNum(),
                    ChessEnum.W_BISHOP.getNum(),
                    ChessEnum.W_QUEEN.getNum(),
                    ChessEnum.W_KING.getNum(),
                    ChessEnum.W_BISHOP.getNum(),
                    ChessEnum.W_KING.getNum(),
                    ChessEnum.W_ROOK.getNum()};
                desk[1] = new int[]{
                    ChessEnum.B_PAWN.getNum(),
                    ChessEnum.B_PAWN.getNum(),
                    ChessEnum.B_PAWN.getNum(),
                    ChessEnum.B_PAWN.getNum(),
                    ChessEnum.B_PAWN.getNum(),
                    ChessEnum.B_PAWN.getNum(),
                    ChessEnum.B_PAWN.getNum(),
                    ChessEnum.B_PAWN.getNum()};
                desk[6] = new int[]{
                    ChessEnum.W_PAWN.getNum(),
                    ChessEnum.W_PAWN.getNum(),
                    ChessEnum.W_PAWN.getNum(),
                    ChessEnum.W_PAWN.getNum(),
                    ChessEnum.W_PAWN.getNum(),
                    ChessEnum.W_PAWN.getNum(),
                    ChessEnum.W_PAWN.getNum(),
                    ChessEnum.W_PAWN.getNum()};

                int[] emptyline = new int[]{
                    ChessEnum.EMPTY.getNum(),
                    ChessEnum.EMPTY.getNum(),
                    ChessEnum.EMPTY.getNum(),
                    ChessEnum.EMPTY.getNum(),
                    ChessEnum.EMPTY.getNum(),
                    ChessEnum.EMPTY.getNum(),
                    ChessEnum.EMPTY.getNum(),
                    ChessEnum.EMPTY.getNum()};
                desk[2] = emptyline;
                desk[3] = emptyline;
                desk[4] = emptyline;
                desk[5] = emptyline;

                toInit.setGameInfo(desk);
            }

            case ("Checkers") -> {
                // draw matrix for checkers
                // upper left corenr is white!!!
                // checkers is on black
                // 0 - empty, 1 - first player checker, 2 - second player checker
                // inverted int indexes (8 = 1(0))
                // if will be 1 - f p adv, 4 - second player adv
                int[][] desk = new int[][]{
                    {0, 2, 0, 2, 0, 2, 0, 2},
                    {2, 0, 2, 0, 2, 0, 2, 0},
                    {0, 2, 0, 2, 0, 2, 0, 2},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 0, 1, 0, 1, 0, 1, 0},
                    {0, 1, 0, 1, 0, 1, 0, 1},
                    {1, 0, 1, 0, 1, 0, 1, 0}
                };
                toInit.setGameInfo(desk);
            }
            case ("BullCow") -> {
                // init arrs?
                //
                // first arr ans 1 2
                // guess hist 1
                // bulls hist 1
                // cows hist 1
                // guess hist 2
                // bulls hist 2
                // cows hist 2
                
                int[][] info = new int[7][0];
                info[0] = new int[]{-1, -1};
                toInit.setGameInfo(info);
            }
            default ->
                throw new RuntimeException(
                        "illegal game type on init phase");
        }
        return saveGame(toInit);

    }

    class GameRSExtractor implements ResultSetExtractor<GameEntity> {

        static ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public GameEntity extractData(ResultSet rs) throws SQLException, DataAccessException {

            GameEntity r = new GameEntity();
            if (rs.next()) {
                try {
                    r.setGameInfo(objectMapper.readValue(rs.getString("game_info"), int[][].class));
                } catch (Exception ex) {
                    r.setGameInfo(null);
                }
                try {
                    r.setEndDate(rs.getTimestamp("endDateTime"));
                } catch (NullPointerException ex) {
                }
                r.setStartDate(rs.getTimestamp("startDateTime"));
                r.setGameState(GameState.getByState(rs.getString("state")));
                r.setId(rs.getLong("id"));
                r.setPlayerFirst(rs.getLong("playerFirst"));
                r.setPlayerSecond(rs.getLong("playerSecond"));
                r.setTurn(rs.getInt("turn"));
                r.setWinPlayer(WinSide.getBySide(rs.getString("win_player")));
                r.setGameType(rs.getString("game_type"));

            } else {
                return null;
            }

            return initialize(r);
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
            try {
                r.setEndDate(rs.getTimestamp("endDateTime"));
            } catch (NullPointerException ex) {
            }
            r.setStartDate(rs.getTimestamp("startDateTime"));
            r.setGameState(GameState.getByState(rs.getString("state")));
            r.setId(rs.getLong("id"));
            r.setPlayerFirst(rs.getLong("playerFirst"));
            r.setPlayerSecond(rs.getLong("playerSecond"));
            r.setTurn(rs.getInt("turn"));
            r.setWinPlayer(WinSide.getBySide(rs.getString("win_player")));
            r.setGameType(rs.getString("game_type"));

            return initialize(r);
        }

    }
}
