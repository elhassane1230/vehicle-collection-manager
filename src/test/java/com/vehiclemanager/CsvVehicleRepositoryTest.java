package com.vehiclemanager;

import com.vehiclemanager.model.*;
import com.vehiclemanager.persistence.CsvVehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvVehicleRepositoryTest {

    @Test
    void roundTripPreservesData(@TempDir Path dir) {
        List<Vehicle> original = List.of(
                new Car("Renault", "Clio", "Noir", 2019, 15000, "clio.jpg", 5),
                new Truck("Mercedes-Benz", "Actros", "Blanc", 2018, 90000, "a.jpg", 26.0));
        Path file = dir.resolve("out.csv");

        CsvVehicleRepository repo = new CsvVehicleRepository();
        repo.save(original, file);
        List<Vehicle> loaded = repo.load(file);

        assertEquals(2, loaded.size());
        assertTrue(loaded.get(0) instanceof Car car && car.getDoors() == 5);
        assertTrue(loaded.get(1) instanceof Truck truck && truck.getPayloadTons() == 26.0);
        assertEquals("Mercedes-Benz", loaded.get(1).getBrand());
    }

    @Test
    void loadMissingFileReturnsEmpty(@TempDir Path dir) {
        assertTrue(new CsvVehicleRepository().load(dir.resolve("nope.csv")).isEmpty());
    }
}
