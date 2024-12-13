package fr.ela.aoc2024;

import fr.ela.aoc2024.utils.Position;

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

        public ClawMachine add(long amount) {
            return new ClawMachine(a, b, new Position(prize().x() + amount, prize.y()+amount));
        }

        private boolean isPositiveInteger(double n) {
            return (n >= 0 && Math.floor(n) == n);
        }
        /*
       Système de 2 equations linéaires a 2 inconnues (A et B, respectivement nombre d'appuis sur les boutons a et b)
       A et B sont des entiers positifs.

       1    A.ax + B.bx = px        A.ax.ay + B.bx.ay = px.ay
                              =>
       2    A.ay + B.by = py        A.ax.ay + B.by.ax = py.ax

                                                        px.ay - py.ax
       1-2 => B(bx.ay -  by.ax) = px.ay - py.ax => B = ---------------
                                                        bx.ay - by.ax

       Idem pour A :
       1    A.ax + B.bx = px        A.ax.by + B.bx.by = px.by                                              px.by - py.bx
                              =>                                => A(ax.by - ax.bx) = px.by - py.bx => A = -------------
       2    A.ay + B.by = py        A.ax.bx + B.by.bx = py.bx                                              ax.by - ax.bx

                                                   (px - B.bx)
       Mais on a aussi : A.ax + B.bx = px  => A =  -----------
                                                       ax
    */
        OptionalLong cheaperCombination() {
            double bStrokes = (double) (prize.x() * a.y - prize.y() * a.x) / (b.x * a.y - b.y * a.x);
            if (isPositiveInteger(bStrokes)) {
                double aStrokes = (prize.x() - bStrokes * b.x) / a.x;
                if (isPositiveInteger(aStrokes)) {
                    return OptionalLong.of(3 * (long) aStrokes + (long) bStrokes);
                }
            }
            return OptionalLong.empty();
        }
    }

    public void solve(String input, String step, long expected1, long expected2) {
        System.out.println("--- " + step + " ----");
        long time = System.currentTimeMillis();
        List<ClawMachine> cms = ClawMachine.parse(input);
        long res = cms.stream().mapToLong(cm -> cm.cheaperCombination().orElse(0)).sum();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 1 (" + expected1 + ") : " + res + " - " + time);
        time = System.currentTimeMillis();
        res = cms.stream().map(cm -> cm.add(10000000000000L)).mapToLong(cm -> cm.cheaperCombination().orElse(0)).sum();
        time = System.currentTimeMillis() - time;
        System.out.println("Part 2 (" + expected2 + ") : " + res + " - " + time);
    }

    @Override
    public void run() {
        solve(readFile(getTestInputPath()), "Test", 480, -1);
        solve(readFile(getInputPath()), "Real", 37901, 77407675412647L);
    }
}

