package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class FieldClassNotDeclared extends CompileErrorException {
    private String fieldName;
    private String className;

    public FieldClassNotDeclared(String classname, String field, int atLine, int atCol){
        super(atLine,atCol);
        fieldName = field;
        className = classname;
    }

    public String toString(){
        return String.format("Error:Line:%d:There is no Field with name %s with in class %s", atLine, fieldName,className);
    }
}
