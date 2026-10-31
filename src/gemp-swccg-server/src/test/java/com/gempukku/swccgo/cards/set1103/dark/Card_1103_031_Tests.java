package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
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

public class Card_1103_031_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("han", "1_11");
                    put("blaster", "1_154");
                    put("colo", "13_11");
                }},
                new HashMap<>() {{
                    put("forces", "1103_31");
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
    public void DarkForcesStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("forces").getBlueprint();

        assertEquals("Dark Forces", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PREMIUM);
            add(Icon.EFFECT);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DarkForcesDeploysOnTable() {
        var scn = GetScenario();
        var forces = scn.GetDSCard("forces");

        scn.StartGame();
        scn.MoveCardsToDSHand(forces);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(forces));
        scn.DSPlayCard(forces);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, forces);
    }

    @Test
    public void DarkForcesCancelsAndRedrawsWeaponDestinyTargetingYourDarkJedi() {
        var scn = GetScenario();
        var forces = scn.GetDSCard("forces");
        var vader = scn.GetDSCard("vader");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(forces);
        scn.MoveCardsToLocation(site, vader, han);
        scn.AttachCardsTo(han, blaster);
        scn.EnsureLSForcePile(1);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        if (scn.AwaitingDSWeaponsSegmentActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.PrepareLSDestiny(1);
        scn.LSUseCardAction(blaster, "Fire");
        if (scn.LSHasCardChoiceAvailable(vader)) {
            scn.LSChooseCard(vader);
        }
        scn.PassResponses("Fire ");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("DESTINY_DRAWN");

        boolean offered = false;
        for (int i = 0; i < 16; i++) {
            if (dsHasCardActions(scn) && scn.DSCardActionAvailable(forces, "Cancel and redraw")) {
                scn.DSUseCardAction(forces, "Cancel and redraw");
                offered = true;
                break;
            }
            if (scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null) {
                var actions = scn.DSGetADParamAsList("actionText");
                if (actions != null) {
                    for (String a : actions) {
                        if (a != null && a.toLowerCase().contains("redraw")) {
                            scn.DSChooseAction(a);
                            offered = true;
                            break;
                        }
                    }
                }
            }
            if (offered) {
                break;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(offered);
        scn.PassAllResponses();
    }

    @Test
    public void DarkForcesExchangesOpponentsLostPileCardWithACardOnTheirColoClawFish() {
        var scn = GetScenario();
        var forces = scn.GetDSCard("forces");
        var colo = scn.GetLSCard("colo");
        var stacked = scn.GetLSFiller(1);
        var lost = scn.GetLSFiller(2);

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(forces);
        scn.MoveCardsToLSSideOfTable(colo);
        scn.StackCardsOn(colo, stacked);
        scn.MoveCardsToTopOfLSLostPile(lost);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(forces, "Exchange Lost Pile card with Colo Claw Fish"));
        scn.DSUseCardAction(forces, "Exchange Lost Pile card with Colo Claw Fish");
        if (scn.DSHasCardChoiceAvailable(lost)) {
            scn.DSChooseCard(lost);
        }
        if (scn.DSHasCardChoiceAvailable(stacked)) {
            scn.DSChooseCard(stacked);
        }
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, stacked);
        assertTrue(scn.gameState().getStackedCards(colo).contains(lost));
    }

    private boolean dsHasCardActions(VirtualTableScenario scn) {
        return scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null;
    }
}
