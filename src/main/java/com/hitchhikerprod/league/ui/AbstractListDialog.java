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
        upButton = newButton(arrowImage, -90.0, "Move up");
        upButton.setDisable(true);
        downButton = newButton(arrowImage, 90.0, "Move down");
        downButton.setDisable(true);

        final InputStream trashCanData = Objects.requireNonNull(getClass().getResourceAsStream("trash-can.png"));
        final Image trashCanImage = new Image(trashCanData, 32, 32, true, true);
        trashCanButton = newButton(trashCanImage, 0, "Delete");
        trashCanButton.setDisable(true);

        final InputStream plusSignData = Objects.requireNonNull(getClass().getResourceAsStream("stack.png"));
        final Image plusSignImage = new Image(plusSignData, 32, 32, true, true);
        plusSignButton = newButton(plusSignImage, 0, "New");

        buttonVBox = new VBox(upButton, plusSignButton, trashCanButton, downButton);
        buttonVBox.setAlignment(Pos.CENTER);
    }

    static Button newButton(final Image image, double rotation, String tooltip) {
        final ImageView imageView = new ImageView(image);
        if (rotation != 0) imageView.setRotate(rotation);
        final Button button = new Button();
        button.setGraphic(imageView);
        button.setTooltip(new Tooltip(tooltip));
        return button;
    }

    static EventHandler<ActionEvent> getReorderHandler(ListView<?> view, int delta) {
        return event -> {
            final int oldIndex = view.getSelectionModel().getSelectedIndex();
            if (oldIndex == -1) return;
            final int newIndex = oldIndex + delta;
            Collections.swap(view.getItems(), oldIndex, newIndex);
            view.getSelectionModel().select(newIndex);
        };
    }
}
