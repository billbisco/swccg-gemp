package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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

public class Card_1283_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19"); // power 3
                }},
                new HashMap<>() {{
                    put("sarlacc", "1283_8");
                    put("tusken", "1_196"); // Tusken Raider (alien)
                    put("jawa", "1_182"); // Jawa (alien)
                    put("vader", "1_168"); // not an alien
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
    public void SarlaccVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("sarlacc").getBlueprint();
        assertEquals("Sarlacc", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(0, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.CREATURE);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.ANCIENT_DESERT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CREATURE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SarlaccVFerocityIsFourPlusDestiny() {
        var scn = GetScenario();
        var sarlacc = scn.GetDSCard("sarlacc");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), sarlacc);
        assertEquals(4 + 3, scn.game().getModifiersQuerying().getFerocity(scn.game().getGameState(), sarlacc, 3f), scn.epsilon);
    }

    @Test
    public void SarlaccVCaptivesHereArePowerMinusNumberOfYourAliensHere() {
        var scn = GetScenario();
        var sarlacc = scn.GetDSCard("sarlacc");
        var tusken = scn.GetDSCard("tusken");
        var jawa = scn.GetDSCard("jawa");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, sarlacc, vader, luke);
        scn.CaptureCardWith(vader, luke);
        assertEquals(3, scn.GetPower(luke));
        scn.MoveCardsToLocation(site, tusken);
        assertEquals(2, scn.GetPower(luke));
        scn.MoveCardsToLocation(site, jawa);
        assertEquals(1, scn.GetPower(luke));
    }

    @Test
    public void SarlaccVDoesNotAffectCaptivesElsewhere() {
        var scn = GetScenario();
        var sarlacc = scn.GetDSCard("sarlacc");
        var tusken = scn.GetDSCard("tusken");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), sarlacc, tusken);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), vader, luke);
        scn.CaptureCardWith(vader, luke);
        assertEquals(3, scn.GetPower(luke));
    }
}
