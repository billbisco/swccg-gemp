package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dack", "1273_22");
                    put("luke", "1_19");
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
    public void DackRalterStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("dack").getBlueprint();

        assertEquals("Dack Ralter", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ROGUE_SQUADRON);
            add(Keyword.GUNNER);
            add(Keyword.HEROIC);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.DACK);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DackRalterMustBeTargetedByWeaponsBeforeNonHeroic() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var dack = scn.GetLSCard("dack");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dack, luke, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(dack));
        assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
    }

    @Test
    public void DackRalterAddsTwoToPowerOfStarshipHePilots() {
        var scn = GetPilotScenario();
        var dack = scn.GetLSCard("dack");
        var ywing = scn.GetLSCard("ywing");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, ywing);
        assertEquals(2, scn.GetPower(ywing));

        scn.BoardAsPilot(ywing, dack);
        assertEquals(4, scn.GetPower(ywing));
    }

    @Test
    public void DackRalterDuringBattleMayAddOneToWeaponDestinyAndHitTargetsArePowerMinusTwo() throws DecisionResultInvalidException {
        // Blaster hits if destiny > defense. Prepared 1 + Dack +1 logs 2 and hits
        // Stormtrooper defense 1. Taking the optional twice would log 3.
        var scn = GetWeaponScenario();
        var dack = scn.GetLSCard("dack");
        var blaster = scn.GetLSCard("lsBlaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dack, trooper);
        scn.AttachCardsTo(dack, blaster);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(1);

        assertEquals(1, scn.GetPower(trooper));
        assertEquals(1, scn.GetDefense(trooper));
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(trooper));
        scn.LSChooseCard(trooper);

        int dackTaken = 0;
        for (int i = 0; i < 40; i++) {
            if (i > 0 && scn.AwaitingLSWeaponsSegmentActions()) {
                break;
            }
            if (scn.GetLSAvailableActions().toString().contains("Add 1 to total weapon destiny")) {
                assertTrue(scn.GetLSAvailableActions().toString().contains("Add 1 to total weapon destiny"));
                scn.LSChooseAction("Add 1 to total weapon destiny");
                dackTaken++;
                continue;
            }
            if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
                continue;
            }
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
                continue;
            }
            throw new AssertionError("No pending decision while firing Dack's blaster after " + i
                    + " steps; Dack taken=" + dackTaken);
        }
        assertEquals(1, dackTaken);
        assertEquals(2, lastTotalWeaponDestiny(scn));
        assertTrue(trooper.isHit());
        assertEquals(0, scn.GetPower(trooper), scn.epsilon);
    }

    @Test
    public void DackRalterAboutToLeaveTableMayPlaceInUsedPile() {
        var scn = GetForfeitScenario();
        var dack = scn.GetLSCard("dack");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dack, vader);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToEndOfPowerSegment(false);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(dack);
        assertTrue("Used Pile must be offered when Dack is about to leave; decision="
                        + (scn.LSGetDecision() == null ? "null" : scn.LSGetDecision().getText())
                        + " actions=" + scn.GetLSAvailableActions(),
                scn.GetLSAvailableActions().toString().contains("Used Pile"));
        scn.LSChooseAction("Used Pile");
        scn.PassAllResponses();
        assertTrue(dack.getZone() == Zone.USED_PILE || dack.getZone() == Zone.TOP_OF_USED_PILE);
    }

    private VirtualTableScenario GetPilotScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dack", "1273_22");
                    put("ywing", "1_147");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
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

    private VirtualTableScenario GetForfeitScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dack", "1273_22");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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

    private VirtualTableScenario GetWeaponScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("dack", "1273_22");
                    put("lsBlaster", "1_152");
                }},
                new HashMap<>() {{
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
}
