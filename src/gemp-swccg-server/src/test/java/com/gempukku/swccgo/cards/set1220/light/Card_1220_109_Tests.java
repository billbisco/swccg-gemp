package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_109_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("toolkit", "1220_109");
                    put("han", "1_11");
                    put("falcon", "1_143");
                }},
                new HashMap<>() {{
                    put("reactor", "1_228");
                    put("presence", "1_227");
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void HansToolkitStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("toolkit").getBlueprint();

        assertEquals("Han's Toolkit", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.DAGOBAH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HansToolkitSuspendsNonImmuneToAlterEffectsWhileHanPilotsFalconAtBattleground() {
        var scn = GetScenario();
        var toolkit = scn.GetLSCard("toolkit");
        var han = scn.GetLSCard("han");
        var falcon = scn.GetLSCard("falcon");
        var system = scn.GetLSStartingLocation();
        var reactor = scn.GetDSCard("reactor");

        scn.StartGame();
        scn.MoveCardsToLocation(system, falcon);
        scn.BoardAsPilot(falcon, han);
        scn.AttachCardsTo(falcon, toolkit);
        scn.MoveCardsToDSSideOfTable(reactor);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(reactor.isSuspended());
    }

    @Test
    public void HansToolkitDoesNotSuspendEffectsWhenHanIsNotPilotingFalcon() {
        var scn = GetScenario();
        var toolkit = scn.GetLSCard("toolkit");
        var han = scn.GetLSCard("han");
        var falcon = scn.GetLSCard("falcon");
        var system = scn.GetLSStartingLocation();
        var reactor = scn.GetDSCard("reactor");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(system, falcon);
        scn.MoveCardsToLocation(site, han);
        scn.AttachCardsTo(falcon, toolkit);
        scn.MoveCardsToDSSideOfTable(reactor);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(reactor.isSuspended());
    }

    @Test
    public void HansToolkitDoesNotSuspendPresenceOfTheForce() {
        var scn = GetScenario();
        var toolkit = scn.GetLSCard("toolkit");
        var han = scn.GetLSCard("han");
        var falcon = scn.GetLSCard("falcon");
        var system = scn.GetLSStartingLocation();
        var presence = scn.GetDSCard("presence");

        scn.StartGame();
        scn.MoveCardsToLocation(system, falcon);
        scn.BoardAsPilot(falcon, han);
        scn.AttachCardsTo(falcon, toolkit);
        scn.AttachCardsTo(system, presence);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(presence.isSuspended());
    }
}
