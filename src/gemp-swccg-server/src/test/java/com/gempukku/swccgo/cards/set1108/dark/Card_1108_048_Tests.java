package com.gempukku.swccgo.cards.set1108.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1108_048_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("blaster", "1_156");
                }},
                new HashMap<>() {{
                    put("urrur", "1108_48");
                    put("urorru", "1210_25");
                    put("gaffi", "1_315");
                    put("farm", "1_292");
                    put("moseisley", "1_295");
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
    public void UrRurStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("urrur").getBlueprint();

        assertEquals("Ur'Ru'r", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.SPECIAL_EDITION);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void UrRurDeploysOnTatooine() {
        var scn = GetScenario();
        var urrur = scn.GetDSCard("urrur");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(urrur);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.DSDeployAvailable(urrur));
        scn.DSDeployCard(urrur);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        assertAtLocation(site, urrur);
        assertEquals(Zone.AT_LOCATION, urrur.getZone());
    }

    @Test
    public void UrRurDeploysTuskenWeaponFromReserveWhileWithURoRRu() {
        var scn = GetScenario();
        var urrur = scn.GetDSCard("urrur");
        var urorru = scn.GetDSCard("urorru");
        var gaffi = scn.GetDSCard("gaffi");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, urrur, urorru);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(gaffi);
        assertTrue(scn.DSCardActionAvailable(urrur, "Tusken weapon"));
        scn.DSUseCardAction(urrur, "Tusken weapon");
        assertTrue(scn.DSDecisionAvailable("Choose card"));
        scn.DSChooseCard(gaffi);
        assertTrue(scn.DSDecisionAvailable("Choose"));
        scn.DSChooseCard(urrur);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(urrur, gaffi));
    }

    @Test
    public void UrRurMovesAsARegularMoveDuringDeployPhase() {
        var scn = GetScenario();
        var urrur = scn.GetDSCard("urrur");
        var farm = scn.GetDSCard("farm");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(site, urrur);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.IsAdjacentTo(site, farm));
        assertTrue(scn.DSCardActionAvailable(urrur, "Move as a regular move"));
        scn.DSUseCardAction(urrur, "Move as a regular move");
        scn.PassForceUseResponses();
        scn.PassAllResponses();

        assertAtLocation(farm, urrur);
    }

    @Test
    public void UrRurAddsTwoToTuskenWeaponDestiny() {
        var scn = GetScenario();
        var urrur = scn.GetDSCard("urrur");
        var gaffi = scn.GetDSCard("gaffi");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetDSStartingLocation();
        var destiny2 = scn.GetDSDestiny(2);

        scn.StartGame();
        scn.MoveCardsToLocation(site, urrur, luke);
        scn.AttachCardsTo(urrur, gaffi);
        scn.AttachCardsTo(luke, blaster);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        scn.PrepareDSDestiny(3);
        scn.PrepareDSDestiny(2);
        assertTrue(scn.DSCanInitiateBattle(site));
        scn.DSUseCardAction(site, "Initiate battle");
        scn.PassForceUseResponses();
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("Battle just initiated"));
        assertTrue(scn.DSCardActionAvailable(gaffi, "Fire"));
        scn.DSUseCardAction(gaffi, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw(2);
        scn.PassAllResponses();
        scn.PassResponses("Battle just initiated");
        scn.PassAllResponses();

        assertEquals(9, lastLoggedDestinyTotal(scn));
        assertTrue(destiny2.getZone() == Zone.USED_PILE || destiny2.getZone() == Zone.LOST_PILE);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(blaster, "Fire"));
    }

    private int lastLoggedDestinyTotal(VirtualTableScenario scn) {
        Integer last = null;
        for (String msg : scn.gameState().getLastMessages()) {
            String lower = msg.toLowerCase();
            int idx = lower.lastIndexOf("destiny: ");
            if (idx >= 0) {
                String rest = msg.substring(idx + "destiny: ".length()).trim();
                last = (int) Float.parseFloat(rest.split("[^0-9.]")[0]);
            }
        }
        assertTrue(last != null);
        return last;
    }
}
