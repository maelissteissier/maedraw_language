package maedraw;

import maedraw.syntax.lexer.Lexer;
import maedraw.syntax.lexer.LexerException;
import maedraw.syntax.node.Node;
import maedraw.syntax.parser.Parser;
import maedraw.syntax.parser.ParserException;
import maedraw.types.Primitive;
import maedraw.types.Type;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.Map;

public class LibParser {

    /**
     * Function that parses the Libraries imported in the Main file. It extracts the functions and the global variables
     * declared in the libraries and does a semantic check on them.
     * If it finds an error, throws an adequate exception labeled with the right failing library's name
     * @param libFinder
     * @return An object LibVarFunctions containing the FunctionFinder with every lib functions,
     * a Map of variables with their types and a map of variables with their values.
     */
    public static LibVarFunctions parselib(LibFinder libFinder){
        FunctionFinder functionFinder = new FunctionFinder();
        Map<String, Type> globalVariablesTypes = new HashMap<>();
        Map<String, Primitive> globalVariables = new HashMap<>();

        // Going through every library found
        for (String libName: libFinder.libs) {
            try{
                // Library files must have the extension .maedraw
                PushbackReader reader
                        = new PushbackReader(new FileReader(libName + ".maedraw"), 1024);
                Parser parser = new Parser(new Lexer(reader));
                Node tree = parser.parse();
                tree.apply(functionFinder);

                // Semantic check imports globalVariables for naming conflict error check
                VariableAndReturnChecker varchecks
                        = new VariableAndReturnChecker(functionFinder, globalVariablesTypes);
                varchecks.visit(tree);

                // Keeping global variable types for Main semantic check
                globalVariablesTypes.putAll(varchecks.getCurrentScope().getVariables());

                // Interpretation of library for GlobalVariable values extraction
                InterpreterEngine interp = new InterpreterEngine(functionFinder);
                interp.visit(tree);

                // Keeping variables extracted as Global variables
                globalVariables.putAll(interp.getCurrentFrame().getVariables());

                // Probably useless but keeping it just in case
                globalVariables.putAll(interp.getCurrentFrame().getGlobalVariables());

                System.out.println("*** SEMANTIC VERIFICATION SUCCESSFULL for lib : " + libName);

            } catch (FileNotFoundException e) {
                System.err.println("ERROR: Couldn't find the library : \"" + libName
                        + "\"");
                System.exit(1);
            } catch (IOException e) {
                System.err.println("(IN LIBRARY : " + libName + ") I/O ERROR: " + e.getMessage());
                System.exit(1);
            } catch (LexerException e) {
                System.err.println("(IN LIBRARY : " + libName + ") LEXER ERROR: " + e.getMessage());
                System.exit(1);
            } catch (ParserException e) {
                System.err.println("(IN LIBRARY : " + libName + ") SYNTAX ERROR: " + e.getMessage());
                System.exit(1);
            }        catch (SemanticException e) {
                System.err.println("(IN LIBRARY : " + libName + ") SEMANTIC ERROR: " + e.getMessage());
                System.exit(1);
            }
        }
        return  new LibVarFunctions(functionFinder, globalVariables, globalVariablesTypes);
    }
}
