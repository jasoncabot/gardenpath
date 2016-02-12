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
        final Player p1 = new Player(player1Id, player1Name, player1Position, player1Fences);
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

        final Player p2 = new Player(player2Id, player2Name, player2Position, player2Fences);


        boolean isMyTurn = isPlayerOneTurn ^ iAmPlayerOne;
        return new Game(gameId, state, lastMoveAt, gameInfo, isMyTurn, iAmPlayerOne ? p1 : p2, iAmPlayerOne ? p2 : p1);
    }

//    public static class GameBuilder {
//        private Long id;
//        private Player me;
//        private Player you;
//        private boolean isMyTurn;
//        private Instant lastMoveAt;
//        private Game.State state;
//        private PrivateInfo privateInfo;
//
//        public GameBuilder withMe(final Player me) {
//            this.me = me;
//            return this;
//        }
//
//        public GameBuilder withYou(final Player you) {
//            this.you = you;
//            return this;
//        }
//
//        public GameBuilder withPrivateInfo(final PrivateInfo info) {
//            this.privateInfo = info;
//            return this;
//        }
//
//        public GameBuilder withAnonymousMemento(final GameMemento memento) {
//            Validate.notNull(memento);
//
//            this.lastMoveAt = memento.getLastMoveAt();
//            this.state = State.valueOf(memento.getState());
//            this.id = memento.getId();
//            this.privateInfo = PrivateInfo.fromHashed(memento.getName(), memento.getHashedPassphrase());
//            this.you = Player.fromMemento(memento, true).orElse(null);
//
//            return this;
//        }
//
//        public GameBuilder withMemento(final GameMemento memento, final String myIdentifier) {
//            this.withAnonymousMemento(memento);
//
//            this.me = null;
//            this.you = null;
//
//            Stream.of(Player.fromMemento(memento, true), Player.fromMemento(memento, false))
//                    .filter(Optional::isPresent)
//                    .forEach(potentialPlayer -> {
//                        final Player player = potentialPlayer.get();
//                        if (myIdentifier != null && myIdentifier.equals(player.getIdentifier())) {
//                            this.me = player;
//                        } else {
//                            this.you = player;
//                        }
//                    });
//
//            this.isMyTurn = this.me != null && (me.isPlayerOne() && memento.isPlayer1Turn() || (!me.isPlayerOne() && !memento.isPlayer1Turn()));
//            return this;
//        }
//
//        public Game build() {
//            final Game game = new Game();
//            game.id = id;
//            game.me = me;
//            game.you = you;
//            game.isMyTurn = isMyTurn;
//            game.lastMoveAt = lastMoveAt;
//            game.state = state;
//            game.privateInfo = privateInfo;
//            return game;
//        }
//    }
//
//    public static Optional<Player> fromMemento(final GameMemento memento, final boolean p1) {
//        final Player player = new Player();
//        player.fences = new ArrayList<>(NUM_FENCES);
//        player.isPlayerOne = p1;
//        if (p1) {
//            player.identifier = memento.getPlayer1Id();
//            player.name = memento.getPlayer1Name();
//            player.position = memento.getPlayer1Position();
//            player.fences.add(Fence.get(memento.getPlayer1Fence1()));
//            player.fences.add(Fence.get(memento.getPlayer1Fence2()));
//            player.fences.add(Fence.get(memento.getPlayer1Fence3()));
//            player.fences.add(Fence.get(memento.getPlayer1Fence4()));
//            player.fences.add(Fence.get(memento.getPlayer1Fence5()));
//            player.fences.add(Fence.get(memento.getPlayer1Fence6()));
//            player.fences.add(Fence.get(memento.getPlayer1Fence7()));
//            player.fences.add(Fence.get(memento.getPlayer1Fence8()));
//            player.fences.add(Fence.get(memento.getPlayer1Fence9()));
//            player.fences.add(Fence.get(memento.getPlayer1Fence10()));
//        } else {
//            player.identifier = memento.getPlayer2Id();
//            player.name = memento.getPlayer2Name();
//            player.position = memento.getPlayer2Position();
//            player.fences.add(Fence.get(memento.getPlayer2Fence1()));
//            player.fences.add(Fence.get(memento.getPlayer2Fence2()));
//            player.fences.add(Fence.get(memento.getPlayer2Fence3()));
//            player.fences.add(Fence.get(memento.getPlayer2Fence4()));
//            player.fences.add(Fence.get(memento.getPlayer2Fence5()));
//            player.fences.add(Fence.get(memento.getPlayer2Fence6()));
//            player.fences.add(Fence.get(memento.getPlayer2Fence7()));
//            player.fences.add(Fence.get(memento.getPlayer2Fence8()));
//            player.fences.add(Fence.get(memento.getPlayer2Fence9()));
//            player.fences.add(Fence.get(memento.getPlayer2Fence10()));
//        }
//
//        if (isBlank(player.identifier)) {
//            return Optional.empty();
//        }
//        return Optional.of(player);
//    }
}
