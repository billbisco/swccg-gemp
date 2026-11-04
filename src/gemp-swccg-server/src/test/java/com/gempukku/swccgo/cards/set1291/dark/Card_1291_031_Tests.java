package com.gempukku.swccgo.cards.set1291.dark;

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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_031_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("xwing", "1_146");
                    put("acclamator", "200_59");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("fanblade", "1291_31");
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
    public void FanbladeStarfighterStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("fanblade").getBlueprint();

        assertEquals("Fanblade Starfighter", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(5, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getHyperspeed(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        assertEquals(1, card.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.GINIVEX_CLASS_STARFIGHTER);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.NAV_COMPUTER);
            add(Icon.PILOT);
            add(Icon.SEPARATIST);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FanbladeStarfighterPermanentPilotProvidesAbilityOfTwoAndIsSuspendedByAddedPilot() {
        var scn = GetScenario();
        var fanblade = scn.GetDSCard("fanblade");
        var vader = scn.GetDSCard("vader");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, fanblade);
        assertEquals(2, scn.GetDSAbilityAtLocation(tatooine));
        scn.BoardAsPilot(fanblade, vader);
        assertEquals(6, scn.GetDSAbilityAtLocation(tatooine));
    }

    @Test
    public void FanbladeStarfighterForfeitPlusTwoWhileWithACloneArmyCard() {
        var scn = GetScenario();
        var fanblade = scn.GetDSCard("fanblade");
        var xwing = scn.GetLSCard("xwing");
        var acclamator = scn.GetLSCard("acclamator");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, fanblade, xwing);
        assertEquals(3, scn.GetForfeit(fanblade));
        scn.MoveCardsToLocation(tatooine, acclamator);
        assertEquals(5, scn.GetForfeit(fanblade));
    }

    @Test
    public void FanbladeStarfighterPowerPlusTwoAndAddsBattleDestinyWhenAttacking() {
        var scn = GetScenario();
        var fanblade = scn.GetDSCard("fanblade");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, fanblade, xwing);
        assertEquals(5, scn.GetPower(fanblade));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(tatooine);
        scn.PassAllResponses();
        assertEquals(5, scn.GetPower(fanblade));
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void FanbladeStarfighterNoBonusWhenDefending() {
        var scn = GetScenario();
        var fanblade = scn.GetDSCard("fanblade");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, fanblade, xwing);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        scn.PassAllResponses();
        assertEquals(3, scn.GetPower(fanblade));
        assertEquals(0, scn.GetDSBattleDestinyCount());
    }
}
