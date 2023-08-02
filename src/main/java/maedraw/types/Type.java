
package maedraw.types;

import maedraw.syntax.analysis.*;
import maedraw.syntax.node.*;

public enum Type {

    INT,
    BOOL,
    TIP,
    COLOR,
    SIZE,
    POS;

    private static class TypeFinder
            extends DepthFirstAdapter {

        private Type type;

        @Override
        public void caseAIntType(
                AIntType node) {

            this.type = Type.INT;
        }

        @Override
        public void caseABoolType(
                ABoolType node) {

            this.type = Type.BOOL;
        }

        @Override
        public void caseATipType(
                ATipType node) {

            this.type = Type.TIP;
        }

        @Override
        public void caseAColorType(
                AColorType node) {

            this.type = Type.COLOR;
        }

        @Override
        public void caseASizeType(
                ASizeType node) {

            this.type = Type.SIZE;
        }

        public Type getType(
                PType node) {

            this.type = null;
            node.apply(this);
            return this.type;
        }

    }

    private static final TypeFinder typeFinder = new TypeFinder();

    public static Type getType(
            PType node) {

        return typeFinder.getType(node);
    }
}
