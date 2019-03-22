import java.io.*;
import java.util.*;

/**
 * Created by estebankramer on 13/03/2019.
 */
public class Parser {
    private  BufferedReader staticBuffer;
    private  BufferedReader dynamicBuffer;
    int n;
    int l;
    int m;
    double rc;
    boolean periodic;
    List<Particle> particles;

    int time;

    public Parser(String s, String d) {

        File staticFile = new File(s);
        File dynamicFile = new File(d);
        time=0;
        particles = new ArrayList<>();
        Random r = new Random();

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

            for (int i=0; i<n; i++){
                String[] array= staticBuffer.readLine().split(" ");
                particles.add(new Particle(i, Double.parseDouble(array[0]),new Property<>("property"),null,0.03,2*Math.PI * r.nextDouble()));
            }
            staticBuffer.close();

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public int getN() {
        return n;
    }

    public int getL() { return l; }

    public int getM() {
        return m;
    }

    public double getRc() {
        return rc;
    }

    public boolean periodic() {
        return periodic;
    }

    public Set<Particle> getParticles() {
        Set<Particle> ans = new HashSet<>();

        try{
            if (!dynamicBuffer.readLine().equals("t"+time)){
                throw new IllegalArgumentException();
            }
            for(int i =0; i<n ; i++){
                String line = dynamicBuffer.readLine();
                String[] data = line.split(" ");
                Particle current = particles.get(i);
                double x=Double.parseDouble(data[0]);
                double y=Double.parseDouble(data[1]);
                //double velocity=Double.parseDouble(data[2]);
                double velocity = 0.03;
                ans.add(new Particle(current.getId(),current.getRatio(),null,new Point(x,y),velocity,current.getAngle()));
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        return ans;
    }

}

