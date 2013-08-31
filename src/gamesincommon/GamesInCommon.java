package gamesincommon;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.SteamGame;
import com.github.koraktor.steamcondenser.steam.community.SteamId;

public class GamesInCommon {
	
	private final String HSARsID = "76561197998157106";

	public GamesInCommon() {
		SteamId id;
		try {
			id = SteamId.create("HSAR");
			Map<Integer, SteamGame> gamesMap = id.getGames();
			Set<Map.Entry<Integer, SteamGame>> games = gamesMap.entrySet();
			for (Entry<Integer, SteamGame> i : games ) {
				System.out.println(i);
			}
		} catch (SteamCondenserException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		GamesInCommon gic = new GamesInCommon();
	}

}
