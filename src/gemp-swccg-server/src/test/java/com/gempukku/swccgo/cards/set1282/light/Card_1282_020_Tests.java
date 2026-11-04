package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_020_Tests {
    /** The Hyperdrive Generator's Gone as the Light Side starting Objective, with the three locations it deploys. */
    static final StartingSetup HyperdriveObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("hyperdrive", "12_89"); // The Hyperdrive Generator's Gone (Objective)
                put("junkyard", "12_87"); // Tatooine: Watto's Junkyard
                put("outskirts", "11_42"); // Tatooine: City Outskirts
                put("credits", "12_42"); // Credits Will Do Fine
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
            for (int i = 0; i < 10; i++) {
                if (scn.LSDecisionAvailable("On which side")) {
                    scn.LSChoose("Left");
                }
                else if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(scn.GetLSCard("junkyard"))) {
                    scn.LSChooseCard(scn.GetLSCard("junkyard"));
                }
                else if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(scn.GetLSCard("outskirts"))) {
                    scn.LSChooseCard(scn.GetLSCard("outskirts"));
                }
                else if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(scn.GetLSCard("credits"))) {
                    scn.LSChooseCard(scn.GetLSCard("credits"));
                }
                else {
                    return;
                }
            }
        }
    };

    protected VirtualTableScenario GetObjectiveScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("planet", "1282_20");
                    put("tatooine", "1_127"); // Tatooine system
                    put("quigon", "11_10"); // Qui-Gon Jinn
                    put("quigonsaber", "11_49"); // Qui-Gon Jinn's Lightsaber
                    put("s1", "1_127");
                    put("s2", "1_127");
                }},
                new HashMap<>() {{
                }},
                40, 10,
                HyperdriveObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("planet", "1282_20");
                    put("tatooine", "1_127"); // Tatooine system
                    put("quigon", "11_10"); // Qui-Gon Jinn
                    put("quigonsaber", "11_49"); // Qui-Gon Jinn's Lightsaber
                    put("s1", "1_127");
                    put("s2", "1_127");
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
    public void ARemotePlanetVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("planet").getBlueprint();

        assertEquals("A Remote Planet", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals("In unfamiliar and potentially hostile territory, Qui-Gon knew that success would require patience and caution.", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.EPISODE_I);
            add(Icon.SETUP);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    /** Starts with The Hyperdrive Generator's Gone and gets to your control phase. */
    private void setUpObjective(VirtualTableScenario scn) {
        scn.StartGame();
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(Zone.SIDE_OF_TABLE, scn.GetLSCard("hyperdrive").getZone());
    }

    /** From your control phase to your deploy phase. */
    private void toDeployPhase(VirtualTableScenario scn) {
        scn.LSPass();
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
    }

    @Test
    public void ARemotePlanetVDeploysOnTheHyperdriveGeneratorsGone() {
        var scn = GetObjectiveScenario();
        var planet = scn.GetLSCard("planet");
        var hyperdrive = scn.GetLSCard("hyperdrive");

        setUpObjective(scn);
        scn.MoveCardsToLSHand(planet);
        toDeployPhase(scn);

        assertTrue(scn.LSDeployAvailable(planet));
        scn.LSDeployCard(planet);
        if (scn.LSHasCardChoiceAvailable(hyperdrive)) {
            scn.LSChooseCard(hyperdrive);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(hyperdrive, planet));
    }

    @Test
    public void ARemotePlanetVMayNotDeployWithoutTheHyperdriveGeneratorsGone() {
        var scn = GetScenario();
        var planet = scn.GetLSCard("planet");

        scn.StartGame();
        scn.MoveCardsToLSHand(planet);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSDeployAvailable(planet));
    }

    private void deployARemotePlanetWithCardsBeneathCreditsWillDoFine(VirtualTableScenario scn, int beneath) {
        var planet = scn.GetLSCard("planet");
        var hyperdrive = scn.GetLSCard("hyperdrive");
        var credits = scn.GetLSCard("credits");

        setUpObjective(scn);
        for (int i = 1; i <= beneath; i++) {
            scn.StackCardsFaceDownOn(credits, scn.GetLSCard("s" + i));
        }
        scn.MoveCardsToLSHand(planet);
        toDeployPhase(scn);
        scn.LSDeployCard(planet);
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(hyperdrive)) {
            scn.LSChooseCard(hyperdrive);
        }
        scn.PassAllResponses();
    }

    @Test
    public void ARemotePlanetVTreatsFourOnTheObjectiveAsTwo() {
        var scn = GetObjectiveScenario();
        deployARemotePlanetWithCardsBeneathCreditsWillDoFine(scn, 2);
        // 2 cards beneath Credits Will Do Fine is enough to flip
        assertTrue(scn.GetLSCard("hyperdrive").isFlipped());
    }

    @Test
    public void ARemotePlanetVDoesNotFlipTheObjectiveWithOneCardBeneathCreditsWillDoFine() {
        var scn = GetObjectiveScenario();
        deployARemotePlanetWithCardsBeneathCreditsWillDoFine(scn, 1);
        assertTrue(scn.IsAttachedTo(scn.GetLSCard("hyperdrive"), scn.GetLSCard("planet")));
        assertFalse(scn.GetLSCard("hyperdrive").isFlipped());
    }

    @Test
    public void ARemotePlanetVAddsOneToYourForceGenerationAtCityOutskirts() {
        var scn = GetObjectiveScenario();
        var planet = scn.GetLSCard("planet");
        var hyperdrive = scn.GetLSCard("hyperdrive");
        var outskirts = scn.GetLSCard("outskirts");

        setUpObjective(scn);
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), outskirts, scn.LS);
        scn.AttachCardsTo(hyperdrive, planet);
        assertEquals(before + 1, scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), outskirts, scn.LS), scn.epsilon);
    }

    @Test
    public void TheHyperdriveGeneratorsGoneStopsYouDeployingSystems() {
        var scn = GetObjectiveScenario();
        var tatooine = scn.GetLSCard("tatooine");

        setUpObjective(scn);
        scn.MoveCardsToLSHand(tatooine);
        toDeployPhase(scn);
        assertFalse(scn.LSDeployAvailable(tatooine));
    }

    @Test
    public void ARemotePlanetVLetsYouDeploySystemsRegardlessOfObjectiveRestrictions() {
        var scn = GetObjectiveScenario();
        var planet = scn.GetLSCard("planet");
        var hyperdrive = scn.GetLSCard("hyperdrive");
        var tatooine = scn.GetLSCard("tatooine");

        setUpObjective(scn);
        scn.AttachCardsTo(hyperdrive, planet);
        scn.MoveCardsToLSHand(tatooine);
        toDeployPhase(scn);
        assertTrue(scn.LSDeployAvailable(tatooine));
    }

    @Test
    public void ARemotePlanetVOncePerGameDeploysQuiGonAndHisMatchingWeaponFromReserveDeck() {
        var scn = GetObjectiveScenario();
        var planet = scn.GetLSCard("planet");
        var hyperdrive = scn.GetLSCard("hyperdrive");
        var quigon = scn.GetLSCard("quigon");
        var quigonsaber = scn.GetLSCard("quigonsaber");
        var site = scn.GetLSCard("outskirts");

        setUpObjective(scn);
        scn.AttachCardsTo(hyperdrive, planet);
        scn.LSActivateForceCheat(12);
        scn.MoveCardsToBottomOfLSReserveDeck(quigon, quigonsaber);
        toDeployPhase(scn);

        assertTrue(scn.LSCardActionAvailable(planet, "Deploy Qui-Gon"));
        scn.LSUseCardAction(planet, "Deploy Qui-Gon");
        assertTrue(scn.LSHasCardChoiceAvailable(quigon));
        scn.LSChooseCard(quigon);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();
        assertEquals(Zone.AT_LOCATION, quigon.getZone());

        assertTrue(scn.LSDecisionAvailable("matching weapon"));
        scn.LSChooseYes();
        assertTrue(scn.LSHasCardChoiceAvailable(quigonsaber));
        scn.LSChooseCard(quigonsaber);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(quigon, quigonsaber));

        // Once per game
        assertFalse(LegacyVb82TestSupport.passUntilLSCardAction(scn, planet, "Deploy Qui-Gon"));
    }
}
