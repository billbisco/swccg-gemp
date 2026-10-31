package com.gempukku.swccgo.cards.set1104.light;

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

public class Card_1104_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("sacrifice", "1104_9");
                }},
                new HashMap<>() {{
                    put("garrison", "13_87");
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
    public void HeroicSacrificeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("sacrifice").getBlueprint();

        assertEquals("Heroic Sacrifice", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
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
    public void HeroicSacrificeDeploysOnOpponentsJustDeployedMaintenanceCardAndReducesPowerByThree() {
        var scn = GetScenario();
        var sacrifice = scn.GetLSCard("sacrifice");
        var garrison = scn.GetDSCard("garrison");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSHand(sacrifice);
        scn.MoveCardsToDSHand(garrison);
        scn.EnsureDSForcePile(8);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int printedPower = scn.GetPower(garrison);
        int dsForceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardPlayAvailable(garrison) || scn.DSDeployAvailable(garrison));
        scn.DSPlayCard(garrison);
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
        }
        scn.PassForceUseResponses();
        scn.PassCardPlayResponses();
        assertTrue("just-deployed window missing. lsDec=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : null)
                        + " dsDec=" + (scn.DSGetDecision() != null ? scn.DSGetDecision().getText() : null),
                scn.LSDecisionAvailable("just deployed"));
        scn.LSPlayCard(sacrifice);
        if (scn.LSHasCardChoiceAvailable(garrison)) {
            scn.LSChooseCard(garrison);
        }
        scn.PassAllResponses();
        assertEquals("sacrifice zone=" + sacrifice.getZone() + " attachedTo=" + sacrifice.getAttachedTo()
                        + " lsDec=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : null),
                garrison, sacrifice.getAttachedTo());
        assertEquals(printedPower - 3, scn.GetPower(garrison), scn.epsilon);
        assertTrue(scn.GetDSForcePileCount() <= dsForceBefore - 1 || scn.GetDSLostPileCount() >= 1);
    }
}
