package com.jsql.view.swing.panel.preferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.jsql.view.swing.action.ActionNewWindow;
import com.jsql.view.swing.panel.PanelPreferences;
import com.jsql.view.swing.util.MediatorHelper;

public class ActionListenerSave implements ActionListener {
    
    private PanelPreferences panelPreferences;

    public ActionListenerSave(PanelPreferences panelPreferences) {
        
        this.panelPreferences = panelPreferences;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        MediatorHelper.model().getMediatorUtils().getPreferencesUtil()
        
        .withIsCheckingUpdate(this.panelPreferences.getPanelGeneral().getCheckboxIsCheckingUpdate().isSelected())
        .withIsReportingBugs(this.panelPreferences.getPanelGeneral().getCheckboxIsReportingBugs().isSelected())
        .withIs4K(this.panelPreferences.getPanelGeneral().getCheckboxIs4K().isSelected())
        
        .withIsFollowingRedirection(this.panelPreferences.getPanelConnection().getCheckboxIsFollowingRedirection().isSelected())
        .withIsUnicodeDecodeDisabled(this.panelPreferences.getPanelConnection().getCheckboxIsUnicodeDecodeDisabled().isSelected())
        .withIsNotTestingConnection(this.panelPreferences.getPanelConnection().getCheckboxIsNotTestingConnection().isSelected())
        .withIsNotProcessingCookies(this.panelPreferences.getPanelConnection().getCheckboxIsNotProcessingCookies().isSelected())
        .withIsProcessingCsrf(this.panelPreferences.getPanelConnection().getCheckboxProcessCsrf().isSelected())
        .withIsLimitingThreads(this.panelPreferences.getPanelConnection().getCheckboxIsLimitingThreads().isSelected())
        .withCountLimitingThreads((Integer) this.panelPreferences.getPanelConnection().getSpinnerLimitingThreads().getValue())
        .withIsConnectionTimeout(this.panelPreferences.getPanelConnection().getCheckboxIsConnectionTimeout().isSelected())
        .withCountConnectionTimeout((Integer) this.panelPreferences.getPanelConnection().getSpinnerConnectionTimeout().getValue())
        .withIsCsrfUserTag(this.panelPreferences.getPanelConnection().getCheckboxIsCsrfUserTag().isSelected())
        .withCsrfUserTag(this.panelPreferences.getPanelConnection().getTextfieldCsrfUserTag().getText())
        .withCsrfUserTagOutput(this.panelPreferences.getPanelConnection().getTextfieldCsrfUserTagOutput().getText())
                
        .withIsParsingForm(this.panelPreferences.getPanelInjection().getCheckboxIsParsingForm().isSelected())
        .withIsNotInjectingMetadata(this.panelPreferences.getPanelInjection().getCheckboxIsNotInjectingMetadata().isSelected())
        .withIsLimitingNormalIndex(this.panelPreferences.getPanelInjection().getCheckboxIsLimitingNormalIndex().isSelected())
        .withCountNormalIndex((Integer) this.panelPreferences.getPanelInjection().getSpinnerNormalIndexCount().getValue())
        .withIsCheckingAllParam(this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllParam().isSelected())
        .withIsCheckingAllURLParam(this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllURLParam().isSelected())
        .withIsCheckingAllRequestParam(this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllRequestParam().isSelected())
        .withIsCheckingAllHeaderParam(this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllHeaderParam().isSelected())
        .withIsCheckingAllBase64Param(this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllBase64Param().isSelected())
        .withIsCheckingAllJsonParam(this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllJsonParam().isSelected())
        .withIsCheckingAllSoapParam(this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllSoapParam().isSelected())
        .withIsCheckingAllCookieParam(this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllCookieParam().isSelected())
        .withIsDefaultStrategy(this.panelPreferences.getPanelInjection().getRadioIsDefaultStrategy().isSelected())
        .withIsZipStrategy(this.panelPreferences.getPanelInjection().getRadioIsZipStrategy().isSelected())
        .withIsDiosStrategy(this.panelPreferences.getPanelInjection().getRadioIsDiosStrategy().isSelected())
        .withIsUrlEncodingDisabled(this.panelPreferences.getPanelInjection().getCheckboxIsUrlEncodingDisabled().isSelected())
        .withIsPerfIndexDisabled(this.panelPreferences.getPanelInjection().getCheckboxIsPerfIndexDisabled().isSelected())
        .withIsLimitingSleepTimeStrategy(this.panelPreferences.getPanelInjection().getCheckboxIsLimitingSleepTimeStrategy().isSelected())
        .withCountSleepTimeStrategy((Integer) this.panelPreferences.getPanelInjection().getSpinnerSleepTimeStrategy().getValue())
                
        .withIsTamperingBase64(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingBase64().isSelected())
        .withIsTamperingEqualToLike(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingEqualToLike().isSelected())
        .withIsTamperingFunctionComment(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingFunctionComment().isSelected())
        .withIsTamperingVersionComment(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingVersionComment().isSelected())
        .withIsTamperingRandomCase(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingRandomCase().isSelected())
        .withIsTamperingEval(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingEval().isSelected())
        .withIsTamperingSpaceToDashComment(this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToDashComment().isSelected())
        .withIsTamperingSpaceToMultilineComment(this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToMultilineComment().isSelected())
        .withIsTamperingSpaceToSharpComment(this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToSharpComment().isSelected())
        
        .persist();
        
        MediatorHelper.model().getMediatorUtils().getProxyUtil().setPreferences(
            this.panelPreferences.getPanelProxy().getCheckboxIsUsingProxy().isSelected(),
            this.panelPreferences.getPanelProxy().getTextProxyAddress().getText(),
            this.panelPreferences.getPanelProxy().getTextProxyPort().getText(),
            this.panelPreferences.getPanelProxy().getCheckboxIsUsingProxyHttps().isSelected(),
            this.panelPreferences.getPanelProxy().getTextProxyAddressHttps().getText(),
            this.panelPreferences.getPanelProxy().getTextProxyPortHttps().getText()
        );
        
        MediatorHelper.model().getMediatorUtils().getTamperingUtil()
        .withBase64(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingBase64().isSelected())
        .withEqualToLike(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingEqualToLike().isSelected())
        .withEval(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingEval().isSelected())
        .withFunctionComment(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingFunctionComment().isSelected())
        .withHexToChar(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingHexToChar().isSelected())
        .withQuoteToUtf8(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingQuoteToUtf8().isSelected())
        .withRandomCase(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingRandomCase().isSelected())
        .withSpaceToDashComment(this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToDashComment().isSelected())
        .withSpaceToMultilineComment(this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToMultilineComment().isSelected())
        .withSpaceToSharpComment(this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToSharpComment().isSelected())
        .withStringToChar(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingStringToChar().isSelected())
        .withVersionComment(this.panelPreferences.getPanelTampering().getCheckboxIsTamperingVersionComment().isSelected());
        
        boolean isRestartRequired = MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().set(
            this.panelPreferences.getPanelAuth().getCheckboxUseDigestAuthentication().isSelected(),
            this.panelPreferences.getPanelAuth().getTextDigestAuthenticationUsername().getText(),
            this.panelPreferences.getPanelAuth().getTextDigestAuthenticationPassword().getText(),
            this.panelPreferences.getPanelAuth().getCheckboxUseKerberos().isSelected(),
            this.panelPreferences.getPanelAuth().getTextKerberosKrb5Conf().getText(),
            this.panelPreferences.getPanelAuth().getTextKerberosLoginConf().getText()
        );
        
        if (
            isRestartRequired
            && JOptionPane
                .showConfirmDialog(
                    MediatorHelper.frame(),
                    "File krb5.conf has changed, please restart.",
                    "Restart",
                    JOptionPane.YES_NO_OPTION
                ) == JOptionPane.YES_OPTION
        ) {
            new ActionNewWindow().actionPerformed(null);
        }
    }
}