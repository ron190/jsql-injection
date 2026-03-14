package com.jsql.util;

import com.jsql.model.InjectionModel;
import com.jsql.model.exception.JSqlException;
import com.jsql.model.injection.method.AbstractMethodInjection;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
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
    
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final InjectionModel injectionModel;
    
    public SoapUtil(InjectionModel injectionModel) {
        this.injectionModel = injectionModel;
    }

    public boolean testParameters(boolean hasFoundInjection) {
        if (!hasFoundInjection) {
            LOGGER.log(
                LogLevelUtil.CONSOLE_DEFAULT,
                "{} [SOAP] params...",
                () -> I18nUtil.valueByKey(AbstractMethodInjection.LOG_CHECKING)
            );
        } else {
            return true;
        }

        if (
            this.injectionModel.getMediatorUtils().preferencesUtil().isCheckingAllSoapParam()
            && this.injectionModel.getMediatorUtils().parameterUtil().isRequestSoap()
        ) {
            try {
                LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Parsing SOAP request...");
                if (this.injectionModel.getMediatorUtils().parameterUtil().getRawRequest().contains(InjectionModel.STAR)) {
                    return this.injectionModel.getMediatorMethod().getRequest().testParameters();
                } else {
                    var document = SoapUtil.convertToDocument(this.injectionModel.getMediatorUtils().parameterUtil().getRawRequest());
                    return this.isTextNodeInjectable(document, document.getDocumentElement());
                }
            } catch (ParserConfigurationException | IOException | SAXException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_DEFAULT, "Incorrect SOAP template: {}", e.getMessage());
            } catch (JSqlException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_ERROR, "No SOAP Request injection");
            }
        }
        return false;
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

    public boolean isTextNodeInjectable(Document originDocument, Node node) {
        var nodeList = SoapUtil.getNodeList(originDocument, node);
        for (var i = 0 ; i < nodeList.getLength() ; i++) {
            var currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                if (this.isTextNodeInjectable(originDocument, currentNode)) {
                    return true;
                }
            } else {
                SoapUtil.removeInjectionPoint(originDocument, originDocument.getDocumentElement());
                var origin = currentNode.getTextContent();
                currentNode.setTextContent(InjectionModel.STAR);
                this.injectionModel.getMediatorUtils().parameterUtil().initRequest(SoapUtil.convertDocumentToString(originDocument));

                try {
                    LOGGER.log(
                        LogLevelUtil.CONSOLE_INFORM,
                        "{} [SOAP] {}={}",
                        () -> I18nUtil.valueByKey(AbstractMethodInjection.LOG_CHECKING),
                        () -> currentNode.getParentNode().getNodeName(),
                        () -> currentNode.getTextContent().replace(InjectionModel.STAR, StringUtils.EMPTY)
                    );
                    if (this.injectionModel.getMediatorMethod().getRequest().testParameters()) {
                        return true;
                    }
                    currentNode.setTextContent(origin);  // restore
                } catch (JSqlException e) {  // Injection failure
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

    private static NodeList getNodeList(Document originDocument, Node node) {
        var nodeList = node.getChildNodes();
        if (nodeList.getLength() == 0) {  // force node check when empty
            try {
                var documentBuilderFactory = DocumentBuilderFactory.newInstance();
                var document = documentBuilderFactory.newDocumentBuilder().newDocument();
                Text textNode = document.createTextNode(StringUtils.EMPTY);
                Node nodeWithText = originDocument.importNode(textNode, true);
                node.appendChild(nodeWithText);
            } catch (ParserConfigurationException e) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, e, e);
            }
        }
        return nodeList;
    }

    public static void removeInjectionPoint(Document doc, Node node) {
        var nodeList = node.getChildNodes();
        for (var i = 0 ; i < nodeList.getLength() ; i++) {
            var currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                SoapUtil.removeInjectionPoint(doc, currentNode);  // calls this method for all the children which is Element
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
