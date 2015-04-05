package gamesincommon;

import com.github.koraktor.steamcondenser.community.SteamGame;
import com.github.koraktor.steamcondenser.community.SteamId;
import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by HSAR on 05/04/2015.
 */
public class Utils {

    /**
     * Finds all games from the given steam user.
     *
     * @param sId
     *            The SteamId of the user to get games from.
     * @return A set of all games for the give user.
     * @throws SteamCondenserException
     */
    public static Collection<SteamGame> getGames(SteamId sId) throws SteamCondenserException {
        return sId.getGames().values();
    }

    /**
     * Finds all games from the given steam user.
     *
     * @param nameToCheck
     *            The name/id of the SteamID to check
     * @return SteamId if valid
     * @throws SteamCondenserException if not
     */
    public static SteamId checkSteamId(String nameToCheck) throws SteamCondenserException {
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
     * Merges multiple user game sets together to keep all games that are the same.
     *
     * @param userGames
     *            A list of user game sets.
     * @return A set containing all common games.
     */
    public static Collection<SteamGame> mergeSets(List<Collection<SteamGame>> userGames) {

        if (userGames.size() == 0) {
            return null;
        }

        Collection<SteamGame> result = new ArrayList<SteamGame>();

        int size = 0;
        int index = 0;

        for (int i = 0; i < userGames.size(); i++) {
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }
            if (userGames.get(i).size() > size) {
                size = userGames.get(i).size();
                index = i;
            }
        }

        result.addAll(userGames.get(index));

        for (Collection<SteamGame> userGame : userGames) {
            if (Thread.currentThread().isInterrupted()) {
                return null;
            }
            result.retainAll(userGame);
        }

        return result;

    }

}
