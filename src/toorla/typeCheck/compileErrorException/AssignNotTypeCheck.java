package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class AssignNotTypeCheck extends CompileErrorException {

//    String lValue, rValue;

    public AssignNotTypeCheck(int atLine, int atCol) {
        super(atLine,atCol);
//        this.lValue = lValue;
//        this.rValue = rValue;
    }

    public String toString() {
//        return String.format("Error:Line:%d:Assign not type checked! expected %s type but found %s type;", lValue, rValue, atLine);

        return String.format("Error:Line:%d:Assign not type checked!", atLine);
    }
}
