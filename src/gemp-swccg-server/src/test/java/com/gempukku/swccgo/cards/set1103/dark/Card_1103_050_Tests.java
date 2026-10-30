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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1103_050_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("undercover", "1103_50");
                    put("spy", "1_163");
                    put("informant", "2_134");
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
        var card = scn.GetDSCard("undercover").getBlueprint();

        assertEquals("Undercover", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
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
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void UndercoverPutsSpyUndercover() {
        var scn = GetScenario();
        var undercover = scn.GetDSCard("undercover");
        var spy = scn.GetDSCard("spy");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, spy);
        scn.MoveCardsToDSHand(undercover);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(3);

        assertTrue(scn.DSCardPlayAvailable(undercover));
        scn.DSPlayCard(undercover);
        if (scn.DSHasCardChoiceAvailable(spy)) {
            scn.DSChooseCard(spy);
        }
        scn.PassAllResponses();

        assertEquals(spy, undercover.getAttachedTo());
        assertTrue(spy.isUndercover());
    }

    @Test
    public void UndercoverTakesInformantDuringControlPhase() {
        var scn = GetScenario();
        var undercover = scn.GetDSCard("undercover");
        var spy = scn.GetDSCard("spy");
        var informant = scn.GetDSCard("informant");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, spy);
        scn.AttachCardsTo(spy, undercover);
        scn.MakeCardGoUndercover(spy);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(informant);

        assertTrue(scn.DSCardActionAvailable(undercover, "Take Informant into hand"));
        scn.DSUseCardAction(undercover, "Take Informant into hand");
        scn.PassAllResponses();
        scn.DSChooseCard(informant);
        scn.PassAllResponses();

        assertEquals(Zone.HAND, informant.getZone());
    }
}
