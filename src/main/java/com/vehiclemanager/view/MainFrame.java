package com.vehiclemanager.view;

import com.vehiclemanager.controller.VehicleController;
import com.vehiclemanager.model.Vehicle;
import com.vehiclemanager.model.VehicleType;
import com.vehiclemanager.service.VehicleCollection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.nio.file.Path;
import java.util.List;

/** Main application window: searchable, sortable table + detail panel + stats bar. */
public class MainFrame extends JFrame {

    private final VehicleController controller;
    private final VehicleTableModel tableModel = new VehicleTableModel();
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<VehicleTableModel> sorter = new TableRowSorter<>(tableModel);
    private final DetailPanel detail = new DetailPanel();
    private final JLabel statusBar = new JLabel();
    private final JTextField searchField = new JTextField(18);
    private final JComboBox<String> typeFilter = new JComboBox<>();

    public MainFrame(VehicleController controller) {
        this.controller = controller;
        setTitle("Gestion de Collection de Véhicules");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        wireSelection();
        refresh(controller.all());
        setPreferredSize(new Dimension(900, 560));
        pack();
        setLocationRelativeTo(null);
    }

    private JToolBar buildToolbar() {
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JButton add = new JButton("Ajouter");
        JButton edit = new JButton("Modifier");
        JButton delete = new JButton("Supprimer");
        JButton refresh = new JButton("Actualiser");
        JButton export = new JButton("Exporter CSV");

        add.addActionListener(e -> onAdd());
        edit.addActionListener(e -> onEdit());
        delete.addActionListener(e -> onDelete());
        refresh.addActionListener(e -> { searchField.setText(""); typeFilter.setSelectedIndex(0); refresh(controller.all()); });
        export.addActionListener(e -> onExport());

        bar.add(add); bar.add(edit); bar.add(delete); bar.add(refresh); bar.add(export);
        bar.addSeparator();

        typeFilter.addItem("Tous les types");
        for (VehicleType t : VehicleType.values()) typeFilter.addItem(t.label());
        typeFilter.addActionListener(e -> applyFilters());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.add(new JLabel("Recherche :"));
        filters.add(searchField);
        filters.add(new JLabel("Filtre :"));
        filters.add(typeFilter);
        bar.add(filters);
        return bar;
    }

    private JPanel buildCenter() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSorter(sorter);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(false);

        JPanel center = new JPanel(new BorderLayout());
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        center.add(detail, BorderLayout.EAST);
        return center;
    }

    private JPanel buildStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        panel.add(statusBar, BorderLayout.WEST);
        return panel;
    }

    private void wireSelection() {
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) { detail.clear(); return; }
            detail.show(tableModel.getVehicleAt(table.convertRowIndexToModel(viewRow)));
        });
    }

    // --- actions ------------------------------------------------------------

    private void onAdd() {
        Vehicle v = VehicleFormDialog.show(this, null);
        if (v == null) return;
        if (!controller.add(v)) {
            JOptionPane.showMessageDialog(this,
                    "Un véhicule identique (marque + modèle) existe déjà.",
                    "Doublon", JOptionPane.WARNING_MESSAGE);
            return;
        }
        applyFilters();
    }

    private void onEdit() {
        Vehicle sel = selectedVehicle();
        if (sel == null) { needSelection(); return; }
        Vehicle edited = VehicleFormDialog.show(this, sel);
        if (edited == null) return;
        controller.update(sel, edited);
        applyFilters();
    }

    private void onDelete() {
        Vehicle sel = selectedVehicle();
        if (sel == null) { needSelection(); return; }
        int c = JOptionPane.showConfirmDialog(this,
                "Supprimer « " + sel + " » de la collection ?",
                "Confirmer", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            controller.delete(sel);
            detail.clear();
            applyFilters();
        }
    }

    private void onExport() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("collection_export.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            new com.vehiclemanager.persistence.CsvVehicleRepository()
                    .save(controller.all(), fc.getSelectedFile().toPath());
            JOptionPane.showMessageDialog(this, "Exporté vers " + fc.getSelectedFile().getName());
        }
    }

    private Vehicle selectedVehicle() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        return tableModel.getVehicleAt(table.convertRowIndexToModel(viewRow));
    }

    private void needSelection() {
        JOptionPane.showMessageDialog(this, "Veuillez d'abord sélectionner un véhicule.",
                "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- refresh / filters --------------------------------------------------

    private void applyFilters() {
        List<Vehicle> base;
        int idx = typeFilter.getSelectedIndex();
        if (idx <= 0) base = controller.all();
        else base = controller.filterByType(VehicleType.values()[idx - 1]);

        String q = searchField.getText();
        if (q != null && !q.isBlank()) {
            String query = q.toLowerCase();
            base = base.stream().filter(v ->
                    v.getBrand().toLowerCase().contains(query)
                 || v.getModel().toLowerCase().contains(query)
                 || v.getColor().toLowerCase().contains(query)).toList();
        }
        refresh(base);
    }

    private void refresh(List<Vehicle> vehicles) {
        tableModel.setVehicles(vehicles);
        updateStatus();
    }

    private void updateStatus() {
        VehicleCollection.Stats s = controller.stats();
        String costly = s.mostExpensive().map(Object::toString).orElse("—");
        statusBar.setText(String.format(
                "  %d véhicule(s)  |  Voitures : %d  ·  Motos : %d  ·  Camions : %d  |  Valeur totale : %,.0f €  ·  Prix moyen : %,.0f €  |  Plus cher : %s",
                s.total(),
                s.countByType().get(VehicleType.CAR),
                s.countByType().get(VehicleType.MOTORCYCLE),
                s.countByType().get(VehicleType.TRUCK),
                s.totalValue(), s.averagePrice(), costly));
    }
}
