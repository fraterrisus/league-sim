package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.Division;
import com.hitchhikerprod.league.definitions.UFA2025;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StandingsPane implements Activatable {
    private static final StandingsPane INSTANCE = new StandingsPane();

    public static StandingsPane getInstance() {
        return INSTANCE;
    }

    public final VBox vbox;
    public final List<TableView> teamTables;

    private StandingsPane() {
        vbox = new VBox();
        teamTables = new ArrayList<>();
    }

    private Callback<TableColumn<UFA2025.TeamData, Double>, TableCell<UFA2025.TeamData, Double>> formattingDoubleCellFactory(final String format) {
        return col -> new TableCell<>() {
            @Override
            public void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format(format, item));
                getStyleClass().forEach(System.out::println);
                System.out.println();
            }
        };
    }

    public void buildPanels(Map<Division, List<UFA2025.TeamData>> divisions) {
        ObservableList<Node> children = vbox.getChildren();
        children.clear();
        teamTables.clear();
        for (Division div : divisions.keySet()) {
            Label divName = new Label(div.name);
            children.add(divName);

            TableView<UFA2025.TeamData> divTable = new TableView<>();
            divTable.setEditable(false);

            TableColumn<UFA2025.TeamData, String> colTeam = new TableColumn<>("Team");
            colTeam.setCellValueFactory(features -> new ReadOnlyStringWrapper(features.getValue().getFullName()));

            TableColumn<UFA2025.TeamData, Integer> colWins = new TableColumn<>("W");
            colWins.getStyleClass().add("cell-align-center");
            colWins.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getWins()));

            TableColumn<UFA2025.TeamData, Integer> colLosses = new TableColumn<>("L");
            colLosses.getStyleClass().add("cell-align-center");
            colLosses.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getLosses()));

            TableColumn<UFA2025.TeamData, Double> colPct = new TableColumn<>("Pct.");
            colPct.getStyleClass().add("cell-align-right");
            colPct.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getWinPercentage()));
            colPct.setCellFactory(formattingDoubleCellFactory("%5.3f"));

            TableColumn<UFA2025.TeamData, Integer> colPlusMinus = new TableColumn<>("+/-");
            colPlusMinus.getStyleClass().add("cell-align-right");
            colPlusMinus.setCellValueFactory(new PropertyValueFactory<>("goalDifference"));

            divTable.getColumns().addAll(List.of(colTeam, colWins, colLosses, colPct, colPlusMinus));
            List<UFA2025.TeamData> teams = divisions.get(div);
            divTable.getItems().addAll(teams);

            final int desiredRows = Math.min(8, teams.size());
            divTable.setPrefHeight(36 * desiredRows);

            teamTables.add(divTable);
            children.add(divTable);
        }
    }

    @Override
    public Node asNode() {
        return vbox;
    }
}
