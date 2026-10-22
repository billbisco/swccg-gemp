package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.common.CardSubtype;
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

public class Card_1291_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("cody", "200_6");
                }},
                new HashMap<>() {{
                    put("muunilinst", "1291_20");
                    put("harnaidan", "1291_22");
                    put("atst", "3_156");
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
    public void MuunilinstCityOfHarnaidanStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("harnaidan").getBlueprint();

        assertEquals("Muunilinst: City Of Harnaidan", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.INTERIOR_SITE);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MuunilinstCityOfHarnaidanCombatVehiclesHereArePowerPlus1() {
        var scn = GetScenario();
        var harnaidan = scn.GetDSCard("harnaidan");
        var atst = scn.GetDSCard("atst");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("muunilinst"));
        scn.MoveLocationToTable(harnaidan);
        scn.MoveCardsToLocation(site, atst);
        int off = scn.GetPower(atst);
        scn.MoveCardsToLocation(harnaidan, atst);
        assertEquals(off + 1, scn.GetPower(atst));
    }

    @Test
    public void MuunilinstCityOfHarnaidanLightForceDrainMinus1WithoutCombatVehicleOrCloneArmy() {
        var scn = GetScenario();
        var harnaidan = scn.GetDSCard("harnaidan");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("muunilinst"));
        scn.MoveLocationToTable(harnaidan);
        scn.MoveCardsToLocation(harnaidan, luke);

        assertEquals(1, Math.round(scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), harnaidan, scn.LS)));
    }

    @Test
    public void MuunilinstCityOfHarnaidanLightForceDrainIs1WithCloneArmyCardHere() {
        var scn = GetScenario();
        var harnaidan = scn.GetDSCard("harnaidan");
        var cody = scn.GetLSCard("cody");

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("muunilinst"));
        scn.MoveLocationToTable(harnaidan);
        scn.MoveCardsToLocation(harnaidan, cody);

        assertEquals(2, Math.round(scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), harnaidan, scn.LS)));
    }
}
