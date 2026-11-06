package com.gempukku.swccgo.cards.set1230.light;

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
import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Card_1230_094_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("damage", "1230_94");
            put("luke", "1_19");
            put("han", "1_11");
            put("blaster", "1_152");
            put("second", "1_153");
            put("saber", "1_155");
            put("corvette", "1_140");
            put("otherShip", "1_146");
            put("quad", "1_159");
            put("cannon", "7_162");
            put("system", "1_126");
            put("speeder", "7_156");
            put("dual", "3_74");
            put("rescue", "5_67");
            put("bespin", "5_76");
            put("clouds", "5_85");
            put("cloudCar", "5_88");
        }}, new HashMap<>() {{
            put("vader", "1_168");
            put("rifle", "1_312");
            put("tie", "1_304");
        }}, 30, 30, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void SuperficialDamageStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario(); var card = scn.GetLSCard("damage").getBlueprint();
        assertEquals("Superficial Damage", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.DEATH_STAR_II); add(Icon.LEGACY_BLOCK_3); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void SuperficialDamageDeploysOnTableWithoutForceCost() {
        var scn = GetScenario(); scn.StartGame(); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSHand(scn.GetLSCard("damage")); scn.SkipToPhase(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSPlayCard(scn.GetLSCard("damage")); scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("damage"));
        assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void SuperficialDamageForfeitsTwoWeaponsFromOneBearerForThreeEachToUsedPile() {
        var scn = DamageReady(false);
        int attrition = scn.GetUnpaidLSAttrition(); int damage = scn.GetUnpaidLSBattleDamage();
        assertTrue(attrition > 6); assertTrue(damage > 6);
        assertFalse(scn.GetLSCard("blaster").getBlueprint().hasForfeitAttribute());
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("blaster")));
        scn.LSChooseCard(scn.GetLSCard("blaster")); scn.PassResponses("FORFEITED_TO_USED_PILE_FROM_TABLE");
        assertInZone(Zone.USED_PILE, scn.GetLSCard("blaster"));
        assertEquals(attrition - 3, scn.GetUnpaidLSAttrition());
        assertEquals(damage - 3, scn.GetUnpaidLSBattleDamage());
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("second")));
        scn.LSChooseCard(scn.GetLSCard("second")); scn.PassResponses("FORFEITED_TO_USED_PILE_FROM_TABLE");
        assertInZone(Zone.USED_PILE, scn.GetLSCard("second"));
        assertEquals(attrition - 6, scn.GetUnpaidLSAttrition());
        assertEquals(damage - 6, scn.GetUnpaidLSBattleDamage());
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"));
        assertEquals(0, scn.GetLSLostPileCount());
    }

    @Test
    public void SuperficialDamageCannotForfeitLightsaberOrWeaponOutsideBattle() {
        var scn = DamageReady(true);
        assertTrue(scn.AwaitingLSAttritionPayment());
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSCard("luke")));
        assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetLSCard("saber")));
        assertTrue(scn.LSHasCardChoiceNotAvailable(scn.GetLSCard("blaster")));
        assertFalse(scn.game().getModifiersQuerying().mayBeForfeitedInBattle(scn.gameState(), scn.GetDSCard("rifle")));
    }

    @Test
    public void SuperficialDamageDownloadsCompatibleWeaponFreeOntoJustDeployedShip() {
        var scn = DownloadReady(4);
        scn.LSPlayCard(scn.GetLSCard("corvette")); scn.LSChooseCard(scn.GetLSCard("system"));
        PassDeploymentResponses(scn);
        assertAtLocation(scn.GetLSCard("system"), scn.GetLSCard("corvette"));
        assertEquals(0, scn.GetLSForcePileCount());
        scn.LSUseCardAction(scn.GetLSCard("damage"), "Deploy a weapon for free"); scn.PassAllResponses();
        AssertDownloadSelectable(scn, "quad", true);
        AssertDownloadSelectable(scn, "cannon", false);
        AssertDownloadSelectable(scn, "blaster", false);
        int reserve = scn.GetLSReserveDeckCount();
        scn.LSChooseCard(scn.GetLSCard("quad")); scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose target")) scn.LSChooseCard(scn.GetLSCard("corvette"));
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(scn.GetLSCard("corvette"), scn.GetLSCard("quad")));
        assertEquals(0, scn.GetLSForcePileCount()); assertEquals(reserve - 1, scn.GetLSReserveDeckCount());
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("cannon"));
    }

    @Test
    public void SuperficialDamageMayDeclineWeaponDownload() {
        var scn = DownloadReady(4);
        scn.LSPlayCard(scn.GetLSCard("corvette")); scn.LSChooseCard(scn.GetLSCard("system"));
        PassDeploymentResponses(scn);
        assertTrue(scn.LSCardActionAvailable(scn.GetLSCard("damage"), "Deploy a weapon for free"));
        scn.LSPass(); scn.PassAllResponses();
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("quad"));
        if (scn.AwaitingDSDeployPhaseActions()) scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
    }

    @Test
    public void SuperficialDamageCharacterDeploymentDoesNotTriggerWeaponDownload() {
        var scn = DownloadReady(3);
        scn.LSPlayCard(scn.GetLSCard("han")); scn.LSChooseCard(scn.GetLSStartingLocation());
        PassDeploymentResponses(scn);
        assertAtLocation(scn.GetLSStartingLocation(), scn.GetLSCard("han"));
        if (scn.AwaitingDSDeployPhaseActions()) scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("damage"), "Deploy a weapon for free"));
        scn.PassAllResponses();
        if (scn.AwaitingDSDeployPhaseActions()) scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
    }

    @Test
    public void SuperficialDamageDownloadsVehicleWeaponFreeAfterVehicleDeployment() {
        var scn = DownloadReady(2);
        scn.LSPlayCard(scn.GetLSCard("speeder")); scn.LSChooseCard(scn.GetDSStartingLocation());
        PassDeploymentResponses(scn);
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetLSCard("speeder"));
        assertEquals(0, scn.GetLSForcePileCount());
        scn.LSUseCardAction(scn.GetLSCard("damage"), "Deploy a weapon for free"); scn.PassAllResponses();
        AssertDownloadSelectable(scn, "dual", true); AssertDownloadSelectable(scn, "quad", false);
        scn.LSChooseCard(scn.GetLSCard("dual")); scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Choose target")) scn.LSChooseCard(scn.GetLSCard("speeder"));
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(scn.GetLSCard("speeder"), scn.GetLSCard("dual")));
        assertEquals(0, scn.GetLSForcePileCount());
    }

    @Test
    public void SuperficialDamageDoesNotDownloadAfterReactDeploymentOutsideYourDeployPhase() {
        var scn = GetScenario(); scn.StartGame(); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("damage"));
        scn.MoveLocationToTable(scn.GetLSCard("bespin")); scn.MoveLocationToTable(scn.GetLSCard("clouds"));
        scn.MoveCardsToLocation(scn.GetLSCard("clouds"), scn.GetLSCard("cloudCar"), scn.GetDSCard("tie"));
        scn.MoveCardsToLSHand(scn.GetLSCard("rescue"), scn.GetLSCard("otherShip"));
        scn.MoveOutOfPlay(scn.GetLSCard("cannon"));
        scn.EnsureLSForcePile(5); scn.SkipToDSTurn(Phase.BATTLE);
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("cannon"));
        scn.DSInitiateBattle(scn.GetLSCard("clouds"));
        scn.LSPlayCard(scn.GetLSCard("rescue"));
        PassResponsesWithoutWeaponDownload(scn);
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(scn.GetLSCard("otherShip")))
            scn.LSChooseCard(scn.GetLSCard("otherShip"));
        PassResponsesWithoutWeaponDownload(scn);
        assertAtLocation(scn.GetLSCard("clouds"), scn.GetLSCard("otherShip"));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("rescue"));
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("cannon"));
    }

    @Test
    public void SuperficialDamageDoesNotDownloadAfterOpponentsShipDeployment() {
        var scn = GetScenario(); scn.StartGame(); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("damage"));
        scn.MoveLocationToTable(scn.GetLSCard("system"));
        scn.MoveCardsToDSHand(scn.GetDSCard("tie")); scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSPlayCard(scn.GetDSCard("tie")); scn.DSChooseCard(scn.GetLSCard("system"));
        PassResponsesWithoutWeaponDownload(scn);
        assertAtLocation(scn.GetLSCard("system"), scn.GetDSCard("tie"));
    }

    private void PassResponsesWithoutWeaponDownload(VirtualTableScenario scn) {
        for (int i = 0; i < 30; i++) {
            if (scn.LSGetDecision() != null && scn.LSGetADParam("actionText") != null)
                assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("damage"), "Deploy a weapon for free"));
            if (!scn.GetCurrentDecision().getText().toLowerCase().contains("optional response")) return;
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        fail("Deployment response window did not finish");
    }

    private VirtualTableScenario DownloadReady(int force) {
        var scn = GetScenario(); scn.StartGame(); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("damage")); scn.MoveLocationToTable(scn.GetLSCard("system"));
        scn.MoveCardsToLocation(scn.GetLSCard("system"), scn.GetLSCard("otherShip"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("luke"));
        scn.MoveCardsToLSHand(scn.GetLSCard("corvette"), scn.GetLSCard("han"), scn.GetLSCard("speeder"));
        for (String key : new String[]{"quad", "cannon", "blaster", "dual"}) scn.MoveOutOfPlay(scn.GetLSCard(key));
        scn.LSChooseAction("Activate Force");
        int activated = Math.min(scn.GetLSReserveDeckCount() - 1, scn.LSGetChoiceMax()); scn.LSDecided(activated);
        if (scn.DSDecisionAvailable("Choose amount of Force to allow opponent")) scn.DSDecided(activated);
        scn.EnsureLSForcePile(force); scn.DrainLSForcePileTo(force);
        scn.PassAllResponses(); scn.PassActivateActions(); scn.PassAllResponses(); scn.SkipToPhase(Phase.DEPLOY);
        for (String key : new String[]{"quad", "cannon", "blaster", "dual"}) scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard(key));
        assertEquals(force, scn.GetLSForcePileCount()); return scn;
    }

    private void AssertDownloadSelectable(VirtualTableScenario scn, String key, boolean expected) {
        var ids = scn.LSGetADParamAsList("blueprintId"); int index = ids.indexOf(scn.GetLSCard(key).getBlueprintId(true));
        assertTrue("Expected displayed card: " + key, index >= 0);
        assertEquals(key, expected, Boolean.parseBoolean(scn.LSGetADParam("selectable")[index]));
    }

    private void PassDeploymentResponses(VirtualTableScenario scn) {
        scn.PassCardAndForceUseResponses(); scn.PassCardAndForceUseResponses();
        scn.PassResponses("Deploying <div");
        if (scn.DSDecisionAvailable("Optional responses")) scn.DSPass();
    }

    private VirtualTableScenario DamageReady(boolean saber) {
        var scn = GetScenario(); scn.StartGame(); scn.SkipToLSTurn(Phase.ACTIVATE);
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("damage"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke"), scn.GetDSCard("vader"));
        if (saber) {
            scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("saber"));
            scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("han"));
            scn.AttachCardsTo(scn.GetLSCard("han"), scn.GetLSCard("blaster"));
        } else scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("blaster"), scn.GetLSCard("second"));
        scn.AttachCardsTo(scn.GetDSCard("vader"), scn.GetDSCard("rifle"));
        scn.EnsureLSForcePile(5); scn.SkipToPhase(Phase.BATTLE);
        scn.LSInitiateBattle(scn.GetLSStartingLocation());
        scn.PrepareLSDestiny(0); scn.PrepareDSDestiny(6); scn.SkipToEndOfPowerSegment(true);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.AwaitingLSAttritionPayment()); return scn;
    }
}
