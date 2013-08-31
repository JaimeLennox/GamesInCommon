package gamesincommon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.SteamGame;
import com.github.koraktor.steamcondenser.steam.community.SteamId;

public class GamesInCommon {

	public GamesInCommon() {

		try {
		  
		  List<Set<Map.Entry<Integer, SteamGame>>> userGames = new ArrayList<Set<Map.Entry<Integer, SteamGame>>>();
		  
			userGames.add(getGames(SteamId.create("HSAR")));
			userGames.add(getGames(SteamId.create("yammy24")));
			
			Set<Map.Entry<Integer, SteamGame>> commonGames = mergeSets(userGames);
			
			// Lists games in common.
			for (Entry<Integer, SteamGame> i : commonGames) {
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
	
	/**
	 * Merges multiple user game sets together to keep all games that are the same.
	 * @param userGames A list of user game sets.
	 * @return A set containing all common games.
	 */
	public Set<Map.Entry<Integer, SteamGame>> mergeSets(List<Set<Map.Entry<Integer, SteamGame>>> userGames) {
	  
	  Set<Map.Entry<Integer, SteamGame>> result = new HashSet<Map.Entry<Integer, SteamGame>>();
	  
	  for (int i = 0; i < userGames.size(); i++) {
	    result.retainAll(userGames.get(i));
	  }
	  
	  return result;
	  
	}

	public static void main(String[] args) {
		GamesInCommon gic = new GamesInCommon();
	}

}
