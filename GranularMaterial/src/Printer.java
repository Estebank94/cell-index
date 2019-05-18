import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Printer {

    BufferedWriter bw;
    double L;
    double W;
    double D;

    public Printer(String path, double L, double W,double D) {

        this.L = L;
        this.W = W;
        this.D = D;

        try {
            bw = new BufferedWriter(new FileWriter(path + ".txt", true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int addBorders(Set<Particle> borders) {
        double radius = 0.01;
        double step = radius/2;
        int count =- 1000000;

        for (double i = -radius; i < W + radius; i += step){
            if(i > W - (W/2 - D/2) || i < W/2 - D/2){
                borders.add(new Particle(count++,i,-radius,0,0,radius,0));
            }
        }

        for(double i =- radius ; i < L+radius; i += step){
            borders.add(new Particle(count++,-radius,i,0,0,radius,0));
            borders.add(new Particle(count++,W+radius,i,0,0,radius,0));
        }

        return borders.size();
    }

    private String generateFileString(Set<Particle> particles) {
        Set<Particle> borders = new HashSet<>();

        addBorders(borders);

        StringBuilder builder = new StringBuilder()
                .append(particles.size() + borders.size() + "\r\n")
                .append("//ID\t X\t Y\t Radius\t VX\t VY\t FN\t \r\n");
        
        appendParticles(particles, builder);
        appendParticles(borders, builder);

        return builder.toString();
    }

    private void appendParticles(Set<Particle> particles, StringBuilder builder) {
        for (Particle p : particles) {
            builder.append(p.getId())
                    .append(" ")
                    .append(p.getPosition().x)
                    .append(" ")
                    .append(p.getPosition().y)
                    .append(" ")
                    .append(p.getRadius())
                    .append(" ")
                    .append(new Double(p.getSpeed().x).floatValue())
                    .append(" ")
                    .append(new Double(p.getSpeed().y).floatValue())
                    .append(" ")
                    .append(new Double(p.getTotalFn() / Math.PI * 2 * p.getRadius()).floatValue())
                    .append("\r\n");
        }
    }

    public void appendToFile(Set<Particle> particles){
        appendToFile(generateFileString(particles));
    }

    public void appendToFile(String data) {
        try {
            bw.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void flush() {
        try {
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
