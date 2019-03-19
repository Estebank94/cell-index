import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        for(Particle particle : ans.keySet()){
            String toWrite = Engine.generateFileString(particle, ans.get(particle), particles);
            Engine.writeToFile(toWrite,particle.getId(),"/Users/estebankramer1/Desktop/results");
        }

        for(Map.Entry<Particle,Set<Particle>> a : ans.entrySet()){

            System.out.print(a.getKey().getId()+" : ");

            for(Particle p: a.getValue()){
                System.out.print(p.getId()+" ");
            }

            System.out.println();
        }
    }

    public double calculateAngle(Particle p, Map<Particle, Set<Particle>> map, double eta) {
        Set<Particle> neighbours = map.get(p);
        Random r = new Random();
        double totalSin = 0;
        double totalCos = 0;
        for(Particle p : neighbours) {
            totalSin += Math.sin(p.getAngle());
            totalCos += Math.cos(p.getAngle());
        }
        totalSin = totalSin / neighbours.size();
        totalCos = totalCos / neighbours.size();

        return Math.atan2(totalSin, totalCos) + eta/2 * r.nextDouble();;
    }

    public Point calculatePosition(Particle p, double angle) {
        double x = p.getLocation().getX() + p.getVelocity() * Math.cos(angle);
        double y = p.getLocation().getY() + p.getVelocity() * Math.sin(angle);
        return new Point(x, y);
    }

    public double calculateVa(Set<Particle> particles) {
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

    public Set<Particle> OffLatice(Map<Particle, Set<Particle>> map, double eta) {
        Set<Particle> particles = map.keySet();
        double angle;
        double va;

        for(Particle p : particles) {
            angle = calculateAngle(p, map, eta);
            p.setAngle(angle);
            p.setLocation(calculatePosition(p, angle));
        }
        va = calculateVa(particles);

    }

    public static void writeToFile(String data, int inedx, String path){
        try {
            Files.write(Paths.get(path + "/results" + inedx + ".txt"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateFileString(Particle particle, Set<Particle> neighbours,Set<Particle> AllParticles){
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
