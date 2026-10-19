package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1281_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("han", "1281_27");
                    put("blaster", "1281_46");
                    put("falcon", "1_143");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("dsblaster", "1_312");
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
    public void HanSoloInnocentScoundrelStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("han").getBlueprint();

        assertEquals("Han Solo, Innocent Scoundrel", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        assertTrue(card.hasSpecialDefenseValueAttribute());
        assertEquals(5, card.getSpecialDefenseValue(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.HAN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HanSoloInnocentScoundrelDefenseValueIsLuckFive() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(3, scn.GetAbility(han));
        assertEquals(5, scn.GetDefense(han));
    }

    @Test
    public void HanSoloInnocentScoundrelAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var falcon = scn.GetLSCard("falcon");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, falcon);
        scn.BoardAsPilot(falcon, han);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(6, scn.GetPower(falcon));
    }

    @Test
    public void HanSoloInnocentScoundrelDrawsBattleDestinyIfUnableToOtherwise() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.SkipToPowerSegment();

        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassAllResponses();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void HanSoloInnocentScoundrelFiresAVirtualBlock8BlasterTwiceDuringBattle() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(han, blaster);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(5);

        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
    }

    @Test
    public void HanSoloInnocentScoundrelIsImmuneToAttritionLessThanThreeAndFourIfArmed() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), han), scn.epsilon);

        scn.AttachCardsTo(han, blaster);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), han), scn.epsilon);
    }

    @Test
    public void HanSoloInnocentScoundrelOffersWeaponDestinyChooseWhenTargeted() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var dsblaster = scn.GetDSCard("dsblaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(trooper, dsblaster);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(5);

        assertTrue(scn.DSCardActionAvailable(dsblaster, "Fire"));
        scn.DSUseCardAction(dsblaster, "Fire");
        scn.DSChooseCard(han);
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.DSPass();

        assertTrue(scn.LSCardActionAvailable(han, "Draw two weapon destinies"));
        scn.LSUseCardAction(han, "Draw two weapon destinies");
        scn.PassAllResponses();
        assertTrue(scn.DSDecisionAvailable("Choose 1 destiny") || scn.LSDecisionAvailable("Choose 1 destiny"));
    }
}
