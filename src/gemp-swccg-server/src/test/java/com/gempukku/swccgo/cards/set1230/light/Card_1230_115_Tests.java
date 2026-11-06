package com.gempukku.swccgo.cards.set1230.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_115_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{

            put("rifle", "1230_115");
            put("rifle2", "1230_115");
            put("rifle3", "1230_115");

            put("scout", "8_5");
            put("scout1", "8_10");
            put("scout2", "8_10");
            put("scout3", "8_10");

            put("luke", "1_19");
            put("alien", "6_41");
            put("door", "8_69");

        }}, new HashMap<>() {{

            put("vader", "1_168");
            put("trooper", "1_194");
            put("atst", "8_172");
            put("bubo", "6_138");
        }}, 30, 30, StartingSetup.LSStartingLocation("8_72"), StartingSetup.DefaultDSGroundLocation,
        StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
        StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void A280SharpshooterRifleStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rifle").getBlueprint();
        assertEquals("A280 Sharpshooter Rifle", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.RIFLE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_3);
            add(Icon.ENDOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void A280SharpshooterRifleDeploysOnlyOnYourRebelScoutForFree() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("scout"), scn.GetLSCard("luke"), scn.GetLSCard("alien"));
        scn.MoveCardsToLSHand(scn.GetLSCard("rifle"));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(scn.GetLSCard("rifle"));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("scout")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("alien")));
        scn.LSChooseCard(scn.GetLSCard("scout"));
        scn.PassAllResponses();
        assertEquals(scn.GetLSCard("scout"), scn.GetLSCard("rifle").getAttachedTo());
        assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void A280SharpshooterRifleAllowsTwoCopiesButNotAThird() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("scout1"), scn.GetLSCard("scout2"), scn.GetLSCard("scout3"));
        scn.MoveCardsToLSHand(scn.GetLSCard("rifle"), scn.GetLSCard("rifle2"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(scn.GetLSCard("rifle"));
        scn.LSChooseCard(scn.GetLSCard("scout1"));
        scn.PassAllResponses();
        if (scn.AwaitingDSDeployPhaseActions()) scn.DSPass();
        scn.LSDeployCard(scn.GetLSCard("rifle2"));
        scn.LSChooseCard(scn.GetLSCard("scout2"));
        scn.PassAllResponses();
        assertEquals(scn.GetLSCard("scout1"), scn.GetLSCard("rifle").getAttachedTo());
        assertEquals(scn.GetLSCard("scout2"), scn.GetLSCard("rifle2").getAttachedTo());
        scn.MoveCardsToLSHand(scn.GetLSCard("rifle3"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertInHand(scn.GetLSCard("rifle3"));
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(scn.GetLSCard("rifle3")));
    }

    @Test
    public void A280SharpshooterRifleControlShotCostsTwoAndLosesCharacterAtSameSite() {
        var scn = ControlReady("vader", false, 5);
        int force = scn.GetLSForcePileCount();
        int used = scn.GetLSUsedPileCount();
        ControlShot(scn, 5);
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("vader"));
        assertEquals(force - 2, scn.GetLSForcePileCount());
        assertEquals(used + 3, scn.GetLSUsedPileCount());
        assertTrue(scn.AwaitingLSControlPhaseActions() || scn.AwaitingDSControlPhaseActions());
    }

    @Test
    public void A280SharpshooterRifleControlShotCanHitCharacterAtAdjacentSite() {
        var scn = ControlReady("vader", true, 5); ControlShot(scn, 5);
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("vader"));
    }

    @Test
    public void A280SharpshooterRifleControlShotCanHitVehicleAtAdjacentSite() {
        var scn = ControlReady("atst", true, 5);
        assertEquals(4, scn.GetDefense(scn.GetDSCard("atst")));
        ControlShot(scn, 3);
        assertTrue(scn.DSDecisionAvailable("Choose card to put on Lost Pile"));
        scn.DSChooseCard(scn.GetDSCard("atst"));
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("atst"));
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("vader"));
    }

    @Test
    public void A280SharpshooterRifleControlShotCanHitCreature() {
        var scn = ControlReady("bubo", false, 5);
        assertEquals(5, scn.GetDefense(scn.GetDSCard("bubo")));
        ControlShot(scn, 4);
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("bubo"));
    }

    @Test
    public void A280SharpshooterRifleModifiedDestinyEqualToDefenseDoesNotHit() {
        var scn = ControlReady("vader", false, 5); ControlShot(scn, 4);
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        assertFalse(scn.GetDSCard("vader").isHit());
        assertInZone(Zone.USED_PILE, scn.GetLSDestiny(4));
    }

    @Test
    public void A280SharpshooterRifleControlShotRequiresTwoAvailableForce() {
        var scn = ControlReady("vader", false, 1);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(1, scn.GetLSForcePileCount());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("rifle"), "Fire during control phase"));
    }

    @Test
    public void A280SharpshooterRifleControlShotCannotTargetBeyondAdjacentSite() {
        var scn = ControlReady("vader", false, 5);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        // Re-enter Control with the target placement already established so the menu is fresh.
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("rifle"), "Fire during control phase"));
    }

    @Test
    public void A280SharpshooterRifleControlShotIsOncePerPhaseAndResetsNextTurn() {
        var scn = ControlReady("vader", false, 5); ControlShot(scn, 0);
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("rifle"), "Fire during control phase"));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("rifle"), "Fire during control phase")); ControlShot(scn, 5);
        assertInZone(Zone.LOST_PILE, scn.GetDSCard("vader"));
    }

    @Test
    public void A280SharpshooterRifleControlAbilityUnavailableInDeployPhase() {
        var scn = ControlReady("vader", false, 5);
        scn.SkipToPhase(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("rifle"), "Fire during control phase"));
    }

    @Test
    public void A280SharpshooterRifleCanFireForFreeInBattleAfterFiringDuringControl() {
        var scn = ControlReady("vader", false, 5); ControlShot(scn, 0);
        scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        int force = scn.GetLSForcePileCount();
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("rifle"), "Fire"));
        scn.LSUseCardAction(scn.GetLSCard("rifle"), "Fire");
        scn.LSChooseCard(scn.GetDSCard("vader"));
        scn.PassAllResponses();
        assertTrue(scn.GetDSCard("vader").isHit());
        assertEquals(force, scn.GetLSForcePileCount());
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader"));
        assertInZone(Zone.USED_PILE, scn.GetLSDestiny(5));
        assertTrue(scn.AwaitingLSWeaponsSegmentActions() || scn.AwaitingDSWeaponsSegmentActions());
    }

    @Test
    public void A280SharpshooterRifleAddsOneAttritionAtItsOwnSite() { CheckAttrition(false, 4); }
    @Test
    public void A280SharpshooterRifleDoesNotAddAttritionAtAdjacentSite() { CheckAttrition(true, 3); }
    private void CheckAttrition(boolean rifleElsewhere, int expected) {
        var scn = GetScenario();
        scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(scn.GetLSCard("door"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("vader"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLocation(rifleElsewhere ? scn.GetLSCard("door") : site, scn.GetLSCard("scout"));
        scn.AttachCardsTo(scn.GetLSCard("scout"), scn.GetLSCard("rifle"));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(3);
        scn.SkipToPowerSegment();
        scn.PassAllResponses();
        scn.LSChooseYes();
        scn.PassAllResponses();
        scn.DSChooseNo();
        scn.PassAllResponses();
        scn.SkipToDamageSegment(false);
        scn.PassAllResponses();
        assertEquals(expected, scn.GetUnpaidDSAttrition());
    }

    private VirtualTableScenario ControlReady(String target, boolean adjacent, int force) {
        var scn = GetScenario();
        scn.StartGame();
        scn.SkipToLSTurn(Phase.ACTIVATE);
        var site = scn.GetLSStartingLocation();
        scn.MoveLocationToTable(scn.GetLSCard("door"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("scout"));
        scn.AttachCardsTo(scn.GetLSCard("scout"), scn.GetLSCard("rifle"));
        scn.MoveCardsToLocation(adjacent ? scn.GetLSCard("door") : site, scn.GetDSCard(target));
        if (target.equals("atst")) scn.BoardAsPilot(scn.GetDSCard("atst"), scn.GetDSCard("vader"));
        scn.LSChooseAction("Activate Force");
        int activated = Math.min(scn.GetLSReserveDeckCount() - 1, scn.LSGetChoiceMax());
        scn.LSDecided(activated);
        if (scn.DSDecisionAvailable("Choose amount of Force to allow opponent")) scn.DSDecided(activated);
        scn.EnsureLSForcePile(force);
        scn.DrainLSForcePileTo(force);
        scn.PassAllResponses();
        scn.PassActivateActions();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSControlPhaseActions());
        return scn;
    }

    private void ControlShot(VirtualTableScenario scn, int destiny) {
        scn.PrepareLSDestiny(destiny);
        scn.LSUseCardAction(scn.GetLSCard("rifle"), "Fire during control phase");
        scn.PassCardAndForceUseResponses();
        scn.PassAllResponses();
    }
}
