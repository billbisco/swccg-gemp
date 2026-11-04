package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardSubtype;
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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("trooper", "1_28");
                    put("blaster", "1_152");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("ornament", "1281_16");
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
    public void AbyssinOrnamentVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("ornament").getBlueprint();

        assertEquals("Abyssin Ornament", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AbyssinOrnamentVCancelsWeaponTargetingYourNonDroidAssassin() {
        var scn = GetScenario();
        var ornament = scn.GetDSCard("ornament");
        var trooper = scn.GetLSCard("trooper");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        var assassin = MissionTestSupport.putInReserve(scn, 98101, scn.DS, Vb81RestTestSupport.assassin("Test Assassin"));
        scn.MoveCardsToLocation(site, assassin, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.MoveCardsToDSHand(ornament);
        scn.LSActivateForceCheat(4);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(assassin);
        scn.DSPass();
        scn.LSPass();
        assertTrue(scn.DSPlayUsedInterruptAvailable(ornament));
        scn.DSPlayUsedInterrupt(ornament);
        scn.PassAllResponses();

        assertAtLocation(site, assassin);
        assertFalse(assassin.isHit());
        assertInZone(Zone.USED_PILE, ornament);
    }

    @Test
    public void AbyssinOrnamentVDoesNotCancelWeaponTargetingNonAssassin() {
        var scn = GetScenario();
        var ornament = scn.GetDSCard("ornament");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var trooper = scn.GetLSCard("trooper");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, stormtrooper, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.MoveCardsToDSHand(ornament);
        scn.LSActivateForceCheat(4);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(stormtrooper);
        scn.DSPass();
        scn.LSPass();
        assertFalse(scn.DSPlayUsedInterruptAvailable(ornament));
    }

    @Test
    public void AbyssinOrnamentVUsesOneForceToTakeJustLostAssassinIntoHand() {
        var scn = GetScenario();
        var ornament = scn.GetDSCard("ornament");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        var assassin = MissionTestSupport.putInReserve(scn, 98102, scn.DS, Vb81RestTestSupport.assassin("Test Assassin"));
        scn.MoveCardsToLocation(site, assassin, luke);
        scn.MoveCardsToDSHand(ornament);
        scn.DSActivateForceCheat(3);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PrepareLSDestiny(3);
        scn.PrepareDSDestiny(0);
        scn.SkipToDamageSegment(true);
        assertTrue(scn.AwaitingDSBattleDamagePayment());
        int force = scn.GetDSForcePileCount();
        scn.DSChooseCard(assassin);
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.DSPlayLostInterruptAvailable(ornament)));
        scn.DSPlayLostInterrupt(ornament);
        scn.PassAllResponses();

        assertInHand(assassin);
        assertInZone(Zone.LOST_PILE, ornament);
        assertEquals(force - 1, scn.GetDSForcePileCount());
    }
}
