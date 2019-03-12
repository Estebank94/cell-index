package com.company;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by estebankramer on 12/03/2019.
 */

public class Particle {
    private Double radius;
    private Point position;
    private Set<Particle> neighbours;

    public Particle(Double radius, Point position) {
        this.radius = radius;
        this.position = position;
        this.neighbours = new HashSet<>();
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Set<Particle> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Set<Particle> neighbours) {
        this.neighbours = neighbours;
    }
}

