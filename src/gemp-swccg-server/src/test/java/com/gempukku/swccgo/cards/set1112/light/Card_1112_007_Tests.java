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

public class Card_1112_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("debnoli", "1112_7");
                    put("xwing", "1_146");
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
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
    public void DebnoliStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("debnoli").getBlueprint();

        assertEquals("Debnoli", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.PERMANENT_WEAPON);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DebnoliAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var debnoli = scn.GetLSCard("debnoli");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, debnoli);
        assertEquals(5, scn.GetPower(xwing));
    }

    @Test
    public void DebnoliMayInitiateBattleForFreeWhileWithAnImperial() {
        var scn = GetScenario();
        var debnoli = scn.GetLSCard("debnoli");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, debnoli, stormtrooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.LSActionAvailable("Initiate battle for free"));
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(site, "Initiate battle for free");
        scn.PassForceUseResponses();
        scn.PassBattleStartResponses();
        assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void DebnoliPermanentWeaponHitsAndSetsForfeitToZero() {
        var scn = GetScenario();
        var debnoli = scn.GetLSCard("debnoli");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, debnoli, stormtrooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(6);
        assertTrue(scn.LSCardActionAvailable(debnoli, "Fire"));
        scn.LSUseCardAction(debnoli, "Fire");
        scn.LSChooseCard(stormtrooper);
        scn.PassAllResponses();
        assertTrue(stormtrooper.isHit());
        assertEquals(0, scn.GetForfeit(stormtrooper));
    }
}
