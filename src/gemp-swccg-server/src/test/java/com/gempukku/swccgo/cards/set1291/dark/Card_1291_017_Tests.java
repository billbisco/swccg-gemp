package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_1291_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("petranaki", "1291_17");
                    put("dooku", "200_76");
                    put("jango", "201_25");
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
    public void GeonosisPetranakiArenaStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("petranaki").getBlueprint();

        assertEquals("Geonosis: Petranaki Arena", card.getTitle());
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
    public void GeonosisPetranakiArenaDookuAndJangoArePowerPlus1Here() {
        var scn = GetScenario();
        var petranaki = scn.GetDSCard("petranaki");
        var dooku = scn.GetDSCard("dooku");
        var jango = scn.GetDSCard("jango");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToLocation(site, dooku, jango, vader);
        int dookuOff = scn.GetPower(dooku);
        int jangoOff = scn.GetPower(jango);
        int vaderOff = scn.GetPower(vader);

        scn.MoveCardsToLocation(petranaki, dooku, jango, vader);
        assertEquals(dookuOff + 1, scn.GetPower(dooku));
        assertEquals(jangoOff + 1, scn.GetPower(jango));
        assertEquals(vaderOff, scn.GetPower(vader));
    }
}
