package com.gempukku.swccgo.cards.set1102.dark;

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

public class Card_1102_032_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("corellia", "2_61");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("fear", "1102_32");
                    put("fear2", "1102_32");
                    put("premiereFear", "1_216");
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
    public void FearWillKeepThemInLineStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("fear").getBlueprint();

        assertEquals("Fear Will Keep Them In Line", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void FearWillKeepThemInLineDeploysOnDeathStar() {
        var scn = GetScenario();
        var fear = scn.GetDSCard("fear");
        var deathstar = scn.GetDSCard("deathstar");

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveCardsToDSHand(fear);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(fear));
        scn.DSPlayCard(fear);
        scn.DSChooseCard(deathstar);
        scn.PassAllResponses();

        assertEquals(deathstar, fear.getAttachedTo());
    }

    @Test
    public void FearWillKeepThemInLineStillDeploysWhenPremiereFearIsOnAStarDestroyer() {
        var scn = GetScenario();
        var fear = scn.GetDSCard("fear");
        var premiereFear = scn.GetDSCard("premiereFear");
        var deathstar = scn.GetDSCard("deathstar");
        var devastator = scn.GetDSCard("devastator");

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), devastator);
        scn.AttachCardsTo(devastator, premiereFear);
        scn.MoveCardsToDSHand(fear);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(fear));
        scn.DSPlayCard(fear);
        scn.DSChooseCard(deathstar);
        scn.PassAllResponses();

        assertEquals(deathstar, fear.getAttachedTo());
    }

    @Test
    public void FearWillKeepThemInLineDoesNotDeployWhenAnotherOvsCopyIsOnTable() {
        var scn = GetScenario();
        var fear = scn.GetDSCard("fear");
        var fear2 = scn.GetDSCard("fear2");
        var deathstar = scn.GetDSCard("deathstar");

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.AttachCardsTo(deathstar, fear);
        scn.MoveCardsToDSHand(fear2);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertFalse(scn.DSCardPlayAvailable(fear2));
    }

    @Test
    public void FearWillKeepThemInLineMakesOpponentLoseTwoForcePerQualifyingStarDestroyerBattleground() {
        var scn = GetScenario();
        var fear = scn.GetDSCard("fear");
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
        scn.AttachCardsTo(deathstar, fear);
        scn.MoveCardsToLocation(corellia, devastator);
        scn.MoveCardsToLocation(kiffex, starDestroyer);
        scn.SkipToPhase(Phase.CONTROL);
        scn.EnsureLSForcePile(4);

        assertTrue(scn.DSCardActionAvailable(fear, "Make opponent lose"));
        int lsForceBefore = scn.GetLSForcePileCount();
        scn.DSUseCardAction(fear, "Make opponent lose");
        scn.PassAllResponses();
        int paid = 0;
        while (scn.LSDecisionAvailable("Choose Force to lose") && paid < 6) {
            scn.LSChooseCard(scn.GetTopOfLSForcePile());
            scn.PassAllResponses();
            paid++;
        }
        assertEquals(4, paid);
        assertEquals(lsForceBefore - 4, scn.GetLSForcePileCount());
    }

    @Test
    public void FearWillKeepThemInLineDoesNotCountStarDestroyerBattlegroundsBeyondTwoParsecs() {
        var scn = GetScenario();
        var fear = scn.GetDSCard("fear");
        var deathstar = scn.GetDSCard("deathstar");
        var tatooine = scn.GetDSCard("tatooine");
        var devastator = scn.GetDSCard("devastator");

        scn.StartGame();
        scn.MoveLocationToTable(deathstar);
        scn.MoveLocationToTable(tatooine);
        scn.AttachCardsTo(deathstar, fear);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertFalse(scn.DSCardActionAvailable(fear, "Make opponent lose"));
    }
}
