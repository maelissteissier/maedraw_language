package maedraw;

import maedraw.syntax.analysis.*;
import maedraw.syntax.node.*;
import maedraw.types.Type;

import java.util.*;

public class VariableAndReturnChecker
        extends DepthFirstAdapter {

    private FunctionFinder functionFinder;

    private FunctionInfo currentFunction;
    private Scope currentScope;

    private Type resultType;

    private Map<String, Type> globalVariables = new HashMap<>();

    private List<Type> argumentTypes;

    public VariableAndReturnChecker(
            FunctionFinder functionFinder, Map<String, Type> globalVariables) {

        this.functionFinder = functionFinder;
        this.globalVariables = globalVariables;
    }

    public Scope getCurrentScope() {
        return currentScope;
    }

    public void visit(
            Node node) {

        if (node != null) {
            node.apply(this);
        }
    }

    private Type evalType(
            Node node) {

        if (this.resultType != null) {
            throw new RuntimeException(
                    "Unexpected error in semantic verifier : resultType != null before node visit");
        }

        visit(node);

        if (this.resultType == null) {
            throw new RuntimeException(
                    "Unexpected error in semantic verifier : resultType == null after node visit");
        }

        Type resultType = this.resultType;
        this.resultType = null;
        return resultType;
    }

    private void evalTypeBoolean(
            Node node,
            Token token) {

        Type type = evalType(node);
        if (type != Type.BOOL) {
            throw new SemanticException(token,
                    "The expression should be boolean");
        }
    }

    private void checkType(Type excpected, Type found, Token token){
        if (excpected != found){
            throw new SemanticException(token, "Type expected : " + excpected + " Type found : " + found);
        }
    }


    private void checkNumberLeft(
            Type type,
            Token token) {

        if (type != Type.INT) {
            throw new SemanticException(token,
                    "The left expression should be an integer");
        }
    }

    private void checkNumberRight(
            Type type,
            Token token) {

        if (type != Type.INT) {
            throw new SemanticException(token,
                    "The right expression should be an integer");
        }
    }

    @Override
    public void caseALibCode(ALibCode node){

        for(PFundecl fundecl : node.getFundecls()){
            visit(fundecl);
        }
        this.currentScope = new Scope(null, globalVariables);
        for(PLibVarDeclInstr varDecl : node.getLibVarDeclInstr()){
            visit(varDecl);
        }
    }

    @Override
    public void caseAMainCode(AMainCode node){
        this.currentScope = new Scope(null, globalVariables);
        visit(node.getCanvasdecl());
        for(PFundecl fundecl : node.getFundecls()){
            visit(fundecl);
        }
        visit(node.getBlock());
    }

    @Override
    public void caseACanvasdecl(ACanvasdecl node) {
        Type width = evalType(node.getWidth());
        Type height = evalType(node.getHeight());
        checkType(Type.INT, width, node.getCanvas());
        checkType(Type.INT, height, node.getComma());
    }



    @Override
    public void caseAFunctionFundecl(
            AFunctionFundecl node) {

        this.currentFunction
                = this.functionFinder.getFunctionInfo(node.getName().getText());
        // As functions and procedures are only declared at the beggining of the program
        // we can give the current scope as the parent of the scope because there is
        // only global variables in this scope. And we want procedures and functions
        // to be able to access them
        this.currentScope = new Scope(this.currentScope);
        this.functionFinder.addScope(node, this.currentScope);
        List<ParamInfo> params = this.currentFunction.getParams();
        for (ParamInfo param : params) {
            this.currentScope.addVariable(param.getName(), param.getType());
        }
        visit(node.getBlock());

        this.currentScope = this.currentScope.getParent();
        this.currentFunction = null;
    }

    @Override
    public void caseAProcedureFundecl(
            AProcedureFundecl node) {

        this.currentFunction
                = this.functionFinder.getFunctionInfo(node.getName().getText());
        // As functions and procedures are only declared at the beggining of the program
        // we can give the current scope as the parent of the scope because there is
        // only global variables in this scope. And we want procedures and functions
        // to be able to access them
        this.currentScope = new Scope(this.currentScope);
        this.functionFinder.addScope(node, this.currentScope);
        List<ParamInfo> params = this.currentFunction.getParams();
        for (ParamInfo param : params) {
            this.currentScope.addVariable(param.getName(), param.getType());
        }

        visit(node.getBlock());

        this.currentScope = this.currentScope.getParent();
        this.currentFunction = null;
    }

    @Override
    public void inABlock(
            ABlock node) {

        // mise en place d'une nouvelle portée en entrant dans un bloc
        this.currentScope = new Scope(this.currentScope);
    }

    @Override
    public void outABlock(
            ABlock node) {

        // retour à la portée précédente en sortant d'un bloc
        this.currentScope = this.currentScope.getParent();
    }

    @Override
    public void caseADeclInstr(
            ADeclInstr node) {

        // Inférence du type et ajout de la déclaration dans la portée
        Type type = evalType(node.getExp());
        this.currentScope.addVariable(node.getIdent(), type);
    }

    @Override
    public void caseALibVarDeclInstr(ALibVarDeclInstr node) {
        // Inférence du type et ajout de la déclaration dans la portée
        Type type = evalType(node.getExp());
        this.currentScope.addVariable(node.getIdent(), type);
    }

    @Override
    public void caseAAssignInstr(
            AAssignInstr node) {

        // Vérifie que l'expression est du même type que la variable
        Type assignedType = evalType(node.getExp());
        Type expected = this.currentScope.getVariableType(node.getIdent());
        checkType(expected, assignedType, node.getAssign());

    }

    @Override
    public void caseATipMoveAngleInstr(ATipMoveAngleInstr node) {
        Type moveType = this.currentScope.getVariableType(node.getIdent());
        Type lengthType = evalType(node.getNumMove());
        Type angleType = evalType(node.getAngle());
        checkType(Type.TIP, moveType, node.getIdent());
        checkType(Type.INT, lengthType, node.getActionMoveAngle());
        checkType(Type.INT, angleType, node.getDegree());
    }

    @Override
    public void caseATipMovePosInstr(ATipMovePosInstr node) {
        Type moveType = this.currentScope.getVariableType(node.getIdent());
        Type pos = evalType(node.getPos());
        checkType(Type.TIP, moveType, node.getIdent());
        checkType(Type.POS, pos, node.getSc());
    }

    @Override
    public void caseATipOffInstr(ATipOffInstr node) {
        Type offType = this.currentScope.getVariableType(node.getIdent());
        checkType(Type.TIP, offType, node.getIdent());
    }

    @Override
    public void caseATipOnInstr(ATipOnInstr node) {
        Type onType = this.currentScope.getVariableType(node.getIdent());
        checkType(Type.TIP, onType, node.getIdent());
    }

    @Override
    public void caseATipColorInstr(ATipColorInstr node) {
        Type tipType = this.currentScope.getVariableType(node.getIdent());
        checkType(Type.TIP, tipType, node.getIdent());
        Type colorType = evalType(node.getColor());
        checkType(Type.COLOR, colorType, node.getActionColor());
    }

    @Override
    public void caseAIfInstr(
            AIfInstr node) {

        // Vérifie l'expression et le bloc
        evalTypeBoolean(node.getExp(), node.getIf());
        visit(node.getBlock());

        if (node.getElseInstr() != null){
            visit(node.getElseInstr());
        }
    }

    @Override
    public void caseAWhileInstr(
            AWhileInstr node) {

        // Vérifie l'expression et le bloc
        evalTypeBoolean(node.getExp(), node.getWhile());
        visit(node.getBlock());
    }

    @Override
    public void caseAReturnInstr(
            AReturnInstr node) {

        if (this.currentFunction == null) {
            throw new SemanticException(node.getReturn(),
                    "Return is invalid in the main program.");
        }

        if (this.currentFunction.returnsValue()) {
            if (node.getExp() == null) {
                throw new SemanticException(node.getReturn(),
                        "Return requires a return value.");
            }

            // Vérifie que l'expression est du même type que la fonction
            Type type = evalType(node.getExp());
            if (type != this.currentFunction.getReturnType()) {
                throw new SemanticException(node.getReturn(),
                        "The expression's type is not appropriate for the return type expected "
                                + this.currentFunction.getName() + ".");
            }
        }
        else {
            if (node.getExp() != null) {
                throw new SemanticException(node.getReturn(),
                        "Return doesn't accept a return value in a procedure "
                                + this.currentFunction.getName() + ".");
            }
        }
    }

    @Override
    public void caseACallInstr(
            ACallInstr node) {

        FunctionInfo function = this.functionFinder
                .getFunctionInfo(node.getIdent().getText());
        if (function == null) {
            throw new SemanticException(node.getIdent(), "The procedure "
                    + node.getIdent().getText() + " doesn't exist.");
        }
        if (function.returnsValue()) {
            throw new SemanticException(node.getIdent(),
                    "The fonction " + node.getIdent().getText()
                            + " cannot be called here.");
        }

        // sauvegarde la liste des types d'arguments courante
        List<Type> previousArgumentTypes = this.argumentTypes;

        // calcule les types des arguments
        this.argumentTypes = new LinkedList<>();
        visit(node.getArgs());

        List<ParamInfo> params = function.getParams();

        // Vérifie que le nombre d'arguments correspond au nombre de paramètres
        if (this.argumentTypes.size() != params.size()) {
            throw new SemanticException(node.getLPar(),
                    "number of argument(s) expected : " + params.size());
        }

        // Vérifie que les arguments sont du même type que les paramètres
        Iterator<Type> argTypesIterator = this.argumentTypes.iterator();
        int count = 1;
        for (ParamInfo param : params) {
            Type argType = argTypesIterator.next();

            if (argType != param.getType()) {
                throw new SemanticException(node.getLPar(), "The argument "
                        + count + " is of the wrong type.");
            }

            count++;
        }

        // restaure la liste des types d'arguments
        this.argumentTypes = previousArgumentTypes;
    }
    @Override
    public void caseAEqExp(
            AEqExp node) {

        evalType(node.getLeft());
        evalType(node.getRight());
        this.resultType = Type.BOOL;
    }

    @Override
    public void caseALtExp(
            ALtExp node) {

        Type left = evalType(node.getLeft());
        Type right = evalType(node.getRight());
        checkNumberLeft(left, node.getLt());
        checkNumberRight(right, node.getLt());
        this.resultType = Type.BOOL;
    }

    @Override
    public void caseAGtExp(
            AGtExp node) {

        Type left = evalType(node.getLeft());
        Type right = evalType(node.getRight());
        checkNumberLeft(left, node.getGt());
        checkNumberRight(right, node.getGt());
        this.resultType = Type.BOOL;
    }

    @Override
    public void caseAAddSum(
            AAddSum node) {

        Type left = evalType(node.getLeft());
        Type right = evalType(node.getRight());

        // Color evolution
        if (left == Type.COLOR && (right == Type.INT || right == Type.COLOR)) {
            this.resultType = Type.COLOR;
            return;
        }

        // Classic int Addition
        checkNumberLeft(left, node.getPlus());
        checkNumberRight(right, node.getPlus());
        this.resultType = Type.INT;


    }

    @Override
    public void caseASubSum(
            ASubSum node) {

        Type left = evalType(node.getLeft());
        Type right = evalType(node.getRight());

        // Color evolution
        if (left == Type.COLOR && right == Type.INT) {
            this.resultType = Type.COLOR;
            return;
        }

        checkNumberLeft(left, node.getMinus());
        checkNumberRight(right, node.getMinus());
        this.resultType = Type.INT;
    }

    @Override
    public void caseAStarMul(AStarMul node) {
        Type left = evalType(node.getLeft());
        Type right = evalType(node.getRight());
        checkNumberLeft(left, node.getStar());
        checkNumberRight(right, node.getStar());
        this.resultType = Type.INT;
    }

    @Override
    public void caseADivMul(ADivMul node) {
        Type left = evalType(node.getLeft());
        Type right = evalType(node.getRight());
        checkNumberLeft(left, node.getSlash());
        checkNumberRight(right, node.getSlash());
        this.resultType = Type.INT;
    }

    @Override
    public void caseAModMul(AModMul node) {
        Type left = evalType(node.getLeft());
        Type right = evalType(node.getRight());
        checkNumberLeft(left, node.getPercent());
        checkNumberRight(right, node.getPercent());
        this.resultType = Type.INT;
    }

    @Override
    public void caseANotNeg(
            ANotNeg node) {

        evalTypeBoolean(node.getExp(), node.getNot());
        this.resultType = Type.BOOL;
    }

    @Override
    public void caseANumberTerm(
            ANumberTerm node) {

        try {
            // Number too large ?
            Integer.parseInt(node.getInteger().getText());
            this.resultType = Type.INT;
        }
        catch (NumberFormatException e) {
            throw new SemanticException(node.getInteger(),
                    "The number given is too big.");
        }
    }

    @Override
    public void caseAVarTerm(
            AVarTerm node) {

        // find variable's type in scope
        this.resultType = this.currentScope.getVariableType(node.getIdent());
    }

    @Override
    public void caseATrueTerm(
            ATrueTerm node) {

        this.resultType = Type.BOOL;
    }

    @Override
    public void caseAFalseTerm(
            AFalseTerm node) {

        this.resultType = Type.BOOL;
    }

    @Override
    public void caseAPosTerm(APosTerm node) {
        Type x_pos = evalType(node.getPosX());
        checkType(Type.INT, x_pos, node.getLAcc());
        Type y_pos = evalType(node.getPosY());
        checkType(Type.INT, y_pos, node.getComma());
        this.resultType = Type.POS;
    }

    @Override
    public void caseARedColor(ARedColor node){
        this.resultType = Type.COLOR;
    }

    @Override
    public void caseABlueColor(ABlueColor node){
        this.resultType = Type.COLOR;
    }

    @Override
    public void caseAYellowColor(AYellowColor node){
        this.resultType = Type.COLOR;
    }

    @Override
    public void caseAWhiteColor(AWhiteColor node){
        this.resultType = Type.COLOR;
    }

    @Override
    public void caseABlackColor(ABlackColor node){
        this.resultType = Type.COLOR;
    }

    @Override
    public void caseARgbColor(ARgbColor node){
        Type rType = evalType(node.getR());
        checkType(Type.INT, rType, node.getLBrk());
        Type gType = evalType(node.getG());
        checkType(Type.INT, gType, node.getComma1());
        Type bType = evalType(node.getB());
        checkType(Type.INT, bType, node.getComma2());
        this.resultType = Type.COLOR;
    }

    @Override
    public void caseASizeTerm(ASizeTerm node){
        this.resultType = Type.SIZE;
    }

    @Override
    public void caseAPencilTerm(APencilTerm node) {

        Type pencilColorType = evalType(node.getColor());
        if (!(pencilColorType == Type.COLOR)){
            throw new SemanticException(node.getLBrk(),
                    "Type expected : " + Type.COLOR + " Type received : " + pencilColorType);
        }

        Type pencilSizeType = evalType(node.getSize());
        if (!(pencilSizeType == Type.SIZE)){
            throw new SemanticException(node.getComma1(),
                    "Type expected : " + Type.SIZE + " Type received : " + pencilSizeType);
        }

        Type pencilPosType = evalType(node.getPos());
        if (!(pencilPosType == Type.POS)){
            throw new SemanticException(node.getComma2(),
                    "Type expected : " + Type.POS + " Type received : " + pencilPosType);
        }
        this.resultType = Type.TIP;
    }

    @Override
    public void caseACallTerm(
            ACallTerm node) {

        FunctionInfo function = this.functionFinder
                .getFunctionInfo(node.getIdent().getText());
        if (function == null) {
            throw new SemanticException(node.getIdent(), "The fonction "
                    + node.getIdent().getText() + " doesn't exist.");
        }
        if (!function.returnsValue()) {
            throw new SemanticException(node.getIdent(),
                    "The procedure " + node.getIdent().getText()
                            + " can't be called here");
        }

        // sauvegarde la liste des types d'arguments courante
        List<Type> previousArgumentTypes = this.argumentTypes;

        // calcule les types des arguments
        this.argumentTypes = new LinkedList<>();
        visit(node.getArgs());

        List<ParamInfo> params = function.getParams();

        // Vérifie que le nombre d'arguments correspond au nombre de paramètres
        if (this.argumentTypes.size() != params.size()) {
            throw new SemanticException(node.getLPar(),
                    "number of argument(s) expected : " + params.size());
        }

        // Vérifie que les arguments sont du même type que les paramètres
        Iterator<Type> argTypesIterator = this.argumentTypes.iterator();
        int count = 1;
        for (ParamInfo param : params) {
            Type argType = argTypesIterator.next();

            if (argType != param.getType()) {
                throw new SemanticException(node.getLPar(), "The argument "
                        + count + " is of the wrong type.");
            }

            count++;
        }

        // restaure la liste des types d'arguments
        this.argumentTypes = previousArgumentTypes;

        this.resultType = function.getReturnType();
    }

    @Override
    public void caseAArg(
            AArg node) {

        this.argumentTypes.add(evalType(node.getExp()));
    }

}
