package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
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

public class Card_1281_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("figrin", "1_9");
                    put("trooper", "1_28");
                }},
                new HashMap<>() {{
                    put("casino", "1281_21");
                    put("coruscant", "7_275");
                    put("rep1", "1_194");
                    put("rep2", "1_195");
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
    public void CoruscantCasinoStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("casino").getBlueprint();

        assertEquals("Coruscant: Casino", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CoruscantCasinoAssassinsAndGamblersAreForfeitPlusOneHere() {
        var scn = GetScenario();
        var casino = scn.GetDSCard("casino");
        var coruscant = scn.GetDSCard("coruscant");
        var figrin = scn.GetLSCard("figrin");
        var trooper = scn.GetLSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(casino);
        var assassin = MissionTestSupport.putInReserve(scn, 98201, scn.DS, Vb81RestTestSupport.assassin("Test Assassin"));
        int assassinForfeit = assassin.getBlueprint().getForfeit().intValue();
        int figrinForfeit = figrin.getBlueprint().getForfeit().intValue();
        int trooperForfeit = trooper.getBlueprint().getForfeit().intValue();
        scn.MoveCardsToLocation(casino, assassin, figrin, trooper);

        assertEquals(assassinForfeit + 1, scn.GetForfeit(assassin));
        assertEquals(figrinForfeit + 1, scn.GetForfeit(figrin));
        assertEquals(trooperForfeit, scn.GetForfeit(trooper));
    }

    @Test
    public void CoruscantCasinoAssassinHerePlacesAReputationInUsedPileOncePerTurn() {
        var scn = GetScenario();
        var casino = scn.GetDSCard("casino");
        var coruscant = scn.GetDSCard("coruscant");
        var rep1 = scn.GetDSCard("rep1");
        var rep2 = scn.GetDSCard("rep2");

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(casino);
        var assassin = MissionTestSupport.putInReserve(scn, 98202, scn.DS, Vb81RestTestSupport.assassin("Test Assassin"));
        scn.MoveCardsToLocation(casino, assassin);
        scn.StackCardsFaceDownOn(assassin, rep1, rep2);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.DSCardActionAvailable(casino, "Place a 'reputation' in Used Pile"));
        scn.DSUseCardAction(casino, "Place a 'reputation' in Used Pile");
        if (scn.DSDecisionAvailable("Choose")) {
            scn.DSChooseCard(rep1);
        }
        scn.PassAllResponses();
        assertEquals(1, scn.gameState().getStackedCards(assassin).size());
        assertTrue(Vb81RestTestSupport.inZone(rep1, Zone.USED_PILE) || Vb81RestTestSupport.inZone(rep2, Zone.USED_PILE));
        assertFalse(Vb81RestTestSupport.dsCardActionAvailable(scn, casino, "Place a 'reputation' in Used Pile"));
    }

    @Test
    public void CoruscantCasinoNeedsAnAssassinWithReputationHere() {
        var scn = GetScenario();
        var casino = scn.GetDSCard("casino");
        var coruscant = scn.GetDSCard("coruscant");

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(casino);
        var assassin = MissionTestSupport.putInReserve(scn, 98203, scn.DS, Vb81RestTestSupport.assassin("Test Assassin"));
        scn.MoveCardsToLocation(casino, assassin);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertFalse(scn.DSCardActionAvailable(casino, "Place a 'reputation' in Used Pile"));
    }
}
