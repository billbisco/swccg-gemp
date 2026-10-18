package com.gempukku.swccgo.cards.set1250.dark;

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
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1250_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("blaster", "1_154");
                }},
                new HashMap<>() {{
                    put("guard", "1250_4");
                    put("trooper", "1_194");
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
    public void CoruscantGuardStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("guard").getBlueprint();

        assertEquals("Coruscant Guard", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CORUSCANT_GUARD);
            add(Keyword.HEROIC);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.CORUSCANT);
            add(Icon.LEGACY_BLOCK_5);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CoruscantGuardMustBeTargetedByWeaponsBeforeNonHeroic() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var guard = scn.GetDSCard("guard");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, guard, trooper, luke);
        scn.AttachCardsTo(luke, blaster);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(guard));
        assertTrue(scn.LSHasCardChoiceNotAvailable(trooper));
    }

    @Test
    public void CoruscantGuardDeploysMinusOneToSenate() {
        var scn = GetSenateScenario();
        var guard = scn.GetDSCard("guard");
        var senate = scn.GetDSCard("senate");

        scn.StartGame();
        scn.MoveLocationToTable(senate);

        assertEquals(3, scn.GetDeployCost(guard));
        assertEquals(2, deployCostTo(scn, guard, senate), scn.epsilon);
    }

    @Test
    public void CoruscantGuardPoliticsPlusTwoOnSidiousIsCumulative() {
        var scn = GetSenateScenario();
        var guard = scn.GetDSCard("guard");
        var guard2 = scn.GetDSCard("guard2");
        var sidious = scn.GetDSCard("sidious");
        var senate = scn.GetDSCard("senate");

        scn.StartGame();
        scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, sidious);

        float base = scn.game().getModifiersQuerying().getPolitics(scn.gameState(), sidious);
        scn.MoveCardsToLocation(senate, guard);
        assertEquals(base + 2, scn.game().getModifiersQuerying().getPolitics(scn.gameState(), sidious), scn.epsilon);
        scn.MoveCardsToLocation(senate, guard2);
        assertEquals(base + 4, scn.game().getModifiersQuerying().getPolitics(scn.gameState(), sidious), scn.epsilon);
    }

    @Test
    public void CoruscantGuardPremiereExpandTheEmpireIsNotTakenFromReserve() {
        var scn = GetSenateScenario();
        var guard = scn.GetDSCard("guard");
        var senate = scn.GetDSCard("senate");
        var expand = scn.GetDSCard("expand");

        scn.StartGame();
        scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, guard);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfOwnReserveDeck(expand);

        assertTrue(scn.DSCardActionAvailable(guard, "Take Expand The Empire"));
        scn.DSUseCardAction(guard, "Take Expand The Empire");
        assertTrue("Premiere Expand The Empire is not [Virtual Block 5]",
                scn.DSHasCardChoiceNotAvailable(expand));
        scn.PassAllResponses();
        assertTrue(expand.getZone() == Zone.RESERVE_DECK || expand.getZone() == Zone.TOP_OF_RESERVE_DECK);
    }

    @Test
    public void CoruscantGuardTakesACardFromForcePileDuringControlPhase() {
        var scn = GetSenateScenario();
        var guard = scn.GetDSCard("guard");
        var senate = scn.GetDSCard("senate");
        var expand = scn.GetDSCard("expand");

        scn.StartGame();
        scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, guard);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSForcePile(expand);

        assertTrue(scn.DSCardActionAvailable(guard, "Take a card from Force Pile"));
        scn.DSUseCardAction(guard, "Take a card from Force Pile");
        scn.PassAllResponses();
        scn.DSChooseCard(expand);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, expand.getZone());
        assertTrue(guard.getZone() == Zone.USED_PILE || guard.getZone() == Zone.TOP_OF_USED_PILE);
    }

    private VirtualTableScenario GetSenateScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("guard", "1250_4");
                    put("guard2", "1250_4");
                    put("sidious", "14_79");
                    put("senate", "12_167");
                    put("expand", "1_215");
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

    private float deployCostTo(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card,
                               com.gempukku.swccgo.game.PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
