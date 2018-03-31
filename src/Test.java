import java.io.File;
import java.util.List;
import java.util.Random;

public class Test {
    public static void main(String[] args) {
//        JList<Integer> list = new Empty<>();
//
//        for (int i = 0; i < 1000000; i++) {
//            int n = new Random().nextInt(25) + 1;
//            list = new Cons<>(n, list);
//        }
//
//        printList(list);
//        printList(list.sort((n1, n2) -> n1 - n2));
//        printList(list.sort((n1, n2) -> n1 - n2).map(n -> n - 10));
//        printList(list.sort((n1, n2) -> n1 - n2).map(n -> n - 10).filter(n -> n % 2 == 0));
//        System.out.println(list.length());

        JNumber n1 = new JNumber(4);
        JNumber n2 = new JNumber(-3);
        JNumber n3 = new JNumber(1, 3);
        JNumber n4 = new JNumber(-8, 7);

        System.out.println(n1);
        System.out.println(n2);
        System.out.println(n3);
        System.out.println(n4);
        System.out.println(n1.add(n2));
        System.out.println(n1.subtract(n2));
        System.out.println(n3.multiply(n4));
        System.out.println(n3.divide(n4));
        System.out.println(n4.divide(n4));

        JString s1 = new JString("hi");
        JString s2 = new JString("there");

        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s1.append(s2));
        System.out.println(s2.append(s1));

        System.out.println(new JIf(JBoolean.FALSE, new JNumber(1), new JSymbol("kek")).evaluate());
        System.out.println();

        List<JExpression> expressions = Tokenizer.parse(new File("C:/Users/Don/IdeaProjects/Jisp/src/ExampleJisp.jisp"));

        for (JExpression expr : expressions) {
            System.out.println(expr.evaluate());
        }
    }

    static <T> void printList(JList<T> list) {
        for (T t : list) {
            System.out.println(t + " ");
        }

        System.out.println();
    }
}
