package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.LeagueApp;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class MenuBar {
    private static final MenuBar INSTANCE = new MenuBar();

    public static MenuBar getInstance() {
        return INSTANCE;
    }

    private final javafx.scene.control.MenuBar menuBar;

    private final Map<String, MenuItem> items;

    private MenuBar() {
        items = new HashMap<>();

        final Menu fileM = makeFileMenu();
        final Menu gameM = makeLeagueMenu();

        menuBar = new javafx.scene.control.MenuBar(fileM, gameM);
        VBox.setVgrow(menuBar, Priority.NEVER);
    }

    public void setApplication(LeagueApp app) {
        activateFileMenu(app);
    }

    private Menu makeLeagueMenu() {
        final Menu leagueM = new Menu("League");

        final MenuItem editDivisionsMI = new MenuItem("Divisions…");
        editDivisionsMI.setDisable(true);
        items.put("league.editDivisions", editDivisionsMI);
        
        final MenuItem editMatchDaysMI = new MenuItem("Match Days…");
        editMatchDaysMI.setDisable(true);
        items.put("league.editMatchDays", editMatchDaysMI);

        final MenuItem editGamesMI = new MenuItem("Games…");
        editGamesMI.setDisable(true);
        items.put("league.editGames", editGamesMI);

        leagueM.getItems().addAll(editDivisionsMI, editMatchDaysMI, editGamesMI);
        return leagueM;
    }

    private Menu makeFileMenu() {
        final Menu fileM = new Menu("File");
        final MenuItem newMI = new MenuItem("New");
        newMI.setDisable(true);

        final MenuItem openMI = new MenuItem("Open...");
        openMI.setAccelerator(new KeyCharacterCombination("O", KeyCombination.CONTROL_DOWN));
        items.put("file.open", openMI);

        final MenuItem saveMI = new MenuItem("Save");
        saveMI.setAccelerator(new KeyCharacterCombination("S", KeyCombination.CONTROL_DOWN));
        items.put("file.save", saveMI);
        saveMI.setDisable(true);

        final MenuItem saveAsMI = new MenuItem("Save As...");
        saveAsMI.setAccelerator(new KeyCharacterCombination("S", KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        items.put("file.saveAs", saveAsMI);
        saveAsMI.setDisable(true);

        final SeparatorMenuItem sepMI = new SeparatorMenuItem();

        final MenuItem quitMI = new MenuItem("Quit");
        quitMI.setAccelerator(new KeyCharacterCombination("Q", KeyCombination.CONTROL_DOWN));
        items.put("file.quit", quitMI);

        fileM.getItems().addAll(newMI, openMI, saveMI, saveAsMI, sepMI, quitMI);
        return fileM;
    }

    public void allowSave() {
        items.get("file.save").setDisable(false);
        items.get("file.saveAs").setDisable(false);
        items.get("league.editDivisions").setDisable(false);
        items.get("league.editMatchDays").setDisable(false);
        items.get("league.editGames").setDisable(false);
    }

    private void activateFileMenu(LeagueApp app) {
        items.get("file.open").setOnAction(ev -> app.menuOpen());
        items.get("file.quit").setOnAction(ev -> app.menuQuit());
        items.get("file.save").setOnAction(ev -> app.menuSave());
        items.get("file.saveAs").setOnAction(ev -> app.menuSaveAs());
        items.get("league.editDivisions").setOnAction(ev -> app.menuEditDivisions());
        items.get("league.editMatchDays").setOnAction(ev -> app.menuEditMatchDays());
        items.get("league.editGames").setOnAction(ev -> app.menuEditGames());
    }

    public Node asNode() {
        return menuBar;
    }
}
