package com.gempukku.swccgo.cards.set1230.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1230_110_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("signal", "1230_110");
                    put("blaster", "1_156");
                    put("insight", "9_47");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void TheSignalStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("signal").getBlueprint();

        assertEquals("The Signal", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_STARTING, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TheSignalTakesACharacterWeaponFromReserveDeck() {
        var scn = GetScenario();
        var signal = scn.GetLSCard("signal");
        var blaster = scn.GetLSCard("blaster");

        scn.StartGame();
        assertTrue(scn.LSDecisionAvailable("Choose starting interrupt"));
        scn.LSPass();
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLSHand(signal);
        scn.MoveCardsToTopOfLSReserveDeck(blaster);
        scn.SkipToPhase(Phase.BATTLE);
        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        assertTrue(scn.LSCardPlayAvailable(signal, "character weapon"));
        scn.LSPlayCard(signal, "character weapon");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose card"));
        scn.LSChooseCard(blaster);
        scn.PassAllResponses();

        assertInHand(blaster);
        assertInZone(Zone.USED_PILE, signal);
        assertEquals(forceBefore - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void TheSignalStartingTakesJediEffectIntoHand() {
        var scn = GetScenario();
        var signal = scn.GetLSCard("signal");
        var insight = scn.GetLSCard("insight");

        scn.StartGame();
        assertTrue(scn.LSDecisionAvailable("Choose starting interrupt"));
        scn.LSChooseCard(signal);
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Deploy"));
        scn.LSChooseNo();
        scn.PassAllResponses();

        assertInHand(insight);
        Zone leftover = signal.getZone();
        assertTrue("Signal is placed in Reserve Deck, then starting-hand draw may take it, zone=" + leftover,
                leftover == Zone.RESERVE_DECK
                        || leftover == Zone.TOP_OF_RESERVE_DECK
                        || leftover == Zone.FORCE_PILE
                        || leftover == Zone.HAND);
    }
}
