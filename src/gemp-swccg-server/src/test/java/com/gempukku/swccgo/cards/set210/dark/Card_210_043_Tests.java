package com.gempukku.swccgo.cards.set210.dark;

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

public class Card_210_043_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                }},
                new HashMap<>()
                {{
                    put("terminal", "210_043");
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
        scn.game().addGameStateListener(scn.DS, listener);
        messages.clear();
        return messages;
    }

    @Test
    public void DeathStarReactorTerminalReshufflesReserveDeckWhenNoDeviceFound() {
        //"may deploy a device ... from Lost Pile or Reserve Deck (reshuffle)": the Reserve Deck is reshuffled after searching it
        var scn = GetScenario();

        var terminal = scn.GetDSCard("terminal");

        scn.StartGame();

        scn.MoveCardsToDSSideOfTable(terminal);

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(terminal, "Deploy device from Reserve Deck"));

        var messages = RecordMessages(scn);
        scn.DSUseCardAction(terminal, "Deploy device from Reserve Deck");
        //no device: both players may verify the Reserve Deck after the unsuccessful search
        assertTrue(scn.DSDecisionAvailable("Verify Reserve Deck"));
        assertTrue(scn.LSDecisionAvailable("Verify Reserve Deck"));
        scn.DSPass();
        scn.LSPass();
        scn.PassAllResponses();

        assertEquals(1, messages.stream().filter(m -> m.startsWith(VirtualTableScenario.DS + " shuffles Reserve Deck")).count());
    }
}
