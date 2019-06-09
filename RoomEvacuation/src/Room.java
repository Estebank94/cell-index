import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Room {

    private Set<Particle> pedestrians;

//  Room
    private double L, W, D;

//  Simulation
    private double t, dt;
    private final static int NUM_PEDESTRIANS = 200;

//  Pedestrians
    private static double minR = 0.25; // m
    private static double maxR = 0.29; // m
    private static double mass = 80; // kg
    private final static double maxSpeed = 6;
    private final static double minSpeed = 0.8;
    private final static double desiredSpeed = 3;
//  Granular Force
    private final double kn = 1.2 * Math.pow(10,5);
    private final double kt = 2 * kn;

//  Driving Force
    private final double tau = 0.5;

//  Social Force
    private final double A = 2000;
    private final double B = 0.08;

    private double printingStep;


    // mu = 0.7
    // gama = 100 kg/s

    public Room(double L, double W, double D, double printingStep) {

        pedestrians = new HashSet<>();

        this.L = L;
        this.W = W;
        this.D = D;
        this.t = 0;
        this.dt = Math.pow(10, -4);
        this.printingStep = printingStep;

        System.out.println("Dt is: " + dt);

        System.out.println("Adding pedestrians...");
        addPedestrians(desiredSpeed);
        System.out.println(pedestrians.size() + " pedestrians added.");
    }

    public void run(String out){
        double time=0;
        double FPS = 60;
        double jump = 1/FPS;
        double nextTime = 0;
        double interactionR = 0;

        NeighbourCalculator neighbourCalculator = new NeighbourCalculator(L,W,interactionR, maxR);
        SocialForce socialForce = new SocialForce(A,B);
        GranularForce granularForce = new GranularForce(kn,kt);
        SelfPropellingForce selfPropellingForce = new SelfPropellingForce(tau);
        InteractionForce interactionForce = new InteractionForce(A,B,kn,kt, W, D);
        Beeman beeman = new Beeman(granularForce,socialForce,interactionForce,selfPropellingForce, neighbourCalculator, dt,pedestrians);

        Printer timePrinter = new Printer(out + "_time",L,W,D);
        Printer animationPrinter = new Printer(out + "_animation",L,W,D);
        animationPrinter.appendToFile(NUM_PEDESTRIANS);
        Printer outTimePrinter = new Printer(out + "_outTime", L, W, D);

        while(pedestrians.size()>0 /* && time < ft */){
            this.pedestrians = beeman.integrate(pedestrians);

            if(time>nextTime){
                animationPrinter.appendToFile(pedestrians);
                nextTime+=jump;
                System.out.println("Time: " + time + " | Remaining Pedestrians: " + pedestrians.size());
                timePrinter.appendToFile(NUM_PEDESTRIANS - pedestrians.size(), time);
            }
            removeEvacuatedPedestrians(pedestrians, outTimePrinter);
            time+=dt;
        }
        timePrinter.close();
    }

    private void addPedestrians(double desiredSpeed) {
        Random r = new Random();

        Vector2D target = new Vector2D(W/2, 0);

        while(pedestrians.size() < NUM_PEDESTRIANS) {
            double radius = r.nextDouble() * (maxR - minR) + minR;

            double x = r.nextDouble() * (W - 2 * radius) + radius;
            double y = r.nextDouble() * (L - 2 * radius) + radius;

            double xSpeed = 0;
            double ySpeed = 0;

//            double desiredSpeed = r.nextDouble()*(maxSpeed-minSpeed)+maxSpeed;

            Particle p = new Particle(pedestrians.size(), x, y, xSpeed,ySpeed, radius, mass, desiredSpeed, target);

            if(!isSuperimposed(p, pedestrians)) {
                pedestrians.add(p);
            }
        }
    }

    private boolean isSuperimposed(Particle p, Set<Particle> newP) {
        for(Particle particle : newP) {
            if(p.overlaps(particle)) {
                return true;
            }
        }
        return false;
    }

    private int removeEvacuatedPedestrians(Set<Particle> pedestrians, Printer outTimePrinter) {
        int evacuated = 0;
        Set<Particle> tobeRemoved= new HashSet<>();
        for (Particle p : pedestrians){
            if(p.getPosition().x > W/2 - D/2 && p.getPosition().x < (W/2 + D/2) && p.getPosition().y<=0){
                tobeRemoved.add(p);
                evacuated++;
            }
        }
        outTimePrinter.appendToFile(evacuated + t + "\n");
        outTimePrinter.flush();
        pedestrians.removeAll(tobeRemoved);
        return evacuated;
    }
}
