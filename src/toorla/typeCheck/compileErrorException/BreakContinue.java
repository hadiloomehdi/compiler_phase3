package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class BreakContinue extends CompileErrorException {
    private String name;

    public BreakContinue(String BorC, int atLine, int atCol) {
        super(atLine,atCol);
        name = BorC;
    }

    @Override
    public String toString() {
        return String.format("Error:Line:%d:Invalid use of %s, %s  must be used as loop statement", atLine,name,name);
    }
}
