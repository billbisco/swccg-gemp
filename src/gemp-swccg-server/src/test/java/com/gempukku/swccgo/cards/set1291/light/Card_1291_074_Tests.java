package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_074_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("gunship", "1291_74");
                    put("bespin", "5_76");
                    put("clouds", "5_85");
                    put("clouds2", "5_85");
                    put("clouds3", "5_85");
                    put("cloudcar", "5_88");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
                    put("laser", "2_158");
                }},
                20, 20,
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
    public void RepublicGunshipWingStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("gunship").getBlueprint();

        assertEquals("Republic Gunship Wing", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(5, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getLandspeed(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals("Enclosed.", card.getLore());
        assertEquals(3, card.getIconCount(Icon.PILOT));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.LAAT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.PILOT);
            add(Icon.CLONE_ARMY);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RepublicGunshipWingPermanentPilotsProvideTotalAbilityOfSix() {
        var scn = GetScenario();
        var gunship = scn.GetLSCard("gunship");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, gunship);
        assertEquals(6, scn.GetLSAbilityAtLocation(site));
        assertEquals(6, scn.GetPower(gunship));
        assertEquals(5, scn.GetManeuver(gunship));
    }

    @Test
    public void RepublicGunshipWingPowerAndManeuverPlusThreeAtCloudSectors() {
        var scn = GetScenario();
        var gunship = scn.GetLSCard("gunship");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, gunship);
        assertEquals(9, scn.GetPower(gunship));
        assertEquals(8, scn.GetManeuver(gunship));
    }

    @Test
    public void RepublicGunshipWingMayDeployAsAReactToACloudSector() {
        var scn = GetScenario();
        var gunship = scn.GetLSCard("gunship");
        var cloudcar = scn.GetLSCard("cloudcar");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, tie, cloudcar);
        scn.MoveCardsToLSHand(gunship);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(clouds);
        assertTrue(scn.LSCardPlayAvailable(gunship));
        scn.LSPlayCard(gunship);
        if (scn.LSHasCardChoiceAvailable(clouds)) {
            scn.LSChooseCard(clouds);
        }
        scn.PassAllResponses();
        assertAtLocation(clouds, gunship);
    }

    @Test
    public void RepublicGunshipWingMayNotDeployNormallyToACloudSector() {
        var scn = GetScenario();
        var gunship = scn.GetLSCard("gunship");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLSHand(gunship);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(gunship));
        scn.LSDeployCard(gunship);
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
        assertFalse(scn.LSHasCardChoiceAvailable(clouds));
    }

    @Test
    public void RepublicGunshipWingMovesLikeAStarfighterAtCloudSectors() {
        var scn = GetScenario();
        var gunship = scn.GetLSCard("gunship");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");
        var clouds2 = scn.GetLSCard("clouds2");
        var clouds3 = scn.GetLSCard("clouds3");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveLocationToTable(clouds2);
        scn.MoveLocationToTable(clouds3);
        scn.MoveCardsToLocation(clouds, gunship);
        scn.SkipToLSTurn(Phase.MOVE);
        // a vehicle moves one sector; a starfighter moves up to two sectors
        assertTrue(scn.LSMoveAvailable(gunship));
        scn.LSMoveCard(gunship, clouds3);
        scn.PassAllResponses();
        assertAtLocation(clouds3, gunship);
    }

    @Test
    public void RepublicGunshipWingMayBeTargetedByWeaponsLikeAStarfighterAtCloudSectors() {
        var scn = GetScenario();
        var gunship = scn.GetLSCard("gunship");
        var bespin = scn.GetLSCard("bespin");
        var clouds = scn.GetLSCard("clouds");
        var tie = scn.GetDSCard("tie");
        var laser = scn.GetDSCard("laser");

        scn.StartGame();
        scn.MoveLocationToTable(bespin);
        scn.MoveLocationToTable(clouds);
        scn.MoveCardsToLocation(clouds, gunship, tie);
        scn.AttachCardsTo(tie, laser);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(clouds);
        scn.PassAllResponses();
        assertTrue(scn.DSCardActionAvailable(laser, "Fire"));
        scn.DSUseCardAction(laser, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(gunship));
    }

    @Test
    public void RepublicGunshipWingImmuneToAttritionLessThanFive() {
        var scn = GetScenario();
        var gunship = scn.GetLSCard("gunship");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, gunship);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), gunship), scn.epsilon);
    }
}
