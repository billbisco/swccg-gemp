package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1220_072_Tests {
    private static final String UPLOAD = "Take a Hoth card into hand";
    private static final StartingSetup START = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>(Map.of("objective", "1220_72"));
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            for (String key : List.of("objective", "hoth", "generators", "decree")) {
                if (scn.DSHasCardChoiceAvailable(scn.GetDSCard(key)))
                    scn.DSChooseCard(scn.GetDSCard(key));
                if (key.equals("hoth")) {
                    var ice = scn.GetDSReserveDeck().stream().filter(c -> Filters.Ice_Plains.accepts(scn.game(), c)
                            && c.getBlueprint().getIconCount(Icon.LEGACY_BLOCK_6) > 0).findFirst();
                    if (ice.isPresent() && scn.DSHasCardChoiceAvailable((PhysicalCardImpl) ice.get()))
                        scn.DSChooseCard((PhysicalCardImpl) ice.get());
                }
            }
        }
    };

    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("revolution", "1_62", "rebel", "1_19")),
                new HashMap<>(Map.ofEntries(Map.entry("hoth", "3_143"), Map.entry("generators", "1282_8"),
                        Map.entry("decipherIce", "3_148"), Map.entry("decree", "1220_38"), Map.entry("blockade", "1220_31"),
                        Map.entry("cannon", "3_158"), Map.entry("landing", "11_77"), Map.entry("landing8", "1282_3"),
                        Map.entry("garrison", "104_7"), Map.entry("marker", "3_149"), Map.entry("vader", "1_168"))),
                30, 30, StartingSetup.DefaultLSGroundLocation, START,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private PhysicalCardImpl setup(VirtualTableScenario scn) {
        // B19 owns the real 1261_52; this test fixture can be removed once that dependency lands.
        var ice = MissionTestSupport.putInHand(scn, 9072, scn.DS,
                new AbstractSite(Side.DARK, Title.Ice_Plains, Title.Hoth, Uniqueness.UNIQUE,
                        ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V) {
                    {
                        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.HOTH);
                        addKeywords(Keyword.MARKER_5);
                    }
                });
        scn.MoveCardsToTopOfDSReserveDeck(ice);
        scn.StartGame();
        assertTrue("The real Objective starting sequence must finish", scn.GetDSCard("objective").getZone().isInPlay());
        return ice;
    }

    @Test
    public void printedObjectiveIdentity() {
        var scn = scenario();
        var bp = scn.GetDSCard("objective").getBlueprint();
        assertEquals("Imperial Occupation", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(0, bp.getDestiny(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.OBJECTIVE)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.OBJECTIVE, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_2)));
        var back = new Card1220_072_BACK();
        assertEquals("Imperial Control", back.getTitle());
        assertEquals(7, back.getDestiny(), scn.epsilon);
    }

    @Test(timeout = 10000)
    public void realPregameSelectsTheExactBlockSixAndEightPrintings() {
        var scn = scenario();
        var ice = setup(scn);
        assertEquals(Zone.LOCATIONS, scn.GetDSCard("hoth").getZone());
        assertEquals(Zone.LOCATIONS, ice.getZone());
        assertEquals(Zone.LOCATIONS, scn.GetDSCard("generators").getZone());
        assertFalse(scn.GetDSCard("decipherIce").getZone().isInPlay());
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("garrison"), Title.Sense));
    }

    @Test(timeout = 10000)
    public void frontUploadsARealCardOnceDuringOwnTurnOutsideControl() {
        var scn = scenario();
        setup(scn);
        var objective = scn.GetDSCard("objective");
        var cannon = scn.GetDSCard("cannon");
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(cannon);
        assertTrue(scn.DSCardActionAvailable(objective, UPLOAD));
        scn.DSUseCardAction(objective, UPLOAD);
        if (scn.DSHasCardChoiceAvailable(cannon))
            scn.DSChooseCard(cannon);
        scn.PassAllResponses();
        assertTrue(scn.GetDSHand().contains(cannon));
        if (scn.AwaitingLSDeployPhaseActions())
            scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(objective, UPLOAD));
    }

    @Test(timeout = 10000)
    public void bothConditionsFlipObjectiveAndRemovingBlockadeFlipsItBack() {
        var scn = scenario();
        setup(scn);
        var obj = scn.GetDSCard("objective");
        var generators = scn.GetDSCard("generators");
        var hoth = scn.GetDSCard("hoth");
        var blockade = scn.GetDSCard("blockade");
        scn.gameState().flipBlownAwayLocations(scn.game(), List.of(generators));
        scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(obj.isFlipped());
        scn.AttachCardsTo(hoth, blockade);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(obj.isFlipped());
        assertEquals("Imperial Control", obj.getTitle());
        scn.MoveCardsToTopOfDSLostPile(blockade);
        scn.SkipToPhase(Phase.BATTLE);
        assertFalse(obj.isFlipped());
    }
}
