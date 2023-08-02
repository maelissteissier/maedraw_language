package maedraw;

import maedraw.syntax.analysis.*;
import maedraw.syntax.node.*;
import maedraw.types.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class InterpreterEngine
        extends DepthFirstAdapter {

    private FunctionFinder functionFinder;

    private Map<String, Primitive> globalVariables = new HashMap<>();

    private Drawing drawingFrame = new Drawing();

    private Frame currentFrame;

    private Primitive result;

    private List<Primitive> argumentValues;

    public InterpreterEngine(FunctionFinder functionFinder, Map<String, Primitive> globalVariables) {

        this.functionFinder = functionFinder;
        this.globalVariables = globalVariables;
    }

    public InterpreterEngine(FunctionFinder functionFinder) {

        this.functionFinder = functionFinder;
    }

    public Frame getCurrentFrame() {
        return currentFrame;
    }

    public void visit(
            Node node) {

        if (node != null) {
            node.apply(this);
        }
    }

    private Primitive eval(
            Node node) {

        if (this.result != null) {
            throw new RuntimeException("Unexpected error in interpretor : resultType != null before node visit");
        }

        visit(node);

        if (this.result == null) {
            throw new RuntimeException("Unexpected error in interpretor : resultType == null after node visit");
        }

        Primitive result = this.result;
        this.result = null;
        return result;
    }

    private boolean evalBoolean(
            Node node,
            Token token) {

        Primitive value = eval(node);
        if (!(value instanceof BoolPrimitive)) {
            throw new InterpreterException(this.currentFrame, token,
                    "The expression should be a boolean");
        }

        return ((BoolPrimitive) value).getValue();
    }

    private Color evalColor(Node node, Token token){
        Primitive value = eval(node);
        if (!(value instanceof ColorPrimitive)) {
            throw new InterpreterException(this.currentFrame, token,
                    "The expression should be a color");
        }
        return ((ColorPrimitive) value).getValue();
    }

    private Tip evalTip(Node node, Token token){
        Primitive value = eval(node);
        if (!(value instanceof TipPrimitive)) {
            throw new InterpreterException(this.currentFrame, token,
                    "The expression should be a tip");
        }
        return ((TipPrimitive) value).getValue();
    }

    private double evalSize(Node node, Token token){
        Primitive value = eval(node);
        if (!(value instanceof SizePrimitive)) {
            throw new InterpreterException(this.currentFrame, token,
                    "The expression should be a size");
        }
        return ((SizePrimitive) value).getValue();
    }

    private Point2D evalPos(Node node, Token token){
        Primitive value = eval(node);
        if (!(value instanceof PosPrimitive)) {
            throw new InterpreterException(this.currentFrame, token,
                    "The expression should be a pos");
        }
        return ((PosPrimitive) value).getValue();
    }

    private int getNumber(
            Primitive value,
            Token token) {

        if (!(value instanceof IntPrimitive)) {
            throw new InterpreterException(this.currentFrame, token,
                    "The expression should be an int");
        }

        return ((IntPrimitive) value).getValue();
    }

    private int getNumberLeft(
            Primitive value,
            Token token) {

        if (!(value instanceof IntPrimitive)) {
            throw new InterpreterException(this.currentFrame, token,
                    "The left expression should be an int.");
        }

        return ((IntPrimitive) value).getValue();
    }

    private int getNumberRight(
            Primitive value,
            Token token) {

        if (!(value instanceof IntPrimitive)) {
            throw new InterpreterException(this.currentFrame, token,
                    "The right expression should be an int.");
        }

        return ((IntPrimitive) value).getValue();
    }

    @Override
    public void caseACanvasdecl(ACanvasdecl node) {
        Primitive width = eval(node.getWidth());
        int canvasWidth = getNumber(width, node.getCanvas());
        Primitive height = eval(node.getHeight());
        int canvasHeight = getNumber(height, node.getComma());
        this.drawingFrame.setCanvasWidth(canvasWidth);
        this.drawingFrame.setCanvasHeight(canvasHeight);
        this.drawingFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.drawingFrame.setLayout(new BorderLayout());
        this.drawingFrame.setUpGUI();
        this.drawingFrame.pack();
        this.drawingFrame.setVisible(true);
    }

    @Override
    public void caseAMainCode(
            AMainCode node) {

        this.currentFrame = new Frame(this.currentFrame, null, globalVariables);
        visit(node.getCanvasdecl());
        //don't visit funcdecl as they are only usefull for function finder and var/return checker
        visit(node.getBlock());

        this.currentFrame = null;
    }

    @Override
    public void caseALibCode(
            ALibCode node) {

        this.currentFrame = new Frame(this.currentFrame, null, globalVariables);

        for(PLibVarDeclInstr varDecl : node.getLibVarDeclInstr()){
            visit(varDecl);
        }
    }

    @Override
    public void caseALibVarDeclInstr(
            ALibVarDeclInstr node) {

        Primitive value = eval(node.getExp());
        this.currentFrame.assignVariable(node.getIdent().getText(), value);
    }

    @Override
    public void caseADeclInstr(
            ADeclInstr node) {

        Primitive value = eval(node.getExp());
        this.currentFrame.assignVariable(node.getIdent().getText(), value);
    }

    @Override
    public void caseAAssignInstr(
            AAssignInstr node) {

        Primitive value = eval(node.getExp());
        this.currentFrame.assignVariable(node.getIdent().getText(), value);
    }

    @Override
    public void caseATipMoveAngleInstr(ATipMoveAngleInstr node) {
        Primitive pencil = this.currentFrame.getValue(node.getIdent().getText());
        if (! (pencil instanceof TipPrimitive)){
            throw new InterpreterException(this.currentFrame, node.getIdent(),
                    "MOVE_ANGLE action can only be called on a tip");
        }

        this.drawingFrame.pencilTip = ((TipPrimitive) pencil).getValue();
        int moveLength = getNumber(eval(node.getNumMove()), node.getActionMoveAngle());
        int angleDegree = getNumber(eval(node.getAngle()), node.getDegree());
        this.drawingFrame.pencilTip.moveAngle(moveLength, this.drawingFrame.sketch, this.drawingFrame.drawing, angleDegree);
        // Keep new tip position in pencil variable
        this.currentFrame.assignVariable(node.getIdent().getText(), new TipPrimitive(this.drawingFrame.pencilTip));
    }

    @Override
    public void caseATipMovePosInstr(ATipMovePosInstr node){
        Primitive pencil = this.currentFrame.getValue(node.getIdent().getText());
        if (! (pencil instanceof TipPrimitive)){
            throw new InterpreterException(this.currentFrame, node.getIdent(),
                    "MOVE_POS action can only be called on a tip");
        }

        this.drawingFrame.pencilTip = ((TipPrimitive) pencil).getValue();
        Point2D destPos = evalPos(node.getPos(), node.getSc());

        this.drawingFrame.pencilTip.movePos(this.drawingFrame.sketch, this.drawingFrame.drawing, destPos.getX(), destPos.getY());

        // Keep new tip position in pencil variable
        this.currentFrame.assignVariable(node.getIdent().getText(), new TipPrimitive(this.drawingFrame.pencilTip));
    }

    @Override
    public void caseATipOffInstr(ATipOffInstr node) {
        Primitive pencil = this.currentFrame.getValue(node.getIdent().getText());
        if (! (pencil instanceof TipPrimitive)){
            throw new InterpreterException(this.currentFrame, node.getIdent(),
                    "MOVE action can only be called on a tip");
        }

        // get the tip from variables
        Tip pencilTip = ((TipPrimitive) pencil).getValue();
        // set new pencil state
        pencilTip.setMarkingState(MarkingState.OFF);
        // Keep new tip state in pencil variable
        this.currentFrame.assignVariable(node.getIdent().getText(), new TipPrimitive(pencilTip));
    }

    @Override
    public void caseATipOnInstr(ATipOnInstr node) {
        Primitive pencil = this.currentFrame.getValue(node.getIdent().getText());
        if (! (pencil instanceof TipPrimitive)){
            throw new InterpreterException(this.currentFrame, node.getIdent(),
                    "MOVE action can only be called on a tip");
        }

        // get the tip from variables
        Tip pencilTip = ((TipPrimitive) pencil).getValue();
        // set new pencil state
        pencilTip.setMarkingState(MarkingState.ON);
        // Keep new tip state in pencil variable
        this.currentFrame.assignVariable(node.getIdent().getText(), new TipPrimitive(pencilTip));
    }

    @Override
    public void caseATipColorInstr(ATipColorInstr node) {
        // TODO : change color of variable pencil and not current frame pencil !
        Primitive pencil = this.currentFrame.getValue(node.getIdent().getText());
        if (! (pencil instanceof TipPrimitive)){
            throw new InterpreterException(this.currentFrame, node.getIdent(),
                    "MOVE action can only be called on a tip");
        }

        // get the tip from variables
        Tip pencilTip = ((TipPrimitive) pencil).getValue();

        // set new color in the pencil
        Color newColor = evalColor(node.getColor(), node.getActionColor());
        pencilTip.setColor(newColor);

        // Keep new tip color in pencil variable
        this.currentFrame.assignVariable(node.getIdent().getText(), new TipPrimitive(pencilTip));
    }

    @Override
    public void caseAIfInstr(
            AIfInstr node) {

        if (evalBoolean(node.getExp(), node.getIf())) {
            visit(node.getBlock());
        } else {
            visit(node.getElseInstr());
        }
    }

    @Override
    public void caseAWhileInstr(
            AWhileInstr node) {

        while (evalBoolean(node.getExp(), node.getWhile())) {
            visit(node.getBlock());
        }
    }

    @Override
    public void caseAReturnInstr(
            AReturnInstr node) {

        if (node.getExp() != null) {
            Primitive value = eval(node.getExp());
            this.currentFrame.setReturnValue(value);
        }

        throw new ReturnException();
    }

    @Override
    public void caseACallInstr(
            ACallInstr node) {

        // sauvegarde les valeurs d'arguments courants
        java.util.List<Primitive> previousArgumentValues = this.argumentValues;

        // Évalue les arguments
        this.argumentValues = new LinkedList<>();
        visit(node.getArgs());

        // Enregistre la localisation de l'appel dans le cadre d'exécution
        // courant
        this.currentFrame.setCallLocation(node.getIdent());

        // Crée un nouveau cadre d'exécution (frame)
        FunctionInfo function = this.functionFinder
                .getFunctionInfo(node.getIdent().getText());
        this.currentFrame = new Frame(this.currentFrame, function, this.currentFrame.getGlobalVariables());

        // Assigne les valeurs des arguments dans les paramètres
        Iterator<Primitive> argumentValuesIterator = this.argumentValues.iterator();
        List<ParamInfo> params = function.getParams();
        for (ParamInfo param : params) {
            Primitive value = argumentValuesIterator.next();
            this.currentFrame.assignVariable(param.getName().getText(), value);
        }

        // exécute la procédure
        try {
            visit(function.getBody());
        }
        catch (ReturnException e) {
            // attrappe l'exception de retour, s'il y a lieu
        }

        // restaure les valeurs d'arguments courants
        this.argumentValues = previousArgumentValues;

        // Remet en place le cadre d'exécution
        this.currentFrame = this.currentFrame.getParent();

        // Enlève la localisation de l'appel du cadre d'exécution courant
        this.currentFrame.setCallLocation(null);
    }



    @Override
    public void caseAEqExp(
            AEqExp node) {

        Primitive left = eval(node.getLeft());
        Primitive right = eval(node.getRight());
        this.result = BoolPrimitive.get(left.equals(right));
    }

    @Override
    public void caseALtExp(
            ALtExp node) {

        int left = getNumberLeft(eval(node.getLeft()), node.getLt());
        int right = getNumberRight(eval(node.getRight()), node.getLt());
        this.result = BoolPrimitive.get(left < right);
    }

    @Override
    public void caseAGtExp(
            AGtExp node) {

        int left = getNumberLeft(eval(node.getLeft()), node.getGt());
        int right = getNumberRight(eval(node.getRight()), node.getGt());
        this.result = BoolPrimitive.get(left > right);
    }

    @Override
    public void caseAAddSum(
            AAddSum node) {

        Primitive left = eval(node.getLeft());
        Primitive right = eval(node.getRight());

        // Adding a color with an int = Brighten Color
        if (left instanceof ColorPrimitive && right instanceof IntPrimitive) {
            this.result = ((ColorPrimitive) left).brightenColor(((IntPrimitive) right).getValue());
            return;
        }

        // Color mixing. (RGB mixing behaviour != real color mixing behavior)
        if (left instanceof ColorPrimitive && right instanceof ColorPrimitive) {
            ((ColorPrimitive) left).mixColor(((ColorPrimitive) right).getValue());
            this.result = left;
            return;
        }

        // classic int addition
        if (left instanceof IntPrimitive) {
            this.result = new IntPrimitive(getNumberLeft(left, node.getPlus())
                    + getNumberRight(right, node.getPlus()));

            return;
        }

        throw new InterpreterException(this.currentFrame, node.getPlus(),
                "The left expression should be an int or a color");
    }

    @Override
    public void caseASubSum(
            ASubSum node) {
        Primitive left = eval(node.getLeft());
        Primitive right = eval(node.getRight());

        // Substract an int to a color = Darken Color
        if (left instanceof ColorPrimitive && right instanceof IntPrimitive) {
            this.result = ((ColorPrimitive) left).darkenColor(((IntPrimitive) right).getValue());
            return;
        }

        // Classic int substraction
        if (left instanceof IntPrimitive) {
            this.result = new IntPrimitive(getNumberLeft(left, node.getMinus())
                    - getNumberRight(right, node.getMinus()));

            return;
        }

        throw new InterpreterException(this.currentFrame, node.getMinus(),
                "The left expression should be an int or a color");
    }

    @Override
    public void caseAStarMul(AStarMul node) {
        int left = getNumberLeft(eval(node.getLeft()), node.getStar());
        int right = getNumberRight(eval(node.getRight()), node.getStar());
        this.result = new IntPrimitive(left * right);
    }

    @Override
    public void caseADivMul(ADivMul node) {
        int left = getNumberLeft(eval(node.getLeft()), node.getSlash());
        int right = getNumberRight(eval(node.getRight()), node.getSlash());
        if (right == 0){
            throw new InterpreterException(this.currentFrame, node.getSlash(), "The divider should not be 0");
        }
        this.result = new IntPrimitive(left / right);
    }

    @Override
    public void caseAModMul(AModMul node) {
        int left = getNumberLeft(eval(node.getLeft()), node.getPercent());
        int right = getNumberRight(eval(node.getRight()), node.getPercent());
        this.result = new IntPrimitive(left % right);
    }

    @Override
    public void caseANotNeg(
            ANotNeg node) {

        this.result = BoolPrimitive.get(!evalBoolean(node.getExp(), node.getNot()));
    }

    @Override
    public void caseANumberTerm(
            ANumberTerm node) {

        try {
            this.result = new IntPrimitive(
                    Integer.parseInt(node.getInteger().getText()));
        }
        catch (NumberFormatException e) {
            throw new InterpreterException(this.currentFrame, node.getInteger(),
                    "The number given is too big.");
        }
    }

    @Override
    public void caseAVarTerm(
            AVarTerm node) {

        String identifier = node.getIdent().getText();
        Primitive value = this.currentFrame.getValue(identifier);
        if (value == null) {
            throw new InterpreterException(this.currentFrame, node.getIdent(),
                    "The variable " + identifier + " has no value.");
        }
        this.result = value;
    }

    @Override
    public void caseATrueTerm(
            ATrueTerm node) {

        this.result = BoolPrimitive.TRUE;
    }

    @Override
    public void caseAFalseTerm(
            AFalseTerm node) {

        this.result = BoolPrimitive.FALSE;
    }

    @Override
    public void caseAColorTerm(AColorTerm node){

        this.result = eval(node.getColor());
    }

    @Override
    public void caseARedColor(ARedColor node){
        this.result = new ColorPrimitive(ColorEnum.RED);
    }

    @Override
    public void caseABlueColor(ABlueColor node){
        this.result = new ColorPrimitive(ColorEnum.BLUE);
    }

    @Override
    public void caseAYellowColor(AYellowColor node){
        this.result = new ColorPrimitive(ColorEnum.YELLOW);
    }

    @Override
    public void caseAWhiteColor(AWhiteColor node){
        this.result = new ColorPrimitive(ColorEnum.WHITE);
    }

    @Override
    public void caseABlackColor(ABlackColor node){
        this.result = new ColorPrimitive(ColorEnum.BLACK);
    }

    @Override
    public void caseARgbColor(ARgbColor node){
        Primitive r = eval(node.getR());
        Integer colorR = getNumber(r, node.getLBrk());
        if (colorR < 0 || colorR > 255){
            throw new InterpreterException(this.currentFrame, node.getLBrk(), "The Red color should be an int between 0 and 255");
        }
        Primitive g = eval(node.getG());
        Integer colorG = getNumber(g, node.getComma1());
        if (colorG < 0 || colorG > 255){
            throw new InterpreterException(this.currentFrame, node.getComma1(), "The Green color should be an int between 0 and 255");
        }
        Primitive b = eval(node.getB());
        Integer colorB = getNumber(b, node.getComma2());
        if (colorB < 0 || colorB > 255){
            throw new InterpreterException(this.currentFrame, node.getComma2(), "The Blue color should be an int between 0 and 255");
        }

        this.result = new ColorPrimitive(new Color(colorR, colorG, colorB));
    }


    @Override
    public void caseASizeTerm(ASizeTerm node){
        this.result = eval(node.getSize());
    }

    @Override
    public void caseAMedSize(AMedSize node){
        this.result = new SizePrimitive(Size.MED);
    }

    @Override
    public void caseALargeSize(ALargeSize node){
        this.result = new SizePrimitive(Size.LARGE);
    }

    @Override
    public void caseASmallSize(ASmallSize node){
        this.result = new SizePrimitive(Size.SMALL);
    }


    @Override
    public void caseAPencilTerm(APencilTerm node) {

        Color pencilColor = evalColor(node.getColor(), node.getLBrk());
        double pencilSize = evalSize(node.getSize(), node.getRBrk());
        Point2D pencilPos = evalPos(node.getPos(), node.getComma2());

        this.result = new TipPrimitive(new PencilTip(pencilPos.getX(),pencilPos.getY(), pencilSize, pencilColor));
    }

    @Override
    public void caseAPosTerm(APosTerm node) {
        int x = getNumber(eval(node.getPosX()), node.getLAcc());
        int y = getNumber(eval(node.getPosY()), node.getRAcc());
        this.result = new PosPrimitive(new Point2D.Double(x, y));
    }

    @Override
    public void caseACallTerm(
            ACallTerm node) {

        // sauvegarde les valeurs d'arguments courants
        List<Primitive> previousArgumentValues = this.argumentValues;

        // Évalue les arguments
        this.argumentValues = new LinkedList<>();
        visit(node.getArgs());

        // Enregistre la localisation de l'appel dans le cadre d'exécution
        // courant
        this.currentFrame.setCallLocation(node.getIdent());

        // Crée un nouveau cadre d'exécution (frame)
        FunctionInfo function = this.functionFinder
                .getFunctionInfo(node.getIdent().getText());
        this.currentFrame = new Frame(this.currentFrame, function);

        // Assigne les valeurs des arguments dans les paramètres
        Iterator<Primitive> argumentValuesIterator = this.argumentValues.iterator();
        List<ParamInfo> params = function.getParams();
        for (ParamInfo param : params) {
            Primitive value = argumentValuesIterator.next();
            this.currentFrame.assignVariable(param.getName().getText(), value);
        }

        // exécute la fonction

        try {
            visit(function.getBody());
            // Attention: Le contexte de l'erreur est celui de l'appelant
            // Puisque le frame de l'appelé n'a pas encore été dépilé, on passe
            // le parent du frame de l'appelé
            throw new InterpreterException(this.currentFrame.getParent(),
                    node.getIdent(), "The function " + node.getIdent().getText()
                    + " didn't return any value.");
        }
        catch (ReturnException e) {
            // attrappe l'exception de retour
        }

        // Récupère la valeur de retour
        this.result = this.currentFrame.getReturnValue();

        // restaure les valeurs d'arguments courants
        this.argumentValues = previousArgumentValues;

        // Remet en place le cadre d'exécution
        this.currentFrame = this.currentFrame.getParent();

        // Enlève la localisation de l'appel du cadre d'exécution courant
        this.currentFrame.setCallLocation(null);
    }

    @Override
    public void caseAArg(
            AArg node) {

        this.argumentValues.add(eval(node.getExp()));
    }
}
