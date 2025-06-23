package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.LeagueController;
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
        // Why does the MenuBar expand when the label shrinks from two lines to one?
        VBox.setVgrow(menuBar, Priority.NEVER);
    }

    public void setController(LeagueController controller) {
        activateFileMenu(controller);
    }

    private Menu makeFileMenu() {
        final Menu fileM = new Menu("File");
        final MenuItem newMI = new MenuItem("New");

        final MenuItem openMI = new MenuItem("Open");
        openMI.setAccelerator(new KeyCharacterCombination("o", KeyCombination.CONTROL_DOWN));
        items.put("file.open", openMI);

        final SeparatorMenuItem sepMI = new SeparatorMenuItem();

        final MenuItem quitMI = new MenuItem("Quit");
        quitMI.setAccelerator(new KeyCharacterCombination("q", KeyCombination.CONTROL_DOWN));
        items.put("file.quit", quitMI);

        fileM.getItems().addAll(newMI, openMI, sepMI, quitMI);
        return fileM;
    }

    private void activateFileMenu(LeagueController controller) {
        items.get("file.open").setOnAction(ev -> controller.menuOpen());
        items.get("file.quit").setOnAction(ev -> controller.menuQuit());
    }

    public Node asNode() {
        return menuBar;
    }
}
