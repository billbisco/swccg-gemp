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
    public void ScoundrelsIngenuityForceDrainsPlusOneWhenDestinyGreaterThanThree() {
        var scn = GetScenario();
        var ingenuity = scn.GetLSCard("ingenuity");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        scn.AttachCardsTo(lando, ingenuity);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(4);

        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertTrue(scn.LSCardActionAvailable(ingenuity, "Draw destiny"));
        scn.LSUseCardAction(ingenuity, "Draw destiny");
        passDestinyWindows(scn);

        assertTrue(scn.LSForceDrainAvailable(site));
        scn.LSForceDrainAt(site);
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
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
        scn.PrepareLSDestiny(3);

        assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.LSUseCardAction(ingenuity, "Draw destiny");
        passDestinyWindows(scn);

        scn.LSForceDrainAt(site);
        scn.PassForceDrainStartResponses();
        assertEquals(1, scn.GetForceDrainTotal());
    }

    private void passDestinyWindows(VirtualTableScenario scn) {
        for (int i = 0; i < 12; i++) {
            if (scn.AwaitingLSControlPhaseActions()) {
                return;
            }
            if (scn.LSAnyDecisionsAvailable()) {
                scn.LSPass();
            }
            else if (scn.DSAnyDecisionsAvailable()) {
                scn.DSPass();
            }
            else {
                return;
            }
        }
    }
}
