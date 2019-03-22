import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Main {
    private final static double VELOCITY = 0.03;

    public static void main (String [ ] args) {

        if(args.length < 3) {
            System.out.println("Incorrecta cantidad de argumentos");
            return;
        }

        Parser parser = new Parser(args[0],args[1]);

        int L = parser.getL();

        int M = parser.getM();

        double Rc = parser.getRc();

        boolean periodic = parser.periodic();

        Set<Particle> particles = parser.getParticles();

        double eta = Double.parseDouble(args[2]);

        Engine engine = new Engine(L,M,Rc,periodic,particles);

        Map<Particle,Set<Particle>> ans = engine.start();

        /* tiempo maximo donde tenemos que tener en cuenta cuando llega a un estado
        estacionario*/
        final int maxTime = 1000;
        boolean polarized = false;
        double va = 0;

        long start = System.currentTimeMillis();

        for(int i=0; i<maxTime && !polarized;  i++){

            Set<Particle> newParticles = calculateNewParticles(ans, eta);
            va = calculateVa(newParticles);
            engine = new Engine(L, M, Rc, periodic, newParticles);
            ans = engine.start();

            String file = generateFileString(newParticles);
            writeToFile(file, i, args[3]);

            if(va > 0.999999){
                polarized = true;
            }

        }

        long end = System.currentTimeMillis();
        System.out.println("Va:" + va + "\t");

        System.out.println("Time: " + (end-start) + "ms");


    }


    public static double calculateAngle(Particle p, Map<Particle, Set<Particle>> map, double eta) {
        Set<Particle> neighbours = map.get(p);
        Random r = new Random();
        double totalSin = 0;
        double totalCos = 0;
        for(Particle particle : neighbours) {
            totalSin += Math.sin(particle.getAngle());
            totalCos += Math.cos(particle.getAngle());
        }
        totalSin = totalSin / neighbours.size();
        totalCos = totalCos / neighbours.size();

        return Math.atan2(totalSin, totalCos) + eta/2 * r.nextDouble();;
    }

    public static Point calculatePosition(Particle p, double angle) {
        double x = p.getLocation().getX() + p.getVelocity() * Math.cos(angle);
        double y = p.getLocation().getY() + p.getVelocity() * Math.sin(angle);
        return new Point(x, y);
    }

    public static double calculateVa(Set<Particle> particles) {
        double vx = 0;
        double vy = 0;

        for(Particle p : particles) {
            vx += Math.cos(p.getAngle()) * p.getVelocity();
            vy += Math.sin(p.getAngle()) * p.getVelocity();
        }
        vx /= particles.size();
        vy /= particles.size();

        return 1/(particles.size() * VELOCITY) * Math.sqrt(Math.pow(vx, 2) + Math.pow(vy, 2));
    }

    public static Set<Particle> calculateNewParticles(Map<Particle, Set<Particle>> map, double eta) {
        Set<Particle> particles = new HashSet<>();
        double angle;

        for(Particle p : map.keySet()) {
            angle = calculateAngle(p, map, eta);
            Particle particle = new Particle(p.getId(), p.getRatio(), null, calculatePosition(p, angle), p.getVelocity(), angle);
            particles.add(particle);
        }

        return particles;

    }

    public static void writeToFile(String data, int index, String path){
        try {
            Files.write(Paths.get(path + "/results" + index + ".txt"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateFileString(Set<Particle> AllParticles){
        StringBuilder builder = new StringBuilder()
                .append(AllParticles.size())
                .append("\r\n")
                .append("//ID\t X\t Y\t Radius\t vx\t vy\t Angle\r\n");
        for(Particle current: AllParticles){
            builder.append(current.getId())
                    .append(" ")
                    .append(current.getLocation().getX())
                    .append(" ")
                    .append(current.getLocation().getY())
                    .append(" ")
                    .append(current.getRatio())
                    .append(" ")
                    .append(current.calculateVx())
                    .append("")
                    .append(current.calculateVy())
                    .append(current.getAngle())
                    .append("\r\n");
        }
        return builder.toString();
    }

}
