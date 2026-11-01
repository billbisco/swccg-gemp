package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1116_058_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("blaster", "1_154");
                }},
                new HashMap<>() {{
                    put("myn", "1116_58");
                    put("trooper", "1_194");
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
    public void MynKyneughStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("myn").getBlueprint();

        assertEquals("Myn Kyneugh", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ROYAL_GUARD);
            add(Keyword.LEADER);
            add(Keyword.HEROIC);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
            add(Icon.DEATH_STAR_II);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_16, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MynKyneughMustBeTargetedByWeaponsBeforeNonHeroic() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var myn = scn.GetDSCard("myn");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, myn, trooper, luke);
        scn.AttachCardsTo(luke, blaster);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(myn));
        assertTrue(scn.LSHasCardChoiceNotAvailable(trooper));
    }

    @Test
    public void MynKyneughIsImmuneToAttritionLessThanFour() {
        var scn = GetScenario();
        var myn = scn.GetDSCard("myn");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, myn);

        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), myn), scn.epsilon);
    }

    @Test
    public void MynKyneughForcePikeIsMatchingWeapon() {
        var scn = GetPikeScenario();
        var myn = scn.GetDSCard("myn");
        var pike = scn.GetDSCard("pike");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, myn);
        scn.AttachCardsTo(myn, pike);

        assertTrue(scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), myn, pike));
        assertFalse(scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), myn, blaster));
    }

    @Test
    public void MynKyneughForcePikeFiresForFree() throws DecisionResultInvalidException {
        var scn = GetPikeScenario();
        var myn = scn.GetDSCard("myn");
        var pike = scn.GetDSCard("pike");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, myn, luke);
        scn.AttachCardsTo(myn, pike);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(pike, "Fire"));
        scn.DSUseCardAction(pike, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void MynKyneughForcePikeTotalWeaponDestinyIsPlusOne() throws DecisionResultInvalidException {
        // Force Pike hits if (total destiny + 1) > defense. Destiny 0 + Myn +1 logs 1;
        // 1+1 > Rebel Guard defense 1 hits. Without Myn, 0+1 is not > 1.
        var scn = GetPikeScenario();
        var myn = scn.GetDSCard("myn");
        var pike = scn.GetDSCard("pike");
        var guard = scn.GetLSCard("guard");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, myn, guard);
        scn.AttachCardsTo(myn, pike);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(0);

        assertEquals(1, scn.GetDefense(guard));
        assertTrue(scn.DSCardActionAvailable(pike, "Fire"));
        scn.DSUseCardAction(pike, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(guard));
        scn.DSChooseCard(guard);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertEquals(1, lastTotalWeaponDestiny(scn));
        assertTrue(guard.isHit());
    }

    @Test
    public void MynKyneughWhenDeployedMayPlayDefensiveShieldFromStartingEffect() {
        var scn = GetShieldScenario();
        var myn = scn.GetDSCard("myn");
        var starting = scn.GetDSCard("starting");
        var shield = scn.GetDSCard("shield");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.StackCardsOn(starting, shield);
        scn.MoveCardsToHand(myn);
        scn.DSActivateMaxForceAndPass();
        scn.PassControlActions();
        scn.DSActivateForceCheat(5);

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardPlayAvailable(myn));
        scn.DSPlayCard(myn);
        scn.DSChooseCard(site);
        scn.PassResponses("Use");
        while (scn.LSDecisionAvailable("optional")) {
            scn.LSPass();
        }
        assertTrue("Defensive Shield must be offered on deploy; startingZone=" + starting.getZone()
                        + " shieldZone=" + shield.getZone()
                        + " decision=" + (scn.DSGetDecision() == null ? "null" : scn.DSGetDecision().getText())
                        + " actions=" + scn.GetDSAvailableActions(),
                scn.GetDSAvailableActions().toString().contains("Defensive Shield"));
        scn.DSChooseAction("Defensive Shield");
        scn.DSChooseCard(shield);
        scn.PassAllResponses();
        assertTrue(shield.getZone().isInPlay());
    }

    private int lastTotalWeaponDestiny(VirtualTableScenario scn) {
        Integer last = null;
        String log = String.join("\n", scn.gameState().getLastMessages());
        for (String msg : scn.gameState().getLastMessages()) {
            String lower = msg.toLowerCase();
            for (String prefix : new String[] {"total weapon destiny is ", "total destiny: "}) {
                int idx = lower.lastIndexOf(prefix);
                if (idx >= 0) {
                    String rest = msg.substring(idx + prefix.length()).trim();
                    last = (int) Float.parseFloat(rest.split("[^0-9.]")[0]);
                }
            }
        }
        assertTrue("Expected a total weapon destiny log, got: " + log, last != null);
        return last;
    }

    private VirtualTableScenario GetPikeScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("guard", "1_26");
                }},
                new HashMap<>() {{
                    put("myn", "1116_58");
                    put("pike", "9_179");
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

    private VirtualTableScenario GetShieldScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("myn", "1116_58");
                    put("starting", "13_69");
                    put("shield", "13_81");
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
}
