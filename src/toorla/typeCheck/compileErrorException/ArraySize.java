package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class ArraySize extends CompileErrorException {

    public ArraySize(int atLine, int atColumn) {
        super(atLine, atColumn);
    }

    public String toString(){
        return String.format("Error:Line:%d:Size of an array must be of type integer", atLine);
    }

}
