package com.gempukku.swccgo.cards.set1112.light;

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
import static org.junit.Assert.assertTrue;

public class Card_1112_010_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("torps", "1112_10");
                    put("xwing", "1_146");
                    put("luke", "1_19");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("tie", "1_304");
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
    public void EnhancedProtonTorpedoesStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("torps").getBlueprint();

        assertEquals("Enhanced Proton Torpedoes", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.PROTON_TORPEDOES);
            add(Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.WEAPON);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EnhancedProtonTorpedoesDeployForOneForceOnYourStarfighterAndFireForFree() {
        var scn = GetScenario();
        var torps = scn.GetLSCard("torps");
        var xwing = scn.GetLSCard("xwing");
        var luke = scn.GetLSCard("luke");
        var tatooine = scn.GetLSCard("tatooine");
        var tie = scn.GetDSCard("tie");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing, tie);
        scn.BoardAsPilot(xwing, luke);
        scn.MoveCardsToLSHand(torps);
        float cost = scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), torps, torps, xwing, false, null, false, 0, null, false);
        assertEquals(1, cost, scn.epsilon);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(3);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(torps));
        scn.LSPlayCard(torps);
        scn.LSChooseCard(xwing);
        scn.PassAllResponses();
        assertEquals(xwing, torps.getAttachedTo());
        assertEquals(force - 1, scn.GetLSForcePileCount());

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        assertTrue(scn.LSCardActionAvailable(torps));
    }
}
