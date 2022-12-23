package yh.fabulousstars.hangman;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Translate;
import yh.fabulousstars.hangman.client.IGame;
import yh.fabulousstars.hangman.client.IPlayer;
import yh.fabulousstars.hangman.client.events.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    private static final int IMAGE_STATES = 11;
    private static final double CANVAS_WIDTH = 480;
    private static final double CANVAS_HEIGHT = 360;
    private final ObservableList<String> chatList;
    private final ObservableList<String> playerList;
    private final Map<String, CanvasWrapper> canvasMap;
    private final MediaHelper media;
    private final MediaPlayer music;
    @FXML
    public TextField guessTextField;
    @FXML
    public ListView playerListView;
    @FXML
    public ListView chatListView;
    @FXML
    public TextField chatTextField;
    @FXML
    public Pane canvasContainer;
    @FXML
    public Button startButton;
    @FXML
    public VBox rootView;
    private IGame game;
    private final Font canvasFont;
    private final double canvasFontWidth;
    private final Affine figureDrawingTransform;
    private final Affine nameTransform;

    public GameController() {
        this.canvasMap = new HashMap<>();
        this.game = null;
        this.canvasFont = Font.loadFont(GameApplication.class.getResourceAsStream("/RobotoMono-Medium.ttf"), 36);
        this.canvasFontWidth = new Text("_").getLayoutBounds().getWidth();
        this.chatList = FXCollections.observableArrayList();
        this.playerList = FXCollections.observableArrayList();
        this.media = MediaHelper.getInstance();
        this.music = this.media.getMedia("8-bit-brisk-music-loop");
        this.music.setVolume(0.3);
        this.music.setCycleCount(Integer.MAX_VALUE);
        // todo set text drawing transforms
        var identity = new Affine();
        this.figureDrawingTransform = identity;
        this.nameTransform = identity;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startButton.setDisable(true);
        startButton.setOnAction(this::onStartButton);
        guessTextField.setOnAction(this::onGuessEntered);
        guessTextField.setDisable(true);
        chatTextField.setOnAction(this::onChatEntered);
        chatListView.setItems(chatList);
        playerListView.setItems(playerList);
    }

    private void onGuessEntered(ActionEvent actionEvent) {
        var guess = guessTextField.getText().trim();
        if (guess.length() == 1) {
            guessTextField.clear();
            // send to server
            game.getManager().submitGuess(guess);
        } else {
            media.getSound("error").play();
        }
    }

    private void onStartButton(ActionEvent actionEvent) {
        startButton.setVisible(false);
        game.start();
    }

    private void onChatEntered(ActionEvent actionEvent) {
        var message = chatTextField.getText().trim();
        chatTextField.clear();
        if (!message.isEmpty()) {
            game.getManager().say(message);
        }
    }

    public void setGame(IGame game) {
        this.game = game;
        Platform.runLater(() -> {
            // initial player (this client)
            updatePlayers(game);
        });

    }

    public void handlePlayerState(PlayerState event) {
        drawCanvases();
    }

    public void handleGameStarted(GameStarted event) {
        startButton.setVisible(false);
        music.play();
        drawCanvases();
    }

    public void handleGuessResult(GuessResult event) {
        if (event.isCorrect()) {
            media.getSound("success").play();
            if(event.isFinished()) {
                guessTextField.setDisable(true);
            }
        } else {
            media.getSound("error").play();
        }
    }

    public void handlePlayerList(IGame game) {

        var players = game.getPlayers();
        updatePlayers(game);
        startButton.setDisable(players.size() < 2);
    }

    public void handleChatMessage(ChatMessage event) {
        chatList.add(0, event.getMessage());
        media.getSound("button").play();
    }

    /**
     * Rebuild canvas grid.
     *
     * @param game
     */
    private void updatePlayers(IGame game) {
        var myId = game.getManager().getClient().getClientId();
        var players = game.getPlayers();
        var clientIds = new ArrayList<String>();
        for (var player : players) {
            clientIds.add(player.getClientId());
            if (!canvasMap.containsKey(player.getClientId())) {
                var canvas = new Canvas();
                canvas.setManaged(false);
                var wrapper = new CanvasWrapper(canvas, player);
                canvasMap.put(player.getClientId(), wrapper);
            }
        }
        // remove obsolete players
        var keys = new ArrayList<>(canvasMap.keySet());
        for (var key : keys) {
            if (!clientIds.contains(key)) {
                canvasMap.remove(key);
            }
        }
        // make me first in list
        clientIds.remove(myId);
        clientIds.add(0, myId);
        // rebuild canvas grid
        int row = 0;
        int col = -1;
        canvasContainer.getChildren().clear();
        var wrappers = canvasMap.values();
        for (var playerId : clientIds) {
            var wrapper = canvasMap.get(playerId);
            if (++col > 2) {
                col = 0;
                row++;
            }
            canvasContainer.getChildren().add(wrapper.canvas);
            wrapper.canvas.setLayoutY(CANVAS_HEIGHT * row);
            wrapper.canvas.setLayoutX(CANVAS_WIDTH * col);
            wrapper.canvas.setWidth(CANVAS_WIDTH);
            wrapper.canvas.setHeight(CANVAS_HEIGHT);
        }
        var wCount = wrappers.size();
        var containerWidth = CANVAS_WIDTH * (wCount > 3 ? 3 : wCount);
        var containerHeight = CANVAS_HEIGHT * (wCount > 3 ? 2 : 1);
        canvasContainer.setMinSize(containerWidth, containerHeight);
        canvasContainer.setPrefSize(containerWidth, containerHeight);
        var scene = rootView.getScene();
        canvasContainer.getParent().layout();
        rootView.layout();
        scene.getWindow().sizeToScene();
        drawCanvases();
    }

    private void drawCanvases() {
        canvasMap.forEach((client, wrapper) -> {
            canvasBackground(wrapper);
        });
    }

    public void canvasBackground(CanvasWrapper wrapper) {

        var state = wrapper.player.getPlayState();
        //gc = set the background color
        //creating a rectangle covering 100% of the canvas makes it look like a background
        //The color is able to change
        GraphicsContext gc = wrapper.canvas.getGraphicsContext2D();

        // if self
        var local = game.getManager().getClient().getClientId();
        var wrapped = wrapper.player.getClientId();
        if (local.equals(wrapped)) {
            gc.setFill(Color.LIGHTSKYBLUE);
        } else {
            gc.setFill(Color.MISTYROSE);
        }

        gc.fillRect(0, 0, wrapper.canvas.getWidth(), wrapper.canvas.getHeight());

        // Name
        gc.setFont(canvasFont);
        gc.setTransform(nameTransform);
        gc.strokeText(wrapper.player.getName(),0, 0);

        //Prints the black bar
        blackBarForLetter(wrapper);
        //Draws the hangman
        hangmanFigure(wrapper);
        //draws the wrongly guessed letters
        addWrongLetter(wrapper);
        //draws the correctly guessed word
        addCorrectLetter(wrapper);
    }

    /**
     * Draw letter positions.
     *
     * @param wrapper
     */
    public void blackBarForLetter(CanvasWrapper wrapper) {
        var state = wrapper.player.getPlayState();
        if (state != null && state.getCurrentWord() != null) {

            if(wrapper.wordTransform == null) {
                // todo transkale and scale
                wrapper.wordTransform = Affine.translate(wrapper.canvas.getWidth() * 0.3);
            }

            GraphicsContext gc = wrapper.canvas.getGraphicsContext2D();
            gc.setFont(canvasFont);
            gc.setTransform(wrapper.wordTransform);
            gc.strokeText(wrapper.wordLines, 0, 0);
        }
    }

    /**
     * Draw figure based on damage.
     *
     * @param wrapper
     */
    public void hangmanFigure(CanvasWrapper wrapper) {

        var state = wrapper.player.getPlayState();
        GraphicsContext gc = wrapper.canvas.getGraphicsContext2D();
        var damage = state == null ? 1 : state.getDamage() + 1;
        if (damage > IMAGE_STATES) {
            damage = IMAGE_STATES;
        }
        gc.setTransform(figureDrawingTransform);
        gc.drawImage(media.getImage("HangmanTranState" + damage),
                0, 10,
                wrapper.canvas.getWidth() * 0.3, wrapper.canvas.getHeight() * 0.5);
    }

    public void addWrongLetter(CanvasWrapper wrapper) {
        var state = wrapper.player.getPlayState();
        if (state != null) {
            // if the guess is wrong make the letter appear in red
            // place them to the right of the hangman

            // todo translate and scale
            var wrongLetterTransform = ;

            GraphicsContext gc = wrapper.canvas.getGraphicsContext2D();
            gc.setFill(Color.RED);
            gc.setFont(canvasFont);
            gc.setTransform(wrongLetterTransform);
            var letters = state.getWrongGuesses();
            int row = 0;
            double width = 0;
            for(var letter : letters) {
                gc.strokeText(String.valueOf(letter), width, 4);
                width += canvasFontWidth;
            }
        }
    }

    public void addCorrectLetter(CanvasWrapper wrapper) {

        var state = wrapper.player.getPlayState();
        if (state != null && state.getCurrentWord() != null) {

            GraphicsContext gc = wrapper.canvas.getGraphicsContext2D();
            gc.setFont(canvasFont);
            gc.setTransform(wrapper.wordTransform);
            var currentWordState = String.valueOf(state.getCorrectGuesses());
            gc.strokeText(currentWordState, 0, 0);
        }
    }

    /**
     * Request word from player.
     * Send it to server.
     *
     * @param event
     */
    public void handleRequestWord(RequestWord event) {
        media.getSound("button").play();
        String word = null;
        while (word == null) {
            word = DialogHelper.promptString(String.format(
                    "Enter new word between %d and %d letters:",
                    event.getMinLength(),
                    event.getMaxLength()
            ));
            if (word != null) {
                word = word.strip();
                if (!(word.length() >= event.getMinLength()
                        && word.length() <= event.getMaxLength())) {
                    word = null;
                }
            }
        }
        // send to server
        game.getManager().submitWord(word);
    }

    public void handleRequestGuess(RequestGuess event) {
        guessTextField.setDisable(false);
        guessTextField.requestFocus();
    }

    public void dispose() {
        music.stop();
        this.guessTextField.setDisable(true);
        this.chatTextField.setDisable(true);
    }

    public void handleGameOver(GameOver event) {
        media.getSound("success").play();
        dispose();

        // todo draw winner / loser gfx

        if (event.isWinner()) {
            DialogHelper.showMessage("You won the game!", Alert.AlertType.CONFIRMATION);
        } else {
            DialogHelper.showMessage("You lost...", Alert.AlertType.CONFIRMATION);
        }
    }

    class CanvasWrapper {
        final Canvas canvas;
        final IPlayer player;
        String wordLines;
        private Affine wordTransform;

        CanvasWrapper(Canvas canvas, IPlayer player) {
            this.canvas = canvas;
            this.player = player;
            this.wordLines = "";
            this.wordTransform = null;
        }
    }

}
