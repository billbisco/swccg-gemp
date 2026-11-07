package com.gempukku.swccgo.cards.set1113.light;

import com.gempukku.swccgo.cards.legacy.EyesInTheDarkDejarikTestBase;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Eyes In The Dark (Original Virtual Set 13). Same text as the Legacy printing (1240_85) except it names
 * [Virtual Set 9] Wipe Them Out, All Of Them; the dejarik clauses are covered by EyesInTheDarkDejarikTestBase.
 */
public class Card_1113_006_Tests extends EyesInTheDarkDejarikTestBase {
    @Override
    protected String eyesBlueprintId() {
        return "1113_6";
    }

    @Test
    public void EyesInTheDarkOriginalVirtualTitleAndSet() {
        var scn = GetDejarikScenario();
        var card = scn.GetLSCard("eyes").getBlueprint();
        assertEquals("Eyes In The Dark", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_13, card.getExpansionSet());
    }

    @Test
    public void EyesInTheDarkOriginalVirtualIgnoresVirtualSet9WipeThemOut() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("eyes", "1113_6");
                    put("holo", "2_63");
                }},
                new HashMap<>() {{
                    put("wipe9", "1109_50");
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
        var eyes = scn.GetLSCard("eyes");
        var holo = scn.GetLSCard("holo");
        var wipe9 = scn.GetDSCard("wipe9");

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        scn.AttachCardsTo(holo, eyes);
        scn.AttachCardsTo(holo, wipe9);
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.CANCEL_GAME_TEXT, wipe9).isEmpty());
    }
}
