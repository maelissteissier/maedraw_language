
package maedraw;

import maedraw.syntax.node.*;
import maedraw.types.Type;

import java.util.HashMap;
import java.util.Map;

public class Scope {

    private Scope parent;

    private Map<String, Type> variables = new HashMap<>();

    public Scope(
            Scope parent) {

        this.parent = parent;
    }

    public Scope(Scope parent, Map<String, Type> globalVariables) {
        this.parent = parent;
        this.variables = globalVariables;
    }

    public Scope getParent() {

        return this.parent;
    }

    private boolean hasVariable(
            String identifier) {

        if (this.variables.containsKey(identifier)) {
            return true;
        }
        else if (this.parent != null) {
            return this.parent.hasVariable(identifier);
        }
        else {
            return false;
        }
    }

    public void addVariable(
            TIdent nameToken,
            Type type) {

        String name = nameToken.getText();

        if (hasVariable(name)) {
            throw new SemanticException(nameToken,
                    "The variable \"" + name + "\" has already been declared.");
        }

        this.variables.put(name, type);
    }

    public Type getVariableType(
            TIdent nameToken) {

        String name = nameToken.getText();
        Type type = this.variables.get(name);

        if (type != null) {
            return type;
        }
        else if (this.parent != null) {
            return this.parent.getVariableType(nameToken);
        }
        else {
            throw new SemanticException(nameToken,
                    "The variable " + name + " hasn't been declared.");
        }
    }

    public Map<String, Type> getVariables() {
        return variables;
    }
}
