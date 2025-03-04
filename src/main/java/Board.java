import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.ArrayList;
import java.util.Arrays;

// Skula's Part
public class Board {

    private final int[][] tiles;
    private final int n;
    private final int hamming;
    private final int manhattan;

    public Board(int[][] tiles) {
        n = tiles.length;
        this.tiles = new int[n][n];

        // Deep copy for immutability
        for (int i = 0; i < n; i++) {
            System.arraycopy(tiles[i], 0, this.tiles[i], 0, n);
        }

        // Calculate Hamming and Manhattan distances ONCE in the constructor -- thanks Alex for the request
        this.hamming = calculateHamming();
        this.manhattan = calculateManhattan();
    }

    public int dimension() {
        return n; // O(1)
    }

    // Hamming distance: Number of tiles out of place
    private int calculateHamming() {
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Avoid comparing the blank tile (0)
                if (tiles[i][j] != 0 && tiles[i][j] != (i * n) + j + 1) {
                    count++;
                }
            }
        }
        return count;
    }

    public int hamming() {
        return hamming;
    }

    // Manhattan distance: Sum of distances to goal positions
    private int calculateManhattan() {
        int sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int value = tiles[i][j];
                if (value != 0) { // Avoid calculating for the blank tile
                    int goalRow = (value - 1) / n;
                    int goalCol = (value - 1) % n;
                    sum += Math.abs(i - goalRow) + Math.abs(j - goalCol);
                }
            }
        }
        return sum;
    }

    public int manhattan() {
        return manhattan;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(n).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2d ", tiles[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Board board = (Board) other;
        return Arrays.deepEquals(tiles, board.tiles);
    }

    public Board twin() {
        int[][] twinTiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(tiles[i], 0, twinTiles[i], 0, n);
        }

        // what is this???? IntelliJ recommended me this refactoring and I don't know what I'm looking at
        outerLoop:
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n - 1; j++) {
                if (twinTiles[i][j] != 0 && twinTiles[i][j + 1] != 0) {
                    // Swap the tiles
                    int temp = twinTiles[i][j];
                    twinTiles[i][j] = twinTiles[i][j + 1];
                    twinTiles[i][j + 1] = temp;
                    break outerLoop;
                }
            }
        }
        return new Board(twinTiles);
    }


    public boolean isGoal() {
        return hamming == 0;
    }

    public Iterable<Board> neighbors() {
        ArrayList<Board> neighbors = new ArrayList<>();
        int zeroRow = -1, zeroCol = -1;

        outerLoop:
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    zeroRow = i;
                    zeroCol = j;
                    break outerLoop;
                }
            }
        }

        // Possible moves (up, down, left, right)
        int[][] moves = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] move : moves) {
            int newRow = zeroRow + move[0];
            int newCol = zeroCol + move[1];

            if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n) {
                int[][] neighborTiles = new int[n][n];
                for (int i = 0; i < n; i++) {
                    System.arraycopy(tiles[i], 0, neighborTiles[i], 0, n);
                }

                neighborTiles[zeroRow][zeroCol] = neighborTiles[newRow][newCol];
                neighborTiles[newRow][newCol] = 0;
                neighbors.add(new Board(neighborTiles));
            }
        }

        return neighbors;
    }

    public static void main(String[] args) {
        // create initial board from file
        In in = new In("./8puzzle-test-files/puzzle3x3-07.txt");
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
