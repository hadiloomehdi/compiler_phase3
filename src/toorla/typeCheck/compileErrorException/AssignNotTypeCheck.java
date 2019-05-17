package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class AssignNotTypeCheck extends CompileErrorException {

    public AssignNotTypeCheck(int atLine, int atCol) {
        super(atLine,atCol);
    }

    public String toString() {
        return String.format("Error:Line:%d:", atLine);
    }
}
