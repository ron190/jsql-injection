package com.jsql.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.jsql.model.InjectionModel;
import com.jsql.model.MediatorModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.injection.method.MethodInjection;

public class SoapUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    public SoapUtil() {
        // TODO Auto-generated constructor stub
    }
    
    public SoapUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }
    InjectionModel injectionModel;

    public boolean testParameters() {
        boolean hasFoundInjection = false;
        
        if (
            injectionModel.preferencesUtil.isCheckingAllSOAPParam()
            && injectionModel.parameterUtil.isRequestSoap()
        ) {
            try {
                Document doc = SoapUtil.convertStringToDocument(injectionModel.parameterUtil.getRawRequest());
                LOGGER.trace("Parsing SOAP from Request...");
                hasFoundInjection = this.injectTextNodes(doc, doc.getDocumentElement());
            } catch (Exception e) {
                LOGGER.trace("SOAP not detected");
            }
        }
        
        return hasFoundInjection;
    }
    
    private static String convertDocumentToString(Document doc) {
        TransformerFactory tf = TransformerFactory.newInstance();
        String output = null;
        try {
            Transformer transformer= tf.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            output = writer.getBuffer().toString();
        } catch (TransformerException e) {
            // ignore
        }
        
        return output;
    }
    
    public static Document convertStringToDocument(String xmlStr) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xmlStr)));
        } catch (Exception e) {
            // ignore
        }
        return doc;
    }

    public static void deleteInjectionPoint(Document doc, Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                SoapUtil.deleteInjectionPoint(doc, currentNode);
            } else if (currentNode.getNodeType() == Node.TEXT_NODE) {
                currentNode.setTextContent(currentNode.getTextContent().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", ""));
            }
        }
    }

    public boolean injectTextNodes(Document doc, Node node) {
        NodeList nodeList = node.getChildNodes();
        boolean hasFoundInjection = false;
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                //calls this method for all the children which is Element
                hasFoundInjection = this.injectTextNodes(doc, currentNode);
                if (hasFoundInjection) {
                    break;
                }
            } else if (currentNode.getNodeType() == Node.TEXT_NODE) {
                SoapUtil.deleteInjectionPoint(doc, doc.getDocumentElement());
                
                currentNode.setTextContent(currentNode.getTextContent() + InjectionModel.STAR);
                
                injectionModel.parameterUtil.initRequest(SoapUtil.convertDocumentToString(doc));
                
                try {
                    LOGGER.info("Checking SOAP Request injection for "+ currentNode.getParentNode().getNodeName() +"="+ currentNode.getTextContent().replace(InjectionModel.STAR, ""));
                    
                    injectionModel.testParameters(MethodInjection.REQUEST);
                    hasFoundInjection = true;
                    
                    // Injection successful
                    break;
                    
                } catch (JSqlException e) {
                    // Injection failure
                    LOGGER.warn("No SOAP Request injection for "+ currentNode.getParentNode().getNodeName() +"="+ currentNode.getTextContent().replace(InjectionModel.STAR, ""), e);
                    
                } finally {
//                    // Erase * at the end of each params
//                    params.stream().forEach(e -> e.setValue(e.getValue().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", "")));
//
//                    // Erase * from JSON if failure
//                    if (!hasFoundInjection) {
//                        paramStar.setValue(paramStar.getValue().replace("*", ""));
//                    }
                }
                
            }
        }
        
        return hasFoundInjection;
    }
}
