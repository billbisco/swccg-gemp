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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.*;

public class Card_1230_103_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("droids", "1230_103");
                    put("c3po", "1_5");
                    put("luke", "1_19"); put("han", "1_11");
                    put("system", "1_127"); put("corvette", "1_140");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194"); put("vader", "1_168");
                    put("saber", "1_324"); put("bolt", "1_205");
                }},
                30, 30,
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
    public void DontForgetTheDroidsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("droids").getBlueprint();
        assertEquals("Don't Forget The Droids", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }


    @Test public void DontForgetTheDroidsLostPaysOneForceToLostAndPlacesOwnDroidOutOfPlay() {
        var scn = BattleReady(false); int force = scn.GetLSForcePileCount(); int lost = scn.GetLSLostPileCount();
        PlayLost(scn, false);
        assertEquals(force - 1, scn.GetLSForcePileCount()); assertEquals(lost + 2, scn.GetLSLostPileCount());
        assertInZone(Zone.OUT_OF_PLAY, scn.GetLSCard("c3po")); assertInZone(Zone.LOST_PILE, scn.GetLSCard("droids"));
    }
    @Test public void DontForgetTheDroidsLostMayPayFromReserveWithNoForcePile() {
        var scn = BattleReady(false); scn.DrainLSForcePileTo(0); int reserve = scn.GetLSReserveDeckCount();
        PlayLost(scn, true); assertEquals(reserve - 1, scn.GetLSReserveDeckCount());
        assertInZone(Zone.OUT_OF_PLAY, scn.GetLSCard("c3po"));
    }
    @Test public void DontForgetTheDroidsLostCancelsDrawnBattleDestinyRatherThanPreventingDraw() {
        var scn = BattleReady(false); PlayLost(scn, false); DrawDarkBattleDestiny(scn);
        assertInZone(Zone.USED_PILE, scn.GetDSDestiny(6)); assertEquals(0, scn.GetDSTotalDestiny());
        assertEquals(1, scn.gameState().getBattleState().getNumBattleDestinyDrawn(scn.DS));
    }
    @Test public void DontForgetTheDroidsLostDoesNotCancelYourBattleDestiny() {
        var scn = BattleReady(false); PlayLost(scn, false); DrawDarkBattleDestiny(scn);
        assertTrue(scn.LSDecisionAvailable("battle destiny?")); scn.PrepareLSDestiny(5); scn.LSChooseYes(); scn.PassAllResponses();
        assertEquals(5, scn.GetLSTotalDestiny());
    }
    @Test public void DontForgetTheDroidsLostAllowsWeaponFireButCancelsBothDrawnWeaponDestinies() {
        var scn = BattleReady(true); PlayLost(scn, false); int used = scn.GetDSUsedPileCount();
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(6), scn.GetDSDestiny(5));
        assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("saber"), "Fire"));
        scn.DSUseCardAction(scn.GetDSCard("saber"), "Fire"); scn.DSChooseCard(scn.GetLSCard("luke")); scn.PassAllResponses();
        assertFalse(scn.GetLSCard("luke").isHit()); assertEquals(used + 2, scn.GetDSUsedPileCount());
        assertInZone(Zone.USED_PILE, scn.GetDSDestiny(6)); assertInZone(Zone.USED_PILE, scn.GetDSDestiny(5));
    }
    @Test public void DontForgetTheDroidsUsedCancelsWeaponTargetingDroid() {
        var scn = BattleReady(true); scn.PassAllResponses();
        scn.DSUseCardAction(scn.GetDSCard("saber"), "Fire"); scn.DSChooseCard(scn.GetLSCard("c3po")); scn.DSPass();
        scn.LSPlayCard(scn.GetLSCard("droids"), "Cancel targeting"); scn.PassAllResponses();
        assertFalse(scn.GetLSCard("c3po").isHit()); assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("c3po"));
        assertInZone(Zone.USED_PILE, scn.GetLSCard("droids"));
    }
    @Test public void DontForgetTheDroidsUsedDoesNotCancelWeaponTargetingNonDroid() {
        var scn = BattleReady(true); scn.PassAllResponses();
        scn.DSUseCardAction(scn.GetDSCard("saber"), "Fire"); scn.DSChooseCard(scn.GetLSCard("luke")); scn.DSPass();
        assertFalse(scn.LSPlayUsedInterruptAvailable(scn.GetLSCard("droids")));
    }
    @Test public void DontForgetTheDroidsUsedDoesNotCancelDeviceTargetingDroid() {
        var scn = GetScenario(); scn.StartGame(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("c3po"), scn.GetDSCard("vader"));
        scn.MoveCardsToLSHand(scn.GetLSCard("droids")); scn.MoveCardsToDSHand(scn.GetDSCard("bolt")); scn.EnsureDSForcePile(5); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(scn.GetDSCard("bolt")); scn.DSChooseCard(scn.GetLSCard("c3po")); scn.DSPass();
        assertFalse(scn.LSPlayUsedInterruptAvailable(scn.GetLSCard("droids"))); scn.PassAllResponses();
        assertEquals(scn.GetLSCard("c3po"), scn.GetDSCard("bolt").getAttachedTo());
    }
    @Test public void DontForgetTheDroidsLostUnavailableAfterBattleStartWindow() {
        var scn = BattleReady(false); scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions()); scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSPlayLostInterruptAvailable(scn.GetLSCard("droids")));
    }
    @Test public void DontForgetTheDroidsLostRequiresOwnDroidAtBattleSite() {
        var scn = GetScenario(); scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("c3po"));
        scn.MoveCardsToLSHand(scn.GetLSCard("droids")); scn.EnsureDSForcePile(3); scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSUseCardAction(scn.GetLSStartingLocation(), "Initiate battle"); scn.PassForceUseResponses(); scn.DSPass();
        assertTrue(scn.LSDecisionAvailable("Optional responses")); assertFalse(scn.LSPlayLostInterruptAvailable(scn.GetLSCard("droids")));
    }
    @Test public void DontForgetTheDroidsLostWorksWhenYouInitiateBattle() {
        var scn = GetScenario(); scn.StartGame(); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("c3po"), scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        scn.MoveCardsToLSHand(scn.GetLSCard("droids")); scn.EnsureLSForcePile(3); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSUseCardAction(site, "Initiate battle"); scn.PassForceUseResponses(); scn.DSPass(); PlayLost(scn, false);
        assertInZone(Zone.OUT_OF_PLAY, scn.GetLSCard("c3po"));
    }
    @Test public void DontForgetTheDroidsLostCancellationExpiresAfterThisBattle() {
        var scn = BattleReady(false); PlayLost(scn, false); scn.SkipToDamageSegment(false); scn.PassAllResponses();
        if (scn.AwaitingDSBattleDamagePayment()) scn.DSPayRemainingBattleDamageFromReserveDeck();
        if (scn.AwaitingLSBattleDamagePayment()) scn.LSPayRemainingBattleDamageFromReserveDeck();
        scn.PassAllResponses(); scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.PrepareDSDestiny(6); scn.SkipToPowerSegment(); scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("battle destiny?")); scn.LSChooseNo(); scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("battle destiny?")); scn.DSChooseYes(); scn.PassAllResponses();
        assertEquals(7, scn.GetDSTotalDestiny());
    }
    private VirtualTableScenario BattleReady(boolean weapon) {
        var scn = GetScenario(); scn.StartGame(); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("c3po"), scn.GetLSCard("luke"), scn.GetLSCard("han"), scn.GetDSCard("vader"));
        if (weapon) scn.AttachCardsTo(scn.GetDSCard("vader"), scn.GetDSCard("saber"));
        scn.MoveCardsToLSHand(scn.GetLSCard("droids")); scn.EnsureDSForcePile(5); scn.EnsureLSForcePile(3); scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSUseCardAction(site, "Initiate battle"); scn.PassForceUseResponses(); return scn;
    }
    private void PlayLost(VirtualTableScenario scn, boolean reserve) {
        scn.LSPlayCard(scn.GetLSCard("droids"), "Place droid out of play"); scn.LSChooseCard(scn.GetLSCard("c3po")); scn.PassAllResponses();
        if (reserve) scn.LSPayForceLossFromReserveDeck(); else scn.LSPayForceLossFromForcePile();
        scn.PassAllResponses();
    }
    private void DrawDarkBattleDestiny(VirtualTableScenario scn) {
        scn.PrepareDSDestiny(6); scn.SkipToPowerSegment(); scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("battle destiny?")); scn.DSChooseYes(); scn.PassAllResponses();
    }
}
