package yh.fabulousstars.hangman.client.events;

import yh.fabulousstars.hangman.client.IGame;
import yh.fabulousstars.hangman.client.IPlayer;

public class GameCreated extends AbstractEvent {
    private final IPlayer owner;
    /**
     * Constructor
     *
     * @param game Game instance
     */
    public GameCreated(IGame game, IPlayer owner) {
        super(game);
        this.owner = owner;
    }
}
