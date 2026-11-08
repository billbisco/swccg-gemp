package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_032_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("quietly", "1240_32");
                    put("aurra", "11_51");
                    put("evazan", "1_172");
                    put("bluffs", "2_150");
                    put("pilot", "1_180");
                    put("pilot2", "1_180");
                    put("effect", "1_220");
                    put("bluff1", "1_180");
                    put("bluff2", "1_180");
                    put("bluff3", "1_180");
                    put("bluff4", "1_180");
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
    public void QuietlyObservingVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("quietly").getBlueprint();

        assertEquals(Title.Quietly_Observing, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardCategory.EFFECT, card.getCardCategory());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.EFFECT);
            add(Icon.TATOOINE);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void QuietlyObservingGameTextIsOfficialText() {
        var scn = GetScenario();
        assertEquals("Deploy on table. Once per game, may deploy Bluffs from Reserve Deck; reshuffle. Bluffs gains one [Light Side] and one [Dark Side]. Once per battle, may deploy a bluff card (if it is a character or vehicle) as a 'react.' To reveal a bluff card, opponent must first use X Force, where X = 4 - number of bluff cards. (Immune to Alter.)",
                scn.GetDSCard("quietly").getBlueprint().getGameText());
    }

    @Test
    public void QuietlyObservingNoLongerBoostsAurraDestinyOrRevealsAliens() {
        var scn = GetScenario();
        var quietly = scn.GetDSCard("quietly");
        var aurra = scn.GetDSCard("aurra");

        scn.StartGame();
        int before = scn.GetDestiny(aurra);
        scn.MoveCardsToDSSideOfTable(quietly);
        assertEquals(before, scn.GetDestiny(aurra));
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(quietly, "Reveal up to two unique"));
    }

    @Test
    public void QuietlyObservingDeploysBluffsFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var quietly = scn.GetDSCard("quietly");
        var bluffs = scn.GetDSCard("bluffs");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(quietly);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(bluffs);
        assertTrue(scn.DSCardActionAvailable(quietly, "Deploy Bluffs from Reserve Deck"));
        scn.DSUseCardAction(quietly, "Deploy Bluffs from Reserve Deck");
        scn.PassAllResponses();
        scn.DSChooseCard(bluffs);
        for (int i = 0; i < 12 && bluffs.getZone() != Zone.LOCATIONS; i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null)
                break;
            String prompt = decision.getText().toLowerCase();
            if (scn.DSGetDecision() != null && prompt.contains("side")) {
                scn.ChooseOption(scn.DS, scn.DSGetMultipleChoices().getFirst());
            } else if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                scn.LSPass();
            }
        }
        assertEquals(Zone.LOCATIONS, bluffs.getZone());
        if (!scn.AwaitingDSDeployPhaseActions() && scn.LSGetDecision() != null)
            scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(quietly, "Deploy Bluffs from Reserve Deck"));
    }

    @Test
    public void QuietlyObservingGivesBluffsOneLightAndOneDarkForceIcon() {
        var scn = GetScenario();
        var quietly = scn.GetDSCard("quietly");
        var bluffs = scn.GetDSCard("bluffs");

        scn.StartGame();
        scn.MoveLocationToTable(bluffs);
        var site = scn.GetDSStartingLocation();
        int ls = scn.GetIconCount(bluffs, Icon.LIGHT_FORCE);
        int ds = scn.GetIconCount(bluffs, Icon.DARK_FORCE);
        int siteLs = scn.GetIconCount(site, Icon.LIGHT_FORCE);
        int siteDs = scn.GetIconCount(site, Icon.DARK_FORCE);
        scn.MoveCardsToDSSideOfTable(quietly);
        assertEquals(ls + 1, scn.GetIconCount(bluffs, Icon.LIGHT_FORCE));
        assertEquals(ds + 1, scn.GetIconCount(bluffs, Icon.DARK_FORCE));
        // other locations unaffected
        assertEquals(siteLs, scn.GetIconCount(site, Icon.LIGHT_FORCE));
        assertEquals(siteDs, scn.GetIconCount(site, Icon.DARK_FORCE));
    }

    private void stackBluffCards(VirtualTableScenario scn, PhysicalCardImpl bluffs, PhysicalCardImpl... cards) {
        scn.StackCardsFaceDownOn(bluffs, cards);
        for (PhysicalCardImpl card : cards)
            card.setBluffCard(true);
    }

    private void skipToDSDeployWithLSToAct(VirtualTableScenario scn) {
        scn.SkipToDSTurn(Phase.DEPLOY);
        if (scn.AwaitingDSDeployPhaseActions())
            scn.DSPass();
    }

    @Test
    public void OpponentMustUseXForceToFlipABluffCardWhereXIsFourMinusBluffCards() {
        var scn = GetScenario();
        var quietly = scn.GetDSCard("quietly");
        var bluffs = scn.GetDSCard("bluffs");

        scn.StartGame();
        scn.MoveLocationToTable(bluffs);
        scn.MoveCardsToDSSideOfTable(quietly);
        stackBluffCards(scn, bluffs, scn.GetDSCard("bluff1"));
        assertEquals(3, scn.game().getModifiersQuerying().getExtraForceCostToFlipBluffCard(scn.gameState(), bluffs, scn.LS));
        assertEquals(0, scn.game().getModifiersQuerying().getExtraForceCostToFlipBluffCard(scn.gameState(), bluffs, scn.DS));
        skipToDSDeployWithLSToAct(scn);
        scn.LSActivateForceCheat(3);
        int force = scn.GetLSForcePileCount();
        assertTrue(force >= 3);
        assertTrue(scn.LSCardActionAvailable(bluffs, "Flip over a 'bluff card' here"));
        scn.LSUseCardAction(bluffs, "Flip over a 'bluff card' here");
        scn.PassAllResponses();
        assertEquals(force - 3, scn.GetLSForcePileCount());
    }

    @Test
    public void OpponentCannotFlipABluffCardWithoutEnoughForce() {
        var scn = GetScenario();
        var quietly = scn.GetDSCard("quietly");
        var bluffs = scn.GetDSCard("bluffs");

        scn.StartGame();
        scn.MoveLocationToTable(bluffs);
        scn.MoveCardsToDSSideOfTable(quietly);
        stackBluffCards(scn, bluffs, scn.GetDSCard("bluff1"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        while (scn.GetLSForcePileCount() > 2)
            scn.MoveCardsToTopOfLSUsedPile(scn.GetTopOfLSForcePile());
        assertEquals(2, scn.GetLSForcePileCount());
        if (scn.AwaitingDSDeployPhaseActions())
            scn.DSPass();
        assertTrue("LS should be able to act: " + scn.LSGetDecision(), scn.LSGetDecision() != null);
        assertFalse(scn.LSCardActionAvailable(bluffs, "Flip over a 'bluff card' here"));
    }

    @Test
    public void FlipIsFreeWithFourBluffCardsOrWithoutQuietlyObserving() {
        var scn = GetScenario();
        var quietly = scn.GetDSCard("quietly");
        var bluffs = scn.GetDSCard("bluffs");

        scn.StartGame();
        scn.MoveLocationToTable(bluffs);
        stackBluffCards(scn, bluffs, scn.GetDSCard("bluff1"));
        assertEquals(0, scn.game().getModifiersQuerying().getExtraForceCostToFlipBluffCard(scn.gameState(), bluffs, scn.LS));
        scn.MoveCardsToDSSideOfTable(quietly);
        stackBluffCards(scn, bluffs, scn.GetDSCard("bluff2"), scn.GetDSCard("bluff3"), scn.GetDSCard("bluff4"));
        assertEquals(0, scn.game().getModifiersQuerying().getExtraForceCostToFlipBluffCard(scn.gameState(), bluffs, scn.LS));
    }

    /** Walks through the responses after a battle is initiated and checks the 'react' is never offered. */
    private void assertNoReactOfferedDuringBattleStart(VirtualTableScenario scn) {
        assertTrue("battle started", scn.IsActiveBattle());
        for (int i = 0; i < 8 && scn.IsActiveBattle() && !scn.AwaitingDSWeaponsSegmentActions() && !scn.AwaitingLSWeaponsSegmentActions(); i++) {
            if (scn.DSGetDecision() != null) {
                assertFalse(scn.DSActionAvailable("Deploy a 'bluff card' as a 'react'"));
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                break;
            }
        }
    }

    @Test
    public void QuietlyObservingDeploysABluffCharacterAsAReactToABattleOncePerBattle() {
        var scn = GetScenario();
        var quietly = scn.GetDSCard("quietly");
        var bluffs = scn.GetDSCard("bluffs");
        var pilot = scn.GetDSCard("pilot");
        var bluff1 = scn.GetDSCard("bluff1");
        var bluff2 = scn.GetDSCard("bluff2");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(bluffs);
        scn.MoveCardsToDSSideOfTable(quietly);
        stackBluffCards(scn, bluffs, bluff1, bluff2);
        scn.MoveCardsToLocation(site, pilot, luke);
        scn.DSActivateForceCheat(6);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        int force = scn.GetDSForcePileCount();
        assertTrue("actions=" + scn.GetDSAvailableActions(), scn.DSActionAvailable("Deploy a 'bluff card' as a 'react'"));
        scn.DSChooseAction("Deploy a 'bluff card' as a 'react'");
        assertTrue(scn.DSHasCardChoiceAvailable(bluff1));
        scn.DSChooseCard(bluff1);
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
            scn.PassAllResponses();
        }
        assertEquals(site, bluff1.getAtLocation());
        assertEquals(force - 2, scn.GetDSForcePileCount());
        // once per battle
        assertFalse(scn.DSGetDecision() != null && scn.DSActionAvailable("Deploy a 'bluff card' as a 'react'"));
    }

    @Test
    public void QuietlyObservingDoesNotReactWithANonCharacterBluffCard() {
        var scn = GetScenario();
        var quietly = scn.GetDSCard("quietly");
        var bluffs = scn.GetDSCard("bluffs");
        var pilot = scn.GetDSCard("pilot");
        var effect = scn.GetDSCard("effect");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(bluffs);
        scn.MoveCardsToDSSideOfTable(quietly);
        stackBluffCards(scn, bluffs, effect);
        scn.MoveCardsToLocation(site, pilot, luke);
        scn.DSActivateForceCheat(6);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        assertNoReactOfferedDuringBattleStart(scn);
    }

    @Test
    public void QuietlyObservingDoesNotReactToABattleYouInitiate() {
        var scn = GetScenario();
        var quietly = scn.GetDSCard("quietly");
        var bluffs = scn.GetDSCard("bluffs");
        var pilot = scn.GetDSCard("pilot");
        var bluff1 = scn.GetDSCard("bluff1");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(bluffs);
        scn.MoveCardsToDSSideOfTable(quietly);
        stackBluffCards(scn, bluffs, bluff1);
        scn.MoveCardsToLocation(site, pilot, luke);
        scn.DSActivateForceCheat(6);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        assertNoReactOfferedDuringBattleStart(scn);
    }
}
