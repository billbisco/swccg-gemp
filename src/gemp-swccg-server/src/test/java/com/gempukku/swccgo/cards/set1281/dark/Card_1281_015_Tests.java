package com.gempukku.swccgo.cards.set1281.dark;

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
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("grimtaash", "2_49"); // Grimtaash
                    put("camp", "11_21"); // The Camp
                    put("cantina", "1_128"); // Tatooine: Cantina
                    put("tatooine", "1_127"); // Tatooine system
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("wellguarded", "1281_15");
                    put("guard1", "6_105"); // Gamorrean Guard: non-unique alien, forfeit 1
                    put("guard2", "6_105");
                    put("ortugg", "6_118"); // Ortugg: unique Gamorrean
                    put("trooper1", "1_194");
                    put("trooper2", "1_194");
                }},
                20, 20,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation, // Tatooine: Marketplace
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void WellGuardedVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("wellguarded").getBlueprint();
        assertEquals("Well Guarded", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WellGuardedVDeploysOnTable() {
        var scn = GetScenario();
        var wellguarded = scn.GetDSCard("wellguarded");
        scn.StartGame();
        scn.MoveCardsToHand(wellguarded);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(wellguarded));
        scn.DSDeployCard(wellguarded);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, wellguarded);
    }

    @Test
    public void WellGuardedVYourNonUniqueAliensInHandAreImmuneToGrimtaash() {
        var scn = GetScenario();
        var wellguarded = scn.GetDSCard("wellguarded");
        var grimtaash = scn.GetLSCard("grimtaash");
        var guard1 = scn.GetDSCard("guard1");
        var ortugg = scn.GetDSCard("ortugg");
        var trooper1 = scn.GetDSCard("trooper1");
        scn.StartGame();
        scn.MoveCardsToHand(guard1, ortugg, trooper1, grimtaash);
        scn.SkipToDSTurn(Phase.DEPLOY);
        var query = scn.game().getModifiersQuerying();
        var lost = java.util.Collections.singleton(com.gempukku.swccgo.common.TargetingReason.TO_BE_LOST);
        assertTrue(query.canBeTargetedBy(scn.gameState(), guard1, grimtaash, lost));
        scn.MoveCardsToDSSideOfTable(wellguarded);
        scn.DSPass();
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertFalse(query.canBeTargetedBy(scn.gameState(), guard1, grimtaash, lost));
        assertTrue(query.canBeTargetedBy(scn.gameState(), ortugg, grimtaash, lost));
        assertTrue(query.canBeTargetedBy(scn.gameState(), trooper1, grimtaash, lost));
    }

    @Test
    public void WellGuardedVGrimtaashImmunityEndsAt13CardsInHand() {
        var scn = GetScenario();
        var wellguarded = scn.GetDSCard("wellguarded");
        var guard1 = scn.GetDSCard("guard1");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(wellguarded);
        scn.MoveCardsToHand(guard1);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), guard1, Title.Grimtaash));
        for (int i = 1; i <= 20 && scn.GetDSHand().size() < 13; i++) {
            scn.MoveCardsToHand(scn.GetDSFiller(i));
        }
        assertEquals(13, scn.GetDSHand().size());
        assertFalse(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), guard1, Title.Grimtaash));
    }

    @Test
    public void WellGuardedVYourNonUniqueAliensAreImmuneToTheCampAndItsDeployCosts() {
        var scn = GetScenario();
        var wellguarded = scn.GetDSCard("wellguarded");
        var camp = scn.GetLSCard("camp");
        var guard1 = scn.GetDSCard("guard1");
        var ortugg = scn.GetDSCard("ortugg");
        var site = scn.GetLSCard("cantina");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("tatooine"));
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLSSideOfTable(camp);
        scn.MoveCardsToHand(guard1, ortugg);
        scn.SkipToDSTurn(Phase.DEPLOY);
        var query = scn.game().getModifiersQuerying();
        assertFalse(query.isImmuneToDeployCostToTargetModifierFromCard(scn.gameState(), guard1, site, camp));
        assertFalse(query.isImmuneToCardTitle(scn.gameState(), guard1, Title.The_Camp));
        scn.MoveCardsToDSSideOfTable(wellguarded);
        scn.DSPass();
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertTrue(query.isImmuneToDeployCostToTargetModifierFromCard(scn.gameState(), guard1, site, camp));
        assertTrue(query.isImmuneToCardTitle(scn.gameState(), guard1, Title.The_Camp));
        assertFalse(query.isImmuneToDeployCostToTargetModifierFromCard(scn.gameState(), ortugg, site, camp));
    }

    private void BattleWithGuards(VirtualTableScenario scn, boolean twoGuards) {
        var wellguarded = scn.GetDSCard("wellguarded");
        var guard1 = scn.GetDSCard("guard1");
        var guard2 = scn.GetDSCard("guard2");
        var ortugg = scn.GetDSCard("ortugg");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.gameState().setRep(scn.DS, ortugg);
        scn.MoveCardsToDSSideOfTable(wellguarded);
        if (twoGuards) {
            scn.MoveCardsToLocation(site, guard1, guard2, luke);
        }
        else {
            scn.MoveCardsToLocation(site, guard1, luke);
        }
        scn.ApplyAdHocModifier(new ForfeitModifier(luke, Filters.or(Filters.sameCardId(guard1), Filters.sameCardId(guard2)), -1));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(2);
        scn.DSInitiateBattle(site);
        assertTrue(scn.IsBattleStarted());
    }

    @Test
    public void WellGuardedVAliensMayNotHaveForfeitReducedInBattleWithTwoNonUniqueAliensOfRepsSpecies() {
        var scn = GetScenario();
        BattleWithGuards(scn, true);
        assertEquals(1, scn.GetForfeit(scn.GetDSCard("guard1")));
    }

    @Test
    public void WellGuardedVForfeitMayBeReducedWithOnlyOneAlienOfRepsSpeciesInBattle() {
        var scn = GetScenario();
        BattleWithGuards(scn, false);
        assertEquals(0, scn.GetForfeit(scn.GetDSCard("guard1")));
    }
}
