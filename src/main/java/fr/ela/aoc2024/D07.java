package fr.ela.aoc2024;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class D07 extends AoC {

    record Input(long result, long[] values) {
        static Input parse(String value) {
            String[] parts = value.split(":\\s");
            long result = Long.parseLong(parts[0]);
            return new Input(result, toLongs(parts[1].split("\\s")));
        }
    }

    boolean isCorrect(Input input) {
        return isCorrectFromIndex(input, 0, input.values[0]);
    }

    boolean isCorrectFromIndex(Input input, int index, long current) {
        index++;
        if (index == input.values.length) {
            return current == input.result;
        }
        long next = input.values[index];
        return isCorrectFromIndex(input, index, current + next) ||
                isCorrectFromIndex(input, index, current * next);
    }

    private static long[] toLongs(String[] strings) {
        return Arrays.stream(strings).mapToLong(Long::parseLong).toArray();
    }


    public long solvePartOne(List<String> input) {
        return input.stream()
                .map(Input::parse)
                .filter(this::isCorrect)
                .mapToLong(Input::result)
                .sum();
    }

    public void solve(Path inputPath, String s, long expected1) {
        System.out.println("--- " + s + " ----");
        List<String> inputs = list(inputPath);
        System.out.println("Part 1 ("+expected1+") : " + solvePartOne(inputs));

    }

    @Override
    public void run() {
        solve(getTestInputPath(), "Test" ,3749);
        solve(getInputPath(), "Real" ,1708857123053L);
    }
}
