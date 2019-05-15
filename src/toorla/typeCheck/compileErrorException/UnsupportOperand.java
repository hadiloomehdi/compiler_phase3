package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class UnsupportOperand extends CompileErrorException {
    private String operation;

    public UnsupportOperand(String op, int atLine, int atCol){
        super(atLine,atCol);
        operation = op;
    }

    public String toString(){
        return String.format("Error:Line:%d:Unsupported operand types for %s", atLine,operation);
    }
}
