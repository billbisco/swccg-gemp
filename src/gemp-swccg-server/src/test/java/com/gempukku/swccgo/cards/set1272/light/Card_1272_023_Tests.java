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
                    put("wedge", "9_31");
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
        assertEquals("Deploy on table. Canceled if your Senate or [Reflections 2] objective on table. Unless your character of ability > 4 at a site, Rogue Squadron pilots are power +1 (power +2 if ability < 3), and armor = 4. (Immune to Alter.)", card.getGameText());
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
    public void RogueInsertionRogueSquadronPilotsGetPowerByAbilityAndArmorFour() {
        var scn = GetScenario();
        var insertion = scn.GetLSCard("insertion");
        var hobbie = scn.GetLSCard("hobbie");
        var wedge = scn.GetLSCard("wedge");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, hobbie, wedge, luke);
        assertEquals(2, scn.GetPower(hobbie));
        assertEquals(3, scn.GetPower(wedge));
        scn.MoveCardsToLSSideOfTable(insertion);
        assertEquals(2, scn.GetAbility(hobbie), scn.epsilon);
        assertEquals("ability < 3: power +2", 4, scn.GetPower(hobbie));
        assertEquals(3, scn.GetAbility(wedge), scn.epsilon);
        assertEquals("ability 3: power +1 only", 4, scn.GetPower(wedge));
        assertEquals(4, scn.GetArmor(hobbie));
        assertEquals(4, scn.GetArmor(wedge));
        // Not a Rogue Squadron pilot: unaffected.
        assertEquals(3, scn.GetPower(luke));
    }

    @Test
    public void RogueInsertionNoLongerGrantsSpyOrAttritionImmunity() {
        var scn = GetScenario();
        var insertion = scn.GetLSCard("insertion");
        var hobbie = scn.GetLSCard("hobbie");

        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), hobbie);
        scn.MoveCardsToLSSideOfTable(insertion);
        assertFalse(scn.HasKeyword(hobbie, Keyword.SPY));
        assertFalse(scn.IsImmuneToAttrition(hobbie));
    }

    @Test
    public void RogueInsertionBonusesDoNotApplyWithYourCharacterOfAbilityMoreThanFourAtASite() {
        var scn = GetScenario();
        var insertion = scn.GetLSCard("insertion");
        var hobbie = scn.GetLSCard("hobbie");
        var wedge = scn.GetLSCard("wedge");
        var obiwan = scn.GetLSCard("obiwan");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(insertion);
        scn.MoveCardsToLocation(site, hobbie, wedge);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), obiwan);
        assertEquals(2, scn.GetPower(hobbie));
        assertEquals(3, scn.GetPower(wedge));
        assertFalse(scn.GetArmor(hobbie) == 4);
    }

    private void fireImperialBlasterAtHobbie(VirtualTableScenario scn, boolean insertionOnTable, int destiny) {
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
    public void RogueInsertionArmorFourStopsDestinyFourButWeaponDestinyIsNoLongerReduced() {
        var scn = GetScenario();
        var hobbie = scn.GetLSCard("hobbie");
        fireImperialBlasterAtHobbie(scn, true, 4);
        assertEquals(4, scn.GetDefense(hobbie));
        assertFalse(hobbie.isHit());

        // Old slip's "-1 to weapon destiny" is gone: destiny 5 > armor 4 hits.
        var scn2 = GetScenario();
        var hobbie2 = scn2.GetLSCard("hobbie");
        fireImperialBlasterAtHobbie(scn2, true, 5);
        assertTrue(hobbie2.isHit());
    }

    @Test
    public void WithoutRogueInsertionWeaponDestinyHitsRogueSquadronPilot() {
        var scn = GetScenario();
        var hobbie = scn.GetLSCard("hobbie");

        fireImperialBlasterAtHobbie(scn, false, 3);
        assertTrue(hobbie.isHit());
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
