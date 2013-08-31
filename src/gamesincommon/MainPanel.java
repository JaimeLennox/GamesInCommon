package gamesincommon;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.github.koraktor.steamcondenser.steam.community.SteamGame;

public class MainPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	GamesInCommon parent;

	JPanel inputPanel;
	PlayerPanel playerPanel;
	FilterPanel filterPanel;

	JPanel resultPanel;

	JButton startButton;

	public MainPanel(GamesInCommon parent) {
		this.parent = parent;

		inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(0, 2));
		playerPanel = new PlayerPanel();
		inputPanel.add(playerPanel);
		filterPanel = new FilterPanel();
		inputPanel.add(filterPanel);

		resultPanel = new JPanel();
		resultPanel.setLayout(new GridLayout(0, 1));
		startButton = new JButton("Scan for games in common");
		startButton.addActionListener(this);
		resultPanel.add(startButton);

		this.setLayout(new GridLayout(0, 1));
		this.add(inputPanel);
		this.add(resultPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// find common games
		Collection<SteamGame> commonGames = parent.findCommonGames(playerPanel.getPlayerNames());
		// apply filters, if applicable
		List<FilterType> filters = filterPanel.getFilters();
		if (!filters.isEmpty()) {
			commonGames = parent.filterGames(commonGames, filters);
		}
		// display filtered list of common games
		parent.showCommonGames(commonGames);

	}
}
