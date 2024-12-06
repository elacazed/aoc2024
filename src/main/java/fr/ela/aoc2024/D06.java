package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Direction;
import fr.ela.aoc2024.utils.Grid;
import fr.ela.aoc2024.utils.Position;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class D06 extends AoC {

    void solve(Path path, String expected) {
        List<String> input = list(path);

        Grid<Character> grid = Grid.parseCharactersGrid(input, c -> c.equals('.') ? null : c);
        Position start = grid.getPositionsOf('^').getFirst();
        grid.remove(start);
        Guard guard = new Guard(start);

        System.out.println("Part 1 ("+expected+") : " + guard.pathToExit(grid));
    }

    private class Guard {
        private final Position start;
        private Position current;
        private Direction direction;
        Set<Position> path = new HashSet<>();

        public Guard(Position start) {
            this.start = start;
            this.current = start;
            this.direction = Direction.NORTH;
        }

        public void move(Grid<Character> grid) {
            Position next = direction.move(current);
            while (grid.contains(next)) {
                direction = direction.right();
                next = direction.move(current);// turn right
            }
            path.add(current);
            current = next;
        }

        public int pathToExit(Grid<Character> grid) {
            while (inBounds(grid)) {
                move(grid);
            }
            path.remove(current);
            return path.size();
        }

        public boolean inBounds(Grid grid) {
            return grid.inBounds(current);
        }

    }


    @Override
    public void run() {
        System.out.println("Test : ");
        solve(getTestInputPath(), "41");
        System.out.println("Real : ");
        solve(getInputPath(), "4789");
    }
}
