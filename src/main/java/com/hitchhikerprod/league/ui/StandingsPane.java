package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.Division;
import com.hitchhikerprod.league.definitions.UFA2025;
import javafx.beans.binding.Bindings;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StandingsPane implements Activatable {
    private static final StandingsPane INSTANCE = new StandingsPane();

    public static StandingsPane getInstance() {
        return INSTANCE;
    }

    public final HBox root;
    private final VBox leftVbox;
    private final VBox rightVbox;
    private final List<TableView> teamTables;

    private final ChoiceBox<String> matchDaySelector;
    private final Button recalcButton;

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

        recalcButton = new Button("Regenerate");
        recalcButton.setAlignment(Pos.CENTER);

        rightVbox = new VBox(matchDayHbox, recalcButton);
        rightVbox.setFillWidth(true);
        rightVbox.setSpacing(10);

        leftVbox = new VBox();
        leftVbox.setFillWidth(true);
        leftVbox.setSpacing(10);

        root = new HBox(leftVbox, rightVbox);
        HBox.setHgrow(leftVbox, Priority.NEVER);
        HBox.setHgrow(rightVbox, Priority.ALWAYS);
        root.setFillHeight(true);
    }

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
        recalcButton.setOnAction(handler);
    }

    public void buildDivisionPanels(Map<Division, List<UFA2025.TeamData>> divisions) {
        final ObservableList<Node> children = leftVbox.getChildren();
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
            colPct.setCellFactory(formattingDoubleCellFactory("%5.3f"));

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

    public void buildGamesPanel(List<UFA2025.UFAGameData> games) {
        final ObservableList<Node> children = rightVbox.getChildren();
        // Leave the matchday selector Hbox (first) and the Recalculate button (last)
        children.remove(1, children.size() - 1);

        for (UFA2025.UFAGameData game : games) {
            final Label awayTeam = new Label(game.getAwayTeam().getShortName());
            awayTeam.setAlignment(Pos.CENTER);
            awayTeam.getStyleClass().addAll("cell-align-center", "font-small");

            final Label homeTeam = new Label(game.getHomeTeam().getShortName());
            homeTeam.setAlignment(Pos.CENTER);
            homeTeam.getStyleClass().addAll("cell-align-center", "font-small");

            final TextField awayScore = new TextField();
            awayScore.setPrefColumnCount(3);
            awayScore.setAlignment(Pos.CENTER);
            awayScore.setEditable(true);
            Bindings.bindBidirectional(awayScore.textProperty(), game.getAwayScoreProperty(), new IntegerStringConverter());

            final TextField homeScore = new TextField();
            homeScore.setPrefColumnCount(3);
            homeScore.setAlignment(Pos.CENTER);
            homeScore.setEditable(true);
            Bindings.bindBidirectional(homeScore.textProperty(), game.getHomeScoreProperty(), new IntegerStringConverter());

            final HBox gameRow = new HBox(awayTeam, awayScore, homeScore, homeTeam);
            //gameRow.getStyleClass().add("debug");
            gameRow.setSpacing(5);
            gameRow.setAlignment(Pos.CENTER);
            children.add(children.size() - 1, gameRow);
        }
    }

    public void setGamesList(List<UFA2025.UFAGameData> games) {
        buildGamesPanel(games);
    }

    @Override
    public Node asNode() {
        return root;
    }
}
