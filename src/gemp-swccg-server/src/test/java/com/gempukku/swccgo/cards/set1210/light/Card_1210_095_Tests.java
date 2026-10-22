package com.gempukku.swccgo.cards.set1210.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_095_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("gospic", "1210_95");
                    put("balanced", "7_84");
                }},
                new HashMap<>() {{
                    put("st1", "1_194");
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

    protected VirtualTableScenario GetAttackRunScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("gospic", "1210_95");
                    put("tech", "1210_110");
                    put("warroom", "1_139");
                    put("attackrun", "2_42");
                }},
                new HashMap<>() {{
                    put("st1", "1_194");
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

    protected VirtualTableScenario GetMBOScenario() {
        // Keep Massassi Base Operations out of Reserve so StartGame does not auto-play it.
        StartingSetup mboOutsideDeck = new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>() {{
                    put("mbo", "111_4");
                }};
            }

            @Override
            public void Setup(VirtualTableScenario scn) {
            }
        };
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("gospic", "1210_95");
                    put("yavin4", "1_135");
                    put("dockingbay", "1_136");
                }},
                new HashMap<>() {{
                    put("st1", "1_194");
                    put("st2", "1_194");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                mboOutsideDeck,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ColonelFeynGospicStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("gospic").getBlueprint();

        assertEquals("Colonel Feyn Gospic", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GospicDoublesRebelTechBonusToAttackRun() {
        var scn = GetAttackRunScenario();
        var gospic = scn.GetLSCard("gospic");
        var tech = scn.GetLSCard("tech");
        var warroom = scn.GetLSCard("warroom");
        var attackrun = scn.GetLSCard("attackrun");

        scn.StartGame();
        scn.MoveLocationToTable(warroom);
        scn.MoveCardsToLocation(warroom, tech, gospic);
        scn.MoveCardsToLSSideOfTable(attackrun);
        assertEquals(2, scn.game().getModifiersQuerying().getEpicEventCalculationTotal(scn.gameState(), attackrun, 0), scn.epsilon);
    }

    @Test
    public void GospicWithMassassiBaseOperationsLimitsOneImperialPerYavin4SitePerTurn() {
        var scn = GetMBOScenario();
        var gospic = scn.GetLSCard("gospic");
        var mbo = scn.GetLSCard("mbo");
        var yavin4 = scn.GetLSCard("yavin4");
        var dockingbay = scn.GetLSCard("dockingbay");
        var st1 = scn.GetDSCard("st1");
        var st2 = scn.GetDSCard("st2");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(mbo);
        scn.MoveLocationToTable(yavin4);
        scn.MoveLocationToTable(dockingbay);
        scn.MoveCardsToLocation(dockingbay, gospic);
        scn.MoveCardsToDSHand(st1, st2);
        scn.EnsureDSForcePile(8);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(8);
        assertTrue(scn.DSCardPlayAvailable(st1));
        scn.DSPlayCard(st1);
        scn.DSChooseCard(dockingbay);
        scn.PassCardAndForceUseResponses();
        for (int i = 0; i < 12; i++) {
            if (scn.DSDecisionAvailable("optional response") || scn.LSDecisionAvailable("optional response")) {
                scn.PassAllResponses();
            }
            else if (scn.DSDecisionAvailable("required") || scn.LSDecisionAvailable("required")) {
                scn.PassResponses("required");
            }
            else if (scn.GetAwaitingDecision(scn.LS) != null
                    && (scn.LSDecisionAvailable("Limit Imperials") || scn.LSCardActionAvailable(gospic, "Limit Imperials"))) {
                scn.LSUseCardAction(gospic, "Limit Imperials");
            }
            else if (scn.LSDecisionAvailable("Choose Deploy action or Pass") || scn.LSDecisionAvailable("Choose action or Pass")) {
                scn.LSPass();
            }
            else {
                break;
            }
        }
        assertEquals(dockingbay, st1.getAtLocation());
        assertTrue(scn.DSCardPlayAvailable(st2));
        scn.DSPlayCard(st2);
        assertFalse(scn.DSHasCardChoiceAvailable(dockingbay));
    }

    @Test
    public void GospicTakesBalancedAttackOncePerTurn() {
        var scn = GetScenario();
        var gospic = scn.GetLSCard("gospic");
        var balanced = scn.GetLSCard("balanced");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, gospic);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(balanced);
        assertTrue(scn.LSCardActionAvailable(gospic, "Take card into hand from Reserve Deck"));
        scn.LSUseCardAction(gospic, "Take card into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(balanced);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, balanced.getZone());
    }
}
