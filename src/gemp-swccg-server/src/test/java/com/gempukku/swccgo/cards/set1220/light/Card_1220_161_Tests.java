package com.gempukku.swccgo.cards.set1220.light;

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

public class Card_1220_161_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("medlab", "1220_161");
                    put("tooonebee", "3_1");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
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
    public void HothEchoMedLabStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("medlab").getBlueprint();

        assertEquals("Hoth: Echo Med Lab", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.UNDERGROUND);
            add(Icon.INTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SCOMP_LINK);
            add(Icon.HOTH);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(0, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HothEchoMedLabDeploysAMedicalDroidHereFromReserveDeck() {
        var scn = GetScenario();
        var medlab = scn.GetLSCard("medlab");
        var tooonebee = scn.GetLSCard("tooonebee");

        scn.StartGame();
        scn.MoveLocationToTable(medlab);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(tooonebee);

        assertTrue(scn.LSCardActionAvailable(medlab, "Deploy a medical droid from Reserve Deck"));
        scn.LSUseCardAction(medlab, "Deploy a medical droid from Reserve Deck");
        if (scn.LSHasCardChoiceAvailable(tooonebee)) {
            scn.LSChooseCard(tooonebee);
        }
        scn.PassAllResponses();

        assertAtLocation(medlab, tooonebee);
    }

    @Test
    public void HothEchoMedLabDoesNotOfferMedicalDroidSearchDuringControlPhase() {
        var scn = GetScenario();
        var medlab = scn.GetLSCard("medlab");
        var tooonebee = scn.GetLSCard("tooonebee");

        scn.StartGame();
        scn.MoveLocationToTable(medlab);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(tooonebee);

        assertFalse(scn.LSCardActionAvailable(medlab, "Deploy a medical droid from Reserve Deck"));
    }

    @Test
    public void HothEchoMedLabForceDrainIsPlusOneIfYourImperialPresent() {
        var scn = GetScenario();
        var medlab = scn.GetLSCard("medlab");
        var stormtrooper = scn.GetDSCard("stormtrooper");

        scn.StartGame();
        scn.MoveLocationToTable(medlab);

        float before = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), medlab, scn.DS);
        scn.MoveCardsToLocation(medlab, stormtrooper);
        float after = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), medlab, scn.DS);
        assertEquals(before + 1, after, scn.epsilon);
    }

    @Test
    public void HothEchoMedLabForceDrainIsUnchangedWhenNoImperialPresent() {
        var scn = GetScenario();
        var medlab = scn.GetLSCard("medlab");

        scn.StartGame();
        scn.MoveLocationToTable(medlab);

        float amount = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), medlab, scn.DS);
        assertEquals(1, amount, scn.epsilon);
    }

    @Test
    public void HothEchoMedLabForceDrainBonusNeedsDarkSideToControlWithAnImperialPresent() {
        var scn = GetScenario();
        var medlab = scn.GetLSCard("medlab");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var luke = scn.GetLSCard("luke");
        var mods = scn.game().getModifiersQuerying();

        //test1: Imperial present but Luke there too: Dark Side does not control, no +1 (the old GEMP text gave +1)
        //test2: Dark Side controls with an Imperial present: +1
        scn.StartGame();
        scn.MoveLocationToTable(medlab);
        float base = mods.getForceDrainAmount(scn.gameState(), medlab, scn.DS);
        scn.MoveCardsToLocation(medlab, stormtrooper, luke);
        assertEquals(base, mods.getForceDrainAmount(scn.gameState(), medlab, scn.DS), scn.epsilon); //test1
        scn.MoveCardsToLSHand(luke);
        assertEquals(base + 1, mods.getForceDrainAmount(scn.gameState(), medlab, scn.DS), scn.epsilon); //test2
    }

    @org.junit.Test
    public void HothEchoMedLabHasTheLatestText() {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<String, String>() {{ put("latest", "1220_161"); }},
                new java.util.HashMap<String, String>() {{  }},
                10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var card = scn.GetLSCard("latest").getBlueprint();
        org.junit.Assert.assertEquals("Light: During your deploy phase, may deploy a medical droid here from Reserve Deck; reshuffle. Dark: If you control, with an Imperial present, Force drain +1 here.",
                card.getGameText());
        org.junit.Assert.assertNull(card.getLegacyVersion());
        org.junit.Assert.assertFalse(card.getGameText().contains("If your Imperial present"));
    }
}
