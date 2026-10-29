package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_047_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("combo", "15_47", "projection", "6_56", "second", "6_56",
                "yarna", "6_59", "luke", "1_19", "obiwan", "1_21", "chamber", "6_81", "interrupt", "1_70")), new HashMap<>());
    }
    @Test public void ProjectionOfASkywalkerAndYarnaStatsAndKeywordsAreCorrect() {
        var scn = scenario(); var card = scn.GetLSCard("combo").getBlueprint();
        assertEquals("Projection Of A Skywalker & Yarna d'al' Gargan", card.getTitle());
        assertEquals(List.of(new TitleComponent(Title.Projection_Of_A_Skywalker, Uniqueness.RESTRICTED_2),
                new TitleComponent(Title.Yarna_dal_Gargan, Uniqueness.UNIQUE)), card.getTitleComponents());
        assertEquals(Side.LIGHT, card.getSide()); assertEquals(3f, card.getDestiny(), scn.epsilon);
        assertEquals(0, card.getCredits()); assertEquals(Rarity.UNKNOWN, card.getRarity());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD, card.getExpansionSet()); assertFalse(card.isHorizontal());
        scn.BlueprintIconCheck(card, List.of(Icon.EFFECT, Icon.REFLECTIONS_GOLD));
        scn.BlueprintKeywordCheck(card, List.of()); scn.BlueprintPersonaCheck(card, List.of());
    }
    @Test public void ProjectionOfASkywalkerAndYarnaDeploysAlongsideOneProjectionAndReducesOnlyAttachedSiteDrain() {
        var scn = scenario(); var combo = scn.GetLSCard("combo"); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.AttachCardsTo(site, scn.GetLSCard("projection")); scn.MoveCardsToHand(combo);
        scn.SkipToLSTurn(Phase.DEPLOY);
        float before = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), site, scn.DS);
        scn.LSPlayCard(combo); scn.LSChooseCard(site); scn.PassAllResponses();
        assertEquals(site, combo.getAttachedTo());
        assertEquals(before - 1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), site, scn.DS), scn.epsilon);
    }
    @Test public void ProjectionOfASkywalkerAndYarnaAllowsOneStandaloneProjectionAfterCombo() {
        var scn = scenario(); scn.StartGame(); scn.AttachCardsTo(scn.GetDSStartingLocation(), scn.GetLSCard("combo"));
        scn.MoveCardsToHand(scn.GetLSCard("projection")); scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("projection")));
    }
    @Test public void ProjectionOfASkywalkerAndYarnaCannotDeployBesideTwoProjections() {
        var scn = scenario(); scn.StartGame(); scn.AttachCardsTo(scn.GetDSStartingLocation(), scn.GetLSCard("projection"), scn.GetLSCard("second"));
        scn.MoveCardsToHand(scn.GetLSCard("combo")); scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("combo")));
    }
    @Test public void ProjectionOfASkywalkerAndYarnaCannotDeployBesideStandaloneYarna() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToLSSideOfTable(scn.GetLSCard("yarna"));
        scn.MoveCardsToHand(scn.GetLSCard("combo")); scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("combo")));
    }
    @Test public void ProjectionOfASkywalkerAndYarnaProtectsNonUniqueHandCardsOnlyBelowThirteen() {
        var scn = scenario(); scn.StartGame(); scn.AttachCardsTo(scn.GetDSStartingLocation(), scn.GetLSCard("combo"));
        scn.MoveCardsToHand(scn.GetLSFiller(1), scn.GetLSCard("luke"), scn.GetLSCard("interrupt"), scn.GetLSCard("yarna"));
        var modifiers = scn.game().getModifiersQuerying();
        assertTrue(scn.GetLSHandCount() < 13);
        assertTrue(modifiers.isImmuneToCardTitle(scn.gameState(), scn.GetLSFiller(1), Title.Monnok));
        for (String name : List.of("luke", "interrupt", "yarna"))
            assertFalse(modifiers.isImmuneToCardTitle(scn.gameState(), scn.GetLSCard(name), Title.Monnok));
        for (int i = 2; scn.GetLSHandCount() < 13; i++) scn.MoveCardsToHand(scn.GetLSFiller(i));
        assertEquals(13, scn.GetLSHandCount());
        assertFalse(modifiers.isImmuneToCardTitle(scn.gameState(), scn.GetLSFiller(1), Title.Monnok));
    }
    @Test public void ProjectionOfASkywalkerAndYarnaRequiresTwoOccupiedBattlegroundsForAlterImmunity() {
        var scn = scenario(); var combo = scn.GetLSCard("combo"); scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("chamber")); scn.AttachCardsTo(scn.GetDSStartingLocation(), combo);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("luke"));
        var modifiers = scn.game().getModifiersQuerying();
        assertFalse(modifiers.isImmuneToCardTitle(scn.gameState(), combo, Title.Alter));
        scn.MoveCardsToLocation(scn.GetLSCard("chamber"), scn.GetLSCard("obiwan"));
        assertTrue(modifiers.isImmuneToCardTitle(scn.gameState(), combo, Title.Alter));
    }
}
