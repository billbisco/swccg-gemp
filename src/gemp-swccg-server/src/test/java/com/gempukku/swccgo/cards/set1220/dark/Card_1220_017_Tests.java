package com.gempukku.swccgo.cards.set1220.dark;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("veers", "1220_17");
                    put("blizzard", "3_154");
                    put("hoth", "3_143");
                    put("marker", "104_4");
                    put("tatooine", "1_289");
                    put("devastator", "1_301");
                    put("plains", "3_148");
                    put("ridge", "3_149");
                    put("walker", "3_157");
                    put("walker2", "3_157");
                    put("walker3", "3_157");
                    put("trooper", "1_180");
                    put("trooper2", "1_180");
                    put("tonnika", "1_195");
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
    public void VeersStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("veers").getBlueprint();

        assertEquals("Veers", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.VEERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.PREMIUM);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void VeersGameTextIsOfficialText() {
        var scn = GetScenario();
        assertEquals("Adds 3 to power of anything he pilots. Deploys -2 to Hoth. Your combat vehicles are defense value +1 at same and related locations. While on Hoth, your AT-ATs and Imperials are deploy -1 and forfeit +1 at related locations.",
                scn.GetDSCard("veers").getBlueprint().getGameText());
    }

    @Test
    public void VeersAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var veers = scn.GetDSCard("veers");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, veers);
        assertEquals(12, scn.GetPower(devastator));
    }

    @Test
    public void VeersDeploysMinusTwoToHothOnly() {
        var scn = GetScenario();
        var veers = scn.GetDSCard("veers");
        var plains = scn.GetDSCard("plains");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(plains);
        assertEquals(1, deployCostTo(scn, veers, plains), scn.epsilon);
        assertEquals(3, deployCostTo(scn, veers, site), scn.epsilon);
    }

    @Test
    public void VeersNoLongerDeploysFreeAboardBlizzard1() {
        var scn = GetScenario();
        var veers = scn.GetDSCard("veers");
        var blizzard = scn.GetDSCard("blizzard");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard);
        assertEquals(3, deployCostTo(scn, veers, blizzard), scn.epsilon);
    }

    @Test
    public void VeersHasNoRevealActionFromHand() {
        var scn = GetScenario();
        var veers = scn.GetDSCard("veers");
        var hoth = scn.GetDSCard("hoth");
        var marker = scn.GetDSCard("marker");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToDSHand(veers);
        scn.MoveCardsToTopOfDSReserveDeck(marker);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(veers, "Reveal to deploy 6th Marker or Blizzard 1"));
    }

    @Test
    public void VeersCombatVehiclesAreDefenseValuePlusOneAtSameAndRelatedLocations() {
        var scn = GetScenario();
        var veers = scn.GetDSCard("veers");
        var plains = scn.GetDSCard("plains");
        var ridge = scn.GetDSCard("ridge");
        var walker = scn.GetDSCard("walker");
        var walker2 = scn.GetDSCard("walker2");
        var walker3 = scn.GetDSCard("walker3");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(plains);
        scn.MoveLocationToTable(ridge);
        scn.MoveCardsToLocation(site, walker3);
        int base = scn.GetDefense(walker3);
        scn.MoveCardsToLocation(plains, veers, walker);
        scn.MoveCardsToLocation(ridge, walker2);
        assertEquals(base + 1, scn.GetDefense(walker));
        assertEquals(base + 1, scn.GetDefense(walker2));
        assertEquals(base, scn.GetDefense(walker3));
    }

    @Test
    public void VeersOnHothMakesAtAtsAndImperialsDeployMinusOneAndForfeitPlusOneAtRelatedLocations() {
        var scn = GetScenario();
        var veers = scn.GetDSCard("veers");
        var plains = scn.GetDSCard("plains");
        var ridge = scn.GetDSCard("ridge");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var walker = scn.GetDSCard("walker");
        var walker2 = scn.GetDSCard("walker2");
        var tonnika = scn.GetDSCard("tonnika");

        scn.StartGame();
        scn.MoveLocationToTable(plains);
        scn.MoveLocationToTable(ridge);
        assertEquals(2, deployCostTo(scn, trooper, ridge), scn.epsilon);
        scn.MoveCardsToLocation(plains, veers);
        // deploy -1 to related locations only
        assertEquals(1, deployCostTo(scn, trooper, ridge), scn.epsilon);
        assertEquals(2, deployCostTo(scn, trooper, plains), scn.epsilon);
        assertEquals(5, deployCostTo(scn, walker, ridge), scn.epsilon);
        assertEquals(6, deployCostTo(scn, walker, plains), scn.epsilon);
        assertEquals(2, deployCostTo(scn, tonnika, ridge), scn.epsilon);

        // forfeit +1 at related locations only
        scn.MoveCardsToLocation(ridge, trooper, walker, tonnika);
        scn.MoveCardsToLocation(plains, trooper2, walker2);
        assertEquals(3, scn.GetForfeit(trooper));
        assertEquals(2, scn.GetForfeit(trooper2));
        assertEquals(7, scn.GetForfeit(walker));
        assertEquals(6, scn.GetForfeit(walker2));
        assertEquals(2, scn.GetForfeit(tonnika));
    }

    @Test
    public void VeersNotOnHothGivesNoDeployOrForfeitBonus() {
        var scn = GetScenario();
        var veers = scn.GetDSCard("veers");
        var plains = scn.GetDSCard("plains");
        var ridge = scn.GetDSCard("ridge");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(plains);
        scn.MoveLocationToTable(ridge);
        scn.MoveCardsToLocation(site, veers);
        assertEquals(2, deployCostTo(scn, trooper, ridge), scn.epsilon);
        scn.MoveCardsToLocation(ridge, trooper);
        assertEquals(2, scn.GetForfeit(trooper));
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
