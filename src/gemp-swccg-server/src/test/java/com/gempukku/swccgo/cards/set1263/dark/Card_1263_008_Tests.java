package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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

public class Card_1263_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("doallyn", "1240_74");
                    put("doallyn2", "1240_74");
                }},
                new HashMap<>() {{
                    put("sbd", "1263_8");
                    put("lin", "1_186");
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
    public void SecurityBattleDroidStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("sbd").getBlueprint();

        assertEquals("Security Battle Droid", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(3, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertTrue(card.getModelTypes().contains(ModelType.BATTLE));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SECURITY_BATTLE_DROID);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.EPISODE_I);
            add(Icon.PRESENCE);
            add(Icon.CORUSCANT);
            add(Icon.LEGACY_BLOCK_6);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SecurityBattleDroidMakesCharactersAtThisSiteLoseHeroic() {
        var scn = GetScenario();
        var sbd = scn.GetDSCard("sbd");
        var doallyn = scn.GetLSCard("doallyn");
        var doallyn2 = scn.GetLSCard("doallyn2");
        var site = scn.GetDSStartingLocation();
        var otherSite = scn.GetLSStartingLocation();

        //test1: a Heroic character at this site loses Heroic
        //test2: a Heroic character at another site keeps Heroic
        scn.StartGame();
        scn.MoveCardsToLocation(site, doallyn);
        scn.MoveCardsToLocation(otherSite, doallyn2);
        assertTrue(scn.HasKeyword(doallyn, Keyword.HEROIC));

        scn.MoveCardsToLocation(site, sbd);
        assertFalse(scn.HasKeyword(doallyn, Keyword.HEROIC)); //test1
        assertTrue(scn.HasKeyword(doallyn2, Keyword.HEROIC)); //test2
    }

    @Test
    public void SecurityBattleDroidDrawsOneBattleDestinyUnlessAlone() {
        var scn = GetScenario();
        var sbd = scn.GetDSCard("sbd");
        var lin = scn.GetDSCard("lin");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sbd, lin, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void SecurityBattleDroidDrawsNoBattleDestinyWhenAlone() {
        var scn = GetScenario();
        var sbd = scn.GetDSCard("sbd");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sbd, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(0, scn.GetDSBattleDestinyCount());
    }
}
