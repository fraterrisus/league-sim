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

public class MenuBar {
    final javafx.scene.control.MenuBar menuBar;

    public MenuBar(LeagueController controller) {
        final Menu fileM = makeFileMenu(controller);

        menuBar = new javafx.scene.control.MenuBar(fileM);
        // Why does the MenuBar expand when the label shrinks from two lines to one?
        VBox.setVgrow(menuBar, Priority.NEVER);
    }

    private static Menu makeFileMenu(LeagueController controller) {
        final Menu fileM = new Menu("File");
        final MenuItem newMI = new MenuItem("New");

        final MenuItem openMI = new MenuItem("Open");
        openMI.setAccelerator(new KeyCharacterCombination("o", KeyCombination.CONTROL_DOWN));
        openMI.setOnAction(ev -> controller.menuOpen());

        final SeparatorMenuItem sepMI = new SeparatorMenuItem();

        final MenuItem quitMI = new MenuItem("Quit");
        quitMI.setAccelerator(new KeyCharacterCombination("q", KeyCombination.CONTROL_DOWN));
        quitMI.setOnAction(ev -> controller.menuQuit());

        fileM.getItems().addAll(newMI, openMI, sepMI, quitMI);
        return fileM;
    }

    public Node asNode() {
        return menuBar;
    }
}
