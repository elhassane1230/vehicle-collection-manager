package com.vehiclemanager.view;

import com.vehiclemanager.model.Vehicle;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Table model backing the main {@link javax.swing.JTable}. */
public class VehicleTableModel extends AbstractTableModel {

    private static final String[] COLUMNS =
            {"Type", "Marque", "Modèle", "Couleur", "Année", "Prix (€)", "Spéc."};

    private final List<Vehicle> rows = new ArrayList<>();

    public void setVehicles(List<Vehicle> vehicles) {
        rows.clear();
        rows.addAll(vehicles);
        fireTableDataChanged();
    }

    public Vehicle getVehicleAt(int row) { return rows.get(row); }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int c) { return COLUMNS[c]; }

    @Override
    public Class<?> getColumnClass(int c) {
        return switch (c) {
            case 4 -> Integer.class;
            case 5 -> Double.class;
            default -> String.class;
        };
    }

    @Override
    public Object getValueAt(int r, int c) {
        Vehicle v = rows.get(r);
        return switch (c) {
            case 0 -> v.getType().label();
            case 1 -> v.getBrand();
            case 2 -> v.getModel();
            case 3 -> v.getColor();
            case 4 -> v.getYear();
            case 5 -> v.getPrice();
            case 6 -> v.specLabel();
            default -> "";
        };
    }
}
