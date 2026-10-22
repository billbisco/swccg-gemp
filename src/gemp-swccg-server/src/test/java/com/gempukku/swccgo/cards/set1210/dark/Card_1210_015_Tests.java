package com.gempukku.swccgo.cards.set1210.dark;

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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("han", "1_11");
                    put("threepio", "1_5");
                }},
                new HashMap<>() {{
                    put("kitik", "1210_15");
                    put("pilot", "1210_13");
                    put("stormtrooper", "1_194");
                    put("avenger", "4_172");
                    put("sandcrawler", "1_309");
                    put("farm", "1_294");
                    put("tatooine", "1_289");
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
    public void KitikKeedkakStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("kitik").getBlueprint();

        assertEquals("Kitik Keed'kak", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(8, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FEMALE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KitikKeedkakAddsOneToAttritionPerOpponentsCharacterPresent() {
        var scn = GetScenario();
        var kitik = scn.GetDSCard("kitik");
        var pilot = scn.GetDSCard("pilot");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var han = scn.GetLSCard("han");
        var threepio = scn.GetLSCard("threepio");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, kitik, pilot, stormtrooper, han, threepio);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(0);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.DSCardActionAvailable(kitik, "Add to attrition"));
        scn.DSUseCardAction(kitik, "Add to attrition");
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(true);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertEquals(2, scn.GetUnpaidLSAttrition());
    }

    @Test
    public void KitikKeedkakMayNotDeployToStarfighterOrEnclosedVehicle() {
        var scn = GetScenario();
        var kitik = scn.GetDSCard("kitik");
        var avenger = scn.GetDSCard("avenger");
        var sandcrawler = scn.GetDSCard("sandcrawler");
        var farm = scn.GetDSCard("farm");
        var tatooine = scn.GetDSCard("tatooine");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(tatooine, avenger);
        scn.MoveCardsToLocation(farm, sandcrawler);
        scn.MoveCardsToDSHand(kitik);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(kitik));
        scn.DSPlayCard(kitik);
        assertFalse(scn.DSHasCardChoiceAvailable(avenger));
        assertFalse(scn.DSHasCardChoiceAvailable(sandcrawler));
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, kitik.getAtLocation());
    }

    @Test
    public void KitikKeedkakMayNotBoardStarfighterOrEnclosedVehicle() {
        var scn = GetScenario();
        var kitik = scn.GetDSCard("kitik");
        var avenger = scn.GetDSCard("avenger");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, avenger, kitik);
        scn.SkipToDSTurn(Phase.MOVE);
        assertFalse(scn.GetDSAvailableActions().toString().toLowerCase().contains("board"));
        assertNotEquals(avenger, kitik.getAttachedTo());
    }
}
