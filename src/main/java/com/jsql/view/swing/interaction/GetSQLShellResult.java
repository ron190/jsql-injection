/*******************************************************************************
 * Copyhacked (H) 2012-2014.
 * This program and the accompanying materials
 * are made available under no term at all, use it like
 * you want, but share and discuss about it
 * every time possible with every body.
 * 
 * Contributors:
 *      ron190 at ymail dot com - initial implementation
 ******************************************************************************/
package com.jsql.view.swing.interaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.jsql.view.swing.MediatorGui;
import com.jsql.view.swing.shell.AbstractShell;

/**
 * Append the result of a command in the terminal.
 */
public class GetSQLShellResult implements InteractionCommand {
    /**
     * Unique identifier for the terminal. Used for outputing results of
     * commands in the right shell tab (in case of multiple shell opened).
     */
    private UUID terminalID;

    /**
     * The result of a command executed in shell.
     */
    private String result;

    /**
     * @param interactionParams The unique identifier of the terminal and the command's result to display
     */
    public GetSQLShellResult(Object[] interactionParams) {
        terminalID = (UUID) interactionParams[0];
        result = (String) interactionParams[1];
    }

    @Override
    public void execute() {
        AbstractShell terminal = MediatorGui.frame().getConsoles().get(this.terminalID);
        
        if (this.result.indexOf("<SQLr>") > -1) {
            List<List<String>> listRows = new ArrayList<>();
            Matcher rowsMatcher = Pattern.compile("(?si)<tr>(<td>.*?</td>)</tr>").matcher(this.result);
            while (rowsMatcher.find()) {
                String values = rowsMatcher.group(1);
                
                Matcher fieldsMatcher = Pattern.compile("(?si)<td>(.*?)</td>").matcher(values);
                List<String> listFields = new ArrayList<>();
                listRows.add(listFields);
                while (fieldsMatcher.find()) {
                    String field = fieldsMatcher.group(1);
                    listFields.add(field);
                }
            }
            
            if (listRows.isEmpty()) {
                terminal.append("Empty result.\n");
            } else {
                List<Integer> listFieldsLength = new ArrayList<>();
                for (
                    final int[] indexLongestRowSearch = {0}; 
                    indexLongestRowSearch[0] < listRows.get(0).size(); 
                    indexLongestRowSearch[0]++
                ) {
                    Collections.sort(
                        listRows, 
                        new Comparator<List<String>>() {
                            @Override
                            public int compare(List<String> firstRow, List<String> secondRow) {
                                return secondRow.get(indexLongestRowSearch[0]).length() - firstRow.get(indexLongestRowSearch[0]).length();
                            }
                        }
                    );
                    
                    listFieldsLength.add(listRows.get(0).get(indexLongestRowSearch[0]).length());
                }
                
                if (!"".equals(this.result)) {
                    terminal.append("+");
                    for (Integer fieldLength: listFieldsLength) {
                        terminal.append("-" + StringUtils.repeat("-", fieldLength) + "-+");
                    }
                    terminal.append("\n");
                    
                    for (List<String> listFields: listRows) {
                        terminal.append("|");
                        int cursorPosition = 0;
                        for (String field: listFields) {
                            terminal.append(" " + field + StringUtils.repeat(" ", listFieldsLength.get(cursorPosition) - field.length()) + " |");
                            cursorPosition++;
                        }
                        terminal.append("\n");
                    }
                    
                    terminal.append("+");
                    for (Integer fieldLength: listFieldsLength) {
                        terminal.append("-" + StringUtils.repeat("-", fieldLength) + "-+");
                    }
                    terminal.append("\n");
                }
            }
        } else if (this.result.indexOf("<SQLm>") > -1) {
            terminal.append(this.result.replace("<SQLm>", "") + "\n");
        } else if (this.result.indexOf("<SQLe>") > -1) {
            terminal.append(this.result.replace("<SQLe>", "") + "\n");
        } else {
            terminal.append("No result\n");
        }
        terminal.append("\n");
        terminal.reset();
    }
}
