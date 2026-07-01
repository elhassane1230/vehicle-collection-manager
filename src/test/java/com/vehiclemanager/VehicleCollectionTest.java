package com.vehiclemanager;

import com.vehiclemanager.model.*;
import com.vehiclemanager.service.VehicleCollection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleCollectionTest {

    private VehicleCollection sample() {
        VehicleCollection c = new VehicleCollection();
        c.add(new Car("Renault", "Clio", "red", 2019, 15000, "clio.jpg", 5));
        c.add(new Motorcycle("Yamaha", "MT-07", "noir", 2021, 7500, "y.jpg", 689));
        c.add(new Truck("Mercedes-Benz", "Actros", "noir", 2018, 90000, "a.jpg", 26));
        c.add(new Car("Audi", "A7", "red", 2020, 55000, "audi.jpg", 4));
        return c;
    }

    @Test
    void rejectsDuplicateBrandModel() {
        VehicleCollection c = sample();
        assertFalse(c.add(new Car("Renault", "Clio", "blue", 2000, 1, "x.jpg", 3)));
        assertEquals(4, c.size());
    }

    @Test
    void searchMatchesBrandModelColor() {
        VehicleCollection c = sample();
        assertEquals(1, c.search("renault").size());
        assertEquals(2, c.search("red").size());
        assertEquals(4, c.search("").size());
    }

    @Test
    void filterByType() {
        assertEquals(2, sample().filterByType(VehicleType.CAR).size());
        assertEquals(1, sample().filterByType(VehicleType.TRUCK).size());
    }

    @Test
    void replaceUpdatesInPlace() {
        VehicleCollection c = sample();
        Vehicle audi = c.filterByType(VehicleType.CAR).stream()
                .filter(v -> v.getBrand().equals("Audi")).findFirst().orElseThrow();
        assertTrue(c.replace(audi, new Car("Audi", "A7", "noir", 2022, 60000, "audi.jpg", 4)));
        assertEquals(3, c.search("noir").size());
    }

    @Test
    void statsAreCorrect() {
        VehicleCollection.Stats s = sample().stats();
        assertEquals(4, s.total());
        assertEquals(2, s.countByType().get(VehicleType.CAR));
        assertEquals(15000 + 7500 + 90000 + 55000, s.totalValue(), 1e-6);
        assertTrue(s.mostExpensive().isPresent());
        assertEquals("Mercedes-Benz", s.mostExpensive().get().getBrand());
    }

    @Test
    void validationRejectsBadInput() {
        assertThrows(IllegalArgumentException.class,
                () -> new Car("", "X", "red", 2020, 1, "i", 4));
        assertThrows(IllegalArgumentException.class,
                () -> new Truck("M", "A", "noir", 2018, -5, "i", 10));
    }
}
