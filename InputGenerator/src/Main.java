import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Created by estebankramer on 14/03/2019.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Generating Inputs!");
        int n = 1000;
        int l = 50;
        int m = 30;
        double rc = 0.5;
        String staticString = generateStaticFileString(n, l, m, rc, false, 1);
        String dynamicString = generateDynamicFileString(n, l);

        writeToFile(staticString, "staticFile","/Users/estebankramer1/Desktop/input/");
        writeToFile(dynamicString, "dynamicFile","/Users/estebankramer1/Desktop/input/");

    }

    public static String generateDynamicFileString(int n, int l){
        Random r = new Random();
        StringBuilder builder = new StringBuilder();
        builder.append("t0\r\n");
        for(int i = 0; i < n ; i++){
            builder.append(r.nextInt(l))
                    .append(" ")
                    .append(r.nextInt(l))
                    .append(" ")
                    .append(r.nextInt(l))
                    .append(" ")
                    .append(r.nextInt(l))
                    .append("\r\n");

        }
        return builder.toString();
    }

    public static String generateStaticFileString(int n, int l, int m, double rc, boolean periodic, double radius){
        Random r = new Random();
        StringBuilder builder = new StringBuilder();
        builder.append(n + "\r\n");
        builder.append(l + "\r\n");
        builder.append(m + "\r\n");
        builder.append(rc + "\r\n");
        builder.append(periodic ? "periodic" : "dfkfkf" + "\r\n");
        for(int i = 0; i < n ; i++){
            builder.append( radius > 0 ? radius : r.nextInt(l))
                    .append(" ")
                    .append("property" + i)
                    .append("\r\n");

        }
        return builder.toString();
    }



    public static void writeToFile(String data, String fileName, String path){
        try {
            Files.write(Paths.get(path + fileName + ".txt"), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
