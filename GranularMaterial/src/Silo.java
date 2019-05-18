import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Silo {
    private Set<Particle> particles;
    private double L, W, D;
    private double time, dt;
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
        this.time = 0;
        this.dt = 0.1 * Math.sqrt(mass/Math.pow(10, 5));
        this.printingStep = printingStep;

        System.out.println("Dt is :" + dt);

        System.out.println("Adding particles...");
        int i = 0;
        int tries = 0;
        while(i < 1000 && tries < MAX_TRIES) {
            if(addPartilce()) {
                i++;
                System.out.println(i);
                tries = 0;
            } else {
                tries++;
            }
        }

        System.out.println(particles.size() + " particles added.");
    }

    public void start(String outPath,double finalTime){

        Beeman integrator = new Beeman(new ForceCalculator(L, W, D)
            , new NeighbourCalculator(L,W,0, maxR), dt,particles);

        Printer printer = new Printer(outPath, L, W, D);
        timePrinter = new Printer(outPath+"_time", 0, 0, 0);
        Printer energyPrinter = new Printer(outPath+"_energy", 0, 0, 0);

        int iterations = 0;
        while(time < finalTime && iterations < 100000) {

            this.particles = integrator.integrate(particles);

            this.particles = removeFallenParticles(time);

            if(iterations % printingStep == 0) {
                printer.appendToFile(particles);
                System.out.println("Time: " + time + "\t iterations: " + iterations);
            }

            getEnergy(energyPrinter);

            time += dt;
            iterations++;
        }

    }

    private boolean addPartilce() {
        Random rand = new Random();

        double radius = rand.nextDouble() * (maxR - minR) + minR;
        double x = rand.nextDouble() * (W - 2 * radius) + radius;
        double y = rand.nextDouble() * (L - 2 * radius) + radius;

        Particle p = new Particle(particles.size(), x, y, 0,0, radius, mass);

        for(Particle other : particles) {
            if(p.overlaps(other)) {
                return false;
            }
        }

        particles.add(p);
        return true;
    }

    private void addParticle(Particle oldParticle, Set<Particle> newParticles) {
        Random rand = new Random();
        boolean done = false;
        double radius = oldParticle.getRadius();
        double x = 0, y = 0;
        Particle p = oldParticle;



        while(!done) {
            x = rand.nextDouble() * (W - 2 * radius) + radius;
            y = rand.nextDouble() * (L/8 - 2 * radius) + radius + L * 7.0/8;

            p = new Particle(oldParticle.getId(), x, y, 0, 0, radius, mass);

            done = !isOverlapingOtherParticle(p, this.particles);
        }

        newParticles.add(p);
    }

    private boolean isOverlapingOtherParticle(Particle p, Set<Particle> newParticles) {
        for(Particle other : newParticles) {
            if(p.overlaps(other)) {
                return true;
            }
        }
        return false;
    }

    private Set<Particle> removeFallenParticles(double time) {
        Set<Particle> newParticles = new HashSet<>();
        for(Particle p : this.particles) {
            if(p.getPosition().getY() > -L/10) {
                newParticles.add(p);
            } else {
                addParticle(p, newParticles);
                timePrinter.appendToFile(time + "\n");
                timePrinter.flush();
            }
        }
        return newParticles;
    }

    private void getEnergy(Printer energyPrinter) {
        double speed2 = 0;
        for(Particle p : particles) {
            speed2 += Math.pow(p.getSpeed().abs(), 2);
        }

        double energy = 0.5 * mass * speed2;

        energyPrinter.appendToFile(energy + "\n");
        energyPrinter.flush();
    }
}
