
package maedraw;

import java.util.*;

import maedraw.syntax.analysis.*;
import maedraw.syntax.node.*;
import maedraw.types.Type;

public class FunctionFinder
        extends DepthFirstAdapter {

    private Map<String, FunctionInfo> functions = new HashMap<>();

    private Map<Node, Scope> scopes = new HashMap<>();

    private Map<Node, Type> types = new HashMap<>();

    private List<ParamInfo> paramList;

    public void visit(
            Node node) {

        if (node != null) {
            node.apply(this);
        }
    }

    private void addFunction(
            Token location,
            FunctionInfo functionInfo) {

        if (this.functions.containsKey(functionInfo.getName())) {
            throw new SemanticException(location,
                    "the function " + functionInfo.getName() + " already exists.");
        }
        this.functions.put(functionInfo.getName(), functionInfo);
    }

    @Override
    public void caseAFunctionFundecl(
            AFunctionFundecl node) {

        this.paramList = new LinkedList<>();
        visit(node.getParams());
        FunctionInfo function = new FunctionInfo(node, this.paramList);
        this.paramList = null;

        addFunction(node.getName(), function);
    }

    @Override
    public void caseAProcedureFundecl(
            AProcedureFundecl node) {

        this.paramList = new LinkedList<>();
        visit(node.getParams());
        FunctionInfo procedure = new FunctionInfo(node, this.paramList);
        this.paramList = null;

        addFunction(node.getName(), procedure);
    }

    @Override
    public void caseAParam(
            AParam node) {

        this.paramList.add(
                new ParamInfo(node.getName(), Type.getType(node.getType())));
    }

    public FunctionInfo getFunctionInfo(
            String name) {

        return this.functions.get(name);
    }

    public void addScope(
            Node key,
            Scope scope) {

        this.scopes.put(key, scope);
    }

    public Scope getScope(
            Node key) {

        return this.scopes.get(key);
    }

    public void addType(
            Node key,
            Type type) {

        this.types.put(key, type);
    }

    public Type getType(
            Node key) {

        return this.types.get(key);
    }
}
