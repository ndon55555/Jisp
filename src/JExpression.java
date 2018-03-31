interface JExpression {
    JExpression evaluate();
}

class JIf<R> implements JExpression {
    private JExpression condition;
    private JExpression thenClause;
    private JExpression elseClause;

    JIf(JExpression condition, JExpression thenClause, JExpression elseClause) {
        this.condition = condition;
        this.thenClause = thenClause;
        this.elseClause = elseClause;
    }

    @Override
    public JExpression evaluate() {
        if (( (JBoolean) this.condition.evaluate()).isTrue()) {
            return this.thenClause.evaluate();
        } else {
            return this.elseClause.evaluate();
        }
    }
}

//abstract class AbstractBinaryExpression<T1, T2, R> implements JExpression<R> {
//    private JExpression<T1> arg1;
//    private JExpression<T2> arg2;
//
//    AbstractBinaryExpression(JExpression<T1> arg1, JExpression<T2> arg2) {
//        this.arg1 = arg1;
//        this.arg2 = arg2;
//    }
//
//    JExpression<T1> getArg1() {
//        return this.arg1;
//    }
//
//    JExpression<T2> getArg2() {
//        return this.arg2;
//    }
//}
//
//abstract class AbstractBinaryBooleanExpression extends AbstractBinaryExpression<JBoolean, JBoolean, JBoolean> {
//    AbstractBinaryBooleanExpression(JExpression<JBoolean> arg1, JExpression<JBoolean> arg2) {
//        super(arg1, arg2);
//    }
//}
//
//class JAnd extends AbstractBinaryBooleanExpression {
//    JAnd(JExpression<JBoolean> be1, JExpression<JBoolean> be2) {
//        super(be1, be2);
//    }
//
//    @Override
//    public JBoolean evaluate() {
//        return new JIf<>(this.getArg1(), this.getArg2(), JBoolean.FALSE).evaluate();
//    }
//}
//
//class JOr extends AbstractBinaryBooleanExpression {
//    JOr(JExpression<JBoolean> be1, JExpression<JBoolean> be2) {
//        super(be1, be2);
//    }
//
//    @Override
//    public JBoolean evaluate() {
//        return new JIf<>(this.getArg1(), JBoolean.TRUE, this.getArg2()).evaluate();
//    }
//}
//
//class JNot implements JExpression<JBoolean> {
//    JExpression<JBoolean> be;
//
//    JNot(JExpression<JBoolean> be) {
//        this.be = be;
//    }
//
//    @Override
//    public JBoolean evaluate() {
//        return new JIf<>(this.be.evaluate(), JBoolean.FALSE, JBoolean.TRUE).evaluate();
//    }
//}
//
//class JXOr extends AbstractBinaryBooleanExpression {
//    JXOr(JExpression<JBoolean> be1, JExpression<JBoolean> be2) {
//        super(be1, be2);
//    }
//
//    @Override
//    public JBoolean evaluate() {
//        JExpression<JBoolean> a = this.getArg1();
//        JExpression<JBoolean> b = this.getArg2();
//        return new JAnd(new JOr(new JNot(a), new JNot(b)), new JOr(a, b))
//                .evaluate();
//    }
//}
//
//class JIff extends AbstractBinaryBooleanExpression {
//    JIff(JExpression<JBoolean> be1, JExpression<JBoolean> be2) {
//        super(be1, be2);
//    }
//
//    @Override
//    public JBoolean evaluate() {
//        return new JNot(new JXOr(this.getArg1(), this.getArg2())).evaluate();
//    }
//}
//
//abstract class NumberComparer extends AbstractBinaryExpression<JNumber, JNumber, JBoolean> {
//    NumberComparer(JExpression<JNumber> arg1, JExpression<JNumber> arg2) {
//        super(arg1, arg2);
//    }
//}