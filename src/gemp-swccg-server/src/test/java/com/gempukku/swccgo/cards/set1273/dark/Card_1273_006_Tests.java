package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("magnaguard", "1273_6");
                    put("trooper", "1_194"); // Imperial warrior
                    put("grievous", "203_27"); // General Grievous
                    put("quarters", "1273_17"); // Coruscant: Palpatine's Quarters
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
    public void IG100MagnaGuardStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("magnaguard").getBlueprint();
        assertEquals("IG-100 MagnaGuard", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.GUARD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.WARRIOR);
            add(Icon.PRESENCE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void IG100MagnaGuardShockstaffDeploysAndFiresForFreeOnThisDroid() {
        var scn = GetScenario();
        var magnaguard = scn.GetDSCard("magnaguard");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var staff = MissionTestSupport.putInHand(scn, 7306, scn.DS, LegacyVb73DarkTestSupport.shockstaff());
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, magnaguard, trooper, luke);
        var qs = scn.game().getModifiersQuerying();
        assertEquals(2, qs.getDeployCost(scn.gameState(), staff, staff, trooper, false, null, false, 0, null, false), scn.epsilon);
        assertEquals(0, qs.getDeployCost(scn.gameState(), staff, staff, magnaguard, false, null, false, 0, null, false), scn.epsilon);

        scn.AttachCardsTo(trooper, staff);
        assertEquals(1, qs.getFireWeaponCost(scn.gameState(), staff, trooper, luke, 1), scn.epsilon);
        scn.AttachCardsTo(magnaguard, staff);
        assertEquals(0, qs.getFireWeaponCost(scn.gameState(), staff, magnaguard, luke, 1), scn.epsilon);
    }

    @Test
    public void IG100MagnaGuardIsHeroicAndPowerPlusTwoOnCoruscant() {
        var scn = GetScenario();
        var magnaguard = scn.GetDSCard("magnaguard");
        var quarters = scn.GetDSCard("quarters");
        scn.StartGame();
        scn.MoveLocationToTable(quarters);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), magnaguard);
        assertEquals(3, scn.GetPower(magnaguard));
        assertFalse(scn.HasKeyword(magnaguard, Keyword.HEROIC));
        scn.MoveCardsToLocation(quarters, magnaguard);
        assertEquals(5, scn.GetPower(magnaguard));
        assertTrue(scn.HasKeyword(magnaguard, Keyword.HEROIC));
    }

    @Test
    public void IG100MagnaGuardIsHeroicAndPowerPlusTwoWithGrievous() {
        var scn = GetScenario();
        var magnaguard = scn.GetDSCard("magnaguard");
        var grievous = scn.GetDSCard("grievous");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), magnaguard, grievous);
        assertEquals(5, scn.GetPower(magnaguard));
        assertTrue(scn.HasKeyword(magnaguard, Keyword.HEROIC));
    }

    @Test
    public void IG100MagnaGuardYourCharactersHereImmuneToAmbushClashOfSabersAndAttritionLessThanFour() {
        var scn = GetScenario();
        var magnaguard = scn.GetDSCard("magnaguard");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, magnaguard, trooper, luke);
        var qs = scn.game().getModifiersQuerying();
        assertTrue(qs.isImmuneToCardTitle(scn.gameState(), trooper, Title.Ambush));
        assertTrue(qs.isImmuneToCardTitle(scn.gameState(), trooper, Title.Clash_Of_Sabers));
        assertEquals(4, qs.getImmunityToAttritionLessThan(scn.gameState(), trooper), scn.epsilon);
        assertFalse(qs.isImmuneToCardTitle(scn.gameState(), luke, Title.Ambush));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), trooper);
        assertFalse(qs.isImmuneToCardTitle(scn.gameState(), trooper, Title.Ambush));
    }
}
