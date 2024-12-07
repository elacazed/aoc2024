package fr.ela.aoc2024;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.LongBinaryOperator;

public class D07 extends AoC {

    enum Operator implements LongBinaryOperator {
        ADD(Long::sum),
        MUL((l,r) -> l*r),
        CONCAT((l,r) -> Long.parseLong(l+""+r));

        private final LongBinaryOperator op;

        Operator(LongBinaryOperator operator) {
            this.op = operator;
        }

        @Override
        public long applyAsLong(long left, long right) {
            return op.applyAsLong(left, right);
        }
    }

    record Input(long result, long[] values) {
        static Input parse(String value) {
            String[] parts = value.split(":\\s");
            long result = Long.parseLong(parts[0]);
            return new Input(result, toLongs(parts[1].split("\\s")));
        }
    }

    boolean isCorrect(Input input, Set<Operator> operators) {
        return isCorrectFromIndex(input, 0, operators, input.values[0]);
    }

    boolean isCorrectFromIndex(Input input, int index, Set<Operator> operators, long current) {
        if (current > input.result) {
            return false;
        }
        final int nextIndex = index + 1;
        if (nextIndex == input.values.length) {
            return current == input.result;
        }
        long next = input.values[nextIndex];
        return operators.stream().anyMatch(op -> isCorrectFromIndex(input, nextIndex, operators, op.applyAsLong(current, next)));
    }

    private static long[] toLongs(String[] strings) {
        return Arrays.stream(strings).mapToLong(Long::parseLong).toArray();
    }

    public long solve(List<String> input, Set<Operator> operators) {
        return input.stream()
                .map(Input::parse)
                .filter(in -> isCorrect(in, operators))
                .mapToLong(Input::result)
                .sum();
    }

    public void solve(Path inputPath, String s, long expected1, long expected2) {
        System.out.println("--- " + s + " ----");
        List<String> inputs = list(inputPath);
        long time = System.currentTimeMillis();
        long res = solve(inputs, EnumSet.of(Operator.ADD, Operator.MUL));
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 ("+expected1+") : " + res +" - "+time);
        time = System.currentTimeMillis();
        res = solve(inputs, EnumSet.allOf(Operator.class));
        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 ("+expected2+") : " + res +" - "+time);
    }

    @Override
    public void run() {
        solve(getTestInputPath(), "Test" ,3749,11387);
        solve(getInputPath(), "Real" ,1708857123053L,0);
    }
}
