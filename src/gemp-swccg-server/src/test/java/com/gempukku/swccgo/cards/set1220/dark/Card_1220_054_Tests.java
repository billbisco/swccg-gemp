package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1220_054_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("brief", "1220_54");
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
    public void BriefLossOfControlStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("brief").getBlueprint();

        assertEquals("Brief Loss Of Control", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BriefLossOfControlPreventsOpponentFromControllingALocationDuringActivate() {
        var scn = GetScenario();
        var brief = scn.GetDSCard("brief");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(brief);
        scn.SkipToLSTurn(Phase.ACTIVATE);
        assertTrue(scn.AwaitingLSActivatePhaseActions());
        assertTrue(scn.LSActionAvailable("Activate Force"));
        scn.LSChooseAction("Activate Force");
        int max = Math.min(scn.GetLSReserveDeckCount() - 1, scn.LSGetChoiceMax());
        scn.LSDecided(max);
        assertTrue(scn.DSDecisionAvailable("without you performing a top-level action"));
        scn.DSDecided(scn.DSGetChoiceMin());
        for (int i = 0; i < 16; i++) {
            if (scn.DSGetADParam("cardId") != null
                    && scn.DSCardPlayAvailable(brief, "Prevent opponent from controlling a location")) {
                break;
            }
            var ls = scn.LSGetDecision();
            String lsText = ls != null && ls.getText() != null ? ls.getText() : "";
            if (lsText.contains("OK") || lsText.toLowerCase().contains("opportunity to perform a top-level action")) {
                scn.LSChoose("OK");
                continue;
            }
            if (scn.LSGetADParam("cardId") != null) {
                scn.LSPass();
                continue;
            }
            if (ls != null && (lsText.toLowerCase().contains("optional") || lsText.toLowerCase().contains("or pass"))) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertTrue("ds=" + scn.GetDSAvailableActions() + " ls=" + scn.GetLSAvailableActions()
                        + " dsDec=" + scn.DSGetDecision() + " lsDec=" + scn.LSGetDecision(),
                scn.DSGetADParam("cardId") != null
                        && scn.DSCardPlayAvailable(brief, "Prevent opponent from controlling a location"));
        scn.DSPlayCard(brief, "Prevent opponent from controlling a location");
        if (scn.DSHasCardChoiceAvailable(site)) {
            scn.DSChooseCard(site);
        }
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, brief);
    }
}
