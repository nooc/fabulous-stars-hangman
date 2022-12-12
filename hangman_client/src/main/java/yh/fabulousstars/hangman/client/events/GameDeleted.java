package yh.fabulousstars.hangman.client.events;

import yh.fabulousstars.hangman.client.IGame;

public class GameDeleted extends AbstractEvent{
    /**
     * Constructor
     *
     * @param game Game instance
     */
    public GameDeleted(IGame game) {
        super(game);
    }
}
