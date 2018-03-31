interface Function<T, R> {
    R apply(T t);
}

interface Predicate<T> extends Function<T, Boolean> {
}

interface Visitor<T extends Visitable<T>, R> extends Function<T, R> {
    R visit(T t);
}

interface Visitable<T extends Visitable<T>> {
    <R> R accept(Visitor<T, R> v);
}