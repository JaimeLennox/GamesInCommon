package gamesincommon;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PlayerPanel extends JPanel implements KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField inputField;
	JTextArea playerTextArea;

	public PlayerPanel() {
		this.setLayout(new GridLayout(0, 1));
		// descriptive label
		this.add(new JLabel("Players (Enter player name(s) and press ENTER)"));
		// text entry area
		inputField = new JTextField();
		this.add(inputField);
		// area to display players to be checked
		playerTextArea = new JTextArea();
		playerTextArea.setEditable(false);
		this.add(playerTextArea);
		// attach key listener
		inputField.addKeyListener(this);
	}

	public List<String> getPlayerNames() {
		return new ArrayList<String>(Arrays.asList(playerTextArea.getText().split("\\n")));
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		char c = e.getKeyChar();
		if (c == KeyEvent.VK_ENTER) {
			// if enter key pressed and text field not empty then add player to list
			if (!inputField.getText().isEmpty()) {
				playerTextArea.append(inputField.getText() + "\n");
				// afterwards clear the text field
				inputField.setText("");
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
