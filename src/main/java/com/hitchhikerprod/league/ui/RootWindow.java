package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.LeagueApp;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RootWindow {
    public static final RootWindow INSTANCE = new RootWindow();

    public static RootWindow getInstance() {
        return INSTANCE;
    }

    public final VBox vbox;
    public final MenuBar menuBar;

    public final NoLeaguePane noLeaguePane;
    public final StandingsPane standingsPane;

    private OpenWindow openWindow;

    private RootWindow() {
        vbox = new VBox();
        vbox.setFillWidth(true);

        menuBar = MenuBar.getInstance();
        noLeaguePane = NoLeaguePane.getInstance();
        standingsPane = StandingsPane.getInstance();
        vbox.getChildren().addAll(menuBar.asNode(), noLeaguePane.asNode());

        openWindow = OpenWindow.NO_LEAGUE;
    }

    public void setApplication(LeagueApp app) {
        menuBar.setApplication(app);
        standingsPane.setApplication(app);
    }

    public Parent asParent() {
        return vbox;
    }

    public OpenWindow getOpenWindow() {
        return openWindow;
    }

    public void checkOpenWindow(OpenWindow desired) {
        if (openWindow != desired) {
            throw new RuntimeException("Unexpected open window " + openWindow + ", expected " + desired);
        }
    }

    public void activate(OpenWindow desired) {
        if (desired == openWindow) return;

        ObservableList<Node> visiblePanes = vbox.getChildren();
        visiblePanes.removeAll(
                noLeaguePane.asNode(),
                standingsPane.asNode()
        );
        Node desiredPane = switch (desired) {
            case NO_LEAGUE -> noLeaguePane.asNode();
            case STANDINGS -> standingsPane.asNode();
        };
        visiblePanes.add(desiredPane);
        VBox.setVgrow(desiredPane, Priority.ALWAYS);
    }

    public enum OpenWindow {
        NO_LEAGUE, STANDINGS;
    }
}
