package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class LessOrMoreArgs extends CompileErrorException {
    public LessOrMoreArgs(int atLine, int atCol){
        super(atLine,atCol);
    }

    public String toString(){
        return String.format("Error:Line:%d:Number of arguments does not match in function;", atLine);
    }
}
