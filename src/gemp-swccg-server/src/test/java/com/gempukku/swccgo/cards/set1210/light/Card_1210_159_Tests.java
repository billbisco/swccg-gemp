package com.gempukku.swccgo.cards.set1210.light;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_159_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("precise", "1210_159");
                    put("luke", "1_19");
                    put("blaster", "1_152");
                    put("obi", "1_21");
                    put("saber", "1_157");
                    put("chewie", "1210_94");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168");
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
    public void PreciseHitStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("precise").getBlueprint();

        assertEquals("Precise Hit", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void PreciseHitUsedPlacesJustHitTargetAndAttachedCardsInOwnersUsedPile() {
        var scn = GetScenario();
        var precise = scn.GetLSCard("precise");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.AttachCardsTo(luke, blaster);
        scn.MoveCardsToLSHand(precise);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.PrepareLSDestiny(7);
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);
        fireUntilHitOptional(scn, 1);

        assertTrue(scn.LSDecisionAvailable("HIT"));
        assertTrue(scn.LSCardPlayAvailable(precise, "Place target") || scn.LSCardPlayAvailable(precise));
        if (scn.LSCardPlayAvailable(precise, "Place target")) {
            scn.LSPlayCard(precise, "Place target");
        } else {
            scn.LSPlayCard(precise);
        }
        for (int i = 0; i < 24; i++) {
            if ((trooper.getZone() == Zone.USED_PILE || trooper.getZone() == Zone.TOP_OF_USED_PILE)
                    && (precise.getZone() == Zone.USED_PILE || precise.getZone() == Zone.TOP_OF_USED_PILE)) {
                break;
            }
            if (chooseIfSelectable(scn, false, trooper)) {
                continue;
            }
            if (payForce(scn, false) || payForce(scn, true)) {
                continue;
            }
            if (resolveDecision(scn, false) || resolveDecision(scn, true)) {
                continue;
            }
            scn.PassAllResponses();
        }

        assertInZone(Zone.USED_PILE, trooper);
        assertInZone(Zone.USED_PILE, precise);
    }

    @Test
    public void PreciseHitUsedIsNotOfferedAfterALightsaberHit() {
        var scn = GetScenario();
        var precise = scn.GetLSCard("precise");
        var obi = scn.GetLSCard("obi");
        var saber = scn.GetLSCard("saber");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, obi, trooper);
        scn.AttachCardsTo(obi, saber);
        scn.MoveCardsToLSHand(precise);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.LSCardActionAvailable(saber, "Fire"));
        scn.PrepareLSDestiny(7);
        scn.PrepareLSDestiny(7);
        scn.LSUseCardAction(saber, "Fire");
        scn.LSChooseCard(trooper);
        fireUntilHitOptional(scn, 2);

        if (scn.LSDecisionAvailable("HIT")) {
            assertFalse(scn.LSCardPlayAvailable(precise));
        }
        else {
            assertTrue(trooper.isHit());
        }
    }

    @Test
    public void PreciseHitLostCancelsHeroicOnAHeroicCharacter() {
        var scn = GetScenario();
        var precise = scn.GetLSCard("precise");
        var chewie = scn.GetLSCard("chewie");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, chewie, han);
        scn.MoveCardsToLSHand(precise);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.HasKeyword(chewie, Keyword.HEROIC));
        assertTrue(scn.LSCardPlayAvailable(precise, "Cancel Heroic"));
        scn.LSPlayCard(precise, "Cancel Heroic");
        if (scn.LSHasCardChoiceAvailable(chewie)) {
            scn.LSChooseCard(chewie);
        }
        scn.PassAllResponses();

        assertFalse(scn.HasKeyword(chewie, Keyword.HEROIC));
        assertInZone(Zone.LOST_PILE, precise);
    }

    @Test
    public void PreciseHitLostIsNotOfferedAgainstANonHeroicCharacter() {
        var scn = GetScenario();
        var precise = scn.GetLSCard("precise");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(precise);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(scn.HasKeyword(luke, Keyword.HEROIC));
        assertFalse(scn.LSCardPlayAvailable(precise, "Cancel Heroic"));
    }

    private void fireUntilHitOptional(VirtualTableScenario scn, int draws) {
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        for (int i = 0; i < draws; ++i) {
            scn.PassDestinyDrawResponses();
        }
        scn.PassResponses("ABOUT_TO_BE_HIT");
        if (scn.DSDecisionAvailable("HIT") || scn.DSDecisionAvailable("ABOUT_TO_BE_HIT")) {
            if (scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                    && (scn.DSGetDecision().getText().toLowerCase().contains("optional")
                    || scn.DSGetDecision().getText().toLowerCase().contains("or pass"))) {
                scn.DSPass();
            }
        }
    }

    private boolean chooseIfSelectable(VirtualTableScenario scn, boolean dark,
                                       com.gempukku.swccgo.game.PhysicalCardImpl card) {
        if (!isSelectable(scn, dark, card)) {
            return false;
        }
        if (dark) {
            scn.DSChooseCard(card);
        } else {
            scn.LSChooseCard(card);
        }
        return true;
    }

    private boolean isSelectable(VirtualTableScenario scn, boolean dark,
                                 com.gempukku.swccgo.game.PhysicalCardImpl card) {
        String[] bps = dark ? scn.DSGetADParam("blueprintId") : scn.LSGetADParam("blueprintId");
        String[] selectable = dark ? scn.DSGetADParam("selectable") : scn.LSGetADParam("selectable");
        if (bps != null && selectable != null) {
            for (int i = 0; i < Math.min(bps.length, selectable.length); i++) {
                if ("true".equalsIgnoreCase(selectable[i]) && card.getBlueprintId(true).equals(bps[i])) {
                    return true;
                }
            }
            return false;
        }
        return dark ? scn.DSHasCardChoiceAvailable(card) : scn.LSHasCardChoiceAvailable(card);
    }

    private boolean payForce(VirtualTableScenario scn, boolean dark) {
        var decision = dark ? scn.DSGetDecision() : scn.LSGetDecision();
        if (decision == null || decision.getText() == null) {
            return false;
        }
        String text = decision.getText().toLowerCase();
        if (!text.contains("force") || text.contains("retriev") || text.contains("forfeit")) {
            return false;
        }
        var force = dark ? scn.GetTopOfDSForcePile() : scn.GetTopOfLSForcePile();
        if (force != null && (dark ? scn.DSHasCardChoiceAvailable(force) : scn.LSHasCardChoiceAvailable(force))) {
            if (dark) {
                scn.DSChooseCard(force);
            } else {
                scn.LSChooseCard(force);
            }
            return true;
        }
        if (text.contains("optional")) {
            scn.PassForceUseResponses();
            return true;
        }
        String[] minParam = dark ? scn.DSGetADParam("min") : scn.LSGetADParam("min");
        if (minParam != null && minParam.length > 0) {
            int min = Integer.parseInt(minParam[0]);
            if (dark) {
                scn.DSDecided(min);
            } else {
                scn.LSDecided(min);
            }
            return true;
        }
        return false;
    }

    private boolean resolveDecision(VirtualTableScenario scn, boolean dark) {
        var decision = dark ? scn.DSGetDecision() : scn.LSGetDecision();
        if (decision == null) {
            return false;
        }
        String text = decision.getText() == null ? "" : decision.getText().toLowerCase();
        if (text.contains("optional") || text.contains("or pass")) {
            if (dark) {
                scn.DSPass();
            } else {
                scn.LSPass();
            }
            return true;
        }
        var results = dark ? scn.DSGetMultipleChoices() : scn.LSGetMultipleChoices();
        if (results != null && !results.isEmpty()) {
            String pick = results.get(0);
            for (String option : results) {
                if (option.equalsIgnoreCase("Yes")) {
                    pick = option;
                    break;
                }
            }
            if (dark) {
                scn.DSChoose(pick);
            } else {
                scn.LSChoose(pick);
            }
            return true;
        }
        String[] minParam = dark ? scn.DSGetADParam("min") : scn.LSGetADParam("min");
        String[] selectable = dark ? scn.DSGetADParam("selectable") : scn.LSGetADParam("selectable");
        if (minParam != null && selectable != null) {
            int min = Integer.parseInt(minParam[0]);
            if (min == 0) {
                if (dark) {
                    scn.DSPass();
                } else {
                    scn.LSPass();
                }
                return true;
            }
            java.util.List<String> picks = new java.util.ArrayList<>();
            for (int i = 0; i < selectable.length && picks.size() < min; i++) {
                if ("true".equalsIgnoreCase(selectable[i])) {
                    picks.add("    " + i);
                }
            }
            if (!picks.isEmpty()) {
                if (dark) {
                    scn.DSDecided(String.join(",", picks));
                } else {
                    scn.LSDecided(String.join(",", picks));
                }
                return true;
            }
        }
        return false;
    }
}
