package gamesincommon;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.community.SteamGame;
import com.github.koraktor.steamcondenser.community.SteamId;

import static gamesincommon.Utils.getGames;
import static gamesincommon.Utils.mergeSets;

public class GamesInCommon {

    private Logger logger;
    private boolean forceWebCheck = false;
    private static final String DB_NAME = "gamedata.db";

    // Enable/disable debug mode - default value is false.
	private boolean debug = false;

	public GamesInCommon() {
		// initialise logger
		logger = Logger.getLogger(GamesInCommon.class.getName());
		logger.setLevel(Level.ALL);

        initDatabase();
	}

	public Logger getLogger() {
		return logger;
	}

    /**
     * Setup the database, adding all current filters.
     */
    private void initDatabase() {
        File dbfile = new File(DB_NAME);
        boolean newDb = !dbfile.exists();

        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);

            if (newDb) {
                logger.log(Level.INFO, "New database created.");
                createTables(connection);
            }

            addFilters(connection);
            connection.close();
        }
        catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }

        logger.log(Level.INFO, "Database initialised.");
    }

    /**
     * Create new tables for a newly created database.
     * @param connection A connection to the GIC database.
     * @throws SQLException
     */
    private void createTables(Connection connection) throws SQLException {
        PreparedStatement createStatement = connection.prepareStatement(
                "CREATE TABLE games                         (" +
                        "id    INT   NOT NULL,               " +
                        "PRIMARY KEY(id)  ON CONFLICT IGNORE)"
        );
        createStatement.executeUpdate();

        createStatement = connection.prepareStatement(
                "CREATE TABLE filters               (" +
                "id  INT  NOT NULL,                  " +
                "PRIMARY KEY(id)  ON CONFLICT IGNORE)"
        );
        createStatement.executeUpdate();

        createStatement = connection.prepareStatement(
                "CREATE TABLE gamefilters                             (" +
                "filter_id  INT  NOT NULL,                             " +
                "game_id    INT  NOT NULL,                             " +
                "PRIMARY KEY(filter_id, game_id)  ON CONFLICT IGNORE,  " +
                "FOREIGN KEY(game_id) REFERENCES games(id),            " +
                "FOREIGN KEY(filter_id) REFERENCES filters(id)        )"
        );
        createStatement.executeUpdate();

        createStatement.close();
    }

    /**
     * Add current filters to the database. If a filter exists already, it is
     * ignored.
     * @param connection A connection to the GIC database.
     * @throws SQLException
     */
    private void addFilters(Connection connection) throws SQLException {
        PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT INTO filters " +
                        "(id)        " +
                        "VALUES (?)  "
        );

        for (FilterType filter : FilterType.values()) {
            insertStatement.setInt(1, filter.ordinal());
            insertStatement.executeUpdate();
        }

        insertStatement.close();
    }

	/**
	 * Finds common games between an arbitrarily long list of users
	 * 
	 * @param users
	 *            A list of names to find common games for.
	 * @return A collection of games common to all users
	 */
    public Collection<SteamGame> findCommonGames(List<SteamId> users) {

        final List<Collection<SteamGame>> userGames = new ArrayList<>();

        final CountDownLatch latch = new CountDownLatch(users.size());
        ExecutorService taskExecutor = Executors.newCachedThreadPool();

        for (final SteamId name : users) {
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        userGames.add(getGames(name));
                        logger.log(Level.INFO, "Added user " + name.getNickname() + " (" + name.getSteamId64() + ").");
                    } catch (SteamCondenserException e) {
                        logger.log(Level.SEVERE, e.getMessage());
                    }
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.log(Level.INFO, "Cancelled.");
            taskExecutor.shutdownNow();
            return null;
        }

        Collection<SteamGame> commonGames = mergeSets(userGames);
        logger.log(Level.INFO, "Search complete.");

        return commonGames;
    }

	/**
	 * Returns games that match one or more of the given filter types
	 * 
	 * @param gameList
	 *            Collection of games to be filtered.
	 * @param filterList
	 *            Collection of filters to apply.
	 * @return A collection of games matching one or more of filters.
	 */
    public Collection<SteamGame> filterGames(Collection<SteamGame> gameList, final List<FilterType> filterList) {

        final Collection<SteamGame> result = new HashSet<>();
        final CountDownLatch latch = new CountDownLatch(gameList.size());
        final ExecutorService taskExecutor = Executors.newCachedThreadPool();

        final Connection connection;
        final PreparedStatement gameSelectStatement;
        final PreparedStatement filterSelectStatement;
        final PreparedStatement insertGameStatement;
        final PreparedStatement insertGameFilterStatement;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);

            gameSelectStatement = connection.prepareStatement(
                    "SELECT g.id FROM games g " +
                    "WHERE g.id = ?"
            );

            filterSelectStatement = connection.prepareStatement(
                    "SELECT f.id FROM gamefilters gf " +
                    "JOIN games g ON gf.game_id = g.id     " +
                    "JOIN filters f ON gf.filter_id = f.id " +
                    "WHERE g.id = ?                      "
            );
            insertGameStatement = connection.prepareStatement(
                    "INSERT OR REPLACE INTO games " +
                    "(id) " +
                    "VALUES (?)"
            );
            insertGameFilterStatement = connection.prepareStatement(
                    "INSERT INTO gamefilters " +
                    "(game_id, filter_id) " +
                    "VALUES (?, ?)"
            );
        }
        catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }

        // start going through the games
        for (final SteamGame game : gameList) {

            taskExecutor.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        filterGame();
                    } catch (SQLException e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        latch.countDown();
                        return;
                    } catch (InterruptedException e) {
                        // This indicates that we've been cancelled .
                        return;
                    }

                    latch.countDown();
                }

                private void filterGame() throws SQLException, InterruptedException {

                    List<Integer> gameFilters = new ArrayList<>();
                    boolean gameExists;

                    synchronized (taskExecutor) {
                        gameSelectStatement.setInt(1, game.getAppId());
                        ResultSet gamesSet = gameSelectStatement.executeQuery();
                        gameExists = gamesSet.isBeforeFirst();

                        filterSelectStatement.setInt(1, game.getAppId());
                        ResultSet gameFiltersSet = filterSelectStatement.executeQuery();
                        while (gameFiltersSet.next()) {
                            gameFilters.add(gameFiltersSet.getInt(1));
                        }
                    }

                    // foundProperties records filters found for the game
                    Set<FilterType> foundProperties = new HashSet<>();

                    if ((!gameExists && gameFilters.isEmpty()) || forceWebCheck) {
                        // Retrieve data from web and store in database.

                        InputStream gameConnectionStream;
                        try {
                            URL gameURL = new URL("http://store.steampowered.com/app/" + game.getAppId());
                            URLConnection gameConnection = gameURL.openConnection();
                            gameConnectionStream = gameConnection.getInputStream();
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, e.getMessage(), e);
                            return;
                        }

                        try (BufferedReader br = new BufferedReader(new InputStreamReader(gameConnectionStream))) {
                            // Read lines in until there are no more to be read, run filter on each line looking for specified package IDs.
                            String line;
                            while ((line = br.readLine()) != null) {

                                if (Thread.currentThread().isInterrupted()) {
                                    throw new InterruptedException();
                                }

                                for (FilterType filter : FilterType.values()) {
                                    if (line.contains("\"" + filter.getValue() + "\"")) {
                                        foundProperties.add(filter);
                                    }
                                }
                            }

                            if (debug) {
                                for (FilterType filter : filterList) {
                                    logger.log(Level.FINEST, "[WEB] Game " + game.getName() + ": \"" + filter.toString() + "\" = "
                                            + foundProperties.contains(filter) + ".");
                                }
                            }

                            synchronized (taskExecutor) {
                                insertGameStatement.setInt(1, game.getAppId());
                                int inserted = insertGameStatement.executeUpdate();

                                if (debug) {
                                    if (inserted > 0) {
                                        logger.log(Level.FINEST, "[LOCAL] Inserted into database game: " + game.getName());
                                    } else {
                                        logger.log(Level.WARNING, "[LOCAL] Unable to insert into database: " + game.getName());
                                    }
                                }

                                insertGameFilterStatement.setInt(1, game.getAppId());

                                for (FilterType filter : foundProperties) {
                                    insertGameFilterStatement.setInt(2, filter.ordinal());
                                    insertGameFilterStatement.executeUpdate();
                                }
                            }

                            logger.log(Level.INFO, "[WEB] Checked game '" + game.getName() + "'");
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, e.getMessage(), e);
                        }
                    } else {
                        // Data is already in database.
                        for (int filter : gameFilters) {
                            foundProperties.add(FilterType.values()[filter]);
                        }
                        logger.log(Level.INFO, "[LOCAL] Checked game '" + game.getName() + "'");
                    }

                    if (foundProperties.containsAll(filterList)) {
                        result.add(game);
                    }
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.log(Level.INFO, "Cancelled");
            taskExecutor.shutdownNow();
            return null;
        }

        return result;
    }

    /**
     * Enable or disable forced web checking.
     * @param required Whether to enable or disable force web checking.
     */
    public void requireWebCheck(boolean required) {
        forceWebCheck = required;
    }

}
