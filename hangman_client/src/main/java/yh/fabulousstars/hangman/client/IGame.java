package yh.fabulousstars.hangman.client;

import yh.fabulousstars.hangman.localclient.LocalPlayer;

import java.util.List;

public interface IGame {

    /**
     * Get game id.
     * @return Game id
     */
    long getId();

    /**
     * Get manager the game belongs to.
     * @return IGameManager instance
     */
    IGameManager getManager();

    /**
     * Get game name.
     * @return game name
     */
    String getName();

    /**
     * Get game theme.
     * @return theme name
     */
    String getGameTheme();

    /**
     * Get list of players.
     * @return List of players.
     */
    List<IPlayer> getPlayers();

    /**
     * Get our player instance.
     * @return
     */
    IPlayer getMe();

    /**
     * Join a game.
     * @return Success or false.
     */
    boolean joinGame(String playerName, String password);

    /**
     * Perform a submit.
     * @param value
     */
    void submit(String value);
}
