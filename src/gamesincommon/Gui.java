package gamesincommon;

import java.awt.Desktop;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.SteamGame;
import com.github.koraktor.steamcondenser.steam.community.SteamId;

public class Gui {

	final static Font font = new Font("Tahoma", Font.PLAIN, 18);

	private JFrame gamesInCommonFrame;

	private JList<SteamGameWrapper> outputList;
  private JList<SteamId> playerList;

	private DefaultListModel<SteamGameWrapper> outputListModel;
	private DefaultListModel<SteamId> playerListModel;

	private ArrayList<SteamId> playerIdList;

	private JTextField addPlayerText;
	private FilterPanel filterPanel;

	private GamesInCommon gamesInCommon;

	private Logger logger;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.initialize();
					window.gamesInCommonFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// create GamesInCommon
		gamesInCommon = new GamesInCommon();
		// logger initialisation
		logger = gamesInCommon.getLogger();
		// remove handlers
		removeHandlers(logger.getParent());
		removeHandlers(logger);
		// console logging
		ConsoleHandler cHandler = new ConsoleHandler();
		cHandler.setLevel(Level.FINEST);
		// logging to GUI
		TextPaneHandler tpHandler = new TextPaneHandler();
		tpHandler.setLevel(Level.INFO);
		// attach handlers
		logger.addHandler(cHandler);
		logger.addHandler(tpHandler);
		// set formats of handlers to be unified
		LogFormatter formatter = new LogFormatter();
		Handler[] handlers = logger.getHandlers();
		for (Handler handler : handlers) {
			handler.setFormatter(formatter);
		}

		gamesInCommonFrame = new JFrame();
		gamesInCommonFrame.setTitle("Games in Common");
		gamesInCommonFrame.setBounds(100, 100, 900, 400);
		gamesInCommonFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JPanel playerPanel = new JPanel();
		playerPanel.setBorder(new TitledBorder(null, "Players", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel consolePanel = new JPanel();
		consolePanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));

		JPanel scanPanel = new JPanel();
		scanPanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel outputPanel = new JPanel();
		outputPanel.setBorder(new TitledBorder(null, "Output", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout groupLayout = new GroupLayout(gamesInCommonFrame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.TRAILING)
										.addComponent(consolePanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 858, Short.MAX_VALUE)
										.addGroup(
												groupLayout
														.createSequentialGroup()
														.addGroup(
																groupLayout
																		.createParallelGroup(Alignment.TRAILING, false)
																		.addGroup(
																				groupLayout
																						.createSequentialGroup()
																						.addComponent(optionsPanel,
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																						.addPreferredGap(ComponentPlacement.UNRELATED)
																						.addComponent(scanPanel,
																								GroupLayout.PREFERRED_SIZE, 134,
																								GroupLayout.PREFERRED_SIZE))
																		.addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, 430,
																				GroupLayout.PREFERRED_SIZE))
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(outputPanel, GroupLayout.PREFERRED_SIZE, 421,
																GroupLayout.PREFERRED_SIZE))).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(
				groupLayout
						.createSequentialGroup()
						.addGap(15)
						.addGroup(
								groupLayout
										.createParallelGroup(Alignment.LEADING)
										.addGroup(
												groupLayout
														.createSequentialGroup()
														.addComponent(playerPanel, GroupLayout.PREFERRED_SIZE, 148,
																GroupLayout.PREFERRED_SIZE)
														.addGap(18)
														.addGroup(
																groupLayout
																		.createParallelGroup(Alignment.TRAILING)
																		.addComponent(scanPanel, GroupLayout.PREFERRED_SIZE, 75,
																				GroupLayout.PREFERRED_SIZE)
																		.addComponent(optionsPanel, GroupLayout.PREFERRED_SIZE, 83,
																				GroupLayout.PREFERRED_SIZE)))
										.addComponent(outputPanel, GroupLayout.PREFERRED_SIZE, 249, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(consolePanel, GroupLayout.PREFERRED_SIZE, 27, Short.MAX_VALUE).addContainerGap()));
		outputPanel.setLayout(new GridLayout(1, 0, 0, 0));

		JScrollPane outputScrollPane = new JScrollPane();
		outputPanel.add(outputScrollPane);

		outputListModel = new DefaultListModel<SteamGameWrapper>();
		outputList = new JList<SteamGameWrapper>(outputListModel);
		outputList.setFont(new Font("Tahoma", Font.PLAIN, 18));
		outputScrollPane.setViewportView(outputList);

		// launches the selected steam game on double click
		outputList.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					SteamGame launchGame = outputList.getSelectedValue().getGame();
					logger.log(Level.INFO, "Launching " + launchGame.getName() + " (" + launchGame.getAppId() + ")");
					try {
						Desktop.getDesktop().browse(new URI("steam://run/" + launchGame.getAppId()));
					} catch (IOException | URISyntaxException e1) {
						logger.log(Level.SEVERE, e1.getMessage());
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});

		JScrollPane consoleScrollPane = new JScrollPane();

		GroupLayout gl_consolePanel = new GroupLayout(consolePanel);
		gl_consolePanel.setHorizontalGroup(gl_consolePanel.createParallelGroup(Alignment.LEADING).addComponent(consoleScrollPane,
				GroupLayout.DEFAULT_SIZE, 852, Short.MAX_VALUE));
		gl_consolePanel.setVerticalGroup(gl_consolePanel.createParallelGroup(Alignment.LEADING).addComponent(consoleScrollPane,
				GroupLayout.PREFERRED_SIZE, 21, Short.MAX_VALUE));

		JTextPane consoleText = tpHandler.getTextPane();
		consoleScrollPane.setViewportView(consoleText);
		consoleText.setFont(new Font("Tahoma", Font.PLAIN, 18));

		consolePanel.setLayout(gl_consolePanel);

		filterPanel = new FilterPanel();
		GroupLayout gl_optionsPanel = new GroupLayout(optionsPanel);
		gl_optionsPanel.setHorizontalGroup(gl_optionsPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_optionsPanel.createSequentialGroup().addContainerGap()
						.addComponent(filterPanel, GroupLayout.PREFERRED_SIZE, 205, Short.MAX_VALUE).addContainerGap()));
		gl_optionsPanel.setVerticalGroup(gl_optionsPanel.createParallelGroup(Alignment.LEADING).addGroup(
				gl_optionsPanel.createSequentialGroup().addContainerGap()
						.addComponent(filterPanel, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE).addContainerGap()));
		optionsPanel.setLayout(gl_optionsPanel);

		JPanel playerButtonPanel = new JPanel();
		playerButtonPanel.setLayout(new GridLayout(1, 2, 0, 0));

		JButton scanButton = new JButton("Scan");
		scanButton.setFont(new Font("Tahoma", Font.PLAIN, 18));

		scanButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread scanThread = new Thread() {
					public void run() {
						scan();
					}
				};
				scanThread.start();
			}
		});
		scanPanel.setLayout(new GridLayout(0, 1, 0, 0));

		scanPanel.add(scanButton);

		JButton addButton = new JButton("Add");
		addButton.setFont(new Font("Tahoma", Font.PLAIN, 18));

		addButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				addName();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

		});

		playerButtonPanel.add(addButton);

		JButton removeButton = new JButton("Remove");
		removeButton.setFont(new Font("Tahoma", Font.PLAIN, 18));

		removeButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				removeName();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

		});

		playerButtonPanel.add(removeButton);

		addPlayerText = new JTextField();
		addPlayerText.setColumns(10);

		addPlayerText.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {

				addPlayerText.setText("");
				addPlayerText.setFont(new Font("Tahoma", Font.PLAIN, 20));

			}

			@Override
			public void focusLost(FocusEvent e) {

				if (addPlayerText.getText().equals("")) {
					addPlayerText.setText("Enter player name...");
					addPlayerText.setFont(new Font("Tahoma", Font.ITALIC, 20));
				}

			}

		});

		addPlayerText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

				char c = e.getKeyChar();

				if (c == KeyEvent.VK_ENTER) {
					addName();
				}

			}

		});

		JScrollPane playerListScrollPane = new JScrollPane();
		GroupLayout gl_playerPanel = new GroupLayout(playerPanel);
		gl_playerPanel.setHorizontalGroup(
		  gl_playerPanel.createParallelGroup(Alignment.LEADING)
		    .addGroup(gl_playerPanel.createSequentialGroup()
		      .addContainerGap()
		      .addGroup(gl_playerPanel.createParallelGroup(Alignment.LEADING, false)
		        .addComponent(playerButtonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		        .addComponent(addPlayerText, GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))
		      .addPreferredGap(ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
		      .addComponent(playerListScrollPane, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
		      .addContainerGap())
		);
		gl_playerPanel.setVerticalGroup(
		  gl_playerPanel.createParallelGroup(Alignment.LEADING)
		    .addGroup(gl_playerPanel.createSequentialGroup()
		      .addContainerGap()
		      .addGroup(gl_playerPanel.createParallelGroup(Alignment.LEADING)
		        .addComponent(playerListScrollPane, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
		        .addGroup(gl_playerPanel.createSequentialGroup()
		          .addComponent(addPlayerText, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
		          .addPreferredGap(ComponentPlacement.UNRELATED)
		          .addComponent(playerButtonPanel, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)))
		      .addContainerGap())
		);

		// initialise data model for playerList, which tells the JList about the data it expects
		playerListModel = new DefaultListModel<SteamId>();
		// initialise ArrayList for player list
		playerIdList = new ArrayList<SteamId>(10);
		// initialise JList for player list display
		playerList = new JList<SteamId>(playerListModel);
		playerList.setFont(new Font("Tahoma", Font.PLAIN, 14));
		playerList.setCellRenderer(new PlayerListRenderer());
		playerList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		playerList.setLayoutOrientation(JList.VERTICAL);
		playerList.setVisibleRowCount(-1);
		playerListScrollPane.setViewportView(playerList);

		// messages the selected user on double click
		playerList.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					SteamId playerId = playerIdList.get(playerList.getSelectedIndex());
					//logger.log(Level.INFO, "Messaging " + playerId.getNickname() + " (" + playerId.getSteamId64() + ")");
					try {
						Desktop.getDesktop().browse(new URI("steam://friends/message/" + playerId.getSteamId64()));
					} catch (IOException | URISyntaxException e1) {
						logger.log(Level.SEVERE, e1.getMessage());
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}
		});

		playerPanel.setLayout(gl_playerPanel);
		gamesInCommonFrame.getContentPane().setLayout(groupLayout);
	}
	
	class PlayerListRenderer extends JLabel implements ListCellRenderer<SteamId> {
	  
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(
        JList<? extends SteamId> list, SteamId value, int index,
        boolean isSelected, boolean cellHasFocus) {
      setText(value.getNickname());
      try {
        ImageIcon icon = new ImageIcon(new URL(value.getAvatarIconUrl())); 
        setIcon(icon);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
      
      return this;
    }


	}

	/**
	 * Removes all handlers for the given logger
	 * 
	 * @param logger
	 *            to clear handlers for
	 */
	private void removeHandlers(Logger loggerToClear) {
		Handler[] handlers = loggerToClear.getHandlers();
		for (Handler handler : handlers) {
			loggerToClear.removeHandler(handler);
		}
	}

	/**
	 * Displays all games from a collection in a JList format.
	 * 
	 * @param games
	 *            Games to show.
	 */
	public void showCommonGames(final Collection<SteamGame> games) {

		if (games == null)
			return;
		List<SteamGameWrapper> gameList = new ArrayList<SteamGameWrapper>();

		for (SteamGame steamGame : games) {
			gameList.add(new SteamGameWrapper(steamGame));
		}

		Collections.sort(gameList);

		// Final count.
		logger.log(Level.INFO, "Total games in common: " + games.size());

		for (final SteamGameWrapper str : gameList) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					outputListModel.addElement(str);
				}
			});

		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				outputList.setSelectedIndex(0);
			}
		});

	}

	/**
	 * Get a list of SteamIds parallel to the playerIDList
	 * 
	 * @return A list of SteamIds in the same order as playerList
	 */
	private List<SteamId> getPlayerIDs() {
		return playerIdList;
	}

	/**
	 * Verifies and retrieves information on the user indicated by addPlayerText, then adds them to playerList.
	 */
	private void addName() {
		// If text field not empty then add player to list.
		// verify with Steam that the entry is a valid Steam ID and throw an error if not
		String name = addPlayerText.getText();
		if ((!name.isEmpty()) && (!name.equals("Enter player name..."))) {
			try {
				SteamId temp = gamesInCommon.checkSteamId(name);
				// add to the list of SteamIds
				playerIdList.add(temp);
				// then add to the JList
				playerListModel.addElement(temp);
			} catch (SteamCondenserException e) {
				logger.log(Level.SEVERE, "\"" + name + "\": " + e.getMessage());
			}

			// Afterwards clear the text field.
			addPlayerText.setText("");
		}
	}

	/**
	 * Removes the currently selected player from playerList. Has no effect if nothing selected.
	 */
	private void removeName() {
		if (playerList.getSelectedIndex() > -1) {
			// remove the selected player from the SteamId list
			playerIdList.remove(playerList.getSelectedIndex());
			// and also from the JList
			playerListModel.removeElement(playerList.getSelectedValue());
		}
	}

	/**
	 * Finds common games within the users of playerList, then applies any selected filters and displays the result
	 */
	private void scan() {
		logger.log(Level.INFO, "Starting scan.");

		outputListModel.clear();

		// Find common games.
		Collection<SteamGame> commonGames = gamesInCommon.findCommonGames(getPlayerIDs());

		// Apply filters, if applicable.
		List<FilterType> filters = filterPanel.getFilters();
		if (!filters.isEmpty()) {
			logger.log(Level.INFO, "Filtering games... ");
			commonGames = gamesInCommon.filterGames(commonGames, filters);
			logger.log(Level.INFO, "Filtering complete.");
		}

		// Display filtered list of common games.
		showCommonGames(commonGames);
	}
}
