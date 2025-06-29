package com.hitchhikerprod.league.ui;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class StatusBar {
    private static final StatusBar INSTANCE = new StatusBar();

    public static StatusBar getInstance() {
        return INSTANCE;
    }

    public final Label label;
    public final ProgressBar progressBar;
    public final HBox hBox;

    private StatusBar() {
        label = new Label("Starting up. Please wait...");
        label.setMinWidth(400);
        label.setPrefWidth(600);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.getStyleClass().add("text-blue");
        label.setTextOverrun(OverrunStyle.CLIP);

        progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(200.0);
        HBox.setHgrow(progressBar, Priority.NEVER);

        hBox = new HBox(label);
        hBox.setAlignment(Pos.CENTER_LEFT);
    }

    public Node asNode() {
        return hBox;
    }

    public Label getLabel() {
        return label;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void progressBarVisible(boolean visible) {
        final ObservableList<Node> children = hBox.getChildren();
        if (visible && !children.contains(progressBar)) {
            children.add(progressBar);
        } else if (!visible) {
            children.remove(progressBar);
        }
    }
}
