package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1263_012_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); }},
                new HashMap<>() {{ put("oap", "1263_12"); put("tentacle", "2_127"); put("grotto", "1263_4"); put("oldgrotto", "12_107");
                    put("drop", "1262_8"); put("faith", "1_217"); put("trooper", "1_194"); }},
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void OperationalAsPlannedVStatsAndIdentityMatchPrintedCard() {
        var scn = scenario();
        var bp = scn.GetDSCard("oap").getBlueprint();
        assertEquals("Operational As Planned", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertTrue(bp.isLegacy());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(5, bp.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, bp.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.INTERRUPT); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.INTERRUPT); add(Icon.DEATH_STAR_II); add(Icon.LEGACY_BLOCK_6); }});
    }

    private boolean selectable(VirtualTableScenario scn, String blueprint) {
        String[] cards = scn.GetADParam(scn.DS, "blueprintId");
        String[] selectable = scn.GetADParam(scn.DS, "selectable");
        if (cards == null) return false;
        for (int i = 0; i < cards.length; i++) {
            if (blueprint.equals(cards[i]) && (selectable == null || Boolean.parseBoolean(selectable[i]))) return true;
        }
        return false;
    }

    private VirtualTableScenario ready(int force) {
        var scn = scenario();
        scn.StartGame();
        scn.MoveCardsToDSHand(scn.GetDSCard("oap"));
        scn.SkipToDSTurn(Phase.CONTROL);
        while (scn.GetDSForcePileCount() > 0) scn.MoveCardsToTopOfDSReserveDeck(scn.GetTopOfDSForcePile());
        var named = new com.gempukku.swccgo.game.PhysicalCardImpl[]{scn.GetDSCard("tentacle"), scn.GetDSCard("grotto"), scn.GetDSCard("oldgrotto"),
                scn.GetDSCard("drop"), scn.GetDSCard("faith"), scn.GetDSCard("trooper")};
        scn.MoveCardsToBottomOfDSReserveDeck(named);
        scn.DSActivateForceCheat(force);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("tentacle"), scn.GetDSCard("grotto"), scn.GetDSCard("oldgrotto"),
                scn.GetDSCard("drop"), scn.GetDSCard("faith"), scn.GetDSCard("trooper"));
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(force, scn.GetDSForcePileCount());
        return scn;
    }

    @Test
    public void OperationalAsPlannedVUsesOneForceToTakeGrottoWerribeeVOrImmediateEffect() {
        var scn = ready(1);
        var oap = scn.GetDSCard("oap");
        assertTrue(scn.DSCardPlayAvailable(oap, "Grotto Werribee or an Immediate Effect"));
        assertFalse(scn.DSCardPlayAvailable(oap, "Effect that deploys for free"));
        scn.DSPlayCard(oap, "Grotto Werribee or an Immediate Effect");
        scn.PassAllResponses();
        assertTrue(selectable(scn, "2_127"));
        assertTrue(selectable(scn, "1263_4"));
        assertFalse(selectable(scn, "12_107"));
        assertFalse(selectable(scn, "1262_8"));
        assertFalse(selectable(scn, "1_194"));
        scn.DSChooseCard(scn.GetDSCard("tentacle"));
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard("tentacle"));
        assertEquals(0, scn.GetDSForcePileCount());
        assertInZone(Zone.USED_PILE, oap);
    }

    @Test
    public void OperationalAsPlannedVUsesTwoForceToTakeEffectThatDeploysForFree() {
        var scn = ready(2);
        var oap = scn.GetDSCard("oap");
        assertTrue(scn.DSCardPlayAvailable(oap, "Effect that deploys for free"));
        scn.DSPlayCard(oap, "Effect that deploys for free");
        scn.PassAllResponses();
        assertTrue(selectable(scn, "1262_8"));
        assertFalse(selectable(scn, "1_217"));
        assertFalse(selectable(scn, "1_194"));
        scn.DSChooseCard(scn.GetDSCard("drop"));
        scn.PassAllResponses();
        assertInZone(Zone.HAND, scn.GetDSCard("drop"));
        assertEquals(0, scn.GetDSForcePileCount());
    }
}
