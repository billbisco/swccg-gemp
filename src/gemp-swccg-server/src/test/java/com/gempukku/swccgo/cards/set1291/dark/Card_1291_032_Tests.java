package com.gempukku.swccgo.cards.set1291.dark;

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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_032_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("rogue", "3_66");
                }},
                new HashMap<>() {{
                    put("hailfire", "1291_32");
                    put("b2", "1291_1");
                    put("docking", "1_291");
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
    public void HailfireDroidTankStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("hailfire").getBlueprint();

        assertEquals("IG-227 'Hailfire' Droid Tank", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        assertEquals(2, card.getLandspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals("Enclosed.", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.HAILFIRE_CLASS_DROID_TANK);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.SEPARATIST);
            add(Icon.PRESENCE);
            add(Icon.PILOT);
            add(Icon.EPISODE_I);
            add(Icon.PERMANENT_WEAPON);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HailfireDroidTankDeploysMinusOneToSameLocationAsASeparatistCard() {
        var scn = GetScenario();
        var hailfire = scn.GetDSCard("hailfire");
        var b2 = scn.GetDSCard("b2");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, b2);
        scn.MoveCardsToDSHand(hailfire);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSDeployAvailable(hailfire));
        scn.DSDeployCard(hailfire);
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
        }
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(site, hailfire));
        assertEquals(forceBefore - 3, scn.GetDSForcePileCount());
    }

    @Test
    public void HailfireDroidTankDeploysAtNormalCostWithoutASeparatistCard() {
        var scn = GetScenario();
        var hailfire = scn.GetDSCard("hailfire");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(hailfire);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetDSForcePileCount();
        scn.DSDeployCard(hailfire);
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
        }
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(site, hailfire));
        assertEquals(forceBefore - 4, scn.GetDSForcePileCount());
    }

    @Test
    public void HailfireDroidTankMissileBatteryHitsCharacterWhenDestinyPlusOneExceedsDefenseValue() {
        var scn = GetScenario();
        var hailfire = scn.GetDSCard("hailfire");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, hailfire, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        int usedBefore = scn.GetDSUsedPileCount();
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(hailfire, "Fire"));
        scn.PrepareDSDestiny(4);
        scn.DSUseCardAction(hailfire, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(luke.isHit());
    }

    @Test
    public void HailfireDroidTankMissileBatteryMissesCharacterWhenDestinyPlusOneDoesNotExceedDefenseValue() {
        var scn = GetScenario();
        var hailfire = scn.GetDSCard("hailfire");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, hailfire, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(3);
        scn.DSUseCardAction(hailfire, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertFalse(luke.isHit());
    }

    @Test
    public void HailfireDroidTankMissileBatteryAddsTwoWhenTargetingAVehicle() {
        var scn = GetScenario();
        var hailfire = scn.GetDSCard("hailfire");
        var rogue = scn.GetLSCard("rogue");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, hailfire, rogue);
        scn.BoardAsPilot(rogue, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertEquals(4, scn.GetDefense(rogue));
        // destiny 2 + 2 (vehicle) + 1 = 5 > 4
        scn.PrepareDSDestiny(2);
        scn.DSUseCardAction(hailfire, "Fire");
        scn.DSChooseCard(rogue);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(rogue.isHit());
    }

    @Test
    public void HailfireDroidTankMissileBatteryMissesVehicleWhenTotalPlusOneDoesNotExceedDefenseValue() {
        var scn = GetScenario();
        var hailfire = scn.GetDSCard("hailfire");
        var rogue = scn.GetLSCard("rogue");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, hailfire, rogue);
        scn.BoardAsPilot(rogue, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        // destiny 1 + 2 (vehicle) + 1 = 4, not > 4
        scn.PrepareDSDestiny(1);
        scn.DSUseCardAction(hailfire, "Fire");
        scn.DSChooseCard(rogue);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertFalse(rogue.isHit());
    }
}
