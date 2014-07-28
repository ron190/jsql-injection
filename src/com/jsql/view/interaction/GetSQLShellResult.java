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
package com.jsql.view.interaction;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.UIManager;
import javax.swing.text.StyleConstants;

import com.jsql.view.GUIMediator;
import com.jsql.view.terminal.Terminal;

/**
 * Append the result of a command in the terminal
 */
public class GetSQLShellResult implements IInteractionCommand{
    // Unique identifier for the terminal.
    // Used for outputing results of commands in the right shell tab (in case of multiple shell opened)
    private UUID terminalID;

    // The result of a command executed in shell
    private String result;

    // The command executed in shell
//    private String cmd;

    /**
     * @param mainGUI
     * @param interactionParams The unique identifier of the terminal and the command's result to display
     */
    public GetSQLShellResult(Object[] interactionParams){
        terminalID = (UUID) interactionParams[0];
        result = (String) interactionParams[1];
//        cmd = (String) interactionParams[2];
    }

    /* (non-Javadoc)
     * @see com.jsql.mvc.view.message.ActionOnView#execute()
     */
    public void execute(){
        Terminal terminal = GUIMediator.gui().consoles.get(terminalID);
        
        if(result.indexOf("<SQLr>") > -1){
            ArrayList<ArrayList<String>> i = new ArrayList<ArrayList<String>>();
            Matcher regexSearch = Pattern.compile("<tr>(<td>.*?</td>)</tr>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(result);
            while(regexSearch.find()){
                String values = regexSearch.group(1);
                
                Matcher regexSearch2 = Pattern.compile("<td>(.*?)</td>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL).matcher(values);
                ArrayList<String> j = new ArrayList<String>();
                i.add(j);
                while(regexSearch2.find()){
                    String values2 = regexSearch2.group(1);
                    j.add(values2);
                }
            }
            
            if(i.size() <= 0){
                terminal.append("Empty result.\n");
            }else{
                ArrayList<Integer> ml = new ArrayList<Integer>();
                for(final int[] ii = {0}; ii[0] < i.get(0).size() ; ii[0]++){
                    Collections.sort( i, new Comparator<ArrayList<String>>() {
                        
                        @Override
                        public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                            return o2.get(ii[0]).length() - o1.get(ii[0]).length();
                        }
                        
                    } );
                    
                    ml.add(i.get(0).get(ii[0]).length());
                }
                
                if(!result.equals("")){
                    StyleConstants.setFontFamily(terminal.style, "monospaced");
                    StyleConstants.setFontSize(terminal.style, ((Font) UIManager.get("TextArea.font")).getSize()+1);

                    terminal.appendStyle("+");
                    for(Integer a1: ml){
                        terminal.appendStyle("-"+new String(new char[a1]).replace("\0", "-")+"-+");
                    }
                    terminal.appendStyle("\n");
                    
                    for(ArrayList<String> a1: i){
                        terminal.appendStyle("|");
                        int ii=0;
                        for(String s: a1){
                            terminal.appendStyle(" "+s+new String(new char[ml.get(ii)-s.length()]).replace("\0", " ")+" |");
                            ii++;
                        }
                        terminal.appendStyle("\n");
                    }
                    
                    terminal.appendStyle("+");
                    for(Integer a1: ml){
                        terminal.appendStyle("-"+new String(new char[a1]).replace("\0", "-")+"-+");
                    }
                    terminal.appendStyle("\n");
                    
                    StyleConstants.setFontFamily(terminal.style, "monospaced");
                    StyleConstants.setFontSize(terminal.style, ((Font) UIManager.get("TextArea.font")).getSize()+1);
                }
            }
        }else if(result.indexOf("<SQLm>") > -1){
            terminal.append(result.replace("<SQLm>", "") + "\n");
        }else if(result.indexOf("<SQLe>") > -1){
            terminal.append(result.replace("<SQLe>", "") + "\n");
        }else
            terminal.append("No result\n");
        terminal.append("\n");
        terminal.reset();
    }
}
