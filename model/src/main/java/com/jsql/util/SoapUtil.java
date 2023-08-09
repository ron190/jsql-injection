package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Pattern;

public class SoapUtil {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final InjectionModel injectionModel;
    
    public SoapUtil(InjectionModel injectionModel) {
        
        this.injectionModel = injectionModel;
    }

    public boolean testParameters() {
        
        var hasFoundInjection = false;
        
        if (
            this.injectionModel.getMediatorUtils().getPreferencesUtil().isCheckingAllSoapParam()
            && this.injectionModel.getMediatorUtils().getParameterUtil().isRequestSoap()
        ) {
            try {
                var doc = SoapUtil.convertToDocument(this.injectionModel.getMediatorUtils().getParameterUtil().getRawRequest());
                LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Parsing SOAP from Request...");
                
                hasFoundInjection = this.isTextNodeInjectable(doc, doc.getDocumentElement());
                
            } catch (Exception e) {
                
                LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "SOAP not detected");
            }
        }
        
        return hasFoundInjection;
    }
    
    public static Document convertToDocument(String xmlStr) throws ParserConfigurationException, SAXException, IOException {
        
        var factory = DocumentBuilderFactory.newInstance();
        
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtils.EMPTY);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, StringUtils.EMPTY);
        factory.setAttribute(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
        factory.setExpandEntityReferences(false);

        var builder = factory.newDocumentBuilder();
        
        return builder.parse(new InputSource(new StringReader(xmlStr)));
    }

    public boolean isTextNodeInjectable(Document doc, Node node) {
        
        var nodeList = node.getChildNodes();

        for (var i = 0; i < nodeList.getLength(); i++) {
            
            var currentNode = nodeList.item(i);
            
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                
                //calls this method for all the children which is Element
                if (this.isTextNodeInjectable(doc, currentNode)) {
                    return true;
                }
                
            } else if (currentNode.getNodeType() == Node.TEXT_NODE) {
                
                SoapUtil.removeInjectionPoint(doc, doc.getDocumentElement());
                
                currentNode.setTextContent(currentNode.getTextContent().replace(InjectionModel.STAR, StringUtils.EMPTY) + InjectionModel.STAR);
                
                this.injectionModel.getMediatorUtils().getParameterUtil().initializeRequest(SoapUtil.convertDocumentToString(doc));
                
                try {
                    LOGGER.log(
                        LogLevelUtil.CONSOLE_INFORM,
                        "Checking SOAP Request injection for {}={}",
                        () -> currentNode.getParentNode().getNodeName(),
                        () -> currentNode.getTextContent().replace(InjectionModel.STAR, StringUtils.EMPTY)
                    );
                    
                    if (this.injectionModel.getMediatorMethod().getRequest().testParameters()) {
                        return true;
                    }

                } catch (JSqlException e) {
                    
                    // Injection failure
                    LOGGER.log(
                        LogLevelUtil.CONSOLE_ERROR,
                        String.format(
                            "No SOAP Request injection for %s=%s",
                            currentNode.getParentNode().getNodeName(),
                            currentNode.getTextContent().replace(InjectionModel.STAR, StringUtils.EMPTY)
                        )
                    );
                }
            }
        }
        
        return false;
    }

    public static void removeInjectionPoint(Document doc, Node node) {
        
        var nodeList = node.getChildNodes();
        
        for (var i = 0; i < nodeList.getLength(); i++) {
            
            var currentNode = nodeList.item(i);
            
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
        
        var transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, StringUtils.EMPTY);
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, StringUtils.EMPTY);
        
        String output = null;
        
        try {
            var transformer = transformerFactory.newTransformer();
            var writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            output = writer.getBuffer().toString();
            
        } catch (TransformerException e) {
            // ignore
        }
        
        return output;
    }
}
