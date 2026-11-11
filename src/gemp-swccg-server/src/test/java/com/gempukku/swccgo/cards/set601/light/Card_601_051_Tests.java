package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.cards.set2261.light.Card2261_097;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/** Let The Wookiee Win (V) (1261_97, class 601_51): the PC card-list text (PC-E1) is the current text. */
public class Card_601_051_Tests {
    @Test
    public void CurrentTextIsThePCCardListText() {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("c", "1261_97", "effect", "1_66", "alterImmune", "1261_82")), new HashMap<>(), 10, 10,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        var bp = scn.GetLSCard("c").getBlueprint();
        assertEquals("USED: Reveal the top 3 cards of Reserve Deck; place one in hand, and the others on bottom of Used Pile (in any order). STARTING: If your starting location was a non-[Special Edition] location, ▼ an [Immune to Alter] Effect and any number of <> sites with < 3 total [Light Side Force]. Place this Interrupt in hand.", bp.getGameText());
        assertTrue(bp.getGameText().contains("any number of"));
        assertNull(bp.getLegacyVersion());
    }

    @Test
    public void StartingDeploysAnyNumberOfSitesAndAnyAlterImmuneEffectPerTheMasterList() {
        // Master List (2013, current): "an [Immune to Alter] Effect and any number of <> sites"; the card list (2261_1097) says "an always [Immune to Alter] Effect and two <> sites".
        assertEquals(Integer.MAX_VALUE, new Card601_051().maxStartingSites());
        assertEquals(2, ((Card601_051) new com.gempukku.swccgo.cards.set2261.light.Card2261_1097()).maxStartingSites());
    }
}
