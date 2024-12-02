package fr.ela.aoc2024.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Matrix<T extends Comparable<T>> {

    private final T[][] matrix;
    private final T zero;
    private final Function<T, T> abs;
    private final BiFunction<T, T, T> division;
    private final BiFunction<T, T, T> multiplication;
    private final BiFunction<T, T, T> addition;
    private final BiFunction<T, T, T> substraction;

    public Matrix(T[][] matrix, T zero, UnaryOperator<T> abs, BinaryOperator<T> division, BinaryOperator<T> multiplication, BinaryOperator<T> addition, BinaryOperator<T> substraction) {
        this.matrix = matrix;
        this.zero = zero;
        this.abs = abs;
        this.division = division;
        this.multiplication = multiplication;
        this.addition = addition;
        this.substraction = substraction;
    }
    public static Matrix<BigDecimal> bigDecimalMatrix(BigDecimal[][] matrix) {
        return new Matrix<>(matrix, BigDecimal.ZERO, BigDecimal::abs, (l1, l2) -> l1.divide(l2, MathContext.DECIMAL128), BigDecimal::multiply, BigDecimal::add, BigDecimal::subtract);
    }

    public static Matrix<BigDecimal> bigDecimalMatrix(BigDecimal[][] matrix, MathContext context) {
        return new Matrix<>(matrix, BigDecimal.ZERO, BigDecimal::abs, (l1, l2) -> l1.divide(l2, context), BigDecimal::multiply, BigDecimal::add, BigDecimal::subtract);
    }

    public static Matrix<Integer> intMatrix(Integer[][] matrix) {
        return new Matrix<>(matrix, 0, Math::abs, (l1, l2) -> l1 / l2, (l1, l2) -> l1 * l2, Integer::sum, (l1, l2) -> l1 - l2);
    }

    public static Matrix<Double> doubleMatrix(Double[][] matrix) {
        return new Matrix<>(matrix, (double) 0, Math::abs, (l1, l2) -> l1 / l2, (l1, l2) -> l1 * l2, Double::sum, (l1, l2) -> l1 - l2);
    }

    public static Matrix<Float> doubleMatrix(Float[][] matrix) {
        return new Matrix<>(matrix, (float) 0, Math::abs, (l1, l2) -> l1 / l2, (l1, l2) -> l1 * l2, Float::sum, (l1, l2) -> l1 - l2);
    }

    public T get(int col, int row) {
        return matrix[col][row];
    }

    public String toString() {
        return toString(Objects::toString);
    }

    public String toString(Function<T, String> toString) {
        String[] lines = new String[matrix.length];
        for (int row = 0; row < matrix.length; row++) {
            lines[row] = Arrays.stream(matrix[row]).map(toString).collect(Collectors.joining(", ", "|", "|"));
        }
        return String.join("\n", lines);
    }

    // See : https://touteslesmaths.fr/complements/TLM1_Pivot_de_Gauss.pdf
    public void gauss() {
        int pivotRow = 0;
        int pivotCol = 0;
        int nRows = matrix.length;
        int nCols = matrix[0].length;
        while (pivotRow < nRows && pivotCol < nCols) {
            T max = zero;
            int idxMax = -1;
            // Find pivot.
            for (int i = pivotRow; i < nRows; i++) {
                T candidate = abs.apply(matrix[i][pivotCol]);
                if (candidate.compareTo(max) > 0) {
                    max = candidate;
                    idxMax = i;
                }
            }
            if (matrix[idxMax][pivotCol].equals(zero)) {
                // nothing to pivot in this column
                pivotCol++;
            } else {
                // swap rows idxMax and pivotRow
                T[] tmp = matrix[pivotRow];
                matrix[pivotRow] = matrix[idxMax];
                matrix[idxMax] = tmp;
                for (int i = pivotRow + 1; i < nRows; i++) {
                    // for all lower rows, subtract so that matrix[i][pivotCol] becomes 0
                    T factor = division.apply(matrix[i][pivotCol], matrix[pivotRow][pivotCol]);
                    matrix[i][pivotCol] = zero;
                    for (int j = pivotCol + 1; j < nCols; j++) {
                        // only need to go right, to the left it's all zeros anyway
                        matrix[i][j] = substraction.apply(matrix[i][j], multiplication.apply(factor, matrix[pivotRow][j]));
                    }
                }
            }
            pivotCol++;
            pivotRow++;
        }
    }
}
