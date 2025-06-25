package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.Division;
import com.hitchhikerprod.league.definitions.UFA2025;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StandingsPane implements Activatable {
    private static final StandingsPane INSTANCE = new StandingsPane();

    public static StandingsPane getInstance() {
        return INSTANCE;
    }

    private final HBox root;
    //private final VBox gamesVbox;
    private final TableView<UFA2025.UFAGameData> gamesTable;
    private final VBox divisionsVbox;
    private final List<TableView> teamTables;

    private final ChoiceBox<String> matchDaySelector;

    private StandingsPane() {
        teamTables = new ArrayList<>();

        matchDaySelector = new ChoiceBox<>();
        matchDaySelector.setPrefWidth(200);

        final Button decrementButton = new Button("<");
        final Button incrementButton = new Button(">");
        decrementButton.setOnAction(ev -> matchDaySelector.getSelectionModel().selectPrevious());
        incrementButton.setOnAction(ev -> matchDaySelector.getSelectionModel().selectNext());

        final HBox matchDayHbox = new HBox(decrementButton, matchDaySelector, incrementButton);
        matchDayHbox.setAlignment(Pos.CENTER);
        matchDayHbox.setSpacing(10);

        gamesTable = buildGamePanel();

        final VBox gamesVbox = new VBox(matchDayHbox, gamesTable);
        gamesVbox.setFillWidth(true);
        divisionsVbox = new VBox();
        divisionsVbox.setFillWidth(true);

        root = new HBox(divisionsVbox, gamesVbox);
        root.setFillHeight(true);
    }

    private <T> Callback<TableColumn<T, Double>, TableCell<T, Double>>
    formattingDoubleCellFactory(Class<T> returnType, final String format) {
        return col -> new TableCell<>() {
            @Override
            public void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format(format, item));
            }
        };
    }

    private <T> Callback<TableColumn<T, Integer>, TableCell<T, Integer>>
    formattingNullableIntegerCellFactory(Class<T> returnType) {
        return col -> new TableCell<>() {
            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || Objects.isNull(item)) ? null : item.toString());
            }
        };
    }

    public void setMatchDays(List<String> strings, int index) {
        setMatchDays(strings);
        setSelectedMatchDay(index);
    }

    public void setMatchDays(List<String> strings) {
        matchDaySelector.getItems().clear();
        matchDaySelector.getItems().addAll(strings);
    }

    public int getSelectedMatchDay() {
        return matchDaySelector.getSelectionModel().getSelectedIndex();
    }

    public void setSelectedMatchDay(int index) {
        matchDaySelector.getSelectionModel().select(index);
    }

    public void setMatchDayCallback(EventHandler<ActionEvent> handler) {
        matchDaySelector.setOnAction(handler);
    }

    public void buildDivisionPanels(Map<Division, List<UFA2025.TeamData>> divisions) {
        final ObservableList<Node> children = divisionsVbox.getChildren();
        children.clear();
        teamTables.clear();
        for (Division div : divisions.keySet()) {
            final Label divName = new Label(div.name);
            divName.getStyleClass().add("division-header");
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
            colPct.setCellFactory(formattingDoubleCellFactory(UFA2025.TeamData.class, "%5.3f"));

            TableColumn<UFA2025.TeamData, Integer> colPlusMinus = new TableColumn<>("+/-");
            colPlusMinus.getStyleClass().add("cell-align-right");
            colPlusMinus.setCellValueFactory(new PropertyValueFactory<>("goalDifference"));

            divTable.getColumns().addAll(List.of(colTeam, colWins, colLosses, colPct, colPlusMinus));
            List<UFA2025.TeamData> teams = divisions.get(div);
            divTable.getItems().addAll(teams);

            final int desiredRows = Math.min(8, teams.size());
            divTable.setPrefHeight(36 * desiredRows);
            divTable.setMinWidth(400);
            divTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

            teamTables.add(divTable);
            children.add(divTable);
        }
    }

    public TableView<UFA2025.UFAGameData> buildGamePanel() {
        final TableView<UFA2025.UFAGameData> gamesTable = new TableView<>();
        gamesTable.setEditable(true);

        TableColumn<UFA2025.UFAGameData, String> colAway = new TableColumn<>("Away");

        TableColumn<UFA2025.UFAGameData, String> colAwayTeam = new TableColumn<>("id");
        colAwayTeam.getStyleClass().addAll("cell-align-center", "game-data-cell");
        colAwayTeam.setCellValueFactory(features ->
                new ReadOnlyStringWrapper(features.getValue().getAwayTeam().getShortName()));

        TableColumn<UFA2025.UFAGameData, Integer> colAwayScore = new TableColumn<>("G");
        colAwayScore.setCellFactory(formattingNullableIntegerCellFactory(UFA2025.UFAGameData.class));
        colAwayScore.getStyleClass().addAll("cell-align-center", "game-data-cell");
        colAwayScore.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getAwayScore()));

        TableColumn<UFA2025.UFAGameData, String> colHome = new TableColumn<>("Home");

        TableColumn<UFA2025.UFAGameData, Integer> colHomeScore = new TableColumn<>("G");
        colHomeScore.setCellFactory(formattingNullableIntegerCellFactory(UFA2025.UFAGameData.class));
        colHomeScore.getStyleClass().addAll("cell-align-center", "game-data-cell");
        colHomeScore.setCellValueFactory(features -> new ReadOnlyObjectWrapper<>(features.getValue().getHomeScore()));

        TableColumn<UFA2025.UFAGameData, String> colHomeTeam = new TableColumn<>("id");
        colHomeTeam.getStyleClass().addAll("cell-align-center", "game-data-cell");
        colHomeTeam.setCellValueFactory(features ->
                new ReadOnlyStringWrapper(features.getValue().getHomeTeam().getShortName()));

        colAway.getColumns().addAll(List.of(colAwayTeam, colAwayScore));
        colHome.getColumns().addAll(List.of(colHomeScore, colHomeTeam));
        gamesTable.getColumns().addAll(List.of(colAway, colHome));
        gamesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        return gamesTable;
    }

    public void setGamesList(List<UFA2025.UFAGameData> games) {
        gamesTable.getItems().clear();
        gamesTable.getItems().addAll(games);
        gamesTable.getSortOrder().clear();
    }

    @Override
    public Node asNode() {
        return root;
    }
}
