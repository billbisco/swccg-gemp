package com.gempukku.swccgo.cards.set1292.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Card_1292_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trooper", "1_28");
                }},
                new HashMap<>() {{
                    put("chamber", "1292_4");
                    put("tatooine", "1_289");
                    put("executor", "4_167");
                    put("visage", "4_135");
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
    public void ExecutorMeditationChamberVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("chamber").getBlueprint();

        assertEquals("Executor: Meditation Chamber", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertNull(card.getLocationDarkSideGameText());
        assertEquals("If Vader controls a battleground site, Force loss from [Dagobah] holograms may not be reduced.", card.getLocationLightSideGameText());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DARK_FORCE);
            add(Icon.DAGOBAH);
            add(Icon.INTERIOR_SITE);
            add(Icon.STARSHIP_SITE);
            add(Icon.MOBILE);
            add(Icon.SCOMP_LINK);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(0, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void setUpVisage(VirtualTableScenario scn, boolean vaderControlsSite) {
        var chamber = scn.GetDSCard("chamber");
        var tatooine = scn.GetDSCard("tatooine");
        var executor = scn.GetDSCard("executor");
        var visage = scn.GetDSCard("visage");
        var vader = scn.GetDSCard("vader");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, executor);
        scn.MoveLocationToTable(chamber);
        if (vaderControlsSite) {
            scn.MoveCardsToLocation(dsSite, vader);
        }
        scn.AttachCardsTo(dsSite, visage);
        // Light Side's Force loss is cut in half (round down): 1 becomes 0 unless it may not be reduced
        scn.ApplyAdHocModifier(new SpecialFlagModifier(visage, ModifierFlag.HALVE_AND_ROUND_DOWN_FORCE_LOSS, scn.LS));
    }

    private void passToEndOfDSTurn(VirtualTableScenario scn) {
        scn.SkipToPhase(Phase.DRAW);
        for (int i = 0; i < 40 && !scn.GetCurrentPlayer().equals(scn.LS); i++) {
            if (scn.AwaitingDSForceLossPayment()) {
                scn.DSPayForceLossFromReserveDeck();
            }
            else if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayForceLossFromReserveDeck();
            }
            else {
                scn.PassResponses();
            }
        }
        assertEquals(scn.LS, scn.GetCurrentPlayer());
    }

    @Test
    public void ExecutorMeditationChamberVForceLossFromHologramMayNotBeReducedWhileVaderControlsBattleground() {
        var scn = GetScenario();
        setUpVisage(scn, true);
        int lifeForce = scn.GetLSLifeForceRemaining();

        passToEndOfDSTurn(scn);
        assertEquals(lifeForce - 1, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void ExecutorMeditationChamberVForceLossFromHologramMayBeReducedWithoutVader() {
        var scn = GetScenario();
        setUpVisage(scn, false);
        int lifeForce = scn.GetLSLifeForceRemaining();

        passToEndOfDSTurn(scn);
        assertEquals(lifeForce, scn.GetLSLifeForceRemaining());
    }
}
