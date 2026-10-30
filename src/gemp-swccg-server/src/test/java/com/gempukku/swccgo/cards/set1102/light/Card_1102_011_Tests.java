package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.common.CardSubtype;
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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1102_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("siesta", "1102_11");
                    put("het", "2_9");
                    put("jawa", "1_12");
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

    @Test
    public void JawaSiestaStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("siesta").getBlueprint();

        assertEquals("Jawa Siesta", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JawaSiestaDeploysOnTable() {
        var scn = GetScenario();
        var siesta = scn.GetLSCard("siesta");

        scn.StartGame();
        scn.MoveCardsToLSHand(siesta);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(siesta) || scn.LSDeployAvailable(siesta));
        scn.LSPlayCard(siesta);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, siesta);
    }

    @Test
    public void JawaSiestaAddsOneDefenseValueToYourUniqueJawas() {
        var scn = GetScenario();
        var siesta = scn.GetLSCard("siesta");
        var het = scn.GetLSCard("het");
        var jawa = scn.GetLSCard("jawa");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(marketplace, het, jawa);
        int uniqueBefore = scn.GetDefense(het);
        int nonUniqueBefore = scn.GetDefense(jawa);
        scn.MoveCardsToLSSideOfTable(siesta);

        assertEquals(uniqueBefore + 1, scn.GetDefense(het));
        assertEquals(nonUniqueBefore, scn.GetDefense(jawa));
    }

    @Test
    public void JawaSiestaAddsOneDestinyToTotalPowerInBattleAtExteriorTatooineSiteWithUniqueJawa() {
        var scn = GetScenario();
        var siesta = scn.GetLSCard("siesta");
        var het = scn.GetLSCard("het");
        var vader = scn.GetDSCard("vader");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(siesta);
        scn.MoveCardsToLocation(marketplace, het, vader);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCanInitiateBattle(marketplace));
        scn.LSInitiateBattle(marketplace);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        completeDestinyToPowerThenSkipBattleDestiny(scn);

        assertEquals(scn.GetPower(het) + 5, scn.GetLSTotalPower());
    }

    @Test
    public void JawaSiestaDoesNotAddDestinyToTotalPowerAtCloudCity() {
        var scn = GetScenario();
        var siesta = scn.GetLSCard("siesta");
        var het = scn.GetLSCard("het");
        var vader = scn.GetDSCard("vader");
        var walkway = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(siesta);
        scn.MoveCardsToLocation(walkway, het, vader);
        scn.PrepareLSDestiny(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(walkway);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        completeDestinyToPowerThenSkipBattleDestiny(scn);

        assertEquals(scn.GetPower(het), scn.GetLSTotalPower());
    }

    @Test
    public void JawaSiestaDoesNotAddDestinyToTotalPowerWithoutAUniqueJawa() {
        var scn = GetScenario();
        var siesta = scn.GetLSCard("siesta");
        var jawa = scn.GetLSCard("jawa");
        var vader = scn.GetDSCard("vader");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(siesta);
        scn.MoveCardsToLocation(marketplace, jawa, vader);
        scn.PrepareLSDestiny(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(marketplace);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        completeDestinyToPowerThenSkipBattleDestiny(scn);

        assertEquals(scn.GetPower(jawa), scn.GetLSTotalPower());
    }

    private void completeDestinyToPowerThenSkipBattleDestiny(VirtualTableScenario scn) {
        for (int i = 0; i < 16; i++) {
            if (scn.LSDecisionAvailable("battle destiny?") || scn.DSDecisionAvailable("battle destiny?")
                    || scn.LSDecisionAvailable("BEFORE_BATTLE_DESTINY_DRAWS")
                    || scn.DSDecisionAvailable("BEFORE_BATTLE_DESTINY_DRAWS")) {
                break;
            }
            if (scn.LSDecisionAvailable("DESTINY_DRAWN") || scn.LSDecisionAvailable("COST_TO_DRAW")
                    || scn.LSDecisionAvailable("ABOUT_TO_DRAW") || scn.LSDecisionAvailable("COMPLETE_DESTINY")
                    || scn.LSDecisionAvailable("DRAWING_DESTINY") || scn.LSDecisionAvailable("destiny to power")) {
                scn.PassDestinyDrawResponses();
                continue;
            }
            if (scn.DSDecisionAvailable("DESTINY_DRAWN") || scn.DSDecisionAvailable("COST_TO_DRAW")
                    || scn.DSDecisionAvailable("ABOUT_TO_DRAW") || scn.DSDecisionAvailable("COMPLETE_DESTINY")
                    || scn.DSDecisionAvailable("DRAWING_DESTINY")) {
                scn.PassDestinyDrawResponses();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            break;
        }
        scn.SkipBattleDestinyDraws(false);
    }
}
