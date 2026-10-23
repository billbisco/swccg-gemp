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

public class Card_1210_094_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("chewie", "1210_94");
                    put("han", "1_11");
                    put("falcon", "1_143");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
                    put("stormtrooper2", "1_194");
                    put("stormtrooper3", "1_194");
                    put("stormtrooper4", "1_194");
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
    public void ChewieEnragedStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("chewie").getBlueprint();

        assertEquals("Chewie, Enraged", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SMUGGLER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.REFLECTIONS_III);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ChewieEnragedAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var chewie = scn.GetLSCard("chewie");
        var falcon = scn.GetLSCard("falcon");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, falcon);
        scn.BoardAsPilot(falcon, chewie);
        assertEquals(5, scn.GetPower(falcon));
    }

    @Test
    public void ChewieEnragedIsHeroicWhilePresentAtASiteWithHan() {
        var scn = GetScenario();
        var chewie = scn.GetLSCard("chewie");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, chewie);
        assertFalse(scn.HasKeyword(chewie, Keyword.HEROIC));
        scn.MoveCardsToLocation(site, han);
        assertTrue(scn.HasKeyword(chewie, Keyword.HEROIC));
    }

    @Test
    public void ChewieEnragedTakesFalconIntoHandFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var chewie = scn.GetLSCard("chewie");
        var falcon = scn.GetLSCard("falcon");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, chewie);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(falcon);
        assertTrue(scn.LSCardActionAvailable(chewie, "Take Kashyyyk or Falcon into hand from Reserve Deck"));
        scn.LSUseCardAction(chewie, "Take Kashyyyk or Falcon into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(falcon);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, falcon.getZone());
    }

    @Test
    public void ChewieEnragedMayBeForfeitedToSatisfyAllRemainingAttritionAndBattleDamage() {
        var scn = GetScenario();
        var chewie = scn.GetLSCard("chewie");
        var han = scn.GetLSCard("han");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var stormtrooper2 = scn.GetDSCard("stormtrooper2");
        var stormtrooper3 = scn.GetDSCard("stormtrooper3");
        var stormtrooper4 = scn.GetDSCard("stormtrooper4");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, chewie, han, stormtrooper, stormtrooper2, stormtrooper3, stormtrooper4, vader);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.PrepareDSDestiny(6);
        scn.PrepareLSDestiny(1);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        scn.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        scn.DSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("Just drew");
        scn.PassResponses("Just drew");
        scn.PassAllResponses();
        scn.PassResponses("COMPLETE_DESTINY_DRAW");
        scn.PassResponses("DRAWING_DESTINY_COMPLETE");
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        scn.LSChooseNo();
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        scn.PassDamageSegmentActions();
        assertTrue(scn.LSCardActionAvailable(chewie, "Forfeit to satisfy"));
        scn.LSUseCardAction(chewie, "Forfeit to satisfy");
        scn.PassAllResponses();
        assertTrue(chewie.getZone() == Zone.LOST_PILE || chewie.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
