package com.gempukku.swccgo.cards.set1282.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_005_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("r2", "2_14"); // R2-D2 (Artoo-Detoo)
                }},
                new HashMap<>() {{
                    put("crumb", "1282_5");
                    put("jabba", "6_109"); // Jabba The Hutt
                    put("chamber", "6_162"); // Jabba's Palace: Audience Chamber
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

    private static final String CRUMB_ACTION = "Cancel and redraw destiny";

    private static boolean dsHasAction(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        return scn.DSAnyDecisionsAvailable() && !scn.GetDSAvailableActions().isEmpty() && scn.DSCardActionAvailable(card, text);
    }

    /** Passes responses until the condition holds; Light Side draws its battle destiny, Dark Side declines. */
    private static void passUntil(VirtualTableScenario scn, BooleanSupplier done) {
        for (int i = 0; i < 40; ++i) {
            if (done.getAsBoolean())
                return;
            if (scn.LSDecisionAvailable("Do you want to draw"))
                scn.LSChooseYes();
            else if (scn.DSDecisionAvailable("Do you want to draw"))
                scn.DSChooseNo();
            else if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
            else
                return;
        }
    }

    @Test
    public void SalaciousCrumbStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("crumb").getBlueprint();
        assertEquals(Title.Salacious_Crumb, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(0, card.getDeployCost(), scn.epsilon);
        assertEquals(0, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(Species.KOWAKIAN, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.JABBAS_PALACE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private PhysicalCardImpl battleAt(VirtualTableScenario scn, PhysicalCardImpl site, boolean withR2) {
        scn.MoveCardsToLocation(site, scn.GetDSCard("jabba"), scn.GetDSCard("crumb"), scn.GetLSCard("luke"), scn.GetLSCard("leia"));
        if (withR2) {
            scn.MoveCardsToLocation(site, scn.GetLSCard("r2"));
        }
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(4);
        var redraw = scn.GetLSDestiny(2);
        var first = scn.GetLSDestiny(5);
        scn.MoveCardsToTopOfLSReserveDeck(redraw);
        scn.MoveCardsToTopOfLSReserveDeck(first);
        scn.LSInitiateBattle(site);
        return first;
    }

    @Test
    public void SalaciousCrumbWithJabbaAtAudienceChamberCancelsAndRedrawsOpponentsBattleDestiny() {
        var scn = GetScenario();
        var crumb = scn.GetDSCard("crumb");
        var chamber = scn.GetDSCard("chamber");
        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        var first = battleAt(scn, chamber, false);
        passUntil(scn, () -> dsHasAction(scn, crumb, CRUMB_ACTION) || scn.IsReachedDamageSegment());

        assertTrue(dsHasAction(scn, crumb, CRUMB_ACTION));
        scn.DSUseCardAction(crumb, CRUMB_ACTION);
        passUntil(scn, () -> scn.LSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER") || scn.DSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER"));

        assertInZone(Zone.USED_PILE, crumb);
        assertInZone(Zone.LOST_PILE, first);
        assertEquals(scn.LS, first.getZoneOwner());
        // redrawn destiny (2) replaces the canceled one (5)
        assertEquals(2, scn.GetLSTotalDestiny());
    }

    @Test
    public void SalaciousCrumbMayNotCancelWhileR2D2Present() {
        var scn = GetScenario();
        var crumb = scn.GetDSCard("crumb");
        var chamber = scn.GetDSCard("chamber");
        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        battleAt(scn, chamber, true);
        passUntil(scn, () -> dsHasAction(scn, crumb, CRUMB_ACTION) || scn.LSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER") || scn.DSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER"));
        assertFalse(dsHasAction(scn, crumb, CRUMB_ACTION));
        assertInZone(Zone.AT_LOCATION, crumb);
    }

    @Test
    public void SalaciousCrumbMayNotCancelAwayFromAudienceChamber() {
        var scn = GetScenario();
        var crumb = scn.GetDSCard("crumb");
        scn.StartGame();
        battleAt(scn, scn.GetDSStartingLocation(), false);
        passUntil(scn, () -> dsHasAction(scn, crumb, CRUMB_ACTION) || scn.LSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER") || scn.DSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER"));
        assertFalse(dsHasAction(scn, crumb, CRUMB_ACTION));
        assertInZone(Zone.AT_LOCATION, crumb);
    }
}
