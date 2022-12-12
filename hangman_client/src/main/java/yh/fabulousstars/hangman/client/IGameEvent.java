package yh.fabulousstars.hangman.client;

public interface IGameEvent {
    /**
     * Get game instance the event belongs to.
     * @return Game instance
     */
    IGame getGame();

    /**
     * Get event type.
     * @return type name
     */
    String getType();
}
