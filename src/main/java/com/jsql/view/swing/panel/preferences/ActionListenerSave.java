package com.jsql.view.swing.panel.preferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.action.ActionNewWindow;
import com.jsql.view.swing.panel.PanelPreferences;

public class ActionListenerSave implements ActionListener {
    
    private PanelPreferences panelPreferences;

    public ActionListenerSave(PanelPreferences panelPreferences) {
        
        this.panelPreferences = panelPreferences;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        MediatorGui.model().getMediatorUtils().getPreferencesUtil().set(
            this.panelPreferences.getPanelGeneralPreferences().getCheckboxIsCheckingUpdate().isSelected(),
            this.panelPreferences.getPanelGeneralPreferences().getCheckboxIsReportingBugs().isSelected(),
            this.panelPreferences.getPanelGeneralPreferences().getCheckboxIs4K().isSelected(),
            
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxIsFollowingRedirection().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxIsNotInjectingMetadata().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxIsCheckingAllParam().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxIsCheckingAllURLParam().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxIsCheckingAllRequestParam().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxIsCheckingAllHeaderParam().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxIsCheckingAllJSONParam().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxIsCheckingAllCookieParam().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxIsCheckingAllSOAPParam().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxIsParsingForm().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxIsNotTestingConnection().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxProcessCookies().isSelected(),
            this.panelPreferences.getPanelInjectionPreferences().getCheckboxProcessCsrf().isSelected(),
            
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingBase64().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingEqualToLike().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingFunctionComment().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingVersionComment().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingRandomCase().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingEval().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getRadioIsTamperingSpaceToDashComment().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getRadioIsTamperingSpaceToMultilineComment().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getRadioIsTamperingSpaceToSharpComment().isSelected()
        );
        
        MediatorGui.model().getMediatorUtils().getProxyUtil().setPreferences(
            this.panelPreferences.getPanelProxyPreferences().getCheckboxIsUsingProxy().isSelected(),
            this.panelPreferences.getPanelProxyPreferences().getTextProxyAddress().getText(),
            this.panelPreferences.getPanelProxyPreferences().getTextProxyPort().getText(),
            this.panelPreferences.getPanelProxyPreferences().getCheckboxIsUsingProxyHttps().isSelected(),
            this.panelPreferences.getPanelProxyPreferences().getTextProxyAddressHttps().getText(),
            this.panelPreferences.getPanelProxyPreferences().getTextProxyPortHttps().getText()
        );
        
        MediatorGui.model().getMediatorUtils().getTamperingUtil().set(
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingBase64().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingVersionComment().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingFunctionComment().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingEqualToLike().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingRandomCase().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingHexToChar().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingQuoteToUtf8().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingEval().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getRadioIsTamperingSpaceToMultilineComment().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getRadioIsTamperingSpaceToDashComment().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getRadioIsTamperingSpaceToSharpComment().isSelected(),
            this.panelPreferences.getPanelTamperingPreferences().getCheckboxIsTamperingStringToChar().isSelected()
        );
        
        boolean isRestartRequired = MediatorGui.model().getMediatorUtils().getAuthenticationUtil().set(
            this.panelPreferences.getPanelAuthenticationPreferences().getCheckboxUseDigestAuthentication().isSelected(),
            this.panelPreferences.getPanelAuthenticationPreferences().getTextDigestAuthenticationUsername().getText(),
            this.panelPreferences.getPanelAuthenticationPreferences().getTextDigestAuthenticationPassword().getText(),
            this.panelPreferences.getPanelAuthenticationPreferences().getCheckboxUseKerberos().isSelected(),
            this.panelPreferences.getPanelAuthenticationPreferences().getTextKerberosKrb5Conf().getText(),
            this.panelPreferences.getPanelAuthenticationPreferences().getTextKerberosLoginConf().getText()
        );
        
        if (
            isRestartRequired
            && JOptionPane.showConfirmDialog(
                MediatorGui.frame(),
                "File krb5.conf has changed, please restart.",
                "Restart",
                JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION
        ) {
            new ActionNewWindow().actionPerformed(null);
        }
    }
}