import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Tokenizer {
//    static String read(File f) {
//        if (!f.getName().contains(".jisp")) {
//            throw new IllegalArgumentException("Need a .jisp file.");
//        }
//
//        StringBuilder sb = new StringBuilder();
//
//        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
//            String line = br.readLine();
//
//            while (line != null) {
//                sb.append(line);
//                line = br.readLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return sb.toString();
//    }

    static List<JExpression> parse(File f) {
        if (!f.getName().contains(".jisp")) {
            throw new IllegalArgumentException("Need a .jisp file.");
        }

        List<JExpression> expressions = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String expr = "";
            String line = br.readLine();
            int leftParens = 0;

            while (line != null) {
                leftParens += Tokenizer.occurrences(line, '(');
                leftParens -= Tokenizer.occurrences(line, ')');
                expr += line;

                if (leftParens < 0) {
                    throw new RuntimeException("Mismatched parentheses.");
                } else if (leftParens == 0) {
                    if (!expr.isEmpty()) {
                        expressions.add(Tokenizer.parseExpression(expr));
                        expr = "";
                    }
                }

                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return expressions;
    }

    static int occurrences(String s, char key) {
        int count = 0;

        for (char c : s.toCharArray()) {
            if (key == c) {
                count++;
            }
        }

        return count;
    }

    static JExpression parseExpression(String expr) {
        if (expr.length() > 2 && expr.charAt(0) == '(' && expr.charAt(expr.length() - 1) == ')') {
            return Tokenizer.parseOperation(expr);
        }

        return Tokenizer.parseAtom(expr);
    }

    static JExpression parseAtom(String expr) {
        if (Tokenizer.canBeNumber(expr)) {
            return new JNumber(Integer.parseInt(expr));
        } else if (Tokenizer.canBeString(expr)) {
            return new JString(expr.substring(1, expr.length() - 1));
        } else if (Tokenizer.canBeBoolean(expr)) {
            return new JBoolean(Boolean.parseBoolean(expr));
        } else if (Tokenizer.canBeSymbol(expr)) {
            return new JSymbol(expr.substring(1));
        } else if (Tokenizer.canBeEmpty(expr)) {
            return new Empty<>();
        } else {
            throw new IllegalArgumentException("Poorly formed atom: " + expr);
        }
    }

    static boolean canBeNumber(String s) {
        if (s.isEmpty()) {
            return false;
        }

        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;
    }

    static boolean canBeString(String s) {
        return s.length() >= 2
               && s.charAt(0) == '"'
               && s.charAt(s.length() - 1) == '"';
    }

    static boolean canBeBoolean(String s) {
        return s.equals("true") || s.equals("false");
    }

    static boolean canBeSymbol(String s) {
        if (s.isEmpty() || s.charAt(0) != '\'') {
            return false;
        }

        for (char c : s.toCharArray()) {
            if (c == '"') {
                return false;
            }
        }

        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                return true;
            }
        }

        return false;
    }

    static boolean canBeEmpty(String expr) {
        return expr.equals("~");
    }

    static JExpression parseOperation(String expr) {
        String contents = expr.substring(1, expr.length() - 1);
        int indexFirstSpace = contents.indexOf(' ');
        String operation = contents.substring(0, indexFirstSpace);
        List<JExpression> args = Tokenizer.parseArgs(contents.substring(indexFirstSpace + 1));

        return Tokenizer.generateExpression(operation, args);
    }

    static List<JExpression> parseArgs(String args) {
        List<JExpression> expressions = new LinkedList<>();
        int leftParenIndex = args.indexOf('(');

        if (leftParenIndex < 0) {
            for (String expr : args.split("\\s+")) {
                expressions.add(Tokenizer.parseAtom(expr));
            }

            return expressions;
        }

        int leftParens = 1;
        int rightParens = 0;
        int rightParenIndex = leftParenIndex;

        while (rightParenIndex < args.length() && leftParens > rightParens) {
            rightParenIndex++;

            if (args.charAt(rightParenIndex) == '(') {
                leftParens++;
            } else if (args.charAt(rightParenIndex) == ')') {
                rightParens++;
            }
        }

        if (rightParenIndex >= args.length()) {
            throw new RuntimeException("Mismatched parentheses.");
        }

        if (leftParenIndex > 0) {
            expressions.addAll(Tokenizer.parseArgs(args.substring(0, leftParenIndex).trim()));
        }

        expressions.add(Tokenizer.parseExpression(args.substring(leftParenIndex, rightParenIndex + 1).trim()));

        if (rightParenIndex < args.length() - 1) {
            expressions.addAll(Tokenizer.parseArgs(args.substring(rightParenIndex + 1).trim()));
        }

        return expressions;
    }

    static JExpression generateExpression(String operation, List<JExpression> args) {
        Function<JExpression, Function<JExpression, JExpression>> op;

        switch (operation) {
            case "+":
                op = a -> b -> ((JNumber) a).add((JNumber) b);
                break;
            case "-":
                op = a -> b -> ((JNumber) a).subtract((JNumber) b);
                break;
            case "*":
                op = a -> b -> ((JNumber) a).multiply((JNumber) b);
                break;
            case "/":
                op = a -> b -> ((JNumber) a).divide((JNumber) b);
                break;
            case "string-append":
                op = a -> b -> ((JString) a).append((JString) b);
                break;
            case "and":
                op = a -> b -> ((JBoolean) a).and((JBoolean) b);
                break;
            case "or":
                op = a -> b -> ((JBoolean) a).or((JBoolean) b);
                break;
            case "cons":
                op = a -> b -> new Cons<>(a, b);
                break;
            default:
                throw new RuntimeException("Couldn't determine operation: " + operation);

        }

        JExpression result = args.remove(0);

        while (!args.isEmpty()) {
            result = op.apply(result).apply(args.remove(0));
        }

        return result;
    }
}
