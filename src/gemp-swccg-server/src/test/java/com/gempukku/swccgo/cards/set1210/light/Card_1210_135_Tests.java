package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardSubtype;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_135_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("armor", "1210_135");
                    put("luke", "1_19");
                    put("c3po", "1_5");
                    put("lukesBack", "1_95");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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

    @Test
    public void PlastoidArmorStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("armor").getBlueprint();

        assertEquals("Plastoid Armor", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void PlastoidArmorDeploysOnYourNonDroidCharacter() {
        var scn = GetScenario();
        var armor = scn.GetLSCard("armor");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(armor);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(armor));
        scn.LSPlayCard(armor);
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(luke, armor.getAttachedTo());
    }

    @Test
    public void PlastoidArmorDoesNotDeployOnADroid() {
        var scn = GetScenario();
        var armor = scn.GetLSCard("armor");
        var c3po = scn.GetLSCard("c3po");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, c3po);
        scn.MoveCardsToLSHand(armor);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(armor));
        assertEquals(Zone.HAND, armor.getZone());
    }

    @Test
    public void PlastoidArmorMakesInterruptsWithBackInTitleDestinyFiveAndUsed() {
        var scn = GetScenario();
        var armor = scn.GetLSCard("armor");
        var luke = scn.GetLSCard("luke");
        var lukesBack = scn.GetLSCard("lukesBack");
        var host = scn.GetLSFiller(1);
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, host);
        scn.AttachCardsTo(host, armor);
        scn.MoveCardsToLSHand(lukesBack);
        assertEquals(5, scn.GetDestiny(lukesBack));
        scn.MoveCardsToTopOfLSLostPile(luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(2);
        assertTrue(!scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), com.gempukku.swccgo.logic.modifiers.ModifierType.USED_INTERRUPT, lukesBack)
                .isEmpty());
        assertTrue(scn.LSPlayUsedInterruptAvailable(lukesBack)
                || scn.LSCardActionAvailable(lukesBack, "Retrieve Luke")
                || scn.LSCardPlayAvailable(lukesBack));
        assertFalse(scn.LSPlayLostInterruptAvailable(lukesBack));
        if (scn.LSPlayUsedInterruptAvailable(lukesBack)) {
            scn.LSPlayUsedInterrupt(lukesBack);
        } else if (scn.LSCardActionAvailable(lukesBack, "Retrieve Luke")) {
            scn.LSUseCardAction(lukesBack, "Retrieve Luke");
        } else {
            scn.LSPlayCard(lukesBack);
        }
        scn.PassCardAndForceUseResponses();
        for (int i = 0; i < 8; i++) {
            if (luke.getZone() == Zone.HAND) {
                break;
            }
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(luke)) {
                scn.LSChooseCard(luke);
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            break;
        }
        scn.PassAllResponses();
        assertTrue(lukesBack.getZone() == Zone.USED_PILE || lukesBack.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(luke.getZone() == Zone.USED_PILE || luke.getZone() == Zone.TOP_OF_USED_PILE || luke.getZone() == Zone.HAND);
    }

    @Test
    public void PlastoidArmorMakesYourCharactersImmuneToBadFeelingHaveI() {
        var scn = GetScenario();
        var armor = scn.GetLSCard("armor");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.AttachCardsTo(luke, armor);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), luke, Title.Bad_Feeling_Have_I));
        assertFalse(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), trooper, Title.Bad_Feeling_Have_I));
    }

    @Test
    public void PlastoidArmorTakesAnInterruptWithBackInTitleIntoHandDuringOpponentsMovePhase() {
        var scn = GetScenario();
        var armor = scn.GetLSCard("armor");
        var luke = scn.GetLSCard("luke");
        var lukesBack = scn.GetLSCard("lukesBack");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.AttachCardsTo(luke, armor);
        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.MOVE);
        scn.DSPass();
        scn.MoveCardsToTopOfLSReserveDeck(lukesBack);
        assertTrue(scn.LSCardActionAvailable(armor, "Take Interrupt with 'back'"));
        scn.LSUseCardAction(armor, "Take Interrupt with 'back'");
        scn.PassCardAndForceUseResponses();
        if (lukesBack.getZone() != Zone.HAND && scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(lukesBack)) {
            scn.LSChooseCard(lukesBack);
        }
        scn.PassAllResponses();
        assertEquals(Zone.HAND, lukesBack.getZone());
    }
}
