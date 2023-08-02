
package maedraw;

import maedraw.syntax.node.*;
import maedraw.types.Type;

import java.util.Collections;
import java.util.List;

public class FunctionInfo {

    private String name;

    private List<ParamInfo> params;

    private Type returnType;

    private PBlock body;

    public FunctionInfo(
            AFunctionFundecl node,
            List<ParamInfo> paramList) {

        this.name = node.getName().getText();
        this.params = Collections.unmodifiableList(paramList);
        this.returnType = Type.getType(node.getReturnType());
        this.body = node.getBlock();
    }

    public FunctionInfo(
            AProcedureFundecl node,
            List<ParamInfo> paramList) {

        this.name = node.getName().getText();
        this.params = Collections.unmodifiableList(paramList);
        this.returnType = null;
        this.body = node.getBlock();
    }

    public boolean returnsValue() {

        return this.returnType != null;
    }

    public Type getReturnType() {

        if (this.returnType == null) {
            throw new RuntimeException(
                    "Internal problem: getReturnType() shouldn't be called on a procedure.");
        }

        return this.returnType;
    }

    public List<ParamInfo> getParams() {

        return this.params;
    }

    public String getName() {

        return this.name;
    }

    public PBlock getBody() {

        return this.body;
    }
}
