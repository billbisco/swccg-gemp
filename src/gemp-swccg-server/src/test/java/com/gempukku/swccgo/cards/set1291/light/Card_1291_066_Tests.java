package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_066_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hunt", "1291_66");
                    put("begun", "1291_50"); // Begun, The Clone War Has (the Mission's host)
                    put("anakin", "200_2"); // Anakin Skywalker
                    put("obi", "11_6"); // Obi-Wan Kenobi
                    put("luke", "1_19"); // neither Anakin nor Obi-Wan
                    put("effect", "1291_54"); // a non-character card for the Reward
                }},
                new HashMap<>() {{
                    put("vader", "1_168"); // Darth Vader: ability 6 ('acolyte')
                    put("vader2", "1_168"); // a second Vader, for Turn the Tide
                    put("trooper", "1_194"); // Stormtrooper: ability 1 (not an 'acolyte')
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
    public void HuntDownTheDarkAcolyteStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hunt").getBlueprint();
        assertEquals("Hunt Down The Dark Acolyte", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.MISSION); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.CLONE_ARMY);
            add(Icon.MISSION);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HuntDownTheDarkAcolyteDeploysOnBegunTheCloneWarHas() {
        var scn = GetScenario();
        var hunt = scn.GetLSCard("hunt");
        var begun = scn.GetLSCard("begun");
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(begun);
        scn.MoveCardsToHand(hunt);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(hunt));
        scn.LSDeployCard(hunt);
        if (scn.LSDecisionAvailable("Choose")) {
            scn.LSChooseCard(begun);
        }
        PassAllResponsesThroughDuel(scn);
        assertTrue(scn.IsAttachedTo(begun, hunt));
    }

    @Test
    public void HuntDownTheDarkAcolyteMayNotDeployWithoutBegunTheCloneWarHas() {
        var scn = GetScenario();
        var hunt = scn.GetLSCard("hunt");
        scn.StartGame();
        scn.MoveCardsToHand(hunt);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(hunt));
    }

    /**
     * Hunt Down on the real Begun, The Clone War Has; the given Light character and Dark character at the Dark Side
     * starting site; skips to the Light Side move phase.
     */
    private void SetUpDuel(VirtualTableScenario scn, String light, String dark) {
        var hunt = scn.GetLSCard("hunt");
        var begun = scn.GetLSCard("begun");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(begun);
        scn.AttachCardsTo(begun, hunt);
        scn.MoveCardsToLocation(site, scn.GetLSCard(light), scn.GetDSCard(dark));
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.AwaitingLSMovePhaseActions());
    }

    /**
     * Initiates the duel and walks it to its results. Light Side draws the two given destinies, Dark Side draws 1 and 2.
     */
    private void Duel(VirtualTableScenario scn, int lightDestiny1, int lightDestiny2) {
        var hunt = scn.GetLSCard("hunt");
        // Each prepared value is a different filler card, drawn top first
        scn.PrepareDSDestiny(2);
        scn.PrepareDSDestiny(1);
        scn.PrepareLSDestiny(lightDestiny2);
        scn.PrepareLSDestiny(lightDestiny1);
        assertTrue(scn.LSCardActionAvailable(hunt, "Initiate duel"));
        scn.LSUseCardAction(hunt, "Initiate duel");
        ChooseDuelists(scn);
        PassAllResponsesThroughDuel(scn);
    }

    /**
     * Passes from the Light Side move phase to the next Dark Side deploy phase (SkipToDSTurn gives up at the end of
     * this turn, where both players get a recirculation response window).
     */
    private static void PassToDarkSideDeployPhase(VirtualTableScenario scn) {
        for (int i = 0; i < 40 && scn.gameState().getCurrentPlayerId().equals(scn.LS); i++) {
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            } else {
                scn.DSPass();
            }
        }
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
    }

    private static void PassAllResponsesThroughDuel(VirtualTableScenario scn) {
        for (int i = 0; i < 10; i++) {
            scn.PassAllResponses();
        }
    }

    /**
     * Answers the targeting: the only Anakin/Obi-Wan at the site, then the only 'acolyte' present.
     */
    private void ChooseDuelists(VirtualTableScenario scn) {
        var site = scn.GetDSStartingLocation();
        if (scn.LSDecisionAvailable("Choose Anakin or Obi-Wan")) {
            for (var c : scn.GetCardsAtLocation(site)) {
                var card = (com.gempukku.swccgo.game.PhysicalCardImpl) c;
                if (scn.LSHasCardChoiceAvailable(card)) {
                    scn.LSChooseCard(card);
                    break;
                }
            }
        }
        if (scn.LSDecisionAvailable("Choose 'acolyte'")) {
            for (var c : scn.GetCardsAtLocation(site)) {
                var card = (com.gempukku.swccgo.game.PhysicalCardImpl) c;
                if (scn.LSHasCardChoiceAvailable(card)) {
                    scn.LSChooseCard(card);
                    break;
                }
            }
        }
    }

    @Test
    public void HuntDownTheDarkAcolyteWonDuelStacksAcolyteFaceDownAndRewardTakesNonCharacterCard() {
        var scn = GetScenario();
        var hunt = scn.GetLSCard("hunt");
        var anakin = scn.GetLSCard("anakin");
        var vader = scn.GetDSCard("vader");
        var effect = scn.GetLSCard("effect");
        var luke = scn.GetLSCard("luke");
        SetUpDuel(scn, "anakin", "vader");
        scn.MoveCardsToTopOfLSReserveDeck(effect, luke);

        // Anakin ability + 6 + 5 beats Vader 6 + 1 + 2
        Duel(scn, 6, 5);

        assertTrue(scn.LSDecisionAvailable("Do you want to take a non-character card into hand from Reserve Deck?"));
        scn.LSChooseYes();
        assertTrue(scn.LSHasCardChoiceAvailable(effect));
        assertEquals(1, scn.LSGetSelectableCount());
        scn.LSChooseCard(effect);
        PassAllResponsesThroughDuel(scn);

        assertInZone(Zone.STACKED_FACE_DOWN, vader);
        assertTrue(scn.IsStackedOn(hunt, vader));
        assertInHand(effect);
        assertAtLocation(scn.GetDSStartingLocation(), anakin);

        // "if no card here": no second duel
        scn.SkipToLSTurn(Phase.MOVE);
        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertFalse(scn.LSCardActionAvailable(hunt, "Initiate duel"));
    }

    @Test
    public void HuntDownTheDarkAcolyteObiWanMayBeTheTarget() {
        var scn = GetScenario();
        var hunt = scn.GetLSCard("hunt");
        var vader = scn.GetDSCard("vader");
        SetUpDuel(scn, "obi", "vader");
        Duel(scn, 6, 5);
        if (scn.LSDecisionAvailable("Do you want to take")) {
            scn.LSChooseNo();
        }
        PassAllResponsesThroughDuel(scn);
        assertTrue(scn.IsStackedOn(hunt, vader));
    }

    @Test
    public void HuntDownTheDarkAcolyteLostOrTiedDuelPlacesTargetInUsedPile() {
        var scn = GetScenario();
        var hunt = scn.GetLSCard("hunt");
        var anakin = scn.GetLSCard("anakin");
        var vader = scn.GetDSCard("vader");
        SetUpDuel(scn, "anakin", "vader");

        Duel(scn, 0, 1);

        assertTrue(anakin.getZone() == Zone.USED_PILE || anakin.getZone() == Zone.TOP_OF_USED_PILE);
        assertAtLocation(scn.GetDSStartingLocation(), vader);
        assertTrue(scn.GetStackedCards(hunt).isEmpty());
    }

    private static boolean huntMayNotCancelDestinyModifierActive(VirtualTableScenario scn) {
        var hunt = scn.GetLSCard("hunt");
        for (var modifier : scn.game().getModifiersQuerying().getModifiers(scn.gameState(), ModifierType.MAY_NOT_CANCEL_DESTINY_DRAWS)) {
            if (modifier.getSource(scn.gameState()) == hunt) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void HuntDownTheDarkAcolyteDuelDestiniesMayNotBeCanceled() {
        var scn = GetScenario();
        var hunt = scn.GetLSCard("hunt");
        SetUpDuel(scn, "anakin", "vader");
        assertFalse(huntMayNotCancelDestinyModifierActive(scn));

        scn.PrepareDSDestiny(2);
        scn.PrepareDSDestiny(1);
        scn.PrepareLSDestiny(5);
        scn.PrepareLSDestiny(6);
        scn.LSUseCardAction(hunt, "Initiate duel");
        ChooseDuelists(scn);
        // Walk the duel until a response window inside the duel's destiny draws
        boolean seenDuringDuel = false;
        for (int i = 0; i < 60; i++) {
            if (huntMayNotCancelDestinyModifierActive(scn) && scn.gameState().getTopDrawDestinyState() != null) {
                seenDuringDuel = true;
                assertTrue(scn.game().getModifiersQuerying().mayNotCancelDestinyDraw(scn.gameState(), scn.DS, false));
                assertTrue(scn.game().getModifiersQuerying().mayNotCancelDestinyDraw(scn.gameState(), scn.LS, true));
                break;
            }
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            } else {
                scn.DSPass();
            }
        }
        assertTrue(seenDuringDuel);
        PassAllResponsesThroughDuel(scn);
        if (scn.LSDecisionAvailable("Do you want to take")) {
            scn.LSChooseNo();
        }
        PassAllResponsesThroughDuel(scn);
        // Only until the end of the duel
        assertFalse(huntMayNotCancelDestinyModifierActive(scn));
    }

    @Test
    public void HuntDownTheDarkAcolyteNotOfferedAgainstNonAcolyte() {
        var scn = GetScenario();
        var hunt = scn.GetLSCard("hunt");
        SetUpDuel(scn, "anakin", "trooper");
        assertFalse(scn.LSCardActionAvailable(hunt, "Initiate duel"));
    }

    @Test
    public void HuntDownTheDarkAcolyteNotOfferedForOtherCharacters() {
        var scn = GetScenario();
        var hunt = scn.GetLSCard("hunt");
        SetUpDuel(scn, "luke", "vader");
        assertFalse(scn.LSCardActionAvailable(hunt, "Initiate duel"));
    }

    @Test
    public void HuntDownTheDarkAcolyteOnlyDuringYourMovePhaseAndOnce() {
        var scn = GetScenario();
        var hunt = scn.GetLSCard("hunt");
        SetUpDuel(scn, "anakin", "vader");
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(hunt, "Initiate duel"));

        // Once during your move phase: a lost duel leaves no card here, but no second duel this phase
        scn.SkipToLSTurn(Phase.MOVE);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("obi"));
        Duel(scn, 0, 1);
        if (scn.AwaitingDSMovePhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSMovePhaseActions());
        assertFalse(scn.LSCardActionAvailable(hunt, "Initiate duel"));
    }

    @Test
    public void HuntDownTheDarkAcolyteTurnTheTidePlacesMissionInUsedPileWhenSamePersonaDeployed() {
        var scn = GetScenario();
        var hunt = scn.GetLSCard("hunt");
        var vader2 = scn.GetDSCard("vader2");
        SetUpDuel(scn, "anakin", "vader");
        Duel(scn, 6, 5);
        if (scn.LSDecisionAvailable("Do you want to take")) {
            scn.LSChooseNo();
        }
        PassAllResponsesThroughDuel(scn);
        assertTrue(scn.IsStackedOn(hunt, scn.GetDSCard("vader")));

        scn.MoveCardsToDSHand(vader2);
        PassToDarkSideDeployPhase(scn);
        scn.EnsureDSForcePile(10);
        assertTrue(scn.DSDeployAvailable(vader2));
        scn.DSDeployCard(vader2);
        if (scn.DSDecisionAvailable("Choose where to deploy")) {
            scn.DSChooseCard(scn.GetDSStartingLocation());
        }
        PassAllResponsesThroughDuel(scn);

        assertTrue(hunt.getZone() == Zone.USED_PILE || hunt.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void HuntDownTheDarkAcolyteTurnTheTideNotTriggeredByOtherDeployments() {
        var scn = GetScenario();
        var hunt = scn.GetLSCard("hunt");
        var trooper = scn.GetDSCard("trooper");
        SetUpDuel(scn, "anakin", "vader");
        Duel(scn, 6, 5);
        if (scn.LSDecisionAvailable("Do you want to take")) {
            scn.LSChooseNo();
        }
        PassAllResponsesThroughDuel(scn);

        scn.MoveCardsToDSHand(trooper);
        PassToDarkSideDeployPhase(scn);
        scn.EnsureDSForcePile(10);
        scn.DSDeployCard(trooper);
        if (scn.DSDecisionAvailable("Choose where to deploy")) {
            scn.DSChooseCard(scn.GetDSStartingLocation());
        }
        PassAllResponsesThroughDuel(scn);

        assertTrue(scn.IsStackedOn(hunt, scn.GetDSCard("vader")));
        assertFalse(hunt.getZone() == Zone.USED_PILE || hunt.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
