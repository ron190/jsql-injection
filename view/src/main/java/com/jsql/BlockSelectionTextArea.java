package com.jsql;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;

public class BlockSelectionTextArea extends JTextArea {

    private int startRow = -1, startCol = -1;
    private int endRow = -1, endCol = -1;

    public BlockSelectionTextArea() {
        // Mouse listener to initiate and handle the selection
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Record the initial mouse press position (row, col)
                int row = BlockSelectionTextArea.this.getRowAtPosition(e.getPoint());
                int col = BlockSelectionTextArea.this.getColumnAtPosition(e.getPoint());
                BlockSelectionTextArea.this.startRow = row;
                BlockSelectionTextArea.this.startCol = col;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // Finalize the block selection on mouse release
                int row = BlockSelectionTextArea.this.getRowAtPosition(e.getPoint());
                int col = BlockSelectionTextArea.this.getColumnAtPosition(e.getPoint());
                BlockSelectionTextArea.this.endRow = row;
                BlockSelectionTextArea.this.endCol = col;

                // Update the selection based on the rectangular block
                try {
                    BlockSelectionTextArea.this.setBlockSelection(BlockSelectionTextArea.this.startRow, BlockSelectionTextArea.this.startCol, BlockSelectionTextArea.this.endRow, BlockSelectionTextArea.this.endCol);
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Mouse motion listener for drag behavior
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Update the block selection as the mouse is dragged
                int row = BlockSelectionTextArea.this.getRowAtPosition(e.getPoint());
                int col = BlockSelectionTextArea.this.getColumnAtPosition(e.getPoint());
                BlockSelectionTextArea.this.endRow = row;
                BlockSelectionTextArea.this.endCol = col;

                // Update the selection during dragging
                try {
                    BlockSelectionTextArea.this.setBlockSelection(BlockSelectionTextArea.this.startRow, BlockSelectionTextArea.this.startCol, BlockSelectionTextArea.this.endRow, BlockSelectionTextArea.this.endCol);
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    // Converts the mouse position to the corresponding row
    private int getRowAtPosition(Point p) {
        int offset = this.viewToModel(p);
        try {
            return this.getLineOfOffset(offset);
        } catch (BadLocationException e) {
            return 0;
        }
    }

    // Converts the mouse position to the corresponding column
    private int getColumnAtPosition(Point p) {
        int offset = this.viewToModel(p);
        try {
            int row = this.getLineOfOffset(offset);
            return offset - this.getLineStartOffset(row);
        } catch (BadLocationException e) {
            return 0;
        }
    }

    // Set the block selection between start and end coordinates (row, col)
    private void setBlockSelection(int startRow, int startCol, int endRow, int endCol) throws BadLocationException {
        // Normalize the selection direction (so it always goes from top-left to bottom-right)
        if (startRow > endRow || (startRow == endRow && startCol > endCol)) {
            int tempRow = startRow;
            int tempCol = startCol;
            startRow = endRow;
            startCol = endCol;
            endRow = tempRow;
            endCol = tempCol;
        }

        // Convert row/column to character offsets
        int startOffset = this.getLineStartOffset(startRow) + startCol;
        int endOffset = this.getLineStartOffset(endRow) + endCol;

        // Adjust the selection range to reflect the rectangular block
        this.setSelectionStart(startOffset);
        this.setSelectionEnd(endOffset);
    }

    public static void main(String[] args) {
        // Create a frame with block selection text area
        JFrame frame = new JFrame("Block Selection Example");
        BlockSelectionTextArea textArea = new BlockSelectionTextArea();
        textArea.setText("This is a test.\nThis is another test.\nBlock selection here.");
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        frame.add(new JScrollPane(textArea));
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
