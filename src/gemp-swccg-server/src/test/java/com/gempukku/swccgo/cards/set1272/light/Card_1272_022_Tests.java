package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1272_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("gunrunner", "1272_22");
                    put("blaster", "1_152");
                    put("saber", "1_155");
                    put("mine", "1_162");
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("enemyBlaster", "1_317");
                    put("strike", "5_135");
                    put("leader", "2_140");
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
    public void RebelGunrunnerStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("gunrunner").getBlueprint();

        assertEquals("Rebel Gunrunner", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.PREMIUM);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelGunrunnerWhenDeployedStacksNonLightsaberWeaponWithoutLostFromReserveDeck() {
        var scn = GetScenario();
        var gunrunner = scn.GetLSCard("gunrunner");
        var blaster = scn.GetLSCard("blaster");
        var saber = scn.GetLSCard("saber");
        var mine = scn.GetLSCard("mine");

        //test1: the weapon is stacked on Rebel Gunrunner
        //test2: lightsabers and weapons with 'lost' in game text may not be chosen
        scn.StartGame();
        scn.MoveCardsToLSHand(gunrunner);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        scn.MoveCardsToTopOfLSReserveDeck(blaster, saber, mine);
        assertTrue(scn.LSDeployAvailable(gunrunner));
        scn.LSDeployCard(gunrunner);
        scn.PassAllResponses();
        assertTrue(lsReserveSearchSelectable(scn, blaster));
        assertFalse(lsReserveSearchSelectable(scn, saber)); //test2
        assertFalse(lsReserveSearchSelectable(scn, mine)); //test2
        scn.ChooseCardBPFromSelection(scn.LS, blaster);
        scn.PassAllResponses();
        assertInZone(Zone.STACKED, blaster); //test1
        assertEquals(gunrunner, blaster.getStackedOn()); //test1
    }

    private void startBattleWithStackedBlaster(VirtualTableScenario scn) {
        var gunrunner = scn.GetLSCard("gunrunner");
        var blaster = scn.GetLSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(gunrunner);
        scn.StackCardsOn(gunrunner, blaster);
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
    }

    @Test
    public void RebelGunrunnerMayDeployStackedWeaponOnParticipatingCardWhenBattleInitiated() {
        var scn = GetScenario();
        var gunrunner = scn.GetLSCard("gunrunner");
        var blaster = scn.GetLSCard("blaster");
        var luke = scn.GetLSCard("luke");

        //test1: the stacked weapon may be deployed on a participating character
        startBattleWithStackedBlaster(scn);
        assertTrue(scn.LSCardActionAvailable(gunrunner, "Deploy"));
        scn.LSUseCardAction(gunrunner, "Deploy");
        scn.PassAllResponses();
        assertEquals(luke, blaster.getAttachedTo()); //test1
    }

    @Test
    public void RebelGunrunnerReturnsWeaponAtEndOfWeaponsSegment() {
        var scn = GetScenario();
        var gunrunner = scn.GetLSCard("gunrunner");
        var blaster = scn.GetLSCard("blaster");
        var luke = scn.GetLSCard("luke");

        //test1: the weapon is on Luke during the weapons segment
        //test2: the weapon is stacked on Rebel Gunrunner again after the weapons segment
        startBattleWithStackedBlaster(scn);
        scn.LSUseCardAction(gunrunner, "Deploy");
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions() || scn.AwaitingDSWeaponsSegmentActions());
        assertEquals(luke, blaster.getAttachedTo()); //test1
        scn.PassWeaponsSegmentActions();
        scn.PassAllResponses();
        assertInZone(Zone.STACKED, blaster); //test2
        assertEquals(gunrunner, blaster.getStackedOn()); //test2
    }

    @Test
    public void RebelGunrunnerReturnsWeaponIfItsCarrierLeavesTableDuringWeaponsSegment() {
        //test1: The deployed weapon is on Luke when he is hit and lost to Dark Strike.
        //test2: The weapon returns to Gunrunner while another Rebel keeps the battle going.
        var scn = GetScenario();
        var gunrunner = scn.GetLSCard("gunrunner");
        var blaster = scn.GetLSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var enemyBlaster = scn.GetDSCard("enemyBlaster");
        var strike = scn.GetDSCard("strike");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(gunrunner);
        scn.StackCardsOn(gunrunner, blaster);
        scn.MoveCardsToLocation(site, luke, leia, trooper);
        scn.AttachCardsTo(trooper, enemyBlaster);
        scn.MoveCardsToDSHand(strike);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(5);
        scn.LSInitiateBattle(site);
        scn.LSUseCardAction(gunrunner, "Deploy");
        scn.LSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(luke, blaster.getAttachedTo()); //test1
        if (scn.AwaitingLSWeaponsSegmentActions())
            scn.LSPass();
        scn.PrepareDSDestiny(6);
        assertTrue(scn.DSCardActionAvailable(enemyBlaster, "Fire"));
        scn.DSUseCardAction(enemyBlaster, "Fire");
        scn.DSChooseCard(luke);
        scn.PassResponses("Fire ");
        scn.PassDestinyDrawResponses();
        scn.PassResponses("ABOUT_TO_BE_HIT");
        ReachDarkInterrupt(scn, strike, "Make");
        assertTrue(luke.isHit()); //test1
        scn.DSPlayCard(strike, "Make");
        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        if (scn.AwaitingDSForceLossPayment()) {
            scn.DSPayRemainingForceLossFromReserveDeck();
            scn.PassAllResponses();
        }
        assertInZone(Zone.LOST_PILE, luke); //test2
        assertTrue(Filters.participatingInBattle.accepts(scn.game(), leia)); //test2
        assertInZone(Zone.STACKED, blaster); //test2
        assertEquals(gunrunner, blaster.getStackedOn()); //test2
    }

    @Test
    public void RebelGunrunnerReturnsWeaponWhenBattleEndsBeforeWeaponsSegment() {
        //test1: Stunning Leader excludes the sole Rebel after Gunrunner deploys the weapon.
        //test2: The battle ends and the weapon returns without a weapons-segment-end trigger.
        var scn = GetScenario();
        var gunrunner = scn.GetLSCard("gunrunner");
        var blaster = scn.GetLSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var leader = scn.GetDSCard("leader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        assertTrue(Filters.interior_site.accepts(scn.game(), site));
        scn.MoveCardsToLSSideOfTable(gunrunner);
        scn.StackCardsOn(gunrunner, blaster);
        scn.MoveCardsToLocation(site, luke, scn.GetDSCard("trooper"));
        scn.MoveCardsToDSHand(leader);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(5);
        scn.LSInitiateBattle(site);
        for (int i = 0; i < 20 && !scn.GetDecidingPlayer().equals(scn.LS); i++) {
            assertTrue(scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        scn.LSUseCardAction(gunrunner, "Deploy");
        ReachDarkInterrupt(scn, leader, "Exclude");
        assertEquals(luke, blaster.getAttachedTo()); //test1
        scn.DSPlayCard(leader, "Exclude");
        scn.PassAllResponses();
        assertNull(scn.gameState().getBattleState()); //test2
        assertInZone(Zone.STACKED, blaster); //test2
        assertEquals(gunrunner, blaster.getStackedOn()); //test2
    }

    private void ReachDarkInterrupt(VirtualTableScenario scn, PhysicalCardImpl card, String action) {
        for (int i = 0; i < 20 && !(scn.GetDecidingPlayer().equals(scn.DS)
                && scn.DSCardPlayAvailable(card, action)); i++) {
            assertTrue(scn.GetCurrentDecision().getText().toLowerCase().contains("optional response"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(scn.DSCardPlayAvailable(card, action));
    }

    private boolean lsReserveSearchSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] blueprintIds = scn.LSGetADParam("blueprintId");
        String[] selectable = scn.LSGetADParam("selectable");
        if (blueprintIds == null || selectable == null)
            return false;
        String id = card.getBlueprintId(true);
        for (int i = 0; i < blueprintIds.length; i++) {
            if (id.equals(blueprintIds[i]) && "true".equals(selectable[i]))
                return true;
        }
        return false;
    }
}
