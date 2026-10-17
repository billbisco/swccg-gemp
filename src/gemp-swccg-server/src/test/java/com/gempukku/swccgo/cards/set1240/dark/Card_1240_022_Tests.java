package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.common.Zone;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1240_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("crush", "1240_22");
                    put("evax", "1240_12");
                    put("motti", "1_164");
                    put("room", "1240_51");
                    put("site", "1_291");
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
    public void CrushTheRebellionStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("crush").getBlueprint();

        assertEquals("Crush The Rebellion", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.PREMIUM);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CrushTheRebellionSubtractsFromCharacterAtASite() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var crush = scn.GetDSCard("crush");
        var evax = scn.GetDSCard("evax");
        var motti = scn.GetDSCard("motti");
        var room = scn.GetDSCard("room");
        var site = scn.GetDSCard("site");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(room);
        scn.MoveLocationToTable(site);
        scn.MoveCardsToDSSideOfTable(crush);
        scn.MoveCardsToLocation(room, evax);
        scn.MoveCardsToLocation(site, luke);
        scn.StackCardsOn(crush, motti);
        scn.ApplyAdHocModifier(new KeywordModifier(crush, motti, Keyword.GENERAL));
        scn.SkipToDSTurn(com.gempukku.swccgo.common.Phase.CONTROL);

        assertEquals(Zone.STACKED, motti.getZone());
        int forfeitBefore = scn.GetForfeit(luke);
        assertTrue(scn.DSCardActionAvailable(crush, "Subtract influence from a character"));
        scn.DSUseCardAction(crush, "Subtract influence from a character");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(forfeitBefore - 1, scn.GetForfeit(luke));
    }
}
