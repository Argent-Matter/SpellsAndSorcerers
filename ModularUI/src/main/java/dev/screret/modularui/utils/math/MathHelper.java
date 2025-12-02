package dev.screret.modularui.utils.math;

import net.minecraft.util.Mth;

import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.mariuszgromada.math.mxparser.Constant;
import org.mariuszgromada.math.mxparser.Expression;

import org.jetbrains.annotations.Nullable;

public class MathHelper {

    // SI prefixes
    public static final Constant k = new Constant("k", 1e3);
    public static final Constant M = new Constant("M", 1e6);
    public static final Constant G = new Constant("G", 1e9);
    public static final Constant T = new Constant("T", 1e12);
    public static final Constant P = new Constant("P", 1e15);
    public static final Constant E = new Constant("E", 1e18);
    public static final Constant Z = new Constant("Z", 1e21);
    public static final Constant Y = new Constant("Y", 1e24);
    public static final Constant m = new Constant("m", 1e-3);
    public static final Constant u = new Constant("u", 1e-6);
    public static final Constant n = new Constant("n", 1e-9);
    public static final Constant p = new Constant("p", 1e-12);
    public static final Constant f = new Constant("f", 1e-15);
    public static final Constant a = new Constant("a", 1e-18);
    public static final Constant z = new Constant("z", 1e-21);
    public static final Constant y = new Constant("y", 1e-24);

    public static final float QUART_PI = Mth.PI / 4f;

    public static final Vector3fc UNIT_X = new Vector3f(1f, 0f, 0f);
    public static final Vector3fc UNIT_Y = new Vector3f(0f, 1f, 0f);
    public static final Vector3fc UNIT_Z = new Vector3f(0f, 0f, 1f);

    public static ParseResult parseExpression(@Nullable String expression) {
        return parseExpression(expression, Double.NaN, false);
    }

    public static ParseResult parseExpression(@Nullable String expression, boolean useSiPrefixes) {
        return parseExpression(expression, Double.NaN, useSiPrefixes);
    }

    public static ParseResult parseExpression(@Nullable String expression, double defaultValue) {
        return parseExpression(expression, defaultValue, true);
    }

    public static ParseResult parseExpression(@Nullable String expression, double defaultValue, boolean useSiPrefixes) {
        if (expression == null || expression.isEmpty()) return ParseResult.success(defaultValue);
        Expression e = new Expression(expression);
        if (useSiPrefixes) {
            e.addConstants(k, M, G, T, P, E, Z, Y, m, u, n, p, f, a, z, y);
        }
        double result = e.calculate();
        if (Double.isNaN(result)) {
            return ParseResult.failure(defaultValue, e.getErrorMessage());
        }
        return ParseResult.success(result);
    }

    public static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return value < Integer.MIN_VALUE ? Integer.MIN_VALUE : (int) value;
        }
    }

    public static int min(int @Nullable... values) {
        if (values == null || values.length == 0) throw new IllegalArgumentException();
        if (values.length == 1) return values[0];
        if (values.length == 2) return Math.min(values[0], values[1]);
        int min = Integer.MAX_VALUE;
        for (int i : values) {
            if (i < min) {
                min = i;
            }
        }
        return min;
    }

    public static int max(int @Nullable... values) {
        if (values == null || values.length == 0) throw new IllegalArgumentException();
        if (values.length == 1) return values[0];
        if (values.length == 2) return Math.max(values[0], values[1]);
        int max = Integer.MIN_VALUE;
        for (int i : values) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }
}
