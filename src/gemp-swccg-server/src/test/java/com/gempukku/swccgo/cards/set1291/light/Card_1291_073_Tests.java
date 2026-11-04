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

public class Card_1291_073_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("laat", "1291_73");
                    put("cylinders", "211_53");
                    put("clone", "210_9");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("docking", "1_291");
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
    public void LowAltitudeAssaultTransportStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("laat").getBlueprint();

        assertEquals("Low-Altitude Assault Transport", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(5, card.getManeuver(), scn.epsilon);
        assertEquals(3, card.getLandspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(2, card.getPilotCapacity());
        assertEquals(6, card.getPassengerCapacity());
        assertEquals("Enclosed.", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.LAAT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.CLONE_ARMY);
            add(Icon.PILOT);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LowAltitudeAssaultTransportPermanentPilotProvidesAbilityOfTwo() {
        var scn = GetScenario();
        var laat = scn.GetLSCard("laat");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, laat);
        assertEquals(2, scn.GetLSAbilityAtLocation(site));
    }

    @Test
    public void LowAltitudeAssaultTransportMayMoveAsAReact() {
        var scn = GetScenario();
        var laat = scn.GetLSCard("laat");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var docking = scn.GetDSCard("docking");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(docking);
        scn.MoveCardsToLocation(site, laat);
        scn.MoveCardsToLocation(docking, luke, trooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(docking);
        assertTrue(scn.LSCardActionAvailable(laat, "react"));
        scn.LSUseCardAction(laat, "react");
        if (scn.LSHasCardChoiceAvailable(docking)) {
            scn.LSChooseCard(docking);
        }
        scn.PassAllResponses();
        assertAtLocation(docking, laat);
    }

    @Test
    public void LowAltitudeAssaultTransportClonesOnCloningCylindersDeployMinusOneAboard() {
        var scn = GetScenario();
        var laat = scn.GetLSCard("laat");
        var cylinders = scn.GetLSCard("cylinders");
        var clone = scn.GetLSCard("clone");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(cylinders);
        scn.StackCardsOn(cylinders, clone);
        scn.MoveCardsToLocation(site, laat);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSDeployAvailable(clone));
        scn.LSDeployCard(clone);
        assertTrue(scn.LSHasCardChoiceAvailable(laat));
        scn.LSChooseCard(laat);
        if (scn.LSDecisionAvailable("Choose capacity slot")) {
            scn.LSChoose("Passenger");
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAboard(laat, clone));
        assertEquals(forceBefore - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void LowAltitudeAssaultTransportClonesNotOnCloningCylindersDeployAtNormalCostAboard() {
        var scn = GetScenario();
        var laat = scn.GetLSCard("laat");
        var clone = scn.GetLSCard("clone");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, laat);
        scn.MoveCardsToLSHand(clone);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetLSForcePileCount();
        scn.LSDeployCard(clone);
        scn.LSChooseCard(laat);
        if (scn.LSDecisionAvailable("Choose capacity slot")) {
            scn.LSChoose("Passenger");
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAboard(laat, clone));
        assertEquals(forceBefore - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void LowAltitudeAssaultTransportImmuneToAttritionLessThanFourOnlyWhileACloneAboard() {
        var scn = GetScenario();
        var laat = scn.GetLSCard("laat");
        var clone = scn.GetLSCard("clone");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, laat);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), laat), scn.epsilon);
        scn.BoardAsPassenger(laat, clone);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), laat), scn.epsilon);
    }
}
