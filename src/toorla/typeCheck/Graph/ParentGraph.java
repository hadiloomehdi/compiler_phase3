package toorla.typeCheck.Graph;

import toorla.ast.Program;
import toorla.ast.declaration.classDecs.ClassDeclaration;

import java.util.HashMap;
import java.util.Map;

public class ParentGraph {
    Map<String, String> parent;

    public ParentGraph(Program toorlaASTCode) {
        parent = new HashMap<>();
        for (ClassDeclaration itrClass : toorlaASTCode.getClasses())
            if (itrClass.getParentName().getName() != null)
                parent.put(itrClass.getName().getName(), itrClass.getParentName().getName());
    }

    public String getParent(String child) throws ParentNotFoundException {
        if (!parent.containsKey(child))
            throw new ParentNotFoundException();
        return parent.get(child);
    }

}
