package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.WidenIncomKoensayrScopeModifier;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_081_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("incom", "1261_81"); put("warRoom", "1_139"); put("echoRoom", "3_57");
            put("shipRoom", "9_58"); put("system", "1_135"); put("jungle", "1_137"); put("throne", "1_138"); put("generators", "3_61");
            put("echoBay", "3_59"); put("echoMed", "3_60"); put("rebel1", "1_28"); put("rebel2", "1_28"); put("rebel3", "1_28");
            put("ebo", "3_34"); put("xwing", "1_146"); put("xwing2", "1_146"); put("ywing", "1_147");
            put("z95", "106_9"); put("speeder", "3_69"); put("t16", "2_75"); put("red1", "1_144");
            put("squadron", "7_150"); put("awing", "9_73"); put("luke", "1_19"); put("beggar", "1_44");
        }}, new HashMap<>() {{ put("trooper", "1_194"); put("vader", "1_168"); put("tie", "1_304"); put("alter", "1_234"); }},
                60, 60, StartingSetup.LSStartingLocation("1_136"), StartingSetup.DSStartingLocation("12_176"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void IncomKoensayrStatsAndComboIdentityMatchPrintedSource() {
        var scn = scenario(); var card = scn.GetLSCard("incom"); var b = card.getBlueprint();
        assertEquals("Incom Corporation & Koensayr Manufacturing", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(3, b.getDestiny(), scn.epsilon); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        assertEquals(Rarity.V, b.getRarity()); assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.EFFECT, Icon.SPECIAL_EDITION, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>(Arrays.asList(Keyword.DEPLOYS_ON_SITE)));
        assertTrue(Filters.title("Incom Corporation").accepts(scn.game(), card));
        assertTrue(Filters.title("Koensayr Manufacturing").accepts(scn.game(), card));
    }

    private VirtualTableScenario started(boolean attached) {
        var scn = scenario(); scn.StartGame();
        for (String key : new String[]{"warRoom", "echoRoom", "shipRoom", "system", "jungle", "throne", "generators"})
            scn.MoveLocationToTable(scn.GetLSCard(key));
        if (attached) scn.AttachCardsTo(scn.GetLSCard("warRoom"), scn.GetLSCard("incom"));
        else scn.MoveCardsToLSHand(scn.GetLSCard("incom"));
        return scn;
    }

    @Test public void IncomKoensayrActuallyDeploysForFreeOnlyToRebelBaseWarRooms() {
        var scn = started(false); scn.SkipToLSTurn(Phase.DEPLOY);
        while (scn.GetLSForcePileCount() > 0) scn.MoveCardsToLSHand(scn.GetTopOfLSForcePile()); assertEquals(0, scn.GetLSForcePileCount());
        scn.LSDeployCard(scn.GetLSCard("incom"));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("warRoom")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("echoRoom")));
        assertTrue(Filters.war_room.accepts(scn.game(), scn.GetLSCard("shipRoom")));
        assertFalse(Filters.Rebel_Base_site.accepts(scn.game(), scn.GetLSCard("shipRoom")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("shipRoom")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("throne")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("jungle")));
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetLSCard("system")));
        scn.LSChooseCard(scn.GetLSCard("warRoom")); scn.PassAllResponses();
        assertEquals(scn.GetLSCard("warRoom"), scn.GetLSCard("incom").getAttachedTo());
        assertEquals(0, scn.GetLSForcePileCount());
    }

    private void upgraded(String key, boolean ground, int printedPower) {
        var scn = started(true); var card = scn.GetLSCard(key);
        scn.MoveCardsToLocation(scn.GetLSCard(ground ? "jungle" : "system"), card);
        assertEquals(2, scn.GetBattleDestinyAbility(card)); assertEquals(printedPower + 1, scn.GetPower(card));
        scn.MoveCardsToLSHand(scn.GetLSCard("incom"));
        assertEquals(1, scn.GetBattleDestinyAbility(card)); assertEquals(printedPower, scn.GetPower(card));
    }
    @Test public void IncomKoensayrUpgradesXwingAndRestoresItWhenRemoved() { upgraded("xwing", false, 3); }
    @Test public void IncomKoensayrAddsYwingsUnlikeOriginalIncom() { upgraded("ywing", false, 2); }
    @Test public void IncomKoensayrUpgradesZ95() { upgraded("z95", false, 2); }
    @Test public void IncomKoensayrUpgradesT47AtRemoteGroundSite() { upgraded("speeder", true, 3); }

    @Test public void IncomKoensayrDoesNotUpgradeT16UnlikeOriginalIncom() {
        var scn = started(true); scn.MoveCardsToLocation(scn.GetLSCard("jungle"), scn.GetLSCard("t16"));
        assertEquals(1, scn.GetBattleDestinyAbility(scn.GetLSCard("t16"))); assertEquals(1, scn.GetPower(scn.GetLSCard("t16")));
    }
    @Test public void IncomKoensayrDoesNotUpgradeSquadronsWithAbilityOnePilots() {
        var scn = started(true); scn.MoveCardsToLocation(scn.GetLSCard("system"), scn.GetLSCard("squadron"));
        assertEquals(3, scn.GetBattleDestinyAbility(scn.GetLSCard("squadron"))); assertEquals(9, scn.GetPower(scn.GetLSCard("squadron")));
    }
    @Test public void IncomKoensayrDoesNotInventPermanentPilotInUnpilotedXwing() {
        var scn = started(true); scn.MoveCardsToLocation(scn.GetLSCard("system"), scn.GetLSCard("red1"));
        assertEquals(0, scn.GetAbility(scn.GetLSCard("red1"))); assertEquals(0, scn.GetPower(scn.GetLSCard("red1")));
        scn.BoardAsPilot(scn.GetLSCard("red1"), scn.GetLSCard("luke"));
        assertEquals(4, scn.GetAbility(scn.GetLSCard("luke"))); assertEquals(6, scn.GetPower(scn.GetLSCard("red1")));
    }
    @Test public void IncomKoensayrDoesNotUpgradeOpposingPermanentPilot() {
        var scn = started(true); var tie = scn.GetDSCard("tie"); scn.MoveCardsToLocation(scn.GetLSCard("system"), tie);
        assertEquals(1, scn.GetBattleDestinyAbility(tie)); assertEquals(1, scn.GetPower(tie));
    }
    @Test public void IncomKoensayrPowerBonusChangesActualBattleTotal() {
        var scn = started(true); var site = scn.GetLSCard("jungle");
        scn.MoveCardsToLocation(site, scn.GetLSCard("speeder"), scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(site); scn.PassAllResponses(); scn.SkipToEndOfPowerSegment(false);
        assertEquals(4, scn.GetLSTotalPower()); assertEquals(1, scn.GetDSTotalPower());
    }
    @Test public void IncomKoensayrTwoUpgradedPilotsEnableActualBattleDestiny() {
        var scn = started(true); var system = scn.GetLSCard("system");
        scn.MoveCardsToLocation(system, scn.GetLSCard("xwing"), scn.GetLSCard("xwing2"), scn.GetDSCard("tie"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.LSInitiateBattle(system); scn.PassAllResponses(); scn.SkipToPowerSegment();
        scn.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS"); assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseNo(); scn.PassAllResponses();
    }

    private void assertNoRetrieve(VirtualTableScenario scn) {
        // Return the priority window to Light Side before testing a negative action condition.
        if (scn.AwaitingDSControlPhaseActions()) scn.DSPass();
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(text -> text.contains("Retrieve a Y-wing or Z-95")));
    }

    private void retrieve(String key) {
        var scn = started(true); var card = scn.GetLSCard(key);
        scn.MoveCardsToTopOfLSLostPile(card); scn.MoveCardsToTopOfLSLostPile(scn.GetLSFiller(1));
        scn.SkipToLSTurn(Phase.CONTROL); int lost = scn.GetLSLostPileCount();
        scn.LSUseCardAction(scn.GetLSCard("incom"), "Retrieve a Y-wing or Z-95"); scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(card)); scn.LSChooseCard(card); scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, card); assertInZone(Zone.LOST_PILE, scn.GetLSFiller(1));
        assertEquals(lost - 1, scn.GetLSLostPileCount()); assertNoRetrieve(scn);
    }
    @Test public void IncomKoensayrRetrievesOneYwingEvenBelowNonmatchingTopCard() { retrieve("ywing"); }
    @Test public void IncomKoensayrRetrievesOneZ95() { retrieve("z95"); }
    @Test public void IncomKoensayrCannotRetrieveXwingWithoutWidening() {
        var scn = started(true); var card = scn.GetLSCard("xwing"); scn.MoveCardsToTopOfLSLostPile(card);
        scn.SkipToLSTurn(Phase.CONTROL); scn.LSUseCardAction(scn.GetLSCard("incom"), "Retrieve"); scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Verify")); scn.LSPass(); scn.DSPass(); assertInZone(Zone.LOST_PILE, card);
    }
    @Test public void IncomKoensayrRetrievalIsOnlyDuringOwnersControlPhase() {
        var scn = started(true); scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("ywing"));
        scn.SkipToLSTurn(Phase.DEPLOY); assertNoRetrieve(scn);
        scn.SkipToDSTurn(Phase.CONTROL); assertNoRetrieve(scn);
    }
    @Test public void IncomKoensayrRetrievalResetsNextOwnControlPhase() {
        var scn = started(true); var effect = scn.GetLSCard("incom");
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("ywing"), scn.GetLSCard("z95")); scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(effect, "Retrieve"); scn.PassAllResponses(); scn.LSChooseCard(scn.GetLSCard("ywing")); scn.PassAllResponses();
        assertNoRetrieve(scn); scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSUseCardAction(effect, "Retrieve"); scn.PassAllResponses(); scn.LSChooseCard(scn.GetLSCard("z95")); scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, scn.GetLSCard("z95"));
    }
    @Test public void IncomKoensayrEmptyLostPileOffersNoRetrieval() {
        var scn = started(true); scn.SkipToLSTurn(Phase.CONTROL); assertEquals(0, scn.GetLSLostPileCount());
        assertNoRetrieve(scn);
    }

    @Test public void IncomKoensayrSuspendsAndResumesWithoutBeingCanceled() {
        var scn = started(true); var effect = scn.GetLSCard("incom"); var site = scn.GetLSCard("warRoom");
        var ship = scn.GetLSCard("ywing"); scn.MoveCardsToLocation(scn.GetLSCard("system"), ship);
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper")); scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("z95"));
        scn.SkipToLSTurn(Phase.CONTROL); assertTrue(effect.isSuspended()); assertEquals(site, effect.getAttachedTo());
        assertEquals(1, scn.GetBattleDestinyAbility(ship)); assertEquals(2, scn.GetPower(ship));
        assertNoRetrieve(scn);
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke")); scn.SkipToPhase(Phase.DEPLOY);
        assertFalse(effect.isSuspended()); assertEquals(2, scn.GetBattleDestinyAbility(ship)); assertEquals(3, scn.GetPower(ship));
        scn.SkipToLSTurn(Phase.CONTROL); assertTrue(scn.LSCardActionAvailable(effect, "Retrieve"));
    }
    @Test public void IncomKoensayrOpposingControlOfOtherSiteDoesNotSuspend() {
        var scn = started(true); scn.MoveCardsToLocation(scn.GetLSCard("echoRoom"), scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.CONTROL); assertFalse(scn.GetLSCard("incom").isSuspended());
    }
    @Test public void IncomKoensayrCancelsPreviouslyDeployedEchoBaseOperations() {
        var scn = started(false); var ebo = scn.GetLSCard("ebo"); scn.AttachCardsTo(scn.GetLSCard("generators"), ebo);
        scn.SkipToLSTurn(Phase.DEPLOY); scn.LSDeployCard(scn.GetLSCard("incom"));
        scn.LSChooseCard(scn.GetLSCard("warRoom")); scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, ebo);
    }
    @Test public void IncomKoensayrCancelsEchoBaseOperationsActuallyBeingDeployed() {
        var scn = started(true);
        scn.MoveLocationToTable(scn.GetLSCard("echoBay")); scn.MoveLocationToTable(scn.GetLSCard("echoMed"));
        scn.MoveCardsToLocation(scn.GetLSCard("echoRoom"), scn.GetLSCard("rebel1"));
        scn.MoveCardsToLocation(scn.GetLSCard("echoBay"), scn.GetLSCard("rebel2"));
        scn.MoveCardsToLocation(scn.GetLSCard("echoMed"), scn.GetLSCard("rebel3"));
        scn.MoveCardsToLSHand(scn.GetLSCard("ebo")); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(scn.GetLSCard("ebo")); scn.LSChooseCard(scn.GetLSCard("generators"));
        scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, scn.GetLSCard("ebo"));
        assertEquals(scn.GetLSCard("warRoom"), scn.GetLSCard("incom").getAttachedTo());
    }
    @Test public void IncomKoensayrSuspendedEffectDoesNotCancelEchoBaseOperations() {
        var scn = started(true); var site = scn.GetLSCard("warRoom"); scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"));
        scn.SkipToLSTurn(Phase.CONTROL); assertTrue(scn.GetLSCard("incom").isSuspended());
        var ebo = scn.GetLSCard("ebo"); scn.AttachCardsTo(scn.GetLSCard("generators"), ebo); scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(scn.GetLSCard("generators"), ebo.getAttachedTo());
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke")); scn.SkipToPhase(Phase.MOVE); scn.PassAllResponses();
        assertFalse(scn.GetLSCard("incom").isSuspended()); assertInZone(Zone.LOST_PILE, ebo);
    }
    @Test public void IncomKoensayrIsNotAnAlterTargetWhileOrdinaryEffectIsCanceled() {
        var scn = started(true); var effect = scn.GetLSCard("incom"); var beggar = scn.GetLSCard("beggar");
        scn.AttachCardsTo(scn.GetDSStartingLocation(), beggar); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("alter")); scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(1);
        scn.DSPlayCard(scn.GetDSCard("alter"), "Draw destiny to cancel Effect");
        assertFalse(scn.DSHasCardChoiceAvailable(effect)); assertTrue(scn.DSHasCardChoiceAvailable(beggar));
        scn.DSChooseCard(beggar); scn.DSChooseCard(scn.GetDSCard("vader"));
        scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, beggar); assertEquals(scn.GetLSCard("warRoom"), effect.getAttachedTo());
    }
    @Test public void IncomKoensayrPreservesExistingWidenedShipInteraction() {
        var scn = started(true); var ship = scn.GetLSCard("awing"); scn.MoveCardsToLocation(scn.GetLSCard("system"), ship);
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("xwing"));
        scn.ApplyAdHocModifier(new WidenIncomKoensayrScopeModifier(scn.GetLSCard("warRoom")));
        scn.SkipToLSTurn(Phase.CONTROL); assertTrue(WidenIncomKoensayrScopeModifier.isActive(scn.game(), scn.GetLSCard("incom")));
        // Ability-2 permanent pilot is retained and must not gain the ability-1 replacement bonus.
        assertEquals(2, scn.GetBattleDestinyAbility(ship)); assertEquals(5, scn.GetPower(ship));
        scn.LSUseCardAction(scn.GetLSCard("incom"), "Retrieve"); scn.PassAllResponses();
        scn.LSChooseCard(scn.GetLSCard("xwing")); scn.PassAllResponses(); assertInZone(Zone.USED_PILE, scn.GetLSCard("xwing"));
    }
}
