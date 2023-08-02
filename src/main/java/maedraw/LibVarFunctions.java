package maedraw;

import maedraw.types.Primitive;
import maedraw.types.Type;

import java.util.Map;

public class LibVarFunctions {
    FunctionFinder functionFinder;
    Map<String, Primitive> globalVariables;
    Map<String, Type> globalVariablesTypes;

    public LibVarFunctions(FunctionFinder functionFinder, Map<String, Primitive> globalVariables,
                           Map<String, Type> globalVariablesTypes) {
        this.functionFinder = functionFinder;
        this.globalVariables = globalVariables;
        this.globalVariablesTypes = globalVariablesTypes;
    }

    public FunctionFinder getFunctionFinder() {
        return functionFinder;
    }

    public Map<String, Primitive> getGlobalVariables() {
        return globalVariables;
    }

    public Map<String, Type> getGlobalVariablesTypes() {
        return globalVariablesTypes;
    }
}
