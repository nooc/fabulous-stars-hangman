package yh.fabulousstars.hangman.client.events;

import yh.fabulousstars.hangman.client.IGame;
import yh.fabulousstars.hangman.client.IPlayer;

public class PlayerJoined extends AbstractEvent {
    private final IPlayer player;
    /**
     * Constructor
     *
     * @param game Game instance
     */
    public PlayerJoined(IGame game, IPlayer player) {
        super(game);
        this.player = player;
    }
}
