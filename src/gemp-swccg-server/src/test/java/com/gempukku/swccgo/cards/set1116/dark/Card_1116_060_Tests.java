package com.gempukku.swccgo.cards.set1116.dark;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1116_060_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("scanner", "1210_138");
                }},
                new HashMap<>() {{
                    put("necklace", "1116_60");
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
    public void OrganasCeremonialNecklaceStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("necklace").getBlueprint();

        assertEquals("Organa's Ceremonial Necklace", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
}});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_16, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void OrganasCeremonialNecklaceDeploysOnTable() {
        var scn = GetScenario();
        var necklace = scn.GetDSCard("necklace");

        scn.StartGame();
        scn.MoveCardsToDSHand(necklace);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(necklace));
        scn.DSPlayCard(necklace);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, necklace);
    }

    @Test
    public void OrganasCeremonialNecklaceBlocksOpponentPeekWhenHandHasFewerThanThirteenCards() {
        var scn = GetScenario();
        var necklace = scn.GetDSCard("necklace");
        var scanner = scn.GetLSCard("scanner");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(necklace);
        scn.MoveCardsToLSSideOfTable(scanner);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(scn.LSCardActionAvailable(scanner, "Peek"));
    }
}
