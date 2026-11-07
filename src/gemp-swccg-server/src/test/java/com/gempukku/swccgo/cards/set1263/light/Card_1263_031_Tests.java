package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1263_031_Tests {
    private static final String IMMEDIATE = "Take Phylo Gandish or an Immediate Effect";
    private static final String FREE = "Take an Effect that deploys for free";
    private static final String RETURN = "Retrieve Return Of The Jedi into hand";

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("fault", "1263_31"); put("immediate", "9_44"); put("traffic", "1_66"); put("paid", "2_32");
            put("oldPhylo", "12_20"); put("xwing", "1_146");
        }}, new HashMap<>() {{ put("vader", "1_168"); put("trooper", "1_194"); }},
                60, 60, StartingSetup.LSStartingLocation("1_129"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test public void FaultUsesFinalSixDestinyLegacyPrinting() {
        var scn = scenario(); var b = scn.GetLSCard("fault").getBlueprint();
        assertEquals("It's Not My Fault!", b.getTitle()); assertEquals(6, b.getDestiny(), scn.epsilon);
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertTrue(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity()); assertEquals(CardSubtype.USED, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.INTERRUPT, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }
    private VirtualTableScenario ready(int force, String darkJediZone) {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToLSHand(scn.GetLSCard("fault"));
        if ("table".equals(darkJediZone)) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        if ("hand".equals(darkJediZone)) scn.MoveCardsToDSHand(scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("trooper")); scn.SkipToLSTurn(Phase.CONTROL);
        while (scn.GetLSForcePileCount() > 0) scn.MoveCardsToTopOfLSReserveDeck(scn.GetTopOfLSForcePile());
        if (force > 0) scn.LSActivateForceCheat(force);
        for (String key : new String[]{"immediate", "traffic", "paid", "oldPhylo", "xwing"}) scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard(key));
        scn.LSPass(); scn.DSPass();
        return scn;
    }
    private void assertReshuffled(VirtualTableScenario scn) {
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("xwing")); assertFalse(scn.gameState().isCardRevealedFromPile(scn.GetLSCard("xwing")));
    }
    private void markReserve(VirtualTableScenario scn) {
        scn.gameState().markCardsRevealedFromPile(Collections.singletonList(scn.GetLSCard("xwing")));
        assertTrue(scn.gameState().isCardRevealedFromPile(scn.GetLSCard("xwing")));
    }
    @Test public void FaultPaysOneToTakeAnImmediateEffectAndReshuffle() {
        var scn = ready(1, "hand"); markReserve(scn); scn.LSPlayCard(scn.GetLSCard("fault"), IMMEDIATE); scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("immediate")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("oldPhylo"))); assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("traffic")));
        scn.LSChooseCard(scn.GetLSCard("immediate")); scn.PassAllResponses();
        assertInHand(scn.GetLSCard("immediate")); assertInZone(Zone.USED_PILE, scn.GetLSCard("fault")); assertEquals(0, scn.GetLSForcePileCount()); assertReshuffled(scn);
    }
    @Test public void FaultPaysTwoToTakeFreeEffectAndReshuffle() {
        var scn = ready(2, "hand"); markReserve(scn); scn.LSPlayCard(scn.GetLSCard("fault"), FREE); scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("traffic")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("paid"))); assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("xwing")));
        scn.LSChooseCard(scn.GetLSCard("traffic")); scn.PassAllResponses();
        assertInHand(scn.GetLSCard("traffic")); assertInZone(Zone.USED_PILE, scn.GetLSCard("fault")); assertEquals(0, scn.GetLSForcePileCount()); assertReshuffled(scn);
    }
    @Test public void FaultZeroForceCannotUseEitherPaidSearchMode() {
        var scn = ready(0, "hand"); assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("fault"), IMMEDIATE));
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("fault"), FREE));
    }
    @Test public void FaultOneForceCannotUseTwoForceMode() {
        var scn = ready(1, "hand"); assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("fault"), IMMEDIATE));
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("fault"), FREE));
    }
    @Test public void FaultOffersFreeRetrievalWithActualDarkJediOnTable() {
        var scn = ready(0, "table"); assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("fault"), RETURN));
    }
    @Test public void FaultDoesNotOfferRetrievalWithDarkJediOnlyInHandAndNonJediOnTable() {
        var scn = ready(2, "hand"); assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("fault"), IMMEDIATE));
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("fault"), RETURN));
    }
}
