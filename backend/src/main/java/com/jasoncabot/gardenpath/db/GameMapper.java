package com.jasoncabot.gardenpath.db;

import com.jasoncabot.gardenpath.core.Fence;
import com.jasoncabot.gardenpath.core.Game;
import com.jasoncabot.gardenpath.core.Player;
import com.jasoncabot.gardenpath.core.PrivateInfo;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameMapper implements ResultSetMapper<Game> {

    @Override
    public Game map(final int i, final ResultSet resultSet, final StatementContext statementContext) throws SQLException {

        // game info
        long gameId = resultSet.getLong("id");
        final Game.State state = Game.State.valueOf(resultSet.getString("state"));
        final Instant lastMoveAt = resultSet.getTimestamp("last_move_at").toInstant();
        final PrivateInfo gameInfo = PrivateInfo.fromHashed(
                resultSet.getString("name"),
                resultSet.getString("hashed_password")
        );
        final boolean isPlayerOneTurn = resultSet.getBoolean("is_player_one_turn");
        final boolean iAmPlayerOne = resultSet.getBoolean("is_player_one");

        // player 1
        final String player1Id = resultSet.getString("p1_id");
        final String player1Name = resultSet.getString("p1_name");
        final int player1Position = resultSet.getInt("p1_position");
        final List<Fence> player1Fences = Stream.of(
                resultSet.getInt("p1_f1"),
                resultSet.getInt("p1_f2"),
                resultSet.getInt("p1_f3"),
                resultSet.getInt("p1_f4"),
                resultSet.getInt("p1_f5"),
                resultSet.getInt("p1_f6"),
                resultSet.getInt("p1_f7"),
                resultSet.getInt("p1_f8"),
                resultSet.getInt("p1_f9"),
                resultSet.getInt("p1_f10"))
                .map(Fence::get)
                .collect(Collectors.toList());
        final Player p1 = new Player(player1Id, player1Name, true, player1Position, player1Fences);
        // player 2
        final String player2Id = resultSet.getString("p2_id");
        final String player2Name = resultSet.getString("p2_name");
        final int player2Position = resultSet.getInt("p2_position");
        final List<Fence> player2Fences = Stream.of(
                resultSet.getInt("p2_f1"),
                resultSet.getInt("p2_f2"),
                resultSet.getInt("p2_f3"),
                resultSet.getInt("p2_f4"),
                resultSet.getInt("p2_f5"),
                resultSet.getInt("p2_f6"),
                resultSet.getInt("p2_f7"),
                resultSet.getInt("p2_f8"),
                resultSet.getInt("p2_f9"),
                resultSet.getInt("p2_f10"))
                .map(Fence::get)
                .collect(Collectors.toList());

        final Player p2 = new Player(player2Id, player2Name, false, player2Position, player2Fences);


        boolean isMyTurn = isPlayerOneTurn ^ !iAmPlayerOne;
        return new Game(gameId, state, lastMoveAt, gameInfo, isMyTurn, iAmPlayerOne ? p1 : p2, iAmPlayerOne ? p2 : p1);
    }
}
