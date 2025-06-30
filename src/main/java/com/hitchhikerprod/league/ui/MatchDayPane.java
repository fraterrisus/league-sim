package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.LeagueApp;
import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.definitions.League;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
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

        root = new VBox(matchDayHbox, gamesGrid);
        root.setFillWidth(true);
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10));
    }

    /** Post-construction setter to make sure we have a handle to the Application object. */
    public void setApplication(LeagueApp app) {
        this.app = app;
    }

    /** Returns the top-level Node so it can be managed by the parent classes. */
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
    void setRegenerateTablesCallback(EventHandler<ActionEvent> handler) {
        rebuildStandingsHandler = handler;
    }

    /** Rebuilds the Grid full of games for this MatchDay. */
    public void setGamesList(League league, int matchDayIndex) {
        gamesGrid.getChildren().clear();

        int gameIndex = 0;
        for (LeagueGameData game : league.getGames(matchDayIndex)) {
            int colIndex = 0;

            final Label awayTeam = new Label(game.getAwayTeam().getId());
            awayTeam.getStyleClass().addAll("cell-align-center", "font-small");
            gamesGrid.add(awayTeam, colIndex++, gameIndex);

            final List<SimpleObjectProperty<Integer>> scoreProps = new ArrayList<>();
            scoreProps.addAll(game.getAwayScoreProperties());
            scoreProps.addAll(game.getHomeScoreProperties());
            for (SimpleObjectProperty<Integer> scoreProp : scoreProps) {
                final TextField scoreField = new TextField();
                scoreField.setPrefColumnCount(3);
                scoreField.setAlignment(Pos.CENTER);
                scoreField.setEditable(true);
                scoreField.focusedProperty().addListener(this::exitTextBoxHandler);
                Bindings.bindBidirectional(scoreField.textProperty(), scoreProp, new IntegerStringConverter());
                gamesGrid.add(scoreField, colIndex++, gameIndex);
            }

            final Label homeTeam = new Label(game.getHomeTeam().getId());
            homeTeam.getStyleClass().addAll("cell-align-center", "font-small");
            gamesGrid.add(homeTeam, colIndex++, gameIndex);

            gameIndex++;
        }

        setGridConstraints();
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
//            final Node currentFocus = app.getStage().getScene().getFocusOwner();
//            if (!scoreTextFields.contains(currentFocus)) {
//                scoreTextFields.getFirst().requestFocus();
//            }
        }
    }

    /** Helper method to encapsulate setting the layout constraints on the Games grid. */
    private void setGridConstraints() {
        final ObservableList<ColumnConstraints> columnConstraints = gamesGrid.getColumnConstraints();
        columnConstraints.clear();
        final int gridWidth = gamesGrid.getColumnCount();

        final ColumnConstraints awayTeamCol = new ColumnConstraints();
        awayTeamCol.setFillWidth(true);
        awayTeamCol.setHgrow(Priority.ALWAYS);
        awayTeamCol.setHalignment(HPos.RIGHT);
        columnConstraints.add(awayTeamCol);

        for (int i = 1; i < gridWidth - 1; i++) {
            final ColumnConstraints scoreCol = new ColumnConstraints();
            scoreCol.setFillWidth(false);
            scoreCol.setHgrow(Priority.NEVER);
            scoreCol.setHalignment(HPos.CENTER);
            columnConstraints.add(scoreCol);
        }

        final ColumnConstraints homeTeamCol = new ColumnConstraints();
        homeTeamCol.setFillWidth(true);
        homeTeamCol.setHgrow(Priority.ALWAYS);
        homeTeamCol.setHalignment(HPos.LEFT);
        columnConstraints.add(homeTeamCol);
    }
}
