
package maedraw;

import maedraw.syntax.node.*;

public class InterpreterException
        extends RuntimeException {

    private static final String LINE_SEPARATOR
            = System.getProperty("line.separator");

    private Frame frame;

    private Token token;

    private String message;

    public InterpreterException(
            Frame frame,
            Token token,
            String message) {

        this.frame = frame;
        this.token = token;
        this.message = message;
    }

    @Override
    public String getMessage() {

        StringBuilder sb = new StringBuilder();
        sb.append("Line " + this.token.getLine() + ", position "
                + this.token.getPos());

        FunctionInfo function = this.frame.getFunction();
        if (function == null) {
            sb.append(" of the main program");
        }
        else if (function.returnsValue()) {
            sb.append(" of the function " + function.getName());
        }
        else {
            sb.append(" of the procedure " + function.getName());
        }

        sb.append(": " + this.message);

        Frame currentFrame = this.frame.getParent();
        while (currentFrame != null) {

            sb.append(LINE_SEPARATOR);
            Token callLocation = currentFrame.getCallLocation();

            sb.append("\t called at line " + callLocation.getLine()
                    + ", position " + callLocation.getPos());

            function = currentFrame.getFunction();
            if (function == null) {
                sb.append(" dof the main program");
            }
            else if (function.returnsValue()) {
                sb.append(" of the function " + function.getName());
            }
            else {
                sb.append(" of the procedure " + function.getName());
            }

            currentFrame = currentFrame.getParent();
        }

        return sb.toString();
    }
}
