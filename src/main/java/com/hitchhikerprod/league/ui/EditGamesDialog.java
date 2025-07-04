package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.definitions.League;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.util.Pair;

import java.util.Objects;
import java.util.Optional;

public class EditGamesDialog extends AbstractListDialog {
    private final ListView<? extends LeagueGameData> gamesView;

    private final int matchDayIndex;

    public EditGamesDialog(Window parent, League league, int matchDayIndex) {
        super(parent, league);
        super.setTitle("Games");

        this.matchDayIndex = matchDayIndex;

        gamesView = new ListView<>(league.getGames(matchDayIndex));
        gamesView.setCellFactory(this::cellFactory);
        final MultipleSelectionModel<? extends LeagueGameData> gamesSelectionModel = gamesView.getSelectionModel();
        gamesSelectionModel.setSelectionMode(SelectionMode.SINGLE);
        gamesSelectionModel.selectedItemProperty().addListener(enableButtons());

        upButton.setOnAction(getReorderHandler(gamesView, -1));
        downButton.setOnAction(getReorderHandler(gamesView, 1));
        trashCanButton.setOnAction(getDeleteHandler());
        plusSignButton.setOnAction(getAddHandler());

        final BorderPane innerPane = new BorderPane();
        innerPane.setCenter(gamesView);
        innerPane.setRight(buttonVBox);

        final DialogPane outerPane = getDialogPane();
        outerPane.setContent(innerPane);
        outerPane.getStylesheets().setAll(parent.getScene().getStylesheets());
        outerPane.getButtonTypes().setAll(ButtonType.CLOSE);
    }

    private <T extends LeagueGameData> ListCell<T> cellFactory(ListView<T> view) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T game, boolean empty) {
                super.updateItem(game, empty);
                if (empty || Objects.isNull(game)) {
                    setText("");
                } else {
                    final StringBuilder gameString = new StringBuilder();
                    gameString.append(game.getHomeTeam().getId())
                            .append(" v ")
                            .append(game.getAwayTeam().getId());
                    final Integer homeScore = game.getHomeScore();
                    final Integer awayScore = game.getAwayScore();
                    if (Objects.nonNull(homeScore) && Objects.nonNull(awayScore)) {
                        gameString.append(" (").append(homeScore).append(":").append(awayScore).append(")");
                    }
                    setText(gameString.toString());
                }
            }
        };
    }

    private EventHandler<ActionEvent> getAddHandler() {
        return event -> {
            final Optional<Pair<String, String>> pair = new NewGameDialog(parent, league.getTeams()).showAndWait();
            pair.ifPresent(teams -> league.createGame(matchDayIndex, teams.getKey(), teams.getValue()));
        };
    }

    private EventHandler<ActionEvent> getDeleteHandler() {
        return event -> {
            final int index = gamesView.getSelectionModel().getSelectedIndex();
            if (index == -1) return;
            final LeagueGameData game = gamesView.getItems().get(index);
            if (Objects.isNull(game)) return;
            final String confirmation = "Are you sure you want to delete this game?";
            final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, confirmation);
            alert.setTitle("Delete Game?");
            final Optional<ButtonType> bType = alert.showAndWait();
            if (bType.isEmpty() || bType.get() != ButtonType.OK) return;
            gamesView.getItems().remove(game);
        };
    }

    private ChangeListener<? super LeagueGameData> enableButtons() {
        return (obs, oldValue, newValue) -> {
            final boolean isNull = Objects.isNull(newValue);
            upButton.setDisable(isNull);
            downButton.setDisable(isNull);
            trashCanButton.setDisable(isNull);
        };
    }
}
