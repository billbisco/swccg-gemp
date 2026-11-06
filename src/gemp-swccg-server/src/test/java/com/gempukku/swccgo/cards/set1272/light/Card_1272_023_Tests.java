package com.gempukku.swccgo.cards.set1272.light;

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
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyForWeaponTargetingModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("insertion", "1272_23");
                    put("hobbie", "3_5");
                    put("luke", "1_19");
                    put("obiwan", "1_21");
                    put("senate", "12_75");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317");
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
    public void RogueInsertionStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("insertion").getBlueprint();

        assertEquals("Rogue Insertion", card.getTitle());
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
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RogueInsertionIsImmuneToAlter() {
        var scn = GetScenario();
        var insertion = scn.GetLSCard("insertion");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(insertion);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), insertion, Title.Alter));
    }

    @Test
    public void RogueInsertionRogueSquadronPilotsAreSpiesWithPowerArmorAndAttritionImmunity() {
        var scn = GetScenario();
        var insertion = scn.GetLSCard("insertion");
        var hobbie = scn.GetLSCard("hobbie");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        //test1: Rogue Squadron pilots are spies
        //test2: power +2
        //test3: armor = 4
        //test4: immune to attrition < 4
        //test5: other characters are not affected
        scn.StartGame();
        scn.MoveCardsToLocation(site, hobbie, luke);
        assertFalse(scn.HasKeyword(hobbie, Keyword.SPY));
        assertEquals(2, scn.GetPower(hobbie));
        assertFalse(scn.IsImmuneToAttrition(hobbie));
        scn.MoveCardsToLSSideOfTable(insertion);
        assertTrue(scn.HasKeyword(hobbie, Keyword.SPY)); //test1
        assertEquals(4, scn.GetPower(hobbie)); //test2
        assertEquals(4, scn.GetArmor(hobbie)); //test3
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), hobbie), scn.epsilon); //test4
        assertFalse(scn.HasKeyword(luke, Keyword.SPY)); //test5
        assertEquals(3, scn.GetPower(luke)); //test5
    }

    @Test
    public void RogueInsertionBonusesDoNotApplyWithYourCharacterOfAbilityMoreThanFourAtASite() {
        var scn = GetScenario();
        var insertion = scn.GetLSCard("insertion");
        var hobbie = scn.GetLSCard("hobbie");
        var obiwan = scn.GetLSCard("obiwan");
        var site = scn.GetLSStartingLocation();

        //test1: still a spy
        //test2: no power, armor or immunity bonus
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(insertion);
        scn.MoveCardsToLocation(site, hobbie);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), obiwan);
        assertTrue(scn.HasKeyword(hobbie, Keyword.SPY)); //test1
        assertEquals(2, scn.GetPower(hobbie)); //test2
        assertFalse(scn.IsImmuneToAttrition(hobbie)); //test2
    }

    private void fireImperialBlasterAtHobbie(VirtualTableScenario scn, boolean insertionOnTable, int destiny) {
        fireImperialBlasterAtHobbie(scn, insertionOnTable, destiny, 0);
    }

    private void fireImperialBlasterAtHobbie(VirtualTableScenario scn, boolean insertionOnTable, int destiny, int extraModifier) {
        var insertion = scn.GetLSCard("insertion");
        var hobbie = scn.GetLSCard("hobbie");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        if (insertionOnTable)
            scn.MoveCardsToLSSideOfTable(insertion);
        scn.MoveCardsToLocation(site, hobbie, trooper);
        scn.AttachCardsTo(trooper, blaster);
        if (extraModifier != 0)
            scn.ApplyAdHocModifier(new EachWeaponDestinyForWeaponTargetingModifier(trooper, hobbie, extraModifier));
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(destiny);
        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(hobbie);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
    }

    @Test
    public void RogueInsertionWeaponDestinyTargetingRogueSquadronPilotIsMinusOne() {
        var scn = GetScenario();
        var hobbie = scn.GetLSCard("hobbie");

        //test1: armor = 4 is the defense value
        //test2: destiny 5 - 1 is not more than defense value 4
        fireImperialBlasterAtHobbie(scn, true, 5);
        assertEquals(4, scn.GetDefense(hobbie)); //test1
        assertFalse(hobbie.isHit()); //test2
    }

    @Test
    public void WithoutRogueInsertionWeaponDestinyHitsRogueSquadronPilot() {
        var scn = GetScenario();
        var hobbie = scn.GetLSCard("hobbie");

        fireImperialBlasterAtHobbie(scn, false, 3);
        assertTrue(hobbie.isHit());
    }

    @Test
    public void RogueInsertionWeaponDestinyTargetingRogueSquadronPilotMayNotBeFurtherReduced() {
        var scn = GetScenario();
        var hobbie = scn.GetLSCard("hobbie");

        //test1: an extra -1 is ignored, so destiny 6 is only reduced to 5, which is more than defense value 4
        fireImperialBlasterAtHobbie(scn, true, 6, -1);
        assertTrue(hobbie.isHit()); //test1
    }

    @Test
    public void RogueInsertionIsCanceledIfYourSenateIsOnTable() {
        var scn = GetScenario();
        var insertion = scn.GetLSCard("insertion");
        var senate = scn.GetLSCard("senate");

        //test1: Rogue Insertion is canceled
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(insertion);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertInZone(Zone.SIDE_OF_TABLE, insertion);
        scn.MoveLocationToTable(senate);
        scn.SkipToPhase(Phase.BATTLE);
        assertInZone(Zone.LOST_PILE, insertion); //test1
    }
}
