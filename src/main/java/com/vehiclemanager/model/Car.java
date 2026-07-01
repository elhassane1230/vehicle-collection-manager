package com.vehiclemanager.model;

/** A car, specified by its number of doors. */
public class Car extends Vehicle {
    private int doors;

    public Car(String brand, String model, String color, int year,
               double price, String imageName, int doors) {
        super(brand, model, color, year, price, imageName, VehicleType.CAR);
        setDoors(doors);
    }

    public int getDoors() { return doors; }
    public void setDoors(int doors) {
        if (doors < 0 || doors > 10) throw new IllegalArgumentException("Nombre de portes invalide : " + doors);
        this.doors = doors;
    }

    @Override public String specLabel() { return doors + " portes"; }
    @Override public double specValue() { return doors; }
}
