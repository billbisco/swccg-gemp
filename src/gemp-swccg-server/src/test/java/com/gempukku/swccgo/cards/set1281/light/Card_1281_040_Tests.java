package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_040_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("street", "1281_40");
                    put("rest", "1281_39");
                    put("narshaddaa", "200_58");
                    put("lando", "601_50");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("boba", "5_91");
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
    public void NarShaddaaUndercityStreetStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("street").getBlueprint();

        assertEquals("Nar Shaddaa: Undercity Street", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.UNDERGROUND);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void NarShaddaaUndercityStreetScoundrelsMoveUsingLandspeedForFree() {
        var scn = GetScenario();
        var street = scn.GetLSCard("street");
        var rest = scn.GetLSCard("rest");
        var narshaddaa = scn.GetLSCard("narshaddaa");
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");

        scn.StartGame();
        scn.MoveLocationToTable(narshaddaa);
        scn.MoveLocationToTable(rest);
        scn.MoveLocationToTable(street);
        scn.MoveCardsToLocation(rest, lando, leia);
        scn.SkipToPhase(Phase.CONTROL);

        float landoCost = scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.gameState(), lando, rest, street, false, 0);
        float leiaCost = scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.gameState(), leia, rest, street, false, 0);
        assertEquals(0, landoCost, scn.epsilon);
        assertEquals(1, leiaCost, scn.epsilon);
    }

    @Test
    public void NarShaddaaUndercityStreetDarkForceDrainIsMinusOneUnlessBountyHunterPresent() {
        var scn = GetScenario();
        var street = scn.GetLSCard("street");
        var narshaddaa = scn.GetLSCard("narshaddaa");
        var boba = scn.GetDSCard("boba");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(narshaddaa);
        scn.MoveLocationToTable(street);
        scn.MoveCardsToLocation(street, trooper);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertEquals(1, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), street, scn.DS), scn.epsilon);

        scn.MoveCardsToLocation(street, boba);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(2, scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), street, scn.DS), scn.epsilon);
    }
}
