public class Main {

    public static void main( String[] args ){
        long start = System.currentTimeMillis();
        Engine engine = new Engine(100);
//        engine.test_timeUntilCrashWithParticle();
//        engine.test_timeCrashedWithWall();
//        engine.test_evolution();
        engine.start(100);
        long end = System.currentTimeMillis();
        System.out.println("Total time:" + (end - start) + " ms");
//        engine.start(100, "/Users/estebankramer1/Desktop/results");
    }
}
