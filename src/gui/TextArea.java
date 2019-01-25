package gui;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TextArea extends JTextPane {

	private static final long serialVersionUID = 1L;

	public TextArea() {

	}

	public void appendText(String msg, Color color) {
		try {
			StyledDocument doc = this.getStyledDocument();
			Style style = this.addStyle("style", null);
			StyleConstants.setForeground(style, color);
			doc.insertString(doc.getLength(), msg, style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
