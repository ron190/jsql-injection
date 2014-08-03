package com.jsql.view;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import com.jsql.view.console.DefaultConsoleAdapter;
import com.jsql.view.console.JColoredConsole;
import com.jsql.view.console.JavaConsoleAdapter;

public class SwingAppender extends WriterAppender {
	private static JColoredConsole consoleColored;
	private static JavaConsoleAdapter javaConsole;

	static SimpleAttributeSet ERROR = new SimpleAttributeSet();
	static SimpleAttributeSet INFO = new SimpleAttributeSet();
	static SimpleAttributeSet ALL = new SimpleAttributeSet();
	static SimpleAttributeSet FATAL = new SimpleAttributeSet();
	static SimpleAttributeSet WARN = new SimpleAttributeSet();
	static SimpleAttributeSet DEBUG = new SimpleAttributeSet();

	// Best to reuse attribute sets as much as possible.
	static {
		StyleConstants.setForeground(ALL, Color.green);
		StyleConstants.setForeground(FATAL, Color.red);
		//		StyleConstants.setItalic(ERROR, true);

		StyleConstants.setForeground(ERROR, Color.red);
		//		StyleConstants.setBold(ERROR, true);

//		StyleConstants.setForeground(WARN, Color.yellow);
		StyleConstants.setForeground(WARN, Color.red);

		//		StyleConstants.setForeground(INFO, Color.blue);
		StyleConstants.setForeground(DEBUG, Color.green);
		StyleConstants.setItalic(DEBUG, true);
	}

	public SwingAppender() {
		this.setLayout(new PatternLayout("[%-5p] (%F:%L) - %m%n"));
	}

	/**
	 * Method from Log4j AppenderSkeleton that gets a call for all Log4J events.
	 *
	 * @param event A logging event.
	 * @see org.apache.log4j.AppenderSkeleton
	 */
	public void append(final LoggingEvent event) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				insertText(layout.format(event), event.getLevel(), event
						.getThrowableInformation());
			}});
	}

	/**
	 * Requires a layout.
	 *
	 * @return true.
	 */
	public boolean requiresLayout() {
		return true;
	}

	/**
	 * This method overrides the parent {@link WriterAppender#closeWriter}
	 * implementation to do nothing because the console stream is not ours to
	 * close.
	 */
	protected final void closeWriter() {
	}

	/**
	 * Colorizes the specified message for the specified log4j level.
	 */
	private void insertText(String message, Level level, ThrowableInformation ti) {

		switch (level.toInt()) {
		case Level.ALL_INT:
			break;
		case Level.FATAL_INT:
			break;
		case Level.ERROR_INT:
			String s[] = ti.getThrowableStrRep();
			for (int i = 0; i < s.length; i++) {
				javaConsole.append(s[i], ERROR);
			}
			break;
		case Level.WARN_INT:
			consoleColored.append(message, WARN);
			consoleColored.setCaretPosition(consoleColored.getDocument().getLength());
			break;
		case Level.INFO_INT:
			consoleColored.append(message, INFO);
			consoleColored.setCaretPosition(consoleColored.getDocument().getLength());
			break;
		case Level.DEBUG_INT:
			//	      insertText(message, DEBUG);
			break;
		}
	}

	public void register(JavaConsoleAdapter javaConsole) {
		SwingAppender.javaConsole = javaConsole;
	}

	public void register(DefaultConsoleAdapter consoleColored) {
		SwingAppender.consoleColored = consoleColored;
	}
}