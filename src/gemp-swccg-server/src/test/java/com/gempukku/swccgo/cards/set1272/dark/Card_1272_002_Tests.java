package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.common.Agenda;
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
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("blaster", "1_152");
                }},
                new HashMap<>() {{
                    put("commander", "1272_2");
                    put("evax", "1240_12");
                    put("trooper", "1_194");
                    put("hand", "1_194");
                    put("yularen", "1_166");
                    put("vader", "1_168");
                    put("ownblaster", "1_317");
                    put("site", "1_291");
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
    public void IsbCentralCommanderStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("commander").getBlueprint();

        assertEquals("ISB Central Commander", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getInfluence(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.C, card.getRarity());
    }

    @Test
    public void IsbCentralCommanderNotTargetedByWeaponsWhileWithAgentOrCouncilMember() {
        var scn = GetScenario();
        var commander = scn.GetDSCard("commander");
        var evax = scn.GetDSCard("evax");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetLSCard("blaster");
        var ownBlaster = scn.GetDSCard("ownblaster");
        var site = scn.GetDSCard("site");

        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, commander);
        assertTrue(scn.game().getModifiersQuerying().hasAgenda(scn.gameState(), commander, Agenda.INTELLIGENCE));
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), commander), scn.epsilon);
        assertTrue(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), commander, blaster));

        scn.MoveCardsToLocation(site, trooper);
        scn.ApplyAdHocModifier(new KeywordModifier(trooper, Keyword.ISB_AGENT));
        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), commander, blaster));
        assertTrue(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), commander, ownBlaster));

        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), trooper);
        assertTrue(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), commander, blaster));

        scn.MoveCardsToLocation(site, evax);
        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), commander, blaster));
    }

    @Test
    public void IsbCentralCommanderExchangesImperialInHandWithLowAbilityImperialInLostPile() {
        var scn = GetScenario();
        var commander = scn.GetDSCard("commander");
        var hand = scn.GetDSCard("hand");
        var yularen = scn.GetDSCard("yularen");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSCard("site");

        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, commander);
        scn.MoveCardsToDSHand(hand);
        scn.MoveCardsToTopOfDSLostPile(yularen, vader);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.DSCardActionAvailable(commander, "Exchange an Imperial"));
        scn.DSUseCardAction(commander, "Exchange an Imperial");
        assertTrue("Yularen (ability 2) should be selectable in Lost Pile", selectable(scn, yularen));
        assertFalse("Vader (ability 6) should be shown but not selectable", selectable(scn, vader));
        scn.DSChooseCard(yularen);
        // One matching Imperial in hand is selected automatically.
        if (hand.getZone() != Zone.LOST_PILE && scn.DSHasCardChoiceAvailable(hand))
            scn.DSChooseCard(hand);
        scn.PassAllResponses();

        assertEquals(Zone.HAND, yularen.getZone());
        assertTrue(hand.getZone() == Zone.LOST_PILE || hand.getZone() == Zone.TOP_OF_LOST_PILE);
        assertTrue(vader.getZone() == Zone.LOST_PILE || vader.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    private boolean selectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        String[] blueprintIds = scn.GetADParam(scn.DS, "blueprintId");
        String[] cardIds = scn.GetADParam(scn.DS, "cardId");
        String[] selectable = scn.GetADParam(scn.DS, "selectable");
        if (blueprintIds == null || selectable == null)
            return false;
        for (int i = 0; i < blueprintIds.length; i++) {
            boolean sameCard = card.getBlueprintId(true).equals(blueprintIds[i])
                    || (cardIds != null && String.valueOf(card.getCardId()).equals(cardIds[i]));
            if (sameCard && "true".equals(selectable[i]))
                return true;
        }
        return false;
    }
}
