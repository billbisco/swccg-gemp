package com.gempukku.swccgo.cards.set1291.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_057_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("fcc", "1291_57");
                    put("yoda", "12_35");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
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
    public void GeonosisForwardCommandCenterStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("fcc").getBlueprint();

        assertEquals("Geonosis: Forward Command Center", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.CORUSCANT);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(0, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }


    @Test
    public void GeonosisForwardCommandCenterEpisodeIYodaIgnoresLocationDeploymentRestrictionsHere() {
        var scn = GetScenario();
        var fcc = scn.GetLSCard("fcc");
        var yoda = scn.GetLSCard("yoda");

        scn.StartGame();
        scn.MoveLocationToTable(fcc);
        scn.MoveCardsToHand(yoda);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(yoda));
        scn.LSDeployCardAndPassResponses(yoda, fcc);
        assertAtLocation(fcc, yoda);
    }

    @Test
    public void GeonosisForwardCommandCenterYodaStillRestrictedWithoutIt() {
        var scn = GetScenario();
        var yoda = scn.GetLSCard("yoda");

        scn.StartGame();
        scn.MoveCardsToHand(yoda);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSDeployAvailable(yoda));
    }

    @Test
    public void GeonosisForwardCommandCenterOncePerGameMayDeployYodaHereFromReserveDeck() {
        var scn = GetScenario();
        var fcc = scn.GetLSCard("fcc");
        var yoda = scn.GetLSCard("yoda");

        scn.StartGame();
        scn.MoveLocationToTable(fcc);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(yoda);
        assertTrue(scn.LSCardActionAvailable(fcc, "Deploy Yoda here from Reserve Deck"));
        scn.LSUseCardAction(fcc, "Deploy Yoda here from Reserve Deck");
        scn.LSChooseCard(yoda);
        scn.PassAllResponses();

        assertAtLocation(fcc, yoda);

        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(fcc, "Deploy Yoda here from Reserve Deck"));
    }

    @Test
    public void GeonosisForwardCommandCenterDarkControlCancelsLightGameText() {
        var scn = GetScenario();
        var fcc = scn.GetLSCard("fcc");
        var yoda = scn.GetLSCard("yoda");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(fcc);
        scn.MoveCardsToLocation(fcc, vader);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(yoda);

        assertTrue(scn.game().getModifiersQuerying().getCardsMarkingGameTextCanceledForPlayer(scn.gameState(), fcc, scn.LS).contains(fcc));
        assertFalse(scn.LSCardActionAvailable(fcc, "Deploy Yoda here from Reserve Deck"));
    }

    @Test
    public void GeonosisForwardCommandCenterLightGameTextNotCanceledWhenDarkDoesNotControl() {
        var scn = GetScenario();
        var fcc = scn.GetLSCard("fcc");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(fcc);
        scn.MoveCardsToLocation(fcc, vader, luke);

        assertTrue(scn.game().getModifiersQuerying().getCardsMarkingGameTextCanceledForPlayer(scn.gameState(), fcc, scn.LS).isEmpty());
    }
}
