package com.gempukku.swccgo.cards.set1103.dark;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1103_033_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("han", "1_11");
                }},
                new HashMap<>() {{
                    put("hem", "1103_33");
                    put("pilot", "1210_13");
                    put("salt", "1_194");
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
    public void HemDazonStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("hem").getBlueprint();

        assertEquals("Hem Dazon", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.A_NEW_HOPE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HemDazonStacksSaltFromHandDuringDeployPhase() {
        var scn = GetScenario();
        var hem = scn.GetDSCard("hem");
        var salt = scn.GetDSCard("salt");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, hem);
        scn.MoveCardsToDSHand(salt);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.DSCardActionAvailable(hem, "Stack 'salt' from hand"));
        scn.DSUseCardAction(hem, "Stack 'salt' from hand");
        scn.PassAllResponses();
        assertTrue(salt.getZone() == Zone.STACKED || salt.getZone() == Zone.STACKED_FACE_DOWN);
        assertEquals(hem, salt.getStackedOn());
    }

    @Test
    public void HemDazonAddsOneToTotalBattleDestinyPerSalt() {
        var scn = GetScenario();
        var hem = scn.GetDSCard("hem");
        var salt = scn.GetDSCard("salt");
        var pilot = scn.GetDSCard("pilot");
        var han = scn.GetLSCard("han");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, hem, pilot, han);
        scn.MoveCardsToDSHand(salt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSUseCardAction(hem, "Stack 'salt' from hand");
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(4);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(true);
        assertEquals(5, scn.GetDSTotalDestiny(), scn.epsilon);
    }

    @Test
    public void HemDazonPlacesSaltInUsedPileDuringControlPhase() {
        var scn = GetScenario();
        var hem = scn.GetDSCard("hem");
        var salt = scn.GetDSCard("salt");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, hem);
        scn.MoveCardsToDSHand(salt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSUseCardAction(hem, "Stack 'salt' from hand");
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(hem, "Place 'salt' in Used Pile"));
        scn.DSUseCardAction(hem, "Place 'salt' in Used Pile");
        scn.PassAllResponses();
        assertTrue(salt.getZone() == Zone.USED_PILE || salt.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void HemDazonPlacesSaltInUsedPileWhenLost() {
        var scn = GetScenario();
        var hem = scn.GetDSCard("hem");
        var salt = scn.GetDSCard("salt");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, hem, luke);
        scn.MoveCardsToDSHand(salt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSUseCardAction(hem, "Stack 'salt' from hand");
        scn.PassAllResponses();

        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(false);
        scn.PassResponses("INITIAL_ATTRITION_CALCULATED");
        assertTrue(scn.AwaitingDSBattleDamagePayment());
        scn.DSChooseCard(hem);
        scn.DSChooseAction("Place 'salt' in Used Pile");
        scn.PassAllResponses();
        assertTrue(salt.getZone() == Zone.USED_PILE || salt.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
