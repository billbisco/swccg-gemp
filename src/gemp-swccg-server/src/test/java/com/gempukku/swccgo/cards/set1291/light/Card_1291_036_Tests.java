package com.gempukku.swccgo.cards.set1291.light;

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

public class Card_1291_036_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("squad", "1291_36");
                    put("squad2", "1291_36");
                    put("specialist", "1291_38"); // Clone Specialist: another clone (different title)
                    put("luke", "1_19"); // Luke Skywalker: ability 4, not a clone
                }},
                new HashMap<>() {{
                    put("trooper", "1_194"); // Stormtrooper
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
    public void CloneInfantrySquadStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("squad").getBlueprint();
        assertEquals("Clone Infantry Squad", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals("Clone trooper.", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CLONE_TROOPER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.WARRIOR);
            add(Icon.CLONE_ARMY);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void battleAtLSSite(VirtualTableScenario scn) {
        var site = scn.GetLSStartingLocation();
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
    }

    @Test
    public void CloneInfantrySquadAddsBattleDestinyWithAnotherClone() {
        var scn = GetScenario();
        var squad = scn.GetLSCard("squad");
        var specialist = scn.GetLSCard("specialist");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), squad, specialist, trooper);
        battleAtLSSite(scn);
        // total ability 4 draws 1; the squad is with another clone and adds one
        assertEquals(2, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void CloneInfantrySquadPairAddsOneBattleDestinyBecauseSameTitleIsNotCumulative() {
        var scn = GetScenario();
        var squad = scn.GetLSCard("squad");
        var squad2 = scn.GetLSCard("squad2");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), squad, squad2, trooper);
        battleAtLSSite(scn);
        assertEquals(2, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void CloneInfantrySquadAddsNoBattleDestinyWithANonClone() {
        var scn = GetScenario();
        var squad = scn.GetLSCard("squad");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), squad, luke, trooper);
        battleAtLSSite(scn);
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void CloneInfantrySquadMayDrawTwoBattleDestinyAndChooseOne() {
        var scn = GetScenario();
        var squad = scn.GetLSCard("squad");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), squad, luke, trooper);
        scn.PrepareLSDestiny(3);
        scn.PrepareLSDestiny(5);
        battleAtLSSite(scn);
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsAction(scn, squad, "Draw two battle destiny" ));
        scn.LSUseCardAction(squad, "Draw two battle destiny");
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsCardChoiceCount(scn) == 2);
    }
}
