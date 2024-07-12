package org.xmlevaluator;


import org.w3c.dom.Document;
import org.xmlevaluator.dto.*;
import org.xmlevaluator.evaluator.Calculator;
import org.xmlevaluator.treeMaker.TreeProvider;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class Main {
    public static void main(String args[]) {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse("path_to_file");
        } catch (Exception e) {
            System.out.println("Error when reading file.");
        }

        if (document == null) {
            return;
        }

        TreeProvider treeProvider = new TreeProvider();
        Tree tree = treeProvider.buildTree("//dateVar");

        VariableStorage storage = new VariableStorage();
        storage.pushVariable("varDate1", "2022-12-12", ExpressionType.DATE);
        storage.pushVariable("varDate2", "2023-12-12", ExpressionType.DATE);

        Calculator calculator = new Calculator(storage, document);

        Result result = calculator.evaluate(tree);

        System.out.println(result.getValue());
    }
}
