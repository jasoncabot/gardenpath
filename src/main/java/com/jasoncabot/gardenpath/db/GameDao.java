package com.jasoncabot.gardenpath.db;

import com.jasoncabot.gardenpath.core.Fence;
import com.jasoncabot.gardenpath.core.Game;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.SingleValueResult;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public abstract class GameDao {
    private static final String FIELDS_FOR_SELECTION = "state, last_move_at, name, hashed_password, is_player_one_turn" +
            ", p1_id, p1_name, p1_position, p1_f1, p1_f2, p1_f3, p1_f4, p1_f5, p1_f6, p1_f7, p1_f8, p1_f9, p1_f10" +
            ", p2_id, p2_name, p2_position, p2_f1, p2_f2, p2_f3, p2_f4, p2_f5, p2_f6, p2_f7, p2_f8, p2_f9, p2_f10 ";
    private static final String PLAY_UPDATE = "UPDATE games SET state = :state, last_move_at = current_timestamp()";
    private static final String PLAY_UPDATE_WHERE = "WHERE id = :id " +
            "AND state = 'IN_PROGRESS' " +
            "AND ((is_player_one_turn AND p1_id = :player_id) OR (NOT(is_player_one_turn) AND p2_id = :player_id))";
    private static final String JOIN_GAME_UPDATE = "p2_id = :playerId, " +
            "p2_name = :playerName, " +
            "is_player_one_turn = :player1Turn, " +
            "p1_position = 4, " +
            "p2_position = 76, " +
            "state = 'IN_PROGRESS', " +
            "last_move_at = current_timestamp() ";

    @SqlQuery("SELECT id, 0 as is_player_one, " +
            FIELDS_FOR_SELECTION +
            "FROM games " +
            "WHERE state = :state")
    public abstract Collection<Game> findAll(@Bind("state") final String state);

    @SingleValueResult(Game.class)
    @SqlQuery("SELECT id, (:player_id = p1_id) as is_player_one, " +
            FIELDS_FOR_SELECTION +
            "FROM games " +
            "WHERE id = :id AND (p1_id = :player_id OR p2_id = :player_id)")
    public abstract Optional<Game> find(@Bind("id") long gameId, @Bind("player_id") String playerId);

    @SingleValueResult(Game.class)
    @SqlQuery("SELECT id, (:player_id = p1_id) as is_player_one, " +
            FIELDS_FOR_SELECTION +
            "FROM games " +
            "WHERE name = :name AND hashed_password = :hashed_password AND (p1_id = :player_id OR p2_id = :player_id)")
    public abstract Optional<Game> find(@Bind("name") final String gameName, @Bind("hashed_password") final String gamePassword, @Bind("player_id") String playerId);

    @SingleValueResult(Game.class)
    @SqlQuery("SELECT id, (:player_id = p1_id) as is_player_one, " +
            FIELDS_FOR_SELECTION +
            "FROM games " +
            "WHERE id = :id AND (p1_id = :player_id OR p2_id = :player_id) AND state = :state")
    public abstract Optional<Game> find(@Bind("id") final long gameId, @Bind("player_id") final String playerId, @Bind("state") final String state);

    @SqlUpdate(PLAY_UPDATE +
            ", p1_position = :position " +
            PLAY_UPDATE_WHERE)
    public abstract void updatePlayerOnePosition(@Bind("id") final long gameId, @Bind("player_id") final String playerId, @Bind("position") final int position, @Bind("state") final String state);

    @SqlUpdate(PLAY_UPDATE +
            ", p2_position = :position " +
            PLAY_UPDATE_WHERE)
    public abstract void updatePlayerTwoPosition(@Bind("id") final long gameId, @Bind("player_id") final String playerId, @Bind("position") final int position, @Bind("state") final String state);

    @SqlUpdate(PLAY_UPDATE +
            ", p1_f1 = :fence1 " +
            ", p1_f2 = :fence2 " +
            ", p1_f3 = :fence3 " +
            ", p1_f4 = :fence4 " +
            ", p1_f5 = :fence5 " +
            ", p1_f6 = :fence6 " +
            ", p1_f7 = :fence7 " +
            ", p1_f8 = :fence8 " +
            ", p1_f9 = :fence9 " +
            ", p1_f10 = :fence10 " +
            PLAY_UPDATE_WHERE)
    public abstract void updatePlayerOneFences(@Bind("id") final long gameId, @Bind("player_id") final String playerId, @BindBean final FenceGameData fences, @Bind("state") final String state);

    @SqlUpdate(PLAY_UPDATE +
            ", p2_f1 = :fence1 " +
            ", p2_f2 = :fence2 " +
            ", p2_f3 = :fence3 " +
            ", p2_f4 = :fence4 " +
            ", p2_f5 = :fence5 " +
            ", p2_f6 = :fence6 " +
            ", p2_f7 = :fence7 " +
            ", p2_f8 = :fence8 " +
            ", p2_f9 = :fence9 " +
            ", p2_f10 = :fence10 " +
            PLAY_UPDATE_WHERE)
    public abstract void updatePlayerTwoFences(@Bind("id") final long gameId, @Bind("player_id") final String playerId, @BindBean final FenceGameData fences, @Bind("state") final String state);

    @SqlUpdate("UPDATE games SET " +
            JOIN_GAME_UPDATE +
            "WHERE id = :gameId AND state = 'WAITING_OPPONENT' AND p1_id != :playerId")
    public abstract void joinPublicGame(@BindBean final StartGameData data);

    @SqlUpdate("UPDATE games SET " +
            JOIN_GAME_UPDATE +
            "WHERE id = :gameId, state = 'WAITING_OPPONENT' AND p1_id != :playerId AND name = :gameName AND hashed_password = :gamePassword")
    public abstract void joinPrivateGame(@BindBean final StartGameData data);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO games (state, last_move_at, p1_id, p1_name) VALUES ('WAITING_OPPONENT', current_timestamp(), :player_id, :player_name)")
    public abstract long createPublicGame(@Bind("player_id") final String playerId, @Bind("player_name") final String playerName);

    // TODO: optimistic locking WHERE NOT(exists(game_name, game_password, state='WAITING_OPPONENT'))
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO games (state, last_move_at, name, hashed_password, p1_id, p1_name) VALUES ('WAITING_OPPONENT', current_timestamp(), :game_name, :game_password, :player_id, :player_name)")
    public abstract long createPrivateGame(@Bind("player_id") final String playerId, @Bind("player_name") final String playerName, @Bind("game_name") final String gameName, @Bind("game_password") final String hashedPassword);

    public static class FenceGameData {

        final int fence1;
        final int fence2;
        final int fence3;
        final int fence4;
        final int fence5;
        final int fence6;
        final int fence7;
        final int fence8;
        final int fence9;
        final int fence10;

        public int getFence1()
        {
            return fence1;
        }

        public int getFence2()
        {
            return fence2;
        }

        public int getFence3()
        {
            return fence3;
        }

        public int getFence4()
        {
            return fence4;
        }

        public int getFence5()
        {
            return fence5;
        }

        public int getFence6()
        {
            return fence6;
        }

        public int getFence7()
        {
            return fence7;
        }

        public int getFence8()
        {
            return fence8;
        }

        public int getFence9()
        {
            return fence9;
        }

        public int getFence10()
        {
            return fence10;
        }

        public FenceGameData(final List<Fence> fences) {
            fence1 = fenceId(fences.get(0));
            fence2 = fenceId(fences.get(1));
            fence3 = fenceId(fences.get(2));
            fence4 = fenceId(fences.get(3));
            fence5 = fenceId(fences.get(4));
            fence6 = fenceId(fences.get(5));
            fence7 = fenceId(fences.get(6));
            fence8 = fenceId(fences.get(7));
            fence9 = fenceId(fences.get(8));
            fence10 = fenceId(fences.get(9));
        }

        private int fenceId(final Fence fence) {
            return fence.isValid() ? fence.hashCode() : 0;
        }
    }

    public static class StartGameData {
        final long gameId;
        final String playerId;
        final String playerName;
        final boolean isPlayer1Turn;
        final String gameName;
        final String gamePassword;

        public long getGameId() {
            return gameId;
        }

        public String getPlayerId() {
            return playerId;
        }

        public String getPlayerName() {
            return playerName;
        }

        public boolean isPlayer1Turn() {
            return isPlayer1Turn;
        }

        public String getGameName() {
            return gameName;
        }

        public String getGamePassword() {
            return gamePassword;
        }

        public StartGameData(final long gameId, final String playerId, final String playerName) {
            this.gameId = gameId;
            this.playerId = playerId;
            this.playerName = playerName;
            this.gameName = null;
            this.gamePassword = null;
            this.isPlayer1Turn = true;
        }

        public StartGameData(final String playerId, final String playerName, final String gameName, final String gamePassword) {
            this.gameId = -1;
            this.playerId = playerId;
            this.playerName = playerName;
            this.gameName = gameName;
            this.gamePassword = gamePassword;
            this.isPlayer1Turn = true;
        }
    }
}
