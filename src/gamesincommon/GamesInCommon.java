package gamesincommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.SteamGame;
import com.github.koraktor.steamcondenser.steam.community.SteamId;
import com.github.koraktor.steamcondenser.steam.community.WebApi;

public class GamesInCommon {

	public enum filterType {

		multiplayer("Multi-player"), coop("Co-op");

		private String value;

		filterType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return this.getValue();
		}

		public static filterType getEnum(String value) {
			if (value == null)
				throw new IllegalArgumentException();
			for (filterType v : values())
				if (value.equalsIgnoreCase(v.getValue()))
					return v;
			throw new IllegalArgumentException();
		}
	};

	public GamesInCommon() {

		try {

			List<Collection<SteamGame>> userGames = new ArrayList<Collection<SteamGame>>();

			userGames.add(getGames(SteamId.create("HSAR")));
			userGames.add(getGames(SteamId.create("yammy24")));

			Collection<SteamGame> commonGames = mergeSets(userGames);

			// List user game sizes.
			for (int i = 0; i < userGames.size(); i++) {
				System.out.println("Set " + i + " size: " + userGames.get(i).size());
			}

			// Lists games in common.
			for (SteamGame i : commonGames) {
				System.out.println(i.getName());
			}

		} catch (SteamCondenserException e) {
			e.printStackTrace();
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
	public Collection<SteamGame> filterGames(Collection<SteamGame> gameList, List<filterType> filterList) {
		InputStream is = null;
		Collection<SteamGame> result = new HashSet<SteamGame>();
		for (SteamGame game : gameList) {
			try {
				is = new URL("http://store.steampowered.com/api/appdetails/?appids=" + game.getAppId()).openStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				// read lines in until there are no more to be read, run filter
				// on each line looking for specified package IDs
				String line;
				while ((line = br.readLine()) != null) {
					for (filterType filter : filterList) {
						if (line.contains(filter.getValue())) {
							result.add(game);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException ioe) {
					// nothing to see here
				}
			}
		}
		return result;
	}

	/**
	 * Merges multiple user game sets together to keep all games that are the
	 * same.
	 * 
	 * @param userGames
	 *            A list of user game sets. There must be at least one set in
	 *            this list.
	 * @return A set containing all common games.
	 */
	public Collection<SteamGame> mergeSets(List<Collection<SteamGame>> userGames) {

		Collection<SteamGame> result = userGames.get(0);

		for (int i = 1; i < userGames.size(); i++) {
			result.retainAll(userGames.get(i));
		}

		return result;

	}

	public static void main(String[] args) {
		GamesInCommon gic = new GamesInCommon();
	}

}
