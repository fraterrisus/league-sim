package com.hitchhikerprod.league.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

public class NoLeaguePane implements Activatable {
    private static final NoLeaguePane INSTANCE = new NoLeaguePane();

    public static NoLeaguePane getInstance() {
        return INSTANCE;
    }

    public final Label label;
    public final ProgressBar progressBar;
    public final VBox vBox;

    private NoLeaguePane() {
        label = new Label("Starting up.\nPlease wait...");
        label.getStyleClass().add("text-blue");

        progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(400.0);
        progressBar.setVisible(false);

        vBox = new VBox(label, progressBar);
        vBox.setAlignment(Pos.CENTER);
        vBox.setFillWidth(true);
    }

    @Override
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
