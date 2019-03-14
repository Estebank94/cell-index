package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by estebankramer on 13/03/2019.
 */
public class Parser {
    private BufferedReader staticBuffer;
    private BufferedReader dynamicBuffer;
    private int n;
    private int l;
    private int m;
    private double rc;
    private boolean periodic;
    private List<Particle> particles;


    private int time;
    private int quantity;

    public Parser(String st, String dy ) {

        File staticFile = new File(st);
        File dynamicFile = new File(dy);
        time = 0;
        quantity = 0;
        particles = new ArrayList<>();

        try {
            staticBuffer = new BufferedReader(new FileReader(staticFile));
            dynamicBuffer = new BufferedReader(new FileReader(dynamicFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            n = Integer.parseInt(staticBuffer.readLine());
            l = Integer.parseInt(staticBuffer.readLine());
            m = Integer.parseInt(staticBuffer.readLine());
            rc = Double.parseDouble(staticBuffer.readLine());
            periodic = staticBuffer.readLine().equals("periodic");

            for(int i = 0; i < n; i++) {
                double radius = Double.parseDouble(staticBuffer.readLine().split(" ")[0]);
                particles.add(new Particle(quantity++, radius, null, 0, 0, new Property<String>("property")));
            }

            staticBuffer.close();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public Set<Particle> getParticles() {
        Set<Particle> ans = new HashSet<>();

        try {
            if(!dynamicBuffer.readLine().equals("t" + time)) {
                throw new IllegalArgumentException();
            }
            for(int i = 0; i < n; i++) {
                String[] data = dynamicBuffer.readLine().split(" ");
                Particle current = particles.get(i);
                double x = Double.parseDouble(data[0]);
                double y = Double.parseDouble(data[1]);
                double velocity = Double.parseDouble(data[2]);
                double angle = Double.parseDouble(data[3]);
                ans.add(new Particle(current.getId(), current.getRadius(), new Point(x,y), velocity, angle, current.getProperty()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public BufferedReader getStaticBuffer() {
        return staticBuffer;
    }

    public void setStaticBuffer(BufferedReader staticBuffer) {
        this.staticBuffer = staticBuffer;
    }

    public BufferedReader getDynamicBuffer() {
        return dynamicBuffer;
    }

    public void setDynamicBuffer(BufferedReader dynamicBuffer) {
        this.dynamicBuffer = dynamicBuffer;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public double getRc() {
        return rc;
    }

    public void setRc(double rc) {
        this.rc = rc;
    }

    public boolean isPeriodic() {
        return periodic;
    }

    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }

    public void setParticles(List<Particle> particles) {
        this.particles = particles;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
