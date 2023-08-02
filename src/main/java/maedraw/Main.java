package maedraw;

import java.io.*;
import java.util.Map;

import maedraw.syntax.lexer.*;
import maedraw.syntax.node.*;
import maedraw.syntax.parser.*;
import maedraw.types.Primitive;
import maedraw.types.Type;

import static maedraw.LibParser.parselib;

public class Main {

    private final static String LANGUAGE_NAME = "maedraw";

    public static void main(
            String[] args) {

        // Vérification de l'argument
        if (args.length != 1) {
            System.err.println("USAGE: java " + LANGUAGE_NAME + ".Main file."
                    + LANGUAGE_NAME);
            System.exit(1);
        }

        String filename = args[0];
        if (!filename.endsWith("." + LANGUAGE_NAME)) {
            System.err.println("ERROR: the file \"" + filename
                    + "\" needs the extension : \"." + LANGUAGE_NAME + "\".");
            System.exit(1);
        }

        try {
            PushbackReader reader
                    = new PushbackReader(new FileReader(filename), 1024);
            Parser parser = new Parser(new Lexer(reader));
            Node tree = parser.parse();

            LibFinder libFinder = new LibFinder();
            tree.apply(libFinder);

            // Parse Libraries imported in MAIN file
            LibVarFunctions varsAndFunctionsFromLibs = parselib(libFinder);
            FunctionFinder functionFinder = varsAndFunctionsFromLibs.getFunctionFinder();
            Map<String, Type> globalVariablesTypes = varsAndFunctionsFromLibs.getGlobalVariablesTypes();
            Map<String, Primitive> globalVariables = varsAndFunctionsFromLibs.getGlobalVariables();

            tree.apply(functionFinder);

            VariableAndReturnChecker varchecks
                    = new VariableAndReturnChecker(functionFinder, globalVariablesTypes);
            varchecks.visit(tree);

             System.out.println("*** SEMANTIC VERIFICATION SUCCESSFULL");


            // Interprétation of MAIN file
            InterpreterEngine interp = new InterpreterEngine(functionFinder, globalVariables);
            interp.visit(tree);
        }
        catch (FileNotFoundException e) {
            System.err.println("ERROR: Couldn't find the file : \"" + filename
                    + "\"");
            System.exit(1);
        }
        catch (IOException e) {
            System.err.println("I/O ERROR: " + e.getMessage());
            System.exit(1);
        }
        catch (LexerException e) {
            System.err.println("LEXER ERROR: " + e.getMessage());
            System.exit(1);
        }
        catch (ParserException e) {
            System.err.println("SYNTAX ERROR: " + e.getMessage());
            System.exit(1);
        }
        catch (SemanticException e) {
            System.err.println("SEMANTIC ERROR: " + e.getMessage());
            System.exit(1);
        }
        catch (InterpreterException e) {
            System.err.println("EXEC ERROR: " + e.getMessage());
            System.exit(1);
        }
    }

}