package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1104_040_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("occupation", "1104_40");
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
    public void ImperialOccupationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("occupation").getBlueprint();

        assertEquals("Imperial Occupation", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.OBJECTIVE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private VirtualTableScenario GarrisonScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("occupation", "1104_40");
                    put("garrison", "222_17");
                    put("cannon", "3_158");
                }},
                30, 30,
                StartingSetup.DefaultLSGroundLocation,
                OccupationIsTheStartingCard,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    /**
     * Imperial Occupation in the deck is tried as the starting card; its pregame deploys find nothing (verify the
     * Reserve Deck) and it leaves play. The tests put it on table themselves.
     */
    private static final StartingSetup OccupationIsTheStartingCard = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>();
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            for (int i = 0; i < 20 && scn.GetCurrentDecision() != null
                    && scn.GetCurrentDecision().getText().startsWith("Verify "); i++)
                scn.PlayerPass(scn.GetDecidingPlayer());
        }
    };

    private void PlayWalkerGarrisonToRetrieveACannon(VirtualTableScenario scn, boolean flipped) {
        var occupation = scn.GetDSCard("occupation");
        var garrison = scn.GetDSCard("garrison");
        var cannon = scn.GetDSCard("cannon");
        scn.StartGame();
        occupation.setObjectiveDeploymentComplete(true);
        scn.MoveCardsToDSSideOfTable(occupation);
        if (flipped)
            scn.gameState().flipCard(scn.game(), occupation, true);
        assertEquals(flipped, occupation.isFlipped());
        scn.MoveCardsToDSHand(garrison);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToTopOfDSLostPile(cannon);
        assertEquals(flipped ? CardSubtype.OUT_OF_PLAY : CardSubtype.LOST,
                scn.game().getModifiersQuerying().getInterruptType(scn.gameState(), garrison));
        assertTrue(scn.DSCardPlayAvailable(garrison, "Retrieve AT-AT Can"));
        scn.DSPlayCard(garrison, "Retrieve AT-AT Can");
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(cannon))
            scn.DSChooseCard(cannon);
        scn.PassAllResponses();
        assertFalse(scn.GetDSLostPile().contains(cannon));
    }

    @Test
    public void ImperialControlMakesWalkerGarrisonAnOutOfPlayInterrupt() {
        // Back side: "Walker Garrison is an Out Of Play Interrupt": placed out of play when completed, not lost.
        var scn = GarrisonScenario();
        var garrison = scn.GetDSCard("garrison");
        PlayWalkerGarrisonToRetrieveACannon(scn, true);
        assertEquals(Zone.OUT_OF_PLAY, garrison.getZone());
    }

    @Test
    public void ImperialOccupationFrontLeavesWalkerGarrisonALostInterrupt() {
        var scn = GarrisonScenario();
        var garrison = scn.GetDSCard("garrison");
        PlayWalkerGarrisonToRetrieveACannon(scn, false);
        assertTrue(scn.GetDSLostPile().contains(garrison));
    }
}
