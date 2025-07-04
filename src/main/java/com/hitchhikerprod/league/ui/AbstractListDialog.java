package com.hitchhikerprod.league.ui;

import com.hitchhikerprod.league.definitions.League;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.io.InputStream;
import java.util.Collections;
import java.util.Objects;

public abstract class AbstractListDialog extends Dialog<Void> {
    protected final Window parent;
    protected final League league;

    protected final Button upButton;
    protected final Button downButton;
    protected final Button trashCanButton;
    protected final Button plusSignButton;
    protected final VBox buttonVBox;

    public AbstractListDialog(Window parent, League league) {
        super();
        super.initOwner(parent);
        super.setResultConverter(buttonType -> null);
        super.setResizable(true);

        this.parent = parent;
        this.league = league;

        final InputStream arrowImageData = Objects.requireNonNull(getClass().getResourceAsStream("arrow-right.png"));
        final Image arrowImage = new Image(arrowImageData, 32, 32, true, true);

        final ImageView upArrowNode = new ImageView(arrowImage);
        upArrowNode.setRotate(-90.0);
        upButton = new Button();
        upButton.setGraphic(upArrowNode);
        upButton.setTooltip(new Tooltip("Move up"));
        upButton.setDisable(true);

        final ImageView downArrowNode = new ImageView(arrowImage);
        downArrowNode.setRotate(90.0);
        downButton = new Button();
        downButton.setGraphic(downArrowNode);
        downButton.setTooltip(new Tooltip("Move down"));
        downButton.setDisable(true);

        final InputStream trashCanData = Objects.requireNonNull(getClass().getResourceAsStream("trash-can.png"));
        final Image trashCanImage = new Image(trashCanData, 32, 32, true, true);
        final ImageView trashCanNode = new ImageView(trashCanImage);
        trashCanButton = new Button();
        trashCanButton.setGraphic(trashCanNode);
        trashCanButton.setTooltip(new Tooltip("Delete"));
        trashCanButton.setDisable(true);

        final InputStream plusSignData = Objects.requireNonNull(getClass().getResourceAsStream("stack.png"));
        final Image plusSignImage = new Image(plusSignData, 32, 32, true, true);
        final ImageView plusSignNode = new ImageView(plusSignImage);
        plusSignButton = new Button();
        plusSignButton.setGraphic(plusSignNode);
        plusSignButton.setTooltip(new Tooltip("New"));

        buttonVBox = new VBox(upButton, plusSignButton, trashCanButton, downButton);
        buttonVBox.setAlignment(Pos.CENTER);
    }

    protected EventHandler<ActionEvent> getReorderHandler(ListView<?> view, int delta) {
        return event -> {
            final int oldIndex = view.getSelectionModel().getSelectedIndex();
            if (oldIndex == -1) return;
            final int newIndex = oldIndex + delta;
            Collections.swap(view.getItems(), oldIndex, newIndex);
            view.getSelectionModel().select(newIndex);
        };
    }
}
