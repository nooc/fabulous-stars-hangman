package yh.fabulousstars.hangman.client;

import java.util.List;

public interface IGameManager {

    /**
     * Get list of games.
     * @return List of games.
     */
    List<IGame> getGames();

    /**
     * Create a new game.
     * @param name Game name
     * @param password Password
     * @return Success or fail
     */
    boolean createGame(String name, String password);

    /**
     * Delete empty game.
     * @param game
     * @return Success or fail.
     */
    boolean deleteGame(IGame game);
}
