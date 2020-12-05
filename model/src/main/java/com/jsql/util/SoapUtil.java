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

import org.apache.commons.lang3.StringUtils;
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
            this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllSoapParam()
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
        
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtils.EMPTY);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, StringUtils.EMPTY);
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
                    return true;
                }
                
            } else if (currentNode.getNodeType() == Node.TEXT_NODE) {
                
                SoapUtil.removeInjectionPoint(doc, doc.getDocumentElement());
                
                currentNode.setTextContent(currentNode.getTextContent().replace(InjectionModel.STAR, StringUtils.EMPTY) + InjectionModel.STAR);
                
                this.injectionModel.getMediatorUtils().getParameterUtil().initializeRequest(SoapUtil.convertDocumentToString(doc));
                
                try {
                    LOGGER.info("Checking SOAP Request injection for "+ currentNode.getParentNode().getNodeName() +"="+ currentNode.getTextContent().replace(InjectionModel.STAR, StringUtils.EMPTY));
                    
                    this.injectionModel.getMediatorMethod().getRequest().testParameters();

                    // Injection successful
                    return true;
                    
                } catch (JSqlException e) {
                    
                    // Injection failure
                    LOGGER.warn("No SOAP Request injection for "+ currentNode.getParentNode().getNodeName() +"="+ currentNode.getTextContent().replace(InjectionModel.STAR, StringUtils.EMPTY), e);
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
                
                currentNode.setTextContent(
                    currentNode
                    .getTextContent()
                    .replaceAll(Pattern.quote(InjectionModel.STAR) + "*$", StringUtils.EMPTY)
                );
            }
        }
    }
    
    private static String convertDocumentToString(Document doc) {
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtils.EMPTY);
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, StringUtils.EMPTY);
        
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
