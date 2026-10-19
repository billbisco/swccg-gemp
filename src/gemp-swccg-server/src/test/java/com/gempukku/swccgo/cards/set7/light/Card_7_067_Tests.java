package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.WidenIncomKoensayrScopeModifier;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Koensayr Manufacturing (7_67). E12: Systems Will Slip Through Your Fingers (V) widens retrieve.
 * Hidden Base (V) is not on this branch; tests apply WidenIncomKoensayrScopeModifier ad-hoc.
 * Lost-pile search decisions display the whole pile, so eligibility is asserted by Verify vs retrieve-to-used.
 */
public class Card_7_067_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("koensayr", "7_67");
                    put("ralltiir", "2_65");
                    put("ywing", "1_147");
                    put("xwing", "1_146");
                    put("red1", "1_144");
                    put("speeder", "3_69");
                    put("xwingSquadron", "7_150");
                    put("corvette", "1_140");
                }},
                new HashMap<>() {{
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
    public void KoensayrManufacturingRetrievesAYwingWithoutWiden() {
        var scn = GetScenario();
        var koensayr = scn.GetLSCard("koensayr");
        var ralltiir = scn.GetLSCard("ralltiir");
        var ywing = scn.GetLSCard("ywing");

        scn.StartGame();
        scn.MoveLocationToTable(ralltiir);
        scn.AttachCardsTo(ralltiir, koensayr);
        scn.MoveCardsToTopOfLSLostPile(ywing);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(WidenIncomKoensayrScopeModifier.isActive(scn.game(), koensayr));
        assertTrue(scn.LSCardActionAvailable(koensayr, "Retrieve a Y-wing"));
        scn.LSUseCardAction(koensayr, "Retrieve a Y-wing");
        scn.PassAllResponses();
        assertTrue("decision=" + scn.LSGetDecision(), scn.LSDecisionAvailable("Choose card"));
        assertFalse(scn.LSDecisionAvailable("Verify"));
        scn.LSChooseCard(ywing);
        scn.PassAllResponses();
        assertEquals(Zone.TOP_OF_USED_PILE, ywing.getZone());
    }

    @Test
    public void KoensayrManufacturingWithoutWidenCannotRetrieveAnXwing() {
        var scn = GetScenario();
        var koensayr = scn.GetLSCard("koensayr");
        var ralltiir = scn.GetLSCard("ralltiir");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(ralltiir);
        scn.AttachCardsTo(ralltiir, koensayr);
        scn.MoveCardsToTopOfLSLostPile(xwing);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardActionAvailable(koensayr, "Retrieve a Y-wing"));
        scn.LSUseCardAction(koensayr, "Retrieve a Y-wing");
        scn.PassAllResponses();
        assertTrue("X-wing must not match printed Y-wing retrieve, decision=" + scn.LSGetDecision(),
                scn.LSDecisionAvailable("Verify"));
        scn.LSPass();
        scn.DSPass();
        assertEquals(Zone.TOP_OF_LOST_PILE, xwing.getZone());
    }

    @Test
    public void KoensayrManufacturingWithWidenRetrievesANonUniqueXwing() {
        var scn = GetScenario();
        var koensayr = scn.GetLSCard("koensayr");
        var ralltiir = scn.GetLSCard("ralltiir");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(ralltiir);
        scn.AttachCardsTo(ralltiir, koensayr);
        scn.ApplyAdHocModifier(new WidenIncomKoensayrScopeModifier(ralltiir));
        scn.MoveCardsToTopOfLSLostPile(xwing);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(WidenIncomKoensayrScopeModifier.isActive(scn.game(), koensayr));
        assertTrue(scn.LSCardActionAvailable(koensayr, "Retrieve a Y-wing"));
        scn.LSUseCardAction(koensayr, "Retrieve a Y-wing");
        scn.PassAllResponses();
        assertTrue("decision=" + scn.LSGetDecision(), scn.LSDecisionAvailable("Choose card"));
        assertFalse(scn.LSDecisionAvailable("Verify"));
        scn.LSChooseCard(xwing);
        scn.PassAllResponses();
        assertEquals(Zone.TOP_OF_USED_PILE, xwing.getZone());
    }

    @Test
    public void KoensayrManufacturingWithWidenRetrievesASnowspeeder() {
        var scn = GetScenario();
        var koensayr = scn.GetLSCard("koensayr");
        var ralltiir = scn.GetLSCard("ralltiir");
        var speeder = scn.GetLSCard("speeder");

        scn.StartGame();
        scn.MoveLocationToTable(ralltiir);
        scn.AttachCardsTo(ralltiir, koensayr);
        scn.ApplyAdHocModifier(new WidenIncomKoensayrScopeModifier(ralltiir));
        scn.MoveCardsToTopOfLSLostPile(speeder);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardActionAvailable(koensayr, "Retrieve a Y-wing"));
        scn.LSUseCardAction(koensayr, "Retrieve a Y-wing");
        scn.PassAllResponses();
        assertTrue("decision=" + scn.LSGetDecision(), scn.LSDecisionAvailable("Choose card"));
        assertFalse(scn.LSDecisionAvailable("Verify"));
        scn.LSChooseCard(speeder);
        scn.PassAllResponses();
        assertEquals(Zone.TOP_OF_USED_PILE, speeder.getZone());
    }

    @Test
    public void KoensayrManufacturingWithWidenCannotRetrieveUniqueRed1() {
        var scn = GetScenario();
        var koensayr = scn.GetLSCard("koensayr");
        var ralltiir = scn.GetLSCard("ralltiir");
        var red1 = scn.GetLSCard("red1");

        scn.StartGame();
        scn.MoveLocationToTable(ralltiir);
        scn.AttachCardsTo(ralltiir, koensayr);
        scn.ApplyAdHocModifier(new WidenIncomKoensayrScopeModifier(ralltiir));
        scn.MoveCardsToTopOfLSLostPile(red1);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardActionAvailable(koensayr, "Retrieve a Y-wing"));
        scn.LSUseCardAction(koensayr, "Retrieve a Y-wing");
        scn.PassAllResponses();
        assertTrue("unique Red 1 must not match, decision=" + scn.LSGetDecision(),
                scn.LSDecisionAvailable("Verify"));
        scn.LSPass();
        scn.DSPass();
        assertEquals(Zone.TOP_OF_LOST_PILE, red1.getZone());
    }

    @Test
    public void KoensayrManufacturingWithWidenCannotRetrieveXwingAssaultSquadron() {
        var scn = GetScenario();
        var koensayr = scn.GetLSCard("koensayr");
        var ralltiir = scn.GetLSCard("ralltiir");
        var xwingSquadron = scn.GetLSCard("xwingSquadron");

        scn.StartGame();
        scn.MoveLocationToTable(ralltiir);
        scn.AttachCardsTo(ralltiir, koensayr);
        scn.ApplyAdHocModifier(new WidenIncomKoensayrScopeModifier(ralltiir));
        scn.MoveCardsToTopOfLSLostPile(xwingSquadron);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(WidenIncomKoensayrScopeModifier.isActive(scn.game(), koensayr));
        assertTrue(scn.LSCardActionAvailable(koensayr, "Retrieve a Y-wing"));
        scn.LSUseCardAction(koensayr, "Retrieve a Y-wing");
        scn.PassAllResponses();
        assertTrue("squadron X-wing Assault Squadron must not match, decision=" + scn.LSGetDecision(),
                scn.LSDecisionAvailable("Verify"));
        scn.LSPass();
        scn.DSPass();
        assertEquals(Zone.TOP_OF_LOST_PILE, xwingSquadron.getZone());
    }

    @Test
    public void KoensayrManufacturingWithWidenCannotRetrieveCorellianCorvette() {
        var scn = GetScenario();
        var koensayr = scn.GetLSCard("koensayr");
        var ralltiir = scn.GetLSCard("ralltiir");
        var corvette = scn.GetLSCard("corvette");

        scn.StartGame();
        scn.MoveLocationToTable(ralltiir);
        scn.AttachCardsTo(ralltiir, koensayr);
        scn.ApplyAdHocModifier(new WidenIncomKoensayrScopeModifier(ralltiir));
        scn.MoveCardsToTopOfLSLostPile(corvette);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(WidenIncomKoensayrScopeModifier.isActive(scn.game(), koensayr));
        assertTrue(scn.LSCardActionAvailable(koensayr, "Retrieve a Y-wing"));
        scn.LSUseCardAction(koensayr, "Retrieve a Y-wing");
        scn.PassAllResponses();
        assertTrue("non-unique corvette must not match, decision=" + scn.LSGetDecision(),
                scn.LSDecisionAvailable("Verify"));
        scn.LSPass();
        scn.DSPass();
        assertEquals(Zone.TOP_OF_LOST_PILE, corvette.getZone());
    }
}
