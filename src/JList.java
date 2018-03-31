import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public interface JList<T> extends JExpression, Iterable<T> {
    T first();

    JExpression rest();

    <R> R foldl(R acc, Function<R, Function<T, R>> combiner);

    <R> R foldr(R acc, Function<R, Function<T, R>> combiner);

    JList<T> reverse();

    JList<T> append(JList<T> that);

    int length();

    <R> JList<R> map(Function<T, R> f);

    JList<T> filter(Predicate<T> p);

    JList<T> sort(Comparator<T> comparator);
}

abstract class AbstractJList<T> implements JList<T> {
    @Override
    public JExpression evaluate() {
        return this;
    }
}

class Cons<T> extends AbstractJList<T> {
    private final T first;
    private final JExpression rest;

    Cons(T first, JExpression rest) {
        this.first = first;
        this.rest = rest;
    }

    @Override
    public T first() {
        return this.first;
    }

    @Override
    public JExpression rest() {
        return this.rest;
    }

    @Override
    public <R> R foldl(R acc, Function<R, Function<T, R>> combiner) {
        for (T data : this) {
            acc = combiner.apply(acc).apply(data);
        }

        return acc;
    }

    @Override
    public <R> R foldr(R acc, Function<R, Function<T, R>> combiner) {
        return this.reverse().foldl(acc, combiner);
    }

    @Override
    public JList<T> reverse() {
        JList<T> emptyList = new Empty<>();
        return this.foldl(emptyList, acc -> item -> new Cons<>(item, acc));
    }

    @Override
    public JList<T> append(JList<T> that) {
        return this.reverse().foldl(that, acc -> item -> new Cons<>(item, acc));
    }

    @Override
    public int length() {
        return this.foldl(0, acc -> item -> acc + 1);
    }

    @Override
    public <R> JList<R> map(Function<T, R> f) {
        JList<R> emptyList = new Empty<>();
        return this.foldr(emptyList, acc -> item -> new Cons<>(f.apply(item), acc));
    }

    @Override
    public JList<T> filter(Predicate<T> p) {
        JList<T> emptyList = new Empty<>();
        return this.foldr(emptyList, acc -> item -> {
            if (p.apply(item)) {
                return new Cons<>(item, acc);
            }

            return acc;
        });
    }

    @Override
    public JList<T> sort(Comparator<T> comparator) {
        @SuppressWarnings("unchecked")
        T[] items = (T[]) new Object[this.length()];
        int i = 0;

        for (T item : this) {
            items[i] = item;
            i++;
        }

        Arrays.sort(items, (a, b) -> comparator.compare(b, a)); // sort in reverse order because rebuilding the list will reverse it
        JList<T> result = new Empty<>();

        for (T item : items) {
            result = new Cons<>(item, result);
        }

        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new JListIterator<>(this);
    }

    @Override
    public String toString() {
        return "(cons " + this.first.toString() + " " + this.rest.toString() + ")";
    }
}

class Empty<T> extends AbstractJList<T> {
    @Override
    public T first() {
        throw new RuntimeException("Cannot call first on empty.");
    }

    @Override
    public JList<T> rest() {
        throw new RuntimeException("Cannot call rest on empty.");
    }

    @Override
    public <R> R foldl(R acc, Function<R, Function<T, R>> combiner) {
        return acc;
    }

    @Override
    public <R> R foldr(R acc, Function<R, Function<T, R>> combiner) {
        return acc;
    }

    @Override
    public JList<T> reverse() {
        return new Empty<>();
    }

    @Override
    public JList<T> append(JList<T> that) {
        return that;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public <R> JList<R> map(Function<T, R> f) {
        return new Empty<>();
    }

    @Override
    public JList<T> filter(Predicate<T> p) {
        return new Empty<>();
    }

    @Override
    public JList<T> sort(Comparator<T> comparator) {
        return new Empty<>();
    }

    @Override
    public Iterator<T> iterator() {
        return new JListIterator<>(this);
    }

    @Override
    public String toString() {
        return "~";
    }
}

class JListIterator<T> implements Iterator<T> {
    JList<T> list;

    JListIterator(JList<T> list) {
        this.list = list;
    }

    @Override
    public boolean hasNext() {
        return (list instanceof Cons);
    }

    @Override
    public T next() {
        Cons<T> consList = (Cons<T>) list;
        T tmp = consList.first();
        this.list = (JList<T>) consList.rest();

        return tmp;
    }
}