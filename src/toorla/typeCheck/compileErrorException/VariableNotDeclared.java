package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class VariableNotDeclared extends CompileErrorException {
    private String varName;
    public VariableNotDeclared(String name, int atLine, int atCol){
        super(atLine,atCol);
        varName = name;
    }

    public String toString(){
        return String.format("Error:Line:%d:Variable %s is not declared yet in this Scope", atLine,varName);
    }
}
