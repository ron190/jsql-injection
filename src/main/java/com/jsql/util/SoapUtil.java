package com.jsql.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
import org.xml.sax.SAXException;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;

public class SoapUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    private InjectionModel injectionModel;
    
    public SoapUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    public boolean testParameters() {
        
        boolean hasFoundInjection = false;
        
        if (
            this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllSOAPParam()
            && this.injectionModel.getMediatorUtils().getParameterUtil().isRequestSoap()
        ) {
            try {
                Document doc = SoapUtil.convertToDocument(this.injectionModel.getMediatorUtils().getParameterUtil().getRawRequest());
                LOGGER.trace("Parsing SOAP from Request...");
                
                hasFoundInjection = this.injectTextNodes(doc, doc.getDocumentElement());
            } catch (Exception e) {
                LOGGER.trace("SOAP not detected");
            }
        }
        
        return hasFoundInjection;
    }
    
    public static Document convertToDocument(String xmlStr) throws ParserConfigurationException, SAXException, IOException {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
        factory.setExpandEntityReferences(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        
        return builder.parse(new InputSource(new StringReader(xmlStr)));
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
                
                SoapUtil.removeInjectionPoint(doc, doc.getDocumentElement());
                
                currentNode.setTextContent(currentNode.getTextContent() + InjectionModel.STAR);
                
                this.injectionModel.getMediatorUtils().getParameterUtil().initializeRequest(SoapUtil.convertDocumentToString(doc));
                
                try {
                    LOGGER.info("Checking SOAP Request injection for "+ currentNode.getParentNode().getNodeName() +"="+ currentNode.getTextContent().replace(InjectionModel.STAR, ""));
                    
                    this.injectionModel.getMediatorMethodInjection().getRequest().testParameters();
                    hasFoundInjection = true;
                    
                    // Injection successful
                    break;
                    
                } catch (JSqlException e) {
                    // Injection failure
                    LOGGER.warn("No SOAP Request injection for "+ currentNode.getParentNode().getNodeName() +"="+ currentNode.getTextContent().replace(InjectionModel.STAR, ""), e);
                }
            }
        }
        
        return hasFoundInjection;
    }

    public static void removeInjectionPoint(Document doc, Node node) {
        
        NodeList nodeList = node.getChildNodes();
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            
            Node currentNode = nodeList.item(i);
            
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                
                //calls this method for all the children which is Element
                SoapUtil.removeInjectionPoint(doc, currentNode);
                
            } else if (currentNode.getNodeType() == Node.TEXT_NODE) {
                
                currentNode.setTextContent(currentNode.getTextContent().replaceAll(Pattern.quote(InjectionModel.STAR) +"$", ""));
            }
        }
    }
    
    private static String convertDocumentToString(Document doc) {
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        
        String output = null;
        try {
            Transformer transformer= transformerFactory.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            output = writer.getBuffer().toString();
        } catch (TransformerException e) {
            // ignore
        }
        
        return output;
    }
}
