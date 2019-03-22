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
        final int maxTime = 4000;
        boolean polarized = false;
        double va = 0;

        long start = System.currentTimeMillis();

        for(int i=0; !polarized;  i++){

            Set<Particle> newParticles = calculateNewParticles(ans, eta, L);
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

        double totalSin = 0;
        double totalCos = 0;
        for(Particle particle : neighbours) {
            totalSin += Math.sin(particle.getAngle());
            totalCos += Math.cos(particle.getAngle());
        }


        double n = new Random().nextDouble()*eta-eta/2;
        return Math.atan2(totalSin/(neighbours.size() + 1), totalCos/(neighbours.size() + 1)) + n;
    }

    public static Point calculatePosition(Particle p, double angle, int L) {
        double x = p.getLocation().getX() + p.getVelocity() * Math.cos(angle);
        double y = p.getLocation().getY() + p.getVelocity() * Math.sin(angle);

        x = x%L;
        y= y%L;

        if(y<0){
            y+=L;
        }
        if(x<0){
            x+=L;
        }


        return new Point(x, y);
    }

    public static double calculateVa(Set<Particle> particles) {
        double totalVx = 0;
        double totalVy = 0;
        double velocity = 0;

        for(Particle p : particles) {
            totalVx += p.getVelocity()*Math.cos(p.getAngle());
            totalVy += p.getVelocity()*Math.sin(p.getAngle());

            velocity = p.getVelocity();
        }

        totalVx /= particles.size();
        totalVy /= particles.size();

        double totalVi = Math.sqrt(Math.pow(totalVx, 2) + Math.pow(totalVy, 2));

        return totalVi / (velocity);
    }

    public static Set<Particle> calculateNewParticles(Map<Particle, Set<Particle>> map, double eta, int L) {
        Set<Particle> particles = new HashSet<>();
        double angle;

        for(Particle p : map.keySet()) {
            angle = calculateAngle(p, map, eta);
            Particle particle = new Particle(p.getId(), p.getRatio(), null, calculatePosition(p, angle, L), p.getVelocity(), angle);
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
                    .append(current.calculateVx()*1000)
                    .append("")
                    .append(current.calculateVy()*1000)
                    .append(current.getAngle())
                    .append("\r\n");
        }
        return builder.toString();
    }

}
