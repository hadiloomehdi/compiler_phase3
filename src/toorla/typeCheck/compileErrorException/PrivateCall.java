package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class PrivateCall extends CompileErrorException {
    private String MorF;
    private String memberName;
    private String className;

    public PrivateCall(String fORm, String membername, String classname, int atLine, int atCol){
        super(atLine,atCol);
        MorF = fORm;
        memberName = membername;
        className = classname;
    }

    public String toString(){
        return String.format("Error:Line:%d:Illegal access to %s %s of an object of class %s;", atLine,MorF,memberName,className);
    }
}
