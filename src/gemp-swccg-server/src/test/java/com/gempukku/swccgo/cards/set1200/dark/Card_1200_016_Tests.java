package com.gempukku.swccgo.cards.set1200.dark;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1200_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("leia", "13_29");
                    put("amidala", "12_22");
                    put("kessel", "1_126");
                }},
                new HashMap<>() {{
                    put("afterher", "1200_16");
                    put("political", "12_137");
                    put("shortage", "1210_31");
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
    public void AfterHerVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("afterher").getBlueprint();

        assertEquals("After Her!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.THEED_PALACE);
            add(Icon.DEFENSIVE_SHIELD);
            add(Icon.LEGACY_BLOCK_D);
        }});
        assertEquals(ExpansionSet.VIRTUAL_SHIELDS, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AfterHerVDoesNotCancelReflections3LeiaGameTextAtASite() {
        var scn = GetScenario();
        var afterher = scn.GetDSCard("afterher");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(afterher);
        scn.MoveCardsToLocation(site, leia);
        AdvanceTableChanged(scn);
        assertFalse(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), leia));
    }

    @Test
    public void AfterHerVCancelsReflections3LeiaGameTextWhenNotPresentAtASite() {
        var scn = GetScenario();
        var afterher = scn.GetDSCard("afterher");
        var leia = scn.GetLSCard("leia");
        var kessel = scn.GetLSCard("kessel");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(afterher);
        scn.MoveLocationToTable(kessel);
        scn.MoveCardsToLocation(kessel, leia);
        AdvanceTableChanged(scn);
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), leia));
    }

    @Test
    public void AfterHerVEachAmidalaStackedOnAPoliticalEffectIsASenator() {
        var scn = GetScenario();
        var afterher = scn.GetDSCard("afterher");
        var amidala = scn.GetLSCard("amidala");
        var political = scn.GetDSCard("political");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(afterher);
        scn.MoveCardsToDSSideOfTable(political);
        scn.MoveCardsToLocation(site, amidala);
        assertFalse(scn.HasKeyword(amidala, Keyword.SENATOR));

        scn.StackCardsOn(political, amidala);
        assertTrue(scn.HasKeyword(amidala, Keyword.SENATOR));
    }

    @Test
    public void AfterHerVTakesVirtualBlock1AstromechShortageIntoHandOncePerGame() {
        var scn = GetScenario();
        var afterher = scn.GetDSCard("afterher");
        var shortage = scn.GetDSCard("shortage");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(afterher);
        scn.SkipToDSTurn(Phase.CONTROL);
        for (int i = 0; i < 12 && !scn.AwaitingDSControlPhaseActions(); i++) {
            if (scn.LSAnyDecisionsAvailable() && !scn.DSAnyDecisionsAvailable()) {
                scn.LSPass();
                continue;
            }
            if (scn.DSAnyDecisionsAvailable() && !scn.AwaitingDSControlPhaseActions()) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.MoveCardsToTopOfDSReserveDeck(shortage);

        assertTrue(scn.DSCardActionAvailable(afterher, "Astromech Shortage"));
        scn.DSUseCardAction(afterher, "Astromech Shortage");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(shortage);
        scn.PassAllResponses();
        assertInHand(shortage);

        scn.SkipToDSTurn(Phase.CONTROL);
        for (int i = 0; i < 12 && !scn.AwaitingDSControlPhaseActions(); i++) {
            if (scn.LSAnyDecisionsAvailable() && !scn.DSAnyDecisionsAvailable()) {
                scn.LSPass();
                continue;
            }
            if (scn.DSAnyDecisionsAvailable() && !scn.AwaitingDSControlPhaseActions()) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(scn.AwaitingDSControlPhaseActions());
        scn.MoveCardsToTopOfDSReserveDeck(shortage);
        assertFalse(scn.DSCardActionAvailable(afterher, "Astromech Shortage"));
    }

    private void AdvanceTableChanged(VirtualTableScenario scn) {
        if (scn.gameState().getCurrentPhase() == Phase.DEPLOY) {
            scn.SkipToPhase(Phase.BATTLE);
        } else {
            scn.SkipToPhase(Phase.DEPLOY);
        }
    }
}
