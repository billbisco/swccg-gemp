package com.gempukku.swccgo.cards.set1116.dark;

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

public class Card_1116_052_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("missiles", "1116_52");
                    put("tie", "1_304");
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
    public void StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("missiles").getBlueprint();
        assertEquals("Concussion Missiles", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.WEAPON); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.MISSILE);
            add(Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.WEAPON);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_16, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Gameplay() {
        var scn = GetScenario();
        var missiles = scn.GetDSCard("missiles");
        var tie = scn.GetDSCard("tie");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, tie);
        scn.MoveCardsToDSHand(missiles);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(missiles));
        scn.DSPlayCard(missiles);
        scn.DSChooseCard(tie);
        scn.PassAllResponses();
        assertEquals(tie, missiles.getAttachedTo());
    }
}
