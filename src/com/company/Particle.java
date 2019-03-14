package com.company;

import java.util.HashSet;
import java.util.Set;


public class Particle {
    private int id;
    private double radius;
    private Point position;
    private Set<Particle> neighbours;
    private double velocity;
    private double angle;
    private Property<String> property;

    public Particle(int id, double radius, Point position, double velocity, double angle, Property<String> property) {
        this.id = id;
        this.radius = radius;
        this.position = position;
        this.neighbours = new HashSet<>();
        this.velocity = velocity;
        this.angle = angle;
        this.property = property;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
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

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public Property<String> getProperty() {
        return property;
    }

    public void setProperty(Property<String> property) {
        this.property = property;
    }

    public static Double calculateBorderDistance(Particle p1, Particle p2) {
        return Math.sqrt(Math.pow(p2.getPosition().getX() - p1.getPosition().getX(), 2) - Math.pow(p2.getPosition().getY() - p1.getPosition().getY(), 2)) - p1.getRadius() - p2.getRadius();
    }

    @Override
    public String toString() {
        return "Particle: " + "ID= "+ id + ", radius= " + radius + ", property= " + property + ", velocity= " + velocity;
    }
}

