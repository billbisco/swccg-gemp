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

public class Card_1291_070_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("azure", "1291_70");
                    put("anakin", "200_2");
                    put("luke", "1_19");
                    put("r2x2", "1_24");
                    put("r2x2b", "1_24");
                    put("maneuvers", "1_70");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("sense", "1_267");
                    put("vader", "1_168");
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
    public void AzureAngelStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("azure").getBlueprint();

        assertEquals("Azure Angel", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        // printed sheet and 2013 master list: deploy 3 (the PC Legacy JSON says 4)
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getManeuver(), scn.epsilon);
        assertEquals(6, card.getHyperspeed(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        assertEquals(1, card.getAstromechCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.STARSHIP);
        }});
        assertEquals(CardSubtype.STARFIGHTER, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.MODIFIED_DELTA_7_INTERCEPTOR);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.STARSHIP);
            add(Icon.NAV_COMPUTER);
            add(Icon.CLONE_ARMY);
            add(Icon.SCOMP_LINK);
            add(Icon.EPISODE_I);
            add(Icon.REPUBLIC);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AzureAngelOncePerGameDeploysAnAstromechAboardFromReserveDeckForFree() {
        var scn = GetScenario();
        var azure = scn.GetLSCard("azure");
        var luke = scn.GetLSCard("luke");
        var r2x2 = scn.GetLSCard("r2x2");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, azure);
        scn.BoardAsPilot(azure, luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(r2x2);
        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(azure, "Deploy astromech"));
        scn.LSUseCardAction(azure, "Deploy astromech");
        scn.LSChooseCard(r2x2);
        scn.PassAllResponses();
        assertTrue(scn.IsAboard(azure, r2x2));
        assertEquals(forceBefore, scn.GetLSForcePileCount());
    }

    @Test
    public void AzureAngelAstromechDeployIsOncePerGame() {
        var scn = GetScenario();
        var azure = scn.GetLSCard("azure");
        var luke = scn.GetLSCard("luke");
        var r2x2 = scn.GetLSCard("r2x2");
        var r2x2b = scn.GetLSCard("r2x2b");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, azure);
        scn.BoardAsPilot(azure, luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(r2x2);
        scn.LSUseCardAction(azure, "Deploy astromech");
        scn.LSChooseCard(r2x2);
        scn.PassAllResponses();
        assertTrue(scn.IsAboard(azure, r2x2));
        scn.MoveCardsToLocation(tatooine, r2x2);
        scn.MoveCardsToLSHand(r2x2b);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(r2x2b);
        assertFalse(scn.LSCardActionAvailable(azure, "Deploy astromech"));
    }

    @Test
    public void AzureAngelYourInterruptsTargetingItMayNotBeCanceledWhileAnakinPiloting() {
        var scn = GetScenario();
        var azure = scn.GetLSCard("azure");
        var anakin = scn.GetLSCard("anakin");
        var maneuvers = scn.GetLSCard("maneuvers");
        var sense = scn.GetDSCard("sense");
        var vader = scn.GetDSCard("vader");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, azure);
        scn.BoardAsPilot(azure, anakin);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.MoveCardsToLSHand(maneuvers);
        scn.MoveCardsToDSHand(sense);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(maneuvers));
        scn.LSPlayCard(maneuvers);
        scn.LSChooseCard(azure);
        assertFalse(scn.DSCardPlayAvailable(sense));
    }

    @Test
    public void AzureAngelInterruptsTargetingItMayBeCanceledWithoutAnakinPiloting() {
        var scn = GetScenario();
        var azure = scn.GetLSCard("azure");
        var luke = scn.GetLSCard("luke");
        var maneuvers = scn.GetLSCard("maneuvers");
        var sense = scn.GetDSCard("sense");
        var vader = scn.GetDSCard("vader");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, azure);
        scn.BoardAsPilot(azure, luke);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.MoveCardsToLSHand(maneuvers);
        scn.MoveCardsToDSHand(sense);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(maneuvers);
        scn.LSChooseCard(azure);
        assertTrue(scn.DSCardPlayAvailable(sense));
    }

    @Test
    public void AzureAngelImmuneToAttritionLessThanFiveOnlyWhileAnakinPiloting() {
        var scn = GetScenario();
        var azure = scn.GetLSCard("azure");
        var anakin = scn.GetLSCard("anakin");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, azure);
        scn.BoardAsPilot(azure, luke);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), azure), scn.epsilon);
        scn.MoveCardsToLocation(tatooine, luke);
        scn.BoardAsPilot(azure, anakin);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), azure), scn.epsilon);
    }
}
