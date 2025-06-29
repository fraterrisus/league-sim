package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.LeagueGameData;
import com.hitchhikerprod.league.beans.LeagueMatchDay;
import com.hitchhikerprod.league.beans.LeagueTeamData;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.List;

public class TeamGamesWindow {
    private static final TeamGamesWindow INSTANCE = new TeamGamesWindow();

    public static TeamGamesWindow getInstance() {
        return INSTANCE;
    }

    private final Stage stage;
    private final VBox root;
    private final GridPane gamesGrid;

    private EventHandler<ActionEvent> rebuildStandingsHandler;

    private TeamGamesWindow() {
        this.stage = new Stage();
        stage.setMinWidth(150);
        stage.setMinHeight(100);
        stage.setWidth(Control.USE_PREF_SIZE);
        stage.setHeight(Control.USE_PREF_SIZE);

        gamesGrid = new GridPane();
        gamesGrid.setHgap(10);
        gamesGrid.setVgap(5);
        gamesGrid.setMinWidth(Control.USE_PREF_SIZE);
        gamesGrid.add(new Label("No games loaded."), 0, 0);
        setGridConstraints();

        final Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("font-medium");
        closeButton.setOnAction(event -> hide());

        root = new VBox(gamesGrid, closeButton);
        root.setSpacing(10);
        root.setAlignment(Pos.CENTER_RIGHT);
        root.setPadding(new Insets(20, 20, 20, 20));
        root.setMinSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
    }

    public void setStylesheets(String path) {
        root.getStylesheets().add(path);
    }

    public void show() {
        // Why does this work? Great question.
        // There's some indication online that setting width/height below the minimum prevents a bug where the window
        // doesn't resize correctly. It still doesn't make any sense.
        stage.setWidth(50);
        stage.setHeight(50);
        stage.show();
        stage.sizeToScene();
    }

    public void hide() {
        stage.hide();
    }

    public void resize() {
        stage.sizeToScene();
    }

    /** Sets the event handler called when the user tabs out of a game score text field. This handler should *only*
     * rebuild the Divisions pane. */
    void setRegenerateTablesCallback(EventHandler<ActionEvent> handler) {
        rebuildStandingsHandler = handler;
    }

    public void setGames(League league, LeagueTeamData team) {
        gamesGrid.getChildren().clear();

        int gameIndex = 0;
        for (LeagueMatchDay matchDay : league.getGames(team)) {
            for (LeagueGameData game : matchDay.getGames()) {
                int colIndex = 0;

                final Label awayTeam = new Label(game.getAwayTeam().getId());
                awayTeam.getStyleClass().addAll("cell-align-center", "font-small");
                awayTeam.setMinWidth(Control.USE_PREF_SIZE);
                gamesGrid.add(awayTeam, colIndex++, gameIndex);

                final List<SimpleObjectProperty<Integer>> scoreProps = new ArrayList<>();
                scoreProps.addAll(game.getAwayScoreProperties());
                scoreProps.addAll(game.getHomeScoreProperties());
                for (SimpleObjectProperty<Integer> scoreProp : scoreProps) {
                    final TextField scoreField = new TextField();
                    scoreField.setPrefColumnCount(3);
                    scoreField.setMinWidth(Control.USE_PREF_SIZE);
                    scoreField.setAlignment(Pos.CENTER);
                    scoreField.setEditable(true);
                    scoreField.focusedProperty().addListener(this::exitTextBoxHandler);
                    Bindings.bindBidirectional(scoreField.textProperty(), scoreProp, new IntegerStringConverter());
                    gamesGrid.add(scoreField, colIndex++, gameIndex);
                }

                final Label homeTeam = new Label(game.getHomeTeam().getId());
                homeTeam.getStyleClass().addAll("cell-align-center", "font-small");
                homeTeam.setMinWidth(Control.USE_PREF_SIZE);
                gamesGrid.add(homeTeam, colIndex, gameIndex);

                gameIndex++;
            }
        }

        stage.setTitle(team.getName());
    }

    private void exitTextBoxHandler(ObservableValue<? extends Boolean> value, Boolean oldValue, Boolean newValue) {
        if (oldValue == true && newValue == false) {
            rebuildStandingsHandler.handle(null);
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
