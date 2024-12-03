package fr.ela.aoc2024;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class D03 extends AoC {

    Pattern pattern = Pattern.compile("mul\\(([0-9]{1,3}),([0-9]{1,3})\\)");

    public List<String> split(String line) {
        List<String> dos = new ArrayList<>();
        String[] elements = line.split("do\\(\\)");
        for (String element : elements) {
            int end = element.indexOf("don't()");
            dos.add(end == -1 ? element : element.substring(0, end));
        }
        return dos;
    }

    public long mul(String line) {
        return pattern.matcher(line).results().mapToLong(r -> Long.parseLong(r.group(1)) * Long.parseLong(r.group(2))).sum();
    }

    @Override
    public void run() {
        System.out.println("Test Result part 1 : " + mul(readFile(getTestInputPath())));
        System.out.println("Test Result part 2 : " + split("xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))").stream().mapToLong(this::mul).sum());

        System.out.println("Result part 1 : " + mul(readFile(getInputPath())));
        System.out.println("Test Result part 2 : " + split(readFile(getInputPath())).stream().mapToLong(this::mul).sum());

    }
}
