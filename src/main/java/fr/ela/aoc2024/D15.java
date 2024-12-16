package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Direction;
import fr.ela.aoc2024.utils.Grid;
import fr.ela.aoc2024.utils.Position;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class D15 extends AoC {

    private Grid<Character> grid;
    private Set<Position> crates = new HashSet<>();

    private static final char ROBOT = '@';
    private static final char CRATE = 'O';
    private static final char WALL = '#';

    private Position robot;
        //    ##########
        //    #.O.O.OOO#
        //    #........#
        //    #OO......#
        //    #OO@.....#
        //    #O#.....O#
        //    #O.....OO#
        //    #O.....OO#
        //    #OO....OO#
        //    ##########

    public Position move(char command) {
        Direction d = getDirection(command);
        Position next = d.move(robot);
        if (!grid.contains(next)) {
            return next;
        }
        char c = grid.get(next);
        if (c == WALL) {
            return robot;
        } else {
            Position nextToRobot = next;
            // crate.
            while (grid.inBounds(next) && crates.contains(next)) {
                next = d.move(next);
            }
            if (!grid.contains(next)) {
                // push
                grid.put(next, CRATE);
                grid.remove(nextToRobot);
                crates.remove(nextToRobot);
                crates.add(next);
                return nextToRobot;
            } else {
                return robot;
            }
        }
    }

    private Direction getDirection(char c) {
        return switch (c) {
            case '>' -> Direction.EAST;
            case '^' -> Direction.NORTH;
            case 'v' -> Direction.SOUTH;
            case '<' -> Direction.WEST;
            default -> throw new IllegalArgumentException();
        };
    }

    public long gps(Position position) {
        return position.x() + position.y() * 100;
    }

    public long gps() {
        return grid.streamPositionsOf(CRATE).mapToLong(this::gps).sum();
    }

    public long part1(List<String> gridLines, List<String> commandLines) {
        grid = Grid.parseCharactersGrid(gridLines, c -> c == '.' ? null : c);
        crates = new HashSet<>(grid.getPositionsOf(CRATE));
        String commands = commandLines.stream().collect(Collectors.joining());
        robot = grid.getPositionsOf('@').getFirst();
        grid.remove(robot);
        print();
        for (char c : commands.toCharArray()) {
            robot = move(c);
        }
        print();
        return gps();
    }

    void print() {
        System.out.println(grid.toString(c -> c == null ? '.' : c));
    }

    public void solve(Path input, String step, long expected1, long expected2) {
        List<List<String>> lists = splitOnEmptyLines(input);
        System.out.println("--- " + step + " ----");
        long time = System.currentTimeMillis();
        long res = part1(lists.get(0), lists.get(1));
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();

        //res = i;
        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }

    @Override
    public void run() {
        solve(getTestInputPath(), "Test", 10092, -1);
        solve(getInputPath(), "Real", 1515788, -1);
    }
}

