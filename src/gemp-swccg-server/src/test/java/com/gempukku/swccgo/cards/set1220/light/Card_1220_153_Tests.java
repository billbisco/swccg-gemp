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
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1220_153_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("shocking", "1220_153");
                }},
                new HashMap<>() {{
                    put("empiresback", "1_272");
                    put("vader", "1_168");
                    put("tarkin", "1_179");
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
    public void ShockingInformationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("shocking").getBlueprint();

        assertEquals("Shocking Information", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
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
    public void ShockingInformationPlaysWhenOpponentTakesACharacterIntoHandFromReserve() {
        var scn = GetScenario();
        var shocking = scn.GetLSCard("shocking");
        var empiresback = scn.GetDSCard("empiresback");
        var vader = scn.GetDSCard("vader");
        var tarkin = scn.GetDSCard("tarkin");

        scn.StartGame();
        scn.MoveCardsToLSHand(shocking);
        var stormtrooper = scn.GetDSCard("stormtrooper");
        scn.MoveCardsToDSHand(empiresback, tarkin, stormtrooper);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(vader);

        assertTrue(scn.DSCardPlayAvailable(empiresback, "Take card into hand from Reserve Deck")
                || scn.DSCardPlayAvailable(empiresback));
        if (scn.DSCardPlayAvailable(empiresback, "Take card into hand from Reserve Deck")) {
            scn.DSPlayCard(empiresback, "Take card into hand from Reserve Deck");
        }
        else {
            scn.DSPlayCard(empiresback);
        }
        for (int i = 0; i < 20; i++) {
            if (scn.LSAnyActionsAvailable()
                    && scn.LSCardPlayAvailable(shocking, "Place 2 cards from hand on Used Pile")) {
                break;
            }
            if (scn.DSHasCardChoiceAvailable(vader)) {
                scn.DSChooseCard(vader);
                continue;
            }
            var dsForce = scn.GetTopOfDSForcePile();
            if (dsForce != null && scn.DSHasCardChoiceAvailable(dsForce)) {
                scn.DSChooseCard(dsForce);
                continue;
            }
            var ls = scn.LSGetDecision();
            String lsText = ls != null && ls.getText() != null ? ls.getText().toLowerCase() : "";
            var ds = scn.DSGetDecision();
            String dsText = ds != null && ds.getText() != null ? ds.getText().toLowerCase() : "";
            if (dsText.contains("optional response") || lsText.contains("optional response")) {
                scn.PassAllResponses();
                continue;
            }
            if (dsText.contains("force") || lsText.contains("force")) {
                scn.PassForceUseResponses();
                continue;
            }
            if (scn.DSGetADParam("cardId") != null) {
                scn.DSPass();
                continue;
            }
            if (ds != null && scn.LSGetADParam("cardId") == null) {
                scn.DSPass();
                continue;
            }
            if (ls != null && scn.LSGetADParam("cardId") == null) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertTrue("ls=" + scn.GetLSAvailableActions() + " lsDec=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : null)
                        + " vaderZone=" + vader.getZone(),
                scn.LSAnyActionsAvailable()
                        && scn.LSCardPlayAvailable(shocking, "Place 2 cards from hand on Used Pile"));
        scn.LSPlayCard(shocking, "Place 2 cards from hand on Used Pile");
        for (int i = 0; i < 16; i++) {
            if (shocking.getZone() == Zone.USED_PILE || shocking.getZone() == Zone.TOP_OF_USED_PILE) {
                break;
            }
            if (scn.DSHasCardChoiceAvailable(tarkin)) {
                scn.DSChooseCard(tarkin);
                continue;
            }
            if (scn.DSHasCardChoiceAvailable(stormtrooper)) {
                scn.DSChooseCard(stormtrooper);
                continue;
            }
            if (Boolean.TRUE.equals(scn.DSChoiceAvailable("Done"))) {
                scn.DSChoose("Done");
                continue;
            }
            scn.PassAllResponses();
        }
        assertInHand(vader);
        assertInZone(Zone.USED_PILE, shocking);
    }
}
