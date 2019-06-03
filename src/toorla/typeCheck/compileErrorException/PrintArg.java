package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class PrintArg extends CompileErrorException {
    public PrintArg(int atLine, int atCol){
        super(atLine,atCol);
    }

    public String toString(){
        return String.format("Error:Line:%d:Type of parameter of print built-in function must be integer , string or array of integer;", atLine);
    }
}
