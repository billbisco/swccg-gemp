package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_042_Tests {
    StartingSetup CenterOfTyrannyObjective = new StartingSetup() {
        @Override
        public HashMap<String, String> Cards() {
            return new HashMap<>() {{
                put("cot", "1273_42");
            }};
        }

        @Override
        public void Setup(VirtualTableScenario scn) {
        }
    };

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("coruscant", "1273_41");
                    put("powerplant", "1273_40");
                    put("lowerlevels", "1273_39");
                    put("shield", "1273_31");
                    put("dockingbay", "12_074");
                    put("obiwan", "1_021");
                    put("luke", "1273_26");
                    put("blaster", "1_152");
                    put("rebel", "1_028");
                    put("redone", "1_144");
                }},
                new HashMap<>() {{
                    put("black2", "1_299");
                    put("bast", "1_165");
                    put("dantooine", "1_282");
                    put("vader", "1_168");
                }},
                10, 10,
                CenterOfTyrannyObjective,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private static PhysicalCardImpl startGame(VirtualTableScenario scn) {
        PhysicalCardImpl rogueInsertion = MissionTestSupport.putInReserve(scn, 97310, scn.LS, Vb81RestTestSupport.lightEffect("Rogue Insertion"));
        scn.StartGame();
        for (int i = 0; i < 10 && scn.LSDecisionAvailable("On which side"); ++i) {
            scn.LSDecided("0");
        }
        return rogueInsertion;
    }

    /**
     * Disables Planetary Shield (destiny 6 > 5) during the Light Side control phase, which flips Center Of Tyranny.
     */
    private static void disablePlanetaryShield(VirtualTableScenario scn) {
        var shield = scn.GetLSCard("shield");
        var powerplant = scn.GetLSCard("powerplant");
        scn.MoveCardsToLocation(powerplant, scn.GetLSCard("rebel"));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(6);
        scn.LSUseCardAction(shield, "Attempt to 'disable'");
        Vb81RestTestSupport.passUntil(scn, () -> scn.AwaitingDSForceLossPayment());
        Vb73LightTestSupport.payDSForceLoss(scn);
    }

    /**
     * Disables Planetary Shield and resolves A Liberated World's immediate retrieve (from the Lost Pile, which may be empty).
     */
    private static void disablePlanetaryShieldAndFlip(VirtualTableScenario scn) {
        disablePlanetaryShield(scn);
        for (int i = 0; i < 10; ++i) {
            if (scn.LSGetMultipleChoices() != null && scn.LSGetMultipleChoices().contains("Retrieve a card into hand")) {
                scn.LSChooseOption("Retrieve a card into hand");
            }
            else if (scn.LSGetCardChoices() != null && scn.LSGetCardChoiceCount() > 0) {
                scn.LSChooseAnyCard();
            }
            else if (scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
            else {
                break;
            }
        }
        assertEquals("A Liberated World", scn.GetLSCard("cot").getBlueprint().getTitle());
        for (int i = 0; i < 5; ++i) {
            Vb73LightTestSupport.payDSForceLoss(scn);
            scn.PassAllResponses();
        }
    }

    @Test
    public void CenterOfTyrannyStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("cot").getBlueprint();

        assertEquals("Center Of Tyranny", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ALiberatedWorldStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        startGame(scn);
        var card = scn.GetLSCard("cot").getOtherSideBlueprint();

        assertEquals("A Liberated World", card.getTitle());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.OBJECTIVE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.OBJECTIVE);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_7);
        }});
    }

    @Test
    public void CenterOfTyrannyDeploysCoruscantSitesPlanetaryShieldAndRogueInsertion() {
        var scn = GetScenario();
        var cot = scn.GetLSCard("cot");
        var coruscant = scn.GetLSCard("coruscant");
        var powerplant = scn.GetLSCard("powerplant");
        var lowerlevels = scn.GetLSCard("lowerlevels");
        var shield = scn.GetLSCard("shield");

        var rogueInsertion = startGame(scn);
        assertEquals(Zone.SIDE_OF_TABLE, cot.getZone());
        assertEquals(Zone.LOCATIONS, coruscant.getZone());
        assertEquals(Zone.LOCATIONS, powerplant.getZone());
        assertEquals(Zone.LOCATIONS, lowerlevels.getZone());
        assertTrue(scn.IsAttachedTo(coruscant, shield));
        assertTrue(rogueInsertion.getZone().isInPlay());
    }

    @Test
    public void CenterOfTyrannyRogueSquadronPilotsAreInsurgentsAndFireBlastersForFree() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var rebel = scn.GetLSCard("rebel");
        var blaster = scn.GetLSCard("blaster");
        var lowerlevels = scn.GetLSCard("lowerlevels");

        startGame(scn);
        scn.MoveCardsToLocation(lowerlevels, luke, rebel);
        scn.AttachCardsTo(luke, blaster);
        assertTrue(scn.HasKeyword(luke, Keyword.INSURGENT));
        assertFalse(scn.HasKeyword(rebel, Keyword.INSURGENT));
        assertEquals(0, scn.game().getModifiersQuerying().getFireWeaponCost(scn.gameState(), blaster, luke, null, 1), scn.epsilon);
        assertEquals(1, scn.game().getModifiersQuerying().getFireWeaponCost(scn.gameState(), blaster, rebel, null, 1), scn.epsilon);
    }

    @Test
    public void CenterOfTyrannyYourCharactersOfAbilityMoreThanFourAreDeployPlusFour() {
        var scn = GetScenario();
        var obiwan = scn.GetLSCard("obiwan");
        var luke = scn.GetLSCard("luke");

        startGame(scn);
        assertEquals(obiwan.getBlueprint().getDeployCost() + 4, scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), obiwan), scn.epsilon);
        assertEquals(luke.getBlueprint().getDeployCost(), scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), luke), scn.epsilon);
    }

    @Test
    public void CenterOfTyrannyDeployPhaseUsesOneForceToDeployCoruscantBattlegroundSite() {
        var scn = GetScenario();
        var cot = scn.GetLSCard("cot");
        var dockingbay = MissionTestSupport.putInReserve(scn, 97311, scn.LS, Vb73LightTestSupport.coruscantBattlegroundSite());
        startGame(scn);
        scn.LSActivateForceCheat(3);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(dockingbay);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(cot, "Deploy a Coruscant battleground site from Reserve Deck"));
        scn.LSUseCardAction(cot, "Deploy a Coruscant battleground site from Reserve Deck");
        Vb81RestTestSupport.passUntil(scn, () -> Vb81RestTestSupport.holdsDecision(scn, scn.LS, "Choose card to deploy"));
        if (scn.LSHasCardChoiceAvailable(dockingbay)) {
            scn.LSChooseCard(dockingbay);
        }
        else {
            Vb73LightTestSupport.chooseFirstSelectable(scn, scn.LS);
        }
        for (int i = 0; i < 10 && !dockingbay.getZone().isInPlay() && scn.GetCurrentDecision() != null; ++i) {
            var type = scn.GetCurrentDecision().getDecisionType();
            if (type == com.gempukku.swccgo.logic.decisions.AwaitingDecisionType.MULTIPLE_CHOICE) {
                scn.PlayerDecided(scn.GetDecidingPlayer(), "0");
            }
            else if (type == com.gempukku.swccgo.logic.decisions.AwaitingDecisionType.ARBITRARY_CARDS) {
                Vb73LightTestSupport.chooseFirstSelectable(scn, scn.GetDecidingPlayer());
            }
            else if (type == com.gempukku.swccgo.logic.decisions.AwaitingDecisionType.CARD_SELECTION) {
                scn.LSChooseCard(scn.LSHasCardChoiceAvailable(scn.GetLSCard("powerplant")) ? scn.GetLSCard("powerplant") : scn.GetLSCard("lowerlevels"));
            }
            else {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        scn.PassAllResponses();
        assertTrue(dockingbay.getZone().isInPlay());
        assertEquals("used=" + scn.GetLSUsedPileCount(), force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void CenterOfTyrannyFlipsWhenPlanetaryShieldDisabledAndRetrievesACard() {
        var scn = GetScenario();
        var cot = scn.GetLSCard("cot");
        var obiwan = scn.GetLSCard("obiwan");

        startGame(scn);
        scn.MoveCardsToTopOfLSLostPile(obiwan);
        disablePlanetaryShield(scn);
        for (int i = 0; i < 10 && obiwan.getZone() != Zone.HAND; ++i) {
            if (scn.LSGetMultipleChoices() != null && scn.LSGetMultipleChoices().contains("Retrieve a card into hand")) {
                scn.LSChooseOption("Retrieve a card into hand");
            }
            else if (scn.LSHasCardChoiceAvailable(obiwan)) {
                scn.LSChooseCard(obiwan);
            }
            else if (scn.GetCurrentDecision() != null && scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
            else {
                break;
            }
        }
        assertEquals("A Liberated World", cot.getBlueprint().getTitle());
        assertEquals(Zone.HAND, obiwan.getZone());
    }

    @Test
    public void ALiberatedWorldForceGenerationAtCoruscantSystemIsPlusTwo() {
        var scn = GetScenario();
        var coruscant = scn.GetLSCard("coruscant");

        startGame(scn);
        float before = scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), coruscant, scn.LS);
        disablePlanetaryShieldAndFlip(scn);
        scn.PassAllResponses();
        assertEquals("A Liberated World", scn.GetLSCard("cot").getBlueprint().getTitle());
        assertEquals(before + 2, scn.game().getModifiersQuerying().getForceGenerationFromLocation(scn.gameState(), coruscant, scn.LS), scn.epsilon);
    }

    @Test
    public void ALiberatedWorldForceDrainsPlusOneAtRelatedBattlegroundSiteControlledWithRogueSquadronPilotWhileYouOccupyASystem() {
        var scn = GetScenario();
        var coruscant = scn.GetLSCard("coruscant");
        var lowerlevels = scn.GetLSCard("lowerlevels");
        var luke = scn.GetLSCard("luke");
        var redone = scn.GetLSCard("redone");

        startGame(scn);
        disablePlanetaryShieldAndFlip(scn);
        scn.PassAllResponses();
        scn.MoveCardsToLocation(lowerlevels, luke);
        float withoutSystem = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), lowerlevels, scn.LS);
        scn.MoveCardsToLocation(coruscant, redone);
        scn.BoardAsPilot(redone, scn.GetLSCard("rebel"));
        float withSystem = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), lowerlevels, scn.LS);
        assertEquals(withoutSystem + 1, withSystem, scn.epsilon);
    }

    @Test
    public void ALiberatedWorldOncePerTurnMayLoseOneForceToStealStarshipOpponentJustLostFromSystem() {
        var scn = GetScenario();
        var black2 = scn.GetDSCard("black2");
        var bast = scn.GetDSCard("bast");
        var dantooine = scn.GetDSCard("dantooine");

        startGame(scn);
        disablePlanetaryShieldAndFlip(scn);
        Vb73LightTestSupport.payDSForceLoss(scn);
        scn.PassAllResponses();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, black2);
        scn.BoardAsPilot(black2, bast);
        scn.DSExecuteAdHocEffect(black2, new com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect(
                new com.gempukku.swccgo.logic.actions.TopLevelGameTextAction(black2, black2.getCardId()), black2));
        for (int i = 0; i < 20 && scn.GetCurrentDecision() != null; ++i) {
            if (scn.GetDecidingPlayer().equals(scn.LS) && scn.GetLSAvailableActions().stream().anyMatch(a -> a.startsWith("Steal")))
                break;
            if (scn.GetCurrentDecision().getDecisionType() == com.gempukku.swccgo.logic.decisions.AwaitingDecisionType.ARBITRARY_CARDS)
                Vb73LightTestSupport.chooseFirstSelectable(scn, scn.GetDecidingPlayer());
            else
                scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.GetLSAvailableActions().stream().anyMatch(a -> a.startsWith("Steal"))));
        scn.LSChooseAction(scn.GetLSAvailableActions().stream().filter(s -> s.startsWith("Steal")).findFirst().get());
        Vb81RestTestSupport.payForceLossFromReserveDeck(scn);
        if (Vb81RestTestSupport.holdsDecision(scn, scn.LS, "Choose card to steal into hand")) {
            Vb73LightTestSupport.chooseFirstSelectable(scn, scn.LS);
        }
        scn.PassAllResponses();
        assertTrue(scn.gameState().getHand(scn.LS).contains(black2));
    }
}
