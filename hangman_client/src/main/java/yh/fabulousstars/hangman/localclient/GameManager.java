package yh.fabulousstars.hangman.localclient;

import yh.fabulousstars.hangman.client.IGame;
import yh.fabulousstars.hangman.client.IGameEvent;
import yh.fabulousstars.hangman.client.IGameEventHandler;
import yh.fabulousstars.hangman.client.IGameManager;
import yh.fabulousstars.hangman.client.events.CreateFailed;
import yh.fabulousstars.hangman.client.events.GameCreated;
import yh.fabulousstars.hangman.client.events.GameDeleted;

import java.util.ArrayList;
import java.util.List;

public class GameManager implements IGameManager {
    private final ArrayList<LocalGame> games;
    private IGameEventHandler handler;
    private String clientId;

    public GameManager(IGameEventHandler handler) {
        this.games = new ArrayList<>();
        this.handler = handler;
        clientId = "dummy";
    }

    @Override
    public List<IGame> getGames() {
        return new ArrayList<>(games);
    }

    @Override
    public void createGame(String name, String playerName, String password) {
        for (var game : games) {
            if(game.getName().equals(name)) {
                sendEvent(new CreateFailed());
                return;
            }
        }
        var game = new LocalGame(this, name, playerName, clientId, password);
        games.add(game);

        sendEvent(new GameCreated(game, game.getMe()));
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    void sendEvent(IGameEvent event) {
        handler.handleGameEvent(event);
    }
}
