
package maedraw;

import maedraw.syntax.node.*;

public class SemanticException
        extends RuntimeException {

    private Token token;

    private String message;

    public SemanticException(
            Token token,
            String message) {

        this.token = token;
        this.message = message;
    }

    @Override
    public String getMessage() {

        return "Line " + this.token.getLine() + ", position "
                + this.token.getPos() + ": " + this.message;
    }
}
