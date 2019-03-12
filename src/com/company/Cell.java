package com.company;

import java.util.List;

/**
 * Created by estebankramer on 12/03/2019.
 */
public class Cell {
    List<Particle> particles;
    Boolean checked;

    public Cell(List<Particle> particles) {
        this.particles = particles;
        this.checked = false;
    }

    public List<Particle> getParticles() {
        return particles;
    }

    public void setParticles(List<Particle> particles) {
        this.particles = particles;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
