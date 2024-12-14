package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Diagonal;
import fr.ela.aoc2024.utils.Position;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class D14 extends AoC {

    private static final Pattern ROBOT_PARSER = Pattern.compile("p=([-\\d]+),([-\\d]+) v=([-\\d]+),([-\\d]+)");

    Robot parse(String s) {
        Matcher m = ROBOT_PARSER.matcher(s);
        if (m.matches()) {
            return new Robot(new Position(Long.parseLong(m.group(1)), Long.parseLong(m.group(2))),
                    Long.parseLong(m.group(3)), Long.parseLong(m.group(4)));
        }
        throw new IllegalArgumentException(s);
    }

    class Robot {
        private Position position;
        public final long dx, dy;

        Robot(Position start, long dx, long dy) {
            this.position = start;
            this.dx = dx;
            this.dy = dy;
        }

        private static long moveInDirection(long start, long moves, long gridSize) {
            long value = (start + moves) % gridSize;
            if (value < 0) {
                value = gridSize + value;
            }
            return value;
        }

        public Robot move(long times, long width, long height) {
            long xmove = moveInDirection(position.x(), times * dx, width);
            long ymove = moveInDirection(position.y(), times * dy, height);
            this.position = new Position(xmove, ymove);
            return this;
        }

        public Diagonal getQuadrant(long width, long height) {
            // width et height toujours impairs, et on ne compte pas les robots sur la colonne/ligne centrale.
            if (position.x() < width / 2) {
                if (position.y() < height / 2) {
                    return Diagonal.NW;
                }
                if (position.y() > height / 2) {
                    return Diagonal.SW;
                }
            }
            if (position.x() > width / 2) {
                if (position.y() < height / 2) {
                    return Diagonal.NE;
                }
                if (position.y() > height / 2) {
                    return Diagonal.SE;
                }
            }
            return null;
        }
    }

    public void solve(List<String> input, String step, long times, int width, int height, long expected1, long expected2) {
        System.out.println("--- " + step + " ----");
        long time = System.currentTimeMillis();
        List<Robot> robots = input.stream().map(this::parse).toList();

        List<Robot> moved = robots.stream().map(r -> r.move(times, width, height)).toList();

        long res = moved.stream().map(r -> r.getQuadrant(width, height))
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .values().stream().mapToLong(Long::valueOf).reduce(1, (x, y) -> x * y);

        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();

        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }

    @Override
    public void run() {
        solve(list(getTestInputPath()), "Test", 100, 11, 7, 12, -1);
        solve(list(getInputPath()), "Real", 100, 101, 103, -1, -1);
    }
}

