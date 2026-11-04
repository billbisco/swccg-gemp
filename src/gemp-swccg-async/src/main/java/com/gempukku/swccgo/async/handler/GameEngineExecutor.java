package com.gempukku.swccgo.async.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * swccg-guild/work#136: the threads that run game-engine work for player answers.
 * <p>
 * Without it, an answer runs the engine (the action, every triggered response, and any AI turns that follow) on the
 * Netty event-loop thread that read the request. That thread serves many other connections, so one slow step in a huge
 * game delays the hall, chat and other games that share it. Here the event loop only reads the request and hands the
 * answer to this pool. Each game still processes one answer at a time, because {@code SwccgGameMediator.playerAnswered}
 * holds that game's lock, so the order of the game's steps does not change.
 * <p>
 * Size: {@code -Dgemp.engineThreads=N}. The default is the number of cores, at least 4. {@code 0} turns the pool off and
 * runs answers on the event loop as before (a kill switch).
 */
public final class GameEngineExecutor {
    private static final Logger LOG = LogManager.getLogger(GameEngineExecutor.class);
    private static final int THREADS = Integer.getInteger("gemp.engineThreads",
            Math.max(4, Runtime.getRuntime().availableProcessors()));
    private static final ExecutorService POOL = THREADS > 0 ? create(THREADS) : null;

    private GameEngineExecutor() {
    }

    private static ExecutorService create(int threads) {
        AtomicInteger n = new AtomicInteger();
        ThreadPoolExecutor pool = new ThreadPoolExecutor(threads, threads, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                r -> {
                    Thread t = new Thread(r, "gemp-engine-" + n.incrementAndGet());
                    t.setDaemon(true);
                    return t;
                });
        LOG.info("Game engine pool: " + threads + " threads");
        return pool;
    }

    /** Whether answers run on the pool (false when -Dgemp.engineThreads=0). */
    public static boolean enabled() {
        return POOL != null;
    }

    /** Runs the task on the pool, or right here when the pool is off. */
    public static void execute(Runnable task) {
        if (POOL == null)
            task.run();
        else
            POOL.execute(task);
    }
}
