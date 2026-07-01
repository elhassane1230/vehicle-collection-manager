package com.vehiclemanager.model;

import java.util.Objects;

/**
 * Base class for every vehicle in the collection.
 *
 * <p>Immutable identity fields (brand, model, type) plus descriptive fields.
 * Subclasses add one type-specific specification (doors, engine size, payload)
 * exposed through {@link #specLabel()} and {@link #specValue()}.
 */
public abstract class Vehicle {

    private final String brand;
    private final String model;
    private String color;
    private int year;
    private double price;
    private String imageName;   // file name only, resolved by the view
    private final VehicleType type;

    protected Vehicle(String brand, String model, String color,
                      int year, double price, String imageName, VehicleType type) {
        this.brand = requireText(brand, "marque");
        this.model = requireText(model, "modèle");
        this.type = Objects.requireNonNull(type, "type");
        setColor(color);
        setYear(year);
        setPrice(price);
        setImageName(imageName);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Le champ '" + field + "' est obligatoire.");
        return value.trim();
    }

    // --- read-only identity ---
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public VehicleType getType() { return type; }

    // --- mutable descriptive fields (with validation) ---
    public String getColor() { return color; }
    public void setColor(String color) { this.color = (color == null ? "" : color.trim()); }

    public int getYear() { return year; }
    public void setYear(int year) {
        if (year < 1885 || year > 2100)
            throw new IllegalArgumentException("Année invalide : " + year);
        this.year = year;
    }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("Le prix ne peut pas être négatif.");
        this.price = price;
    }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) {
        this.imageName = (imageName == null || imageName.isBlank()) ? "pas_image.png" : imageName.trim();
    }

    /** Short label for the type-specific spec, e.g. "4 portes". */
    public abstract String specLabel();

    /** Numeric value of the type-specific spec (doors, cc, tonnes), for CSV round-trips. */
    public abstract double specValue();

    @Override
    public String toString() { return brand + " " + model; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle other)) return false;
        return type == other.type
                && brand.equalsIgnoreCase(other.brand)
                && model.equalsIgnoreCase(other.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, brand.toLowerCase(), model.toLowerCase());
    }
}
