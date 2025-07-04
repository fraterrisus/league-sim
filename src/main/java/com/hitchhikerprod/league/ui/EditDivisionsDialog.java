package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.LeagueDivision;
import com.hitchhikerprod.league.beans.LeagueTeamData;
import com.hitchhikerprod.league.definitions.League;
import javafx.beans.Observable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.InputStream;
import java.util.Objects;

import static com.hitchhikerprod.league.ui.AbstractListDialog.getReorderHandler;
import static com.hitchhikerprod.league.ui.AbstractListDialog.newButton;

public class EditDivisionsDialog extends Dialog<Void> {
    private final Window parent;
    private final League league;

    private final ListView<? extends LeagueDivision> divisionList;
    private final ListView<? extends LeagueTeamData> teamList;

    private final Button divUpButton;
    private final Button divDownButton;
    private final Button divNewButton;
    private final Button divDeleteButton;

    private final Button teamUpButton;
    private final Button teamDownButton;
    private final Button teamNewButton;
    private final Button teamDeleteButton;

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
        divDeleteButton = newButton(trashCanImage, 0, "Delete");
        divDeleteButton.setDisable(true);

        final VBox divButtons = new VBox(divUpButton, divNewButton, divDeleteButton, divDownButton);
        divButtons.setAlignment(Pos.CENTER);

        teamUpButton = newButton(arrowImage, -90, "Move up");
        teamUpButton.setDisable(true);
        teamDownButton = newButton(arrowImage, 90, "Move down");
        teamDownButton.setDisable(true);
        teamNewButton = newButton(plusSignImage, 0, "New");
        teamDeleteButton = newButton(trashCanImage, 0, "Delete");
        teamDeleteButton.setDisable(true);

        final VBox teamButtons = new VBox(teamUpButton, teamNewButton, teamDeleteButton, teamDownButton);
        teamButtons.setAlignment(Pos.CENTER);

        final HBox innerPane = new HBox(divisionList, divButtons, teamList, teamButtons);

        final DialogPane outerPane = super.getDialogPane();
        outerPane.setContent(innerPane);
        outerPane.getStylesheets().setAll(parent.getScene().getStylesheets());
        outerPane.getButtonTypes().setAll(ButtonType.OK);
    }

    private <T extends LeagueDivision> void getDivisionSelectionHandler(Observable observable, T oldValue, T newValue) {
        final boolean disable = Objects.isNull(newValue);
        divUpButton.setDisable(disable);
        divDownButton.setDisable(disable);
        divDeleteButton.setDisable(disable);
        setTeamsForDivision(newValue);
    }

    private <T extends LeagueTeamData> void getTeamSelectionHandler(Observable observable, T oldValue, T newValue) {

    }

    private <T extends LeagueTeamData> ListCell<T> teamCellFactory(ListView<T> view) {
        return null;
    }

    private <T extends LeagueDivision> void setTeamsForDivision(T division) {
        if (Objects.isNull(division)) return;
    }
}
