package com.gempukku.swccgo.cards.set1200.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1200_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("professor", "1200_10");
                    put("wait", "3_44");
                    put("alter", "1_71");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void TheProfessorVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("professor").getBlueprint();

        assertEquals("The Professor", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DAGOBAH);
            add(Icon.DEFENSIVE_SHIELD);
            add(Icon.LEGACY_BLOCK_D);
        }});
        assertEquals(ExpansionSet.VIRTUAL_SHIELDS, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TheProfessorVItCanWaitMayNotBePlayed() {
        var scn = GetScenario();
        var professor = scn.GetLSCard("professor");
        var wait = scn.GetLSCard("wait");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(professor);
        scn.MoveCardsToLSHand(wait);
        scn.MoveCardsToDSHand(trooper);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(trooper));
        scn.DSDeployCard(trooper);
        scn.DSChooseCard(site);
        scn.PassAllResponses();

        assertFalse(scn.LSCardPlayAvailable(wait));
    }

    @Test
    public void TheProfessorVMayNotBeCanceled() {
        var scn = GetScenario();
        var professor = scn.GetLSCard("professor");
        var alter = scn.GetLSCard("alter");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(professor);
        scn.MoveCardsToLSHand(alter);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(scn.LSCardPlayAvailable(alter));
    }
}
