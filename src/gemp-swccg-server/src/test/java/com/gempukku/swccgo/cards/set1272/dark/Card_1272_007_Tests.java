package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_007_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("mace", "12_13");
                    put("obiwan", "1_21");
                }},
                new HashMap<>() {{
                    put("waytt", "1272_7");
                    put("coruscant", "12_165");
                    put("tatooine", "1_289");
                    put("ommni", "1_262");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private static boolean inLostPile(PhysicalCardImpl card) {
        return card.getZone() == Zone.LOST_PILE || card.getZone() == Zone.TOP_OF_LOST_PILE;
    }

    private static boolean inUsedPile(PhysicalCardImpl card) {
        return card.getZone() == Zone.USED_PILE || card.getZone() == Zone.TOP_OF_USED_PILE;
    }

    @Test
    public void WhereAreYouTakingThemStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("waytt").getBlueprint();

        assertEquals("Where Are You Taking Them?", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.THEED_PALACE);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WhereAreYouTakingThemLimitsCoruscantJediImmunityAtNonEpisodeISites() {
        var scn = GetScenario();
        var waytt = scn.GetDSCard("waytt");
        var mace = scn.GetLSCard("mace");
        var obiwan = scn.GetLSCard("obiwan");
        var walkway = scn.GetLSStartingLocation();
        var marketplace = scn.GetDSStartingLocation();

        //test1: [Coruscant] Jedi at a non-[Episode I] site is limited to immune to attrition < 5
        //test2: no limit at an [Episode I] site
        //test3: no limit on a Jedi without the [Coruscant] icon
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(waytt);
        scn.MoveCardsToLocation(walkway, mace, obiwan);
        scn.ApplyAdHocModifier(new ImmuneToAttritionLessThanModifier(mace, Filters.or(Filters.sameCardId(mace), Filters.sameCardId(obiwan)), 7));

        assertEquals(5, immunity(scn, mace), scn.epsilon); //test1
        assertEquals(7, immunity(scn, obiwan), scn.epsilon); //test3

        scn.MoveCardsToLocation(marketplace, mace);
        assertEquals(7, immunity(scn, mace), scn.epsilon); //test2
    }

    @Test
    public void WhereAreYouTakingThemCancelsYourGameTextOnCoruscantSystems() {
        var scn = GetScenario();
        var waytt = scn.GetDSCard("waytt");
        var coruscant = scn.GetDSCard("coruscant");
        var tatooine = scn.GetDSCard("tatooine");

        //test1: deploys on table
        //test2: your game text on a [Coruscant] system is canceled
        //test3: opponent's game text on that system is not
        //test4: a system without the [Coruscant] icon is not affected
        scn.StartGame();
        scn.MoveLocationToTable(coruscant);
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToDSHand(waytt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(coruscant.isLocationGameTextCanceledForPlayer(scn.DS));

        assertTrue(scn.DSDeployAvailable(waytt));
        scn.DSDeployCard(waytt);
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, waytt.getZone()); //test1

        assertTrue(coruscant.isLocationGameTextCanceledForPlayer(scn.DS)); //test2
        assertFalse(coruscant.isLocationGameTextCanceledForPlayer(scn.LS)); //test3
        assertFalse(tatooine.isLocationGameTextCanceledForPlayer(scn.DS)); //test4
    }

    @Test
    public void WhereAreYouTakingThemLosesOneForceToPlaceJustPlayedInterruptInLostPile() {
        var scn = GetScenario();
        var waytt = scn.GetDSCard("waytt");
        var ommni = scn.GetDSCard("ommni");

        //test1: during your control phase, a just-resolved Interrupt may be placed in owner's Lost Pile
        //test2: costs 1 Force
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(waytt);
        scn.MoveCardsToDSHand(ommni);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(ommni));
        scn.DSPlayCard(ommni);
        scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
        scn.PassResponses("Playing");
        scn.PassResponses("SHUFFLE_CARD_PILE");
        scn.PassResponses("PUT_IN_CARD_PILE_FROM_OFF_TABLE");
        assertTrue(inUsedPile(ommni));

        assertTrue(scn.DSCardActionAvailable(waytt, "Coruscant?"));
        int lifeBefore = scn.GetDSLifeForceRemaining();
        scn.DSUseCardAction(waytt, "Coruscant?");
        scn.PassResponses("FORCE_LOSS_INITIATED");
        scn.PassResponses("ABOUT_TO_LOSE_FORCE_NOT_FROM_BATTLE_DAMAGE");
        var forceLost = scn.GetTopOfDSReserveDeck();
        assertTrue(scn.DSDecisionAvailable("Choose Force to lose"));
        scn.DSChooseCard(forceLost);
        scn.PassAllResponses();

        assertTrue(inLostPile(ommni)); //test1
        assertEquals(scn.DS, ommni.getZoneOwner()); //test1
        assertTrue(inLostPile(forceLost)); //test2
        assertEquals(lifeBefore - 2, scn.GetDSLifeForceRemaining()); //test2
    }

    @Test
    public void WhereAreYouTakingThemDoesNothingOutsideYourControlPhase() {
        var scn = GetScenario();
        var waytt = scn.GetDSCard("waytt");
        var ommni = scn.GetDSCard("ommni");

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(waytt);
        scn.MoveCardsToDSHand(ommni);
        scn.SkipToDSTurn(Phase.DEPLOY);

        scn.DSPlayCard(ommni);
        scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
        scn.PassResponses("Playing");
        scn.PassResponses("SHUFFLE_CARD_PILE");
        scn.PassResponses("PUT_IN_CARD_PILE_FROM_OFF_TABLE");
        assertTrue(inUsedPile(ommni));
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(inLostPile(ommni));
    }

    private float immunity(VirtualTableScenario scn, PhysicalCardImpl card) {
        return scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), card);
    }
}
