package com.vehiclemanager.controller;

import com.vehiclemanager.model.Vehicle;
import com.vehiclemanager.model.VehicleType;
import com.vehiclemanager.persistence.CsvVehicleRepository;
import com.vehiclemanager.service.VehicleCollection;

import java.nio.file.Path;
import java.util.List;

/**
 * Mediates between the Swing view and the model/persistence layers.
 * Persists automatically after each mutation.
 */
public class VehicleController {

    private final VehicleCollection collection;
    private final CsvVehicleRepository repository;
    private final Path dataFile;

    public VehicleController(VehicleCollection collection, CsvVehicleRepository repository, Path dataFile) {
        this.collection = collection;
        this.repository = repository;
        this.dataFile = dataFile;
    }

    public List<Vehicle> all() { return collection.all(); }

    public boolean add(Vehicle v) {
        boolean ok = collection.add(v);
        if (ok) save();
        return ok;
    }

    public boolean update(Vehicle oldV, Vehicle newV) {
        boolean ok = collection.replace(oldV, newV);
        if (ok) save();
        return ok;
    }

    public boolean delete(Vehicle v) {
        boolean ok = collection.remove(v);
        if (ok) save();
        return ok;
    }

    public List<Vehicle> search(String query) { return collection.search(query); }
    public List<Vehicle> filterByType(VehicleType type) { return collection.filterByType(type); }

    public VehicleCollection.Stats stats() { return collection.stats(); }

    public void save() { repository.save(collection.all(), dataFile); }
}
