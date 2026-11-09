package com.gempukku.swccgo.cards.set2271.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Ice-Heart (2271_5): the Virtual Block 7.1 printing of Ysanne Isard (1271_5), printed as "•Ice-Heart" and renamed Ysanne
 * Isard on the Virtual Cards Master List (2013-04-27). Same game text; the version is the printed title. Bill
 * (2026-11-08): "Although they have different names; they are the same persona" - Ice-Heart, the Legacy Ysanne Isard
 * (601_189) and the Set 0 Ysanne Isard (200_92) all have the Ysanne Isard persona and are one unique card.
 */
public class Card_2271_005_Tests {
    private static final String RELOCATE = "Relocate ISB agent to a site you control";

    private static VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("luke", "1_19");
        }}, new HashMap<>() {{
            put("iceHeart", "2271_5");
            put("legacy", "1271_5");
            put("set0", "200_92");
            put("isbOriginal", "2271_27");
            put("isbCurrent", "1271_27");
            put("storm2", "1_179");
        }}, 20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts, StartingSetup.NoLSShields,
                StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void IceHeartIsTheLegacyYsanneIsardPrintedAsIceHeart() {
        var s = scenario();
        SwccgCardBlueprint version = s.GetDSCard("iceHeart").getBlueprint();
        SwccgCardBlueprint current = s.GetDSCard("legacy").getBlueprint();
        assertEquals("Ice-Heart", version.getPrintedTitle());
        assertEquals("Ice-Heart", GameUtils.getFullName(version));
        assertEquals("Ysanne Isard", version.getTitle());
        assertEquals(current.getTitle(), version.getTitle());
        assertEquals(current.getTitles(), version.getTitles());
        assertEquals(current.getUniqueness(), version.getUniqueness());
        assertEquals(current.getGameText(), version.getGameText());
        assertEquals("Power +3 while defending a battle with a spy. While with a non-frozen captive, attrition against opponent everywhere is +2. During your move phase, may use 1 Force to 'break cover' of an Undercover spy here. Immune to attrition < 3.",
                version.getGameText());
        LegacyVersion info = version.getLegacyVersion();
        assertEquals("1271_5", info.currentPrintingId());
        assertEquals("", info.label());
        assertEquals(LocalDate.of(2010, 6, 18), info.printedOn());
        assertEquals(LocalDate.of(2013, 3, 14), info.supersededOn());
        // The current card keeps the latest title, Ysanne Isard
        assertNull(current.getLegacyVersion());
        assertNull(current.getPrintedTitle());
        assertEquals("Ysanne Isard", GameUtils.getFullName(current));
    }

    @Test
    public void EveryYsanneIsardCardHasTheYsanneIsardPersona() {
        var s = scenario();
        for (String key : List.of("iceHeart", "legacy", "set0"))
            assertEquals(key, Set.of(Persona.YSANNE_ISARD), s.GetDSCard(key).getBlueprint().getPersonas());
    }

    @Test
    public void IceHeartLegacyAndSet0YsanneIsardAreOneUniqueCard() {
        List<String> keys = List.of("iceHeart", "legacy", "set0");
        for (String onTableKey : keys) {
            for (String inHandKey : keys) {
                if (onTableKey.equals(inHandKey))
                    continue;
                for (boolean otherOut : List.of(false, true)) {
                    String what = onTableKey + " on table=" + otherOut + ", " + inHandKey + " in hand";
                    var s = scenario();
                    var onTable = s.GetDSCard(onTableKey);
                    var inHand = s.GetDSCard(inHandKey);
                    s.StartGame();
                    if (otherOut)
                        s.MoveCardsToLocation(s.GetDSStartingLocation(), onTable);
                    s.MoveCardsToDSHand(inHand);
                    s.SkipToDSTurn(Phase.DEPLOY);
                    s.EnsureDSForcePile(10);
                    assertEquals(what, otherOut, s.game().getModifiersQuerying().isUniquenessOnTableLimitReached(s.game().getGameState(), inHand));
                    assertEquals(what, !otherOut, s.DSDeployAvailable(inHand));
                    // Set 0 and Legacy cards have different expansion icons, so the persona rules let one replace the
                    // other (persona replacement); Ice-Heart and the Legacy Ysanne Isard are the same printing.
                    boolean replaceable = otherOut && (onTableKey.equals("set0") || inHandKey.equals("set0"));
                    assertEquals(what, !otherOut || replaceable, s.DSCardPlayAvailable(inHand));
                }
            }
        }
    }

    @Test
    public void Set0YsanneIsardPersonaReplacesIceHeart() {
        var s = scenario();
        var iceHeart = s.GetDSCard("iceHeart");
        var set0 = s.GetDSCard("set0");
        s.StartGame();
        var site = s.GetDSStartingLocation();
        s.MoveCardsToLocation(site, iceHeart);
        s.MoveCardsToDSHand(set0);
        s.SkipToDSTurn(Phase.DEPLOY);
        s.EnsureDSForcePile(10);
        s.DSPlayCard(set0);
        if (s.DSHasCardChoiceAvailable(iceHeart))
            s.DSChooseCard(iceHeart);
        s.PassAllResponses();
        assertTrue(s.CardsAtLocation(site, set0));
        assertFalse(s.CardsAtLocation(site, iceHeart));
    }

    private static boolean relocateOffered(String isbKey) {
        var s = scenario();
        var isb = s.GetDSCard(isbKey);
        s.StartGame();
        var office = MissionTestSupport.putLocationOnTable(s, 9201, s.DS, MissionTestSupport.isbCentralOffice());
        s.AttachCardsTo(office, isb);
        var agent = MissionTestSupport.putInHand(s, 9202, s.DS, MissionTestSupport.isbAgent());
        s.MoveCardsToLocation(office, agent, s.GetDSCard("iceHeart"));
        s.MoveCardsToLocation(s.GetDSStartingLocation(), s.GetDSCard("storm2"));
        assertTrue(s.HasKeyword(agent, Keyword.ISB_AGENT));
        s.SkipToDSTurn(Phase.MOVE);
        s.DSActivateForceCheat(3);
        boolean offered = s.DSActionAvailable(RELOCATE);
        if (offered) {
            s.DSUseCardAction(isb, RELOCATE);
            s.DSChooseCard(agent);
            s.DSChooseCard(s.GetDSStartingLocation());
            s.PassAllResponses();
            assertAtLocation(s.GetDSStartingLocation(), agent);
        }
        return offered;
    }

    @Test
    public void InTheHandsOfTheISBOriginalFindsIceHeart() {
        // 2271_27 (Jun 2010 slip): "an ISB agent present with Ice-Heart"
        assertTrue(relocateOffered("isbOriginal"));
    }

    @Test
    public void InTheHandsOfTheISBCurrentFindsIceHeartAsYsanneIsard() {
        // 1271_27 (Master List): "an ISB agent present with Ysanne Isard"
        assertTrue(relocateOffered("isbCurrent"));
    }
}
