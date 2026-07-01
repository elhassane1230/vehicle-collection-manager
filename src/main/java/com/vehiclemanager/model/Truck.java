package com.vehiclemanager.model;

/** A truck, specified by its payload capacity in tonnes. */
public class Truck extends Vehicle {
    private double payloadTons;

    public Truck(String brand, String model, String color, int year,
                 double price, String imageName, double payloadTons) {
        super(brand, model, color, year, price, imageName, VehicleType.TRUCK);
        setPayloadTons(payloadTons);
    }

    public double getPayloadTons() { return payloadTons; }
    public void setPayloadTons(double payloadTons) {
        if (payloadTons < 0) throw new IllegalArgumentException("Charge utile invalide : " + payloadTons);
        this.payloadTons = payloadTons;
    }

    @Override public String specLabel() { return payloadTons + " t"; }
    @Override public double specValue() { return payloadTons; }
}
