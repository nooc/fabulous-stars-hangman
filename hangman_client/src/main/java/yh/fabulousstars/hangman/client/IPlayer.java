package yh.fabulousstars.hangman.client;

public interface IPlayer {

    /**
     * Get player id.
     * @return Player id
     */
    long getId();

    /**
     * Check if player is owner/creator.
     * @return True or false
     */
    boolean isOwner();

    /**
     * Get game instance this player belongs to.
     * @return game instance
     */
    IGame getGame();

    /**
     * Get player name.
     * @return Name
     */
    String getName();

    /**
     * Get player state.
     * @return PlayetState
     */
    PlayerState getState();

    /**
     * Get player damage.
     * @return 0 to MAX_DAMAGE
     */
    int getDamage();
}
