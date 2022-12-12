package yh.fabulousstars.hangman.client.events;

import yh.fabulousstars.hangman.client.IGame;

public class FailedToJoin extends AbstractEvent{

    /**
     * Constructor
     *
     * @param game Game instance
     */
    public FailedToJoin(IGame game) {
        super(game);
    }
}
