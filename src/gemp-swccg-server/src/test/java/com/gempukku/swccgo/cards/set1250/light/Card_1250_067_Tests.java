package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class Card_1250_067_Tests {
    private VirtualTableScenario scenario(boolean withCredits) {
        StartingSetup start = new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                return StartingSetup.DefaultLSGroundLocation.Cards();
            }
            @Override public void Setup(VirtualTableScenario s) {
                if (withCredits) s.MoveCardsToLSSideOfTable(s.GetLSCard("credits"));
                StartingSetup.DefaultLSGroundLocation.Setup(s);
            }
        };
        return new VirtualTableScenario(new HashMap<>() {{
            put("howl", "1250_67"); put("credits", "12_42");
        }}, new HashMap<>(), 35, 35, start, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void KraytDragonHowlMatchesFinal67EPrinting() {
        var s = scenario(false); var bp = s.GetLSCard("howl").getBlueprint();
        assertEquals("Krayt Dragon Howl", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(4, bp.getDestiny(), s.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(CardSubtype.USED_OR_STARTING, bp.getCardSubtype());
        s.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.INTERRUPT)));
        s.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.LEGACY_BLOCK_5)));
        s.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void WithoutCreditsHowlIsNotOfferedAsAStartingInterrupt() {
        var s = scenario(false); s.StartGame(false);
        assertFalse(s.LSDecisionAvailable("Choose starting interrupt"));
        assertEquals(8, s.GetLSHandCount());
    }

    @Test
    public void CreditsMakesHowlAvailableAtTheActualStartingInterruptDecision() {
        var s = scenario(true); s.StartGame(false);
        assertTrue(s.LSDecisionAvailable("Choose starting interrupt"));
        assertTrue(s.LSGetBPChoices().contains(s.GetLSCard("howl").getBlueprintId(true)));
        s.LSPass();
        assertEquals(8, s.GetLSHandCount());
    }

    @Test
    public void UsedModeActivatesExactlyOneForceWithoutObiWanOrCredits() {
        var s = scenario(false); s.StartGame(); s.MoveCardsToHand(s.GetLSCard("howl"));
        s.SkipToLSTurn(Phase.CONTROL);
        var top = s.GetTopOfLSReserveDeck();
        int force = s.GetLSForcePileCount(); int reserve = s.GetLSReserveDeckCount();
        assertTrue(s.LSCardPlayAvailable(s.GetLSCard("howl"), "Activate 1 Force"));
        s.LSPlayCard(s.GetLSCard("howl"), "Activate 1 Force"); s.PassAllResponses();
        assertEquals(force + 1, s.GetLSForcePileCount()); assertEquals(reserve - 1, s.GetLSReserveDeckCount());
        assertTrue(s.GetLSForcePile().contains(top));
        assertTrue(s.GetLSUsedPile().contains(s.GetLSCard("howl")));
    }

    @Test
    public void UsedModeCannotActivateFromEmptyReserve() {
        var s = scenario(false); s.StartGame(); s.MoveCardsToHand(s.GetLSCard("howl"));
        for (var card : new ArrayList<>(s.GetLSReserveDeck())) s.MoveCardsToTopOfLSForcePile((PhysicalCardImpl) card);
        s.SkipToLSTurn(Phase.CONTROL);
        assertEquals(0, s.GetLSReserveDeckCount());
        assertFalse(s.LSCardPlayAvailable(s.GetLSCard("howl"), "Activate 1 Force"));
    }
}
