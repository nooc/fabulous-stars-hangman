package yh.fabulousstars.hangman.client.events;

import yh.fabulousstars.hangman.client.IGame;

public class GameStarted extends AbstractEvent {
    /**
     * Constructor
     *
     * @param game Game instance
     */
    public GameStarted(IGame game) {
        super(game);
    }
}
