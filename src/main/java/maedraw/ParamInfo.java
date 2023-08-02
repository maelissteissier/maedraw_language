
package maedraw;

import maedraw.syntax.node.*;
import maedraw.types.Type;

public class ParamInfo {

    private TIdent name;

    private Type type;

    public ParamInfo(
            TIdent name,
            Type type) {

        this.name = name;
        this.type = type;
    }

    public TIdent getName() {

        return this.name;
    }

    public Type getType() {

        return this.type;
    }

}
