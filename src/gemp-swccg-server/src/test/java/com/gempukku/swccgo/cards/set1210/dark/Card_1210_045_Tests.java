package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1210_045_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("corvette", "1_140");
                }},
                new HashMap<>() {{
                    put("maneuver", "1210_45");
                    put("alderaan", "1_281");
                    put("scout", "1_305");
                }},
                10, 10,
                StartingSetup.LSStartingLocation("1_127"),
                StartingSetup.DSStartingLocation("2_143"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ManeuverCheckStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("maneuver").getBlueprint();

        assertEquals("Maneuver Check", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ManeuverCheckDeploysOnTable() {
        var scn = GetScenario();
        var maneuver = scn.GetDSCard("maneuver");

        scn.StartGame();
        scn.MoveCardsToDSHand(maneuver);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(maneuver));
        scn.DSPlayCard(maneuver);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, maneuver);
    }

    @Test
    public void ManeuverCheckTreatsATargetedSystemAsSubjugatedIfAlderaanIsBlownAway() {
        var scn = GetScenario();
        var maneuver = scn.GetDSCard("maneuver");
        var alderaan = scn.GetDSCard("alderaan");
        var tatooine = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(maneuver);
        scn.MoveLocationToTable(alderaan);
        scn.game().getModifiersQuerying().blownAway(alderaan);
        alderaan.setBlownAway(true);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(maneuver, "Target a system as Subjugated"));
        scn.DSUseCardAction(maneuver, "Target a system as Subjugated");
        if (scn.DSHasCardChoiceAvailable(tatooine)) {
            scn.DSChooseCard(tatooine);
        }
        scn.PassAllResponses();

        assertEquals("Tatooine", scn.gameState().getSubjugatedPlanet());
    }

    @Test
    public void ManeuverCheckDoesNotSubjugateASystemIfOpponentHasACapitalStarshipThere() {
        var scn = GetScenario();
        var maneuver = scn.GetDSCard("maneuver");
        var alderaan = scn.GetDSCard("alderaan");
        var corvette = scn.GetLSCard("corvette");
        var tatooine = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(maneuver);
        scn.MoveLocationToTable(alderaan);
        scn.MoveCardsToLocation(tatooine, corvette);
        scn.game().getModifiersQuerying().blownAway(alderaan);
        alderaan.setBlownAway(true);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(maneuver, "Target a system as Subjugated"));
        scn.DSUseCardAction(maneuver, "Target a system as Subjugated");
        if (scn.DSHasCardChoiceAvailable(tatooine)) {
            scn.DSChooseCard(tatooine);
        }
        scn.PassAllResponses();

        assertNull(scn.gameState().getSubjugatedPlanet());
    }

    @Test
    public void ManeuverCheckPreventsStarshipsFromMovingTheTurnTheyDeployToDeathStar() {
        var scn = GetScenario();
        var maneuver = scn.GetDSCard("maneuver");
        var scout = scn.GetDSCard("scout");
        var deathStar = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(maneuver);
        scn.MoveCardsToDSHand(scout);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(2);
        assertTrue(scn.DSCardPlayAvailable(scout));
        scn.DSPlayCard(scout);
        if (scn.DSHasCardChoiceAvailable(deathStar)) {
            scn.DSChooseCard(deathStar);
        }
        scn.PassAllResponses();
        assertAtLocation(deathStar, scout);

        scn.SkipToPhase(Phase.MOVE);
        assertTrue(scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), scout));
    }
}
