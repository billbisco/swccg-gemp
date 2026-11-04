package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("obi", "1_21");      // Obi-Wan Kenobi: a Jedi
                    put("bacta", "3_32");    // Bacta Tank: places a just-lost character here instead of Lost Pile
                }},
                new HashMap<>() {{
                    put("powers", "1274_10");
                    put("trooper", "1_194");
                    put("vader", "1_168");
                }},
                30, 30,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private static boolean dsCan(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.DSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static boolean lsCan(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        try {
            return scn.LSCardActionAvailable(card, text);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static boolean passUntil(VirtualTableScenario scn, BooleanSupplier condition) {
        for (int i = 0; i < 30; i++) {
            if (condition.getAsBoolean()) {
                return true;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                return false;
            }
        }
        return condition.getAsBoolean();
    }

    @Test
    public void YourPowersAreWeakOldManVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("powers").getBlueprint();

        assertEquals("Your Powers Are Weak, Old Man", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.INTERRUPT); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void YourPowersAreWeakOldManVUsedCancelsAttemptToRemoveJustLostCharacterFromLostPile() {
        var scn = GetScenario();
        var powers = scn.GetDSCard("powers");
        var bacta = scn.GetLSCard("bacta");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(powers);
        scn.MoveCardsToLSSideOfTable(bacta);
        scn.MoveCardsToLocation(site, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSExecuteAdHocEffect(luke, new LoseCardFromTableEffect(new TopLevelGameTextAction(luke, luke.getCardId()), luke));
        assertTrue(passUntil(scn, () -> lsCan(scn, bacta, "Stack")));
        scn.LSUseCardAction(bacta, "Stack");
        assertTrue(passUntil(scn, () -> dsCan(scn, powers, "Cancel attempt")));
        scn.DSUseCardAction(powers, "Cancel attempt");
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, luke);
        assertFalse(scn.IsStackedOn(bacta, luke));
        assertInZone(Zone.USED_PILE, powers);
    }

    @Test
    public void YourPowersAreWeakOldManVLostTakesCardFromUsedPileAfterDeployingCharacterToSameSiteAsJedi() {
        var scn = GetScenario();
        var powers = scn.GetDSCard("powers");
        var trooper = scn.GetDSCard("trooper");
        var vader = scn.GetDSCard("vader");
        var obi = scn.GetLSCard("obi");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, obi);
        scn.MoveCardsToDSHand(powers, trooper);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSUsedPile(vader);
        scn.DSDeployCard(trooper);
        if (scn.DSDecisionAvailable("Choose")) {
            scn.DSChooseCard(site);
        }
        assertTrue(passUntil(scn, () -> dsCan(scn, powers, "Take a card from Used Pile")));
        scn.DSUseCardAction(powers, "Take a card from Used Pile");
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Choose")) {
            scn.DSChooseCard(vader);
            scn.PassAllResponses();
        }
        assertInZone(Zone.HAND, vader);
        assertInZone(Zone.LOST_PILE, powers);
    }

    @Test
    public void YourPowersAreWeakOldManVLostNotAvailableWithoutJediAtSite() {
        var scn = GetScenario();
        var powers = scn.GetDSCard("powers");
        var trooper = scn.GetDSCard("trooper");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(powers, trooper);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSUsedPile(vader);
        scn.DSDeployCard(trooper);
        if (scn.DSDecisionAvailable("Choose")) {
            scn.DSChooseCard(site);
        }
        assertFalse(dsCan(scn, powers, "Take a card from Used Pile"));
    }
}
