package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.LeagueApp;
import com.hitchhikerprod.league.definitions.UFA2025;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.List;

public class MatchDayPane {
    private static final MatchDayPane INSTANCE  = new MatchDayPane();

    public static MatchDayPane getInstance() {
        return INSTANCE;
    }

    private LeagueApp app;

    private final VBox root;
    private final ChoiceBox<String> matchDaySelector;
    private final GridPane gamesGrid;

    private EventHandler<ActionEvent> rebuildStandingsHandler;

    private final List<Node> scoreTextFields = new ArrayList<>();

    private MatchDayPane() {
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

        root = new VBox(matchDayHbox, gamesGrid);
        root.setFillWidth(true);
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(10);
    }

    public void setApplication(LeagueApp app) {
        this.app = app;
    }

    public Node asNode() {
        return root;
    }

    /** Resets the values in the Match Day dropdown. */
    public void setMatchDays(List<String> strings) {
        matchDaySelector.getItems().clear();
        matchDaySelector.getItems().addAll(strings);
    }

    /** Returns the current value of the Match Day dropdown. */
    public int getSelectedMatchDay() {
        return matchDaySelector.getSelectionModel().getSelectedIndex();
    }

    /** Changes the selector on the Match Day dropdown. */
    public void setSelectedMatchDay(int index) {
        matchDaySelector.getSelectionModel().select(index);
    }

    /** Sets the event handler called when the Match Day dropdown changes. This handler should rebuild both the
     * Divisions pane and the Games list. */
    public void setMatchDayCallback(EventHandler<ActionEvent> handler) {
        matchDaySelector.setOnAction(handler);
    }

    /** Sets the event handler called when the user tabs out of a game score text field. This handler should *only*
     * rebuild the Divisions pane. */
    public void setRegenerateTablesCallback(EventHandler<ActionEvent> handler) {
        rebuildStandingsHandler = handler;
    }

    /** Rebuilds the Grid full of games for this MatchDay. */
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

    /**
     * This handler is only called when the focus exits one of the text boxes in the Games pane, so we should always
     * rebuild the standings. However the result of getFocusOwner() is the *new* focus target; if that's another text
     * field, we don't need to do anything, but if it *isn't*, then we probably tabbed off the end of the pane and we'd
     * like to move the focus back to the first text field. (Unfortunately, we can't tell the difference between Tab
     * and Shift-Tab from here, so winding backwards to the last text field doesn't work.)
     */
    private void exitTextBoxHandler(ObservableValue<? extends Boolean> value, Boolean oldValue, Boolean newValue) {
        if (oldValue == true && newValue == false) {
            rebuildStandingsHandler.handle(null);

            final Node currentFocus = app.getStage().getScene().getFocusOwner();
            if (!scoreTextFields.contains(currentFocus)) {
                scoreTextFields.getFirst().requestFocus();
            }
        }
    }

    /** Helper method to encapsulate setting the layout constraints on the Games grid. */
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
}
