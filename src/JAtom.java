interface JAtom extends JExpression {
    JNumber add(JNumber num);

    JNumber subtract(JNumber num);

    JNumber multiply(JNumber num);

    JNumber divide(JNumber num);

    JString append(JString str);

    JBoolean and(JBoolean bool);

    JBoolean or(JBoolean bool);
}

abstract class AbstractJAtom implements JAtom {
    private static final IllegalArgumentException ONLY_NUMBERS
    = new IllegalArgumentException("This method only works for numbers.");
    private static final IllegalArgumentException ONLY_STRINGS
    = new IllegalArgumentException("This method only works for strings.");
    private static final IllegalArgumentException ONLY_BOOLEANS
    = new IllegalArgumentException("This method only works for booleans.");

    @Override
    public JNumber add(JNumber num) {
        throw ONLY_NUMBERS;
    }

    @Override
    public JNumber subtract(JNumber num) {
        throw ONLY_NUMBERS;
    }

    @Override
    public JNumber multiply(JNumber num) {
        throw ONLY_NUMBERS;
    }

    @Override
    public JNumber divide(JNumber num) {
        throw ONLY_NUMBERS;
    }

    @Override
    public JString append(JString str) {
        throw ONLY_STRINGS;
    }

    @Override
    public JExpression evaluate() {
        return this;
    }

    @Override
    public JBoolean and(JBoolean bool) {
        throw ONLY_BOOLEANS;
    }

    @Override
    public JBoolean or(JBoolean bool) {
        throw ONLY_BOOLEANS;
    }
}

class JNumber extends AbstractJAtom implements Comparable<JNumber> {
    static int getGCD(int a, int b) {
        return (b == 0) ? a : JNumber.getGCD(b, a % b);
    }

    static int abs(int n) {
        return (n < 0) ? -n : n;
    }

    private int numerator;
    private int denominator;

    JNumber(int val) {
        this(val, 1);
    }

    JNumber(int numerator, int denominator) {
        boolean isNonNegNumerator = numerator >= 0;
        boolean isNonNegDenominator = denominator >= 0;
        int absNumerator = JNumber.abs(numerator);
        int absDenominator = JNumber.abs(denominator);
        int gcd = JNumber.getGCD(absNumerator, absDenominator);
        this.numerator = absNumerator / gcd;
        this.denominator = absDenominator / gcd;

        if (isNonNegNumerator != isNonNegDenominator) {
            this.numerator = -this.numerator;
        }
    }

    @Override
    public JNumber add(JNumber that) {
        int commonDenominator = this.getCommonDenominator(that);
        int thisNewNumerator = commonDenominator / this.denominator * this.numerator;
        int thatNewNumerator = commonDenominator / that.denominator * that.numerator;

        JNumber result = new JNumber(thisNewNumerator + thatNewNumerator, commonDenominator);
        return result;
    }

    private int getCommonDenominator(JNumber that) {
        return this.denominator / JNumber.getGCD(this.denominator, that.denominator) * that.denominator;
    }

    @Override
    public JNumber subtract(JNumber that) {
        return this.add(that.negate());
    }

    private JNumber negate() {
        return new JNumber(-this.numerator, this.denominator);
    }

    @Override
    public JNumber multiply(JNumber that) {
        return new JNumber(this.numerator * that.numerator, this.denominator * that.denominator);
    }

    @Override
    public JNumber divide(JNumber that) {
        return this.multiply(that.reciprocal());
    }

    private JNumber reciprocal() {
        return new JNumber(this.denominator, this.numerator);
    }

    @Override
    public String toString() {
        String result = Integer.toString(this.numerator);

        if (this.denominator != 1) {
            result += "/" + Integer.toString(this.denominator);
        }

        return result;
    }

    @Override
    public int compareTo(JNumber that) {
        int commonDenominator = this.getCommonDenominator(that);
        int thisNewNumerator = commonDenominator / this.denominator * this.numerator;
        int thatNewNumerator = commonDenominator / that.denominator * that.numerator;
        return thisNewNumerator - thatNewNumerator;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JNumber)) {
            return false;
        }

        JNumber that = (JNumber) o;
        return this.numerator == that.numerator
               && this.denominator == that.denominator;
    }

    @Override
    public int hashCode() {
        int result = 2;
        result = result * 13 + this.numerator;
        result = result * 13 + this.denominator;

        return result;
    }
}

class JString extends AbstractJAtom implements Comparable<JString> {
    private String val;

    JString(String val) {
        this.val = val;
    }

    @Override
    public JString append(JString that) {
        return new JString(this.val + that.val);
    }

    @Override
    public String toString() {
        return "\"" + this.val + "\"";
    }

    @Override
    public int compareTo(JString that) {
        return this.val.compareTo(that.val);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JString)) {
            return false;
        }

        JString that = (JString) o;
        return this.val.equals(that.val);
    }

    @Override
    public int hashCode() {
        return this.val.hashCode() * 37;
    }
}

class JBoolean extends AbstractJAtom {
    static final JBoolean TRUE = new JBoolean(true);
    static final JBoolean FALSE = new JBoolean(false);

    private boolean isTrue;

    JBoolean(boolean isTrue) {
        this.isTrue = isTrue;
    }

    boolean isTrue() {
        return this.isTrue;
    }

    @Override
    public JBoolean and(JBoolean that) {
        return new JBoolean(this.isTrue && that.isTrue);
    }

    @Override
    public JBoolean or(JBoolean that) {
        return new JBoolean(this.isTrue || that.isTrue);
    }

    @Override
    public String toString() {
        return (this.isTrue) ? "true" : "false";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JBoolean)) {
            return false;
        }

        JBoolean that = (JBoolean) o;
        return this.isTrue == that.isTrue;
    }

    @Override
    public int hashCode() {
        return (this.isTrue) ? 1 : 0;
    }
}

class JSymbol extends AbstractJAtom {
    private String symbolRepresentation;

    JSymbol(String symbolRepresentation) {
        this.symbolRepresentation = symbolRepresentation;
    }

    @Override
    public String toString() {
        return "'" + this.symbolRepresentation;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JSymbol)) {
            return false;
        }

        JSymbol that = (JSymbol) o;
        return this.symbolRepresentation.equals(that.symbolRepresentation);
    }

    @Override
    public int hashCode() {
        return this.symbolRepresentation.hashCode() * 19;
    }
}