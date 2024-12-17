package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Direction;
import fr.ela.aoc2024.utils.Grid;
import fr.ela.aoc2024.utils.Path;
import fr.ela.aoc2024.utils.Position;
import fr.ela.aoc2024.utils.Walker;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class D16 extends AoC {

    public static final String TEST_MAZE = """
            ###############
            #.......#....E#
            #.#.###.#.###.#
            #.....#.#...#.#
            #.###.#####.#.#
            #.#.#.......#.#
            #.#.#####.###.#
            #...........#.#
            ###.#.#####.#.#
            #...#.....#.#.#
            #.#.#.###.#.#.#
            #.....#...#.#.#
            #.###.#.#.#.#.#
            #S..#.....#...#
            ###############"""; //7036

    public static final String TEST_MAZE_2 = """
            #################
            #...#...#...#..E#
            #.#.#.#.#.#.#.#.#
            #.#.#.#...#...#.#
            #.#.#.#.###.#.#.#
            #...#.#.#.....#.#
            #.#.#.#.#.#####.#
            #.#...#.#.#.....#
            #.#.#####.#.###.#
            #.#.#.......#...#
            #.#.###.#####.###
            #.#.#...#.....#.#
            #.#.#.#####.###.#
            #.#.#.........#.#
            #.#.#.#########.#
            #S#.............#
            #################"""; //11048

    record RainDeer(Position position, Direction direction) {
        RainDeer advance(Direction d) {
            return new RainDeer(d.move(position), d);
        }
    }

    record Cost(Direction direction, int cost) implements Comparable<Cost> {

        public Cost(RainDeer start) {
            this(start.direction, 0);
        }

        public Cost add(Cost other) {
            return new Cost(other.direction, direction == other.direction ? cost + 1 : cost + 1001);
        }

        @Override
        public int compareTo(Cost o) {
            return cost - o.cost;
        }
    }

    public class Maze {
        final Grid<Character> grid;
        final Position start;
        final Position end;

        public Maze(List<String> input) {
            this.grid = Grid.parseCharactersGrid(input, c -> c == '.' ? null : c);
            this.start = grid.getPositionsOf('S').getFirst();
            this.end = grid.getPositionsOf('E').getFirst();
            grid.remove(start);
            grid.remove(end);
        }

        public Path<RainDeer, Cost> findShortestPath() {
            Walker<RainDeer, Cost> walker = new Walker<>(this::nextSteps, this::cost, (c1, c2) -> c1.add(c2));
            Path<RainDeer, Cost> path = walker.findShortestPath(List.of(new RainDeer(start, Direction.EAST)), rd -> rd.position.equals(end));
            //print(path);
            return path;
        }

        void print(Path<RainDeer, Cost> path) {
            if (path != null) {
                path.stream().forEach(rd -> grid.put(rd.position, rd.direction.character()));
            }
            System.out.println(grid.toString(c -> c == null ? '.' : c));
            if (path != null) {
                path.stream().forEach(rd -> grid.remove(rd.position));
            }
        }

        Cost cost(RainDeer next) {
            return new Cost(next);
        }

        List<RainDeer> nextSteps(RainDeer raindeer) {
            return EnumSet.complementOf(EnumSet.of(raindeer.direction.opposite())).stream()
                    .map(raindeer::advance)
                    .filter(rd -> !grid.contains(rd.position))
                    .toList();
        }
    }


    public void solve(List<String> input, String step, long expected1, long expected2) {
        System.out.println("--- " + step + " ----");
        long time = System.currentTimeMillis();

        Maze maze = new Maze(input);
        var path = maze.findShortestPath();

        long res = path.cost().cost;
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();

        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }

    @Override
    public void run() {
        solve(Arrays.asList(TEST_MAZE.split("\\n")), "Test", 7036, -1);
        solve(Arrays.asList(TEST_MAZE_2.split("\\n")), "Test", 11048, -1);
        solve(list(getInputPath()), "Real", 127520, -1);
    }
}

