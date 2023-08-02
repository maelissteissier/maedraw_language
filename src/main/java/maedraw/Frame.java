
package maedraw;

import maedraw.syntax.node.*;
import maedraw.types.Primitive;

import java.util.HashMap;
import java.util.Map;

public class Frame {

    private Frame parent;

    private FunctionInfo function;

    private Map<String, Primitive> variables = new HashMap<>();

    private Map<String, Primitive> globalVariables = new HashMap<>();

    private Primitive returnValue;

    private Token callLocation;

    public Frame(
            Frame parent,
            FunctionInfo function) {

        this.parent = parent;
        this.function = function;
        this.callLocation = this.callLocation;
    }
    public Frame(
            Frame parent,
            FunctionInfo function,
            Map<String, Primitive> globalVariables) {

        this.parent = parent;
        this.function = function;
        this.callLocation = this.callLocation;
        this.globalVariables = globalVariables;
    }

    public void assignVariable(
            String name,
            Primitive value) {
        if (this.globalVariables.containsKey(name)){
            this.globalVariables.put(name, value);
        } else {
            this.variables.put(name, value);
        }

    }

    public Primitive getValue(
            String name) {
        if (this.globalVariables.containsKey(name)){
            return this.globalVariables.get(name);
        }
        return this.variables.get(name);
    }

    public Frame getParent() {

        return this.parent;
    }

    public FunctionInfo getFunction() {

        return this.function;
    }

    public Primitive getReturnValue() {

        return this.returnValue;
    }

    public void setReturnValue(
            Primitive returnValue) {

        this.returnValue = returnValue;
    }

    public void setFunction(
            FunctionInfo function) {

        this.function = function;
    }

    public void setCallLocation(
            Token callLocation) {

        this.callLocation = callLocation;
    }

    public Token getCallLocation() {

        return this.callLocation;
    }

    public Map<String, Primitive> getVariables() {
        return variables;
    }

    public Map<String, Primitive> getGlobalVariables() {
        return globalVariables;
    }
}
