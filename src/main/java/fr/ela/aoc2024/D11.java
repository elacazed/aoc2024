package fr.ela.aoc2024;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class D11 extends AoC {

    static Map<State, Long> cache = new ConcurrentHashMap<>();

    public record State(Stone stone, int stones) {
    }

    public record Stone(long number) {

        public Stone(String s) {
            this(Long.parseLong(s));
        }

        Stream<Stone> next() {
            if (number == 0) {
                return Stream.of(new Stone(1));
            }
            var digits = (long) Math.floor(Math.log10(number) + 1);
            if (digits % 2 == 0) {
                long len = digits / 2;
                long rad = (long) Math.pow(10, len);
                long big = number / rad;
                return Stream.of(new Stone(big), new Stone(number - big * rad));
            }
            return Stream.of(new Stone(number * 2024));
        }
    }

    public long countStones(Stone stone, int blink) {
        State state = new State(stone, blink);
        Long value = cache.get(state);
        if (value == null) {
            value = countStones(stone.next(), blink - 1);
            cache.put(state, value);
        }
        return value;
    }

    public long countStones(Stream<Stone> stones, int blink) {
        if (blink == 0) {
            return stones.count();
        }
        return stones.mapToLong(stone -> countStones(stone, blink)).sum();
    }

    public void solve(String input, String step, long expected1, long expected2) {
        System.out.println("--- " + step + " ----");
        long time = System.currentTimeMillis();

        List<Stone> stones = Arrays.stream(input.split("\\s")).map(Stone::new).toList();
        long res = countStones(stones.stream(), 25);
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }

    @Override
    public void run() {
        solve("125 17", "Test", 55312, -1);
        solve("0 5601550 3914 852 50706 68 6 645371", "Real", 189092, -1);
    }
}
