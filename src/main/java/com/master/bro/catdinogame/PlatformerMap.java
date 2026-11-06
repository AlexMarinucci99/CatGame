package com.master.bro.catdinogame;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Immutable map data. Moving bodies have sinusoidal, deterministic trajectories. */
record PlatformerMap(double width, double height, List<Body> bodies) {
    record Rect(double x, double y, double w, double h) {
        boolean overlaps(Rect b) { return x < b.x+b.w && x+w > b.x && y < b.y+b.h && y+h > b.y; }
    }
    record Body(String kind, double x, double y, double w, double h,
                double ax, double ay, double period, double belt) {
        Rect at(double time) {
            double phase = period > 0 ? (1-Math.cos(time*2*Math.PI/period))/2 : 0;
            return new Rect(x+ax*phase, y+ay*phase, w, h);
        }
        boolean solid() { return kind.equals("solid") || kind.equals("moving") || kind.equals("belt"); }
        boolean lethal() { return kind.equals("saw") || kind.equals("press") || kind.equals("spikes"); }
    }
    PlatformerMap { bodies = List.copyOf(bodies); }
    static PlatformerMap load() {
        try (var stream = Objects.requireNonNull(PlatformerMap.class.getResourceAsStream("/levels/sorting.csv"));
             var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            List<Body> bodies = new ArrayList<>();
            for (String line; (line=reader.readLine())!=null;) {
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] p=line.split(",");
                if(p.length!=9) throw new IOException("Invalid map row: "+line);
                bodies.add(new Body(p[0], Double.parseDouble(p[1]), Double.parseDouble(p[2]),
                        Double.parseDouble(p[3]), Double.parseDouble(p[4]), Double.parseDouble(p[5]),
                        Double.parseDouble(p[6]), Double.parseDouble(p[7]), Double.parseDouble(p[8])));
            }
            return new PlatformerMap(6200, 1400, bodies);
        } catch (IOException | RuntimeException e) { throw new IllegalStateException("Cannot load platform map", e); }
    }
}
