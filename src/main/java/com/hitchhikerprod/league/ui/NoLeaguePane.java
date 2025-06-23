package com.hitchhikerprod.league.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

public class NoLeaguePane {
    public final Label label;
    public final ProgressBar progressBar;
    public final VBox vBox;

    public NoLeaguePane() {
        label = new Label("No League file loaded.\nUse File>Open to read a League file.");

        progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(400.0);
        progressBar.setVisible(false);

        vBox = new VBox(label, progressBar);
        vBox.setAlignment(Pos.CENTER);
        vBox.setFillWidth(true);
    }

    public Node asNode() {
        return vBox;
    }

    public Label getLabel() {
        return label;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
