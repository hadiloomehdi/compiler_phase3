package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class ConditionNotBoolean extends CompileErrorException {
    private String name;
    public ConditionNotBoolean(String LorI, int atLine, int atCol){
        super(atLine,atCol);
        name = LorI;
    }
    public String toString(){
        return String.format("Error:Line:%d:Condition type must be bool in %s statements", atLine, name);
    }
}
