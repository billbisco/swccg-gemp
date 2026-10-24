package com.gempukku.swccgo.cards.set1210.dark;

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
import static org.junit.Assert.assertTrue;

public class Card_1210_041_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("pack", "1210_41");
                    put("dathcha", "1_169");
                    put("jawa", "1_182");
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
    public void JawaPackStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("pack").getBlueprint();

        assertEquals("Jawa Pack", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JawaPackDeploysOnTable() {
        var scn = GetScenario();
        var pack = scn.GetDSCard("pack");

        scn.StartGame();
        scn.MoveCardsToDSHand(pack);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.DSCardPlayAvailable(pack) || scn.DSDeployAvailable(pack));
        scn.DSPlayCard(pack);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, pack);
    }

    @Test
    public void JawaPackAddsOneDefenseValueToYourUniqueJawas() {
        var scn = GetScenario();
        var pack = scn.GetDSCard("pack");
        var dathcha = scn.GetDSCard("dathcha");
        var jawa = scn.GetDSCard("jawa");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, dathcha, jawa);
        int uniqueBefore = scn.GetDefense(dathcha);
        int nonUniqueBefore = scn.GetDefense(jawa);
        scn.MoveCardsToDSSideOfTable(pack);

        assertEquals(uniqueBefore + 1, scn.GetDefense(dathcha));
        assertEquals(nonUniqueBefore, scn.GetDefense(jawa));
    }

    @Test
    public void JawaPackAddsOneDestinyToTotalPowerInBattleAtExteriorTatooineSiteWithUniqueJawa() {
        var scn = GetScenario();
        var pack = scn.GetDSCard("pack");
        var dathcha = scn.GetDSCard("dathcha");
        var luke = scn.GetLSCard("luke");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(pack);
        scn.MoveCardsToLocation(marketplace, dathcha, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(5);
        assertTrue(scn.DSCanInitiateBattle(marketplace));
        scn.DSInitiateBattle(marketplace);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        completeDestinyToPowerThenSkipBattleDestiny(scn);

        assertEquals(scn.GetPower(dathcha) + 5, scn.GetDSTotalPower());
    }

    @Test
    public void JawaPackDoesNotAddDestinyToTotalPowerAtCloudCity() {
        var scn = GetScenario();
        var pack = scn.GetDSCard("pack");
        var dathcha = scn.GetDSCard("dathcha");
        var luke = scn.GetLSCard("luke");
        var walkway = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(pack);
        scn.MoveCardsToLocation(walkway, dathcha, luke);
        scn.PrepareDSDestiny(5);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(walkway);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        completeDestinyToPowerThenSkipBattleDestiny(scn);

        assertEquals(scn.GetPower(dathcha), scn.GetDSTotalPower());
    }

    @Test
    public void JawaPackDoesNotAddDestinyToTotalPowerWithoutAUniqueJawa() {
        var scn = GetScenario();
        var pack = scn.GetDSCard("pack");
        var jawa = scn.GetDSCard("jawa");
        var luke = scn.GetLSCard("luke");
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(pack);
        scn.MoveCardsToLocation(marketplace, jawa, luke);
        scn.PrepareDSDestiny(5);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(marketplace);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        completeDestinyToPowerThenSkipBattleDestiny(scn);

        assertEquals(scn.GetPower(jawa), scn.GetDSTotalPower());
    }

    private void completeDestinyToPowerThenSkipBattleDestiny(VirtualTableScenario scn) {
        for (int i = 0; i < 16; i++) {
            if (scn.LSDecisionAvailable("battle destiny?") || scn.DSDecisionAvailable("battle destiny?")
                    || scn.LSDecisionAvailable("BEFORE_BATTLE_DESTINY_DRAWS")
                    || scn.DSDecisionAvailable("BEFORE_BATTLE_DESTINY_DRAWS")) {
                break;
            }
            if (scn.DSDecisionAvailable("DESTINY_DRAWN") || scn.DSDecisionAvailable("COST_TO_DRAW")
                    || scn.DSDecisionAvailable("ABOUT_TO_DRAW") || scn.DSDecisionAvailable("COMPLETE_DESTINY")
                    || scn.DSDecisionAvailable("DRAWING_DESTINY") || scn.DSDecisionAvailable("destiny to power")) {
                scn.PassDestinyDrawResponses();
                continue;
            }
            if (scn.LSDecisionAvailable("DESTINY_DRAWN") || scn.LSDecisionAvailable("COST_TO_DRAW")
                    || scn.LSDecisionAvailable("ABOUT_TO_DRAW") || scn.LSDecisionAvailable("COMPLETE_DESTINY")
                    || scn.LSDecisionAvailable("DRAWING_DESTINY")) {
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
