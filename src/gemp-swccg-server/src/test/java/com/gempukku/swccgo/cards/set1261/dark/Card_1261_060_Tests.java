package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_060_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("han", "1_11"); put("worrt", "6_48"); }},
                new HashMap<>() {{ put("rifle", "1261_60"); put("boba", "5_91"); put("otherBoba", "7_167"); put("jpBoba", "601_2");
                    put("vader", "1_168"); put("oldRifle", "5_179"); put("bossk", "4_100"); put("officer", "1_166"); put("delivery", "1220_45"); put("hidden", "6_154"); }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void FettsBlasterRiflePrintedMetadataMatchesSource() {
        var scn = scenario(); var b = scn.GetDSCard("rifle").getBlueprint();
        assertEquals("Fett's Blaster Rifle", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.DARK, b.getSide());
        assertEquals(2, b.getDestiny(), scn.epsilon); assertEquals(CardSubtype.CHARACTER, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintPersonaCheck(b, new ArrayList<>() {{ add(Persona.BOBA_FETTS_BLASTER_RIFLE); }});
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.WEAPON); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.BLASTER_RIFLE); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.WEAPON); add(Icon.CLOUD_CITY); add(Icon.LEGACY_BLOCK_6); }});
    }

    @Test public void FettsBlasterRifleActuallyDeploysOnCloudCityBobaForFree() {
        var scn = scenario(); scn.StartGame(); var boba = scn.GetDSCard("boba"); var rifle = scn.GetDSCard("rifle");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), boba); scn.MoveCardsToDSHand(rifle);
        scn.SkipToDSTurn(Phase.DEPLOY); int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(rifle); if (scn.DSHasCardChoiceAvailable(boba)) scn.DSChooseCard(boba); scn.PassAllResponses();
        assertSame(boba, rifle.getAttachedTo()); assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test public void FettsBlasterRifleActuallyDeploysOnJabbasPalaceBobaForFree() {
        var scn = scenario(); scn.StartGame(); var boba = scn.GetDSCard("jpBoba"); var rifle = scn.GetDSCard("rifle");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), boba); scn.MoveCardsToDSHand(rifle);
        scn.SkipToDSTurn(Phase.DEPLOY); int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(rifle); if (scn.DSHasCardChoiceAvailable(boba)) scn.DSChooseCard(boba); scn.PassAllResponses();
        assertSame(boba, rifle.getAttachedTo()); assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test public void FettsBlasterRifleRejectsOtherEditionBobaAndOrdinaryWarrior() {
        var scn = scenario(); scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("otherBoba"), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("rifle")); scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("rifle")));
    }

    private VirtualTableScenario battle(boolean rifle, boolean naturalAbility) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("boba"), scn.GetLSCard("obiwan"), scn.GetLSCard("han"));
        if (rifle) scn.AttachCardsTo(scn.GetDSCard("boba"), scn.GetDSCard("rifle"));
        if (naturalAbility) scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("hidden"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); scn.PrepareDSDestiny(4); return scn;
    }

    @Test public void FettsBlasterRifleGrantsExactlyOneBattleDestinyWhenOtherwiseUnable() {
        assertEquals(0, battle(false, false).GetDSBattleDestinyCount());
        assertEquals(1, battle(true, false).GetDSBattleDestinyCount());
        assertEquals(1, battle(true, true).GetDSBattleDestinyCount());
    }

    private void fire(VirtualTableScenario scn, int destiny) {
        int force = scn.GetDSForcePileCount(); scn.PrepareDSDestiny(destiny); int reserve = scn.GetDSReserveDeckCount();
        scn.DSUseCardAction(scn.GetDSCard("rifle"), "Fire");
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("obiwan"))) scn.DSChooseCard(scn.GetLSCard("obiwan"));
        scn.PassAllResponses(); assertEquals(force, scn.GetDSForcePileCount()); assertEquals(reserve - 1, scn.GetDSReserveDeckCount());
    }

    @Test public void FettsBlasterRifleFreeSingleDestinyPlusOneHitsAboveDefenseAndResetsForfeit() {
        var scn = battle(true, false); assertEquals(6, scn.GetDefense(scn.GetLSCard("obiwan")));
        fire(scn, 6); assertTrue(scn.GetLSCard("obiwan").isHit()); assertEquals(0, scn.GetForfeit(scn.GetLSCard("obiwan")));
    }

    @Test public void FettsBlasterRifleEqualDefenseMissesAndPreservesForfeit() {
        var scn = battle(true, false); int forfeit = scn.GetForfeit(scn.GetLSCard("obiwan"));
        fire(scn, 5); assertFalse(scn.GetLSCard("obiwan").isHit()); assertEquals(forfeit, scn.GetForfeit(scn.GetLSCard("obiwan")));
    }

    @Test public void FettsBlasterRifleCannotFireAtCreature() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("boba"), scn.GetLSCard("worrt"));
        scn.AttachCardsTo(scn.GetDSCard("boba"), scn.GetDSCard("rifle"));
        scn.DSActivateMaxForceAndPass(); scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(site, "Initiate attack on creature"); scn.PassAllResponses();
        assertTrue(scn.gameState().isDuringAttack()); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("rifle"), "Fire"));
    }

    @Test public void FettsBlasterRifleDeploysAndReshufflesWithoutInventedSearchCapButPreservesUniqueTurnLimit() {
        var scn = scenario(); scn.StartGame(); var rifle = scn.GetDSCard("rifle"); var delivery = scn.GetDSCard("delivery");
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("boba")); scn.AttachCardsTo(scn.GetDSCard("boba"), rifle);
        scn.MoveCardsToDSHand(scn.GetDSCard("officer"));
        scn.SkipToDSTurn(Phase.DEPLOY); scn.MoveCardsToTopOfDSReserveDeck(delivery);
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.RESERVE_DECK, delivery); int force = scn.GetDSForcePileCount();
        var remaining = scn.GetDSReserveDeck().stream().filter(c -> c != delivery).findFirst().orElseThrow();
        scn.gameState().markCardsRevealedFromPile(java.util.Collections.singletonList(remaining));
        assertTrue(scn.gameState().isCardRevealedFromPile(remaining));
        scn.DSUseCardAction(rifle, "Deploy Special Delivery from Reserve Deck"); scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(delivery)) scn.DSChooseCard(delivery); scn.PassAllResponses();
        assertEquals(scn.GetCurrentDecision().getText(), Zone.SIDE_OF_TABLE, delivery.getZone()); assertEquals(force, scn.GetDSForcePileCount());
        assertTrue(scn.GetDSReserveDeck().contains(remaining)); assertFalse(scn.gameState().isCardRevealedFromPile(remaining));
        if (scn.AwaitingLSDeployPhaseActions()) scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(rifle, "Deploy Special Delivery from Reserve Deck"));
        scn.MoveCardsToTopOfDSReserveDeck(delivery);
        // A normal deployment refreshes the action menu after the manual removal fixture.
        scn.DSDeployCard(scn.GetDSCard("officer"));
        if (scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation())) scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses(); if (scn.AwaitingLSDeployPhaseActions()) scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(force - 2, scn.GetDSForcePileCount());
        assertTrue("No printed timeframe prevents another deployment after Delivery leaves table", scn.DSCardActionAvailable(rifle, "Deploy Special Delivery from Reserve Deck"));
        scn.DSUseCardAction(rifle, "Deploy Special Delivery from Reserve Deck"); scn.PassAllResponses();
        // The search can repeat, but normal uniqueness still forbids deploying this unique title twice in one turn.
        assertTrue(scn.DSDecisionAvailable("Verify Reserve Deck")); assertTrue(scn.LSDecisionAvailable("Verify Reserve Deck"));
        assertTrue(scn.game().getModifiersQuerying().isPlayingCardTitleTurnLimitReached(scn.gameState(), delivery));
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.RESERVE_DECK, delivery);
        scn.DSPass(); scn.LSPass(); scn.PassAllResponses();
        assertEquals(force - 2, scn.GetDSForcePileCount());
        scn.SkipToPhase(Phase.MOVE);
        assertFalse(scn.DSCardActionAvailable(rifle, "Deploy Special Delivery from Reserve Deck"));
    }

    @Test public void FettsBlasterRifleCancelsActualHiddenWeaponsBeforeItsDestinyDraw() {
        var scn = battle(true, false);
        int reserve = scn.GetDSReserveDeckCount();
        scn.DSPlayCard(scn.GetDSCard("hidden"));
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("obiwan"))) scn.DSChooseCard(scn.GetLSCard("obiwan"));
        scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.LOST_PILE, scn.GetDSCard("hidden"));
        assertFalse(scn.GetLSCard("obiwan").isHit()); assertEquals(reserve, scn.GetDSReserveDeckCount());
    }

    @Test public void HiddenWeaponsStillResolvesWithoutFettsBlasterRifle() {
        var scn = battle(false, false);
        int reserve = scn.GetDSReserveDeckCount();
        scn.DSPlayCard(scn.GetDSCard("hidden"));
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("obiwan"))) scn.DSChooseCard(scn.GetLSCard("obiwan"));
        scn.PassAllResponses();
        assertTrue(scn.GetLSCard("obiwan").isHit()); assertEquals(reserve - 1, scn.GetDSReserveDeckCount());
    }
    @Test public void FettsBlasterRifleMatchingPairRequiresAnEligibleBobaWhoCanUseIt() {
        var scn = scenario(); var matching = scn.GetDSCard("rifle").getBlueprint().getMatchingCharacterFilter();
        assertTrue(matching.accepts(scn.game(), scn.GetDSCard("boba")));
        assertTrue(matching.accepts(scn.game(), scn.GetDSCard("jpBoba")));
        assertFalse(matching.accepts(scn.game(), scn.GetDSCard("otherBoba")));
        assertFalse(matching.accepts(scn.game(), scn.GetDSCard("vader")));
    }

    @Test public void FettsBlasterRifleCannotDuplicateExistingBobaRifleWeaponPersona() {
        var scn = scenario(); scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("boba"), scn.GetDSCard("bossk"));
        scn.AttachCardsTo(scn.GetDSCard("bossk"), scn.GetDSCard("oldRifle"));
        scn.MoveCardsToDSHand(scn.GetDSCard("rifle")); scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("rifle")));
    }
}
