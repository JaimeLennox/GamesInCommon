package gamesincommon;

import gamesincommon.GamesInCommon;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GamesInCommonTest {

    GamesInCommon gamesInCommon;

    @Before
    public void setUp() throws Exception {
        gamesInCommon = new GamesInCommon();
        gamesInCommon.requireWebCheck(false);
    }

    @Test
    public void testGetLogger() throws Exception {
        Logger loggerUnderTest = gamesInCommon.getLogger();
        assertNotNull(loggerUnderTest);
    }
}