package fr.ela.aoc2024;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class D03 extends AoC {

    Pattern pattern = Pattern.compile("mul\\(([0-9]{1,3}),([0-9]{1,3})\\)");


    public long mul(String line) {
        Matcher matcher = pattern.matcher(line);
        long result = 0;
        while (matcher.find()) {
            result += Long.parseLong(matcher.group(1)) * Long.parseLong(matcher.group(2));
        }
        return result;
    }

    @Override
    public void run() {
        System.out.println("Test Result part 1 : "+mul(readFile(getTestInputPath())));
        System.out.println("Result part 1 : "+mul(readFile(getInputPath())));

    }
}
