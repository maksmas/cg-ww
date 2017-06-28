import java.util.*;
import java.io.*;
import java.math.*;

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int size = in.nextInt();
        int unitsPerPlayer = in.nextInt();
        Field f = new Field(size);

        while (true) {
            for (int i = 0; i < size; i++) {
                String row = in.next();
                f.addRow(row, i);
            }
            for (int i = 0; i < unitsPerPlayer; i++) {
                int unitX = in.nextInt();
                int unitY = in.nextInt();
                f.addPlayer(unitX, unitY, i);
            }
            for (int i = 0; i < unitsPerPlayer; i++) {
                int otherX = in.nextInt();
                int otherY = in.nextInt();
            }
            int legalActions = in.nextInt();
            String bestMove = "";
            int bestMoveScore = -10;
            String di = "";
            for (int i = 0; i < legalActions; i++) {
                String atype = in.next();
                int index = in.nextInt();
                String dir1 = in.next();
                String dir2 = in.next();
                if (bestMoveScore == 6) continue;
                int moveScore = f.evaluateMove(atype, dir1, dir2, index);
                if (moveScore > bestMoveScore) {
                    bestMove = atype + " " + index + " " + dir1 + " " + dir2;
                    bestMoveScore = moveScore;
                    di = f.debugInfo;
                }
            }
            // f.printGrid();
            // System.err.println(di);
            System.out.println(bestMove);
        }
    }
}

class Field {
    final int EMPTY = -1;
    int size;
    int[][] grid;
    int x1, y1, x2, y2;
    String debugInfo = "";
    
    public Field(int size) {
        this.size = size;
        grid = new int[size][size];
    }
    
    void addRow(String row, int i) {
        grid[i] = new int[size];
        for (int j = 0; j < size; ++j) {
            char c = row.charAt(j);
            grid[i][j] = c == '.' ? EMPTY : Integer.valueOf(String.valueOf(c));
        }
    }
    
    void addPlayer(int x, int y, int i) {
        if (i == 0) {
            this.x1 = x;
            this.y1 = y;
        } else {
            this.x2 = x;
            this.y2 = y;
        }
    }
    
    int evaluateMove(String moveType, String moveDir, String buildDir, int i) {
        int x, y;
        if (i == 0) {
            x = x1;
            y = y1;
        } else {
            x = x2;
            y = y2;
        }
        int mx, my, willBuildTo;
        boolean willBeBlocked = false;
        if (moveType.equals("MOVE&BUILD")) {
        //FIXME bug in eval
            mx = x + getXOffset(moveDir);
            my = y + getYOffset(moveDir);
            
            int bx = mx + getXOffset(buildDir);
            int by = my + getYOffset(buildDir);
            
            willBuildTo = grid[by][bx] + 1;
            willBeBlocked = this.wiilBeBlocked(mx,my,bx,by);
        } else  {
            mx = x;
            my = y;
            
            willBuildTo = 0;
        }
        
        int willBeAt = grid[my][mx];
        
        //TODO different eval for different moves
        debugInfo = willBeAt + " " + willBuildTo;
        if (willBuildTo == 4 || willBuildTo - willBeAt > 1) willBuildTo = 0;
        return willBeBlocked ? -1 : (willBeAt + willBuildTo);
    }
    
    boolean wiilBeBlocked(int x, int y, int bx, int by) {
        int height = grid[y][x];

        for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                int sx = x + i;
                int sy = y + j;
                if ((i == 0 && j == 0) || sx < 0 || sx == size || sy < 0 || sy == size) continue;
                int bn = grid[sy][sx];
                if (sy == by && sx == bx) bn++;
                if (bn >= 0 && bn < 4 && bn <= height + 1) return false;
            }
        }
        return true;
    }
    
    
    void printGrid() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                int c = grid[i][j];
                System.err.print(c == EMPTY ? '.' : "" + c);
            }
            System.err.println();
        }
    }
    
    int getYOffset(String dir) {
        char c = dir.charAt(0);
        if (c == 'N') return -1;
        else if (c == 'S') return 1;
        else return 0;
    }
    
    int getXOffset(String dir) {
        if (dir.contains("W")) return -1;
        else if (dir.contains("E")) return 1;
        else return 0;
    }
}