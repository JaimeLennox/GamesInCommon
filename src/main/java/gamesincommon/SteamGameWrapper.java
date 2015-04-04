package gamesincommon;

import com.github.koraktor.steamcondenser.community.SteamGame;

public class SteamGameWrapper implements Comparable<SteamGameWrapper> {
	
	private SteamGame game;
	
	public SteamGameWrapper(SteamGame game) {
		this.game = game;
	}
	
	public SteamGame getGame() {
		return game;
	}
	
	@Override
	public String toString() {
		return game.getName();
	}

	@Override
	public int compareTo(SteamGameWrapper o) {
		return this.toString().compareToIgnoreCase(o.toString());
	}

}
