package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_032_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                }},
                new HashMap<>() {{
                    put("quietly", "1240_32");
                    put("aurra", "11_51");
                    put("bossk", "110_5");
                    put("cad", "203_24");
                    put("evazan", "1_172");
                    put("rodian", "2_104");
                }},
                10,
                10,
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
    public void QuietlyObservingVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("quietly").getBlueprint();

        assertEquals(Title.Quietly_Observing, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardCategory.EFFECT, card.getCardCategory());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.EFFECT);
            add(Icon.TATOOINE);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void QuietlyObservingMakesAurraBosskAndCadDestinyPlusTwo() {
        var scn = GetScenario();
        var quietly = scn.GetDSCard("quietly");
        var aurra = scn.GetDSCard("aurra");
        var bossk = scn.GetDSCard("bossk");
        var cad = scn.GetDSCard("cad");
        var evazan = scn.GetDSCard("evazan");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aurra, bossk, cad, evazan);
        int aurraBase = scn.GetDestiny(aurra);
        int bosskBase = scn.GetDestiny(bossk);
        int cadBase = scn.GetDestiny(cad);
        int evazanBase = scn.GetDestiny(evazan);

        scn.MoveCardsToSideOfTable(quietly);
        scn.SkipToPhase(Phase.DEPLOY);

        assertEquals(aurraBase + 2, scn.GetDestiny(aurra));
        assertEquals(bosskBase + 2, scn.GetDestiny(bossk));
        assertEquals(cadBase + 2, scn.GetDestiny(cad));
        assertEquals(evazanBase, scn.GetDestiny(evazan));
    }

    @Test
    public void QuietlyObservingOncePerGameRevealedAliensAreAssassinsAndBlackSunAgents() {
        var scn = GetScenario();
        var quietly = scn.GetDSCard("quietly");
        var evazan = scn.GetDSCard("evazan");
        var rodian = scn.GetDSCard("rodian");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToSideOfTable(quietly);
        scn.MoveCardsToHand(evazan, rodian);
        scn.SkipToPhase(Phase.DEPLOY);

        assertFalse(scn.HasKeyword(evazan, Keyword.ASSASSIN));
        assertTrue(scn.DSCardActionAvailable(quietly, "Reveal up to two unique"));
        scn.DSUseCardAction(quietly, "Reveal up to two unique");
        scn.DSChoose("1");
        // Evazan is the only unique alien in hand (the Rodian is not unique), so he is chosen automatically.
        if (scn.DSHasCardChoiceAvailable(evazan)) {
            assertFalse(scn.DSHasCardChoiceAvailable(rodian));
            scn.DSChooseCard(evazan);
        }
        assertTrue(scn.DSDecisionAvailable("Reveal another alien from Reserve Deck"));
        scn.DSChooseNo();
        scn.PassAllResponses();

        scn.MoveCardsToLocation(site, evazan, rodian);
        assertTrue(scn.HasKeyword(evazan, Keyword.ASSASSIN));
        assertTrue(scn.HasKeyword(evazan, Keyword.BLACK_SUN_AGENT));
        assertFalse(scn.HasKeyword(rodian, Keyword.BLACK_SUN_AGENT)); // not revealed

        Block4DarkTestSupport.advanceUntil(scn, scn::AwaitingDSDeployPhaseActions, 10);
        assertFalse(scn.DSCardActionAvailable(quietly, "Reveal up to two unique"));
    }
}
