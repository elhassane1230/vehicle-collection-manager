package com.vehiclemanager.persistence;

import com.vehiclemanager.model.Vehicle;
import com.vehiclemanager.model.VehicleFactory;
import com.vehiclemanager.model.VehicleType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves the collection as a small, self-contained CSV file
 * (no external dependencies). Columns:
 * {@code type,brand,model,color,year,price,image,spec}.
 */
public class CsvVehicleRepository {

    private static final String HEADER = "type,brand,model,color,year,price,image,spec";

    public void save(List<Vehicle> vehicles, Path file) {
        StringBuilder sb = new StringBuilder(HEADER).append('\n');
        for (Vehicle v : vehicles) {
            sb.append(String.join(",",
                    v.getType().name(),
                    esc(v.getBrand()),
                    esc(v.getModel()),
                    esc(v.getColor()),
                    Integer.toString(v.getYear()),
                    Double.toString(v.getPrice()),
                    esc(v.getImageName()),
                    Double.toString(v.specValue())
            )).append('\n');
        }
        try {
            if (file.getParent() != null) Files.createDirectories(file.getParent());
            Files.writeString(file, sb.toString());
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible d'enregistrer " + file, e);
        }
    }

    public List<Vehicle> load(Path file) {
        List<Vehicle> out = new ArrayList<>();
        if (!Files.exists(file)) return out;
        try {
            List<String> lines = Files.readAllLines(file);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (i == 0 && line.startsWith("type,")) continue; // header
                if (line.isBlank()) continue;
                List<String> f = parse(line);
                if (f.size() < 8) continue;
                VehicleType type = VehicleType.fromString(f.get(0));
                out.add(VehicleFactory.create(
                        type, f.get(1), f.get(2), f.get(3),
                        Integer.parseInt(f.get(4).trim()),
                        Double.parseDouble(f.get(5).trim()),
                        f.get(6),
                        Double.parseDouble(f.get(7).trim())));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de lire " + file, e);
        }
        return out;
    }

    // --- minimal RFC-4180-ish CSV helpers ---
    private static String esc(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return '"' + s.replace("\"", "\"\"") + '"';
        }
        return s;
    }

    private static List<String> parse(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else inQuotes = false;
                } else cur.append(c);
            } else {
                if (c == '"') inQuotes = true;
                else if (c == ',') { out.add(cur.toString()); cur.setLength(0); }
                else cur.append(c);
            }
        }
        out.add(cur.toString());
        return out;
    }
}
