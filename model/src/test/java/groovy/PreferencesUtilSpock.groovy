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
            }
    }
    
    def 'Check saved preferences are loaded from the JVM'() {
        when:
            preferencesUtil
            .withIsCheckingUpdate(isCheckingUpdate)
            .withIsReportingBugs(isReportingBugs)
            .withIsFollowingRedirection(isFollowingRedirection)
            .withIsNotInjectingMetadata(isNotInjectingMetadata)
            
            .withIsCheckingAllParam(isCheckingAllParam)
            .withIsCheckingAllURLParam(isCheckingAllURLParam)
            .withIsCheckingAllRequestParam(isCheckingAllRequestParam)
            .withIsCheckingAllHeaderParam(isCheckingAllHeaderParam)
            .withIsCheckingAllJsonParam(isCheckingAllJsonParam)
            .withIsCheckingAllCookieParam(isCheckingAllCookieParam)
            .withIsCheckingAllSoapParam(isCheckingAllSoapParam)

            .withIsParsingForm(isParsingForm)
            .withIsNotTestingConnection(isNotTestingConnection)
            .withIsNotProcessingCookies(isNotProcessingCookies)
            .withIsProcessingCsrf(isProcessingCsrf)

            .withIsTamperingBase64(isTamperingBase64)
            .withIsTamperingEqualToLike(isTamperingEqualToLike)
            .withIsTamperingFunctionComment(isTamperingFunctionComment)
            .withIsTamperingVersionComment(isTamperingVersionComment)
            .withIsTamperingRandomCase(isTamperingRandomCase)
            .withIsTamperingEval(isTamperingEval)
            .withIsTamperingSpaceToDashComment(isTamperingSpaceToDashComment)
            .withIsTamperingSpaceToMultilineComment(isTamperingSpaceToMultilineComment)
            .withIsTamperingSpaceToSharpComment(isTamperingSpaceToSharpComment)

            .withIs4K(is4K)

            .withIsLimitingThreads(isLimitingThreads)
            .withCountLimitingThreads(countLimitingThreads)

            .withIsCsrfUserTag(isCsrfUserTag)
            .withCsrfUserTag(csrfUserTag)
        
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
                isCheckingAllJsonParam == true
                isCheckingAllCookieParam == true
                isCheckingAllSoapParam == true
                
                isParsingForm == true
                isNotTestingConnection == true
                isNotProcessingCookies == true
                isProcessingCsrf == true
                
                isTamperingBase64 == true
                isTamperingEqualToLike == true
                isTamperingFunctionComment == true
                isTamperingVersionComment == true
                isTamperingRandomCase == true
                isTamperingEval == true
                isTamperingSpaceToDashComment == true
                isTamperingSpaceToMultilineComment == true
                isTamperingSpaceToSharpComment == true
                
                isLimitingThreads == true
                countLimitingThreads == 0
                
                isCsrfUserTag == true
                csrfUserTag == ""
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
                isCheckingAllJsonParam == true
                isCheckingAllCookieParam == true
                isCheckingAllSoapParam == true
                
                isParsingForm == true
                isNotTestingConnection == true
                isNotProcessingCookies == true
                isProcessingCsrf == true
                
                isTamperingBase64 == true
                isTamperingEqualToLike == true
                isTamperingFunctionComment == true
                isTamperingVersionComment == true
                isTamperingRandomCase == true
                isTamperingEval == true
                isTamperingSpaceToDashComment == true
                isTamperingSpaceToMultilineComment == true
                isTamperingSpaceToSharpComment == true
                
                isLimitingThreads == true
                countLimitingThreads == 0
                
                isCsrfUserTag == true
                csrfUserTag == ""
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
            isCheckingAllJsonParam = true
            isCheckingAllCookieParam = true
            isCheckingAllSoapParam = true
            
            isParsingForm = true
            isNotTestingConnection = true
            isNotProcessingCookies = true
            isProcessingCsrf = true
            
            isTamperingBase64 = true
            isTamperingEqualToLike = true
            isTamperingFunctionComment = true
            isTamperingVersionComment = true
            isTamperingRandomCase = true
            isTamperingEval = true
            isTamperingSpaceToDashComment = true
            isTamperingSpaceToMultilineComment = true
            isTamperingSpaceToSharpComment = true
                
            isLimitingThreads = true
            countLimitingThreads = 0
            
            isCsrfUserTag = true
            csrfUserTag = ""
    }
    
    // Restore default preferences to JVM
    def cleanupSpec() {
        
        def preferencesUtil = 
            new PreferencesUtil()
            .withCheckingUpdate()
            .withReportingBugs()
    }
}