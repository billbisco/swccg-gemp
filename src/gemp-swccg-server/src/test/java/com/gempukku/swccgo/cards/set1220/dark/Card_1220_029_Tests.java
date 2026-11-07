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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_029_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("despair", "1220_29");
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
    public void DespairStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("despair").getBlueprint();

        assertEquals("Despair", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DespairDeploysOnTable() {
        var scn = GetScenario();
        var despair = scn.GetDSCard("despair");

        scn.StartGame();
        scn.MoveCardsToDSHand(despair);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(despair));
        scn.DSDeployCard(despair);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, despair);
    }

    /** Despair on table; Vader escorts Luke at the Dark site (frozen if asked); Han is at the Light site. */
    private VirtualTableScenario WithCaptive(boolean frozen) {
        var scn = GetScenario();
        scn.StartGame();
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("despair"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("han"));
        scn.CaptureCardWith(vader, luke);
        if (frozen) scn.FreezeCard(luke);
        return scn;
    }

    private boolean HanMayNotMove(VirtualTableScenario scn) {
        return scn.game().getModifiersQuerying().mayNotMove(scn.gameState(), scn.GetLSCard("han"));
    }

    @Test
    public void FrozenCaptiveYouHoldStopsOpponentsCardsMovingDuringYourBattlePhase() {
        var scn = WithCaptive(true);
        assertTrue(scn.GetLSCard("luke").isFrozen());
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(HanMayNotMove(scn));
    }

    @Test
    public void FrozenCaptiveDoesNotStopMovementOutsideYourBattlePhase() {
        var scn = WithCaptive(true);
        scn.SkipToDSTurn(Phase.MOVE);
        assertFalse(HanMayNotMove(scn));
        scn.SkipToLSTurn(Phase.BATTLE);
        assertFalse(HanMayNotMove(scn));
    }

    @Test
    public void CaptiveThatIsNotFrozenDoesNotStopMovement() {
        var scn = WithCaptive(false);
        assertFalse(scn.GetLSCard("luke").isFrozen());
        scn.SkipToDSTurn(Phase.BATTLE);
        assertFalse(HanMayNotMove(scn));
    }
}
