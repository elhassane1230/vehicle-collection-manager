package com.vehiclemanager.service;

import com.vehiclemanager.model.Vehicle;
import com.vehiclemanager.model.VehicleType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * The collection of vehicles — the single source of truth for the whole app.
 * Replaces the old duplicated {@code ar} / {@code collectionneur} lists.
 */
public class VehicleCollection {

    private final List<Vehicle> vehicles = new ArrayList<>();

    public VehicleCollection() {}

    public VehicleCollection(List<Vehicle> initial) { vehicles.addAll(initial); }

    // --- CRUD ---------------------------------------------------------------

    /** Add a vehicle. @return false if an equal vehicle already exists. */
    public boolean add(Vehicle v) {
        if (contains(v.getBrand(), v.getModel())) return false;
        return vehicles.add(v);
    }

    public boolean remove(Vehicle v) { return vehicles.remove(v); }

    /** Replace {@code oldV} by {@code newV} in place (used by the edit dialog). */
    public boolean replace(Vehicle oldV, Vehicle newV) {
        int i = vehicles.indexOf(oldV);
        if (i < 0) return false;
        vehicles.set(i, newV);
        return true;
    }

    public boolean contains(String brand, String model) {
        return vehicles.stream().anyMatch(v ->
                v.getBrand().equalsIgnoreCase(brand) && v.getModel().equalsIgnoreCase(model));
    }

    /** Unmodifiable snapshot of all vehicles. */
    public List<Vehicle> all() { return List.copyOf(vehicles); }

    public int size() { return vehicles.size(); }
    public boolean isEmpty() { return vehicles.isEmpty(); }
    public void clear() { vehicles.clear(); }

    // --- Search & filter ----------------------------------------------------

    public List<Vehicle> filterByType(VehicleType type) {
        return vehicles.stream().filter(v -> v.getType() == type).toList();
    }

    /** Free-text search across brand, model and color (case-insensitive, substring). */
    public List<Vehicle> search(String query) {
        if (query == null || query.isBlank()) return all();
        String q = query.toLowerCase(Locale.ROOT).trim();
        return vehicles.stream()
                .filter(v -> v.getBrand().toLowerCase(Locale.ROOT).contains(q)
                          || v.getModel().toLowerCase(Locale.ROOT).contains(q)
                          || v.getColor().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    // --- Statistics ---------------------------------------------------------

    /** Aggregate statistics over the whole collection. */
    public record Stats(int total, Map<VehicleType, Integer> countByType,
                        double totalValue, double averagePrice, Optional<Vehicle> mostExpensive) {}

    public Stats stats() {
        Map<VehicleType, Integer> byType = new EnumMap<>(VehicleType.class);
        for (VehicleType t : VehicleType.values()) byType.put(t, 0);
        double total = 0;
        for (Vehicle v : vehicles) {
            byType.merge(v.getType(), 1, Integer::sum);
            total += v.getPrice();
        }
        double avg = vehicles.isEmpty() ? 0 : total / vehicles.size();
        Optional<Vehicle> costly = vehicles.stream().max(Comparator.comparingDouble(Vehicle::getPrice));
        return new Stats(vehicles.size(), byType, total, avg, costly);
    }
}
