package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1250_025_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{put("luke","1_19");put("leia","1_17");put("obi","108_4");put("quiet","4_89");put("related","4_85");put("alter","1_71");put("controlEffect","1_45");}},
                new HashMap<>() {{put("wipe","1250_25");put("boba","108_5");put("vader","1_168");put("trooper","1_194");put("blaster","1_317");put("device","1_201");put("controlEffect","1_224");}},
                15,15,StartingSetup.DefaultLSGroundLocation,StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void WipeThemOutAllOfThemStatsIconsAndKeywordsMatchPrintedImage() {
        var scn=scenario();var b=scn.GetDSCard("wipe").getBlueprint();
        assertEquals("Wipe Them Out, All Of Them",b.getTitle());assertTrue(b.hasVirtualSuffix());assertTrue(b.isLegacy());assertEquals(Side.DARK,b.getSide());
        assertEquals(5,b.getDestiny(),scn.epsilon);assertEquals(Uniqueness.UNIQUE,b.getUniqueness());assertEquals(CardSubtype.NORMAL,b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5,b.getExpansionSet());assertEquals(Rarity.V,b.getRarity());
        assertEquals("Darth Sidious' command was merciless and direct. He left no room for misinterpretation.",b.getLore());
        scn.BlueprintCardTypeCheck(b,new ArrayList<>() {{add(CardType.EFFECT);}});
        scn.BlueprintIconCheck(b,new ArrayList<>() {{add(Icon.EFFECT);add(Icon.CORUSCANT);add(Icon.EPISODE_I);add(Icon.LEGACY_BLOCK_5);}});
        scn.BlueprintKeywordCheck(b,new ArrayList<>() {{add(Keyword.DEPLOYS_ON_SITE);}});
    }
    @Test public void WipeThemOutAllOfThemActuallyDeploysOnAnySiteForFree() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToDSHand(scn.GetDSCard("wipe"));scn.SkipToDSTurn(Phase.DEPLOY);int before=scn.GetDSForcePileCount();
        scn.DSDeployCard(scn.GetDSCard("wipe"));scn.DSChooseCard(scn.GetLSStartingLocation());scn.PassAllResponses();
        assertEquals(scn.GetLSStartingLocation(),scn.GetDSCard("wipe").getAttachedTo());assertEquals(before,scn.GetDSForcePileCount());
    }
    private VirtualTableScenario weapon(boolean effect,boolean dark,boolean control,int raw) {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();
        if(effect)scn.AttachCardsTo(scn.GetLSStartingLocation(),scn.GetDSCard("wipe"));
        scn.MoveCardsToLocation(site,scn.GetDSCard("boba"),scn.GetLSCard(dark?"luke":"obi"));
        if(control)scn.MoveCardsToLocation(scn.GetLSStartingLocation(),dark?scn.GetDSCard("trooper"):scn.GetLSCard("leia"));
        if(dark)scn.DSStartBattleAndSkipToWeaponsSegment(site);else {scn.LSStartBattleAndSkipToWeaponsSegment(site);}
        if(dark) {
            scn.PrepareDSDestiny(raw);scn.DSUseCardAction(scn.GetDSCard("boba"),"Fire");scn.DSChooseCard(scn.GetLSCard("luke"));
        } else {
            scn.PrepareLSDestiny(raw);scn.PrepareLSDestiny(raw+1);scn.LSUseCardAction(scn.GetLSCard("obi"),"Fire");scn.LSChooseCard(scn.GetDSCard("boba"));
        }
        scn.PassForceUseResponses();scn.PassWeaponFireWithDestinyDraw(dark?1:2);scn.PassAllResponses();return scn;
    }
    @Test public void WipeThemOutAllOfThemDarkPermanentWeaponMissesAtReducedEqualBoundary() {assertFalse(weapon(true,true,false,5).GetLSCard("luke").isHit());}
    @Test public void WipeThemOutAllOfThemDarkPermanentWeaponHitsAboveReducedBoundary() {assertTrue(weapon(true,true,false,6).GetLSCard("luke").isHit());}
    @Test public void WipeThemOutAllOfThemDarkControlOfAnotherBattlegroundRestoresNormalHit() {assertTrue(weapon(true,true,true,4).GetLSCard("luke").isHit());}
    @Test public void WipeThemOutAllOfThemAbsentDoesNotReducePermanentWeaponDraw() {assertTrue(weapon(false,true,false,4).GetLSCard("luke").isHit());}
    @Test public void WipeThemOutAllOfThemLightPermanentWeaponLosesTwoOnEachOfTwoDraws() {assertFalse(weapon(true,false,false,4).GetDSCard("boba").isHit());}
    @Test public void WipeThemOutAllOfThemLightPermanentWeaponHitsAboveReducedTwoDrawBoundary() {assertTrue(weapon(true,false,false,5).GetDSCard("boba").isHit());}
    @Test public void WipeThemOutAllOfThemLightControlOfAnotherBattlegroundRestoresBothDraws() {assertTrue(weapon(true,false,true,3).GetDSCard("boba").isHit());}
    private VirtualTableScenario draw(boolean related,boolean own) {
        var scn=scenario();scn.StartGame();scn.AttachCardsTo(scn.GetDSStartingLocation(),scn.GetDSCard("wipe"));
        scn.MoveLocationToTable(scn.GetLSCard("quiet"));if(related)scn.MoveLocationToTable(scn.GetLSCard("related"));
        if(own)scn.GetLSCard("quiet").setOwner(scn.DS);
        scn.SkipToLSTurn(Phase.DRAW);scn.LSPass();return scn;
    }
    @Test public void WipeThemOutAllOfThemActuallyActivatesExactlyOneDuringOpponentsDrawPhase() {
        var scn=draw(false,false);int before=scn.GetDSForcePileCount(),reserve=scn.GetDSReserveDeckCount();
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("wipe"),"Activate 1 Force"));scn.DSUseCardAction(scn.GetDSCard("wipe"),"Activate 1 Force");scn.PassAllResponses();
        assertEquals(before+1,scn.GetDSForcePileCount());assertEquals(reserve-1,scn.GetDSReserveDeckCount());
    }
    @Test public void WipeThemOutAllOfThemActuallyRetrievesExactlyOneToUsedPile() {
        var scn=draw(false,false);scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("device"));int before=scn.GetDSUsedPileCount();
        scn.DSUseCardAction(scn.GetDSCard("wipe"),"Retrieve 1 Force");scn.PassAllResponses();assertEquals(before+1,scn.GetDSUsedPileCount());assertInZone(Zone.USED_PILE,scn.GetDSCard("device"));
    }
    @Test public void WipeThemOutAllOfThemCannotUseBothActivationAndRetrievalInSamePhase() {
        var scn=draw(false,false);scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("device"));scn.DSUseCardAction(scn.GetDSCard("wipe"),"Activate 1 Force");scn.PassAllResponses();
        if(scn.LSDecisionAvailable("draw"))scn.LSPass();assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("wipe")));assertInZone(Zone.LOST_PILE,scn.GetDSCard("device"));
    }
    @Test public void WipeThemOutAllOfThemDoesNotTriggerForSiteWithRelatedLocation() {
        var scn=draw(true,false);assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("wipe")));
    }
    @Test public void WipeThemOutAllOfThemDoesNotTriggerForItsOwnersIsolatedSite() {
        var scn=draw(false,true);assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("wipe")));
    }
    @Test public void WipeThemOutAllOfThemCannotActivateWithoutReserveDeckCards() {
        var scn=scenario();scn.StartGame();scn.AttachCardsTo(scn.GetDSStartingLocation(),scn.GetDSCard("wipe"));scn.MoveLocationToTable(scn.GetLSCard("quiet"));
        scn.MoveCardsToDSHand(scn.gameState().getReserveDeck(scn.DS).toArray(com.gempukku.swccgo.game.PhysicalCardImpl[]::new));scn.SkipToLSTurn(Phase.DRAW);scn.LSPass();
        assertNotNull(scn.DSGetDecision());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("wipe"),"Activate 1 Force"));
    }
    @Test public void WipeThemOutAllOfThemDoesNotGrantActionDuringOwnersDrawPhase() {
        var scn=draw(false,false);scn.SkipToDSTurn(Phase.DRAW);assertTrue(scn.AwaitingDSDrawPhaseActions());assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("wipe")));
    }
    @Test public void WipeThemOutAllOfThemCannotBeTargetedByActualAlter() {
        var scn=scenario();scn.StartGame();scn.AttachCardsTo(scn.GetDSStartingLocation(),scn.GetDSCard("wipe"));scn.MoveCardsToLSSideOfTable(scn.GetLSCard("controlEffect"));scn.MoveCardsToDSSideOfTable(scn.GetDSCard("controlEffect"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(),scn.GetLSCard("luke"));scn.MoveCardsToLSHand(scn.GetLSCard("alter"));scn.SkipToLSTurn(Phase.CONTROL);scn.LSPlayCard(scn.GetLSCard("alter"));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSCard("controlEffect")));assertFalse(scn.LSHasCardChoiceAvailable(scn.GetDSCard("wipe")));
    }
    @Test public void WipeThemOutAllOfThemDoesNotReduceOrdinaryCharacterWeaponDestiny() {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();scn.AttachCardsTo(scn.GetLSStartingLocation(),scn.GetDSCard("wipe"));
        scn.MoveCardsToLocation(site,scn.GetDSCard("trooper"),scn.GetLSCard("luke"));scn.AttachCardsTo(scn.GetDSCard("trooper"),scn.GetDSCard("blaster"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site);scn.PrepareDSDestiny(5);scn.DSUseCardAction(scn.GetDSCard("blaster"),"Fire");scn.DSChooseCard(scn.GetLSCard("luke"));scn.PassForceUseResponses();scn.PassWeaponFireWithDestinyDraw();assertTrue(scn.GetLSCard("luke").isHit());
    }
    @Test public void WipeThemOutAllOfThemDoesNotReduceBattleDestinyForPermanentWeaponCharacter() {
        var scn=scenario();scn.StartGame();var site=scn.GetDSStartingLocation();scn.AttachCardsTo(scn.GetLSStartingLocation(),scn.GetDSCard("wipe"));
        scn.MoveCardsToLocation(site,scn.GetDSCard("boba"),scn.GetDSCard("trooper"),scn.GetLSCard("luke"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site);scn.PrepareDSDestiny(4);scn.SkipToEndOfPowerSegment(true);assertEquals(4,scn.GetDSTotalDestiny());
    }
    @Test public void WipeThemOutAllOfThemDrawPhaseUsageRenewsInANewOpponentsTurn() {
        var scn=draw(false,false);scn.DSUseCardAction(scn.GetDSCard("wipe"),"Activate 1 Force");scn.PassAllResponses();
        scn.SkipToLSTurn(Phase.DRAW);scn.LSPass();int before=scn.GetDSForcePileCount();assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("wipe"),"Activate 1 Force"));
        scn.DSUseCardAction(scn.GetDSCard("wipe"),"Activate 1 Force");scn.PassAllResponses();assertEquals(before+1,scn.GetDSForcePileCount());
    }
}
