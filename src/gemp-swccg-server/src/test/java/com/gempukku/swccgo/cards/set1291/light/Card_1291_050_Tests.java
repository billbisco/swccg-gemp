package com.gempukku.swccgo.cards.set1291.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_050_Tests {
    /**
     * Republic At War is kept outside the deck so that tests can choose whether it is on table.
     */
    static final StartingSetup RepublicAtWarOutsideDeck = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("raw", "1291_69"); // Republic At War / Aggressive Negotiations
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("begun", "1291_50");
                    put("fcc", "1291_57"); // Geonosis: Forward Command Center
                    put("rescue", "1291_67"); // Rescue On Geonosis ([Clone Army] Mission)
                    put("assault", "1291_65"); // Assault On Muunilinst ([Clone Army] Mission)
                    put("clonepilot", "221_55"); // Clone Pilot ([Clone Army] card)
                    put("trooper", "1116_9"); // Clone Trooper (a clone without the [Clone Army] icon)
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                RepublicAtWarOutsideDeck,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private void PutRepublicAtWarInPlay(VirtualTableScenario scn) {
        var raw = scn.GetLSCard("raw");
        scn.MoveCardsToLSSideOfTable(raw);
        raw.setObjectiveDeploymentComplete(true);
        scn.gameState().reapplyAffectingForCard(scn.game(), raw);
        scn.PassAllResponses();
    }

    @Test
    public void BegunTheCloneWarHasStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("begun").getBlueprint();

        assertEquals("Begun, The Clone War Has", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
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
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BegunTheCloneWarHasDoesNotDeployWithoutRepublicAtWar() {
        var scn = GetScenario();
        var begun = scn.GetLSCard("begun");

        scn.StartGame();
        scn.MoveCardsToLSHand(begun);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardPlayAvailable(begun));
    }

    @Test
    public void BegunTheCloneWarHasDeploysOnTableWhileRepublicAtWarOnTable() {
        var scn = GetScenario();
        var begun = scn.GetLSCard("begun");

        scn.StartGame();
        PutRepublicAtWarInPlay(scn);
        scn.MoveCardsToLSHand(begun);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSCardPlayAvailable(begun));
        scn.LSPlayCard(begun);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, begun);
    }

    @Test
    public void BegunTheCloneWarHasGivesForwardCommandCenterOneMoreLightForceIcon() {
        var scn = GetScenario();
        var begun = scn.GetLSCard("begun");
        var fcc = scn.GetLSCard("fcc");

        scn.StartGame();
        scn.MoveLocationToTable(fcc);
        int before = scn.GetIconCount(fcc, Icon.LIGHT_FORCE);
        int darkBefore = scn.GetIconCount(fcc, Icon.DARK_FORCE);

        scn.MoveCardsToLSSideOfTable(begun);

        assertEquals(before + 1, scn.GetIconCount(fcc, Icon.LIGHT_FORCE));
        assertEquals(darkBefore, scn.GetIconCount(fcc, Icon.DARK_FORCE));
    }

    @Test
    public void BegunTheCloneWarHasAddsOneForceGenerationAtYourBattlegroundControlledWithACloneArmyCard() {
        var scn = GetScenario();
        var begun = scn.GetLSCard("begun");
        var clonePilot = scn.GetLSCard("clonepilot");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, clonePilot);
        assertEquals(1, scn.GetIconCount(clonePilot, Icon.CLONE_ARMY));
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.LS);

        scn.MoveCardsToLSSideOfTable(begun);

        float after = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.LS);
        assertEquals(before + 1, after, scn.epsilon);
    }

    @Test
    public void BegunTheCloneWarHasDoesNotAddForceGenerationAtBattlegroundControlledWithoutACloneArmyCard() {
        var scn = GetScenario();
        var begun = scn.GetLSCard("begun");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.LS);

        scn.MoveCardsToLSSideOfTable(begun);

        float after = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.LS);
        assertEquals(before, after, scn.epsilon);
    }

    @Test
    public void BegunTheCloneWarHasDoesNotAddForceGenerationAtBattlegroundYouDoNotControl() {
        var scn = GetScenario();
        var begun = scn.GetLSCard("begun");
        var clonePilot = scn.GetLSCard("clonepilot");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, clonePilot, vader);
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.LS);

        scn.MoveCardsToLSSideOfTable(begun);

        float after = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), site, scn.LS);
        assertEquals(before, after, scn.epsilon);
    }

    @Test
    public void BegunTheCloneWarHasYourClonesGainCloneArmy() {
        var scn = GetScenario();
        var begun = scn.GetLSCard("begun");
        var trooper = scn.GetLSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, luke);
        assertTrue(scn.HasKeyword(trooper, Keyword.CLONE_TROOPER));
        assertEquals(0, scn.GetIconCount(trooper, Icon.CLONE_ARMY));

        scn.MoveCardsToLSSideOfTable(begun);

        assertEquals(1, scn.GetIconCount(trooper, Icon.CLONE_ARMY));
        assertEquals(0, scn.GetIconCount(luke, Icon.CLONE_ARMY));
    }

    @Test
    public void BegunTheCloneWarHasTakesACloneArmyMissionIntoHandFromReserveDeckOncePerTurn() {
        var scn = GetScenario();
        var begun = scn.GetLSCard("begun");
        var rescue = scn.GetLSCard("rescue");
        var assault = scn.GetLSCard("assault");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(begun);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(rescue, assault);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSCardActionAvailable(begun, "Mission into hand from Reserve Deck"));
        scn.LSUseCardAction(begun, "Mission into hand from Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoicesAvailable(rescue, assault));
        scn.LSChooseCard(rescue);
        scn.PassAllResponses();

        assertInHand(rescue);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(begun, "Mission into hand from Reserve Deck"));

        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(assault);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSCardActionAvailable(begun, "Mission into hand from Reserve Deck"));
        scn.LSUseCardAction(begun, "Mission into hand from Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(assault));
        scn.LSChooseCard(assault);
        scn.PassAllResponses();

        assertInHand(assault);
    }
}
