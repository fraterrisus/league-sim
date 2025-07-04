package com.hitchhikerprod.league.ui;

import javafx.beans.Observable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Pair;

public class NewTeamDialog extends Dialog<Pair<String,String>> {
    private final DialogPane outerPane;
    private final TextField longNameBox;
    private final TextField shortNameBox;

    public NewTeamDialog(Window parent) {
        super();
        super.initOwner(parent);
        super.setTitle("New Team");
        super.setResultConverter(this::getValues);

        final GridPane innerPane = new GridPane();
        innerPane.setHgap(10);
        innerPane.setVgap(10);

        innerPane.add(new Label("Full name:"), 0, 0);
        innerPane.add(new Label("Abbreviation:"), 0, 1);

        longNameBox = new TextField();
        longNameBox.setPrefColumnCount(20);
        longNameBox.textProperty().addListener(this::enableOkButton);
        innerPane.add(longNameBox, 1, 0);

        shortNameBox = new TextField();
        shortNameBox.setPrefColumnCount(4);
        shortNameBox.textProperty().addListener(this::enableOkButton);
        innerPane.add(shortNameBox, 1, 1);

        outerPane = super.getDialogPane();
        outerPane.setContent(innerPane);
        outerPane.getStylesheets().setAll(parent.getScene().getStylesheets());
        outerPane.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
        outerPane.lookupButton(ButtonType.OK).setDisable(true);
    }

    private void enableOkButton(Observable observable, String oldValue, String newValue) {
        final boolean disable = longNameBox.getText().isEmpty() || shortNameBox.getText().isEmpty();
        outerPane.lookupButton(ButtonType.OK).setDisable(disable);
    }

    private Pair<String, String> getValues(ButtonType button) {
        if (button == ButtonType.OK) {
            return new Pair<>(longNameBox.getText(), shortNameBox.getText());
        } else {
            return null;
        }
    }
}
