package yh.fabulousstars.hangman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import yh.fabulousstars.hangman.client.IGameEvent;
import yh.fabulousstars.hangman.client.events.GameStarted;
import yh.fabulousstars.hangman.client.events.PlayerDamage;
import yh.fabulousstars.hangman.client.events.PlayerJoined;
import yh.fabulousstars.hangman.client.events.SubmitWord;
import yh.fabulousstars.hangman.localclient.GameManager;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @FXML
    public TextArea logTextArea;
    @FXML
    public Button createButton;
    @FXML
    public TextField gameNameField;
    @FXML
    public TextField joinPasswordField;
    @FXML
    public ListView gameList;
    @FXML
    public Button joinButton;
    private GameManager gameManager;

    @FXML
    public void onCreateButtonClick(ActionEvent actionEvent) {

        var name = gameNameField.getText().strip();
        var password = joinPasswordField.getText();
        if(!name.isEmpty()) {
            if(gameManager.createGame(name, password)) {
                createButton.setDisable(true);
            }
        }
    }
    @FXML
    public void onJoinButtonClick(ActionEvent actionEvent) {
    }

    /**
     * Initialize game controller.
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        gameList.setDisable(true);

        gameManager = new GameManager(this::handleGameEvent);
    }

    private void handleGameEvent(IGameEvent event) {
        if(event instanceof PlayerJoined) {
            //...
        } else if (event instanceof PlayerDamage) {

        } else if (event instanceof GameStarted) {

        } else if (event instanceof SubmitWord) {
            // TODO: Submit word
        }
    }
}