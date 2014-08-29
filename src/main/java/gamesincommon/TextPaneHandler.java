package gamesincommon;

import java.awt.Color;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class TextPaneHandler extends Handler {

	private JTextPane textPane;

	private SimpleAttributeSet normalAttributes;
	private SimpleAttributeSet errorAttributes;

	public TextPaneHandler(int fontSize) {
		super();
		textPane = new JTextPane();
		textPane.setEditable(false);
		DefaultCaret caret = (DefaultCaret) textPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		normalAttributes = new SimpleAttributeSet();
		StyleConstants.setFontSize(normalAttributes, fontSize);

		errorAttributes = new SimpleAttributeSet();
		StyleConstants.setFontSize(errorAttributes, fontSize);
		StyleConstants.setForeground(errorAttributes, Color.RED);
	}

	@Override
	public void publish(final LogRecord record) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// add new line
				Document tempDoc = textPane.getDocument();
				int offset = tempDoc.getLength();
				try {
					// if the record is Level.SEVERE then print in RED
					if (record.getLevel().equals(Level.SEVERE)) {
						tempDoc.insertString(offset, getFormatter().format(record), errorAttributes);
					} else {
						tempDoc.insertString(offset, getFormatter().format(record), normalAttributes);
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Access the internal JTextPane
	 * @return The textpane used to show logger records
	 */
	public JTextPane getTextPane() {
		return this.textPane;
	}

	@Override
	public void close() throws SecurityException {
		// no code required here, as no significant memory/file/network assets are held during runtime
	}

	@Override
	public void flush() {
		// no buffering takes place (input is appended straight to textPane thus no action is needed during flush)
	}

}
