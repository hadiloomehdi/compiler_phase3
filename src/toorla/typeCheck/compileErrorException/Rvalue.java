package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class Rvalue extends CompileErrorException {

    public Rvalue(int atLine, int atCol){
        super(atLine,atCol);
    }

    public String toString(){
        return String.format("Error:Line:%d:Left hand side expression is not assignable;", atLine);
    }
}
