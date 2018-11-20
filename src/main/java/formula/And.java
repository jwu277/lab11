package formula;

public class And implements Formula {

    Formula left;
    Formula right;

    public And(Formula left, Formula right) {
        this.left = left;
        this.right = right;
    }

    public boolean evaluate() {
        return left.evaluate() && right.evaluate();
    }

}
