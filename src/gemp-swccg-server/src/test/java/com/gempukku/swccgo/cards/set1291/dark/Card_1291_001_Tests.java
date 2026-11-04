package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
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

public class Card_1291_001_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("b2", "1291_1");
                    put("droideka", "200_80"); // [Separatist] droid
                    put("trooper", "1_194"); // Stormtrooper (no [Separatist])
                    put("rifle", "12_186"); // Battle Droid Blaster Rifle ([Episode I], fires using 1 Force)
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

    private static AbstractNormalEffect inCompleteControl() {
        return new AbstractNormalEffect(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "In Complete Control", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    @Test
    public void B2SuperBattleDroidStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("b2").getBlueprint();
        assertEquals("B2 Super Battle Droid", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.BATTLE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.WARRIOR);
            add(Icon.SEPARATIST);
            add(Icon.PRESENCE);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void B2SuperBattleDroidDeploysPlusOneIfInCompleteControlOnTable() {
        var scn = GetScenario();
        var b2 = scn.GetDSCard("b2");
        scn.StartGame();
        scn.MoveCardsToHand(b2);
        assertEquals(3, scn.GetDeployCost(b2));
        MissionTestSupport.putOnTable(scn, 9101, scn.DS, inCompleteControl());
        assertEquals(4, scn.GetDeployCost(b2));
    }

    @Test
    public void B2SuperBattleDroidDrawsOneBattleDestinyIfUnableToOtherwise() {
        var scn = GetScenario();
        var b2 = scn.GetDSCard("b2");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, b2, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void B2SuperBattleDroidAddsOneToAttritionAgainstOpponentHere() {
        var scn = GetScenario();
        var b2 = scn.GetDSCard("b2");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, b2, trooper, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(1);
        scn.SkipToDamageSegment(true);
        // Dark total battle destiny 2, +1 from B2 (Stormtrooper is not a [Separatist] card)
        assertEquals(3, scn.gameState().getBattleState().getAttritionTotal(scn.game(), scn.LS), scn.epsilon);
    }

    @Test
    public void B2SuperBattleDroidAddsTwoToAttritionWithAnotherSeparatistCard() {
        var scn = GetScenario();
        var b2 = scn.GetDSCard("b2");
        var droideka = scn.GetDSCard("droideka");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, b2, droideka, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareDSDestiny(2);
        scn.PrepareLSDestiny(1);
        scn.SkipToDamageSegment(true);
        assertEquals(4, scn.gameState().getBattleState().getAttritionTotal(scn.game(), scn.LS), scn.epsilon);
    }

    @Test
    public void B2SuperBattleDroidFiresEpisodeIWeaponForFree() {
        var scn = GetScenario();
        var b2 = scn.GetDSCard("b2");
        var rifle = scn.GetDSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, b2, luke);
        scn.AttachCardsTo(b2, rifle);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.PrepareDSDestiny(1);
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(rifle, "Fire"));
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        // the rifle normally fires using 1 Force
        assertEquals(forceBefore, scn.GetDSForcePileCount());
    }
}
