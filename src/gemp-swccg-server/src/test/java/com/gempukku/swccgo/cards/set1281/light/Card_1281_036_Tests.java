package com.gempukku.swccgo.cards.set1281.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ingenuity", "1281_36");
                    put("lando", "601_50");
                    put("blaster", "1_152");
                    put("falcon", "1_143");
                    put("pilot", "1_27");
                    put("quarters", "5_080");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10,
                10,
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
    public void ScoundrelsIngenuityStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("ingenuity").getBlueprint();

        assertEquals("Scoundrel's Ingenuity", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.SCOUNDREL);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ScoundrelsIngenuityMakesScoundrelPowerPlusTwo() {
        var scn = GetScenario();
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        int power = scn.GetPower(lando);

        scn.AttachCardsTo(lando, ingenuity);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(power + 2, scn.GetPower(lando));
    }

    @Test
    public void ScoundrelsIngenuityAddsOneToTotalWeaponDestiny() {
        var scn = GetScenario();
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var blaster = scn.GetLSCard("blaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, trooper);
        scn.AttachCardsTo(lando, ingenuity, blaster);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(2);
        scn.PrepareLSDestiny(1);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        Integer last = null;
        for (String msg : scn.gameState().getLastMessages()) {
            String lower = msg.toLowerCase();
            for (String prefix : new String[] {"total weapon destiny is ", "total destiny: "}) {
                int idx = lower.lastIndexOf(prefix);
                if (idx >= 0) {
                    String rest = msg.substring(idx + prefix.length()).trim();
                    last = (int) Float.parseFloat(rest.split("[^0-9.]")[0]);
                }
            }
        }
        assertTrue("Expected a total weapon destiny log, got: " + String.join("\n", scn.gameState().getLastMessages()), last != null);
        assertEquals(2, last.intValue());
    }

    @Test
    public void ScoundrelsIngenuityForceDrainsPlusOneWhenDestinyGreaterThanThree() {
        var scn = GetScenario();
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        scn.AttachCardsTo(lando, ingenuity);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardActionAvailable(ingenuity, "Draw destiny"));
        scn.LSUseCardAction(ingenuity, "Draw destiny");
        scn.PrepareLSDestiny(4);
        finishIngenuityDrawBackToControl(scn);

        assertTrue("expected drain, ls=" + scn.LSGetDecision() + " ds=" + scn.DSGetDecision()
                        + " actions=" + scn.GetLSAvailableActions(),
                scn.LSForceDrainAvailable(site));
        scn.LSForceDrainAt(site);
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void ScoundrelsIngenuityRaisesRelatedLocationForceDrains() {
        var scn = GetScenario();
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var pilot = scn.GetLSCard("pilot");
        var quarters = scn.GetLSCard("quarters");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(quarters);
        site.setPartOfSystem("Bespin");
        quarters.setPartOfSystem("Bespin");
        scn.MoveCardsToLocation(site, lando);
        scn.MoveCardsToLocation(quarters, pilot);
        scn.AttachCardsTo(lando, ingenuity);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(ingenuity, "Draw destiny"));
        scn.LSUseCardAction(ingenuity, "Draw destiny");
        scn.PrepareLSDestiny(5);
        finishIngenuityDrawBackToControl(scn);
        assertTrue("expected related-site drain, ls=" + scn.LSGetDecision() + " ds=" + scn.DSGetDecision()
                        + " actions=" + scn.GetLSAvailableActions(),
                scn.LSForceDrainAvailable(quarters));
        scn.LSForceDrainAt(quarters);
        scn.PassForceDrainStartResponses();
        assertEquals("related drain is Dark icons + Ingenuity (Guest Quarters has 1 Dark Force), siteSystem="
                        + site.getPartOfSystem() + " quartersSystem=" + quarters.getPartOfSystem()
                        + " related=" + com.gempukku.swccgo.filters.Filters.sameOrRelatedLocation(lando).accepts(scn.game(), quarters),
                2, scn.GetForceDrainTotal());
    }

    @Test
    public void ScoundrelsIngenuityDoesNotRaiseForceDrainsWhenDestinyIsThreeOrLess() {
        var scn = GetScenario();
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        scn.AttachCardsTo(lando, ingenuity);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardActionAvailable(ingenuity, "Draw destiny"));
        scn.LSUseCardAction(ingenuity, "Draw destiny");
        scn.PrepareLSDestiny(3);
        finishIngenuityDrawBackToControl(scn);

        scn.LSForceDrainAt(site);
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }

    private void finishIngenuityDrawBackToControl(VirtualTableScenario scn) {
        scn.PassDestinyDrawResponses();
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSControlPhaseActions());
    }

    @Test
    public void ScoundrelsIngenuityDrawIsControlPhaseOnly() {
        var scn = GetScenario();
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        scn.AttachCardsTo(lando, ingenuity);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(ingenuity, "Draw destiny"));
        scn.SkipToPhase(Phase.BATTLE);
        assertFalse(scn.LSCardActionAvailable(ingenuity, "Draw destiny"));
    }
}
