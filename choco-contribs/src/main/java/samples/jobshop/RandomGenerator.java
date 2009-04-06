package samples.jobshop;

public final class RandomGenerator {
    private final static int A = 16807;
    private final static int B = 127773;
    private final static int C = 2836;
    private final static int M = (0x1 << 31) - 1;
    private static int seed = 1;

    private RandomGenerator() {
    }

    private static double nextRand() {
        int rand = A * (seed % B) - (seed / B) * C;
        if (rand < 0) {
            rand += M;
        }
        seed = rand;
        return (double) rand / M;
    }

    private static int nextRand(int a, int b) {
        return (int) Math.floor(a + nextRand() * (b - a + 1));
    }

    public static int[][] randMatrix(int seed, int x, int y) {
        RandomGenerator.seed = seed;
        int[][] matrix = new int[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                matrix[i][j] = nextRand(1, 99);
            }
        }
        return matrix;
    }

    public static int[][] randShuffle(int seed, int x, int y) {
        RandomGenerator.seed = seed;
        int[][] matrix = new int[x][y];

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                matrix[i][j] = j;
            }
            for (int j = 0; j < y; j++) {
                final int s = nextRand(j, y - 1);
                final int t = matrix[i][j];
                matrix[i][j] = matrix[i][s];
                matrix[i][s] = t;
            }
        }
        return matrix;
    }
}
