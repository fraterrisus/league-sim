package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.beans.Named;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.Objects;

public class CellFactories {
    static <T extends Named> ListCell<T> nameCellFactory(ListView<T> ignored) {
        return new ListCell<>() {
            @Override
            protected void updateItem(T division, boolean empty) {
                super.updateItem(division, empty);
                if (Objects.isNull(division) || empty) {
                    setText("");
                } else {
                    setText(division.getName());
                }
            }
        };
    }
}
