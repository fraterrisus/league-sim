package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.LeagueApp;
import com.hitchhikerprod.league.beans.Division;
import com.hitchhikerprod.league.definitions.UFA2025;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
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

    private LeagueApp app;
    public final HBox root;
    private final VBox leftVbox;
    private final VBox rightVbox;
    private final GridPane gamesGrid;

    private final ChoiceBox<String> matchDaySelector;

    private final List<Node> scoreTextFields = new ArrayList<>();
    private EventHandler<ActionEvent> rebuildStandingsHandler;

    private StandingsPane() {
        matchDaySelector = new ChoiceBox<>();
        matchDaySelector.setPrefWidth(200);

        final Button decrementButton = new Button("<");
        final Button incrementButton = new Button(">");
        decrementButton.setOnAction(ev -> matchDaySelector.getSelectionModel().selectPrevious());
        incrementButton.setOnAction(ev -> matchDaySelector.getSelectionModel().selectNext());

        final HBox matchDayHbox = new HBox(decrementButton, matchDaySelector, incrementButton);
        matchDayHbox.setAlignment(Pos.CENTER);
        matchDayHbox.setSpacing(10);

        gamesGrid = new GridPane();
        gamesGrid.setHgap(10);
        gamesGrid.setVgap(5);
        setGridConstraints();
        
        rightVbox = new VBox(matchDayHbox, gamesGrid);
        rightVbox.setFillWidth(true);
        rightVbox.setAlignment(Pos.TOP_CENTER);
        rightVbox.setSpacing(10);

        leftVbox = new VBox();
        leftVbox.setFillWidth(true);
        leftVbox.setAlignment(Pos.CENTER);
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

    private void exitTextBoxHandler(ObservableValue<? extends Boolean> value, Boolean oldValue, Boolean newValue) {
        final Node currentFocus = app.getStage().getScene().getFocusOwner();
        if (oldValue == true && newValue == false && scoreTextFields.contains(currentFocus)) {
            rebuildStandingsHandler.handle(null);
        }
    }

    public void setApplication(LeagueApp app) {
        this.app = app;
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

    public void setRegenerateTablesCallback(EventHandler<ActionEvent> handler) {
        rebuildStandingsHandler = handler;
    }

    public void setDivisions(Map<Division, List<UFA2025.TeamData>> divisions) {
        final ObservableList<Node> children = leftVbox.getChildren();
        children.clear();
        for (Division div : divisions.keySet()) {
            final Label divName = new Label(div.name);
            divName.getStyleClass().add("division-header");
            children.add(divName);

            final TableView<UFA2025.TeamData> divTable = new TableView<>();
            divTable.setEditable(false);

            final TableColumn<UFA2025.TeamData, String> colTeam = new TableColumn<>("Team");
            colTeam.setCellValueFactory(features -> new ReadOnlyStringWrapper(features.getValue().getFullName()));

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

    public void setStandings(Map<Division, List<UFA2025.TeamData>> divisions) {
        final ObservableList<Node> children = leftVbox.getChildren();
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
                    });
        }
    }

    public void setGamesList(List<UFA2025.UFAGameData> games) {
        gamesGrid.getChildren().clear();
        scoreTextFields.clear();

        int gameIndex = 0;
        for (UFA2025.UFAGameData game : games) {
            final Label awayTeam = new Label(game.getAwayTeam().getShortName());
            awayTeam.getStyleClass().addAll("cell-align-center", "font-small");

            final Label homeTeam = new Label(game.getHomeTeam().getShortName());
            homeTeam.getStyleClass().addAll("cell-align-center", "font-small");

            final TextField awayScore = new TextField();
            awayScore.setPrefColumnCount(3);
            awayScore.setAlignment(Pos.CENTER);
            awayScore.setEditable(true);
            awayScore.focusedProperty().addListener(this::exitTextBoxHandler);
            Bindings.bindBidirectional(awayScore.textProperty(), game.getAwayScoreProperty(), new IntegerStringConverter());
            scoreTextFields.add(awayScore);

            final TextField homeScore = new TextField();
            homeScore.setPrefColumnCount(3);
            homeScore.setAlignment(Pos.CENTER);
            homeScore.setEditable(true);
            homeScore.focusedProperty().addListener(this::exitTextBoxHandler);
            Bindings.bindBidirectional(homeScore.textProperty(), game.getHomeScoreProperty(), new IntegerStringConverter());
            scoreTextFields.add(homeScore);

            gamesGrid.addRow(gameIndex, awayTeam, awayScore, homeScore, homeTeam);
            gameIndex++;
        }
    }

    private void setGridConstraints() {
        final ObservableList<ColumnConstraints> columnConstraints = gamesGrid.getColumnConstraints();

        final ColumnConstraints column0 = new ColumnConstraints();
        column0.setFillWidth(true);
        column0.setHgrow(Priority.ALWAYS);
        column0.setHalignment(HPos.RIGHT);

        final ColumnConstraints column1 = new ColumnConstraints();
        column1.setFillWidth(false);
        column1.setHgrow(Priority.NEVER);
        column1.setHalignment(HPos.CENTER);

        final ColumnConstraints column2 = new ColumnConstraints();
        column2.setFillWidth(false);
        column2.setHgrow(Priority.NEVER);
        column2.setHalignment(HPos.CENTER);

        final ColumnConstraints column3 = new ColumnConstraints();
        column3.setFillWidth(true);
        column3.setHgrow(Priority.ALWAYS);
        column3.setHalignment(HPos.LEFT);

        columnConstraints.addAll(column0, column1, column2, column3);
    }
    
    @Override
    public Node asNode() {
        return root;
    }
}
