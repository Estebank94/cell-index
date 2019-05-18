import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Silo {

    private Set<Particle> particles;

    private double L, W, D;
    private double t, dt;
    private final static int MAX_TRIES = 500;

    private static double minR = 0.01; // m
    private static double maxR = 0.015; // m
    private static double mass = 0.01; // kg

    private double printingStep;

    private Printer timePrinter;

    // mu = 0.7
    // gama = 100 kg/s

    public Silo(double L, double W, double D, double printingStep) {

        particles = new HashSet<>();

        this.L = L;
        this.W = W;
        this.D = D;
        this.t = 0;
        this.dt = 0.1 * Math.sqrt(mass/Math.pow(10, 5));
        this.printingStep = printingStep;

        System.out.println("Dt is: " + dt);

        System.out.println("Adding particles...");
        int i = 0;
        int tries = 0;
        while(i < 1000 && tries < MAX_TRIES) {
            if(addParticle()) {
                i++;
                System.out.println(i);
                tries = 0;
            } else {
                tries++;
            }
        }

        System.out.println(particles.size() + " particles added.");
    }

    public void run(String outPath, double ft){

        double interactionR = 0;

        Beeman beeman = new Beeman(new ForceCalculator(L, W, D)
            ,new NeighbourCalculator(L,W,interactionR, maxR), dt, particles);

        Printer printer = new Printer(outPath, L, W, D);
        timePrinter = new Printer(outPath + "_time", 0, 0, 0);
        Printer energyPrinter = new Printer(outPath + "_energy", 0, 0, 0);

        int i = 0;
        while(t < ft && i < 100000) {

            this.particles = beeman.integrate(particles);

            this.particles = removeFallenParticles(t);

            if(i % printingStep == 0) {
                printer.appendToFile(particles);
                System.out.println("Time: " + t + "\t Iterations: " + i);
            }

            getEnergy(energyPrinter);

            t += dt;
            i++;
        }

    }

    private boolean addParticle() {
        Random r = new Random();

        double radius = r.nextDouble() * (maxR - minR) + minR;
        double x = r.nextDouble() * (W - 2 * radius) + radius;
        double y = r.nextDouble() * (L - 2 * radius) + radius;

        double xSpeed = 0;
        double ySpeed = 0;

        Particle p = new Particle(particles.size(), x, y, xSpeed,ySpeed, radius, mass);

        for(Particle other : particles) {
            if(p.overlaps(other)) {
                return false;
            }
        }

        particles.add(p);
        return true;
    }

    private void addFallenParticles(Particle old, Set<Particle> newParticles) {
        Random rand = new Random();
        boolean done = false;
        double r = old.getRadius();
        double x;
        double y;
        Particle p = old;

        while(!done) {
            x = rand.nextDouble() * (W - 2 * r) + r;
            y = rand.nextDouble() * (L/8 - 2 * r) + r + L * 7.0/8;

            double xSpeed = 0, ySpeed = 0;

            p = new Particle(old.getId(), x, y, xSpeed, ySpeed, r, mass);

            done = !isSuperimposed(p, this.particles);
        }

        newParticles.add(p);
    }

    private boolean isSuperimposed(Particle p, Set<Particle> newP) {
        for(Particle particle : newP) {
            if(p.overlaps(particle)) {
                return true;
            }
        }
        return false;
    }

    private Set<Particle> removeFallenParticles(double t) {
        Set<Particle> particles = new HashSet<>();
        for(Particle p : this.particles) {
            if(p.getPosition().getY() > (-L/10)) {
                particles.add(p);
            } else {
                addFallenParticles(p, particles);
                timePrinter.appendToFile(t + "\n");
                timePrinter.flush();
            }
        }
        return particles;
    }

    private void getEnergy(Printer energyPrinter) {
        double speed = 0;
        for(Particle p : particles) {
            speed += Math.pow(p.getSpeed().abs(), 2);
        }

        double energy = 0.5 * mass * speed;

        energyPrinter.appendToFile(energy + "\n");
        energyPrinter.flush();
    }
}
