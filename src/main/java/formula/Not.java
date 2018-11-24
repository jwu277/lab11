package formula;

public class Not implements Formula {

    Formula f;

    public Not(Formula f) {
        this.f = f;
    }

    @Override
    public boolean evaluate() {
        return !f.evaluate();
    }

    @Override
    public String toString() {
        return "!" + f.toString();
    }

}
