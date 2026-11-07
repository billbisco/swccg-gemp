package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1261_063_Tests {
    private static final String BOTTOM = "Place under Reserve Deck";
    private static final String REFLECTION = "Deploy Reflection from Reserve Deck";
    private static final String DOWNLOAD = "Deploy card to Dagobah from Reserve Deck";

    private VirtualTableScenario scenario(boolean training) {
        var objective = new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                return new HashMap<>(Map.of("objective", "7_138", "dagobah", "4_84"));
            }
            @Override public void Setup(VirtualTableScenario scn) {}
        };
        var scn = new VirtualTableScenario(new HashMap<>(Map.of(
                "daughter", "1261_63", "reflection", "4_33", "other", "1_66", "luke", "9_24",
                "saber", "9_90", "jungle", "4_86")),
                new HashMap<>(Map.of("vader", "1_168")), 40, 40,
                training ? objective : StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "legacy");
        scn.StartGame();
        return scn;
    }

    @Test public void daughterUsesFinalPrintedStatsAndPersona() {
        var scn = scenario(false); var b = scn.GetLSCard("daughter").getBlueprint();
        assertEquals("Daughter Of Skywalker", b.getTitle()); assertEquals(1, b.getDestiny(), scn.epsilon);
        assertEquals(5, b.getDeployCost(), scn.epsilon); assertEquals(4, b.getPower(), scn.epsilon);
        assertEquals(5, b.getAbility(), scn.epsilon); assertEquals(8, b.getForfeit(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(Rarity.V, b.getRarity()); assertTrue(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.REBEL)));
        scn.BlueprintPersonaCheck(b, new ArrayList<>(List.of(Persona.LEIA)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.REBEL, Icon.PILOT, Icon.WARRIOR, Icon.ENDOR, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(List.of(Keyword.SCOUT, Keyword.LEADER, Keyword.FEMALE)));
    }

    @Test public void actualHandActionPutsThisCopyOnBottomWithoutShufflingOrPaying() {
        var scn = scenario(false); var leia = scn.GetLSCard("daughter"); var other = scn.GetLSCard("other");
        scn.MoveCardsToLSHand(leia, other);
        scn.SkipToLSTurn(Phase.CONTROL);
        var top = scn.GetTopOfLSReserveDeck(); int force = scn.GetLSForcePileCount();
        scn.gameState().markCardsRevealedFromPile(List.of(top));
        scn.LSUseCardAction(leia, BOTTOM);
        assertFalse(scn.LSHasCardChoiceAvailable(other));
        if (scn.LSHasCardChoiceAvailable(leia)) scn.LSChooseCard(leia);
        scn.PassAllResponses();
        assertEquals(leia, scn.GetLSReserveDeck().getLast()); assertEquals(top, scn.GetTopOfLSReserveDeck());
        assertEquals(force, scn.GetLSForcePileCount()); assertTrue(scn.gameState().isCardRevealedFromPile(top));
        assertInZone(Zone.HAND, other);
    }

    @Test public void activeDaughterActuallyDownloadsReflectionAndReshufflesOutsideDeploy() {
        var scn = scenario(false); var leia = scn.GetLSCard("daughter"); var reflection = scn.GetLSCard("reflection");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), leia);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains(BOTTOM)));
        var marker = scn.GetLSCard("other");
        scn.MoveCardsToTopOfLSReserveDeck(reflection, marker);
        scn.gameState().markCardsRevealedFromPile(List.of(marker));
        scn.LSUseCardAction(leia, REFLECTION);
        scn.LSChooseCard(reflection);
        if (scn.LSHasCardChoiceAvailable(leia)) scn.LSChooseCard(leia);
        scn.PassAllResponses();
        assertEquals(leia, reflection.getAttachedTo());
        assertFalse(scn.gameState().isCardRevealedFromPile(marker));
    }

    @Test public void originalObjectiveActuallyDeploysLegacyDaughterInsteadOfLukeForRemainderOfGame() {
        var scn = scenario(true); var objective = scn.GetLSCard("objective");
        var daughter = scn.GetLSCard("daughter"); var jungle = scn.GetLSCard("jungle");
        assertInZone(Zone.SIDE_OF_TABLE, objective);
        scn.MoveLocationToTable(jungle);
        scn.SkipToLSTurn(Phase.DEPLOY); scn.EnsureLSForcePile(8);
        scn.MoveCardsToTopOfLSReserveDeck(daughter);
        assertFalse(GameConditions.hasGameTextModification(scn.game(), objective,
                ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE));
        scn.LSUseCardAction(objective, DOWNLOAD);
        scn.LSChooseCard(daughter);
        if (scn.LSHasCardChoiceAvailable(jungle)) scn.LSChooseCard(jungle);
        scn.PassAllResponses();
        assertAtLocation(jungle, daughter);
        scn.MoveCardsToTopOfLSLostPile(daughter);
        assertTrue(GameConditions.hasGameTextModification(scn.game(), objective,
                ModifyGameTextType.MIND_WHAT_YOU_HAVE_LEARNED_SAVE_YOU_IT_CAN__TARGETS_LEIA_INSTEAD_OF_LUKE));
    }

    private void attrition(int amount, boolean immune) {
        var scn = scenario(false); var leia = scn.GetLSCard("daughter"); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, leia, scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("saber"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site); scn.PassAllResponses();
        scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(amount - 1);
        for (int i = 0; i < 4; i++) { scn.SkipToEndOfPowerSegment(true); scn.PassDestinyDrawResponses(); }
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED"); scn.PassDamageSegmentActions();
        assertEquals(amount, scn.GetUnpaidLSAttrition()); assertEquals(0, scn.GetUnpaidLSBattleDamage());
        assertEquals(immune, scn.LSDecisionAvailable("(if desired)"));
        if (immune) { scn.LSChooseCards(); assertAtLocation(site, leia); }
        else { scn.LSPayAttritionFromCardInPlay(leia); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, leia); }
    }

    @Test public void daughterSurvivesActualAttritionThree() { attrition(3, true); }
    @Test public void daughterMustForfeitAtActualAttritionFour() { attrition(4, false); }
}
