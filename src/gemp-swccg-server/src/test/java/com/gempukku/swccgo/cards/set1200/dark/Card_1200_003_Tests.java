package com.gempukku.swccgo.cards.set1200.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1200_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("kal", "1_15");
                }},
                new HashMap<>() {{
                    put("detention", "1200_3");
                    put("wookiee", "3_127");
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
    public void ImperialDetentionStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("detention").getBlueprint();

        assertEquals("Imperial Detention", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEFENSIVE_SHIELD);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REFLECTIONS_III);
            add(Icon.DEFENSIVE_SHIELD);
            add(Icon.LEGACY_BLOCK_D);
        }});
        assertEquals(ExpansionSet.VIRTUAL_SHIELDS, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImperialDetentionIdJustAsSoonKissAWookieeMayNotBePlayed() {
        var scn = GetScenario();
        var detention = scn.GetDSCard("detention");
        var wookiee = scn.GetDSCard("wookiee");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(detention);
        scn.MoveCardsToLSHand(luke);
        scn.MoveCardsToDSHand(wookiee);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(luke));
        scn.LSDeployCard(luke);
        scn.LSChooseCard(site);
        scn.PassAllResponses();

        assertFalse(scn.DSPlayLostInterruptAvailable(wookiee));
        assertAtLocation(site, luke);
    }

    @Test
    public void ImperialDetentionRequiresExtraForceToDeployAPrintedZeroCostCharacter() {
        var scn = GetScenario();
        var detention = scn.GetDSCard("detention");
        var kal = scn.GetLSCard("kal");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(detention);
        scn.MoveCardsToLSHand(kal);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.DrainLSForcePileTo(1);
        assertTrue(scn.LSDeployAvailable(kal));
        int forceBefore = scn.GetLSForcePileCount();
        scn.LSDeployCard(kal);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertAtLocation(site, kal);
        assertEquals(forceBefore - 1, scn.GetLSForcePileCount());
    }
}
