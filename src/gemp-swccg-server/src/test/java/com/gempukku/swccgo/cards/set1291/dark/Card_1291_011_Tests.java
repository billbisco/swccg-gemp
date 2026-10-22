package com.gempukku.swccgo.cards.set1291.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_011_Tests {
    /**
     * Separatist Uprising is kept outside the deck so that tests can choose whether it is on table.
     */
    StartingSetup UprisingOutsideDeck = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("uprising", "1291_29");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("war", "1291_11");
                    put("council", "1291_19");
                    put("arena", "1291_26");
                    put("defense", "1291_27");
                    put("dooku", "200_76");
                    put("vader", "1_168");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                UprisingOutsideDeck,
                VirtualTableScenario.Open
        );
    }

    private void PutUprisingInPlay(VirtualTableScenario scn) {
        var uprising = scn.GetDSCard("uprising");
        scn.MoveCardsToDSSideOfTable(uprising);
        uprising.setObjectiveDeploymentComplete(true);
        scn.gameState().reapplyAffectingForCard(scn.game(), uprising);
        scn.PassAllResponses();
    }

    @Test
    public void WarHasBegunStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("war").getBlueprint();

        assertEquals("War Has Begun", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WarHasBegunDoesNotDeployWithoutSeparatistUprising() {
        var scn = GetScenario();
        var war = scn.GetDSCard("war");

        scn.StartGame();
        scn.MoveCardsToDSHand(war);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardPlayAvailable(war));
    }

    @Test
    public void WarHasBegunDeploysOnTableWhileSeparatistUprisingOnTable() {
        var scn = GetScenario();
        var war = scn.GetDSCard("war");

        scn.StartGame();
        PutUprisingInPlay(scn);
        scn.MoveCardsToDSHand(war);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardPlayAvailable(war));
        scn.DSPlayCard(war);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, war);
    }

    @Test
    public void WarHasBegunGivesYourSeparatistCouncilRoomOneMoreDarkForceIcon() {
        var scn = GetScenario();
        var war = scn.GetDSCard("war");
        var council = scn.GetDSCard("council");

        scn.StartGame();
        scn.MoveLocationToTable(council);
        assertEquals(2, scn.GetIconCount(council, Icon.DARK_FORCE));
        assertEquals(0, scn.GetIconCount(council, Icon.LIGHT_FORCE));

        scn.MoveCardsToDSSideOfTable(war);

        assertEquals(3, scn.GetIconCount(council, Icon.DARK_FORCE));
        assertEquals(0, scn.GetIconCount(council, Icon.LIGHT_FORCE));
    }

    @Test
    public void WarHasBegunAddsOneForceGenerationAtYourBattlegroundControlledWithASeparatistCard() {
        var scn = GetScenario();
        var war = scn.GetDSCard("war");
        var dooku = scn.GetDSCard("dooku");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dooku);
        assertEquals(1, scn.GetIconCount(dooku, Icon.SEPARATIST));
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.DS);

        scn.MoveCardsToDSSideOfTable(war);

        float after = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.DS);
        assertEquals(before + 1, after, scn.epsilon);
    }

    @Test
    public void WarHasBegunDoesNotAddForceGenerationAtBattlegroundControlledWithoutASeparatistCard() {
        var scn = GetScenario();
        var war = scn.GetDSCard("war");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader);
        assertEquals(0, scn.GetIconCount(vader, Icon.SEPARATIST));
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.DS);

        scn.MoveCardsToDSSideOfTable(war);

        float after = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.DS);
        assertEquals(before, after, scn.epsilon);
    }

    @Test
    public void WarHasBegunDoesNotAddForceGenerationAtBattlegroundYouDoNotControl() {
        var scn = GetScenario();
        var war = scn.GetDSCard("war");
        var dooku = scn.GetDSCard("dooku");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dooku, luke);
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.DS);

        scn.MoveCardsToDSSideOfTable(war);

        float after = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.DS);
        assertEquals(before, after, scn.epsilon);
    }

    @Test
    public void WarHasBegunTakesASeparatistMissionIntoHandFromReserveDeckOncePerTurn() {
        var scn = GetScenario();
        var war = scn.GetDSCard("war");
        var arena = scn.GetDSCard("arena");
        var defense = scn.GetDSCard("defense");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(war);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(arena, defense);

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardActionAvailable(war, "Mission into hand from Reserve Deck"));
        scn.DSUseCardAction(war, "Mission into hand from Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoicesAvailable(arena, defense));
        scn.DSChooseCard(arena);
        scn.PassAllResponses();

        assertInHand(arena);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(war, "Mission into hand from Reserve Deck"));

        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(defense);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardActionAvailable(war, "Mission into hand from Reserve Deck"));
        scn.DSUseCardAction(war, "Mission into hand from Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(defense));
        scn.DSChooseCard(defense);
        scn.PassAllResponses();

        assertInHand(defense);
    }
}
