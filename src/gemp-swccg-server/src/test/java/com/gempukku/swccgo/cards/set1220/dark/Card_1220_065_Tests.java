package com.gempukku.swccgo.cards.set1220.dark;

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

public class Card_1220_065_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("han", "1_11");
                    put("blaster", "1_152");
                }},
                new HashMap<>() {{
                    put("selfdestruct", "1220_65");
                    put("probe", "1220_15");
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
    public void SelfDestructMechanismStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("selfdestruct").getBlueprint();

        assertEquals("Self-Destruct Mechanism", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SelfDestructMechanismPlaysWhenYourDroidIsJustHitByACharacterWeapon() {
        var scn = GetScenario();
        var selfdestruct = scn.GetDSCard("selfdestruct");
        var probe = scn.GetDSCard("probe");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, probe);
        scn.AttachCardsTo(han, blaster);
        scn.MoveCardsToDSHand(selfdestruct);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(7);
        assertTrue("fire not offered. actions=" + scn.GetLSAvailableActions(),
                scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        assertTrue("probe not targetable. ls=" + scn.GetLSAvailableActions()
                        + " lsDec=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : null),
                scn.LSHasCardChoiceAvailable(probe));
        scn.LSChooseCard(probe);
        scn.PassForceUseResponses();
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("DESTINY_DRAWN");
        scn.PassResponses("ABOUT_TO_BE_HIT");
        scn.PassResponses("about to");
        for (int i = 0; i < 12; i++) {
            if (scn.DSGetADParam("cardId") != null
                    && (scn.DSCardPlayAvailable(selfdestruct, "Lose droid to target firing character")
                    || scn.DSCardPlayAvailable(selfdestruct))) {
                break;
            }
            if (probe.isHit() && scn.DSGetADParam("cardId") != null) {
                break;
            }
            var ds = scn.DSGetDecision();
            String dsText = ds != null && ds.getText() != null ? ds.getText().toLowerCase() : "";
            if (!probe.isHit() && dsText.contains("optional")) {
                scn.PassResponses("optional");
                continue;
            }
            if (dsText.contains("force") && dsText.contains("optional")) {
                scn.PassForceUseResponses();
                continue;
            }
            if (dsText.contains("destiny")) {
                scn.PassResponses("DESTINY");
                continue;
            }
            if (dsText.contains("about to")) {
                scn.PassResponses("about to");
                continue;
            }
            break;
        }
        assertTrue("hit=" + probe.isHit() + " ds=" + scn.GetDSAvailableActions()
                        + " dsDec=" + (scn.DSGetDecision() != null ? scn.DSGetDecision().getText() : null)
                        + " lsDec=" + (scn.LSGetDecision() != null ? scn.LSGetDecision().getText() : null),
                scn.DSGetADParam("cardId") != null
                        && (scn.DSCardPlayAvailable(selfdestruct, "Lose droid to target firing character")
                        || scn.DSCardPlayAvailable(selfdestruct)));
        if (scn.DSCardPlayAvailable(selfdestruct, "Lose droid to target firing character")) {
            scn.DSPlayCard(selfdestruct, "Lose droid to target firing character");
        }
        else {
            scn.DSPlayCard(selfdestruct);
        }
        for (int i = 0; i < 20; i++) {
            if (selfdestruct.getZone() == Zone.USED_PILE || selfdestruct.getZone() == Zone.TOP_OF_USED_PILE) {
                break;
            }
            if (scn.DSHasCardChoiceAvailable(han)) {
                scn.DSChooseCard(han);
                continue;
            }
            var ds = scn.DSGetDecision();
            String dsText = ds != null && ds.getText() != null ? ds.getText().toLowerCase() : "";
            var ls = scn.LSGetDecision();
            String lsText = ls != null && ls.getText() != null ? ls.getText().toLowerCase() : "";
            if (dsText.contains("optional") || lsText.contains("optional")) {
                scn.PassResponses("optional");
                continue;
            }
            if (dsText.contains("destiny") || lsText.contains("destiny")) {
                scn.PassResponses("DESTINY");
                continue;
            }
            if (dsText.contains("about to") || lsText.contains("about to")) {
                scn.PassResponses("about to");
                continue;
            }
            if (dsText.contains("force") || lsText.contains("force")) {
                scn.PassForceUseResponses();
                continue;
            }
            if (ds != null && scn.DSGetADParam("cardId") == null) {
                scn.DSPass();
                continue;
            }
            if (ds == null && ls != null) {
                var ids = scn.LSGetCardChoices();
                if (ids != null && !ids.isEmpty()) {
                    scn.LSDecided(ids.getFirst());
                    continue;
                }
                var forceTop = scn.GetTopOfLSForcePile();
                if (forceTop != null && scn.LSHasCardChoiceAvailable(forceTop)) {
                    scn.LSChooseCard(forceTop);
                    continue;
                }
                if (scn.LSHasCardChoiceAvailable(han)) {
                    scn.LSChooseCard(han);
                    continue;
                }
                scn.LSPass();
                continue;
            }
            break;
        }
        assertInZone(Zone.USED_PILE, selfdestruct);
        assertInZone(Zone.LOST_PILE, probe);
    }
}
