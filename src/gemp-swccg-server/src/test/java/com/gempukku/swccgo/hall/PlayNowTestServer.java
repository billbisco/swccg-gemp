package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.chat.ChatServer;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.db.BotStatsDAO;
import com.gempukku.swccgo.db.DeckDAO;
import com.gempukku.swccgo.db.GempSettingDAO;
import com.gempukku.swccgo.db.InGameStatisticsDAO;
import com.gempukku.swccgo.db.IpBanDAO;
import com.gempukku.swccgo.db.PlayerDAO;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.GameRecorder;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.league.LeagueScheduleService;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;
import com.gempukku.swccgo.service.AdminService;
import com.gempukku.swccgo.tournament.PairingMechanismRegistry;
import com.gempukku.swccgo.tournament.TournamentPrizeSchemeRegistry;
import com.gempukku.swccgo.tournament.TournamentService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** A real HallServer and SwccgoServer on the test card library, with every database mocked. */
public class PlayNowTestServer {
    public final PlayerDAO playerDAO = mock(PlayerDAO.class);
    public final BotStatsDAO botStatsDAO = mock(BotStatsDAO.class);
    public final GameRecorder gameRecorder = mock(GameRecorder.class);
    public final ChatServer chatServer = new ChatServer();
    public final SwccgoServer swccgoServer;
    public final HallServer hallServer;

    public PlayNowTestServer() {
        swccgoServer = new SwccgoServer(mock(DeckDAO.class), VirtualTableScenario._cardLibrary, chatServer,
                gameRecorder, mock(InGameStatisticsDAO.class));
        hallServer = new HallServer(swccgoServer, chatServer, mock(LeagueService.class),
                mock(TournamentService.class), VirtualTableScenario._cardLibrary, VirtualTableScenario._formatLibrary,
                mock(CollectionsManager.class), playerDAO, mock(IpBanDAO.class), mock(GempSettingDAO.class),
                botStatsDAO, mock(AdminService.class), new TournamentPrizeSchemeRegistry(),
                new PairingMechanismRegistry(), mock(PackagedProductStorage.class), mock(SoloDraftDefinitions.class),
                mock(LeagueScheduleService.class));
    }

    /** A registered player, known to the mocked player DAO. */
    public Player member(int id, String name) {
        Player p = new Player(id, name, "x", "", null, null, null, null);
        when(playerDAO.getPlayer(name)).thenReturn(p);
        return p;
    }

    /** A guest as the game handlers see them: no account, just their seat. */
    public static Player seat(String seatId) {
        return new Player(-1, seatId, null, "", null, null, null, null);
    }
}
