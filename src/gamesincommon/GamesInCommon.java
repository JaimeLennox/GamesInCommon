package gamesincommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.SteamGame;
import com.github.koraktor.steamcondenser.steam.community.SteamId;

public class GamesInCommon {

	private Logger logger;

	public GamesInCommon() {
		// initialise logger
		logger = Logger.getLogger(GamesInCommon.class.getName());
		logger.setLevel(Level.ALL);
		// initialise database connector
	}

	public Logger getLogger() {
		return logger;
	}

	/**
	 * Finds common games between an arbitrarily long list of users
	 * 
	 * @param users
	 *            A list of names to find common games for.
	 * @return A collection of games common to all users
	 */
	public Collection<SteamGame> findCommonGames(List<SteamId> users) {

		List<Collection<SteamGame>> userGames = new ArrayList<Collection<SteamGame>>();

		for (SteamId name : users) {
			try {
				userGames.add(getGames(name));
				logger.log(Level.INFO, "Added user " + name.getNickname() + " (" + name.getSteamId64() + ").");
			} catch (SteamCondenserException e) {
				logger.log(Level.SEVERE, e.getMessage());
				return null;
			}
		}

		Collection<SteamGame> commonGames = mergeSets(userGames);
		logger.log(Level.INFO, "Search complete.");

		return commonGames;
	}

	public SteamId checkSteamId(String nameToCheck) throws SteamCondenserException {
		try {
			return SteamId.create(nameToCheck);
		} catch (SteamCondenserException e1) {
			try {
				return SteamId.create(Long.parseLong(nameToCheck));
			} catch (SteamCondenserException | NumberFormatException e2) {
				throw e1;
			}
		}
	}

	/**
	 * Finds all games from the given steam user.
	 * 
	 * @param sId
	 *            The SteamId of the user to get games from.
	 * @return A set of all games for the give user.
	 * @throws SteamCondenserException
	 */
	public Collection<SteamGame> getGames(SteamId sId) throws SteamCondenserException {
		return sId.getGames().values();
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

		final Collection<SteamGame> result = new HashSet<SteamGame>();
		
		final CountDownLatch latch = new CountDownLatch(gameList.size());
		ExecutorService taskExecutor = Executors.newCachedThreadPool();

		for (final SteamGame game : gameList) {

		  taskExecutor.execute(new Runnable() {
        @Override
        public void run() {
          result.addAll(filterGame(game));
          latch.countDown();
        }

        private Collection<? extends SteamGame> filterGame(SteamGame game) {
          Collection<SteamGame> result = new HashSet<SteamGame>();
          
            // Need to send requests to the steampowered.com website for data.
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(
                "http://store.steampowered.com/api/appdetails/?appids=" + game.getAppId()).openStream()));) {
              
              // Read lines in until there are no more to be read, run filter on each line looking for specified package IDs.
              String line;
              while (((line = br.readLine()) != null) && (!result.contains(game))) {
                for (FilterType filter : filterList) {
                  if (line.contains("\"" + filter.getValue() + "\"")) {
                    result.add(game);
                  }
                }
              }

              logger.log(Level.INFO, "Checked game '" + game.getName() + "'");

            } catch (IOException e) {
              logger.log(Level.SEVERE, e.getMessage(), e);
            }
          return result;
        }
		  });
		}
		
		try {
      latch.await();
    } catch (InterruptedException e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
    }
		
		return result;
	}
	
	/**
	 * Merges multiple user game sets together to keep all games that are the same.
	 * 
	 * @param userGames
	 *            A list of user game sets.
	 * @return A set containing all common games.
	 */
	public Collection<SteamGame> mergeSets(List<Collection<SteamGame>> userGames) {

		if (userGames.size() == 0) {
			return null;
		}

		Collection<SteamGame> result = new ArrayList<SteamGame>();

		int size = 0;
		int index = 0;

		for (int i = 0; i < userGames.size(); i++) {
			if (userGames.get(i).size() > size) {
				size = userGames.get(i).size();
				index = i;
			}
		}

		result.addAll(userGames.get(index));

		for (int i = 0; i < userGames.size(); i++) {
			result.retainAll(userGames.get(i));
		}

		return result;

	}

	public String sanitiseInputString(String input) {
		return input.replace("'", "''");
	}

}
