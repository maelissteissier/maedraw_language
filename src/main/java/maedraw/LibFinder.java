package maedraw;

import java.util.*;
import maedraw.syntax.analysis.DepthFirstAdapter;
import maedraw.syntax.node.ALibimport;

public class LibFinder extends DepthFirstAdapter {
    List<String> libs = new LinkedList<>();

    @Override
    public void caseALibimport(ALibimport node) {
        String libName = node.getPath().getText();
        if (!libs.contains(libName)){
            libs.add(node.getPath().getText());
        } else {
            throw new SemanticException(node.getImport(), "two libraries with the same name (" + libName + ") have been imported");
        }
    }
}
