package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class AssignNotTypeCheck extends CompileErrorException {

    public AssignNotTypeCheck() {

    }

    public String toString() {
        return String.format("Error:Line:%d:", atLine);
    }
}
