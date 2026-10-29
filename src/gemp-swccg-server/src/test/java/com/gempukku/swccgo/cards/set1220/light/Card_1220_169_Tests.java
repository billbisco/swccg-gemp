package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_1220_169_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("blaster", "1220_169");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("bantha", "1_307");
                    put("walker", "104_5");
                    put("docking", "1_291");
                    put("farm", "1_292");
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
    public void LukesBlasterPistolStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("blaster").getBlueprint();

        assertEquals("Luke's Blaster Pistol", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.CLOUD_CITY);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LukesBlasterPistolOpponentMayNotMoveFromHereDuringBattle() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var docking = scn.GetDSCard("docking");
        var farm = scn.GetDSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(docking);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(docking, luke, vader);
        scn.AttachCardsTo(luke, blaster);
        assertFalse(scn.game().getModifiersQuerying().mayNotMoveFromLocationToLocationUsingLandspeed(
                scn.gameState(), vader, docking, farm, false));
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(docking);
        scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().mayNotMoveFromLocationToLocationUsingLandspeed(
                scn.gameState(), vader, docking, farm, false));
    }

    @Test
    public void LukesBlasterPistolHitsCharacterWhenDestinyPlusTwoExceedsDefenseValueAndForfeitMinusThree() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        scn.AttachCardsTo(luke, blaster);
        float forfeitBefore = scn.GetForfeit(vader);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.PrepareLSDestiny(5);
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(vader);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(vader.isHit());
        assertEquals(forfeitBefore - 3, scn.GetForfeit(vader), scn.epsilon);
    }

    @Test
    public void LukesBlasterPistolTargetedCharacterLosesImmunityToAttritionForRemainderOfTurn() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        scn.AttachCardsTo(luke, blaster);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(
                scn.gameState(), vader), scn.epsilon);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(0);
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(vader);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertFalse(vader.isHit());
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(
                scn.gameState(), vader), scn.epsilon);
    }

    @Test
    public void LukesBlasterPistolMayTargetAVehicleWithManeuverAndNotAnATAT() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var bantha = scn.GetDSCard("bantha");
        var walker = scn.GetDSCard("walker");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, bantha, walker);
        scn.AttachCardsTo(luke, blaster);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(bantha));
        assertFalse(scn.LSHasCardChoiceAvailable(walker));
    }

    @Test
    public void LukesBlasterPistolMayFireOnlyOncePerBattle() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, vader);
        scn.AttachCardsTo(luke, blaster);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(0);
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(vader);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            assertFalse(scn.LSCardActionAvailable(blaster, "Fire"));
        }
    }
}
