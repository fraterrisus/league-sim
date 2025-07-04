package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.LeagueMatchDay;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

public class EditMatchDaysDialog extends Dialog<Void> {
    final ListView<? extends LeagueMatchDay> matchDayView;

    public EditMatchDaysDialog(Window parent, ObservableList<? extends LeagueMatchDay> matchDays) {
        super();
        super.initOwner(parent);
        super.setTitle("Match Days");
        super.setResultConverter(buttonType -> null);
        super.setResizable(true);

        matchDayView = new ListView<>(matchDays);
        matchDayView.setCellFactory(this::cellFactory);
        matchDayView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final InputStream arrowImageData = Objects.requireNonNull(getClass().getResourceAsStream("arrow-right.png"));
        final Image arrowImage = new Image(arrowImageData, 32, 32, true, true);

        final ImageView upArrowNode = new ImageView(arrowImage);
        upArrowNode.setRotate(-90.0);
        final Button upButton = new Button();
        upButton.setGraphic(upArrowNode);
        upButton.setOnAction(getReorderHandler(-1));

        final ImageView downArrowNode = new ImageView(arrowImage);
        downArrowNode.setRotate(90.0);
        final Button downButton = new Button();
        downButton.setGraphic(downArrowNode);
        downButton.setOnAction(getReorderHandler(1));

        final VBox buttonVBox = new VBox(upButton, upArrowNode, downArrowNode, downButton);
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
}
