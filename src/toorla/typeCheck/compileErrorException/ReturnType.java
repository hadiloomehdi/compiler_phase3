package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class ReturnType extends CompileErrorException {
    private String returnType;

    public ReturnType(String returntype, int atLine, int atCol){
        super(atLine,atCol);
        returnType = returntype;
    }

    public String toString(){
        return String.format("Error:Line:%d:Expression returned by this method must be %s;", atLine,returnType);
    }
}
