package com.gempukku.swccgo.cards.set201.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.BooleanSupplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Imperial Enforcement (Virtual Set 1). The current PC card image (20 December 2022, Holotable 212764a) reads "Once per
 * turn during battle, if you just drew a character for destiny, may take that card into hand to cancel and redraw that
 * destiny"; the 12 December 2022 image (Holotable 12d0bca) had no "during battle". Bill (12 Nov 2026): the wording on
 * the card takes precedence. Bare 201_30 = the current image; 201_30o1 = the 12 December 2022 text; 201_30o = the
 * original 12 July 2020 text (also printed on the E1-marked image of 3 October 2022).
 */
public class Card_201_030_Tests {
    static final String CURRENT_TEXT = "Deploy on table. You may initiate battles for free. Once per turn during battle, if you just drew a character for destiny, may take that card into hand to cancel and redraw that destiny. Canceled if your non-Imperial character (or starship) on table. [Immune to Alter.]";
    static final String DEC_12_TEXT = "Deploy on table. You may initiate battles for free. Once per turn, if you just drew a character for destiny, may take that card into hand to cancel and redraw that destiny. Canceled if your non-Imperial character (or starship) on table. [Immune to Alter.]";
    static final String ORIGINAL_TEXT = "Deploy on table. Lost if your non-Imperial character (or starship) on table. Unless you occupy fewer locations than opponent: if you win a battle, retrieve an Imperial; and, during battle, may take an Imperial just drawn for destiny into hand to cancel and redraw that destiny. [Immune to Alter.]";
    static final String REDRAW = "Take destiny card into hand and cause re-draw";

    protected VirtualTableScenario GetScenario(String enforcementId) {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rebel", "1_28");      // Rebel Trooper
                }},
                new HashMap<>() {{
                    put("enforcement", enforcementId);
                    put("trooper", "1_168");   // Darth Vader (Imperial, ability 6: draws battle destiny)
                    put("drawn", "1_194");     // Stormtrooper, the character drawn for destiny
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                "anything_goes"
        );
    }

    /** Test-only Dark Side Effect whose game text draws one destiny outside battle. */
    private static AbstractNormalEffect destinyDrawer() {
        return new AbstractNormalEffect(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Test Destiny Drawer", Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
            @Override
            protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
                TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Draw destiny");
                action.appendEffect(new DrawDestinyEffect(action, playerId) {
                    @Override
                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                    }
                });
                return Collections.singletonList(action);
            }
        };
    }

    private static void passUntil(VirtualTableScenario scn, BooleanSupplier done) {
        for (int i = 0; i < 40; ++i) {
            if (done.getAsBoolean())
                return;
            if (scn.LSDecisionAvailable("battle destiny?"))
                scn.LSChooseNo();
            else if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
            else if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else
                return;
        }
    }

    private static boolean dsHasRedraw(VirtualTableScenario scn) {
        return scn.DSAnyDecisionsAvailable() && !scn.GetDSAvailableActions().isEmpty()
                && scn.DSCardActionAvailable(scn.GetDSCard("enforcement"), REDRAW);
    }

    /** Dark Side battles at the Light Side site and draws the Stormtrooper as battle destiny. */
    private static boolean redrawOfferedDuringBattle(VirtualTableScenario scn) {
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("enforcement"));
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"), scn.GetLSCard("rebel"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(6);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("drawn"));
        scn.DSInitiateBattle(site);
        scn.SkipToPowerSegment();
        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();
        final boolean[] offered = {false};
        passUntil(scn, () -> {
            if (dsHasRedraw(scn))
                offered[0] = true;
            return offered[0] || scn.DSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER") || scn.LSDecisionAvailable("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        });
        return offered[0];
    }

    /** Dark Side draws the Stormtrooper for destiny in its deploy phase (no battle). */
    private static boolean redrawOfferedOutsideBattle(VirtualTableScenario scn) {
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("enforcement"));
        var drawer = MissionTestSupport.putOnTable(scn, 9301, scn.DS, destinyDrawer());
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("drawn"));
        assertTrue(scn.DSCardActionAvailable(drawer, "Draw destiny"));
        scn.DSUseCardAction(drawer, "Draw destiny");
        final boolean[] offered = {false};
        passUntil(scn, () -> {
            if (dsHasRedraw(scn))
                offered[0] = true;
            return offered[0] || scn.DSDecisionAvailable("Choose Deploy action or Pass");
        });
        assertFalse(scn.gameState().isDuringBattle());
        return offered[0];
    }

    @Test
    public void ImperialEnforcementBareCardHasTheCurrentImageText() {
        var scn = GetScenario("201_30");
        var card = scn.GetDSCard("enforcement").getBlueprint();
        assertEquals(Title.Imperial_Enforcement, card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(CURRENT_TEXT, card.getGameText());
        assertNull(card.getTextState());
    }

    @Test
    public void ImperialEnforcementRedrawIsUsableDuringBattle() {
        var scn = GetScenario("201_30");
        assertTrue(redrawOfferedDuringBattle(scn));
        scn.DSUseCardAction(scn.GetDSCard("enforcement"), REDRAW);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, scn.GetDSCard("drawn").getZone());
    }

    @Test
    public void ImperialEnforcementRedrawIsNotUsableOutsideBattle() {
        var scn = GetScenario("201_30");
        assertFalse(redrawOfferedOutsideBattle(scn));
    }

    @Test
    public void ImperialEnforcementDecember12TextState() {
        var scn = GetScenario("201_30o1");
        var card = scn.GetDSCard("enforcement").getBlueprint();
        assertEquals(Title.Imperial_Enforcement, card.getTitle());
        assertEquals(DEC_12_TEXT, card.getGameText());
        TextState state = card.getTextState();
        assertEquals("201_30", state.currentId());
        assertEquals("PC-E2", state.label());
        assertEquals(LocalDate.of(2022, 12, 12), state.printedOn());
        assertEquals("Imperial Enforcement [PC-E2]", GameUtils.getFullName(card));
    }

    @Test
    public void ImperialEnforcementDecember12TextRedrawsOutsideBattleToo() {
        var scn = GetScenario("201_30o1");
        assertTrue(redrawOfferedOutsideBattle(scn));
    }

    @Test
    public void ImperialEnforcementOriginalTextState() {
        var scn = GetScenario("201_30o");
        var card = scn.GetDSCard("enforcement").getBlueprint();
        assertEquals(Title.Imperial_Enforcement, card.getTitle());
        assertEquals(ORIGINAL_TEXT, card.getGameText());
        TextState state = card.getTextState();
        assertEquals("201_30", state.currentId());
        assertEquals("Printed", state.label());
        assertEquals(LocalDate.of(2020, 7, 12), state.printedOn());
    }

    @Test
    public void ImperialEnforcementOriginalTextRedrawsAnImperialDuringBattleOnly() {
        assertTrue(redrawOfferedDuringBattle(GetScenario("201_30o")));
        assertFalse(redrawOfferedOutsideBattle(GetScenario("201_30o")));
    }
}
