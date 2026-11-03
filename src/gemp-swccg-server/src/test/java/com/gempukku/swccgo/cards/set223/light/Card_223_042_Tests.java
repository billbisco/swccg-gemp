package com.gempukku.swccgo.cards.set223.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_223_042_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("passage", "223_042");
                }},
                new HashMap<>()
                {{
                }},
                10,
                10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /**
     * Records the game messages sent from now on (the replay of earlier messages on registration is discarded).
     */
    private List<String> RecordMessages(VirtualTableScenario scn) {
        List<String> messages = new ArrayList<>();
        GameStateListener listener = (GameStateListener) Proxy.newProxyInstance(GameStateListener.class.getClassLoader(),
                new Class<?>[]{GameStateListener.class}, (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "sendMessage": messages.add((String) args[0]); return null;
                        case "hashCode": return System.identityHashCode(proxy);
                        case "equals": return proxy == args[0];
                        case "toString": return "message recorder";
                    }
                    Class<?> type = method.getReturnType();
                    if (type == boolean.class) return false;
                    if (type == int.class) return 0;
                    if (type == long.class) return 0L;
                    return null;
                });
        scn.game().addGameStateListener(scn.LS, listener);
        messages.clear();
        return messages;
    }

    @Test
    public void PassageReshufflesReserveDeckWhenNoFalconOrHanFound() {
        //"...from hand and/or Reserve Deck; reshuffle": the Reserve Deck is reshuffled after searching it, even if nothing is found
        var scn = GetScenario();

        var passage = scn.GetLSCard("passage");

        scn.StartGame();

        scn.MoveCardsToLSSideOfTable(passage);

        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(passage, "Deploy Falcon and Han"));

        var messages = RecordMessages(scn);
        scn.LSUseCardAction(passage, "Deploy Falcon and Han");
        //no Falcon or Han: both players may verify the Reserve Deck after the unsuccessful search
        assertTrue(scn.LSDecisionAvailable("Verify Reserve Deck"));
        assertTrue(scn.DSDecisionAvailable("Verify Reserve Deck"));
        scn.LSPass();
        scn.DSPass();
        scn.PassAllResponses();

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(1, messages.stream().filter(m -> m.startsWith(VirtualTableScenario.LS + " shuffles Reserve Deck")).count());
    }
}
