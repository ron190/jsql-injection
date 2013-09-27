/*******************************************************************************
 * Copyhacked (H) 2012-2013.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.jsql.model.InjectionModel;
import com.jsql.view.GUITools;
import com.jsql.view.RoundScroller;
import com.jsql.view.bruteforce.HashBruter;
import com.jsql.view.component.popup.JPopupTextArea;
import com.jsql.view.component.popup.JPopupTextField;

/**
 * Manager to brute force a hash of various types.
 */
public class BruteForceManager extends JPanel{
    private static final long serialVersionUID = 7813237648910094160L;

    public AbstractButton run;

    private JPopupTextField hash;
    private JComboBox<String> hashTypes;
    
    private JCheckBox low;
    private JCheckBox up;
    private JCheckBox num;
    private JCheckBox spec;
    
    private JPopupTextField exclude;
    private JPopupTextField mini;
    private JPopupTextField max;
    
    private JPopupTextArea result;
    private JLabel loader;
    
    private InjectionModel model;

    public BruteForceManager(InjectionModel model){
        super(new BorderLayout());

        this.model = model;
        
        JPanel options = new JPanel(new BorderLayout());

        JPanel firstLine = new JPanel(new BorderLayout());
        firstLine.add(new JLabel(" Hash"), BorderLayout.WEST);

        hash = new JPopupTextField();
        hash.setToolTipText("<html><b>Hash to brute force</b><br>" +
                "<i>Passwords for admin pages or for database users are<br>" +
                "usually hashed inside database.</i></html>");
        firstLine.add(hash, BorderLayout.CENTER);
        hash.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2,2,2,0,UIManager.getColor ( "Panel.background" )),
                GUITools.BLU_ROUND_BORDER));

        final JPanel secondLine = new JPanel();
        secondLine.setLayout( new BoxLayout(secondLine, BoxLayout.X_AXIS) );

        low = new JCheckBox("a-z", true);
        up = new JCheckBox("A-Z", true);
        num = new JCheckBox("0-9", true);
        spec = new JCheckBox("Special", true);

        secondLine.add(new JLabel(" Type ", SwingConstants.RIGHT));

        hashTypes = new JComboBox<String>(new String[]{"md2","md5","sha-1","sha-256","sha-384",
                "sha-512","mysql"/*,"crc16","crc32","crc64","adler32"*/});
        hashTypes.setSelectedIndex(1);
        hashTypes.setMaximumSize( new Dimension((int) hashTypes.getPreferredSize().getWidth(),22) );
        hashTypes.setToolTipText("<html><b>Type of hash</b><br>" +
                "<i>MD5 is commonly used to hash password of admin pages. MySQL passwords are<br>" +
                "hashed differently (cf. Type mysql ; these are found into database 'mysql', table 'user').</i></html>");

        secondLine.add(hashTypes);

        secondLine.add(low);
        secondLine.add(up);
        secondLine.add(num);
        secondLine.add(spec);
        secondLine.add(Box.createGlue());

        low.setToolTipText("<html><b>Lower case characters</b><br>" +
                "Check if searched string contains any of following characters:<br>" +
                "<span style=\"font-family:'Courier New';\">abcdefghijklmnopqrstuvwxyz</span></html>");
        up.setToolTipText("<html><b>Upper case characters</b><br>" +
                "Check if searched string contains any of following characters:<br>" +
                "<span style=\"font-family:'Courier New';\">ABCDEFGHIJKLMNOPQRSTUVWXYZ</span></html>");
        num.setToolTipText("<html><b>Numeric characters</b><br>" +
                "Check if searched string contains any of following characters:<br>" +
                "<span style=\"font-family:'Courier New';\">0123456789</span></html>");
        spec.setToolTipText("<html><b>Special characters</b><br>" +
                "Check if searched string contains any of following characters:<br>" +
                "<span style=\"font-family:'Courier New';\">&nbsp;~`!@#$%^&*()_-+={}[]|\\;:'\"<.,>/?</span></html>");

        JPanel thirdLine = new JPanel();
        thirdLine.setLayout( new BoxLayout(thirdLine, BoxLayout.X_AXIS) );
        
        thirdLine.add(new JLabel(" Exclude ", SwingConstants.RIGHT));
        exclude = new JPopupTextField();
        exclude.setToolTipText("<html><b>Exclude characters</b><br>" +
                "Speed up process by excluding characters from the search.</html>");
        thirdLine.add(exclude);

        mini = new JPopupTextField("1");
        max = new JPopupTextField("5");
        
        thirdLine.add(new JLabel(" Length min. ", SwingConstants.RIGHT));
        thirdLine.add(mini);
        thirdLine.add(new JLabel("max. ", SwingConstants.RIGHT));
        thirdLine.add(max);

        mini.setToolTipText("<html><b>Minimum length of searched string</b><br>" +
                "Speed up process by specifying the minimum length to search.</html>");
        max.setToolTipText("<html><b>Maximum length of searched string</b><br>" +
                "Speed up process by specifying the maximum length to search.</html>");
        
        mini.setHorizontalAlignment(JTextField.RIGHT);
        max.setHorizontalAlignment(JTextField.RIGHT);

        exclude.setMaximumSize(new Dimension(90,(int) exclude.getPreferredSize().getHeight()));
        exclude.setMinimumSize(new Dimension(90,(int) exclude.getPreferredSize().getHeight()));

        mini.setMaximumSize(new Dimension(30,(int) mini.getPreferredSize().getHeight()));
        max.setMaximumSize(new Dimension(30,(int) max.getPreferredSize().getHeight()));
        mini.setMinimumSize(new Dimension(30,(int) mini.getPreferredSize().getHeight()));
        max.setMinimumSize(new Dimension(30,(int) max.getPreferredSize().getHeight()));

        final JPanel secondAndThirdLine = new JPanel(new BorderLayout());
        secondAndThirdLine.add(secondLine, BorderLayout.NORTH);
        secondAndThirdLine.add(thirdLine, BorderLayout.SOUTH);

        options.add(firstLine,BorderLayout.NORTH);
        options.add(secondAndThirdLine, BorderLayout.SOUTH);
        this.add(options, BorderLayout.NORTH);

        result = new JPopupTextArea();
        result.setLineWrap(true);
        this.add(new RoundScroller(result), BorderLayout.CENTER);
        
        JPanel lastLine = new JPanel();
        lastLine.setOpaque(false);
        lastLine.setLayout( new BoxLayout(lastLine, BoxLayout.X_AXIS) );
        run = new JButton("Start",new ImageIcon(getClass().getResource("/com/jsql/view/images/key.png")));
        run.setToolTipText("<html><b>Begin brute forcing the hash</b><br>" +
                "<i>Such process calculates a hash for every possible combinations of characters, hoping<br>" +
                "a hash will be equal to the user's one. It always either fails or never ends. Use instead<br>" +
                "websites like md5decrypter.co.uk to search for precalculated pairs of hash and password,<br>" +
                "also you may try other brute force softwares like John the Ripper.</i></html>");
        run.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2,0,0,0,UIManager.getColor ( "Panel.background" )),
                GUITools.BLU_ROUND_BORDER));

        loader = new JLabel(GUITools.SPINNER);
        loader.setVisible(false);

        lastLine.add(Box.createHorizontalGlue());
        lastLine.add(loader);
        lastLine.add(Box.createRigidArea(new Dimension(5,0)));
        lastLine.add(run);
        
        run.addActionListener(new BruteForceAction());
        
        this.add(lastLine, BorderLayout.SOUTH);
    }
    
    private class BruteForceAction implements ActionListener{
        final Boolean[] doStop = {false};
        
        @Override
        public void actionPerformed(ActionEvent arg0) {
            if(run.getText().equals("Stop")){
                run.setEnabled(false);
                doStop[0] = true;
            }
            else{
                try{
                    Integer.parseInt(max.getText());
                    Integer.parseInt(mini.getText());
                }catch(NumberFormatException e){
                    result.setText("*** Incorrect length");
                    return;
                }

                if(hash.getText().equals("")){
                    result.setText("*** Empty hash");
                    return;
                }else if(
                        !spec.isSelected()&&
                        !up.isSelected()&&
                        !low.isSelected()&&
                        !num.isSelected()){
                    result.setText("*** Select a character range");
                    return;
                }else if( Integer.parseInt(max.getText()) < Integer.parseInt(mini.getText()) ){
                    result.setText("*** Incorrect minimum and maximum length");
                    return;
                }

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // Reset the panel
                        run.setText("Stop");
                        loader.setVisible(true);
                        result.setText(null);

                        // Configure the hasher
                        final HashBruter hashBruter = new HashBruter();

                        hashBruter.setMinLength(Integer.parseInt(mini.getText()));
                        hashBruter.setMaxLength(Integer.parseInt(max.getText()));

                        if(spec.isSelected()) hashBruter.addSpecialCharacters();
                        if(up.isSelected()) hashBruter.addUpperCaseLetters();
                        if(low.isSelected()) hashBruter.addLowerCaseLetters();
                        if(num.isSelected()) hashBruter.addDigits();
                        if(!exclude.getText().equals("")) hashBruter.excludeChars(exclude.getText());

                        hashBruter.setType((String)hashTypes.getSelectedItem());
                        hashBruter.setHash(hash.getText().toUpperCase().replaceAll("[^a-zA-Z0-9]", "").trim());

                        // Begin the unhashing process
                        Thread thread = new Thread(new Runnable() { @Override public void run() { hashBruter.tryBruteForce(); } }, "Display brute force results");
                        thread.start();

                        while (!hashBruter.isDone() && !hashBruter.isFound() && !doStop[0]) {
                            hashBruter.setEndtime(System.nanoTime());

                            try {
                                Thread.sleep(1000); // /!\ KEEP IT: delay to update result panel /!\
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            
                            result.setText("Current string: " + hashBruter.getPassword() + "\n");
                            result.append("Current hash: " + hashBruter.getGeneratedHash() + "\n\n");
                            result.append("Number of possibilities: " + hashBruter.getNumberOfPossibilities() + "\n");
                            result.append("Checked hashes: " + hashBruter.getCounter() + "\n");
                            result.append("Estimated hashes left: " + hashBruter.getRemainder() + "\n");
                            result.append("Per second: " + hashBruter.getPerSecond() + "\n\n");
                            result.append( hashBruter.calculateTimeElapsed() + "\n");
                            
                            if(hashBruter.getPerSecond()!=0){
                                result.append( "Traversing remaining: " +
                                        Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder()))/(float)hashBruter.getPerSecond()/60f/60.0f/24f)) + "days " +
                                        Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder()))/(float)hashBruter.getPerSecond()/60f/60f%24)) + "h " +
                                        Math.round(Math.floor(Float.parseFloat(Long.toString(hashBruter.getRemainder()))/(float)hashBruter.getPerSecond()/60f%60)) + "min " +
                                        Math.round((Float.parseFloat(Long.toString(hashBruter.getRemainder()))/(float)hashBruter.getPerSecond())%60) + "s\n"); 
                            }
                            
                            result.append("Percent done: " + (100*(float)hashBruter.getCounter()/hashBruter.getNumberOfPossibilities()) + "%");
                            
                            if(doStop[0]){
                                hashBruter.setIsDone(true);
                                hashBruter.setFound(true);
                                break;
                            }
                        }

                        // Display the result
                        if(doStop[0]){
                            result.append("\n\n*** Aborted\n");
                        }else if(hashBruter.isFound()){
                            result.append("\n\nFound hash:\n" +
                                    hashBruter.getGeneratedHash() + "\n" +
                                    "String: " + hashBruter.getPassword());
                            
                            BruteForceManager.this.model.sendMessage("Found hash:\n" +
                                    hashBruter.getGeneratedHash() + "\n" +
                                    "String: " + hashBruter.getPassword());
                        }else if(hashBruter.isDone()){
                            result.append("\n\n*** Hash not found");
                        }
                        
                        doStop[0] = false;
                        loader.setVisible(false);
                        run.setText("Start");
                        run.setEnabled(true);
                    }
                }, "Start brute force").start();

            }
        }
    }
}
