package com.gempukku.swccgo.cards.set1210.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_142_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("undercover", "1210_142");
                    put("spy", "1_020");
                    put("sabotage", "2_056");
                }},
                new HashMap<>() {{
                    put("ig88", "1_179");
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
    public void UndercoverStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("undercover").getBlueprint();

        assertEquals("Undercover", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void UndercoverPutsSpyUndercover() {
        var scn = GetScenario();
        var undercover = scn.GetLSCard("undercover");
        var spy = scn.GetLSCard("spy");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, spy);
        scn.MoveCardsToLSHand(undercover);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(undercover) || scn.LSDeployAvailable(undercover));
        scn.LSPlayCard(undercover);
        if (scn.LSHasCardChoiceAvailable(spy)) {
            scn.LSChooseCard(spy);
        }
        scn.PassAllResponses();

        assertEquals(spy, undercover.getAttachedTo());
        assertTrue(spy.isUndercover());
    }

    @Test
    public void UndercoverTakesSabotageDuringControlPhase() {
        var scn = GetScenario();
        var undercover = scn.GetLSCard("undercover");
        var spy = scn.GetLSCard("spy");
        var sabotage = scn.GetLSCard("sabotage");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, spy);
        scn.AttachCardsTo(spy, undercover);
        scn.MakeCardGoUndercover(spy);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(sabotage);

        assertTrue(scn.LSCardActionAvailable(undercover, "Take Sabotage into hand"));
        scn.LSUseCardAction(undercover, "Take Sabotage into hand");
        scn.PassAllResponses();
        scn.LSChooseCard(sabotage);
        scn.PassAllResponses();

        assertEquals(Zone.HAND, sabotage.getZone());
    }
}
