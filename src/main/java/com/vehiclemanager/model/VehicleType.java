package com.vehiclemanager.model;

/** Vehicle categories, each with a French display label. */
public enum VehicleType {
    CAR("Voiture"),
    MOTORCYCLE("Moto"),
    TRUCK("Camion");

    private final String label;

    VehicleType(String label) { this.label = label; }

    /** Human-readable French label (e.g. "Voiture"). */
    public String label() { return label; }

    /** Resolve a type from either its enum name or its French label (case-insensitive). */
    public static VehicleType fromString(String s) {
        for (VehicleType t : values()) {
            if (t.name().equalsIgnoreCase(s) || t.label.equalsIgnoreCase(s)) return t;
        }
        throw new IllegalArgumentException("Type de véhicule inconnu : " + s);
    }

    @Override public String toString() { return label; }
}
