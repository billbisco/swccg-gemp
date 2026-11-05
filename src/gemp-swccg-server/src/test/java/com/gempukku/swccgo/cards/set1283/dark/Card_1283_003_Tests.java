package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("valarian", "1283_3");
                    put("bib", "13_55"); // "While with Jabba, power +3"
                    put("boba", "108_5"); // "Adds one battle destiny if with Han or Jabba"
                    put("jabba", "6_109");
                    put("no_bargain", "1115_56"); // non-[Lightsaber] alien females are Mistryl
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

    @Test
    public void LadyValarianStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("valarian").getBlueprint();
        assertEquals("Lady Valarian", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(Species.WHIPHID, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FEMALE);
            add(Keyword.GANGSTER);
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.JABBAS_PALACE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LadyValarianAddsOneToYourTotalBattleDestinyHere() {
        //test1: your total battle destiny here is +1
        var scn = GetScenario();
        var valarian = scn.GetDSCard("valarian");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        // Stormtrooper brings total ability to 4 so Dark Side draws battle destiny
        scn.MoveCardsToLocation(site, valarian, trooper, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PrepareDSDestiny(3);
        scn.PrepareLSDestiny(1);
        scn.SkipToDamageSegment(true);

        // Dark Side total battle destiny 3 +1 is the attrition against Light Side
        assertEquals(4, scn.gameState().getBattleState().getAttritionTotal(scn.game(), scn.LS), scn.epsilon); //test1
    }

    @Test
    public void LadyValarianMayBeJabbaForBibWhenJabbaNotOnTable() {
        //test1: Bib's "While with Jabba, power +3" works with Lady Valarian
        //test2: not once Jabba is on table
        //test3: Bib is still pumped by Jabba himself
        var scn = GetScenario();
        var valarian = scn.GetDSCard("valarian");
        var bib = scn.GetDSCard("bib");
        var jabba = scn.GetDSCard("jabba");
        var dsSite = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dsSite, bib);
        float basePower = scn.GetPower(bib);
        scn.MoveCardsToLocation(dsSite, valarian);
        assertEquals(basePower + 3, scn.GetPower(bib), scn.epsilon); //test1

        scn.MoveCardsToLocation(lsSite, jabba);
        assertEquals(basePower, scn.GetPower(bib), scn.epsilon); //test2

        scn.MoveCardsToLocation(dsSite, jabba);
        assertEquals(basePower + 3, scn.GetPower(bib), scn.epsilon); //test3
    }

    @Test
    public void LadyValarianMayBeJabbaForBobaFettBattleDestiny() {
        //test1: Boba Fett With Blaster Rifle adds one battle destiny when with Lady Valarian
        //test2: not while Jabba is on table elsewhere
        var scn = GetScenario();
        var valarian = scn.GetDSCard("valarian");
        var boba = scn.GetDSCard("boba");
        var jabba = scn.GetDSCard("jabba");
        var luke = scn.GetLSCard("luke");
        var dsSite = scn.GetDSStartingLocation();
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(dsSite, valarian, boba, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(dsSite);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertEquals(2, scn.GetDSBattleDestinyCount()); //test1

        var scn2 = GetScenario();
        valarian = scn2.GetDSCard("valarian");
        boba = scn2.GetDSCard("boba");
        jabba = scn2.GetDSCard("jabba");
        luke = scn2.GetLSCard("luke");
        dsSite = scn2.GetDSStartingLocation();
        lsSite = scn2.GetLSStartingLocation();

        scn2.StartGame();
        scn2.MoveCardsToLocation(dsSite, valarian, boba, luke);
        scn2.MoveCardsToLocation(lsSite, jabba);
        scn2.SkipToDSTurn(Phase.BATTLE);
        scn2.DSInitiateBattle(dsSite);
        scn2.PassAllResponses();
        scn2.SkipToPowerSegment();
        assertEquals(1, scn2.GetDSBattleDestinyCount()); //test2
    }

    @Test
    public void LadyValarianMakesMistrylHereHeroicOncePerTurn() {
        //test1: may target a Mistryl of ability > 2 here
        //test2: target is Heroic until end of turn
        //test3: once per turn
        var scn = GetScenario();
        var valarian = scn.GetDSCard("valarian");
        var noBargain = scn.GetDSCard("no_bargain");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, valarian);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(valarian, "Heroic"));

        scn.MoveCardsToDSSideOfTable(noBargain);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.HasKeyword(valarian, Keyword.MISTRYL));
        assertFalse(scn.HasKeyword(valarian, Keyword.HEROIC));
        assertTrue(scn.DSCardActionAvailable(valarian, "Heroic")); //test1
        scn.DSUseCardAction(valarian, "Heroic");
        if (scn.DSHasCardChoiceAvailable(valarian)) {
            scn.DSChooseCard(valarian);
        }
        scn.PassAllResponses();
        assertTrue(scn.HasKeyword(valarian, Keyword.HEROIC)); //test2
        if (scn.LSGetDecision() != null) {
            scn.LSPass();
        }
        assertFalse(scn.DSCardActionAvailable(valarian, "Heroic")); //test3

        scn.SkipToLSTurn();
        assertFalse(scn.HasKeyword(valarian, Keyword.HEROIC)); //test2
    }
}
