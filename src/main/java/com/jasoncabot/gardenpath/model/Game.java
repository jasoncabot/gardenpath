package com.jasoncabot.gardenpath.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jasoncabot.gardenpath.persistence.GameMemento;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class Game
{
    public static final int NUMBER_OF_SQUARES = 9;
    public static final int NUMBER_OF_FENCE_POSTS = NUMBER_OF_SQUARES + 1;
    public static final int TOTAL_SQUARES = NUMBER_OF_SQUARES * NUMBER_OF_SQUARES;
    public static final int TOTAL_FENCE_POSTS = NUMBER_OF_FENCE_POSTS * NUMBER_OF_FENCE_POSTS;

    private Long id;
    private Player me;
    private Player you;
    private boolean isMyTurn;
    private Instant lastMoveAt;
    private State state;
    private PrivateInfo privateInfo;

    private Game()
    {
        this.state = State.UNKNOWN;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    static boolean adjacent(final int a, final int b)
    {
        return (b == a - NUMBER_OF_SQUARES
                || (b == a + NUMBER_OF_SQUARES)
                || (b == a + 1 && b % NUMBER_OF_SQUARES > 0)
                || (b == a - 1 && a % NUMBER_OF_SQUARES > 0));
    }

    public Long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Player getMe()
    {
        return me;
    }

    public Player getYou()
    {
        return you;
    }

    public boolean isMyTurn()
    {
        return isMyTurn;
    }

    public long getLastMoveAt()
    {
        return lastMoveAt == null ? 0 : lastMoveAt.toEpochMilli();
    }

    public State getState()
    {
        return state;
    }

    @JsonIgnore
    public PrivateInfo getPrivateInfo()
    {
        return privateInfo;
    }

    @JsonIgnore
    public Optional<Player> getWinner()
    {
        return Stream.of(me, you).filter(Objects::nonNull).filter(Player::isInWinningPosition).findFirst();
    }

    @JsonProperty("winner")
    public Player getWinningPlayer()
    {
        return getWinner().orElse(null);
    }

    public void join(final Player joiner) throws GameException
    {
        Validate.notNull(joiner);

        if (me != null)
        {
            throw new GameException("Cannot join game you are already part of");
        }
        else if (you == null)
        {
            throw new GameException("Cannot join game without an opponent");
        }

        this.me = joiner;

        if (!(me.isPlayerOne() ^ you.isPlayerOne()))
        {
            throw new GameException("Cannot join game if two players are both player 1");
        }

        me.moveToStart();
        you.moveToStart();

        this.state = State.IN_PROGRESS;
        this.isMyTurn = me.isPlayerOne();
        this.lastMoveAt = Instant.now();
    }

    public void start()
    {
        this.state = State.WAITING_OPPONENT;
        this.isMyTurn = true;
        this.lastMoveAt = Instant.now();
    }

    public void move(final int end) throws GameException
    {
        if (!(getState() == State.IN_PROGRESS))
        {
            throw new GameException("Can only move when game is in progress");
        }
        if (!isMyTurn())
        {
            throw new GameException("Can only move on your turn");
        }
        if (!canMoveTo(end))
        {
            throw new GameException("Not a valid move");
        }
        me.updatePosition(end);
        endPlay();
    }

    public void fence(final Fence fence) throws GameException
    {
        if (!(getState() == State.IN_PROGRESS))
        {
            throw new GameException("Can only play fence when game is in progress");
        }
        if (!isMyTurn())
        {
            throw new GameException("Can only play fence on your turn");
        }
        if (!fence.isValid())
        {
            throw new GameException("Fence between " + fence.getStartIndex() + " and " + fence.getEndIndex() + " is not valid");
        }
        if (!me.hasFreeFence())
        {
            throw new GameException("No free fences");
        }
        final Collection<Fence> fencesPlayed = fencesOnBoard();
        if (fencesPlayed.stream().anyMatch(f -> f.blocksFence(fence)))
        {
            throw new GameException("Fence is blocked");
        }
        final Collection<Fence> fencesWithNewlyPlayed = new ArrayList<>(fencesPlayed);
        fencesWithNewlyPlayed.add(fence);
        if (!playersCanReachWinningPositions(fencesWithNewlyPlayed))
        {
            throw new GameException("Both players must be able to reach the end");
        }
        me.playFence(fence);
        endPlay();
    }

    private boolean playersCanReachWinningPositions(final Collection<Fence> fences)
    {
        boolean iCanReachWinningPosition = pathExists(me.getPosition(), me.getWinningPositions(), fences);
        boolean youCanReachWinningPosition = pathExists(you.getPosition(), you.getWinningPositions(), fences);
        return iCanReachWinningPosition && youCanReachWinningPosition;
    }

    private boolean canMoveTo(final int end)
    {
        final int start = me.getPosition();
        final int opponent = you.getPosition();

        // We can't move on top of the opponent
        if (end == opponent)
        {
            return false;
        }

        // We must move on the board
        if (end < 0 || end >= TOTAL_SQUARES)
        {
            return false;
        }

        // There must be no fence blocking a basic move of one square
        if (adjacent(start, end))
        {
            return noFenceBetween(start, end);
        }
        else
        {
            // we must be able to move from our square to the opponents square, then from our opponents square to the new square
            return adjacent(start, opponent) && noFenceBetween(start, opponent) && adjacent(opponent, end) && noFenceBetween(opponent, end);
        }
    }

    private boolean noFenceBetween(int start, int end)
    {
        return noFenceBetween(start, end, fencesOnBoard());
    }

    private boolean noFenceBetween(int start, int end, final Collection<Fence> fences)
    {
        return adjacent(start, end) && fences.stream().noneMatch(f -> f.blocksMove(start, end));
    }

    private List<PathNode> buildNodes(final Collection<Fence> fences)
    {
        final List<PathNode> allNodes = new ArrayList<>(TOTAL_SQUARES);
        for (int a = 0; a < TOTAL_SQUARES; a++)
        {
            allNodes.add(new PathNode(a));
        }

        allNodes.forEach(parent ->
                parent.getChildren().addAll(allNodes.stream().filter(node ->
                                adjacent(parent.getPosition(), node.getPosition()) && noFenceBetween(parent.getPosition(), node.getPosition(), fences)
                ).collect(Collectors.toList())));

        return allNodes;
    }

    private boolean pathExists(final int start, final int[] ending, final Collection<Fence> fences)
    {
        final List<PathNode> nodes = buildNodes(fences);

        // start on our current position and try to get to the end
        final PriorityQueue<PathNode> toExplore = new PriorityQueue<>();
        toExplore.add(nodes.get(start));

        PathNode current;
        while (!toExplore.isEmpty())
        {
            current = toExplore.remove();

            if (Arrays.binarySearch(ending, current.getPosition()) >= 0)
            {
                return true;
            }

            if (!current.isVisited())
            {
                toExplore.addAll(current.getChildren());
            }
            current.setVisited(true);
        }
        return false;
    }

    private Collection<Fence> fencesOnBoard()
    {
        return Stream.concat(
                me.getFences().stream().filter(Fence::isValid),
                you.getFences().stream().filter(Fence::isValid)
        ).collect(Collectors.toList());
    }

    void endPlay()
    {
        this.isMyTurn = false;
        this.lastMoveAt = Instant.now();

        if (getWinner().isPresent())
        {
            this.state = State.GAME_OVER;
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Game game = (Game) o;

        return new EqualsBuilder()
                .append(id, game.id)
                .append(state, game.state)
                .append(me, game.me)
                .append(you, game.you)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(me)
                .append(you)
                .append(state)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("me", me)
                .append("you", you)
                .append("isMyTurn", isMyTurn)
                .append("lastMoveAt", lastMoveAt)
                .append("state", state)
                .toString();
    }

    public enum State
    {
        UNKNOWN,
        WAITING_OPPONENT,
        IN_PROGRESS,
        GAME_OVER
    }

    public static class Builder
    {
        private Long id;
        private Player me;
        private Player you;
        private boolean isMyTurn;
        private Instant lastMoveAt;
        private State state;
        private PrivateInfo privateInfo;

        public Builder withMe(final Player me)
        {
            this.me = me;
            return this;
        }

        public Builder withYou(final Player you)
        {
            this.you = you;
            return this;
        }

        public Builder withPrivateInfo(final PrivateInfo info)
        {
            this.privateInfo = info;
            return this;
        }

        public Builder withAnonymousMemento(final GameMemento memento)
        {
            Validate.notNull(memento);

            this.lastMoveAt = memento.getLastMoveAt();
            this.state = State.valueOf(memento.getState());
            this.id = memento.getId();
            this.privateInfo = PrivateInfo.fromHashed(memento.getName(), memento.getHashedPassphrase());
            this.you = Player.fromMemento(memento, true).orElse(null);

            return this;
        }

        public Builder withMemento(final GameMemento memento, final String myIdentifier)
        {
            this.withAnonymousMemento(memento);

            this.me = null;
            this.you = null;

            Stream.of(Player.fromMemento(memento, true), Player.fromMemento(memento, false))
                    .filter(Optional::isPresent)
                    .forEach(potentialPlayer -> {
                        final Player player = potentialPlayer.get();
                        if (myIdentifier != null && myIdentifier.equals(player.getIdentifier()))
                        {
                            this.me = player;
                        }
                        else
                        {
                            this.you = player;
                        }
                    });

            this.isMyTurn = this.me != null && (me.isPlayerOne() && memento.isPlayer1Turn() || (!me.isPlayerOne() && !memento.isPlayer1Turn()));
            return this;
        }

        public Game build()
        {
            final Game game = new Game();
            game.id = id;
            game.me = me;
            game.you = you;
            game.isMyTurn = isMyTurn;
            game.lastMoveAt = lastMoveAt;
            game.state = state;
            game.privateInfo = privateInfo;
            return game;
        }
    }
}
