package yh.fabulousstars.hangman.client.events;

import yh.fabulousstars.hangman.client.IGame;

public class SubmitWord extends AbstractEvent {

    /**
     * Constructor
     *
     * @param game Game instance
     */
    public SubmitWord(IGame game) {
        super(game);
    }
}
