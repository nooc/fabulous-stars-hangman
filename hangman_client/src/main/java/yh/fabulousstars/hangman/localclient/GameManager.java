package yh.fabulousstars.hangman.localclient;

import yh.fabulousstars.hangman.client.IGame;
import yh.fabulousstars.hangman.client.IGameEvent;
import yh.fabulousstars.hangman.client.IGameEventHandler;
import yh.fabulousstars.hangman.client.IGameManager;
import yh.fabulousstars.hangman.client.events.GameDeleted;

import java.util.ArrayList;
import java.util.List;

public class GameManager implements IGameManager {
    private final ArrayList<LocalGame> games;
    private IGameEventHandler handler;

    public GameManager(IGameEventHandler handler) {
        this.games = new ArrayList<>();
        this.handler = handler;
    }

    @Override
    public List<IGame> getGames() {
        return new ArrayList<>(games);
    }

    @Override
    public boolean createGame(String name, String password) {
        for (var game : games) {
            if(game.getName().equals(name)) {
                return false;
            }
        }
        games.add(new LocalGame(this, name, password));

        return true;
    }

    @Override
    public boolean deleteGame(IGame game) {
        if(games.contains(game)) {
            games.remove(game);
            sendEvent(new GameDeleted(game));
        }
        return false;
    }

    void sendEvent(IGameEvent event) {
        handler.handleGameEvent(event);
    }
}
