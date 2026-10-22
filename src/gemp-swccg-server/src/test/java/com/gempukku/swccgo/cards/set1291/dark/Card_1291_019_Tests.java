package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("momaw", "1_20");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("council", "1291_19");
                    put("gunray", "12_112");
                    put("aks", "12_97");
                    put("baskol", "12_98");
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
    public void SeparatistCouncilRoomStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("council").getBlueprint();

        assertEquals("Geonosis: Separatist Council Room", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.UNDERGROUND);
            add(Icon.PLANET);
            add(Icon.SCOMP_LINK);
            add(Icon.EPISODE_I);
            add(Icon.DARK_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(0, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SeparatistCouncilRoomDarkSideDeploysGunrayFromReserveDeck() {
        var scn = GetScenario();
        var council = scn.GetDSCard("council");
        var gunray = scn.GetDSCard("gunray");

        scn.StartGame();
        scn.MoveLocationToTable(council);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(gunray);
        scn.EnsureDSForcePile(5);

        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSCardActionAvailable(council, "Deploy Gunray from Reserve Deck"));
        scn.DSUseCardAction(council, "Deploy Gunray from Reserve Deck");
        scn.PassAllResponses();
        assertTrue(scn.DSHasCardChoiceAvailable(gunray));
        scn.DSChooseCard(gunray);
        assertTrue(scn.DSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.DSHasCardChoiceAvailable(council));
        scn.DSChooseCard(council);
        scn.PassAllResponses();

        assertAtLocation(council, gunray);
    }

    @Test
    public void SeparatistCouncilRoomTwoSenatorsHereAreInASenateMajority() {
        var scn = GetScenario();
        var council = scn.GetDSCard("council");
        var aks = scn.GetDSCard("aks");
        var baskol = scn.GetDSCard("baskol");

        scn.StartGame();
        scn.MoveLocationToTable(council);
        scn.MoveCardsToLocation(council, aks, baskol);

        assertTrue(scn.HasKeyword(aks, Keyword.SENATOR));
        assertTrue(scn.HasKeyword(baskol, Keyword.SENATOR));
        assertTrue(GameConditions.isInSenateMajority(scn.game(), aks));
        assertTrue(GameConditions.isInSenateMajority(scn.game(), baskol));
    }

    @Test
    public void SeparatistCouncilRoomOneSenatorHereIsNotInASenateMajority() {
        var scn = GetScenario();
        var council = scn.GetDSCard("council");
        var aks = scn.GetDSCard("aks");
        var baskol = scn.GetDSCard("baskol");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(council);
        scn.MoveCardsToLocation(council, aks);
        scn.MoveCardsToLocation(dsSite, baskol);

        assertFalse(GameConditions.isInSenateMajority(scn.game(), aks));
        assertFalse(GameConditions.isInSenateMajority(scn.game(), baskol));
    }

    @Test
    public void SeparatistCouncilRoomLightSideSpyMayNotDeployHereWhileGunrayHere() {
        var scn = GetScenario();
        var council = scn.GetDSCard("council");
        var gunray = scn.GetDSCard("gunray");
        var momaw = scn.GetLSCard("momaw");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(council);
        scn.MoveCardsToLocation(council, gunray);
        scn.MoveCardsToLSHand(momaw);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);

        assertTrue(scn.HasKeyword(momaw, Keyword.SPY));
        assertTrue(scn.LSDeployAvailable(momaw));
        scn.LSDeployCard(momaw);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(lsSite));
        assertFalse(scn.LSHasCardChoiceAvailable(council));
    }

    @Test
    public void SeparatistCouncilRoomLightSideSpyMayNotDeployHereWhileDarkSideSenatorHere() {
        var scn = GetScenario();
        var council = scn.GetDSCard("council");
        var aks = scn.GetDSCard("aks");
        var momaw = scn.GetLSCard("momaw");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(council);
        scn.MoveCardsToLocation(council, aks);
        scn.MoveCardsToLSHand(momaw);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);

        assertTrue(scn.LSDeployAvailable(momaw));
        scn.LSDeployCard(momaw);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(lsSite));
        assertFalse(scn.LSHasCardChoiceAvailable(council));
    }

    @Test
    public void SeparatistCouncilRoomLightSideSpyMayDeployHereWhenNeitherGunrayNorDarkSideSenatorHere() {
        var scn = GetScenario();
        var council = scn.GetDSCard("council");
        var vader = scn.GetDSCard("vader");
        var momaw = scn.GetLSCard("momaw");

        scn.StartGame();
        scn.MoveLocationToTable(council);
        scn.MoveCardsToLocation(council, vader);
        scn.MoveCardsToLSHand(momaw);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);

        assertTrue(scn.LSDeployAvailable(momaw));
        scn.LSDeployCard(momaw);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(council));
        scn.LSChooseCard(council);
        scn.PassAllResponses();

        assertAtLocation(council, momaw);
    }

    @Test
    public void SeparatistCouncilRoomLightSideNonSpyMayDeployHereWhileGunrayHere() {
        var scn = GetScenario();
        var council = scn.GetDSCard("council");
        var gunray = scn.GetDSCard("gunray");
        var momaw = scn.GetLSCard("momaw");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(council);
        scn.MoveCardsToLocation(council, gunray);
        // Light Side has no Force icons at the Council Room, so a non-spy needs Light presence there to deploy.
        scn.MoveCardsToLocation(council, momaw);
        scn.MoveCardsToLSHand(luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);

        assertFalse(scn.HasKeyword(luke, Keyword.SPY));
        assertTrue(scn.LSDeployAvailable(luke));
        scn.LSDeployCard(luke);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(council));
        scn.LSChooseCard(council);
        scn.PassAllResponses();

        assertAtLocation(council, luke);
    }
}
