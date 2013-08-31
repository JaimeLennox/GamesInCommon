package gamesincommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.SteamGame;
import com.github.koraktor.steamcondenser.steam.community.SteamId;

public class GamesInCommon {
	
  /**
   * Displays all common games from the given steam users.
   * @param users A list of names to find common games for.
   */
	public void displayCommonGames(List<String> users) {
    
	  try {
      
      List<Collection<SteamGame>> userGames = new ArrayList<Collection<SteamGame>>();
      
      for (String name : users) {
        userGames.add(getGames(SteamId.create(name)));
      }
      
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
	 * Creates a list of users from user input.
	 * @return The list of user names.
	 */
	public List<String> getUsers() {
	  
	  List<String> users = new ArrayList<String>();
    
    System.out.println("Enter users one by one, typing 'FIN' when complete:");
    
    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));){
      
      String input;
      input = br.readLine();
      
      while (!input.equals("FIN")) {
        
        users.add(input);
        input = br.readLine();
        
      }
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return users;
    
	}

	/**
	 * Finds all games from the given steam user.
	 * @param sId The SteamId of the user to get games from.
	 * @return A set of all games for the give user.
	 * @throws SteamCondenserException
	 */
	public Collection<SteamGame> getGames(SteamId sId) throws SteamCondenserException {
		return sId.getGames().values();
	} 
	
	/**
	 * Merges multiple user game sets together to keep all games that are the same.
	 * @param userGames A list of user game sets. There must be at least one set in this list.
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
	  
		GamesInCommon gamesInCommon = new GamesInCommon();
		
		List<String> users = gamesInCommon.getUsers();
		gamesInCommon.displayCommonGames(users);
		
	}

}
