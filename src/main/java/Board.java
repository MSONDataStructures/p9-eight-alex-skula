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
        // construct a board from an n-by-n array of tiles
        // (where tiles[i][j] = tile at row i, column j)
        n = tiles.length;
        this.tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            // using arraycopy because intellij keeps bugging me about it ;D
            System.arraycopy(tiles[i], 0, this.tiles[i], 0, n);
        }

        // Calculate Hamming and Manhattan distances in constructor
        hamming = calculateHamming();
        manhattan = calculateManhattan();
    }

    public int dimension() {
        // board dimension n
        return n;
    }

    private int calculateHamming() {
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != 0 && tiles[i][j] != i * n + j + 1) {
                    count++;
                }
            }
        }
        return count;
    }

    public int hamming() {
        // number of tiles out of place
        return hamming;
    }


    private int calculateManhattan() {
        int sum = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] != 0) {
                    int goalRow = (tiles[i][j] - 1) / n;
                    int goalCol = (tiles[i][j] - 1) % n;
                    sum += Math.abs(i - goalRow) + Math.abs(j - goalCol);
                }
            }
        }
        return sum;
    }

    public int manhattan() {
        // sum of Manhattan distances between tiles and goal
        return manhattan;
    }
    public String toString() {
        // string representation of this board
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

    public boolean equals(Object other) {
        // does this board equal y?
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;

        Board that = (Board) other;
        return Arrays.deepEquals(this.tiles, that.tiles);
    }

    public Board twin() {
        // a board that is obtained by exchanging any pair of tiles
        int[][] twinTiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(tiles[i], 0, twinTiles[i], 0, n);
        }

        // Find two non-zero tiles to swap
        int row1 = -1, col1 = -1, row2, col2;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (twinTiles[i][j] != 0) {
                    if (row1 == -1) {
                        row1 = i;
                        col1 = j;
                    } else {
                        row2 = i;
                        col2 = j;
                        // Swap the tiles
                        int temp = twinTiles[row1][col1];
                        twinTiles[row1][col1] = twinTiles[row2][col2];
                        twinTiles[row2][col2] = temp;
                        return new Board(twinTiles);  // Return immediately after swapping
                    }
                }
            }
        }
        return new Board(twinTiles); //Should never reach here given valid inputs
    }

    public boolean isGoal() {
        // is this board the goal board?
        return hamming == 0;
    }

    public Iterable<Board> neighbors() {
        // all neighboring boards
        ArrayList<Board> neighbors = new ArrayList<>();
        int zeroRow = -1;
        int zeroCol = -1;

        // Find the blank square
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    zeroRow = i;
                    zeroCol = j;
                    break;
                }
            }
        }

        // Generate neighbors by swapping the blank square with adjacent tiles
        int[][] moves = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Up, Down, Left, Right
        for (int[] move : moves) {
            int newRow = zeroRow + move[0];
            int newCol = zeroCol + move[1];

            if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n) {
                int[][] neighborTiles = new int[n][n];
                for (int i = 0; i < n; i++) {
                    System.arraycopy(tiles[i], 0, neighborTiles[i], 0, n);
                }
                // Swap blank square
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
