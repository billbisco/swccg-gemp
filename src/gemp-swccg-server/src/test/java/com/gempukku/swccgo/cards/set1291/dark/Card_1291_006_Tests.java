package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19"); // ability 4
                    put("rebeltrooper", "1_28"); // Rebel Trooper, ability 1
                    put("tatooine", "1_127");
                }},
                new HashMap<>() {{
                    put("durge", "1291_6");
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
    public void DurgeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("durge").getBlueprint();
        assertEquals("Durge", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BOUNTY_HUNTER);
            add(Keyword.ASSASSIN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.WARRIOR);
            add(Icon.PILOT);
            add(Icon.EPISODE_I);
            add(Icon.SEPARATIST);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DurgeAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var durge = scn.GetDSCard("durge");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, durge);
        assertEquals(3, scn.GetPower(tie));
    }

    @Test
    public void DurgePowerPlus1ForEachOpponentsCharacterPresentWithAbilityMoreThan3() {
        var scn = GetScenario();
        var durge = scn.GetDSCard("durge");
        var luke = scn.GetLSCard("luke");
        var rebeltrooper = scn.GetLSCard("rebeltrooper");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, durge, rebeltrooper);
        assertEquals(4, scn.GetPower(durge));
        scn.MoveCardsToLocation(site, luke);
        assertEquals(5, scn.GetPower(durge));
    }

    @Test
    public void DurgeAddsOneBattleDestinyWhilePresentOnMuunilinst() {
        var scn = GetScenario();
        var durge = scn.GetDSCard("durge");
        var rebeltrooper = scn.GetLSCard("rebeltrooper");
        scn.StartGame();
        var plaza = MissionTestSupport.putLocationOnTable(scn, 9160, scn.DS, MissionTestSupport.muunilinstSite("Muunilinst: Plaza"));
        scn.MoveCardsToLocation(plaza, durge, rebeltrooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(plaza);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void DurgeAddsNoBattleDestinyAwayFromMuunilinstWithAbility3Alone() {
        var scn = GetScenario();
        var durge = scn.GetDSCard("durge");
        var rebeltrooper = scn.GetLSCard("rebeltrooper");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, durge, rebeltrooper);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToPowerSegment();
        assertEquals(0, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void DurgeMayUse2ForceToRegenerateIntoHandWhenJustLost() {
        var scn = GetScenario();
        var durge = scn.GetDSCard("durge");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, durge);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetDSForcePileCount();
        scn.DSExecuteAdHocEffect(durge, new LoseCardFromTableEffect(new TopLevelGameTextAction(durge, durge.getCardId()), durge));
        scn.PassResponses("ABOUT_TO_BE_LOST_FROM_TABLE");
        assertTrue(scn.LSDecisionAvailable("LOST_FROM_TABLE"));
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(durge, "'Regenerate'"));
        scn.DSUseCardAction(durge, "'Regenerate'");
        scn.PassAllResponses();
        assertInZone(Zone.HAND, durge);
        assertEquals(forceBefore - 2, scn.GetDSForcePileCount());
    }
}
