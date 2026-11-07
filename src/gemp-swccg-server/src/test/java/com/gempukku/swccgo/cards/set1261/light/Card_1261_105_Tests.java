package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.*;

public class Card_1261_105_Tests {
    private static final String TRANSPORT = "'Transport' characters";

    private VirtualTableScenario scenario() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "combo", "1261_105", "luke", "1_19", "leia", "1_17", "jungle", "1_137", "interior", "1_128")),
                new HashMap<>(Map.of("dining", "5_168", "vader", "1_168")),
                40, 40, StartingSetup.LSStartingLocation("5_80"), StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        return scn;
    }

    @Test public void firstAidUsesTheExactFinalLegacyComboPrinting() {
        var scn = scenario(); var b = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Odin Nesloor & First Aid", b.getTitle());
        assertEquals(4, b.getDestiny(), scn.epsilon); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        assertEquals(CardSubtype.USED, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.INTERRUPT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.INTERRUPT, Icon.SPECIAL_EDITION, Icon.EPISODE_I, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }

    private VirtualTableScenario transport(String destination, int destiny, boolean both, boolean fund) {
        var scn = scenario(); var source = scn.GetLSStartingLocation();
        var first = scn.GetLSCard("luke"); var second = scn.GetLSCard("leia");
        var opposing = scn.GetDSCard("vader");
        scn.MoveLocationToTable(scn.GetLSCard("jungle"));
        scn.MoveLocationToTable(scn.GetLSCard("interior"));
        scn.MoveLocationToTable(scn.GetDSCard("dining"));
        scn.MoveCardsToLocation(source, first, second, opposing);
        var combo = scn.GetLSCard("combo");
        scn.MoveCardsToLSHand(combo); scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(TRANSPORT)));
        scn.SkipToPhase(Phase.MOVE);
        if (fund) scn.EnsureLSForcePile(5);
        else scn.LSUseForceCheat(scn.GetLSForcePileCount());
        scn.PrepareLSDestiny(destiny);
        scn.LSPlayCard(combo, TRANSPORT);
        if (scn.LSHasCardChoiceAvailable(source)) scn.LSChooseCard(source);
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetDSCard("dining")));
        scn.LSChooseCard(scn.GetLSCard(destination));
        assertFalse(scn.LSHasCardChoiceAvailable(opposing));
        if (both) scn.LSChooseCards(first, second); else scn.LSChooseCard(first);
        scn.PassAllResponses(); scn.PassDestinyDrawResponses();
        return scn;
    }

    private void resolve(VirtualTableScenario scn, boolean pay) {
        assertTrue(scn.LSDecisionAvailable("transport"));
        if (pay) scn.LSChooseYes(); else scn.LSChooseNo();
        scn.PassForceUseResponses(); scn.PassAllResponses();
    }

    @Test public void paysActualDestinyToTransportBothFromAnInteriorSiteToAnExteriorSite() {
        var scn = transport("jungle", 2, true, true);
        int before = scn.GetLSForcePileCount();
        resolve(scn, true);
        assertEquals(before - 2, scn.GetLSForcePileCount());
        assertAtLocation(scn.GetLSCard("jungle"), scn.GetLSCard("luke"), scn.GetLSCard("leia"));
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("combo")));
    }

    @Test public void zeroDestinyMayTransportOneForFreeToAnInteriorBattleground() {
        var scn = transport("interior", 0, false, true);
        int before = scn.GetLSForcePileCount();
        resolve(scn, true);
        assertEquals(before, scn.GetLSForcePileCount());
        assertAtLocation(scn.GetLSCard("interior"), scn.GetLSCard("luke"));
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("leia"));
        assertTrue(scn.GetLSUsedPile().contains(scn.GetLSCard("combo")));
    }

    @Test public void decliningAnAffordableTransportMakesOnlyTheInterruptLost() {
        var scn = transport("jungle", 2, true, true);
        int before = scn.GetLSForcePileCount();
        resolve(scn, false);
        assertEquals(before, scn.GetLSForcePileCount());
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"), scn.GetLSCard("leia"));
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("combo")));
    }

    @Test public void anUnaffordableTransportMakesTheInterruptLostWithoutMovingAnyone() {
        var scn = transport("jungle", 2, false, false);
        scn.PassAllResponses();
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"), scn.GetLSCard("leia"));
        assertTrue(scn.GetLSLostPile().contains(scn.GetLSCard("combo")));
    }
}
