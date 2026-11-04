package com.gempukku.swccgo.async.handler;

import com.gempukku.swccgo.SubscriptionExpiredException;
import com.gempukku.swccgo.game.Player;
import com.gempukku.swccgo.game.SwccgGameMediator;
import com.gempukku.swccgo.guest.GuestIdentity;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * swccg-guild/work#136: a player's answer runs the engine on the game engine pool, not on the Netty thread that read
 * the request, so a slow step in one game doesn't hold up that thread's other connections.
 */
public class GameEngineExecutorTests {
    @Test
    public void SlowAnswerDoesNotHoldTheRequestThread() throws Exception {
        GuestHandlerFixture f = new GuestHandlerFixture(GuestHandlerFixture.enabled());
        GuestRequestHandler guestHandler = new GuestRequestHandler(f.context);
        GameRequestHandler gameHandler = new GameRequestHandler(f.context, null);
        String cookie = GuestIdentity.COOKIE + "=" + Requests.cookieValue(f.guestStart(guestHandler, null, "1.2.3.4").headers.get("set-cookie"));
        SwccgGameMediator mediator = f.swccgoServer.getGameById("g1");

        AtomicReference<String> engineThread = new AtomicReference<>();
        CountDownLatch answered = new CountDownLatch(1);
        doAnswer(inv -> {
            engineThread.set(Thread.currentThread().getName());
            Thread.sleep(1500); // a slow engine step
            return null;
        }).when(mediator).playerAnswered(any(Player.class), anyInt(), anyInt(), anyString());
        when(mediator.getCommunicationChannel(any(Player.class), anyInt())).thenAnswer(inv -> {
            answered.countDown();
            throw new SubscriptionExpiredException();
        });

        RecordingResponseWriter rw = new RecordingResponseWriter();
        long start = System.nanoTime();
        gameHandler.handleRequest("/g1", Requests.post("/x", "participantId=x&channelNumber=1&decisionId=3&decisionValue=0", cookie),
                f.context, rw, "1.2.3.4");
        long requestThreadMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);

        assertTrue(requestThreadMs < 1000, "the request thread is free while the engine works (took " + requestThreadMs + " ms)");
        assertTrue(answered.await(10, TimeUnit.SECONDS), "the answer was processed");
        assertTrue(engineThread.get().startsWith("gemp-engine-"), "ran on " + engineThread.get());
        for (int i = 0; i < 100 && rw.status == 0; i++)
            Thread.sleep(20);
        assertEquals(410, rw.status, "errors still reach the client");
    }
}
