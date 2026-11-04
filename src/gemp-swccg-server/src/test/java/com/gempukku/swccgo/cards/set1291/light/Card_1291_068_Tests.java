package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_068_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("acclamator", "1291_68");
                    put("acclamator2", "1291_68");
                    put("xwing", "1_146");
                    put("xwing2", "1_146");
                    put("missiles", "9_87");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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
    public void AcclamatorClassAssaultShipStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("acclamator").getBlueprint();

        assertEquals("Acclamator-Class Assault Ship", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(5, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getHyperspeed(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(4, card.getPilotCapacity());
        assertEquals(4, card.getPassengerCapacity());
        assertEquals(4, card.getVehicleCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.CAPITAL, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.ACCLAMATOR_CLASS_ASSAULT_SHIP);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SHIP_DOCKING_CAPABILITY);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.PILOT);
            add(Icon.NAV_COMPUTER);
            add(Icon.CLONE_ARMY);
            add(Icon.REPUBLIC);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AcclamatorClassAssaultShipPermanentPilotProvidesAbilityOfTwo() {
        var scn = GetScenario();
        var acclamator = scn.GetLSCard("acclamator");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, acclamator);
        assertEquals(2, scn.GetLSAbilityAtLocation(tatooine));
    }

    @Test
    public void AcclamatorClassAssaultShipAddsOneAttritionForItselfAsAPilotedRepublicStarship() {
        var scn = GetScenario();
        var acclamator = scn.GetLSCard("acclamator");
        var xwing = scn.GetLSCard("xwing");
        var xwing2 = scn.GetLSCard("xwing2");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        // total ability 4 so that Light Side draws battle destiny
        scn.MoveCardsToLocation(tatooine, acclamator, xwing, xwing2, tie);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(0);
        scn.SkipToDamageSegment(true);
        // one piloted [Republic] starship here (the X-wings are not [Republic])
        assertEquals(1, scn.GetUnpaidDSAttrition());
    }

    @Test
    public void AcclamatorClassAssaultShipAddsOneAttritionForEachPilotedRepublicStarshipHere() {
        var scn = GetScenario();
        var acclamator = scn.GetLSCard("acclamator");
        var acclamator2 = scn.GetLSCard("acclamator2");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, acclamator, acclamator2, tie);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        scn.PrepareLSDestiny(0);
        scn.PrepareDSDestiny(0);
        scn.SkipToDamageSegment(true);
        // two piloted [Republic] starships here; the second copy's identical modifier is not cumulative
        assertEquals(2, scn.GetUnpaidDSAttrition());
    }

    @Test
    public void AcclamatorClassAssaultShipConcussionMissilesMayDeployAboard() {
        var scn = GetScenario();
        var acclamator = scn.GetLSCard("acclamator");
        var missiles = scn.GetLSCard("missiles");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, acclamator);
        scn.MoveCardsToLSHand(missiles);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(missiles));
        scn.LSDeployCard(missiles);
        if (scn.LSHasCardChoiceAvailable(acclamator)) {
            scn.LSChooseCard(acclamator);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(acclamator, missiles));
    }
}
