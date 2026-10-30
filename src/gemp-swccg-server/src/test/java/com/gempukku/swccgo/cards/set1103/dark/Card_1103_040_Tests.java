package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1103_040_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("motti", "1103_40");
                    put("devastator", "1_301");
                    put("isd", "1_302");
                    put("kdy", "7_232");
                    put("dantooine", "1_282");
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
    public void MottiStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("motti").getBlueprint();

        assertEquals("Motti", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.A_NEW_HOPE);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MottiAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var isd = scn.GetDSCard("isd");
        var dantooine = scn.GetDSCard("dantooine");

        scn.StartGame();
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, isd);
        scn.BoardAsPilot(isd, motti);
        assertEquals(10, scn.GetPower(isd));
    }

    @Test
    public void MottiMakesUniqueStarDestroyersPowerPlusTwo() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var devastator = scn.GetDSCard("devastator");
        var dantooine = scn.GetDSCard("dantooine");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, motti);
        scn.MoveLocationToTable(dantooine);
        scn.MoveCardsToLocation(dantooine, devastator);
        assertEquals(11, scn.GetPower(devastator));
    }

    @Test
    public void MottiMakesKuatDriveYardsImmuneToAlterAndMayDeployItOncePerGame() {
        var scn = GetScenario();
        var motti = scn.GetDSCard("motti");
        var kdy = scn.GetDSCard("kdy");
        var dantooine = scn.GetDSCard("dantooine");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, motti);
        scn.MoveLocationToTable(dantooine);
        scn.AttachCardsTo(dantooine, kdy);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), kdy, Title.Alter));

        scn.MoveCardsToTopOfDSReserveDeck(kdy);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(motti, "Deploy Kuat Drive Yards from Reserve Deck"));
    }
}
