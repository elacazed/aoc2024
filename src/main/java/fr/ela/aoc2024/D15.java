package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Direction;
import fr.ela.aoc2024.utils.Grid;
import fr.ela.aoc2024.utils.Position;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class D15 extends AoC {

    private Grid<Character> grid;
    private Set<Position> crates = new HashSet<>();

    private Map<Long, List<Crate>> cratesMap;
    private final Map<Position, Crate> cratesByPosition = new HashMap<>();

    private static final char ROBOT = '@';
    private static final char CRATE = 'O';

    private static final char CRATE_LEFT = '[';
    private static final char CRATE_RIGHT = ']';
    private static final char WALL = '#';

    record Crate(Position left, Position right) {

        public Crate(Position left) {
            this(left, left.move(1, 0));
        }

        public long y() {
            return left.y();
        }

        boolean xOverlaps(Crate other) {
            return other.left.x() == left.x() || other.left.x() == right.x() || other.right.x() == left.x();
        }

        Crate move(Direction d) {
            return new Crate(d.move(left), d.move(right));
        }

        public Stream<Position> positionsStream() {
            return Stream.of(left, right);
        }

        public boolean touches(Crate next, Direction d) {
            return switch (d) {
                case WEST -> next.y() == y() && left.x() == next.right.x() + 1;
                case EAST -> next.y() == y() && right.x() + 1 == next.left.x();
                case NORTH -> next.y() == y() - 1 && xOverlaps(next);
                case SOUTH -> next.y() == y() + 1 && xOverlaps(next);
            };
        }
    }

    void removeCrate(Crate crate) {
        cratesMap.get(crate.y()).remove(crate);
        cratesByPosition.remove(crate.left);
        cratesByPosition.remove(crate.right);
        grid.remove(crate.left);
        grid.remove(crate.right);
    }

    void put(Crate crate) {
        grid.put(crate.left, CRATE_LEFT);
        grid.put(crate.right, CRATE_RIGHT);
        cratesByPosition.put(crate.left, crate);
        cratesByPosition.put(crate.right, crate);
        cratesMap.computeIfAbsent(crate.y(), c -> new ArrayList<>()).add(crate);
    }

    private Position robot;

    public Position move(char command, boolean wide) {
        Direction d = getDirection(command);
        Position next = d.move(robot);
        if (!grid.contains(next)) {
            return next;
        }
        char c = grid.get(next);
        if (c == WALL) {
            return robot;
        } else {
            return wide ? pushWide(next, d) : push(next, d);
        }
    }

    public Position pushWide(Position next, Direction d) {
        // crates
        if (!cratesByPosition.containsKey(next)) {
            throw new IllegalStateException();
        }
        Crate push = cratesByPosition.get(next);
        Set<Crate> cratesToPush = pushedCrates(push, d);
        Set<Position> cratesToPushPositions = cratesToPush.stream().flatMap(Crate::positionsStream).collect(Collectors.toSet());
        // Can we push  all crates in the given direction ?
        // the position(s) where the crate would move must be all emptu or filled with a position from the moving crates.
        Stream<Position> positionstoCheck = switch (d) {
            case EAST -> Stream.of(cratesToPush.stream().max(Comparator.comparingLong(c -> c.right.x())).orElse(push).right());
            case WEST -> Stream.of(cratesToPush.stream().min(Comparator.comparingLong(c -> c.left.x())).orElse(push).left());
            case NORTH, SOUTH -> cratesToPush.stream().flatMap(Crate::positionsStream);
        };

        boolean pushPossible = positionstoCheck
                .map(d::move)
                .allMatch(p -> cratesToPushPositions.contains(p) || !grid.contains(p));
        if (pushPossible) {
            cratesToPush.forEach(this::removeCrate);
            cratesToPush.forEach(c -> put(c.move(d)));
            return next;
        } else {
            return robot;
        }
    }

    Set<Crate> getContiguousHorizontalCrates(Crate crate, Direction d) {
        Stream<Crate> candidates = cratesMap.get(crate.y()).stream();
        if (d == Direction.WEST) {
            candidates = candidates
                    .filter(c -> c.left().x() < crate.left.x())
                    .sorted(Comparator.comparingLong(c -> ((Crate) c).right.x()).reversed());
        } else {
            candidates = candidates
                    .filter(c -> c.left().x() > crate.left.x())
                    .sorted(Comparator.comparingLong(c -> c.right.x()));
        }
        List<Crate> crates = candidates.toList();

        Set<Crate> result = new HashSet<>();
        result.add(crate);
        if (crates.isEmpty()) {
            return result;
        }
        Iterator<Crate> it = crates.iterator();
        Crate current = crate;
        Crate next = it.next();
        while (current.touches(next, d) && it.hasNext()) {
            result.add(next);
            current = next;
            next = it.next();
        }
        if (current.touches(next, d)) {
            result.add(next);
        }
        return result;
    }

    Set<Crate> getContiguousVerticalCrates(Crate crate, Direction d) {
        long base = crate.y();
        int offset = d == Direction.NORTH ? -1 : 1;

        Set<Crate> result = new HashSet<>();
        result.add(crate);
        if (cratesMap.containsKey(base + offset)) {
            List<Crate> contiguous = cratesMap.get(base + offset).stream().filter(c -> c.xOverlaps(crate)).toList();
            contiguous.forEach(c -> result.addAll(getContiguousVerticalCrates(c, d)));
        }
        return result;
    }

    Set<Crate> pushedCrates(Crate crate, Direction d) {
        return switch (d) {
            case EAST, WEST -> getContiguousHorizontalCrates(crate, d);
            case NORTH, SOUTH -> getContiguousVerticalCrates(crate, d);
        };
    }

    private Position push(Position next, Direction d) {
        Position nextRobotPosition = next;
        // crate.
        while (grid.inBounds(next) && crates.contains(next)) {
            next = d.move(next);
        }
        if (!grid.contains(next)) {
            // push
            grid.put(next, CRATE);
            grid.remove(nextRobotPosition);
            crates.remove(nextRobotPosition);
            crates.add(next);
            return nextRobotPosition;
        } else {
            return robot;
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

    public long gps2() {
        return cratesMap.values().stream().flatMap(List::stream).map(Crate::left).mapToLong(this::gps).sum();
    }

    public long part1(List<String> gridLines, List<String> commandLines) {
        grid = Grid.parseCharactersGrid(gridLines, c -> c == '.' ? null : c);
        crates = new HashSet<>(grid.getPositionsOf(CRATE));
        String commands = commandLines.stream().collect(Collectors.joining());
        robot = grid.getPositionsOf('@').getFirst();
        grid.remove(robot);
        print();
        for (char c : commands.toCharArray()) {
            robot = move(c, false);
        }
        print();
        return gps();
    }

    public String wide(String line) {
        char[] result = new char[line.length() * 2];
        for (int i = 0; i < line.length() * 2; i += 2) {
            char c = line.charAt(i / 2);
            if (c == WALL || c == '.') {
                result[i] = c;
                result[i + 1] = c;
            }
            if (c == CRATE) {
                result[i] = '[';
                result[i + 1] = ']';
            }
            if (c == ROBOT) {
                result[i] = ROBOT;
                result[i + 1] = '.';
            }
        }
        return new String(result);
    }

    public long part2(List<String> gridLines, List<String> commandLines) {
        grid = Grid.parseCharactersGrid(gridLines.stream().map(this::wide).toList(), c -> c == '.' ? null : c);
        cratesMap = new HashMap<>();
        grid.streamPositionsOf(CRATE_LEFT)
                .map(Crate::new)
                .forEach(c -> {
                    cratesMap.computeIfAbsent(c.y(), crate -> new ArrayList<>()).add(c);
                    cratesByPosition.put(c.left, c);
                    cratesByPosition.put(c.right, c);
                });
        cratesMap.forEach((key, value) -> value.sort(Comparator.comparingLong(c -> c.left().x())));
        String commands = String.join("", commandLines);
        robot = grid.getPositionsOf('@').getFirst();
        grid.remove(robot);

        char[] chars = commands.toCharArray();
        //print(0, chars[0]);
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            robot = move(c, true);
            //print(i + 1, chars[Math.min(i+1, chars.length -1)]);
        }
        print();
        return gps2();
    }


    void print(int i, char dir) {
        grid.put(robot, dir);
        System.out.println("---- " + i + " ----");
        System.out.println(grid.toString(c -> c == null ? '.' : c));
        grid.remove(robot);
    }

    void print() {
        grid.put(robot, '@');
        System.out.println(grid.toString(c -> c == null ? '.' : c));
        grid.remove(robot);
    }

    public void solve(Path input, String step, long expected1, long expected2) {
        List<List<String>> lists = splitOnEmptyLines(input);
        System.out.println("--- " + step + " ----");
        long time = System.currentTimeMillis();
        long res = part1(lists.get(0), lists.get(1));
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();

        res = part2(lists.get(0), lists.get(1));
        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }

    @Override
    public void run() {
        solve(getTestInputPath(), "Test", 10092, -1);
        solve(getInputPath(), "Real", 1515788, -1);
    }
}

