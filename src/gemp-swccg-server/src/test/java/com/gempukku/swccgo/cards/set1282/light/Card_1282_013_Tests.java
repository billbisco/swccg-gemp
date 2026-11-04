package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
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

public class Card_1282_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("villie", "1282_13");
                    put("devaronian", "6_10"); // non-unique Devaronian
                    put("devaronian2", "6_10");
                    put("chamber", "6_81"); // Jabba's Palace: Audience Chamber
                    put("cantina", "1_128"); // Tatooine: Cantina
                    put("mig", "1210_143"); // Yerka Mig (V): lost if opponent controls its location
                    put("xwing", "1_146");
                    put("tatooine", "1_127"); // Tatooine system
                }},
                new HashMap<>() {{
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

    protected VirtualTableScenario GetObjectiveScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("villie", "1282_13");
                    put("devaronian", "6_10");
                    put("agents", "112_1"); // Agents In The Court (Objective)
                    put("cantina", "1_128");
                }},
                new HashMap<>() {{
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
    public void VilmarhStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("villie").getBlueprint();

        assertEquals("Vilmarh 'Villie' Grahrk", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals("Devaronian gambler. Smuggler.", card.getLore());
        assertEquals(Species.DEVARONIAN, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GAMBLER);
            add(Keyword.SMUGGLER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void VilmarhAdds2ToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var villie = scn.GetLSCard("villie");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        int unpiloted = scn.GetPower(xwing);
        scn.BoardAsPilot(xwing, villie);
        assertEquals(unpiloted + 2, scn.GetPower(xwing));
    }

    @Test
    public void VilmarhPresentAtAudienceChamberBoostsNonUniqueDevaronians() {
        var scn = GetScenario();
        var villie = scn.GetLSCard("villie");
        var devaronian = scn.GetLSCard("devaronian");
        var devaronian2 = scn.GetLSCard("devaronian2");
        var chamber = scn.GetLSCard("chamber");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        scn.MoveCardsToLocation(site, devaronian);
        scn.MoveCardsToLSHand(devaronian2);
        int power = scn.GetPower(devaronian);
        int forfeit = scn.GetForfeit(devaronian);
        int deploy = scn.GetDeployCost(devaronian2);

        // Not at Audience Chamber: no bonus
        scn.MoveCardsToLocation(site, villie);
        assertEquals(power, scn.GetPower(devaronian));
        assertEquals(forfeit, scn.GetForfeit(devaronian));
        assertEquals(deploy, scn.GetDeployCost(devaronian2));

        scn.MoveCardsToLocation(chamber, villie);
        assertEquals(power + 1, scn.GetPower(devaronian));
        assertEquals(forfeit + 1, scn.GetForfeit(devaronian));
        assertEquals(deploy - 1, scn.GetDeployCost(devaronian2));
        // Vilmarh is unique: not boosted by his own text
        assertEquals(3, scn.GetPower(villie));
    }

    @Test
    public void VilmarhStackedOnYourObjectiveBoostsNonUniqueDevaronians() {
        var scn = GetObjectiveScenario();
        var villie = scn.GetLSCard("villie");
        var devaronian = scn.GetLSCard("devaronian");
        var agents = scn.GetLSCard("agents");
        var site = scn.GetLSCard("cantina");

        scn.StartGame();
        scn.MoveLocationToTable(site);
        scn.MoveCardsToLSSideOfTable(agents);
        scn.MoveCardsToLocation(site, devaronian);
        int power = scn.GetPower(devaronian);
        int forfeit = scn.GetForfeit(devaronian);

        scn.StackCardsOn(agents, villie);
        assertEquals(power + 1, scn.GetPower(devaronian));
        assertEquals(forfeit + 1, scn.GetForfeit(devaronian));
    }

    @Test
    public void VilmarhMayPlaceYerkaMigInUsedPileInsteadOfLost() {
        var scn = GetScenario();
        var villie = scn.GetLSCard("villie");
        var chamber = scn.GetLSCard("chamber");
        var cantina = scn.GetLSCard("cantina");
        var mig = scn.GetLSCard("mig");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(cantina, mig);
        scn.MoveCardsToLocation(chamber, villie);
        scn.MoveCardsToDSHand(trooper);
        scn.SkipToDSTurn(Phase.DEPLOY);

        // Opponent takes control of the Cantina: Yerka Mig (V) is about to be lost
        scn.DSDeployCard(trooper);
        scn.DSChooseCard(cantina);
        scn.PassResponses();

        assertTrue(scn.LSCardActionAvailable(villie, "Used Pile"));
        scn.LSUseCardAction(villie, "Used Pile");
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, mig);
    }

    @Test
    public void VilmarhAwayFromAudienceChamberDoesNotSaveYerkaMig() {
        var scn = GetScenario();
        var villie = scn.GetLSCard("villie");
        var cantina = scn.GetLSCard("cantina");
        var mig = scn.GetLSCard("mig");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(cantina, mig);
        scn.MoveCardsToLocation(site, villie);
        scn.MoveCardsToDSHand(trooper);
        scn.SkipToDSTurn(Phase.DEPLOY);

        scn.DSDeployCard(trooper);
        scn.DSChooseCard(cantina);
        scn.PassResponses();

        assertFalse(scn.LSCardActionAvailable(villie, "Used Pile"));
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, mig);
    }
}
