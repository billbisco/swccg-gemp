package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_008_Tests {
    @Test
    public void printedArmoredRoyalGuardGetsActualImmunityToAttritionLessThanThree() {
        var scn = new VirtualTableScenario(new HashMap<>(), new HashMap<>(Map.of("jax", "1261_8")),
                12, 12, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        var jax = scn.GetDSCard("jax");
        var bp = jax.getBlueprint();
        assertEquals("Carnor Jax, Royal Guard", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(2, bp.getDestiny(), scn.epsilon);
        assertEquals(4, bp.getDeployCost(), scn.epsilon);
        assertEquals(5, bp.getPower(), scn.epsilon);
        assertEquals(2, bp.getAbility(), scn.epsilon);
        assertEquals(3, bp.getArmor(), scn.epsilon);
        assertEquals(5, bp.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.IMPERIAL)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.IMPERIAL, Icon.WARRIOR,
                Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>(List.of(Keyword.ROYAL_GUARD)));
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), jax);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), jax),
                scn.epsilon);
    }
}
