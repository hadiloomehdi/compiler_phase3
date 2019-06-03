package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class RvalueDecInc extends CompileErrorException {
    private String DorI;

    public RvalueDecInc(String dORi, int atLine, int atCol){
        super(atLine,atCol);
        DorI = dORi;
    }

    public String toString(){
        return String.format("Error:Line:%d:Operand of %s must be a valid lvalue;", atLine,DorI);
    }

}
