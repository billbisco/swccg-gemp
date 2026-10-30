package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1103_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hook", "1103_10");
                    put("hook1", "1103_10");
                    put("hook2", "1103_10");
                    put("hook3", "1103_10");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("reinforcements", "1_251");
                    put("copy", "1_251");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void GrapplingHookStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hook").getBlueprint();

        assertEquals("Grappling Hook", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.GRABBER);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GrapplingHookGrabsJustPlayedInterrupt() {
        var scn = GetScenario();
        var hook = scn.GetLSCard("hook");
        var luke = scn.GetLSCard("luke");
        var reinforcements = scn.GetDSCard("reinforcements");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(hook);
        scn.MoveCardsToDSHand(reinforcements);
        scn.LSActivateForceCheat(2);
        scn.DSActivateForceCheat(2);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(reinforcements));
        scn.DSPlayCard(reinforcements);
        scn.LSPass();
        scn.DSPass();

        assertTrue(scn.LSCardPlayAvailable(hook, "grab"));
        scn.LSPlayCard(hook, "grab");
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, hook);
        assertTrue(scn.IsStackedOn(hook, reinforcements));
        assertInZone(Zone.STACKED, reinforcements);
    }

    @Test
    public void GrapplingHookMakesOtherCopiesOfStackedInterruptDestinyMinusThree() {
        var scn = GetScenario();
        var hook = scn.GetLSCard("hook");
        var luke = scn.GetLSCard("luke");
        var reinforcements = scn.GetDSCard("reinforcements");
        var copy = scn.GetDSCard("copy");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(hook);
        scn.MoveCardsToDSHand(reinforcements);
        scn.LSActivateForceCheat(2);
        scn.DSActivateForceCheat(2);
        scn.SkipToDSTurn(Phase.CONTROL);

        int printedDestiny = scn.GetDestiny(copy);
        scn.DSPlayCard(reinforcements);
        scn.LSPass();
        scn.DSPass();
        scn.LSPlayCard(hook, "grab");
        scn.PassAllResponses();

        assertTrue(scn.IsStackedOn(hook, reinforcements));
        assertEquals(printedDestiny - 3, scn.GetDestiny(copy));
    }

    @Test
    public void GrapplingHookDoesNotGrabWhenThreeGrapplingHooksAreOnTable() {
        var scn = GetScenario();
        var hook = scn.GetLSCard("hook");
        var hook1 = scn.GetLSCard("hook1");
        var hook2 = scn.GetLSCard("hook2");
        var hook3 = scn.GetLSCard("hook3");
        var luke = scn.GetLSCard("luke");
        var reinforcements = scn.GetDSCard("reinforcements");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSSideOfTable(hook1, hook2, hook3);
        scn.MoveCardsToLSHand(hook);
        scn.MoveCardsToDSHand(reinforcements);
        scn.LSActivateForceCheat(2);
        scn.DSActivateForceCheat(2);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(reinforcements));
        scn.DSPlayCard(reinforcements);
        scn.LSPass();
        scn.DSPass();

        assertFalse(scn.LSCardPlayAvailable(hook));
        assertFalse(scn.LSCardPlayAvailable(hook, "grab"));
        assertEquals(Zone.HAND, hook.getZone());
    }
}
