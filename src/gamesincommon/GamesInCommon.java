package gamesincommon;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.SteamGame;
import com.github.koraktor.steamcondenser.steam.community.SteamId;

public class GamesInCommon {

	public GamesInCommon() {
		// LEGACY CODE
		// try {
		// // get games for HSAR
		// SteamId id = SteamId.create("HSAR");
		// Map<Integer, SteamGame> gamesMap = id.getGames();
		// Set<Map.Entry<Integer, SteamGame>> games = gamesMap.entrySet();
		// // lists games for HSAR (probably bad code, just testing right now)
		// for (Entry<Integer, SteamGame> i : games) {
		// System.out.println(i.getValue().getName());
		// }
		// } catch (SteamCondenserException e) {
		// System.err.println(e.getMessage());
		// }
		try {
			Set<Map.Entry<Integer, SteamGame>> set1 = getGames(SteamId.create("HSAR"));
			System.out.println("SET1 size = "+set1.size());
			Set<Map.Entry<Integer, SteamGame>> set2 = getGames(SteamId.create("yammy24"));
			System.out.println("SET2 size = "+set2.size());
			set1.retainAll(set2);
			// lists games in common
			for (Entry<Integer, SteamGame> i : set1) {
				System.out.println(i.getValue().getName());
			}
		} catch (SteamCondenserException e) {
			e.printStackTrace();
		}
	}

	public Set<Map.Entry<Integer, SteamGame>> getGames(SteamId sid) throws SteamCondenserException {
		Set<Map.Entry<Integer, SteamGame>> result;
		result = sid.getGames().entrySet();
		return result;
	}

	public static void main(String[] args) {
		GamesInCommon gic = new GamesInCommon();
	}

}
