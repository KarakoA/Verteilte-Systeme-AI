import mapgenerator.HeightMapGeneratorMultiThreaded;
import utils.ImageWriter;

import java.io.IOException;
import java.util.Random;

public class Main {
    /**
     * Application entry point
     *
     * @param args not used
     */
    public static void main(String[] args) throws IOException {
        int seed = new Random().nextInt(15455);//13088

        System.out.println("Seed: "+seed);
        HeightMapGeneratorMultiThreaded generator = new HeightMapGeneratorMultiThreaded(seed, 150, 4, 2f, 0.5f);
        long t1 = System.currentTimeMillis();
        float[][] data = generator.generateMultiThreaded(1024, 1024,4);
        long t2 = System.currentTimeMillis();
        System.out.println("Generate took: " + (t2 - t1)+" ms");

        new ImageWriter().writeHeightMapToImage("heightmap.png", data);
        long t3 = System.currentTimeMillis();
        System.out.println("Write took: " + (t3 - t2)+" ms");
    }


}
