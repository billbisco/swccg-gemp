package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1263_021_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{put("luke","1_19");put("leia","1_17");}},
                new HashMap<>() {{put("walker","1263_21");put("mtt","14_122");put("mtt2","14_122");put("cannon","3_158");put("other","3_154");put("trooper","1_194");put("vader","1_168");put("veers","3_87");put("rifle","1_317");}},
                12,12,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void DroidWalkerStatsIconsAndKeywordsMatchPrintedImage() {
        var scn=scenario();var b=scn.GetDSCard("walker").getBlueprint();
        assertEquals("Droid Walker",b.getTitle());assertFalse(b.hasVirtualSuffix());assertTrue(b.isLegacy());
        assertEquals(Side.DARK,b.getSide());assertEquals(Uniqueness.UNRESTRICTED,b.getUniqueness());assertEquals(CardSubtype.COMBAT,b.getCardSubtype());
        assertEquals(2,b.getDestiny(),scn.epsilon);assertEquals(3,b.getDeployCost(),scn.epsilon);assertEquals(4,b.getPower(),scn.epsilon);
        assertEquals(4,b.getArmor(),scn.epsilon);assertEquals(2,b.getLandspeed(),scn.epsilon);assertEquals(5,b.getForfeit(),scn.epsilon);
        assertEquals("Enclosed.",b.getLore());assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        scn.BlueprintCardTypeCheck(b,new ArrayList<>() {{add(CardType.VEHICLE);}});
        scn.BlueprintIconCheck(b,new ArrayList<>() {{add(Icon.VEHICLE);add(Icon.PILOT);add(Icon.PRESENCE);add(Icon.A_NEW_HOPE);add(Icon.EPISODE_I);add(Icon.LEGACY_BLOCK_6);}});
        scn.BlueprintKeywordCheck(b,new ArrayList<>() {{add(Keyword.ENCLOSED);}});
        assertTrue(b.getModelTypes().isEmpty());
    }
    private VirtualTableScenario deploy(String mtt,int count,int force) {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();
        scn.MoveCardsToDSHand(scn.GetDSCard("walker"));
        if(mtt!=null) {
            var transport=scn.GetDSCard("mtt");
            if("opponent".equals(mtt))transport.setOwner(scn.LS);
            scn.MoveCardsToLocation("otherSite".equals(mtt)?scn.GetLSStartingLocation():site,transport);
        }
        if(count==2)scn.MoveCardsToLocation(site,scn.GetDSCard("mtt2"));
        scn.SkipToDSTurn(Phase.CONTROL);scn.EnsureDSForcePile(force);
        while(scn.GetDSForcePileCount()>force)scn.MoveCardsToTopOfDSReserveDeck((PhysicalCardImpl)scn.game().getGameState().getForcePile(scn.DS).getFirst());
        scn.SkipToPhase(Phase.DEPLOY);return scn;
    }
    private void deployWalker(VirtualTableScenario scn,int cost) {
        var card=scn.GetDSCard("walker");int before=scn.GetDSForcePileCount();assertTrue(scn.DSDeployAvailable(card));
        scn.DSDeployCard(card);if(scn.DSHasCardChoiceAvailable(scn.GetDSStartingLocation()))scn.DSChooseCard(scn.GetDSStartingLocation());scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION,card.getZone());assertEquals(before-cost,scn.GetDSForcePileCount());
        assertEquals(4,scn.GetPower(card));assertEquals(0,scn.GetAbility(card));
    }
    @Test public void DroidWalkerDeploysForTwoForceToSameSiteAsOwnMTT() {deployWalker(deploy("own",1,2),2);}
    @Test public void DroidWalkerMultipleMTTsDoNotMultiplyDeploymentDiscount() {deployWalker(deploy("own",2,2),2);}
    @Test public void DroidWalkerStillCostsThreeWithoutMTT() {deployWalker(deploy(null,0,3),3);}
    @Test public void DroidWalkerOpponentsMTTDoesNotDiscountDeployment() {deployWalker(deploy("opponent",1,3),3);}
    @Test public void DroidWalkerMTTAtAnotherSiteDoesNotDiscountDeployment() {deployWalker(deploy("otherSite",1,3),3);}
    @Test public void DroidWalkerCannotDeployWithOnlyOneForceEvenWithMTT() {
        var scn=deploy("own",1,1);assertNotNull(scn.DSGetDecision());assertFalse(scn.DSDeployAvailable(scn.GetDSCard("walker")));
    }
    private VirtualTableScenario battle(String companion) {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,scn.GetDSCard("walker"),scn.GetLSCard("luke"));
        if(companion!=null)scn.MoveCardsToLocation(site,scn.GetDSCard(companion));
        scn.SkipToLSTurn(Phase.BATTLE);scn.PrepareDSDestiny(3);scn.PrepareLSDestiny(0);
        scn.LSInitiateBattle(site);scn.SkipToEndOfPowerSegment(true);return scn;
    }
    @Test public void DroidWalkerPresenceAndPilotPermitPowerWithoutAddingUnprintedAbility() {
        var scn=battle(null);assertEquals(4,scn.GetPower(scn.GetDSCard("walker")));assertEquals(0,scn.GetAbility(scn.GetDSCard("walker")));
    }
    @Test public void DroidWalkerDrawsOneActualBattleDestinyWithCompanionAndInsufficientAbility() {assertEquals(3,battle("trooper").GetDSTotalDestiny());}
    @Test public void DroidWalkerDrawsNoDestinyWhenAloneDespiteOpposingCharacter() {assertEquals(0,battle(null).GetDSTotalDestiny());}
    @Test public void DroidWalkerDoesNotAddAnotherDestinyWhenAlreadyAbleToDraw() {
        var scn=battle("vader");assertEquals(1,scn.GetDSBattleDestinyCount());assertEquals(4,scn.GetDSTotalDestiny());
    }
    @Test public void DroidWalkerAllowsActualATATCannonDeploymentAtNormalTwoForceCost() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("walker"),scn.GetDSCard("other"));
        scn.MoveCardsToDSHand(scn.GetDSCard("cannon"));scn.SkipToDSTurn(Phase.DEPLOY);int before=scn.GetDSForcePileCount();
        scn.DSDeployCard(scn.GetDSCard("cannon"));scn.DSChooseCard(scn.GetDSCard("walker"));scn.PassAllResponses();
        assertEquals(scn.GetDSCard("walker"),scn.GetDSCard("cannon").getAttachedTo());assertEquals(before-2,scn.GetDSForcePileCount());
    }
    @Test public void DroidWalkerDoesNotGrantCannonDeploymentToAnUnrelatedNonATAT() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("walker"),scn.GetDSCard("mtt"),scn.GetDSCard("other"));
        scn.MoveCardsToDSHand(scn.GetDSCard("cannon"));scn.SkipToDSTurn(Phase.DEPLOY);scn.DSDeployCard(scn.GetDSCard("cannon"));
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("walker")));assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("mtt")));
    }
    @Test public void DroidWalkerDoesNotGrantDeploymentForAnUnrelatedWeapon() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("walker"),scn.GetDSCard("trooper"),scn.GetDSCard("veers"));
        scn.MoveCardsToDSHand(scn.GetDSCard("rifle"));scn.SkipToDSTurn(Phase.DEPLOY);scn.DSDeployCard(scn.GetDSCard("rifle"));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("walker")));assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("trooper")));
    }
    private VirtualTableScenario fire(int destiny,boolean aboardWalker) {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site,scn.GetDSCard("walker"),scn.GetDSCard("trooper"),scn.GetLSCard("luke"),scn.GetLSCard("leia"));
        if(!aboardWalker) {
            scn.MoveCardsToLocation(site,scn.GetDSCard("other"));scn.MoveCardsToDSHand(scn.GetDSCard("veers"));scn.BoardAsPilot(scn.GetDSCard("other"),scn.GetDSCard("veers"));
        }
        scn.AttachCardsTo(scn.GetDSCard(aboardWalker?"walker":"other"),scn.GetDSCard("cannon"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site);scn.PrepareDSDestiny(destiny);int force=scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("cannon"),"Fire"));scn.DSUseCardAction(scn.GetDSCard("cannon"),"Fire");scn.DSChooseCard(scn.GetLSCard("luke"));
        scn.PassForceUseResponses();scn.PassResponses("Fire ");scn.PassDestinyDrawResponses();scn.PassResponses("ABOUT_TO_BE_HIT");
        for(int i=0;i<8;i++) {
            scn.PassAllResponses();
            if(scn.DSDecisionAvailable("Required responses"))scn.DSChooseAction("Hit target may not satisfy attrition");else break;
        }
        assertEquals(force-2,scn.GetDSForcePileCount());return scn;
    }
    private boolean cannotSatisfy(VirtualTableScenario scn,String target) {return scn.game().getModifiersQuerying().cannotSatisfyAttrition(scn.game().getGameState(),scn.GetLSCard(target));}
    @Test public void DroidWalkerActualCannonHitPreventsOnlyHitTargetFromSatisfyingAttrition() {
        var scn=fire(4,true);assertTrue(scn.GetLSCard("luke").isHit());assertTrue(cannotSatisfy(scn,"luke"));assertFalse(cannotSatisfy(scn,"leia"));
    }
    @Test public void DroidWalkerCannonMissBelowDefenseDoesNotPreventAttritionPayment() {
        var scn=fire(2,true);assertFalse(scn.GetLSCard("luke").isHit());assertFalse(cannotSatisfy(scn,"luke"));
    }
    @Test public void DroidWalkerCannonMissEqualToDefenseDoesNotPreventAttritionPayment() {
        var scn=fire(3,true);assertFalse(scn.GetLSCard("luke").isHit());assertFalse(cannotSatisfy(scn,"luke"));
    }
    @Test public void DroidWalkerDoesNotChangeHitsByCannonAboardAnotherVehicle() {
        var scn=fire(4,false);assertTrue(scn.GetLSCard("luke").isHit());assertFalse(cannotSatisfy(scn,"luke"));
    }
    @Test public void DroidWalkerHitTargetActuallyForfeitsWithoutPayingAttrition() {
        var scn=fire(4,true);scn.PrepareDSDestiny(5);scn.PrepareLSDestiny(0);scn.SkipToDamageSegment(true);
        assertEquals(5,scn.GetUnpaidLSAttrition());scn.LSChooseCard(scn.GetLSCard("luke"));scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE,scn.GetLSCard("luke"));assertEquals(5,scn.GetUnpaidLSAttrition());
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("leia")));
    }
    @Test public void DroidWalkerDoesNotAcceptAnUnprintedAdditionalPilot() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("walker"),scn.GetDSCard("other"));
        scn.MoveCardsToDSHand(scn.GetDSCard("veers"));scn.SkipToDSTurn(Phase.DEPLOY);scn.DSDeployCard(scn.GetDSCard("veers"));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("walker")));assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("other")));
    }
    @Test public void DroidWalkerDoesNotAcceptAnUnprintedPassenger() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetDSCard("walker"),scn.GetDSCard("other"));
        scn.MoveCardsToDSHand(scn.GetDSCard("trooper"));scn.SkipToDSTurn(Phase.DEPLOY);scn.DSDeployCard(scn.GetDSCard("trooper"));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetDSCard("walker")));assertTrue(scn.DSHasCardChoiceAvailable(scn.GetDSCard("other")));
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // PC card list (PC-E1) is the latest text ("cannot be used"; same play).
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(), new java.util.HashMap<>(java.util.Map.of("c", "1263_21")), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetDSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("Deploys -1 to same site as your MTT. AT-AT Cannon may deploy aboard, and targets it hits may not be used to satisfy attrition. Unless alone, draws one battle destiny if not able to otherwise.", text);
        org.junit.Assert.assertFalse(text.contains("may not be used"));
        org.junit.Assert.assertNull(scn.GetDSCard("c").getBlueprint().getLegacyVersion());
    }
}
