package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1240_9");
                    put("rifle", "1_312");
                    put("other", "1_194");
                    put("blaster", "1_317");
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

    @Test
    public void ImperialStormtrooperStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("trooper").getBlueprint();

        assertEquals("Imperial Stormtrooper", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.STORMTROOPER);
            add(Keyword.CLONE_TROOPER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ImperialStormtrooperBlasterRifleDeploysForFreeOnThisTrooperOnly() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var other = scn.GetDSCard("other");
        var rifle = scn.GetDSCard("rifle");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, other);

        float onTrooper = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), rifle, rifle, trooper, false, null, false, 0, null, false);
        float onOther = scn.game().getModifiersQuerying().getDeployCost(scn.gameState(), rifle, rifle, other, false, null, false, 0, null, false);
        assertEquals(0, onTrooper, scn.epsilon);
        assertTrue(onOther > 0);
    }

    @Test
    public void ImperialStormtrooperIsImmuneToRebelBarrier() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var other = scn.GetDSCard("other");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, other);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), trooper, Title.Rebel_Barrier));
        assertFalse(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), other, Title.Rebel_Barrier));
    }

    private void fireRifleUntilDrawTwoOffered(VirtualTableScenario scn, String expectedText) {
        for (int i = 0; i < 20; i++) {
            if (scn.DSCardActionAvailable(scn.GetDSCard("trooper"), expectedText))
                return;
            if (scn.LSDecisionAvailable("Optional responses") || scn.LSAnyDecisionsAvailable() && scn.GetAwaitingDecision(scn.LS) != null && scn.LSAnyActionsAvailable())
                scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
        }
    }

    @Test
    public void ImperialStormtrooperFiresBlasterRifleForFreeAndMayDrawTwoChooseOneTakingOtherIntoHand() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var rifle = scn.GetDSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, luke);
        scn.AttachCardsTo(trooper, rifle);

        scn.SkipToPhase(Phase.BATTLE);
        scn.EnsureDSForcePile(1);
        var low = scn.GetDSDestiny(1);
        var high = scn.GetDSDestiny(6);
        scn.MoveCardsToTopOfDSReserveDeck(low);
        scn.MoveCardsToTopOfDSReserveDeck(high);

        scn.DSInitiateBattle(site);
        for (int i = 0; i < 6 && !scn.AwaitingDSWeaponsSegmentActions(); i++) {
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass(); else scn.DSPass();
        }
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        int forceBefore = scn.GetDSForcePileCount();
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(luke);
        fireRifleUntilDrawTwoOffered(scn, "Draw two weapon destiny");
        assertTrue(scn.DSCardActionAvailable(trooper, "Draw two weapon destiny"));
        assertEquals(forceBefore, scn.GetDSForcePileCount());

        int handBefore = scn.GetDSHandCount();
        scn.DSUseCardAction(trooper, "Draw two weapon destiny");
        for (int i = 0; i < 20; i++) {
            if (scn.DSDecisionAvailable("into hand")) break;
            if (scn.DSHasCardChoiceAvailable(high)) {
                scn.DSChooseCard(high);
                continue;
            }
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass(); else scn.DSPass();
        }
        assertTrue(scn.DSDecisionAvailable("into hand"));
        scn.DSChooseYes();
        assertEquals(Zone.HAND, low.getZone());
        assertEquals(handBefore + 1, scn.GetDSHandCount());
    }

    @Test
    public void ImperialStormtrooperDrawTwoNotOfferedForOtherWeapons() {
        var scn = GetScenario();
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper, luke);
        scn.AttachCardsTo(trooper, blaster);

        scn.SkipToPhase(Phase.BATTLE);
        scn.EnsureDSForcePile(3);
        scn.DSInitiateBattle(site);
        for (int i = 0; i < 6 && !scn.AwaitingDSWeaponsSegmentActions(); i++) {
            if (scn.LSAnyDecisionsAvailable()) scn.LSPass(); else scn.DSPass();
        }
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(luke);
        fireRifleUntilDrawTwoOffered(scn, "Draw two weapon destiny");
        assertFalse(scn.DSCardActionAvailable(trooper, "Draw two weapon destiny"));
    }
}
