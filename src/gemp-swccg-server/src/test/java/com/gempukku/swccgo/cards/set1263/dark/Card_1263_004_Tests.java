package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1263_004_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{ put("luke", "1_19"); put("leia", "1_17"); put("blockSix", "1261_84"); }},
                new HashMap<>() {{ put("grotto", "1263_4"); put("trooper", "1_194"); put("other", "1_194"); put("vader", "1_168");
                    put("avenger", "1263_19"); put("system", "1_282"); }},
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void GrottoWerribeeVStatsAndIdentityMatchPrintedCard() {
        var scn = scenario();
        var bp = scn.GetDSCard("grotto").getBlueprint();
        assertEquals("Grotto Werribee", bp.getTitle());
        assertTrue(bp.hasVirtualSuffix());
        assertTrue(bp.isLegacy());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(1, bp.getDestiny(), scn.epsilon);
        assertEquals(3, bp.getDeployCost(), scn.epsilon);
        assertEquals(2, bp.getPower(), scn.epsilon);
        assertEquals(4, bp.getAbility(), scn.epsilon);
        assertEquals(4, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.ALIEN); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.ALIEN); add(Icon.PILOT); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_6); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.INFORMATION_BROKER); }});
        assertTrue(Filters.Force_Sensitive_character.accepts(scn.game(), scn.GetDSCard("grotto")));
    }

    @Test
    public void GrottoWerribeeVAddsTwoToPowerOfStarshipHePilots() {
        var scn = scenario();
        scn.StartGame();
        var avenger = scn.GetDSCard("avenger");
        scn.MoveLocationToTable(scn.GetDSCard("system"));
        scn.MoveCardsToLocation(scn.GetDSCard("system"), avenger);
        scn.BoardAsPilot(avenger, scn.GetDSCard("grotto"));
        assertEquals(5, scn.GetPower(avenger));
    }

    private float immunity(VirtualTableScenario scn, String side, String card) {
        var c = side.equals("DS") ? scn.GetDSCard(card) : scn.GetLSCard(card);
        return scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), c);
    }

    private VirtualTableScenario placed() {
        var scn = scenario();
        scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("grotto"), scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("other"));
        scn.SkipToDSTurn(Phase.CONTROL);
        return scn;
    }

    @Test
    public void GrottoWerribeeVYourCharactersHereAreImmuneToAttritionLessThanThree() {
        var scn = placed();
        assertEquals(3, immunity(scn, "DS", "grotto"), scn.epsilon);
        assertEquals(3, immunity(scn, "DS", "trooper"), scn.epsilon);
    }

    @Test
    public void GrottoWerribeeVImmunityDoesNotApplyElsewhereOrToOpponent() {
        var scn = placed();
        assertEquals(0, immunity(scn, "DS", "other"), scn.epsilon);
        var grotto = scn.GetDSCard("grotto");
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(),
                com.gempukku.swccgo.logic.modifiers.ModifierType.IMMUNITY_TO_ATTRITION_LESS_THAN, scn.GetLSCard("luke"))
                .stream().noneMatch(m -> m.getSource(scn.gameState()) == grotto));
    }

    @Test
    public void GrottoWerribeeVYourCharactersHereAreImmuneToClashOfSabers() {
        var scn = placed();
        var q = scn.game().getModifiersQuerying();
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("grotto"), Title.Clash_Of_Sabers));
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("trooper"), Title.Clash_Of_Sabers));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("other"), Title.Clash_Of_Sabers));
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), scn.GetLSCard("luke"), Title.Clash_Of_Sabers));
    }

    @Test
    public void GrottoWerribeeVDoesNotLowerHigherImmunity() {
        var scn = placed();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        assertTrue(immunity(scn, "DS", "vader") >= 3);
    }

    /**
     * Light battles at Dark's starting site while a Light card's modifier makes Light draw one destiny to total power.
     * Returns whether Grotto offered his response; if offered, uses it and checks Light's total power drops.
     */
    private boolean grottoRespondsToAddedDestiny(String sourceName, boolean grottoHere) {
        var scn = scenario();
        scn.StartGame();
        var here = scn.GetDSStartingLocation();
        var away = scn.GetLSStartingLocation();
        var grotto = scn.GetDSCard("grotto");
        var source = scn.GetLSCard(sourceName);
        if (sourceName.equals("blockSix")) scn.MoveCardsToLSSideOfTable(source);
        else scn.MoveCardsToLocation(here, source);
        scn.MoveCardsToLocation(here, scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.MoveCardsToLocation(grottoHere ? here : away, grotto);
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.NumDestinyDrawsToPowerOnlyModifier(source, 1, scn.LS, Filters.any));
        scn.LSActivateForceCheat(4);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(here);
        var trace = new ArrayList<String>();
        for (int i = 0; i < 60 && scn.GetCurrentDecision() != null; i++) {
            var text = scn.GetCurrentDecision().getText();
            var decider = scn.GetDecidingPlayer();
            trace.add(decider + ":" + text);
            if (text.contains("INITIAL_ATTRITION_CALCULATED") || text.toLowerCase().contains("damage segment")) break;
            if (text.contains("battle destiny?")) { scn.PlayerChooseYes(decider); continue; }
            if (decider.equals(scn.DS) && dsMayUse(scn, grotto, "Draw two destiny")) {
                int before = scn.GetLSTotalPower();
                scn.PrepareDSDestiny(3);
                scn.PrepareDSDestiny(2);
                scn.DSUseCardAction(grotto, "Draw two destiny");
                for (int j = 0; j < 30 && scn.GetCurrentDecision() != null; j++) {
                    var t = scn.GetCurrentDecision().getText();
                    var d = scn.GetDecidingPlayer();
                    if (scn.gameState().getBattleState() == null || t.contains("INITIAL_ATTRITION_CALCULATED")) break;
                    if (d.equals(scn.DS) && t.contains("destiny") && t.startsWith("Choose 1")) {
                        scn.PlayerDecided(scn.DS, scn.DSGetCardChoices().get(0));
                        continue;
                    }
                    if (scn.GetLSTotalPower() < before) break;
                    if (t.contains("battle destiny?")) { scn.PlayerChooseYes(d); continue; }
                    scn.PlayerPass(d);
                }
                assertTrue(trace.toString(), scn.GetLSTotalPower() < before);
                return true;
            }
            scn.PlayerPass(decider);
        }
        assertTrue(trace.toString(), trace.stream().anyMatch(t -> t.contains("BATTLE_DESTINY_DRAWS") || t.contains("DESTINY_DRAWN")));
        return false;
    }

    private static boolean dsMayUse(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card, String text) {
        try {
            return scn.DSCardActionAvailable(card, text);
        } catch (IllegalStateException notACardActionDecision) {
            return false;
        }
    }

    @Test
    public void GrottoWerribeeVRespondsWhenOpponentsBlockSixCardAddsDestinyToTotalPowerHere() {
        assertTrue(grottoRespondsToAddedDestiny("blockSix", true));
    }

    @Test
    public void GrottoWerribeeVDoesNotRespondToDestinyAddedByNonBlockSixCard() {
        assertFalse(grottoRespondsToAddedDestiny("leia", true));
    }

    @Test
    public void GrottoWerribeeVDoesNotRespondToDestinyAddedElsewhere() {
        assertFalse(grottoRespondsToAddedDestiny("blockSix", false));
    }
}
