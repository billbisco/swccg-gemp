package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127"); // Tatooine system
                    put("luke", "1_19"); // Luke Skywalker, destiny 1
                    put("echo", "3_6"); // Echo Base Trooper (warrior)
                    put("blaster", "1_152"); // Blaster
                    put("echo2", "3_6");
                    put("blaster2", "1_152");
                }},
                new HashMap<>() {{
                    put("bossk", "1281_4");
                    put("tie", "1_304"); // TIE Fighter
                    put("trooper", "1_194"); // Stormtrooper (made a slaver in tests)
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
    public void BosskWithMortarGunVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("bossk").getBlueprint();
        assertEquals("Bossk With Mortar Gun", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(Species.TRANDOSHAN, card.getSpecies());
        assertTrue(card.hasPersona(Persona.BOSSK));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ASSASSIN);
            add(Keyword.SLAVER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.PERMANENT_WEAPON);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BosskWithMortarGunVAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var bossk = scn.GetDSCard("bossk");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, bossk);
        assertEquals(3, scn.GetPower(tie));
    }

    @Test
    public void BosskWithMortarGunVMortarGunHitsAndZeroesForfeitWhenDestinyMatchesTargetsDestinyNumber() {
        var scn = GetScenario();
        var bossk = scn.GetDSCard("bossk");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, bossk, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(1);
        assertTrue(scn.DSCardActionAvailable(bossk, "Fire"));
        scn.DSUseCardAction(bossk, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(luke.isHit());
        assertEquals(0, scn.GetForfeit(luke));
    }

    @Test
    public void BosskWithMortarGunVMortarGunMissesWhenDestinyDoesNotMatch() {
        var scn = GetScenario();
        var bossk = scn.GetDSCard("bossk");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, bossk, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(2);
        scn.DSUseCardAction(bossk, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertFalse(luke.isHit());
    }

    @Test
    public void BosskWithMortarGunVMayNotBeTargetedByWeaponsUnlessOtherSlaversPresentAreHit() {
        var scn = GetScenario();
        var bossk = scn.GetDSCard("bossk");
        var trooper = scn.GetDSCard("trooper");
        var echo = scn.GetLSCard("echo");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, bossk, trooper, echo);
        scn.ApplyAdHocModifier(new KeywordModifier(trooper, Filters.sameCardId(trooper), Keyword.SLAVER));
        scn.AttachCardsTo(echo, blaster);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(trooper));
        assertFalse(scn.LSHasCardChoiceAvailable(bossk));
    }

    @Test
    public void BosskWithMortarGunVMayBeTargetedByWeaponsOnceOtherSlaversPresentAreHit() {
        var scn = GetScenario();
        var bossk = scn.GetDSCard("bossk");
        var trooper = scn.GetDSCard("trooper");
        var echo = scn.GetLSCard("echo");
        var blaster = scn.GetLSCard("blaster");
        var echo2 = scn.GetLSCard("echo2");
        var blaster2 = scn.GetLSCard("blaster2");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, bossk, trooper, echo, echo2);
        scn.ApplyAdHocModifier(new KeywordModifier(trooper, Filters.sameCardId(trooper), Keyword.SLAVER));
        scn.AttachCardsTo(echo, blaster);
        scn.AttachCardsTo(echo2, blaster2);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(6);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(6);
        scn.LSUseCardAction(blaster, "Fire");
        assertFalse(scn.LSHasCardChoiceAvailable(bossk));
        scn.LSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(trooper.isHit());
        if (scn.AwaitingDSWeaponsSegmentActions()) {
            scn.DSPass();
        }
        scn.LSUseCardAction(blaster2, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(bossk));
    }

    @Test
    public void BosskWithMortarGunVMayBeTargetedByWeaponsWithNoOtherSlaverPresent() {
        var scn = GetScenario();
        var bossk = scn.GetDSCard("bossk");
        var trooper = scn.GetDSCard("trooper");
        var echo = scn.GetLSCard("echo");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, bossk, trooper, echo);
        scn.AttachCardsTo(echo, blaster);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(bossk));
    }
}
