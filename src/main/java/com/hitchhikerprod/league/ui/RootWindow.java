package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.LeagueApp;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class RootWindow {
    public static final RootWindow INSTANCE = new RootWindow();

    public static RootWindow getInstance() {
        return INSTANCE;
    }

    private final BorderPane pane;

    private RootWindow() {
        pane = new BorderPane();
        pane.setTop(MenuBar.getInstance().asNode());
        pane.setLeft(StandingsPane.getInstance().asNode());
        pane.setCenter(MatchDayPane.getInstance().asNode());
        pane.setBottom(StatusBar.getInstance().asNode());
    }

    public void setApplication(LeagueApp app) {
        MenuBar.getInstance().setApplication(app);
        MatchDayPane.getInstance().setApplication(app);
        StandingsPane.getInstance().setApplication(app);
    }

    public Parent asParent() {
        return pane;
    }

    public void setStatusMessage(String message) {
        StatusBar.getInstance().label.setText(message);
    }

    public void setStatusMessage(String message, boolean showProgressBar) {
        setStatusMessage(message);
        StatusBar.getInstance().progressBarVisible(showProgressBar);
    }

    public void setRegenerateTablesCallback(EventHandler<ActionEvent> handler) {
        MatchDayPane.getInstance().setRegenerateTablesCallback(handler);
        TeamGamesWindow.getInstance().setRegenerateTablesCallback(handler);
    }

    public DoubleProperty getProgressProperty() {
        return StatusBar.getInstance().getProgressBar().progressProperty();
    }

    public StringProperty getStatusProperty() {
        return StatusBar.getInstance().label.textProperty();
    }

    public void allowSave() {
        MenuBar.getInstance().allowSave();
    }
}
