package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.LeagueApp;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;

public class TeamContextMenu {
    private final ContextMenu root;

    public TeamContextMenu(LeagueApp app, TableView<LeagueTeamData> parent) {
        root = new ContextMenu();

        final MenuItem showGamesMI = new MenuItem("Games");
        showGamesMI.setOnAction(event -> app.contextMenuShowGames(parent));

        root.getItems().add(showGamesMI);
    }

    public ContextMenu asContextMenu() {
        return root;
    }
}
