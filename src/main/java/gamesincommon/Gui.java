package gamesincommon;

import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.community.SteamGame;
import com.github.koraktor.steamcondenser.community.SteamId;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Gui {

	final static Font font = new Font("Tahoma", Font.PLAIN, 18);

	private JFrame gamesInCommonFrame;

	private JPanel consolePanel;
	private JPanel optionsPanel;
	private JPanel outputPanel;
	private JPanel playerPanel;
	private JPanel scanPanel;
	private FilterPanel filterPanel;

	private JScrollPane consoleScrollPane;
	private JScrollPane outputScrollPane;
	private JScrollPane playerListScrollPane;

	private JButton addButton;
	private JButton removeButton;
    private JButton searchButton;
	private JButton scanButton;
	private JButton cancelButton;

    private JMenuBar menuBar;
    private JMenu menu;
    private JCheckBoxMenuItem menuWebCheck;
    private JMenuItem menuExit;

	private JTextPane consoleText;
	private JTextField addPlayerText;

	private JList<SteamGameWrapper> outputList;
	private JList<SteamId> playerList;

	private DefaultListModel<SteamGameWrapper> outputListModel;
	private DefaultListModel<SteamId> playerListModel;

	private ArrayList<SteamId> playerIdList;

	private GamesInCommon gamesInCommon;

	private Logger logger;

	private Scanner<Void, Void> scanner;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
            public void run() {
				new Gui();
            }
        });
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
		gamesInCommonFrame.setVisible(true);
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
		TextPaneHandler tpHandler = new TextPaneHandler(font.getSize());
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
		gamesInCommonFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		playerPanel = new JPanel();
		playerPanel.setBorder(BorderFactory.createTitledBorder("Players"));

		optionsPanel = new JPanel();
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));

		consolePanel = new JPanel();
		consolePanel.setBorder(BorderFactory.createTitledBorder("Console"));

		scanPanel = new JPanel();
		scanPanel.setBorder(BorderFactory.createTitledBorder("Scan"));

		outputPanel = new JPanel();
		outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));

		outputScrollPane = new JScrollPane();
		outputScrollPane.setMinimumSize(new Dimension(450, outputScrollPane.getMinimumSize().height));
		outputListModel = new DefaultListModel<SteamGameWrapper>();
		outputList = new JList<SteamGameWrapper>(outputListModel);
		outputList.setFont(font);
		outputScrollPane.setViewportView(outputList);

		// launches the selected steam game on double click
		outputList.addMouseListener(new MouseAdapter() {
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
		});

		consoleScrollPane = new JScrollPane();

		consoleText = tpHandler.getTextPane();
		consoleScrollPane.setViewportView(consoleText);
		consoleScrollPane.setMinimumSize(new Dimension(consoleScrollPane.getMinimumSize().width, 100));

		filterPanel = new FilterPanel();

		scanButton = new JButton("Scan");
		scanButton.setFont(font);

		scanButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((CardLayout) scanPanel.getLayout()).last(scanPanel);
				scanner = new Scanner<Void, Void>();
				scanner.execute();
			}
		});

		cancelButton = new JButton("Cancel");
		cancelButton.setFont(font);

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scanner.cancel(true);
			}
		});

		addButton = new JButton("Add");
		addButton.setFont(font);

		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				addName();
			}
		});

		removeButton = new JButton("Remove");
		removeButton.setFont(font);

		removeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				removeName();
			}
		});

        searchButton = new JButton("Search");
        searchButton.setFont(font);

        searchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchName();
            }
        });

        menuBar = new JMenuBar();
        menu = new JMenu("Menu");

        menuWebCheck = new JCheckBoxMenuItem("Force web check");
        menuWebCheck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    gamesInCommon.requireWebCheck(true);
                }
                else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    gamesInCommon.requireWebCheck(false);
                }
            }
        });

        menuExit = new JMenuItem("Exit");
        menuExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menu.add(menuWebCheck);
        menu.add(menuExit);
        menuBar.add(menu)
;
		addPlayerText = new JTextField();
		addPlayerText.setColumns(10);
		addPlayerText.setFont(font);

		addPlayerText.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
				if (addPlayerText.getText().equals("Enter player name...")) {
					addPlayerText.setText("");
				}
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (addPlayerText.getText().equals("")) {
                    addPlayerText.setText("Enter player name...");
                }
            }
        });

		addPlayerText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				char c = e.getKeyChar();
				if (c == KeyEvent.VK_ENTER) {
					addName();
				}
			}
		});

		playerListScrollPane = new JScrollPane();
		playerListScrollPane.setMinimumSize(new Dimension(playerListScrollPane.getMinimumSize().width, 105));

		// initialise data model for playerList, which tells the JList about the data it expects
		playerListModel = new DefaultListModel<SteamId>();
		// initialise ArrayList for player list
		playerIdList = new ArrayList<SteamId>(10);
		// initialise JList for player list display
		playerList = new JList<SteamId>(playerListModel);
		playerList.setFont(font);
		playerList.setCellRenderer(new PlayerListRenderer());
		playerList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		playerList.setLayoutOrientation(JList.VERTICAL);
		playerList.setVisibleRowCount(-1);
		playerListScrollPane.setViewportView(playerList);

		// messages the selected user on double click
		playerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    SteamId playerId = playerIdList.get(playerList.getSelectedIndex());
                    // logger.log(Level.INFO, "Messaging " + playerId.getNickname() + " (" + playerId.getSteamId64() + ")");
                    try {
                        Desktop.getDesktop().browse(new URI("steam://friends/message/" + playerId.getSteamId64()));
                    } catch (IOException | URISyntaxException e1) {
                        logger.log(Level.SEVERE, e1.getMessage());
                    }
                }
            }
        });

		playerPanel.setLayout(new MigLayout("", "grow", "grow"));
		playerPanel.add(addPlayerText, "cell 0 0, grow");
		playerPanel.add(playerListScrollPane, "cell 1 0, span 0 2, grow");
		playerPanel.add(addButton, "cell 0 1, split 3, grow");
        playerPanel.add(searchButton, "grow");
		playerPanel.add(removeButton, "grow");

		outputPanel.setLayout(new MigLayout("", "grow", "grow"));
		outputPanel.add(outputScrollPane, "grow");

		optionsPanel.setLayout(new MigLayout("", "grow", "grow"));
		optionsPanel.add(filterPanel, "grow");

		scanPanel.setLayout(new CardLayout(10, 10));
		scanPanel.add(scanButton);
		scanPanel.add(cancelButton);

		consolePanel.setLayout(new MigLayout("", "grow", "grow"));
		consolePanel.add(consoleScrollPane, "grow");

		gamesInCommonFrame.getContentPane().setLayout(new MigLayout("", "grow", "grow"));
		gamesInCommonFrame.getContentPane().add(playerPanel, "grow");
		gamesInCommonFrame.getContentPane().add(outputPanel, "grow, wrap, span 0 2");
		gamesInCommonFrame.getContentPane().add(optionsPanel, "grow, split 2");
		gamesInCommonFrame.getContentPane().add(scanPanel, "grow");
		gamesInCommonFrame.getContentPane().add(consolePanel, "south");
        gamesInCommonFrame.setJMenuBar(menuBar);

		gamesInCommonFrame.pack();

	}

	class Scanner<T, V> extends SwingWorker<T, V> {
		@Override
		protected T doInBackground() throws Exception {
			scan();
			return null;
		}

		@Override
		protected void done() {
			((CardLayout) scanPanel.getLayout()).first(scanPanel);
		}
	}

	/**
	 * Removes all handlers for the given logger
	 *
	 * @param loggerToClear logger to clear handlers for
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

		if (games == null) {
			return;
		}

		List<SteamGameWrapper> gameList = new ArrayList<SteamGameWrapper>();

		for (SteamGame steamGame : games) {
			gameList.add(new SteamGameWrapper(steamGame));
		}

		Collections.sort(gameList);

		// Final count.
		logger.log(Level.INFO, "Total games in common: " + games.size());

		for (final SteamGameWrapper str : gameList) {
			outputListModel.addElement(str);
		}

		outputList.setSelectedIndex(0);

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
			// Strip URL character sequences
			name = name.replace("http://steamcommunity.com/profiles/", "");
			name = name.replace("/", "");
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
     * Searches steam for the given name. This works with nicknames.
     */
    private void searchName() {
        Set<SteamId> searchResults = new HashSet<SteamId>();

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet sessionIdRequest = new HttpGet("http://steamcommunity.com");

        String sessionId = "";

        try {
            HttpResponse sessionIdResponse = httpClient.execute(sessionIdRequest);
            BufferedReader br = new BufferedReader(new InputStreamReader(sessionIdResponse.getEntity().getContent()));

            String line;
            while ((line = br.readLine()) != null) {
                // Retrieve session id through really dodgy string methods because I'm bad at Java
                String[] singleLine = line.split("\"");
                if (singleLine[0].contains("g_sessionID")) {
                    sessionId = singleLine[1];
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpGet searchRequest = new HttpGet("http://steamcommunity.com/search/SearchCommunityAjax?text="
                + addPlayerText.getText() + "&filter=users&sessionid=" + sessionId + "&steamid_user=false&page=1");

        try {
            HttpResponse response = httpClient.execute(searchRequest);

            JSONObject searchJson = new JSONObject(new JSONTokener(response.getEntity().getContent()));

            if (searchJson.getInt("success") == 1) {
                Document doc = Jsoup.parse(searchJson.getString("html"));

                Elements names = doc.getElementsByClass("searchPersonaName");
                for (Element name : names) {
                    // ID is the last element of the href.
                    String playerUrl = name.attr("href");
                    String[] urlSplit = playerUrl.split("/");
                    SteamId id = gamesInCommon.checkSteamId(urlSplit[urlSplit.length - 1]);

                    if (id != null) {
                        String logId = id.getCustomUrl();
                        if (logId == null) logId = id.getBaseUrl();
                        logger.log(Level.FINEST, "Found potential search match: " + logId);
                        searchResults.add(id);
                    }
                }
            }

        } catch (IOException | SteamCondenserException e) {
            e.printStackTrace();
        }

        popupWindow(searchResults);
    }

    private void popupWindow(Set<SteamId> searchResults) {
        final JDialog popup = new JDialog(gamesInCommonFrame);
        DefaultListModel<SteamId> popupPlayerModel = new DefaultListModel<SteamId>();
        JList<SteamId> popupPlayers = new JList<SteamId>();

        popupPlayers.setCellRenderer(new PlayerListRenderer());
        popupPlayers.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    @SuppressWarnings("unchecked")
                    final List<SteamId> users = ((JList<SteamId>) e.getSource()).getSelectedValuesList();
                    if (!users.isEmpty()) {
                        addPlayerText.setText(Long.toString(users.get(0).getSteamId64()));
                        popup.dispose();
                        addName();
                    }
                }
            }
        });

        popupPlayers.setModel(popupPlayerModel);
        popup.add(popupPlayers);

        for (SteamId id : searchResults) {
            popupPlayerModel.addElement(id);
        }

        popup.setVisible(true);
        popup.pack();
        popup.setLocationRelativeTo(null);
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
