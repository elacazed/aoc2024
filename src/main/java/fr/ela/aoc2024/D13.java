package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class D13 extends AoC {

    private static final Pattern CLAWMACHINE_PATTERN = Pattern.compile("""
            Button A: X\\+(\\d+), Y\\+(\\d+)
            Button B: X\\+(\\d+), Y\\+(\\d+)
            Prize: X=(\\d+), Y=(\\d+)""");

    record Button(int x, int y) {
    }

    record Combination(int a, int b) {
        long cost() {
            return a * 3L + b;
        }
    }

    record ClawMachine(Button a, Button b, Position prize) {

        public static List<ClawMachine> parse(String input) {
            Matcher m = CLAWMACHINE_PATTERN.matcher(input);
            return m.results().map(mr -> {
                Button a = new Button(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                Button b = new Button(Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
                Position prize = new Position(Integer.parseInt(m.group(5)), Integer.parseInt(m.group(6)));
                return new ClawMachine(a, b, prize);
            }).toList();
        }

        OptionalLong cheaperCombination() {
            return reachingPairs().stream().mapToLong(Combination::cost).min();
        }

        private List<Combination> reachingPairs() {
            var pairs = D13.reachingPairs(prize.x(), a.x, b.x);
            pairs.retainAll(D13.reachingPairs(prize.y(), a.y, b.y));
            return pairs;
        }
    }


    private static List<Combination> reachingPairs(int x, int ax, int bx) {
        int cur = x;
        List<Combination> result = new ArrayList<>();
        int a = 0;
        while (cur > 0) {
            cur -= ax;
            a++;
            if (cur % bx == 0) {
                result.add(new Combination(a, cur / bx));
            }
        }
        if (cur == ax) {
            result.add(new Combination(a + 1, 0));
        }
        if (cur == bx) {
            result.add(new Combination(a, 1));
        }
        return result;
    }


    public void solve(String input, String step, long expected1, long expected2) {
        System.out.println("--- " + step + " ----");
        long time = System.currentTimeMillis();
        List<ClawMachine> cms = ClawMachine.parse(input);
        long res = cms.stream().mapToLong(cm -> cm.cheaperCombination().orElse(0)).sum();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();

        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }

    @Override
    public void run() {
        solve(readFile(getTestInputPath()), "Test", 480, -1);
        solve(readFile(getInputPath()), "Real", -1, -1);
    }
}

