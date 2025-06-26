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

        menuBar = new javafx.scene.control.MenuBar(fileM);
        VBox.setVgrow(menuBar, Priority.NEVER);
    }

    public void setApplication(LeagueApp app) {
        activateFileMenu(app);
    }

    private Menu makeFileMenu() {
        final Menu fileM = new Menu("File");
        final MenuItem newMI = new MenuItem("New");

        final MenuItem openMI = new MenuItem("Open");
        openMI.setAccelerator(new KeyCharacterCombination("O", KeyCombination.CONTROL_DOWN));
        items.put("file.open", openMI);

        final SeparatorMenuItem sepMI = new SeparatorMenuItem();

        final MenuItem quitMI = new MenuItem("Quit");
        quitMI.setAccelerator(new KeyCharacterCombination("Q", KeyCombination.CONTROL_DOWN));
        items.put("file.quit", quitMI);

        fileM.getItems().addAll(newMI, openMI, sepMI, quitMI);
        return fileM;
    }

    private void activateFileMenu(LeagueApp app) {
        items.get("file.open").setOnAction(ev -> app.menuOpen());
        items.get("file.quit").setOnAction(ev -> app.menuQuit());
    }

    public Node asNode() {
        return menuBar;
    }
}
