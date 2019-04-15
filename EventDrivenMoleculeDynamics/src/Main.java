import java.util.ArrayList;
import java.util.List;

public class Main {

//    public static void main( String[] args ){
//        Engine engine = new Engine(100, 0);
//        engine.start(100, "/Users/estebankramer1/Desktop/results");
//    }

    /* mscomazzon: /Users/martinascomazzon/Documents/2019/ITBA/Simulacion de Sistemas/Resultados */
    /* ekramer: /Users/estebankramer1/Desktop/results */

    public static void main( String[] args ){
        Engine engine = new Engine(200, 200);
//        Engine engine = new Engine(100, 100, 10000);
        engine.start("/Users/martinascomazzon/Documents/2019/ITBA/Simulacion de Sistemas/Resultados");

//        for(int i = 0; i<=100; i++){
//            List<Integer> list = new ArrayList<>();
//            int m = (int)(Math.random()*100);
//            System.out.println(m);
//            list.add(m);
//        }

    }
}
