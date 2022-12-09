package yh.fabulousstars.hangman.localclient;

import yh.fabulousstars.hangman.client.IGame;
import yh.fabulousstars.hangman.client.IPlayer;
import yh.fabulousstars.hangman.client.PlayerState;

public class LocalPlayer implements IPlayer {
    private static long playerIdCounter = 100;
    private final long id;
    private final LocalGame game;
    private final String name;
    private PlayerState state;
    private int damage;
    private boolean owner;

    LocalPlayer(LocalGame game, String name) {
        this.id = playerIdCounter++;
        this.game = game;
        this.name = name;
        this.state = PlayerState.Initial;
        this.damage = 0;
        this.owner = false;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isOwner() {
        return owner;
    }

    void setOwner() {
        owner = true;
    }

    @Override
    public IGame getGame() {
        return game;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PlayerState getState() {
        return state;
    }

    @Override
    public int getDamage() {
        return damage;
    }
}
