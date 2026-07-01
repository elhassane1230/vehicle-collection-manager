package com.vehiclemanager.model;

/** Central factory that builds the right {@link Vehicle} subclass from raw fields. */
public final class VehicleFactory {

    private VehicleFactory() {}

    /**
     * Build a vehicle of the given type.
     * @param spec doors (car), cc (motorcycle) or payload in tonnes (truck)
     */
    public static Vehicle create(VehicleType type, String brand, String model, String color,
                                 int year, double price, String imageName, double spec) {
        return switch (type) {
            case CAR        -> new Car(brand, model, color, year, price, imageName, (int) spec);
            case MOTORCYCLE -> new Motorcycle(brand, model, color, year, price, imageName, (int) spec);
            case TRUCK      -> new Truck(brand, model, color, year, price, imageName, spec);
        };
    }
}
