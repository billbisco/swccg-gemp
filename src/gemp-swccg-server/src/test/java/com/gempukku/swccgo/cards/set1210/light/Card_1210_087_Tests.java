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
import static org.junit.Assert.assertTrue;

public class Card_1210_087_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("beru", "1210_87");
                    put("luke", "1_19");
                    put("farm", "1_132");
                    put("hydro", "1210_117");
                    put("crop", "1_3");
                    put("stew", "1_072");
                    put("camie", "7_009");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
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
    public void BeruLarsStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("beru").getBlueprint();

        assertEquals("Beru Lars", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BeruLarsDrawsTopCardOfReserveDeckWhenPlayingBeruStew() {
        var scn = GetScenario();
        var beru = scn.GetLSCard("beru");
        var stew = scn.GetLSCard("stew");
        var camie = scn.GetLSCard("camie");
        var farm = scn.GetLSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, beru);
        scn.MoveCardsToLSHand(stew);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(camie);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSFiller(1));
        assertTrue(scn.LSCardPlayAvailable(stew));
        scn.LSPlayCard(stew);
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(beru, "Draw top card of Reserve Deck"));
        scn.LSUseCardAction(beru, "Draw top card of Reserve Deck");
        scn.PassAllResponses();
        assertEquals(Zone.HAND, camie.getZone());
    }

    @Test
    public void BeruLarsPlacesCropInUsedPileWhenDestinyIsWithinOne() {
        var scn = GetScenario();
        var beru = scn.GetLSCard("beru");
        var luke = scn.GetLSCard("luke");
        var hydro = scn.GetLSCard("hydro");
        var crop = scn.GetLSCard("crop");
        var farm = scn.GetLSCard("farm");
        var stormtrooper = scn.GetDSCard("stormtrooper");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, beru, luke, stormtrooper);
        scn.AttachCardsTo(farm, hydro);
        scn.StackCardsOn(hydro, crop);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(farm);
        scn.PrepareLSDestiny(2);
        scn.PrepareDSDestiny(0);
        scn.PassWeaponsSegmentActions();
        scn.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.DSPass();
        assertTrue(scn.LSCardActionAvailable(beru, "Place 'crop' in Used Pile"));
        scn.LSUseCardAction(beru, "Place 'crop' in Used Pile");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        assertTrue(crop.getZone() == Zone.USED_PILE || crop.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
