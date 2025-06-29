package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.LeagueTeamData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.Pair;

import java.util.List;

public class NewGameDialog extends Dialog<Pair<String,String>> {
    final List<? extends LeagueTeamData> teams;

    final ChoiceBox<String> awayTeam;
    final ChoiceBox<String> homeTeam;

    public NewGameDialog(Window parent, List<? extends LeagueTeamData> teams) {
        super();
        super.initOwner(parent);
        super.setTitle("Select Teams");
        super.setResultConverter(this::getSelectedItems);

        this.teams = teams;
        final List<String> teamNames = teams.stream().map(LeagueTeamData::getName).toList();
        
        final GridPane innerPane = new GridPane();
        
        innerPane.add(new Label("Away team:"), 0, 0);
        innerPane.add(new Label("Home team:"), 0, 1);
        awayTeam = new ChoiceBox<>();
        awayTeam.getItems().setAll(teamNames);
        homeTeam = new ChoiceBox<>();
        homeTeam.getItems().setAll(teamNames);
        innerPane.add(awayTeam, 1, 0);
        innerPane.add(homeTeam, 1, 1);
        
        final DialogPane outerPane = super.getDialogPane();
        outerPane.setContent(innerPane);
        outerPane.getStylesheets().setAll(parent.getScene().getStylesheets());
        outerPane.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
    }

    public Pair<String,String> getSelectedItems(ButtonType button) {
        if (button == ButtonType.OK) {
            final String awayTeamId = teams.stream()
                    .filter(t -> t.getName().equalsIgnoreCase(awayTeam.getValue()))
                    .findFirst()
                    .map(LeagueTeamData::getId)
                    .orElseThrow(() -> new RuntimeException("Select box error"));
            final String homeTeamId = teams.stream()
                    .filter(t -> t.getName().equalsIgnoreCase(homeTeam.getValue()))
                    .findFirst()
                    .map(LeagueTeamData::getId)
                    .orElseThrow(() -> new RuntimeException("Select box error"));
            return new Pair<>(awayTeamId, homeTeamId);
        } else {
            return null;
        }
    }
}
