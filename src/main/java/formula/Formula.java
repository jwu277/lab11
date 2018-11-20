package formula;

/*
   Data type definition:
   Formula = BooleanLiteral(value:boolean) + And(left,right:Formula)
 */

public interface Formula {

    public boolean evaluate();

}
