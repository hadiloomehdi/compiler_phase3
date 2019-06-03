package toorla.typeCheck.compileErrorException;

import toorla.CompileErrorException;

public class MainClassInEntry extends CompileErrorException {
    public MainClassInEntry(){
    }

    public String toString(){
        return String.format("Error:There is no main function ( return int with no argument ) in entry class;");
    }
}
