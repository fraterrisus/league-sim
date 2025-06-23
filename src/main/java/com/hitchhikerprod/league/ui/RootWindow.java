package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.LeagueController;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public class RootWindow {
    public final VBox vbox;
    public final MenuBar menuBar;

    public final NoLeaguePane noLeaguePane;

    private OpenWindow openWindow;

    public RootWindow(LeagueController controller) {
        vbox = new VBox();
        vbox.setFillWidth(true);
        vbox.setPrefWidth(600.0);

        menuBar = new MenuBar(controller);
        noLeaguePane = new NoLeaguePane();
        vbox.getChildren().addAll(menuBar.asNode(), noLeaguePane.asNode());
        openWindow = OpenWindow.NO_LEAGUE;
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

    public enum OpenWindow {
        NO_LEAGUE;
    }
}
