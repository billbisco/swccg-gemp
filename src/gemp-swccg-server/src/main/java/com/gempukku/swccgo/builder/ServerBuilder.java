package com.gempukku.swccgo.builder;

import com.gempukku.swccgo.game.save.GameSaveCodec;
import com.gempukku.swccgo.game.save.GameSaveDAO;
import com.gempukku.swccgo.game.save.SaveBuild;
import com.gempukku.swccgo.game.save.SavedGameManager;
import com.gempukku.swccgo.guest.GuestSettings;
import org.apache.logging.log4j.LogManager;
import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.gempukku.swccgo.guest.PlayNowGames;
import com.gempukku.swccgo.chat.ChatServer;
import com.gempukku.swccgo.collection.CollectionsManager;
import com.gempukku.swccgo.collection.TransferDAO;
import com.gempukku.swccgo.db.*;
import com.gempukku.swccgo.cache.CacheManager;
import com.gempukku.swccgo.draft2.SoloDraftDefinitions;
import com.gempukku.swccgo.events.CalendarService;
import com.gempukku.swccgo.events.EventHistoryService;
import com.gempukku.swccgo.game.GameHistoryService;
import com.gempukku.swccgo.game.GameRecorder;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgoServer;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.hall.HallServer;
import com.gempukku.swccgo.league.LeagueFactory;
import com.gempukku.swccgo.league.LeagueScheduleService;
import com.gempukku.swccgo.league.LeagueService;
import com.gempukku.swccgo.metrics.MetricsDAO;
import com.gempukku.swccgo.metrics.MetricsService;
import com.gempukku.swccgo.merchant.MerchantService;
import com.gempukku.swccgo.packagedProduct.DraftPackStorage;
import com.gempukku.swccgo.packagedProduct.PackagedProductStorage;
import com.gempukku.swccgo.service.AdminService;
import com.gempukku.swccgo.service.AuthService;
import com.gempukku.swccgo.service.LoginThrottle;
import com.gempukku.swccgo.security.LegacyHashMigration;
import com.gempukku.swccgo.security.PasswordHasher;
import com.gempukku.swccgo.security.PasswordSettings;
import com.gempukku.swccgo.service.LoggedUserHolder;
import com.gempukku.swccgo.tournament.*;

import java.lang.reflect.Type;
import java.util.Map;

public class ServerBuilder {

    public static void CreatePrerequisites(Map<Type, Object> objectMap) {
        LoggedUserHolder loggedUserHolder = new LoggedUserHolder();
        loggedUserHolder.start();
        objectMap.put(LoggedUserHolder.class, loggedUserHolder);
    }
    public static void CreateServices(Map<Type, Object> objectMap) {
        objectMap.put(SwccgoFormatLibrary.class,
                new SwccgoFormatLibrary(
                        extract(objectMap, SwccgCardBlueprintLibrary.class)));

        objectMap.put(GameHistoryService.class,
                new GameHistoryService(
                        extract(objectMap, GameHistoryDAO.class)));

        objectMap.put(CollectionsManager.class,
                new CollectionsManager(
                        extract(objectMap, PlayerDAO.class),
                        extract(objectMap, CollectionDAO.class),
                        extract(objectMap, TransferDAO.class),
                        extract(objectMap, SwccgCardBlueprintLibrary.class)));

        objectMap.put(SoloDraftDefinitions.class,
                new SoloDraftDefinitions(
                        extract(objectMap, CollectionsManager.class),
                        extract(objectMap, SwccgCardBlueprintLibrary.class),
                        extract(objectMap, SwccgoFormatLibrary.class)));

        objectMap.put(GameRecorder.class,
                new GameRecorder(
                        extract(objectMap, GameHistoryService.class), extract(objectMap, SoloDraftDefinitions.class)));

        objectMap.put(LeagueService.class,
                new LeagueService(
                        extract(objectMap, SwccgCardBlueprintLibrary.class),
                        extract(objectMap, LeagueDAO.class),
                        extract(objectMap, LeagueMatchDAO.class),
                        extract(objectMap, LeagueParticipationDAO.class),
                        extract(objectMap, CollectionsManager.class),
                        extract(objectMap, SoloDraftDefinitions.class)));

        // League creation (the League Admin form and the league schedules both go through the factory) and the
        // schedules that create leagues on their own
        objectMap.put(LeagueFactory.class,
                new LeagueFactory(
                        extract(objectMap, SwccgCardBlueprintLibrary.class),
                        extract(objectMap, SwccgoFormatLibrary.class),
                        extract(objectMap, SoloDraftDefinitions.class),
                        extract(objectMap, LeagueDAO.class),
                        extract(objectMap, LeagueService.class)));

        objectMap.put(LeagueScheduleService.class,
                new LeagueScheduleService(
                        extract(objectMap, LeagueScheduleDAO.class),
                        extract(objectMap, LeagueFactory.class),
                        extract(objectMap, LeagueService.class),
                        extract(objectMap, LeagueDAO.class),
                        extract(objectMap, SwccgCardBlueprintLibrary.class)));

        PasswordHasher passwordHasher = new PasswordHasher(PasswordSettings.iterations());
        objectMap.put(AuthService.class,
                new AuthService(
                        extract(objectMap, PlayerDAO.class),
                        passwordHasher,
                        new LoginThrottle()));
        // Wrap any unsalted SHA-256 hashes left in the DB (idempotent, background).
        LegacyHashMigration.runInBackground(extract(objectMap, PlayerDAO.class), passwordHasher);

        objectMap.put(AdminService.class,
                new AdminService(
                        extract(objectMap, PlayerDAO.class),
                        extract(objectMap, IpBanDAO.class),
                        extract(objectMap, LoggedUserHolder.class),
                        extract(objectMap, AuthService.class)
                ));

        TournamentPrizeSchemeRegistry tournamentPrizeSchemeRegistry = new TournamentPrizeSchemeRegistry();
        PairingMechanismRegistry pairingMechanismRegistry = new PairingMechanismRegistry();

        objectMap.put(TournamentService.class,
                new TournamentService(
                        extract(objectMap, CollectionsManager.class),
                        extract(objectMap, PackagedProductStorage.class),
                        new DraftPackStorage(),
                        pairingMechanismRegistry,
                        tournamentPrizeSchemeRegistry,
                        extract(objectMap, TournamentDAO.class),
                        extract(objectMap, TournamentPlayerDAO.class),
                        extract(objectMap, TournamentMatchDAO.class)));

        // The Events tab: the calendar and the completed-event browser (read-only views over the league and
        // tournament tables; the browser caches a month at a time and is emptied with the other caches)
        objectMap.put(CalendarService.class,
                new CalendarService(
                        extract(objectMap, LeagueDAO.class),
                        extract(objectMap, LeagueService.class),
                        extract(objectMap, TournamentDAO.class),
                        extract(objectMap, SwccgCardBlueprintLibrary.class),
                        extract(objectMap, SwccgoFormatLibrary.class),
                        extract(objectMap, SoloDraftDefinitions.class),
                        extract(objectMap, LeagueScheduleService.class)));

        EventHistoryService eventHistoryService = new EventHistoryService(
                extract(objectMap, LeagueDAO.class),
                extract(objectMap, TournamentDAO.class),
                extract(objectMap, SwccgCardBlueprintLibrary.class),
                extract(objectMap, SwccgoFormatLibrary.class),
                extract(objectMap, SoloDraftDefinitions.class));
        extract(objectMap, CacheManager.class).addCache(eventHistoryService);
        objectMap.put(EventHistoryService.class, eventHistoryService);

        objectMap.put(MerchantService.class,
                new MerchantService(
                        extract(objectMap, SwccgCardBlueprintLibrary.class),
                        extract(objectMap, CollectionsManager.class),
                        extract(objectMap, MerchantDAO.class)));

        objectMap.put(MetricsService.class,
                new MetricsService(extract(objectMap, MetricsDAO.class)));

        objectMap.put(ChatServer.class, new ChatServer());

        objectMap.put(SwccgoServer.class,
                new SwccgoServer(
                        extract(objectMap, DeckDAO.class),
                        extract(objectMap, SwccgCardBlueprintLibrary.class),
                        extract(objectMap, ChatServer.class),
                        extract(objectMap, GameRecorder.class),
                        extract(objectMap, InGameStatisticsDAO.class)));

        objectMap.put(HallServer.class,
                new HallServer(
                        extract(objectMap, SwccgoServer.class),
                        extract(objectMap, ChatServer.class),
                        extract(objectMap, LeagueService.class),
                        extract(objectMap, TournamentService.class),
                        extract(objectMap, SwccgCardBlueprintLibrary.class),
                        extract(objectMap, SwccgoFormatLibrary.class),
                        extract(objectMap, CollectionsManager.class),
                        extract(objectMap, PlayerDAO.class),
                        extract(objectMap, IpBanDAO.class),
                        extract(objectMap, GempSettingDAO.class),
                        extract(objectMap, BotStatsDAO.class),
                        extract(objectMap, AdminService.class),
                        tournamentPrizeSchemeRegistry,
                        pairingMechanismRegistry,
                        extract(objectMap, PackagedProductStorage.class),
                        extract(objectMap, SoloDraftDefinitions.class),
                        extract(objectMap, LeagueScheduleService.class)
                ));

        GuestSettings guestSettings = GuestSettings.fromConfiguration();
        objectMap.put(GuestSettings.class, guestSettings);
        extract(objectMap, HallServer.class).setMaxPlayNowGames(guestSettings.maxConcurrentGames());
        objectMap.put(PlayNowGames.class, extract(objectMap, HallServer.class).getPlayNowGames());
        if (guestSettings.enabled()) {
            // Play now games are saved every turn and can be resumed (only while Play now is on)
            extract(objectMap, HallServer.class).enablePlayNowSaves(
                    extract(objectMap, GameSaveDAO.class),
                    new GameSaveCodec(extract(objectMap, SwccgCardBlueprintLibrary.class),
                            extract(objectMap, SwccgoFormatLibrary.class), SaveBuild.current()),
                    Clock.systemUTC(),
                    new SavedGameManager.Settings(guestSettings.maxConcurrentGames(),
                            Duration.ofMinutes(guestSettings.unloadIdleMinutes()),
                            Duration.ofMinutes(guestSettings.capEvictIdleMinutes()),
                            Duration.ofDays(guestSettings.saveRetentionDays())));
        }
    }

    private static <T> T extract(Map<Type, Object> objectMap, Class<T> clazz) {
        T result = (T) objectMap.get(clazz);
        if (result == null)
            throw new RuntimeException("Unable to find class " + clazz.getName());
        return result;
    }

    public static void StartServers(Map<Type, Object> objectMap) {
        final HallServer hallServer = extract(objectMap, HallServer.class);
        if (hallServer.getPlayNowSaveManager() != null) {
            ScheduledExecutorService saves = Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "play-now-saves");
                thread.setDaemon(true);
                return thread;
            });
            saves.scheduleWithFixedDelay(() -> {
                try {
                    hallServer.tickPlayNowSaves();
                } catch (RuntimeException exp) {
                    LogManager.getLogger(ServerBuilder.class).error("Play now save tick failed", exp);
                }
            }, 1, 1, TimeUnit.MINUTES);
            Runtime.getRuntime().addShutdownHook(new Thread(hallServer::savePlayNowGames, "play-now-save-on-shutdown"));
        }
        MetricsService metricsService = extract(objectMap, MetricsService.class);
        ScheduledExecutorService metricsJobs = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "metrics-maintenance");
            thread.setDaemon(true);
            return thread;
        });
        metricsJobs.scheduleWithFixedDelay(() -> {
            try {
                metricsService.processDueJobsIfTime();
            } catch (RuntimeException exp) {
                LogManager.getLogger(ServerBuilder.class).error("Metrics maintenance failed", exp);
            }
        }, 1, 1, TimeUnit.HOURS);
        extract(objectMap, HallServer.class).startServer();
        extract(objectMap, SwccgoServer.class).startServer();
        extract(objectMap, ChatServer.class).startServer();
    }

    public static void StopServers(Map<Type, Object> objectMap) {
        extract(objectMap, HallServer.class).savePlayNowGames();
        extract(objectMap, HallServer.class).stopServer();
        extract(objectMap, SwccgoServer.class).stopServer();
        extract(objectMap, ChatServer.class).stopServer();
    }
}
