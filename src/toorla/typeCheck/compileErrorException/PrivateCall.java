package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class PrivateCall extends CompileErrorException {
    private String access;
    private String memberName;
    private String className;

    public PrivateCall(String PorP, String membername, String classname, int atLine, int atCol){
        super(atLine,atCol);
        access = PorP;
        memberName = membername;
        className = classname;
    }

    public String toString(){
        return String.format("Error:Line:%d:Illegal access to %s %s of an object of Class %s", atLine,access,memberName,className);
    }
}
