package com.gempukku.swccgo.cards.set1105.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_1105_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("fear", "1105_36");
                    put("vader", "1_168");
                    put("bait", "1220_52");
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
    public void FearStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("fear").getBlueprint();

        assertEquals("Fear", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.INTERRUPT);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_5, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FearWhenDrawnForDestinyPlacesANonImmuneToAlterEffectInUsedPile() {
        var scn = GetScenario();
        var fear = scn.GetDSCard("fear");
        var bait = scn.GetDSCard("bait");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(bait);
        scn.MoveCardsToLocation(site, vader, luke);
        scn.MoveCardsToTopOfDSReserveDeck(fear);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.SkipToPowerSegment();
        scn.MoveCardsToTopOfDSReserveDeck(fear);
        assertTrue("expected DS battle destiny prompt; ds=" + scn.DSGetDecision() + " ls=" + scn.LSGetDecision(),
                scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        for (int i = 0; i < 16; i++) {
            if (scn.DSGetADParam("cardId") != null && (containsAction(scn, true, "Place Effect")
                    || containsAction(scn, true, "Used Pile"))) {
                break;
            }
            var ds = scn.DSGetDecision();
            String dsText = ds != null && ds.getText() != null ? ds.getText() : "";
            if (dsText.toLowerCase().contains("place effect")) {
                break;
            }
            if (scn.LSDecisionAvailable("DESTINY_DRAWN") && scn.LSGetADParam("cardId") == null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSDecisionAvailable("DESTINY_DRAWN") && scn.DSGetADParam("cardId") == null) {
                scn.DSPass();
                continue;
            }
            var ls = scn.LSGetDecision();
            String lsText = ls != null && ls.getText() != null ? ls.getText() : "";
            if (lsText.toLowerCase().contains("optional") || lsText.toLowerCase().contains("or pass")) {
                scn.LSPass();
                continue;
            }
            if (ds != null && (dsText.toLowerCase().contains("optional") || dsText.toLowerCase().contains("or pass"))
                    && !containsAction(scn, true, "Place Effect")) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue("ds=" + scn.GetDSAvailableActions() + " dsDec=" + scn.DSGetDecision()
                        + " lsDec=" + scn.LSGetDecision(),
                containsAction(scn, true, "Place Effect") || containsAction(scn, true, "Used Pile")
                        || (scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                        && scn.DSGetDecision().getText().toLowerCase().contains("place effect")));
        if (scn.DSGetADParam("cardId") != null && containsAction(scn, true, "Place Effect")) {
            scn.DSChooseAction("Place Effect in Used Pile");
        }
        else if (scn.DSGetADParam("cardId") != null) {
            scn.DSUseCardAction(fear);
        }
        else {
            scn.DSChooseAction("Place Effect in Used Pile");
        }
        if (scn.DSHasCardChoiceAvailable(bait)) {
            scn.DSChooseCard(bait);
        }
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, bait);
    }

    private boolean containsAction(VirtualTableScenario scn, boolean dark, String text) {
        var actions = dark ? scn.GetDSAvailableActions() : scn.GetLSAvailableActions();
        for (String action : actions) {
            if (action != null && action.contains(text)) {
                return true;
            }
        }
        return false;
    }
}
