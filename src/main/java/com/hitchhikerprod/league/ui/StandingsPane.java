package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.LeagueApp;
import com.hitchhikerprod.league.beans.RawDivision;
import com.hitchhikerprod.league.definitions.UFA2025;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

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

        final TableView<UFA2025.TeamData> divTable = new TableView<>();
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
    public void buildDivisionsPane(Map<RawDivision, List<UFA2025.TeamData>> divisions) {
        final ObservableList<Node> children = root.getChildren();
        children.clear();

        for (RawDivision div : divisions.keySet()) {
            final Label divName = new Label(div.name);
            divName.getStyleClass().add("division-header");
            children.add(divName);

            final TableView<UFA2025.TeamData> divTable = new TableView<>();
            divTable.setEditable(false);

            final TableColumn<UFA2025.TeamData, String> colTeam = new TableColumn<>("Team");
            colTeam.setCellValueFactory(features -> new ReadOnlyStringWrapper(features.getValue().getName()));

            final TableColumn<UFA2025.TeamData, Integer> colWins = new TableColumn<>("W");
            colWins.getStyleClass().add("cell-align-center");
            colWins.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getWins()));

            final TableColumn<UFA2025.TeamData, Integer> colLosses = new TableColumn<>("L");
            colLosses.getStyleClass().add("cell-align-center");
            colLosses.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getLosses()));

            final TableColumn<UFA2025.TeamData, Double> colPct = new TableColumn<>("Pct.");
            colPct.getStyleClass().add("cell-align-right");
            colPct.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getWinPercentage()));
            colPct.setCellFactory(formattingDoubleCellFactory("%5.3f"));

            final TableColumn<UFA2025.TeamData, Integer> colPlusMinus = new TableColumn<>("+/-");
            colPlusMinus.getStyleClass().add("cell-align-right");
            colPlusMinus.setCellValueFactory(new PropertyValueFactory<>("goalDifference"));

            divTable.getColumns().addAll(List.of(colTeam, colWins, colLosses, colPct, colPlusMinus));
            divTable.setMinWidth(400);
            divTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
            VBox.setVgrow(divTable, Priority.ALWAYS);

            children.add(divTable);
        }
    }

    /** Rebuilds the *contents* of the Divisions tables. */
    public void setStandings(Map<RawDivision, List<UFA2025.TeamData>> divisions) {
        final ObservableList<Node> children = root.getChildren();
        int i = 0;
        while (i < children.size()) {
            final Label divLabel = (Label)children.get(i);
            final TableView<UFA2025.TeamData> divTable = (TableView<UFA2025.TeamData>)children.get(i + 1);
            i += 2;
            divisions.entrySet().stream()
                    .filter(e -> e.getKey().name.equals(divLabel.getText()))
                    .findFirst()
                    .ifPresent(entry -> {
                        final List<UFA2025.TeamData> teams = entry.getValue();
                        final ObservableList<UFA2025.TeamData> items = divTable.getItems();
                        items.clear();
                        items.addAll(teams);
                        final int desiredRows = Math.min(8, teams.size());
                        divTable.setPrefHeight(36 * desiredRows);
                        divTable.getSortOrder().clear();
                    });
        }
    }


    /**
     * Returns a CellFactory implementation (for passing into #SetCellFactory) that formats the Double value according
     * to the format argument. */
    private Callback<TableColumn<UFA2025.TeamData, Double>, TableCell<UFA2025.TeamData, Double>>
    formattingDoubleCellFactory(final String format) {
        return col -> new TableCell<>() {
            @Override
            public void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format(format, item));
            }
        };
    }
}
