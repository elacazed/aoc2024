package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Direction;
import fr.ela.aoc2024.utils.Grid;
import fr.ela.aoc2024.utils.Position;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class D06 extends AoC {

    void solve(Path inputPath, int expected, int loops) {
        List<String> input = list(inputPath);

        Grid<Character> grid = Grid.parseCharactersGrid(input, c -> c.equals('.') ? null : c);
        Position start = grid.getPositionsOf('^').getFirst();
        grid.remove(start);
        Guard guard = new Guard(start);

        System.out.println("Part 1 ("+expected+") : " + guard.pathToExit(grid));
        Set<Position> path = new HashSet<>(guard.path.keySet());
        path.remove(start);
        int counter = 0;
        for (Position p : path) {
            grid.put(p, '#');
            guard.reset();
            if (guard.isLooping(grid)) {
                counter++;
            }
            grid.remove(p);
        }
        System.out.println("Part 2 ("+loops+") : " + counter);
    }

    private class Guard {
        private final Position start;
        private Position current;
        private Direction direction;
        Map<Position, Direction> path = new HashMap();

        public Guard(Position start) {
            this.start = start;
            this.current = start;
            this.direction = Direction.NORTH;
        }

        public boolean move(Grid<Character> grid) {
            Position next = direction.move(current);
            while (grid.contains(next)) {
                direction = direction.right();
                next = direction.move(current);// turn right
            }
            if (path.containsKey(current) && path.get(current).equals(direction)) {
                return false;
            }
            path.put(current, direction);
            current = next;
            return true;
        }

        public int pathToExit(Grid<Character> grid) {
            while (inBounds(grid)) {
                move(grid);
            }
            return path.size() - 1;
        }


        public boolean isLooping(Grid<Character> grid) {
            boolean go = true;
            while (inBounds(grid) && go) {
                go = move(grid);
            }
            return inBounds(grid);
        }

        public boolean inBounds(Grid grid) {
            return grid.inBounds(current);
        }

        public void reset() {
            current = start;
            path.clear();
            direction = Direction.NORTH;
        }
    }


    @Override
    public void run() {
        System.out.println("Test : ");
        solve(getTestInputPath(), 41, 6);
        System.out.println("Real : ");
        solve(getInputPath(), 4789, 0);
    }
}
