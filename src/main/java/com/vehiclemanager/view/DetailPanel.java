package com.vehiclemanager.view;

import com.vehiclemanager.model.Vehicle;
import com.vehiclemanager.util.ImageLoader;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

/** Side panel showing the currently selected vehicle with a scaled image. */
public class DetailPanel extends JPanel {

    private final JLabel image = new JLabel();
    private final JLabel title = new JLabel();
    private final JLabel type = new JLabel();
    private final JLabel color = new JLabel();
    private final JLabel year = new JLabel();
    private final JLabel price = new JLabel();
    private final JLabel spec = new JLabel();

    public DetailPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setPreferredSize(new Dimension(280, 0));

        image.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(image);
        add(javax.swing.Box.createVerticalStrut(10));
        add(title);
        add(javax.swing.Box.createVerticalStrut(10));
        for (JLabel l : new JLabel[]{type, color, year, price, spec}) {
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(l);
            add(javax.swing.Box.createVerticalStrut(4));
        }
        clear();
    }

    public void clear() {
        image.setIcon(ImageLoader.load(ImageLoader.PLACEHOLDER, 240, 160));
        title.setText("Aucun véhicule sélectionné");
        type.setText(" ");
        color.setText(" ");
        year.setText(" ");
        price.setText(" ");
        spec.setText(" ");
    }

    public void show(Vehicle v) {
        image.setIcon(ImageLoader.load(v.getImageName(), 240, 160));
        title.setText(v.getBrand() + " " + v.getModel());
        type.setText("Type : " + v.getType().label());
        color.setText("Couleur : " + v.getColor());
        year.setText("Année : " + v.getYear());
        price.setText(String.format("Prix : %,.0f €", v.getPrice()));
        spec.setText("Spécification : " + v.specLabel());
        revalidate();
        repaint();
    }
}
