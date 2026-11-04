package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_031_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("shield", "1273_31");
                    put("coruscant", "1273_41");
                    put("powerplant", "1273_40");
                    put("rebel", "1_028");
                }},
                new HashMap<>(),
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
    public void PlanetaryShieldStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("shield").getBlueprint();

        assertEquals("Planetary Shield", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(com.gempukku.swccgo.common.Keyword.DEPLOYS_ON_LOCATION);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void PlanetaryShieldDeploysOnCoruscantOnlyIfCenterOfTyrannyOnTable() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        var coruscant = scn.GetLSCard("coruscant");

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveCardsToLSHand(shield);
        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(shield));

        Vb73LightTestSupport.putOnTable(scn, scn.LS, "1273_42");
        scn.SkipToNextTurn(scn.LS);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(shield));
        scn.LSDeployCard(shield);
        if (scn.LSHasCardChoiceAvailable(coruscant)) {
            scn.LSChooseCard(coruscant);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(coruscant, shield));
    }

    private void setUpAttempt(VirtualTableScenario scn, boolean rogueSquadronPilotAtPowerPlant) {
        var shield = scn.GetLSCard("shield");
        var coruscant = scn.GetLSCard("coruscant");
        var powerplant = scn.GetLSCard("powerplant");
        var rebel = scn.GetLSCard("rebel");

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(powerplant);
        Vb73LightTestSupport.putOnTable(scn, scn.LS, "1273_42");
        scn.AttachCardsTo(coruscant, shield);
        scn.MoveCardsToLocation(powerplant, rebel);
        if (rogueSquadronPilotAtPowerPlant) {
            var pilot = MissionTestSupport.putInReserve(scn, 97302, scn.LS, Vb73LightTestSupport.rogueSquadronPilot());
            scn.MoveCardsToLocation(powerplant, pilot);
        }
        scn.SkipToLSTurn(Phase.CONTROL);
    }

    @Test
    public void PlanetaryShieldDisabledOnDestinyMoreThanFiveOpponentLosesThree() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        setUpAttempt(scn, false);

        int dsForce = scn.GetDSLifeForceRemaining();
        assertTrue(scn.LSCardActionAvailable(shield, "Attempt to 'disable'"));
        scn.PrepareLSDestiny(6);
        scn.LSUseCardAction(shield, "Attempt to 'disable'");
        Vb81RestTestSupport.passUntil(scn, () -> scn.AwaitingDSForceLossPayment());
        Vb81RestTestSupport.payForceLossFromReserveDeck(scn);
        assertEquals(Zone.OUT_OF_PLAY, shield.getZone());
        assertEquals(dsForce - 3, scn.GetDSLifeForceRemaining());
    }

    @Test
    public void PlanetaryShieldNotDisabledOnDestinyFiveAndOnlyOncePerControlPhase() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        setUpAttempt(scn, false);

        int dsForce = scn.GetDSLifeForceRemaining();
        scn.PrepareLSDestiny(5);
        scn.LSUseCardAction(shield, "Attempt to 'disable'");
        Vb81RestTestSupport.passUntil(scn, () -> scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.IsAttachedTo(scn.GetLSCard("coruscant"), shield));
        assertEquals(dsForce, scn.GetDSLifeForceRemaining());
        assertFalse(Vb81RestTestSupport.lsCardActionAvailable(scn, shield, "Attempt to 'disable'"));
    }

    @Test
    public void PlanetaryShieldAttemptRequiresYouToOccupyMainPowerPlant() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        var coruscant = scn.GetLSCard("coruscant");
        var powerplant = scn.GetLSCard("powerplant");

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(powerplant);
        Vb73LightTestSupport.putOnTable(scn, scn.LS, "1273_42");
        scn.AttachCardsTo(coruscant, shield);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(shield, "Attempt to 'disable'"));
    }

    @Test
    public void PlanetaryShieldMainPowerPlantAddsTwoPerRogueSquadronPilotThere() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        setUpAttempt(scn, true);

        // destiny 4 + 2 (one Rogue Squadron pilot at Main Power Plant) = 6 > 5
        scn.PrepareLSDestiny(4);
        scn.LSUseCardAction(shield, "Attempt to 'disable'");
        Vb81RestTestSupport.passUntil(scn, () -> scn.AwaitingDSForceLossPayment());
        Vb81RestTestSupport.payForceLossFromReserveDeck(scn);
        assertEquals(Zone.OUT_OF_PLAY, shield.getZone());
    }

    @Test
    public void PlanetaryShieldMayNotBeLost() {
        var scn = GetScenario();
        var shield = scn.GetLSCard("shield");
        var coruscant = scn.GetLSCard("coruscant");

        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.AttachCardsTo(coruscant, shield);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSExecuteAdHocEffect(shield, new LoseCardFromTableEffect(
                new TopLevelGameTextAction(shield, shield.getCardId()), shield));
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(coruscant, shield));
    }
}
