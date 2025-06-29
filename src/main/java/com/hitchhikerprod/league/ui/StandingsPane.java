package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.League;
import com.hitchhikerprod.league.LeagueApp;
import com.hitchhikerprod.league.beans.LeagueColumn;
import com.hitchhikerprod.league.beans.LeagueDivision;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class StandingsPane {
    private static final StandingsPane INSTANCE = new StandingsPane();

    public static StandingsPane getInstance() {
        return INSTANCE;
    }

    private LeagueApp app;
    private final VBox root;

    private StandingsPane() {
        final TableView<LeagueTeamData> divTable = new TableView<>();
        divTable.setEditable(false);

        root = new VBox(divTable);
        root.setFillWidth(true);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
    }

    /** Post-construction setter to make sure we have a handle to the Application object. */
    public void setApplication(LeagueApp app) {
        this.app = app;
    }

    /** Returns the top-level Node so it can be managed by the parent classes. */
    public Node asNode() {
        return root;
    }

    /** Rebuilds the TableViews inside the Divisions pane. Should only be called when a new League is loaded
     * (or if we eventually add "create" functionality). */
    public void buildDivisionsPane(League league, int matchDayIdx) {
        final ObservableList<Node> children = root.getChildren();
        children.clear();

        for (LeagueDivision div : league.getDivisionTables(matchDayIdx).keySet()) {
            final Label divName = new Label(div.getName());
            divName.getStyleClass().add("division-header");
            children.add(divName);

            final TableView<LeagueTeamData> divTable = new TableView<>();
            divTable.setEditable(false);
            divTable.setMinWidth(400);
            divTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            divTable.setContextMenu(new TeamContextMenu(app, divTable).asContextMenu());
            VBox.setVgrow(divTable, Priority.ALWAYS);

            final ObservableList<TableColumn<LeagueTeamData, ?>> columns = divTable.getColumns();

            final TableColumn<LeagueTeamData, String> colTeam = new TableColumn<>("Team");
            colTeam.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getName()));
            columns.add(colTeam);

            columns.addAll(league.getDivisionColumns().stream()
                    .map(LeagueColumn::getColumn)
                    .toList());

            children.add(divTable);
        }
    }

    /** Rebuilds the *contents* of the Divisions tables. */
    public void setStandings(League league, int matchDayIdx) {
        final Map<? extends LeagueDivision, List<? extends LeagueTeamData>> divisions =
                league.getDivisionTables(matchDayIdx);
        final ObservableList<Node> children = root.getChildren();
        int i = 0;
        while (i < children.size()) {
            final Label divLabel = (Label)children.get(i);
            final TableView<LeagueTeamData> divTable = (TableView<LeagueTeamData>)children.get(i + 1);
            i += 2;
            divisions.entrySet().stream()
                    .filter(e -> e.getKey().getName().equals(divLabel.getText()))
                    .findFirst()
                    .ifPresent(entry -> {
                        final List<? extends LeagueTeamData> teams = entry.getValue();
                        final ObservableList<LeagueTeamData> items = divTable.getItems();
                        items.clear();
                        items.addAll(teams);
                        final int desiredRows = Math.min(8, teams.size());
                        divTable.setPrefHeight(32 * (desiredRows + 1));
                        divTable.getSortOrder().clear();
                    });
        }
    }
}
