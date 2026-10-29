package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardSubtype;
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

public class Card_1220_077_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("stalker", "1220_77");
                    put("probe", "1220_15");
                    put("hoth", "3_143");
                    put("ice", "3_148");
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
    public void StalkerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("stalker").getBlueprint();

        assertEquals("Stalker", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(8, card.getDeployCost(), scn.epsilon);
        assertEquals(8, card.getPower(), scn.epsilon);
        assertEquals(7, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getHyperspeed(), scn.epsilon);
        assertEquals(9, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SHIP_DOCKING_CAPABILITY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.HOTH);
            add(Icon.PILOT);
            add(Icon.NAV_COMPUTER);
            add(Icon.SCOMP_LINK);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void StalkerDeploysHothProbeDroidToRelatedSiteFromReserveDeck() {
        var scn = GetScenario();
        var stalker = scn.GetDSCard("stalker");
        var probe = scn.GetDSCard("probe");
        var hoth = scn.GetDSCard("hoth");
        var ice = scn.GetDSCard("ice");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveLocationToTable(ice);
        scn.MoveCardsToLocation(hoth, stalker);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(probe);
        assertTrue(scn.DSCardActionAvailable(stalker, "Deploy [Hoth] Probe Droid from Reserve Deck"));
        scn.DSUseCardAction(stalker, "Deploy [Hoth] Probe Droid from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(probe);
        scn.PassAllResponses();
        assertEquals(ice, probe.getAtLocation());
    }

    @Test
    public void StalkerDoesNotOfferProbeDroidSearchDuringControlPhase() {
        var scn = GetScenario();
        var stalker = scn.GetDSCard("stalker");
        var probe = scn.GetDSCard("probe");
        var hoth = scn.GetDSCard("hoth");
        var ice = scn.GetDSCard("ice");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveLocationToTable(ice);
        scn.MoveCardsToLocation(hoth, stalker);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(probe);
        assertFalse(scn.DSCardActionAvailable(stalker, "Deploy [Hoth] Probe Droid from Reserve Deck"));
    }
}
