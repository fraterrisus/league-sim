package com.hitchhikerprod.league.beans;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class LeagueColumn<T> {
    private final Class<T> klass;
    private final String header;
    private final Pos alignment;
    private final int fieldNum;
    private final String formatString;

    /**
     * Returns a CellFactory implementation (for passing into #SetCellFactory) that sets the cell alignment and
     * (optionally) applies a format string. "item.toString()" is basically what the default CellFactory does, so long
     * as you don't pass it a Node.
     * */
    private Callback<TableColumn<LeagueTeamData, T>, TableCell<LeagueTeamData, T>>
    formattingCellFactory(final Class<T> ignored, final String format, Pos alignment) {
        if (format == null) {
            return col -> {
                final TableCell<LeagueTeamData, T> cell = new TableCell<>() {
                    @Override
                    public void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : item.toString());
                    }
                };
                cell.setAlignment(alignment);
                return cell;
            };
        } else {
            return col -> {
                final TableCell<LeagueTeamData, T> cell = new TableCell<>() {
                    @Override
                    public void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(empty ? null : String.format(format, item));
                    }
                };
                cell.setAlignment(alignment);
                return cell;
            };
        }
    }

    public LeagueColumn(int fieldNum, Class<T> klass, String header, Pos alignment) {
        this(fieldNum, klass, header, alignment, null);
    }

    public LeagueColumn(int fieldNum, Class<T> klass, String header, Pos alignment, String formatString) {
        this.klass = klass;
        this.header = header;
        this.alignment = alignment;
        this.fieldNum = fieldNum;
        this.formatString = formatString;
    }

    public TableColumn<LeagueTeamData, T> getColumn() {
        TableColumn<LeagueTeamData, T> col = new TableColumn<>(header);
        col.setCellValueFactory(ltd -> new ReadOnlyObjectWrapper<>(ltd.getValue().getData(klass, fieldNum)));
        col.setCellFactory(formattingCellFactory(klass, formatString, alignment));
        return col;
    }
}
