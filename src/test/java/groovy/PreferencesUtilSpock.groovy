package groovy

import com.jsql.util.PreferencesUtil

import spock.lang.Specification

class PreferencesUtilSpock extends Specification {

    def 'Check default values are set when loading saved preferences'() {
        
        when:
            def preferencesUtil = new PreferencesUtil()
            
        then:
            preferencesUtil.with {
                isCheckingUpdate == true
                isReportingBugs == true
                is4K == true
                
                isFollowingRedirection == false
                isNotInjectingMetadata == false
                
                isCheckingAllParam == false
                isCheckingAllURLParam == false
                isCheckingAllRequestParam == false
                isCheckingAllHeaderParam == false
                isCheckingAllJSONParam == false
                isCheckingAllCookieParam == false
                isCheckingAllSOAPParam == false
                
                isParsingForm == false
                isNotTestingConnection == false
                isProcessingCookies == false
                isProcessingCsrf == false
                
                isTamperingBase64 == false
                isTamperingEqualToLike == false
                isTamperingFunctionComment == false
                isTamperingVersionComment == false
                isTamperingRandomCase == false
                isTamperingEval == false
                isTamperingSpaceToDashComment == false
                isTamperingSpaceToMultlineComment == false
                isTamperingSpaceToSharpComment == false
            }
    }
    
    def 'Check saved preferences are loaded from the JVM'() {
        when:
            preferencesUtil.set(
                isCheckingUpdate,
                isReportingBugs,
                isFollowingRedirection,
                isNotInjectingMetadata,
                
                isCheckingAllParam,
                isCheckingAllURLParam,
                isCheckingAllRequestParam,
                isCheckingAllHeaderParam,
                isCheckingAllJSONParam,
                isCheckingAllCookieParam,
                isCheckingAllSOAPParam,
                
                isParsingForm,
                isNotTestingConnection,
                isProcessingCookies,
                isProcessingCsrf,
                
                isTamperingBase64,
                isTamperingEqualToLike,
                isTamperingFunctionComment,
                isTamperingVersionComment,
                isTamperingRandomCase,
                isTamperingEval,
                isTamperingSpaceToDashComment,
                isTamperingSpaceToMultlineComment,
                isTamperingSpaceToSharpComment,
                
                is4K
            )
        
        then:
            preferencesUtil.with {
                isCheckingUpdate == false
                isReportingBugs == false
                is4K == false
                
                isFollowingRedirection == true
                isNotInjectingMetadata == true
                
                isCheckingAllParam == true
                isCheckingAllURLParam == true
                isCheckingAllRequestParam == true
                isCheckingAllHeaderParam == true
                isCheckingAllJSONParam == true
                isCheckingAllCookieParam == true
                isCheckingAllSOAPParam == true
                
                isParsingForm == true
                isNotTestingConnection == true
                isProcessingCookies == true
                isProcessingCsrf == true
                
                isTamperingBase64 == true
                isTamperingEqualToLike == true
                isTamperingFunctionComment == true
                isTamperingVersionComment == true
                isTamperingRandomCase == true
                isTamperingEval == true
                isTamperingSpaceToDashComment == true
                isTamperingSpaceToMultlineComment == true
                isTamperingSpaceToSharpComment == true
            }
        
        when:
            def anotherPreferencesUtil = new PreferencesUtil()
            anotherPreferencesUtil.loadSavedPreferences()
            
        then:
            anotherPreferencesUtil.with {
                isCheckingUpdate == false
                isReportingBugs == false
                is4K == false
                
                isFollowingRedirection == true
                isNotInjectingMetadata == true
                
                isCheckingAllParam == true
                isCheckingAllURLParam == true
                isCheckingAllRequestParam == true
                isCheckingAllHeaderParam == true
                isCheckingAllJSONParam == true
                isCheckingAllCookieParam == true
                isCheckingAllSOAPParam == true
                
                isParsingForm == true
                isNotTestingConnection == true
                isProcessingCookies == true
                isProcessingCsrf == true
                
                isTamperingBase64 == true
                isTamperingEqualToLike == true
                isTamperingFunctionComment == true
                isTamperingVersionComment == true
                isTamperingRandomCase == true
                isTamperingEval == true
                isTamperingSpaceToDashComment == true
                isTamperingSpaceToMultlineComment == true
                isTamperingSpaceToSharpComment == true
            }
        
        where:
            preferencesUtil = new PreferencesUtil()
            
            isCheckingUpdate = false
            isReportingBugs = false
            is4K = false
            
            isFollowingRedirection = true
            isNotInjectingMetadata = true
            
            isCheckingAllParam = true
            isCheckingAllURLParam = true
            isCheckingAllRequestParam = true
            isCheckingAllHeaderParam = true
            isCheckingAllJSONParam = true
            isCheckingAllCookieParam = true
            isCheckingAllSOAPParam = true
            
            isParsingForm = true
            isNotTestingConnection = true
            isProcessingCookies = true
            isProcessingCsrf = true
            
            isTamperingBase64 = true
            isTamperingEqualToLike = true
            isTamperingFunctionComment = true
            isTamperingVersionComment = true
            isTamperingRandomCase = true
            isTamperingEval = true
            isTamperingSpaceToDashComment = true
            isTamperingSpaceToMultlineComment = true
            isTamperingSpaceToSharpComment = true
        
    }
    
    // Restore default preferences to jvm
    def cleanupSpec() {
        
        def preferencesUtil = new PreferencesUtil()
        preferencesUtil.set(
            true,
            true,
            false,
            
            false,
            false,
                 
            false,
            false,
            false,
            false,
            false,
            false,
            false,
                 
            false,
            false,
            false,
            false,
                 
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false
        )
    }
    
}