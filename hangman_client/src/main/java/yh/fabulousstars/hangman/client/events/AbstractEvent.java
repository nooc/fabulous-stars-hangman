package yh.fabulousstars.hangman.client.events;

import yh.fabulousstars.hangman.client.IGame;
import yh.fabulousstars.hangman.client.IGameEvent;

public abstract class AbstractEvent implements IGameEvent {
    /**
     * Game instance of event.
     */
    private final IGame game;

    /**
     * Constructor
     * @param game Game instance
     */
    AbstractEvent(IGame game) {
        this.game = game;
    }

    @Override
    public IGame getGame() {
        return this.game;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}
