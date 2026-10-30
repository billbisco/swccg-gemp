package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1104_031_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("lando", "13_27");
                }},
                new HashMap<>() {{
                    put("breached", "1104_31");
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
    public void BreachedDefensesStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("breached").getBlueprint();

        assertEquals("Breached Defenses", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.EFFECT);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Control));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BreachedDefensesDeploysOnOpponentsJustDeployedMaintenanceCardAndReducesPowerByThree() {
        var scn = GetScenario();
        var breached = scn.GetDSCard("breached");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(breached);
        scn.MoveCardsToLSHand(lando);
        scn.EnsureLSForcePile(8);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int printedPower = scn.GetPower(lando);
        int lsForceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(lando) || scn.LSDeployAvailable(lando));
        scn.LSPlayCard(lando);
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        scn.PassForceUseResponses();
        scn.PassCardPlayResponses();
        assertTrue("just-deployed window missing. lsDec=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : null)
                        + " dsDec=" + (scn.DSGetDecision() != null ? scn.DSGetDecision().getText() : null),
                scn.DSDecisionAvailable("just deployed"));
        scn.DSPlayCard(breached);
        if (scn.DSHasCardChoiceAvailable(lando)) {
            scn.DSChooseCard(lando);
        }
        scn.PassAllResponses();
        assertEquals("breached zone=" + breached.getZone() + " attachedTo=" + breached.getAttachedTo()
                        + " dsDec=" + (scn.DSGetDecision() != null ? scn.DSGetDecision().getText() : null),
                lando, breached.getAttachedTo());
        assertEquals(printedPower - 3, scn.GetPower(lando), scn.epsilon);
        assertTrue(scn.GetLSForcePileCount() <= lsForceBefore - 1 || scn.GetLSLostPileCount() >= 1);
    }
}
