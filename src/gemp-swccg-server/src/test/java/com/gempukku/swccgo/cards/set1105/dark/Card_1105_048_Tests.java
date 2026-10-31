package com.gempukku.swccgo.cards.set1105.dark;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1105_048_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("fallen", "6_66");
                    put("fly", "8_51");
                    put("t47form", "7_107");
                    put("sense", "1_109");
                    put("db94", "1_129");
                    put("tatooine", "1_127");
                    put("ywing", "1_147");
                    put("rogue", "3_66");
                    put("ridge", "3_62");
                    put("saber", "9_90");
                }},
                new HashMap<>() {{
                    put("tea", "1105_48");
                    put("trooper", "1_194");
                    put("veers", "3_87");
                    put("garrison", "104_7");
                    put("blizzard", "3_154");
                    put("ice", "3_148");
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
    public void TakeEvasiveActionStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("tea").getBlueprint();

        assertEquals("Take Evasive Action", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
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
    public void TakeEvasiveActionUsedCancelsFallenPortalBeingPlayed() {
        var scn = GetScenario();
        var tea = scn.GetDSCard("tea");
        var fallen = scn.GetLSCard("fallen");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var db94 = scn.GetLSCard("db94");

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(db94, luke, trooper);
        scn.MoveCardsToLSHand(fallen);
        scn.MoveCardsToDSHand(tea);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(db94);
        assertTrue(scn.LSCardPlayAvailable(fallen));
        scn.LSPlayCard(fallen);
        finishFallenPortalTargeting(scn, trooper);
        passUntilDsCanPlay(scn, tea);
        assertTrue("TEA missing after Fallen Portal; ds=" + scn.DSGetDecision()
                        + " ls=" + scn.LSGetDecision()
                        + " actions=" + scn.GetDSAvailableActions(),
                dsHasCardActions(scn) && scn.DSCardPlayAvailable(tea));
        scn.DSPlayCard(tea);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, tea);
        assertTrue(fallen.getZone() == Zone.LOST_PILE || fallen.getZone() == Zone.TOP_OF_LOST_PILE
                || fallen.getZone() == Zone.HAND);
    }

    @Test
    public void TakeEvasiveActionUsedCancelsFlyCasualBeingPlayed() {
        var scn = GetScenario();
        var tea = scn.GetDSCard("tea");
        var fly = scn.GetLSCard("fly");
        var ywing = scn.GetLSCard("ywing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLSHand(fly, ywing);
        scn.MoveCardsToDSHand(tea);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue("Fly Casual deploy missing; actions=" + scn.GetLSAvailableActions(),
                scn.LSCardPlayAvailable(fly, "starship or pilot"));
        scn.LSPlayCard(fly, "starship or pilot");
        passUntilDsCanPlay(scn, tea);
        assertTrue("TEA missing after Fly Casual; ds=" + scn.DSGetDecision()
                        + " ls=" + scn.LSGetDecision()
                        + " actions=" + scn.GetDSAvailableActions(),
                dsHasCardActions(scn) && scn.DSCardPlayAvailable(tea));
        scn.DSPlayCard(tea);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, tea);
        assertTrue(fly.getZone() == Zone.LOST_PILE || fly.getZone() == Zone.TOP_OF_LOST_PILE
                || fly.getZone() == Zone.HAND);
    }

    @Test
    public void TakeEvasiveActionUsedCancelsT47BattleFormationCancelingForceDrain() {
        var scn = GetScenario();
        var tea = scn.GetDSCard("tea");
        var t47form = scn.GetLSCard("t47form");
        var rogue = scn.GetLSCard("rogue");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var ice = scn.GetDSCard("ice");
        var ridge = scn.GetLSCard("ridge");

        scn.StartGame();
        scn.MoveLocationToTable(ice);
        scn.MoveLocationToTable(ridge);
        scn.MoveCardsToLocation(ice, rogue, luke);
        scn.BoardAsPilot(rogue, luke);
        scn.MoveCardsToLocation(ridge, trooper);
        scn.MoveCardsToLSHand(t47form);
        scn.MoveCardsToDSHand(tea);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(ridge));
        scn.DSForceDrainAt(ridge);
        scn.DSPass();
        assertTrue(scn.LSCardPlayAvailable(t47form, "Cancel Force drain"));
        scn.LSPlayCard(t47form, "Cancel Force drain");
        passUntilDsCanPlay(scn, tea);
        assertTrue(dsHasCardActions(scn) && scn.DSCardPlayAvailable(tea));
        scn.DSPlayCard(tea);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, tea);
    }

    @Test
    public void TakeEvasiveActionUsedDoesNotCancelT47BattleFormationCancelingWalkerGarrison() {
        var scn = GetScenario();
        var tea = scn.GetDSCard("tea");
        var t47form = scn.GetLSCard("t47form");
        var veers = scn.GetDSCard("veers");
        var garrison = scn.GetDSCard("garrison");
        var blizzard = scn.GetDSCard("blizzard");
        var ice = scn.GetDSCard("ice");

        scn.StartGame();
        scn.MoveLocationToTable(ice);
        scn.MoveCardsToLocation(ice, veers);
        scn.MoveCardsToDSHand(garrison, tea);
        scn.MoveCardsToLSHand(t47form);
        scn.MoveCardsToTopOfDSReserveDeck(blizzard);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.EnsureDSForcePile(2);
        assertTrue("Walker Garrison missing; actions=" + scn.GetDSAvailableActions(),
                scn.DSCardPlayAvailable(garrison));
        scn.DSPlayCard(garrison);
        passUntilLsCanPlay(scn, t47form);
        assertTrue("T-47 missing vs Walker Garrison; ls=" + scn.LSGetDecision()
                        + " ds=" + scn.DSGetDecision()
                        + " actions=" + scn.GetLSAvailableActions(),
                lsHasCardActions(scn) && scn.LSCardPlayAvailable(t47form));
        scn.LSPlayCard(t47form);
        passUntilDsCanPlay(scn, tea);
        assertFalse("TEA must not cancel T-47 that is canceling Walker Garrison; actions="
                        + scn.GetDSAvailableActions(),
                dsHasCardActions(scn) && scn.DSCardPlayAvailable(tea));
        scn.PassAllResponses();
        assertInZone(Zone.HAND, tea);
    }

    @Test
    public void TakeEvasiveActionUsedIsImmuneToSense() {
        var scn = GetScenario();
        var tea = scn.GetDSCard("tea");
        var fallen = scn.GetLSCard("fallen");
        var sense = scn.GetLSCard("sense");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var db94 = scn.GetLSCard("db94");

        scn.StartGame();
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(db94, luke, trooper);
        scn.MoveCardsToLSHand(fallen, sense);
        scn.MoveCardsToDSHand(tea);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(db94);
        scn.LSPlayCard(fallen);
        finishFallenPortalTargeting(scn, trooper);
        passUntilDsCanPlay(scn, tea);
        assertTrue(dsHasCardActions(scn) && scn.DSCardPlayAvailable(tea));
        scn.DSPlayCard(tea);
        passUntilLsOptionalOnPlay(scn);
        if (lsHasCardActions(scn)) {
            assertFalse("Sense offered against immune TEA; actions=" + scn.GetLSAvailableActions(),
                    scn.LSCardPlayAvailable(sense));
        }
        assertEquals(Zone.HAND, sense.getZone());
    }

    @Test
    public void TakeEvasiveActionLostCancelsAndRedrawsOpponentWeaponDestiny() {
        var scn = GetScenario();
        var tea = scn.GetDSCard("tea");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.AttachCardsTo(luke, saber);
        scn.MoveCardsToDSHand(tea);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.LSCardActionAvailable(saber, "Fire"));
        scn.LSUseCardAction(saber, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(trooper));
        scn.LSChooseCard(trooper);
        scn.PrepareLSDestiny(1);
        scn.PrepareLSDestiny(5);
        scn.PassResponses("Fire ");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        passUntilTeaLostRedraw(scn);
        assertTrue("TEA LOST missing on weapon destiny; ds=" + scn.DSGetDecision()
                        + " ls=" + scn.LSGetDecision()
                        + " actions=" + scn.GetDSAvailableActions()
                        + " log=" + String.join(" | ", scn.gameState().getLastMessages()),
                teaLostRedrawAvailable(scn));
        scn.DSPlayCard(tea, "Cancel and redraw destiny");
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, tea);
    }

    @Test
    public void TakeEvasiveActionLostDoesNotCancelBattleDestiny() {
        var scn = GetScenario();
        var tea = scn.GetDSCard("tea");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.MoveCardsToDSHand(tea);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.LSPass();
        scn.DSPass();
        scn.SkipToPowerSegment();
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("battle destiny")) {
            scn.LSChooseYes();
        }
        else if (scn.DSDecisionAvailable("battle destiny")) {
            scn.DSChooseYes();
        }
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        passUntilTeaLostRedraw(scn);
        assertFalse("TEA LOST must not cancel battle destiny; actions=" + scn.GetDSAvailableActions(),
                teaLostRedrawAvailable(scn));
        assertInZone(Zone.HAND, tea);
    }

    private void finishFallenPortalTargeting(VirtualTableScenario scn, PhysicalCardImpl trooper) {
        for (int i = 0; i < 6; i++) {
            if (dsHasCardActions(scn) && scn.DSCardPlayAvailable(scn.GetDSCard("tea"))) {
                return;
            }
            try {
                if (scn.LSHasCardChoiceAvailable(trooper)) {
                    scn.LSChooseCard(trooper);
                    continue;
                }
            } catch (RuntimeException ignored) {
            }
            if (scn.LSChoiceAvailable("Done")) {
                scn.LSChoose("Done");
                continue;
            }
            break;
        }
    }

    private void passUntilDsCanPlay(VirtualTableScenario scn, PhysicalCardImpl card) {
        for (int i = 0; i < 16; i++) {
            if (dsHasCardActions(scn) && scn.DSCardPlayAvailable(card)) {
                return;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && !dsHasCardActions(scn)) {
                scn.DSPass();
                continue;
            }
            break;
        }
    }

    private void passUntilLsCanPlay(VirtualTableScenario scn, PhysicalCardImpl card) {
        for (int i = 0; i < 16; i++) {
            if (lsHasCardActions(scn) && scn.LSCardPlayAvailable(card)) {
                return;
            }
            if (scn.LSGetDecision() != null && !lsHasCardActions(scn)) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            break;
        }
    }

    private void passUntilLsOptionalOnPlay(VirtualTableScenario scn) {
        for (int i = 0; i < 12; i++) {
            if (lsHasCardActions(scn) || (scn.LSGetDecision() != null && scn.LSGetADParam("cardId") != null)) {
                return;
            }
            if (scn.LSGetDecision() != null) {
                return;
            }
            if (scn.DSGetDecision() != null && !dsHasCardActions(scn)) {
                scn.DSPass();
                continue;
            }
            break;
        }
    }

    private void passUntilTeaLostRedraw(VirtualTableScenario scn) {
        for (int i = 0; i < 24; i++) {
            if (teaLostRedrawAvailable(scn)) {
                return;
            }
            if (isPastDestinyWindow(scn.DSGetDecision()) || isPastDestinyWindow(scn.LSGetDecision())) {
                return;
            }
            if (scn.LSGetDecision() != null && !lsHasCardActions(scn)) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && !dsHasCardActions(scn)) {
                scn.DSPass();
                continue;
            }
            if (dsHasCardActions(scn)) {
                return;
            }
            break;
        }
    }

    private boolean isPastDestinyWindow(Object decision) {
        if (decision == null) {
            return false;
        }
        String name = decision.getClass().getName();
        return name.contains("BattleDamage") || name.contains("LoseOrForfeit");
    }

    private boolean teaLostRedrawAvailable(VirtualTableScenario scn) {
        if (!dsHasCardActions(scn)) {
            return false;
        }
        return scn.DSCardPlayAvailable(scn.GetDSCard("tea"), "Cancel and redraw destiny");
    }

    private boolean dsHasCardActions(VirtualTableScenario scn) {
        return hasCardActions(scn.DSGetDecision(), scn.DSGetADParam("cardId"), scn.DSGetADParam("actionText"));
    }

    private boolean lsHasCardActions(VirtualTableScenario scn) {
        return hasCardActions(scn.LSGetDecision(), scn.LSGetADParam("cardId"), scn.LSGetADParam("actionText"));
    }

    private boolean hasCardActions(Object decision, String[] cardIds, String[] actionTexts) {
        return decision != null && cardIds != null && actionTexts != null && cardIds.length > 0;
    }
}
