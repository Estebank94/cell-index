package com.company;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by estebankramer on 12/03/2019.
 */

public class Particle {
    private Integer id;
    private Double radius;
    private Point position;
    private Set<Particle> neighbours;
    private Double velocity;
    private Double angle;
    private Property<String> property;

    public Particle(Integer id, Double radius, Point position, Double velocity, Double angle, Property<String> property) {
        this.id = id;
        this.radius = radius;
        this.position = position;
        this.neighbours = new HashSet<>();
        this.velocity = velocity;
        this.angle = angle;
        this.property = property;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getVelocity() {
        return velocity;
    }

    public void setVelocity(Double velocity) {
        this.velocity = velocity;
    }

    public Double getAngle() {
        return angle;
    }

    public void setAngle(Double angle) {
        this.angle = angle;
    }

    public Property<String> getProperty() {
        return property;
    }

    public void setProperty(Property<String> property) {
        this.property = property;
    }

    public static Double calculateBorderDistance(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow(p2.getPosition().getX() - p1.getPosition().getX(), 2) - Math.pow(p2.getPosition().getY() - p1.getPosition().getY(), 2)) - p1.getRadius();
    }

    @Override
    public String toString() {
        return "Particle: " + "ID= "+ id + ", radius= " + radius + ", property= " + property + ", velocity= " + velocity;
    }
}

