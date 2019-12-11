package com.jsql.util.tampering;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class TamperingXml {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();
    
    private Model xmlModel;

    public TamperingXml(String fileXml) {
        JacksonXmlModule module = new JacksonXmlModule();
        module.setDefaultUseWrapper(false);
        XmlMapper xmlMapper = new XmlMapper(module);
        
        try {
            this.xmlModel = xmlMapper.readValue(TamperingXml.class.getClassLoader().getResource("tamper/"+ fileXml), Model.class);
        } catch (IOException e) {
            LOGGER.error(e, e);
        }
    }

    public Model getXmlModel() {
        return this.xmlModel;
    }

}
