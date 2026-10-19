package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAddBattleDestinyDrawsModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_034_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("bravado", "1281_34");
                    put("lando", "601_50");
                    put("leia", "1_17");
                    put("han", "1_11");
                    put("ladyLuck", "601_49");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ScoundrelsBravadoStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("bravado").getBlueprint();

        assertEquals("Scoundrel's Bravado", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.SCOUNDREL);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ScoundrelsBravadoCannotDeployFromHand() {
        var scn = GetScenario();
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        scn.MoveCardsToLSHand(bravado);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSDeployAvailable(bravado));
        assertEquals(Zone.HAND, bravado.getZone());
    }

    @Test
    public void ScoundrelsBravadoAddsTwoDefenseValueAndImmunityToAttritionLessThanFive() {
        var scn = GetScenario();
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        int defense = scn.GetDefense(lando);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), lando), scn.epsilon);

        scn.AttachCardsTo(lando, bravado);
        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(scn.IsAttachedTo(lando, bravado));
        assertEquals(defense + 2, scn.GetDefense(lando));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), lando), scn.epsilon);
    }

    @Test
    public void ScoundrelsBravadoAddsTwoToExistingImmunityOnStarshipTheyPilot() {
        var scn = GetScenario();
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var ladyLuck = scn.GetLSCard("ladyLuck");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, ladyLuck);
        scn.BoardAsPilot(ladyLuck, lando);
        int defense = scn.GetDefense(ladyLuck);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ladyLuck), scn.epsilon);

        scn.AttachCardsTo(lando, bravado);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(defense + 2, scn.GetDefense(ladyLuck));
        assertEquals(7, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ladyLuck), scn.epsilon);
    }

    @Test
    public void ScoundrelsBravadoLosesOneForceToAddABattleDestinyAndCapsAtTwo() {
        var scn = GetScenario();
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, leia, trooper);
        scn.AttachCardsTo(lando, bravado);
        scn.ApplyAdHocModifier(new AddsBattleDestinyModifier(leia, 2, scn.LS, true));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.EnsureLSForcePile(2);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(bravado, "Add a battle destiny"));
        scn.LSUseCardAction(bravado, "Add a battle destiny");
        completeBravadoForceLoss(scn);
        assertTrue("expected to still be in battle after Bravado, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                scn.gameState().isDuringBattle());
        assertEquals(force - 1, scn.GetLSForcePileCount());
        assertEquals(5, scn.GetLSBattleDestinyCount());
        if (scn.LSGetDecision() == null && scn.DSGetDecision() != null) {
            scn.DSPass();
        }
        assertTrue("once-per-turn must be checked in a card-action window. ls=" + scn.LSGetDecision()
                        + " ds=" + scn.DSGetDecision(),
                scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(bravado, "Add a battle destiny"));
        scn.SkipToPowerSegment();
        assertTrue("expected LS battle destiny prompt, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision()
                        + " text=" + (scn.LSGetDecision() == null ? "null" : scn.LSGetDecision().getText()),
                scn.LSDecisionAvailable("battle destiny"));
        String capPrompt = scn.LSGetDecision().getText();
        assertTrue("expected limited to 2, prompt=" + capPrompt, capPrompt.contains("limited to 2"));
    }

    @Test
    public void ScoundrelsBravadoAddsDestinyAnywhereEvenIfNotInTheBattle() {
        var scn = GetScenario();
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();
        var market = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        scn.MoveCardsToLocation(market, han, trooper);
        scn.AttachCardsTo(lando, bravado);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.LSInitiateBattle(market);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(bravado, "Add a battle destiny"));
        scn.LSUseCardAction(bravado, "Add a battle destiny");
        completeBravadoForceLoss(scn);
        assertTrue("expected to still be in battle, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                scn.gameState().isDuringBattle());
        assertEquals("count after anywhere Bravado, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                1, scn.GetLSBattleDestinyCount());
        scn.SkipToPowerSegment();
        assertTrue("expected draw 1 battle destiny anywhere, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision()
                        + " count=" + scn.GetLSBattleDestinyCount(),
                scn.LSDecisionAvailable("draw 1 battle destiny"));
    }

    private void completeBravadoForceLoss(VirtualTableScenario scn) {
        scn.PassAllResponses();
        assertTrue("expected Force loss for Bravado, ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                scn.LSDecisionAvailable("Choose Force to lose"));
        scn.LSChooseCard(scn.GetTopOfLSForcePile());
        scn.PassForceUseResponses();
        scn.PassAllResponses();
    }

    @Test
    public void ScoundrelsBravadoDoesNotOfferWhenAddedBattleDestinyIsPrevented() {
        var scn = GetScenario();
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, trooper);
        scn.AttachCardsTo(lando, bravado);
        scn.ApplyAdHocModifier(new MayNotAddBattleDestinyDrawsModifier(trooper, bravado));
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertFalse(scn.LSCardActionAvailable(bravado, "Add a battle destiny"));
    }
}
