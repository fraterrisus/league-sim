package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.LeagueController;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public class RootWindow {
    public static final RootWindow INSTANCE = new RootWindow();

    public static RootWindow getInstance() {
        return INSTANCE;
    }

    public final VBox vbox;
    public final MenuBar menuBar;

    public final NoLeaguePane noLeaguePane;

    private OpenWindow openWindow;

    private RootWindow() {
        vbox = new VBox();
        vbox.setFillWidth(true);
        vbox.setPrefWidth(600.0);

        menuBar = MenuBar.getInstance();
        noLeaguePane = NoLeaguePane.getInstance();
        vbox.getChildren().addAll(menuBar.asNode(), noLeaguePane.asNode());

        openWindow = OpenWindow.NO_LEAGUE;
    }

    public void setController(LeagueController controller) {
        menuBar.setController(controller);
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
                noLeaguePane.asNode()
        );
        visiblePanes.add(switch (desired) {
            case NO_LEAGUE -> noLeaguePane.asNode();
            case STANDINGS -> null;
        });
    }

    public enum OpenWindow {
        NO_LEAGUE, STANDINGS;
    }
}
