package com.gempukku.swccgo.cards.set1291.light;

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

public class Card_1291_038_Tests {
    protected VirtualTableScenario GetScenario() {
        return GetScenario(StartingSetup.DefaultLSGroundLocation);
    }

    protected VirtualTableScenario GetSpaceScenario() {
        return GetScenario(StartingSetup.DefaultLSSpaceSystem);
    }

    protected VirtualTableScenario GetScenario(StartingSetup lsLocation) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("specialist", "1291_38");
                    put("arc", "214_14"); // ARC-170 Starfighter: [Clone Army], power 3, maneuver 4
                    put("n1", "12_90"); // Naboo Defense Fighter: [Republic] but not [Clone Army], maneuver 4
                    put("xwing", "1_146"); // Red 5
                    put("cannon", "7_162"); // X-wing Laser Cannon: targets a starfighter
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("tie", "1_304");
                }},
                10, 10,
                lsLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void CloneSpecialistStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("specialist").getBlueprint();
        assertEquals("Clone Specialist", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals("Clone trooper.", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CLONE_TROOPER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.CLONE_ARMY);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CloneSpecialistPilotingCloneArmyStarfighterAddsPowerDefenseValueAndForfeit() {
        var scn = GetSpaceScenario();
        var specialist = scn.GetLSCard("specialist");
        var arc = scn.GetLSCard("arc");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), arc);
        assertEquals(3, scn.GetPower(arc));
        assertEquals(4, scn.GetDefense(arc));
        scn.BoardAsPilot(arc, specialist);
        assertEquals(5, scn.GetPower(arc));
        assertEquals(5, scn.GetDefense(arc));
        assertEquals(5, scn.GetForfeit(specialist));
    }

    @Test
    public void CloneSpecialistPilotingNonCloneArmyStarfighterAddsOnlyPower() {
        var scn = GetSpaceScenario();
        var specialist = scn.GetLSCard("specialist");
        var n1 = scn.GetLSCard("n1");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), n1);
        int power = scn.GetPower(n1);
        scn.BoardAsPilot(n1, specialist);
        assertEquals(power + 2, scn.GetPower(n1));
        assertEquals(4, scn.GetDefense(n1));
        assertEquals(3, scn.GetForfeit(specialist));
    }

    private void fireCannonFromPilotedStarfighter(VirtualTableScenario scn) {
        var specialist = scn.GetLSCard("specialist");
        var xwing = scn.GetLSCard("xwing");
        var cannon = scn.GetLSCard("cannon");
        var tie = scn.GetDSCard("tie");
        var system = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(system, xwing, tie);
        scn.BoardAsPilot(xwing, specialist);
        scn.AttachCardsTo(xwing, cannon);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsAction(scn, cannon, "Fire"));
        scn.LSUseCardAction(cannon, "Fire");
        if (scn.LSDecisionAvailable("number for X")) {
            scn.LSDecided("0");
        }
        Vb91LightTestSupport.advanceUntil(scn, () -> Vb91LightTestSupport.lsCardChoiceCount(scn) > 0);
        scn.LSChooseCard(tie);
        if (scn.LSDecisionAvailable("number for X")) {
            scn.LSDecided("0");
        }
    }

    @Test
    public void CloneSpecialistMayDrawTwoWeaponDestinyWhenPilotedStarshipFiresWeapon() {
        var scn = GetSpaceScenario();
        var specialist = scn.GetLSCard("specialist");
        fireCannonFromPilotedStarfighter(scn);
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsAction(scn, specialist, "Draw two weapon destiny"));
        scn.LSUseCardAction(specialist, "Draw two weapon destiny");
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsCardChoiceCount(scn) == 2);
    }
}
