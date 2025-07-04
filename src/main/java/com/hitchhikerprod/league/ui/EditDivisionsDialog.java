package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.LeagueDivision;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.definitions.League;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

import static com.hitchhikerprod.league.ui.AbstractListDialog.getReorderHandler;
import static com.hitchhikerprod.league.ui.AbstractListDialog.newButton;

public class EditDivisionsDialog extends Dialog<Void> {
    private final Window parent;
    private final League league;

    private final ListView<? extends LeagueDivision> divisionList;
    private ListView<? extends LeagueTeamData> teamList;

    private final Button divUpButton;
    private final Button divDownButton;
    private final Button divNewButton;
    private final Button divDeleteButton;

    private final Button teamNewButton;
    private final Button teamDeleteButton;

    private final HBox innerPane;

    public EditDivisionsDialog(Window parent, League league) {
        super();
        super.initOwner(parent);
        super.setResultConverter(buttonType -> null);
        super.setResizable(true);
        super.setTitle("Edit Divisions");

        this.parent = parent;
        this.league = league;

        divisionList = new ListView<>(league.getDivisions());
        divisionList.setCellFactory(CellFactories::nameCellFactory);
        final MultipleSelectionModel<? extends LeagueDivision> divSelectionModel = divisionList.getSelectionModel();
        divSelectionModel.setSelectionMode(SelectionMode.SINGLE);
        divSelectionModel.selectedItemProperty().addListener(this::getDivisionSelectionHandler);

        teamList = new ListView<>();
        teamList.setCellFactory(CellFactories::nameCellFactory); // FIXME
        final MultipleSelectionModel<? extends LeagueTeamData> teamSelectionModel = teamList.getSelectionModel();
        teamSelectionModel.setSelectionMode(SelectionMode.SINGLE);
        teamSelectionModel.selectedItemProperty().addListener(this::getTeamSelectionHandler);

        final InputStream arrowImageData = Objects.requireNonNull(getClass().getResourceAsStream("arrow-right.png"));
        final Image arrowImage = new Image(arrowImageData, 32, 32, true, true);

        final InputStream trashCanData = Objects.requireNonNull(getClass().getResourceAsStream("trash-can.png"));
        final Image trashCanImage = new Image(trashCanData, 32, 32, true, true);

        final InputStream plusSignData = Objects.requireNonNull(getClass().getResourceAsStream("stack.png"));
        final Image plusSignImage = new Image(plusSignData, 32, 32, true, true);

        divUpButton = newButton(arrowImage, -90, "Move up");
        divUpButton.setDisable(true);
        divUpButton.setOnAction(getReorderHandler(divisionList, -1));

        divDownButton = newButton(arrowImage, 90, "Move down");
        divDownButton.setDisable(true);
        divDownButton.setOnAction(getReorderHandler(divisionList, 1));

        divNewButton = newButton(plusSignImage, 0, "New");
        divNewButton.setOnAction(getDivisionAddHandler());

        divDeleteButton = newButton(trashCanImage, 0, "Delete");
        divDeleteButton.setDisable(true);
        divDeleteButton.setOnAction(getDivisionDeleteHandler());

        final VBox divButtons = new VBox(divUpButton, divNewButton, divDeleteButton, divDownButton);
        divButtons.setAlignment(Pos.CENTER);

        teamNewButton = newButton(plusSignImage, 0, "New");
        teamNewButton.setDisable(true);
        teamNewButton.setOnAction(getTeamAddHandler());

        teamDeleteButton = newButton(trashCanImage, 0, "Delete");
        teamDeleteButton.setDisable(true);
        teamDeleteButton.setOnAction(getTeamDeleteHandler());

        final VBox teamButtons = new VBox(teamNewButton, teamDeleteButton);
        teamButtons.setAlignment(Pos.CENTER);

        innerPane = new HBox(divisionList, divButtons, teamList, teamButtons);

        final DialogPane outerPane = super.getDialogPane();
        outerPane.setContent(innerPane);
        outerPane.getStylesheets().setAll(parent.getScene().getStylesheets());
        outerPane.getButtonTypes().setAll(ButtonType.OK);
    }

    private <D extends LeagueDivision> void getDivisionSelectionHandler(Observable observable, D oldValue, D newValue) {
        final boolean disable = Objects.isNull(newValue);
        divUpButton.setDisable(disable);
        divDownButton.setDisable(disable);
        divDeleteButton.setDisable(disable);
        teamNewButton.setDisable(disable);
        rebuildTeamList(newValue.getObservableTeams());
    }

    private void rebuildTeamList(ObservableList<? extends LeagueTeamData> teams) {
        teamList = new ListView<>(teams);
        teamList.setCellFactory(CellFactories::nameCellFactory);
        final MultipleSelectionModel<? extends LeagueTeamData> teamSelectionModel = teamList.getSelectionModel();
        teamSelectionModel.setSelectionMode(SelectionMode.SINGLE);
        teamSelectionModel.selectedItemProperty().addListener(this::getTeamSelectionHandler);
        final ObservableList<Node> children = innerPane.getChildren();
        children.remove(2);
        children.add(2, teamList);
    }

    private <T extends LeagueTeamData> void getTeamSelectionHandler(Observable observable, T oldValue, T newValue) {

    }

    private EventHandler<ActionEvent> getDivisionAddHandler() {
        return event -> {
            final int index = divisionList.getSelectionModel().getSelectedIndex();
            final TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("New Division");
            nameDialog.setHeaderText("New division name:");
            final Optional<String> newName = nameDialog.showAndWait();
            newName.ifPresent(name -> {
                if (index == -1) league.addDivision(name);
                else league.addDivision(index, name);
            });
        };
    }

    private EventHandler<ActionEvent> getDivisionDeleteHandler() {
        return event -> {};
    }

    private EventHandler<ActionEvent> getTeamAddHandler() {
        return event ->
            new NewTeamDialog(parent).showAndWait()
                    .ifPresent(newTeam -> league.createTeam(
                            divisionList.getSelectionModel().getSelectedIndex(),
                            newTeam.getKey(), newTeam.getValue()));
    }

    private EventHandler<ActionEvent> getTeamDeleteHandler() {
        return event -> {};
    }
}
