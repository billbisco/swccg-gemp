package com.gempukku.swccgo.cards.set1107.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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
import static org.junit.Assert.assertTrue;

public class Card_1107_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("zuckuss", "1107_22");
                    put("mist", "4_170");
                    put("tatooine", "1_289");
                    put("fourlom", "4_91");
                    put("vigo", "10_53");
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
    public void ZuckussStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("zuckuss").getBlueprint();

        assertEquals("Zuckuss", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BOUNTY_HUNTER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.ZUCKUSS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.DAGOBAH);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_7, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ZuckussAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var zuckuss = scn.GetDSCard("zuckuss");
        var mist = scn.GetDSCard("mist");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, mist);
        scn.BoardAsPilot(mist, zuckuss);
        assertEquals(4, scn.GetPower(mist));
    }

    @Test
    public void ZuckussMakesMistHunterManeuverPlusOne() {
        var scn = GetScenario();
        var zuckuss = scn.GetDSCard("zuckuss");
        var mist = scn.GetDSCard("mist");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, mist);
        scn.BoardAsPilot(mist, zuckuss);
        assertEquals(4, scn.GetManeuver(mist));
    }

    @Test
    public void ZuckussIsImmuneToAttritionLessThanThree() {
        var scn = GetScenario();
        var zuckuss = scn.GetDSCard("zuckuss");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, zuckuss);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), zuckuss), scn.epsilon);
    }

    @Test
    public void ZuckussDeploysNonLightsaber4LOMHereOncePerGame() {
        var scn = GetScenario();
        var zuckuss = scn.GetDSCard("zuckuss");
        var fourlom = scn.GetDSCard("fourlom");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, zuckuss);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(fourlom);
        assertTrue(scn.DSCardActionAvailable(zuckuss, "Deploy 4-LOM from Reserve Deck"));
        scn.DSUseCardAction(zuckuss, "Deploy 4-LOM from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(fourlom);
        scn.PassAllResponses();
        assertEquals(site, fourlom.getAtLocation());
    }
}
