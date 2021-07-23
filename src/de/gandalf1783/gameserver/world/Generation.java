package de.gandalf1783.gameserver.world;

import java.awt.*;

public class Generation {

    public static long SEED;
    public static long OCEAN_SEED;

    /**
     *
     *                     ############################
     *                     #### IMPORTANT REMINDER ####
     *                     ############################
     *
     *
     *
     * This Class is capable of generating a OpenSimplexNoise Map and can determine which spot is equal to which tile.
     *
     * Keep Attention when changing this class, so the World-Generation Process (the Steps 1-2) does still match when
     * overlaying all results.
     *
     */


    public static int[][] overlayAllSteps(int[][] valuesStep1, int[][] valuesStep2) {
        int[][] finalArray = new int[valuesStep1.length][valuesStep1.length];

        for(int x = 0; x < valuesStep1.length; x++) {
            for(int y = 0; y < valuesStep1.length; y++) {

                if(valuesStep1[x][y] == valuesStep2[x][y]) {
                    finalArray[x][y] = valuesStep1[x][y];
                } else if(valuesStep2[x][y] != 0 ) {
                    finalArray[x][y] = valuesStep2[x][y];
                } else {
                    finalArray[x][y] = valuesStep1[x][y];
                }
            }
        }

        return finalArray;
    }

    /**
     * STEP 1 OF WORLD GEN
     * Puts Water (Seas) (6) onto the Land (0) and Sand (3). Might not create rivers or something like that.
     *
     */
    public static int[][] convertValuesToLandWaterMap(double[][] values) {
        int[][] output = new int[values.length][values.length];

        for(int x = 0; x < values.length; x++) {
            for(int y = 0; y < values.length; y++) {

                if(values[x][y] > 0.7) {
                    output[x][y] = 6; // Water
                } else if(values[x][y] > 0.58) {
                    output[x][y] = 3; // Sand
                } else {
                    output[x][y] = 200; // Grass
                }

            }
        }

        return output;
    }

    /**
     * STEP 2 OF WORLD GEN
     * Puts Ocean (5) or Water (6) or Sand(3) onto Land. 0 will be give when there is no Ocean or Sand.
     */
    public static int[][] convertValuesToLandOceanMap(double[][] values) {
        int[][] output = new int[values.length][values.length];

        for(int x = 0; x < values.length; x++) {
            for(int y = 0; y < values.length; y++) {

                if(values[x][y] > 0.8) {
                    output[x][y] = 5; // Ocean (DeepWaterTile)
                } else if(values[x][y] > 0.79) {
                    output[x][y] = 6; // Water
                } else if(values[x][y] > 0.78){
                    output[x][y] = 3; // Sand
                } else {
                    output[x][y] = 0; // Nothing
                }

            }
        }

        return output;
    }

    /**
     * Generates a Noise Map from given Seed (Generation.SEED).
     * @param zoomFactor Zoom-Factor to use
     * @param iterations Iteration to do. (Specifies Array-Size in X/Y Direction.
     * @return Values
     */
    public static double[][] generateNoiseMap(double zoomFactor, int iterations) {
        OpenSimplex2S noise = new OpenSimplex2S(SEED);

        double[][] values = new double[iterations][iterations];

        for(int x = 0; x < iterations; x++) {
            for(int y = 0; y < iterations; y++) {
                values[x][y] = noise.noise2(x/zoomFactor, y/zoomFactor);
            }
        }

        return values;
    }

}
