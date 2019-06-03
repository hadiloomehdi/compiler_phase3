package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class MethodClassNotDeclared extends CompileErrorException {
    private String className;
    private String methodName;

    public MethodClassNotDeclared(String classname, String methodname, int atLine, int atCol){
        super(atLine,atCol);
        className = classname;
        methodName = methodname;
    }

    public String toString(){
        return String.format("Error:Line:%d:There is no Method with name %s with such parameters in class %s;", atLine, methodName,className);
    }

}
