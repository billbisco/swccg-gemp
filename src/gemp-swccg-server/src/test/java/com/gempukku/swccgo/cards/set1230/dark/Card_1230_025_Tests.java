package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1230_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("cane", "1230_25");
                }},
                10, 10,
                StartingSetup.LSStartingLocation("1_125"),
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void CaneAdissStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("cane").getBlueprint();
        assertEquals("Cane Adiss", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_LOCATION);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CaneAdissDeploysAfterOpponentForceDrainsAtNonBattlegroundSite() {
        var scn = GetScenario();
        var cane = scn.GetDSCard("cane");
        var luke = scn.GetLSCard("luke");
        var trash = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(trash, luke);
        scn.MoveCardsToDSHand(cane);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(trash));
        scn.LSForceDrainAt(trash);
        assertTrue(scn.DSCardPlayAvailable(cane));
    }
}
