package yh.fabulousstars.hangman.client.events;

import yh.fabulousstars.hangman.client.IGame;
import yh.fabulousstars.hangman.client.IPlayer;

/**
 * Player gets "damage", e.g. in hangman, get closer to be hanged.
 */
public class PlayerDamage extends AbstractEvent {
    private IPlayer player;

    /**
     * Constructor
     *
     * @param game Game instance
     */
    public PlayerDamage(IGame game, IPlayer player) {
        super(game);
        this.player = player;
    }

    IPlayer getPlayer()
    {
        return player;
    }
}
