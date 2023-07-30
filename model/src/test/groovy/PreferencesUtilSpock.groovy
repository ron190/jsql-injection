import com.jsql.util.PreferencesUtil
import spock.lang.Specification

class PreferencesUtilSpock extends Specification {

    def 'Check default values are set when loading saved preferences'() {
        
        when:
            def preferencesUtil = new PreferencesUtil()
            
        then:
            preferencesUtil.isCheckingUpdate
            preferencesUtil.isReportingBugs
            !preferencesUtil.is4K
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

            preferencesUtil.persist()
        
        then:
            !preferencesUtil.isCheckingUpdate
            !preferencesUtil.isReportingBugs
            !preferencesUtil.is4K

            preferencesUtil.isFollowingRedirection
            preferencesUtil.isNotInjectingMetadata

            preferencesUtil.isCheckingAllParam
            preferencesUtil.isCheckingAllURLParam
            preferencesUtil.isCheckingAllRequestParam
            preferencesUtil.isCheckingAllHeaderParam
            preferencesUtil.isCheckingAllJsonParam
            preferencesUtil.isCheckingAllCookieParam
            preferencesUtil.isCheckingAllSoapParam

            preferencesUtil.isParsingForm
            preferencesUtil.isNotTestingConnection
            preferencesUtil.isNotProcessingCookies
            preferencesUtil.isProcessingCsrf

            preferencesUtil.isTamperingBase64
            preferencesUtil.isTamperingEqualToLike
            preferencesUtil.isTamperingFunctionComment
            preferencesUtil.isTamperingVersionComment
            preferencesUtil.isTamperingRandomCase
            preferencesUtil.isTamperingEval
            preferencesUtil.isTamperingSpaceToDashComment
            preferencesUtil.isTamperingSpaceToMultilineComment
            preferencesUtil.isTamperingSpaceToSharpComment

            preferencesUtil.isLimitingThreads
            preferencesUtil.countLimitingThreads == 0

            preferencesUtil.isCsrfUserTag
            preferencesUtil.csrfUserTag() == ""
        
        when:
            def anotherPreferencesUtil = new PreferencesUtil()
            anotherPreferencesUtil.loadSavedPreferences()
            
        then:
            !anotherPreferencesUtil.isCheckingUpdate
            !anotherPreferencesUtil.isReportingBugs
            !anotherPreferencesUtil.is4K

            anotherPreferencesUtil.isFollowingRedirection
            anotherPreferencesUtil.isNotInjectingMetadata

            anotherPreferencesUtil.isCheckingAllParam
            anotherPreferencesUtil.isCheckingAllURLParam
            anotherPreferencesUtil.isCheckingAllRequestParam
            anotherPreferencesUtil.isCheckingAllHeaderParam
            anotherPreferencesUtil.isCheckingAllJsonParam
            anotherPreferencesUtil.isCheckingAllCookieParam
            anotherPreferencesUtil.isCheckingAllSoapParam

            anotherPreferencesUtil.isParsingForm
            anotherPreferencesUtil.isNotTestingConnection
            anotherPreferencesUtil.isNotProcessingCookies
            anotherPreferencesUtil.isProcessingCsrf

            anotherPreferencesUtil.isTamperingBase64
            anotherPreferencesUtil.isTamperingEqualToLike
            anotherPreferencesUtil.isTamperingFunctionComment
            anotherPreferencesUtil.isTamperingVersionComment
            anotherPreferencesUtil.isTamperingRandomCase
            anotherPreferencesUtil.isTamperingEval
            anotherPreferencesUtil.isTamperingSpaceToDashComment
            anotherPreferencesUtil.isTamperingSpaceToMultilineComment
            anotherPreferencesUtil.isTamperingSpaceToSharpComment

            anotherPreferencesUtil.isLimitingThreads
            anotherPreferencesUtil.countLimitingThreads == 0

            anotherPreferencesUtil.isCsrfUserTag
            anotherPreferencesUtil.csrfUserTag() == ""

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
        
        def preferencesUtil = new PreferencesUtil()
        preferencesUtil.persist()
    }
}