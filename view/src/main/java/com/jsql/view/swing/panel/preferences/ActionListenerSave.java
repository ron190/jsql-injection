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
        
        MediatorHelper.model().getMediatorUtils().getPreferencesUtil().set(
            this.panelPreferences.getPanelGeneral().getCheckboxIsCheckingUpdate().isSelected(),
            this.panelPreferences.getPanelGeneral().getCheckboxIsReportingBugs().isSelected(),
            this.panelPreferences.getPanelGeneral().getCheckboxIs4K().isSelected(),
            
            this.panelPreferences.getPanelGeneral().getCheckboxIsFollowingRedirection().isSelected(),
            this.panelPreferences.getPanelInjection().getCheckboxIsNotInjectingMetadata().isSelected(),
            
            this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllParam().isSelected(),
            this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllURLParam().isSelected(),
            this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllRequestParam().isSelected(),
            this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllHeaderParam().isSelected(),
            this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllJSONParam().isSelected(),
            this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllCookieParam().isSelected(),
            this.panelPreferences.getPanelInjection().getCheckboxIsCheckingAllSOAPParam().isSelected(),
            this.panelPreferences.getPanelInjection().getCheckboxIsParsingForm().isSelected(),
            
            this.panelPreferences.getPanelGeneral().getCheckboxIsNotTestingConnection().isSelected(),
            this.panelPreferences.getPanelGeneral().getCheckboxProcessCookies().isSelected(),
            this.panelPreferences.getPanelGeneral().getCheckboxProcessCsrf().isSelected(),
            
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingBase64().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingEqualToLike().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingFunctionComment().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingVersionComment().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingRandomCase().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingEval().isSelected(),
            this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToDashComment().isSelected(),
            this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToMultilineComment().isSelected(),
            this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToSharpComment().isSelected(),
            
            this.panelPreferences.getPanelGeneral().getCheckboxIsLimitingThreads().isSelected(),
            (Integer) this.panelPreferences.getPanelGeneral().getSpinnerLimitingThreads().getValue(),
            this.panelPreferences.getPanelGeneral().getCheckboxIsCsrfUserTag().isSelected(),
            this.panelPreferences.getPanelGeneral().getTextfieldCsrfUserTag().getText()
        );
        
        MediatorHelper.model().getMediatorUtils().getProxyUtil().setPreferences(
            this.panelPreferences.getPanelProxy().getCheckboxIsUsingProxy().isSelected(),
            this.panelPreferences.getPanelProxy().getTextProxyAddress().getText(),
            this.panelPreferences.getPanelProxy().getTextProxyPort().getText(),
            this.panelPreferences.getPanelProxy().getCheckboxIsUsingProxyHttps().isSelected(),
            this.panelPreferences.getPanelProxy().getTextProxyAddressHttps().getText(),
            this.panelPreferences.getPanelProxy().getTextProxyPortHttps().getText()
        );
        
        MediatorHelper.model().getMediatorUtils().getTamperingUtil().set(
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingBase64().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingVersionComment().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingFunctionComment().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingEqualToLike().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingRandomCase().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingHexToChar().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingQuoteToUtf8().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingEval().isSelected(),
            this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToMultilineComment().isSelected(),
            this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToDashComment().isSelected(),
            this.panelPreferences.getPanelTampering().getRadioIsTamperingSpaceToSharpComment().isSelected(),
            this.panelPreferences.getPanelTampering().getCheckboxIsTamperingStringToChar().isSelected()
        );
        
        boolean isRestartRequired = MediatorHelper.model().getMediatorUtils().getAuthenticationUtil().set(
            this.panelPreferences.getPanelAuthentication().getCheckboxUseDigestAuthentication().isSelected(),
            this.panelPreferences.getPanelAuthentication().getTextDigestAuthenticationUsername().getText(),
            this.panelPreferences.getPanelAuthentication().getTextDigestAuthenticationPassword().getText(),
            this.panelPreferences.getPanelAuthentication().getCheckboxUseKerberos().isSelected(),
            this.panelPreferences.getPanelAuthentication().getTextKerberosKrb5Conf().getText(),
            this.panelPreferences.getPanelAuthentication().getTextKerberosLoginConf().getText()
        );
        
        if (
            isRestartRequired
            && JOptionPane.showConfirmDialog(
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