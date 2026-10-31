package com.gempukku.swccgo.cards.set1108.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1108_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("yerka", "1108_25");
                    put("cantina", "1_128");
                    put("boshek", "1_4");
                    put("momaw", "1_20");
                    put("luke", "1_19");
                    put("junk", "1_62");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void YerkaMigVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("yerka").getBlueprint();

        assertEquals("Yerka Mig", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void YerkaMigVDeploysOnlyOnCantina() {
        var scn = GetScenario();
        var yerka = scn.GetLSCard("yerka");
        var cantina = scn.GetLSCard("cantina");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveCardsToLSHand(yerka);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSCardPlayAvailable(yerka));
        scn.LSPlayCard(yerka);
        assertTrue(scn.LSHasCardChoiceAvailable(cantina));
        assertFalse(scn.LSHasCardChoiceAvailable(lsSite));
        scn.LSChooseCard(cantina);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(cantina, yerka));
    }

    @Test
    public void YerkaMigVDoesNotDeployWithoutCantina() {
        var scn = GetScenario();
        var yerka = scn.GetLSCard("yerka");

        scn.StartGame();
        scn.MoveCardsToLSHand(yerka);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardPlayAvailable(yerka));
    }

    /**
     * Light Side deploys the given character from hand to the Cantina during its deploy phase and both players pass
     * the Force-use and play responses, stopping at Light Side's "just deployed" optional response window (if any).
     */
    private void DeployFromHandToCantina(VirtualTableScenario scn, PhysicalCardImpl character, int deployCost) {
        var cantina = scn.GetLSCard("cantina");

        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(character));
        scn.LSDeployCard(character);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.LSHasCardChoiceAvailable(cantina));
        scn.LSChooseCard(cantina);
        assertTrue(scn.DSDecisionAvailable("Use " + deployCost + " Force - Optional responses"));
        scn.DSPass();
        assertTrue(scn.LSDecisionAvailable("Use " + deployCost + " Force - Optional responses"));
        scn.LSPass();
        assertTrue(scn.DSDecisionAvailable("PLAY - Optional responses"));
        scn.DSPass();
        assertAtLocation(cantina, character);
    }

    @Test
    public void YerkaMigVMayRetrieve1ForceWhenYouDeployAUniqueAlienToABattlegroundWhileAllYourAbilityIsFromAliens() {
        var scn = GetScenario();
        var yerka = scn.GetLSCard("yerka");
        var cantina = scn.GetLSCard("cantina");
        var momaw = scn.GetLSCard("momaw");
        var boshek = scn.GetLSCard("boshek");
        var junk = scn.GetLSCard("junk");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(cantina, yerka);
        scn.MoveCardsToLocation(cantina, momaw);
        scn.MoveCardsToTopOfLSLostPile(junk);
        scn.MoveCardsToLSHand(boshek);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(6);

        assertTrue(scn.HasType(boshek, CardType.ALIEN));
        assertEquals(Uniqueness.UNIQUE, boshek.getBlueprint().getUniqueness());
        DeployFromHandToCantina(scn, boshek, 4);

        assertTrue(scn.LSDecisionAvailable("just deployed - Optional responses"));
        assertTrue(scn.LSCardActionAvailable(yerka, "Retrieve 1 Force"));
        scn.LSUseCardAction(yerka, "Retrieve 1 Force");
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, junk);
    }

    @Test
    public void YerkaMigVDoesNotOfferRetrievalWhileANonAlienProvidesAbility() {
        var scn = GetScenario();
        var yerka = scn.GetLSCard("yerka");
        var cantina = scn.GetLSCard("cantina");
        var luke = scn.GetLSCard("luke");
        var boshek = scn.GetLSCard("boshek");
        var junk = scn.GetLSCard("junk");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(cantina, yerka);
        scn.MoveCardsToLocation(lsSite, luke);
        scn.MoveCardsToTopOfLSLostPile(junk);
        scn.MoveCardsToLSHand(boshek);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(6);

        DeployFromHandToCantina(scn, boshek, 4);

        assertFalse(scn.LSCardActionAvailable(yerka, "Retrieve 1 Force"));
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertInZone(Zone.LOST_PILE, junk);
    }

    @Test
    public void YerkaMigVAddsThreeToYourTotalPowerInBattleWhileAllYourAbilityIsFromAliens() {
        var scn = GetScenario();
        var yerka = scn.GetLSCard("yerka");
        var cantina = scn.GetLSCard("cantina");
        var momaw = scn.GetLSCard("momaw");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(cantina, yerka);
        scn.MoveCardsToLocation(cantina, momaw, vader);
        scn.SkipToDSTurn(Phase.BATTLE);

        assertTrue(scn.DSCanInitiateBattle(cantina));
        scn.DSInitiateBattle(cantina);
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);

        // Light Side draws no battle destiny here (Momaw's ability is 3), so its total power is just power + 3.
        assertEquals(scn.GetPower(momaw) + 3, scn.GetLSTotalPower());
    }

    @Test
    public void YerkaMigVDoesNotAddToTotalPowerWhileANonAlienProvidesAbility() {
        var scn = GetScenario();
        var yerka = scn.GetLSCard("yerka");
        var cantina = scn.GetLSCard("cantina");
        var momaw = scn.GetLSCard("momaw");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(cantina, yerka);
        scn.MoveCardsToLocation(cantina, momaw, vader);
        scn.MoveCardsToLocation(lsSite, luke);
        scn.SkipToDSTurn(Phase.BATTLE);

        assertTrue(scn.DSCanInitiateBattle(cantina));
        scn.DSInitiateBattle(cantina);
        scn.PrepareDSDestiny(0);
        scn.PrepareLSDestiny(0);
        scn.SkipToEndOfPowerSegment(true);

        assertEquals(scn.GetPower(momaw), scn.GetLSTotalPower());
    }

    @Test
    public void YerkaMigVIsLostWhenOpponentControlsCantina() {
        var scn = GetScenario();
        var yerka = scn.GetLSCard("yerka");
        var cantina = scn.GetLSCard("cantina");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(cantina, yerka);
        scn.MoveCardsToDSHand(vader);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(8);

        assertTrue(scn.IsAttachedTo(cantina, yerka));
        assertTrue(scn.DSDeployAvailable(vader));
        scn.DSDeployCard(vader);
        assertTrue(scn.DSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.DSHasCardChoiceAvailable(cantina));
        scn.DSChooseCard(cantina);
        scn.PassAllResponses();

        assertAtLocation(cantina, vader);
        assertInZone(Zone.LOST_PILE, yerka);
    }

    @Test
    public void YerkaMigVIsNotLostWhileOpponentDoesNotControlCantina() {
        var scn = GetScenario();
        var yerka = scn.GetLSCard("yerka");
        var cantina = scn.GetLSCard("cantina");
        var momaw = scn.GetLSCard("momaw");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.AttachCardsTo(cantina, yerka);
        scn.MoveCardsToLocation(cantina, momaw);
        scn.MoveCardsToDSHand(vader);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(8);

        assertTrue(scn.DSDeployAvailable(vader));
        scn.DSDeployCard(vader);
        assertTrue(scn.DSDecisionAvailable("Choose where to deploy"));
        assertTrue(scn.DSHasCardChoiceAvailable(cantina));
        scn.DSChooseCard(cantina);
        scn.PassAllResponses();

        assertAtLocation(cantina, vader);
        assertTrue(scn.IsAttachedTo(cantina, yerka));
    }
}
