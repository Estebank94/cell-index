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

    private int addBorders(Set<Particle> limits) {
        double delta= 1/10.0;
        int count=1;
        double radius = 0.15;
        for(double i =-radius ; i< L+radius;i+=delta){
            limits.add(new Particle(-count++,-radius,i,0,0,radius,0,0,Vector2D.ZERO));
            limits.add(new Particle(-count++,W+radius,i,0,0,radius,0,0,Vector2D.ZERO));
        }
        for (double j= -radius; j< W+radius;j+=delta){
            if(j < W/2 - D/2 || j > (W/2 + D/2)){
                limits.add(new Particle(-count++,j,-radius,0,0,radius,0,0,Vector2D.ZERO));
            }
            limits.add(new Particle(-count++,j,L+radius,0,0,radius,0,0,Vector2D.ZERO));
        }
        return count;
    }

    private String generateFileString(Set<Particle> particles) {
        StringBuilder builder = new StringBuilder();
        Set<Particle> borders = new HashSet<>();
        addBorders(borders);
        builder.append(particles.size() + borders.size() + "\r\n")
                .append("//ID\t X\t Y\t Radius\t VX\t VY\t FN\t \r\n");

        appendParticles(particles, builder);
        appendParticles(borders, builder);

        return builder.toString();
    }

    private String generateFileString(double totalPedestriansThatEvacuated, double time) {
        StringBuilder builder = new StringBuilder();
        builder.append(totalPedestriansThatEvacuated)
                .append(" ")
                .append(time)
                .append("\r\n");
        return builder.toString();
    }

    private String generateFileString(double time) {
        StringBuilder builder = new StringBuilder();
        builder.append(time)
                .append("\r\n");
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


    public void appendToFile(String data) {
        try {
            bw.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendToFile(Set<Particle> allParticles){
        appendToFile(generateFileString(allParticles));
    }

    public void appendToFile(double totalPedestriansThatEvacuated, double time){
        appendToFile(generateFileString(totalPedestriansThatEvacuated, time));
    }

    public void appendToFile(double time){
        appendToFile(generateFileString(time));
    }


    public void close() {
        if (bw != null) try {
            bw.flush();
            bw.close();
        } catch (IOException ioe2) {
            // just ignore it
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
