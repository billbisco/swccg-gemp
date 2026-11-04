package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.CancelOpponentsForceDrainBonusesModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_017_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("loje", "1282_17");
                    put("laugh", "1282_21"); // I Know That Laugh
                    put("boshuda", "6_54"); // Bo Shuda
                    put("chamber", "6_81"); // Jabba's Palace: Audience Chamber
                    put("ithorian", "6_20"); // an alien
                    put("s1", "6_10");
                    put("s2", "6_10");
                    put("s3", "6_10");
                    put("s4", "6_10");
                }},
                new HashMap<>() {{
                    put("coldfeet", "601_4"); // Cold Feet (V): may suspend Bo Shuda
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
    public void LojeNellaVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("loje").getBlueprint();

        assertEquals("Loje Nella", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ACCOUNTANT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LojeNellaVMayLose2ForceToCancelColdFeetTargetingBoShuda() {
        var scn = GetScenario();
        var loje = scn.GetLSCard("loje");
        var boshuda = scn.GetLSCard("boshuda");
        var chamber = scn.GetLSCard("chamber");
        var coldfeet = scn.GetDSCard("coldfeet");

        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        scn.AttachCardsTo(chamber, boshuda);
        scn.MoveCardsToLocation(chamber, loje);
        scn.MoveCardsToDSHand(coldfeet);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardPlayAvailable(coldfeet, "Suspend"));
        scn.DSPlayCard(coldfeet, "Suspend");
        scn.DSChooseCard(boshuda);
        int lsLostBefore = scn.GetLSLostPileCount();
        assertTrue(scn.LSCardActionAvailable(loje, "cancel"));
        scn.LSUseCardAction(loje, "cancel");
        LegacyVb82TestSupport.lsPayForceLoss(scn);
        scn.PassAllResponses();

        assertFalse(coldfeet.getZone() == Zone.USED_PILE);
        assertEquals(lsLostBefore + 2, scn.GetLSLostPileCount());
        assertFalse(boshuda.isSuspended());
    }

    @Test
    public void LojeNellaVActivates1ForceAfterYouForceDrainWithAnAlien() {
        var scn = GetScenario();
        var loje = scn.GetLSCard("loje");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, loje);
        scn.SkipToLSTurn(Phase.CONTROL);

        scn.LSForceDrainAt(site);
        scn.PassForceDrainStartResponses();
        scn.DSPayRemainingForceLossFromReserveDeck();
        scn.DSPass(); // FORCE_DRAIN_COMPLETED responses
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(loje, "Activate"));
        scn.LSUseCardAction(loje, "Activate");
        scn.PassAllResponses();
        assertEquals(force + 1, scn.GetLSForcePileCount());
    }

    @Test
    public void LojeNellaVProtectsYourForceDrainBonusesWithFourCardsStacked() {
        var scn = GetScenario();
        var loje = scn.GetLSCard("loje");
        var laugh = scn.GetLSCard("laugh");
        var ithorian = scn.GetLSCard("ithorian");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, loje, ithorian);
        scn.MoveCardsToLSSideOfTable(laugh);
        // Stand-in for an opponent's card that cancels your Force drain bonuses (e.g. Imperial Decree)
        scn.ApplyAdHocModifier(new CancelOpponentsForceDrainBonusesModifier(scn.GetDSStartingLocation(), Filters.any));

        scn.StackCardsFaceDownOn(laugh, scn.GetLSCard("s1"), scn.GetLSCard("s2"), scn.GetLSCard("s3"));
        assertTrue(scn.game().getModifiersQuerying().isForceDrainModifierCanceled(scn.gameState(), site, ithorian, scn.LS, scn.LS, 1));

        scn.StackCardsFaceDownOn(laugh, scn.GetLSCard("s4"));
        assertFalse(scn.game().getModifiersQuerying().isForceDrainModifierCanceled(scn.gameState(), site, ithorian, scn.LS, scn.LS, 1));
    }
}
