package com.gempukku.swccgo.cards.set1240.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_097_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("skull", "1240_97");
                    put("snake1", "4_7");
                    put("snake2", "4_7");
                    put("tauntaun", "3_70");
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
    public void SkullVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("skull").getBlueprint();

        assertEquals("Skull", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SkullVPackAttacksOnlyDuringOpponentsControlPhase() {
        var scn = GetScenario();
        var skull = scn.GetLSCard("skull");
        var snake1 = scn.GetLSCard("snake1");
        var snake2 = scn.GetLSCard("snake2");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(skull);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, snake1, snake2, trooper);
        assertFalse(scn.LSCardActionAvailable(skull, "Your pack attacks"));

        var scn2 = GetScenario();
        scn2.StartGame();
        scn2.MoveCardsToLSHand(scn2.GetLSCard("skull"));
        scn2.SkipToDSTurn(Phase.CONTROL);
        scn2.MoveCardsToLocation(scn2.GetLSStartingLocation(), scn2.GetLSCard("snake1"), scn2.GetLSCard("snake2"), scn2.GetDSCard("trooper"));
        scn2.DSPass();
        assertTrue(scn2.LSCardActionAvailable(scn2.GetLSCard("skull"), "Your pack attacks"));
    }

    @Test
    public void SkullVSingleCreatureIsNotAPack() {
        var scn = GetScenario();
        var skull = scn.GetLSCard("skull");
        var snake1 = scn.GetLSCard("snake1");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(skull);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, snake1, trooper);
        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(skull, "Your pack attacks"));
    }

    @Test
    public void SkullVPackAttackOnlyLeadMayBeTargetedByWeapons() {
        var scn = GetScenario();
        var skull = scn.GetLSCard("skull");
        var snake1 = scn.GetLSCard("snake1");
        var snake2 = scn.GetLSCard("snake2");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(skull);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, snake1, snake2, trooper);
        scn.DSPass();

        scn.LSUseCardAction(skull, "Your pack attacks");
        scn.LSChooseCard(snake1);
        if (scn.LSHasCardChoiceAvailable(trooper)) {
            scn.LSChooseCard(trooper);
        }
        // Pass responses until the attack has begun; then check weapon targeting restriction.
        for (int i = 0; i < 10 && scn.gameState().getAttackState() == null; i++) {
            if (scn.DSDecisionAvailable("")) {
                scn.DSPass();
            }
            else if (scn.LSDecisionAvailable("")) {
                scn.LSPass();
            }
        }
        assertTrue(scn.gameState().getAttackState() != null);
        var mq = scn.game().getModifiersQuerying();
        assertTrue(mq.getModifiersAffectingCard(scn.gameState(),
                com.gempukku.swccgo.logic.modifiers.ModifierType.MAY_NOT_BE_TARGETED_BY, snake2).size() > 0);
        assertEquals(0, mq.getModifiersAffectingCard(scn.gameState(),
                com.gempukku.swccgo.logic.modifiers.ModifierType.MAY_NOT_BE_TARGETED_BY, snake1).size());
    }

    @Test
    public void SkullVUses3ForceToRetrieveACreature() {
        var scn = GetScenario();
        var skull = scn.GetLSCard("skull");
        var tauntaun = scn.GetLSCard("tauntaun");
        var snake = scn.GetLSCard("snake1");

        scn.StartGame();
        scn.MoveCardsToLSHand(skull);
        scn.MoveCardsToTopOfLSLostPile(tauntaun, snake);
        scn.SkipToLSTurn(Phase.DEPLOY);

        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(skull, "retrieve a creature"));
        scn.LSUseCardAction(skull, "retrieve a creature");
        scn.PassAllResponses();
        scn.LSChooseCard(snake);
        scn.PassAllResponses();
        assertEquals(force - 3, scn.GetLSForcePileCount());
        assertTrue(snake.getZone() == Zone.USED_PILE || snake.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(tauntaun.getZone() == Zone.LOST_PILE || tauntaun.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
