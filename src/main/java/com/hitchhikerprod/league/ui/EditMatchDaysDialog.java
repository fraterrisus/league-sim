package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.LeagueMatchDay;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

import java.util.Objects;
import java.util.Optional;

public class EditMatchDaysDialog extends AbstractListDialog {
    final ListView<? extends LeagueMatchDay> matchDayView;

    public EditMatchDaysDialog(Window parent, League league) {
        super(parent, league);
        super.setTitle("Match Days");

        matchDayView = new ListView<>(league.getMatchDays());
        matchDayView.setCellFactory(this::cellFactory);
        final MultipleSelectionModel<? extends LeagueMatchDay> listSelectionModel = matchDayView.getSelectionModel();
        listSelectionModel.setSelectionMode(SelectionMode.SINGLE);
        listSelectionModel.selectedItemProperty().addListener(enableButtons());

        upButton.setOnAction(getReorderHandler(matchDayView, -1));
        downButton.setOnAction(getReorderHandler(matchDayView, 1));
        trashCanButton.setOnAction(getDeleteHandler());
        plusSignButton.setOnAction(getAddHandler());

        final BorderPane innerPane = new BorderPane();
        innerPane.setCenter(matchDayView);
        innerPane.setRight(buttonVBox);

        final DialogPane outerPane = getDialogPane();
        outerPane.setContent(innerPane);
        outerPane.getStylesheets().setAll(parent.getScene().getStylesheets());
        outerPane.getButtonTypes().setAll(ButtonType.CLOSE);
    }

    private <T extends LeagueMatchDay> ListCell<T> cellFactory(ListView<T> view) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T matchDay, boolean empty) {
                super.updateItem(matchDay, empty);
                if (empty || Objects.isNull(matchDay)) {
                    setText("");
                } else {
                    setText(matchDay.getName());
                }
            }
        };
    }

    private EventHandler<ActionEvent> getAddHandler() {
        return event -> {
            final int index = matchDayView.getSelectionModel().getSelectedIndex();
            final TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("New Match Day");
            nameDialog.setHeaderText("New match day name:");
            final Optional<String> newName = nameDialog.showAndWait();
            newName.ifPresent(name -> {
                if (index == -1) league.addMatchDay(name);
                else league.addMatchDay(index, name);
            });
        };
    }

    private EventHandler<ActionEvent> getDeleteHandler() {
        return event -> {
            final int index = matchDayView.getSelectionModel().getSelectedIndex();
            if (index == -1) return;
            final LeagueMatchDay matchDay = matchDayView.getItems().get(index);
            if (Objects.isNull(matchDay)) return;
            if (!matchDay.getGames().isEmpty()) {
                final String confirmation = "This match day has existing games; are you sure you want to delete it?";
                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, confirmation);
                alert.setTitle("Delete Match Day?");
                final Optional<ButtonType> bType = alert.showAndWait();
                if (bType.isEmpty() || bType.get() != ButtonType.OK) return;
            }
            matchDayView.getItems().remove(matchDay);
        };
    }

    private ChangeListener<? super LeagueMatchDay> enableButtons() {
        return (obs, oldValue, newValue) -> {
            final boolean isNull = Objects.isNull(newValue);
            upButton.setDisable(isNull);
            downButton.setDisable(isNull);
            trashCanButton.setDisable(isNull);
        };
    }
}
