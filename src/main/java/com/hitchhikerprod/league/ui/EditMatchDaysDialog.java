package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.LeagueMatchDay;
import com.hitchhikerprod.league.definitions.League;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class EditMatchDaysDialog extends Dialog<Void> {
    final League league;
    final ListView<? extends LeagueMatchDay> matchDayView;

    final Button upButton;
    final Button downButton;
    final Button trashCanButton;
    final Button plusSignButton;

    public EditMatchDaysDialog(Window parent, League league) {
        super();
        super.initOwner(parent);
        super.setTitle("Match Days");
        super.setResultConverter(buttonType -> null);
        super.setResizable(true);

        this.league = league;

        matchDayView = new ListView<>(league.getMatchDays());
        matchDayView.setCellFactory(this::cellFactory);
        final MultipleSelectionModel<? extends LeagueMatchDay> listSelectionModel = matchDayView.getSelectionModel();
        listSelectionModel.setSelectionMode(SelectionMode.SINGLE);
        listSelectionModel.selectedItemProperty().addListener(enableButtons());

        final InputStream arrowImageData = Objects.requireNonNull(getClass().getResourceAsStream("arrow-right.png"));
        final Image arrowImage = new Image(arrowImageData, 32, 32, true, true);

        final ImageView upArrowNode = new ImageView(arrowImage);
        upArrowNode.setRotate(-90.0);
        upButton = new Button();
        upButton.setGraphic(upArrowNode);
        upButton.setOnAction(getReorderHandler(-1));
        upButton.setTooltip(new Tooltip("Move up"));
        upButton.setDisable(true);

        final ImageView downArrowNode = new ImageView(arrowImage);
        downArrowNode.setRotate(90.0);
        downButton = new Button();
        downButton.setGraphic(downArrowNode);
        downButton.setOnAction(getReorderHandler(1));
        downButton.setTooltip(new Tooltip("Move down"));
        downButton.setDisable(true);

        final InputStream trashCanData = Objects.requireNonNull(getClass().getResourceAsStream("trash-can.png"));
        final Image trashCanImage = new Image(trashCanData, 32, 32, true, true);
        final ImageView trashCanNode = new ImageView(trashCanImage);
        trashCanButton = new Button();
        trashCanButton.setGraphic(trashCanNode);
        trashCanButton.setOnAction(getDeleteHandler());
        trashCanButton.setTooltip(new Tooltip("Delete"));
        trashCanButton.setDisable(true);

        final InputStream plusSignData = Objects.requireNonNull(getClass().getResourceAsStream("stack.png"));
        final Image plusSignImage = new Image(plusSignData, 32, 32, true, true);
        final ImageView plusSignNode = new ImageView(plusSignImage);
        plusSignButton = new Button();
        plusSignButton.setGraphic(plusSignNode);
        plusSignButton.setOnAction(getAddHandler());
        plusSignButton.setTooltip(new Tooltip("New"));

        final VBox buttonVBox = new VBox(upButton, plusSignButton, trashCanButton, downButton);
        buttonVBox.setAlignment(Pos.CENTER);

        final BorderPane innerPane = new BorderPane();
        innerPane.setCenter(matchDayView);
        innerPane.setRight(buttonVBox);

        final DialogPane outerPane = super.getDialogPane();
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
                    return;
                }
                setText(matchDay.getName());
            }
        };
    }

    private EventHandler<ActionEvent> getReorderHandler(int delta) {
        return event -> {
            final int oldIndex = matchDayView.getSelectionModel().getSelectedIndex();
            if (oldIndex == -1) return;
            final int newIndex = oldIndex + delta;
            Collections.swap(matchDayView.getItems(), oldIndex, newIndex);
            matchDayView.getSelectionModel().select(newIndex);
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
            if (!matchDay.getGames().isEmpty()) {
                final String confirmation = "This match day has existing games; are you sure you want to delete it?";
                final Alert alert = new Alert(Alert.AlertType.CONFIRMATION, confirmation);
                alert.setTitle("Delete Match Day");
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
