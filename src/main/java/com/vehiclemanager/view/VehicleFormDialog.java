package com.vehiclemanager.view;

import com.vehiclemanager.model.Vehicle;
import com.vehiclemanager.model.VehicleFactory;
import com.vehiclemanager.model.VehicleType;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.File;

/**
 * Modal dialog to create or edit a vehicle, with field validation and a
 * type-aware specification field (doors / cc / tonnes).
 */
public class VehicleFormDialog {

    private VehicleFormDialog() {}

    /**
     * @param existing vehicle to edit, or {@code null} to create a new one
     * @return the built vehicle, or {@code null} if the user cancelled
     */
    public static Vehicle show(Component parent, Vehicle existing) {
        JComboBox<VehicleType> typeBox = new JComboBox<>(VehicleType.values());
        JTextField brand = new JTextField();
        JTextField model = new JTextField();
        JTextField color = new JTextField();
        JTextField year = new JTextField();
        JTextField price = new JTextField();
        JTextField spec = new JTextField();
        JLabel specLabel = new JLabel();
        JTextField image = new JTextField();
        JButton browse = new JButton("Parcourir…");

        Runnable updateSpecLabel = () -> {
            VehicleType t = (VehicleType) typeBox.getSelectedItem();
            specLabel.setText(switch (t) {
                case CAR -> "Nombre de portes :";
                case MOTORCYCLE -> "Cylindrée (cc) :";
                case TRUCK -> "Charge utile (t) :";
            });
        };
        typeBox.addActionListener(e -> updateSpecLabel.run());

        browse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                image.setText(fc.getSelectedFile().getName());
            }
        });

        if (existing != null) {
            typeBox.setSelectedItem(existing.getType());
            typeBox.setEnabled(false); // type is part of identity; keep it stable on edit
            brand.setText(existing.getBrand());
            model.setText(existing.getModel());
            color.setText(existing.getColor());
            year.setText(String.valueOf(existing.getYear()));
            price.setText(String.valueOf(existing.getPrice()));
            spec.setText(String.valueOf(existing.specValue()));
            image.setText(existing.getImageName());
        } else {
            year.setText("2020");
            price.setText("0");
            spec.setText("0");
        }
        updateSpecLabel.run();

        JPanel imagePanel = new JPanel(new BorderLayout(4, 0));
        imagePanel.add(image, BorderLayout.CENTER);
        imagePanel.add(browse, BorderLayout.EAST);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Type :"));        form.add(typeBox);
        form.add(new JLabel("Marque :"));      form.add(brand);
        form.add(new JLabel("Modèle :"));      form.add(model);
        form.add(new JLabel("Couleur :"));     form.add(color);
        form.add(new JLabel("Année :"));       form.add(year);
        form.add(new JLabel("Prix (€) :"));    form.add(price);
        form.add(specLabel);                   form.add(spec);
        form.add(new JLabel("Image :"));       form.add(imagePanel);

        String title = existing == null ? "Ajouter un véhicule" : "Modifier le véhicule";
        while (true) {
            int result = JOptionPane.showConfirmDialog(
                    parent, form, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return null;
            try {
                VehicleType type = (VehicleType) typeBox.getSelectedItem();
                Vehicle v = VehicleFactory.create(
                        type,
                        brand.getText(),
                        model.getText(),
                        color.getText(),
                        Integer.parseInt(year.getText().trim()),
                        Double.parseDouble(price.getText().trim().replace(",", ".")),
                        image.getText(),
                        Double.parseDouble(spec.getText().trim().replace(",", ".")));
                return v;
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(parent,
                        "Année, prix et spécification doivent être numériques.",
                        "Champs invalides", JOptionPane.WARNING_MESSAGE);
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(parent, iae.getMessage(),
                        "Champs invalides", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
