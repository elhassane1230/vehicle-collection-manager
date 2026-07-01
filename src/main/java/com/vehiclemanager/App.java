package com.vehiclemanager;

import com.vehiclemanager.controller.VehicleController;
import com.vehiclemanager.model.Car;
import com.vehiclemanager.model.Motorcycle;
import com.vehiclemanager.model.Truck;
import com.vehiclemanager.model.Vehicle;
import com.vehiclemanager.persistence.CsvVehicleRepository;
import com.vehiclemanager.service.VehicleCollection;
import com.vehiclemanager.view.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Application entry point: sets the look-and-feel, loads data, shows the window. */
public class App {

    private static final Path DATA_FILE = Path.of("data", "collection.csv");
    private static final Path SAMPLE_FILE = Path.of("data", "sample-collection.csv");

    public static void main(String[] args) {
        setLookAndFeel();

        CsvVehicleRepository repo = new CsvVehicleRepository();
        List<Vehicle> initial = repo.load(DATA_FILE);
        if (initial.isEmpty()) {
            initial = Files.exists(SAMPLE_FILE) ? repo.load(SAMPLE_FILE) : seed();
        }
        VehicleCollection collection = new VehicleCollection(initial);
        VehicleController controller = new VehicleController(collection, repo, DATA_FILE);
        controller.save(); // persist initial state to the live data file

        SwingUtilities.invokeLater(() -> new MainFrame(controller).setVisible(true));
    }

    private static void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { /* fall back to default */ }
    }

    /** Built-in sample collection used the first time the app runs. */
    private static List<Vehicle> seed() {
        List<Vehicle> v = new ArrayList<>();
        v.add(new Car("Renault", "Clio", "Noir", 2019, 15000, "clio_noir.jpg", 5));
        v.add(new Car("Audi", "A7", "Rouge", 2020, 55000, "audi_a7.jpg", 4));
        v.add(new Motorcycle("Yamaha", "MT-07", "Noir", 2021, 7500, "yahama_kawazaki.jpg", 689));
        v.add(new Motorcycle("BMW", "Motorrad", "Noir", 2022, 12000, "bmw_moto.jpg", 1250));
        v.add(new Truck("Mercedes-Benz", "Actros", "Blanc", 2018, 90000, "mercedes_actros.jpg", 26.0));
        return v;
    }
}
