package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.ReleaseCaptiveEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_067_Tests {
    private VirtualTableScenario fixture(boolean darkStartsPrize) { return fixture(darkStartsPrize, true); }

    private VirtualTableScenario fixture(boolean darkStartsPrize, boolean startingEffect) {
        var light = new HashMap<>(Map.of("maintenance", "13_21", "regularFalcon", "1_143",
                "space", "6_87", "effect", "13_5", "lsPresence", "1_146", "pilot", "1_19", "falconPilot", "1_11"));
        if (!startingEffect) light.remove("effect");
        StartingSetup darkStart = darkStartsPrize ? new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                return new HashMap<>(Map.of("cct", "7_296", "chamber", "5_166", "console", "5_107",
                        "tower", "5_172", "darkPrize", "10_42"));
            }
            @Override public void Setup(VirtualTableScenario scn) {
                for (int i = 0; i < 20 && scn.GetCurrentDecision() != null; i++) {
                    var prompt = scn.GetCurrentDecision().getText();
                    if (prompt.contains("On which side of")) scn.DSChooseOption("Left");
                    else if (prompt.startsWith("Choose a location to deploy")) scn.DSChooseCard(scn.GetDSCard("chamber"));
                    else if (prompt.contains("Choose Carbonite Chamber Console")) scn.DSChooseCard(scn.GetDSCard("console"));
                    else if (prompt.contains("Choose Carbonite Chamber to deploy")) scn.DSChooseCard(scn.GetDSCard("chamber"));
                    else if (prompt.contains("Choose Security Tower to deploy")) scn.DSChooseCard(scn.GetDSCard("tower"));
                    else if (prompt.contains("Choose Jabba's Prize to deploy")) scn.DSChooseCard(scn.GetDSCard("darkPrize"));
                    else if (prompt.startsWith("Verify ") || prompt.toLowerCase().contains("optional response")) {
                        scn.PlayerPass(scn.GetDecidingPlayer());
                    } else break;
                }
            }
        } : StartingSetup.DefaultDSGroundLocation;
        var outside = new StartingSetup() {
            @Override public HashMap<String, String> Cards() { return new HashMap<>(Map.of("prize", "1261_67")); }
            @Override public void Setup(VirtualTableScenario scn) {}
        };
        return new VirtualTableScenario(light, new HashMap<>(Map.of("spacePresence", "1_299", "darkPilot", "1_173", "vader", "1_168", "audience", "6_162")), 40, 40, darkStartsPrize ? StartingSetup.DefaultLSGroundLocation : StartingSetup.DefaultLSSpaceSystem,
                darkStart, StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                outside, StartingSetup.NoDSShields, "legacy");
    }

    @Test public void lightJabbasPrizeHasOnePlayableFaceAndExactPrintedIdentity() {
        var scn = fixture(false); var b = scn.GetLSCard("prize").getBlueprint();
        assertEquals(Title.Jabbas_Prize, b.getTitle()); assertEquals(Side.LIGHT, b.getSide());
        assertEquals(0, b.getDestiny(), scn.epsilon); assertEquals(0, b.getAbility(), scn.epsilon);
        assertEquals(0, b.getPower(), scn.epsilon); assertEquals(0, b.getDeployCost(), scn.epsilon);
        assertEquals(0, b.getForfeit(), scn.epsilon); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertTrue(b.hasVirtualSuffix()); assertFalse(scn.GetLSCard("prize").isDoubleSided());
        assertTrue(b.isDoesNotCountTowardDeckLimit()); assertTrue(b.isMayNotBePlacedInReserveDeck());
        assertEquals(Rarity.V, b.getRarity()); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(List.of(CardType.REBEL)));
        scn.BlueprintPersonaCheck(b, new ArrayList<>(List.of(Persona.CORRAN_HORN)));
        scn.BlueprintIconCheck(b, new ArrayList<>(List.of(Icon.REBEL, Icon.REFLECTIONS_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        assertFalse(Filters.Corran_Horn.accepts(scn.game(), scn.GetLSCard("prize")));
    }

    @Test public void prizeIsOutOfPlayWithoutTheOpponentFrozenPrizeAndFalconRestrictionPersists() {
        var scn = fixture(false); scn.StartGame(); var prize = scn.GetLSCard("prize");
        assertEquals(scn.GetCurrentDecision().getText(), Zone.OUT_OF_PLAY, prize.getZone());
        assertFalse(Filters.Corran_Horn.accepts(scn.game(), prize));
        var space = scn.GetLSStartingLocation(); var normal = scn.GetLSCard("regularFalcon"); var maintenance = scn.GetLSCard("maintenance");
        scn.MoveCardsToLocation(space, scn.GetDSCard("spacePresence"), scn.GetLSCard("lsPresence"));
        scn.BoardAsPilot(scn.GetLSCard("lsPresence"), scn.GetLSCard("pilot"));
        scn.BoardAsPilot(scn.GetDSCard("spacePresence"), scn.GetDSCard("darkPilot"));
        scn.MoveCardsToLSHand(normal, maintenance, scn.GetLSCard("falconPilot"));
        scn.LSActivateForceCheat(12); scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.GetCurrentDecision().getText() + " " + scn.GetLSAvailableActions(), scn.LSDeployAvailable(normal));
        assertFalse(scn.LSDeployAvailable(maintenance));
    }

    @Test public void replacesActualDarkJabbasPrizeAtCarbonChamberTestingSetup() {
        var scn = fixture(true); scn.StartGame();
        var prize = scn.GetLSCard("prize");
        assertTrue("decision=" + scn.GetCurrentDecision().getText() + " zone=" + prize.getZone(),
                Filters.imprisonedIn(scn.GetDSCard("tower")).accepts(scn.game(), prize));
        assertTrue(Filters.frozenCaptive.accepts(scn.game(), prize));
        assertTrue(Filters.Corran_Horn.accepts(scn.game(), prize));
        assertEquals(Zone.OUT_OF_PLAY, scn.GetDSCard("darkPrize").getZone());
    }
    private void leavesTable(boolean release) {
        var scn = fixture(true); scn.StartGame();
        var prize = scn.GetLSCard("prize");
        assertTrue(Filters.frozenCaptive.accepts(scn.game(), prize));
        scn.SkipToLSTurn(Phase.DEPLOY);
        var site = scn.GetLSStartingLocation();
        var action = new TopLevelGameTextAction(site, scn.LS, site.getCardId());
        if (release) action.appendEffect(new ReleaseCaptiveEffect(action, prize));
        else action.appendEffect(new LoseCardFromTableEffect(action, prize));
        scn.carryOutEffectInPhaseActionByPlayer(scn.LS, action);
        if (scn.LSHasCardChoiceAvailable(prize)) scn.LSChooseCard(prize);
        if (scn.LSHasCardChoiceAvailable(scn.GetDSCard("tower"))) scn.LSChooseCard(scn.GetDSCard("tower"));
        scn.PassAllResponses();
        assertEquals(Zone.OUT_OF_PLAY, prize.getZone());
        assertFalse(Filters.Corran_Horn.accepts(scn.game(), prize));
        assertFalse(scn.GetLSLostPile().contains(prize));
    }

    @Test public void nativeReleasePlacesPrizeOutOfPlayInsteadOfKeepingCorran() { leavesTable(true); }
    @Test public void nativeLossPlacesTheCaptiveOutOfPlayInsteadOfInLostPile() { leavesTable(false); }

    private void audienceBattle(boolean atAudience) {
        var scn = fixture(true); scn.StartGame();
        var prize = scn.GetLSCard("prize"); var audience = scn.GetDSCard("audience");
        scn.MoveLocationToTable(audience);
        scn.gameState().imprisonCharacter(scn.game(), prize, audience);
        assertTrue(Filters.imprisonedIn(audience).accepts(scn.game(), prize));
        assertTrue(Filters.frozenCaptive.accepts(scn.game(), prize));
        var site = atAudience ? audience : scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("pilot"), scn.GetDSCard("vader"));
        scn.LSActivateForceCheat(8); scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(3); scn.PrepareDSDestiny(3);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(3, scn.GetLSTotalDestiny());
        assertEquals(atAudience ? 5 : 4, scn.GetDSTotalDestiny());
    }

    @Test public void captiveAddsOneToTheOpponentsActualAudienceChamberBattleDraw() { audienceBattle(true); }
    @Test public void captiveDoesNotAddToAnActualBattleDrawAtADifferentSite() { audienceBattle(false); }
    @Test public void realStartingEffectPubliclyRevealsTheLightPrize() {
        var scn = fixture(false); scn.StartGame();
        assertTrue(scn.IsCardActive(scn.GetLSCard("effect")));
        assertTrue(scn.gameState().getLastMessages().toString(), scn.gameState().getLastMessages().stream()
                .anyMatch(msg -> msg.contains("reveals") && msg.contains("1261_67")));
        assertEquals(Zone.OUT_OF_PLAY, scn.GetLSCard("prize").getZone());
    }

    @Test public void withoutAStartingEffectThereIsNoPrizeReveal() {
        var scn = fixture(false, false); scn.StartGame();
        assertFalse(scn.gameState().getLastMessages().stream()
                .anyMatch(msg -> msg.contains("reveals") && msg.contains("1261_67")));
        assertEquals(Zone.OUT_OF_PLAY, scn.GetLSCard("prize").getZone());
    }
}
