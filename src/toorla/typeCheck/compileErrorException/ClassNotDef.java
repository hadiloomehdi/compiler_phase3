package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class ClassNotDef extends CompileErrorException {
    private String name;
    public ClassNotDef(String className, int atLine, int atCol){
        super(atLine,atCol);
        name = className;
    }

    public String toString(){
        return String.format("Error:Line:%d:There is no class with name %s", atLine,name);
    }
}
