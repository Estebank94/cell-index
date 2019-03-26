import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;


public class Main {
    public static void main(String[] args) {
        if( args.length < 5 ) {
            System.out.println("Wrong arguments");
            return;
        }
        int n = Integer.parseInt(args[0]); //1000
        int l = Integer.parseInt(args[1]); //50
        int m = Integer.parseInt(args[2]); //30
        double rc = Double.parseDouble(args[3]); //0.5
        String periodic = args[4];
        if( (double) l/m < rc ) {
            System.out.println("l/m must be higher than rc!");
            return;
        }
        System.out.println("Generating Inputs!");
        String staticString = generateStaticFileString(n, l, m, rc, periodic.equals("periodic") ? true : false, 0);
        String dynamicString = generateDynamicFileString(n, l);

        writeToFile(staticString, "staticFile","/Users/martinascomazzon/Documents/2019/files/");
        writeToFile(dynamicString, "dynamicFile","/Users/martinascomazzon/Documents/2019/files/");

    }

    public static String generateDynamicFileString(int n, int l){
        Random r = new Random();
        StringBuilder builder = new StringBuilder();
        builder.append("t0\r\n");
        for(int i = 0; i < n ; i++){
            builder.append(r.nextDouble()*l)
                    .append(" ")
                    .append(r.nextDouble()*l)
                    .append(" ")
                    .append(r.nextDouble())
                    .append(" ")
                    .append(r.nextDouble())
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
        builder.append(periodic ? "periodic" + "\r\n" : "dfkfkf" + "\r\n");
        for(int i = 0; i < n ; i++){
            builder.append( radius == 0 ? radius : r.nextInt(l))
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
