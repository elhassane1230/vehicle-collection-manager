package com.vehiclemanager.model;

/** A motorcycle, specified by its engine displacement in cc. */
public class Motorcycle extends Vehicle {
    private int engineCc;

    public Motorcycle(String brand, String model, String color, int year,
                      double price, String imageName, int engineCc) {
        super(brand, model, color, year, price, imageName, VehicleType.MOTORCYCLE);
        setEngineCc(engineCc);
    }

    public int getEngineCc() { return engineCc; }
    public void setEngineCc(int engineCc) {
        if (engineCc < 0) throw new IllegalArgumentException("Cylindrée invalide : " + engineCc);
        this.engineCc = engineCc;
    }

    @Override public String specLabel() { return engineCc + " cc"; }
    @Override public double specValue() { return engineCc; }
}
