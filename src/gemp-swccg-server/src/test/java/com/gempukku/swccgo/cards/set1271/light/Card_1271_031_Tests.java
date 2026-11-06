package com.gempukku.swccgo.cards.set1271.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

public class Card_1271_031_Tests {
    private VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("biggs", "1271_31"); put("original", "1_3"); put("red3", "1_145");
            put("ywing", "1_147"); put("corvette", "1_140"); put("hobbie", "3_5"); put("take", "1_19");
            put("ground", "5_79"); put("camie", "7_9"); put("fixer", "1115_10"); put("tatooine", "12_85");
        }}, new HashMap<>() {{ put("isd", "1_302"); }}, 10, 10,
                StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void BiggsRogueLegendBlocksCamieSearchWhenEveryNamedPersonaIsAlreadyInPlay() {
        //test1: Camie cannot search when Luke, Fixer, and this differently titled Biggs are all already on table.
        // Fixer has no implemented original printing, so the OVS printing supplies his unchanged identity.
        var scn = GetScenario(); var site = scn.GetLSCard("tatooine"); var camie = scn.GetLSCard("camie");
        scn.StartGame(); scn.MoveLocationToTable(site);
        scn.MoveCardsToLocation(site, camie, scn.GetLSCard("take"), scn.GetLSCard("fixer"), scn.GetLSCard("biggs"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(camie, "Deploy card from Reserve Deck")); //test1
    }

    @Test
    public void BiggsRogueLegendStatsAndKeywordsAreCorrect() {
        var scn = GetScenario(); var physical = scn.GetLSCard("biggs"); var card = physical.getBlueprint();
        assertEquals("Biggs, Rogue Legend", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(1, scn.GetLandspeed(physical));
        assertEquals(CardCategory.CHARACTER, card.getCardCategory());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.REBEL); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{ add(Icon.REBEL); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.LEGACY_BLOCK_7); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.RED_SQUADRON); add(Keyword.ROGUE_SQUADRON); }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{ add(Persona.BIGGS); }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BiggsRogueLegendAddsThreePowerAndOneManeuverToRedThree() {
        //test1: Red 3's printed power3/maneuver4 become6/5 with its matching pilot.
        var scn = GetScenario(); var ship = scn.GetLSCard("red3");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), ship);
        scn.BoardAsPilot(ship, scn.GetLSCard("biggs"));
        assertEquals(6, scn.GetPower(ship)); //test1
        assertEquals(5, scn.GetManeuver(ship)); //test1
    }

    @Test
    public void BiggsRogueLegendAddsOnlyTwoPowerToAnotherShip() {
        //test1: A nonmatching Y-wing gains2 power and no maneuver bonus.
        var scn = GetScenario(); var ship = scn.GetLSCard("ywing");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), ship);
        scn.BoardAsPilot(ship, scn.GetLSCard("biggs"));
        assertEquals(4, scn.GetPower(ship)); //test1
        assertEquals(3, scn.GetManeuver(ship)); //test1
    }

    @Test
    public void BiggsRogueLegendDrawsBattleDestinyWhenPilotingRedThreeWithoutEnoughAbility() {
        //test1: His2 ability is insufficient normally, but Red3's printed exception gives one draw.
        var scn = GetScenario(); var site = scn.GetDSStartingLocation(); var ship = scn.GetLSCard("red3");
        scn.StartGame(); scn.MoveCardsToLocation(site, ship, scn.GetDSCard("isd"));
        scn.BoardAsPilot(ship, scn.GetLSCard("biggs"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.LSInitiateBattle(site); scn.PassAllResponses();
        assertEquals(1, scn.GetLSBattleDestinyCount()); //test1
    }

    @Test
    public void BiggsRogueLegendDoesNotGrantBattleDestinyOnAnotherShip() {
        //test1: A Y-wing's permanent pilot1 plus Biggs2 cannot draw battle destiny.
        var scn = GetScenario(); var site = scn.GetDSStartingLocation(); var ship = scn.GetLSCard("ywing");
        scn.StartGame(); scn.MoveCardsToLocation(site, ship, scn.GetDSCard("isd"));
        scn.BoardAsPilot(ship, scn.GetLSCard("biggs"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.LSInitiateBattle(site); scn.PassAllResponses();
        assertEquals(0, scn.GetLSBattleDestinyCount()); //test1
    }

    @Test
    public void BiggsRogueLegendDoesNotAddAnExtraDrawWhenAlreadyAbleToDraw() {
        //test1: Another piloted ship supplies enough ability; the exception still gives only one draw.
        var scn = GetScenario(); var site = scn.GetDSStartingLocation(); var ship = scn.GetLSCard("red3");
        var corvette = scn.GetLSCard("corvette");
        scn.StartGame(); scn.MoveCardsToLocation(site, ship, corvette, scn.GetDSCard("isd"));
        scn.BoardAsPilot(ship, scn.GetLSCard("biggs")); scn.BoardAsPilot(corvette, scn.GetLSCard("hobbie"));
        scn.SkipToLSTurn(Phase.BATTLE); scn.EnsureLSForcePile(5); scn.LSInitiateBattle(site); scn.PassAllResponses();
        assertEquals(1, scn.GetLSBattleDestinyCount()); //test1
    }

    @Test
    public void BiggsRogueLegendTakesAForcePileCardInsteadOfDrainingAtOpponentsSystem() {
        //test1: An ordinary legal Force drain can be exchanged for a chosen Force Pile card.
        //test2: Taking it consumes that system's drain opportunity.
        var scn = GetScenario(); var site = scn.GetDSStartingLocation(); var ship = scn.GetLSCard("red3");
        var biggs = scn.GetLSCard("biggs"); var take = scn.GetLSCard("take");
        scn.StartGame(); scn.MoveCardsToLocation(site, ship); scn.BoardAsPilot(ship, biggs);
        scn.SkipToLSTurn(Phase.CONTROL); scn.MoveCardsToTopOfLSForcePile(take);
        assertTrue(scn.LSForceDrainAvailable(site)); //test1
        assertTrue(scn.LSCardActionAvailable(biggs, "Take card")); //test1
        scn.LSUseCardAction(biggs, "Take card"); scn.PassAllResponses();
        scn.ChooseCardBPFromSelection(scn.LS, take); scn.PassAllResponses();
        assertInZone(Zone.HAND, take); //test1
        if (scn.GetDecidingPlayer().equals(scn.DS)) scn.DSPass();
        assertFalse(scn.LSForceDrainAvailable(site)); //test2
        assertFalse(scn.LSCardActionAvailable(biggs, "Take card")); //test2
    }

    @Test
    public void BiggsRogueLegendCannotSearchAtOwnersSystem() {
        //test1: A legal drain at your own system does not qualify for his replacement.
        var scn = GetScenario(); var site = scn.GetLSStartingLocation(); var ship = scn.GetLSCard("red3");
        var biggs = scn.GetLSCard("biggs");
        scn.StartGame(); scn.MoveCardsToLocation(site, ship); scn.BoardAsPilot(ship, biggs);
        scn.SkipToLSTurn(Phase.CONTROL); scn.EnsureLSForcePile(5);
        assertTrue(scn.LSForceDrainAvailable(site)); //test1
        assertFalse(scn.LSCardActionAvailable(biggs, "Take card")); //test1
    }

    @Test
    public void BiggsRogueLegendCountsAsBiggsForOlderCards() {
        //test1: Hobbie's printed Biggs condition sees this differently titled version.
        var scn = GetScenario(); var hobbie = scn.GetLSCard("hobbie"); var biggs = scn.GetLSCard("biggs");
        scn.StartGame(); var ground = scn.GetLSCard("ground"); scn.MoveLocationToTable(ground);
        scn.MoveCardsToLocation(ground, hobbie);
        assertEquals(2, scn.GetPower(hobbie)); //test1
        scn.MoveCardsToLocation(ground, biggs);
        assertEquals(4, scn.GetPower(hobbie)); //test1
    }

    @Test
    public void BiggsRogueLegendCannotDeployAlongsideAnotherBiggsPersona() {
        //test1: The original Biggs and this version cannot coexist as separate unique characters.
        var scn = GetScenario(); var biggs = scn.GetLSCard("biggs");
        scn.StartGame(); var ground = scn.GetLSCard("ground"); scn.MoveLocationToTable(ground);
        scn.MoveCardsToLocation(ground, scn.GetLSCard("original"));
        scn.MoveCardsToLSHand(biggs); scn.SkipToLSTurn(Phase.DEPLOY); scn.EnsureLSForcePile(10);
        assertFalse(scn.LSDeployAvailable(biggs)); //test1
    }
}
