package fr.ela.aoc2024;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class D01 extends AoC {

    private static final Pattern pattern = Pattern.compile("(\\d+)\\s+(\\d+)");

    record Pair(int left, int right) {

        public static Pair of(String value) {
            Matcher m = pattern.matcher(value);
            if (m.matches()) {
                return new Pair(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
            }
            throw new IllegalArgumentException();
        }

        int distance() {
            return Math.abs(left - right);
        }

        int score() {
            return left * right;
        }
    }

    int distance(Stream<Pair> pairs) {
        List<Integer> first = new ArrayList<>();
        List<Integer> second = new ArrayList<>();
        pairs.forEach(p -> {
            first.add(p.left);
            second.add(p.right);
        });
        first.sort(Comparator.naturalOrder());
        second.sort(Comparator.naturalOrder());
        Iterator<Integer> firstIt = first.iterator();
        Iterator<Integer> secondIt = second.iterator();
        int total = 0;
        while (firstIt.hasNext()) {
            total += new Pair(firstIt.next(), secondIt.next()).distance();
        }
        return total;
    }

    long score(Stream<Pair> pairs) {
        Map<Integer, Pair> counts = new HashMap<>();

        pairs.forEach(p -> {
            counts.compute(p.left, (pair, count) -> count == null ? new Pair(1, 0) : new Pair(count.left + 1, count.right));
            counts.compute(p.right, (pair, count) -> count == null ? new Pair(0, 1) : new Pair(count.left, count.right + 1));
        });

        return counts.entrySet().stream().mapToLong(e -> e.getKey() * e.getValue().score()).sum();
    }


    @Override
    public void run() {
        System.out.println("Test Distance Part 1 : " + distance(stream(getTestInputPath(), Pair::of)));
        System.out.println("Distance Part 1 : " + distance(stream(getInputPath(), Pair::of)));
        System.out.println("Test Score Part 2 : " + score(stream(getTestInputPath(), Pair::of)));
        System.out.println("Score Part 2 : " + score(stream(getInputPath(), Pair::of)));
    }
}
