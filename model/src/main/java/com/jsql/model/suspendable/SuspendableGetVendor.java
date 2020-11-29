package com.jsql.model.suspendable;

import java.util.EnumMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jsql.model.InjectionModel;
import com.jsql.model.bean.util.Header;
import com.jsql.model.bean.util.Interaction;
import com.jsql.model.bean.util.Request;
import com.jsql.model.exception.StoppedByUserSlidingException;
import com.jsql.model.injection.vendor.MediatorVendor;
import com.jsql.model.injection.vendor.model.Vendor;
import com.jsql.util.I18nUtil;

/**
 * Runnable class, define insertionCharacter that will be used by all futures requests,
 * i.e -1 in "[..].php?id=-1 union select[..]", sometimes it's -1, 0', 0, etc,
 * this class/function tries to find the working one by searching a special error message
 * in the source page.
 */
public class SuspendableGetVendor extends AbstractSuspendable<Vendor> {
    
    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = Logger.getRootLogger();

    public SuspendableGetVendor(InjectionModel injectionModel) {
        super(injectionModel);
    }

    @Override
    public Vendor run(Object... args) throws StoppedByUserSlidingException {
        
        Vendor vendor = null;
        
        if (this.injectionModel.getMediatorVendor().getVendorByUser() != this.injectionModel.getMediatorVendor().getAuto()) {
            
            vendor = this.injectionModel.getMediatorVendor().getVendorByUser();
            LOGGER.info(I18nUtil.valueByKey("LOG_DATABASE_TYPE_FORCED_BY_USER") +" ["+ vendor +"]");
            
        } else {
            
            LOGGER.trace("Fingerprinting database...");
        
            String insertionCharacter = "'\"#-)'\"*";
            String pageSource = this.injectionModel.injectWithoutIndex(insertionCharacter, "get:vendor");
                
            MediatorVendor mediatorVendor = this.injectionModel.getMediatorVendor();
            Vendor[] vendors =
                mediatorVendor
                .getVendors()
                .stream()
                .filter(v -> v != mediatorVendor.getAuto())
                .toArray(Vendor[]::new);
            
            // Test each vendor
            for (Vendor vendorTest: vendors) {
                
                if (
                    pageSource.matches(
                        "(?si).*("
                        + vendorTest.instance().fingerprintErrorsAsRegex()
                        + ").*"
                    )
                ) {
                    vendor = vendorTest;
                    LOGGER.info("Possibly ["+ vendor +"] from basic fingerprinting");
                    break;
                }
            }
            
            vendor = this.initializeVendor(vendor);
        }
        
        Request requestSetVendor = new Request();
        requestSetVendor.setMessage(Interaction.SET_VENDOR);
        requestSetVendor.setParameters(vendor);
        this.injectionModel.sendToViews(requestSetVendor);
        
        return vendor;
    }

    public Vendor initializeVendor(Vendor vendor) {
        
        Vendor vendorFixed = vendor;
        
        if (vendorFixed == null) {
            
            vendorFixed = this.injectionModel.getMediatorVendor().getMySQL();
            LOGGER.info(I18nUtil.valueByKey("LOG_DATABASE_TYPE_NOT_FOUND") +" ["+ vendorFixed +"]");
            
        } else {
            
            LOGGER.info(I18nUtil.valueByKey("LOG_USING_DATABASE_TYPE") +" ["+ vendorFixed +"]");
            
            Map<Header, Object> msgHeader = new EnumMap<>(Header.class);
            msgHeader.put(
                Header.URL,
                this.injectionModel.getMediatorUtils().getConnectionUtil().getUrlByUser()
            );
            msgHeader.put(Header.VENDOR, vendorFixed);
            
            Request requestDatabaseIdentified = new Request();
            requestDatabaseIdentified.setMessage(Interaction.DATABASE_IDENTIFIED);
            requestDatabaseIdentified.setParameters(msgHeader);
            this.injectionModel.sendToViews(requestDatabaseIdentified);
        }
        
        return vendorFixed;
    }
}