package com.gempukku.swccgo.cards.set1210.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_053_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("corellia", "2_61");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("doctrine", "1210_53");
                    put("deathstar", "2_143");
                    put("devastator", "1_301");
                    put("starDestroyer", "1_302");
                    put("kiffex", "2_147");
                    put("tatooine", "1_289");
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
    public void TarkinDoctrineStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("doctrine").getBlueprint();

        assertEquals("Tarkin Doctrine", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TarkinDoctrineDeploysOnDeathStar() {
        var scn = GetScenario();
        var doctrine = scn.GetDSCard("doctrine");
        var deathstar = scn.GetDSCard("deathstar");

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveCardsToDSHand(doctrine);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(doctrine));
        scn.DSPlayCard(doctrine);
        scn.DSChooseCard(deathstar);
        scn.PassAllResponses();

        assertEquals(deathstar, doctrine.getAttachedTo());
    }

    @Test
    public void TarkinDoctrineMakesOpponentLoseOneForcePerQualifyingStarDestroyerBattleground() {
        var scn = GetScenario();
        var doctrine = scn.GetDSCard("doctrine");
        var deathstar = scn.GetDSCard("deathstar");
        var corellia = scn.GetLSCard("corellia");
        var kiffex = scn.GetDSCard("kiffex");
        var tatooine = scn.GetDSCard("tatooine");
        var devastator = scn.GetDSCard("devastator");
        var starDestroyer = scn.GetDSCard("starDestroyer");

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(corellia);
        scn.MoveLocationToTable(kiffex);
        scn.MoveLocationToTable(tatooine);
        scn.AttachCardsTo(deathstar, doctrine);
        scn.MoveCardsToLocation(corellia, devastator);
        scn.MoveCardsToLocation(kiffex, starDestroyer);
        scn.SkipToPhase(Phase.CONTROL);
        scn.EnsureLSForcePile(2);

        assertTrue(scn.DSCardActionAvailable(doctrine, "Make opponent lose"));
        int lsForceBefore = scn.GetLSForcePileCount();
        scn.DSUseCardAction(doctrine, "Make opponent lose");
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSForcePile());
        scn.PassAllResponses();
        scn.LSChooseCard(scn.GetTopOfLSForcePile());
        scn.PassAllResponses();

        assertEquals(lsForceBefore - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void TarkinDoctrineDoesNotCountStarDestroyerBattlegroundsBeyondTwoParsecs() {
        var scn = GetScenario();
        var doctrine = scn.GetDSCard("doctrine");
        var deathstar = scn.GetDSCard("deathstar");
        var tatooine = scn.GetDSCard("tatooine");
        var devastator = scn.GetDSCard("devastator");

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(tatooine);
        scn.AttachCardsTo(deathstar, doctrine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertFalse(scn.DSCardActionAvailable(doctrine, "Make opponent lose"));
    }

    @Test
    public void TarkinDoctrineIsCanceledWhenOpponentControlsDeathStar() {
        var scn = GetScenario();
        var doctrine = scn.GetDSCard("doctrine");
        var deathstar = scn.GetDSCard("deathstar");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.AttachCardsTo(deathstar, doctrine);
        scn.MoveCardsToLocation(deathstar, xwing);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, doctrine);
    }
}
