package gamesincommon;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.community.SteamId;

public class GamesInCommon {

	public GamesInCommon() {
		SteamId id;
		try {
			id = SteamId.create("HSAR");
			id.getGames();
		} catch (SteamCondenserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
