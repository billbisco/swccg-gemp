package com.gempukku.swccgo.cards.set1210.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_162_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("sabotage", "1210_162");
                    put("luke", "1_19");
                    put("spy", "7_5");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317");
                    put("dengar", "110_7");
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
    public void SabotageStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("sabotage").getBlueprint();

        assertEquals("Sabotage", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SabotageMakesWeaponLostWhenDestinyMinusSpiesIsLessThanTargetDestiny() {
        var scn = GetScenario();
        var sabotage = scn.GetLSCard("sabotage");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.MoveCardsToLSHand(sabotage);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(1);

        assertTrue(scn.LSCardPlayAvailable(sabotage));
        scn.LSPlayCard(sabotage);
        assertTrue(scn.LSDecisionAvailable("Choose"));
        scn.LSChooseCard(blaster);
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, blaster);
        assertInZone(Zone.LOST_PILE, sabotage);
    }

    @Test
    public void SabotageDoesNotMakeWeaponLostWhenDestinyMinusSpiesIsNotLess() {
        var scn = GetScenario();
        var sabotage = scn.GetLSCard("sabotage");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.MoveCardsToLSHand(sabotage);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(5);

        assertTrue(scn.LSCardPlayAvailable(sabotage));
        scn.LSPlayCard(sabotage);
        assertTrue(scn.LSDecisionAvailable("Choose"));
        scn.LSChooseCard(blaster);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(trooper, blaster));
        assertInZone(Zone.LOST_PILE, sabotage);
    }

    @Test
    public void SabotageCancelsPermanentWeaponTargetingAtSameSiteAsYourSpy() {
        var scn = GetScenario();
        var sabotage = scn.GetLSCard("sabotage");
        var spy = scn.GetLSCard("spy");
        var dengar = scn.GetDSCard("dengar");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, spy, dengar);
        scn.MoveCardsToLSHand(sabotage);
        scn.DSActivateForceCheat(2);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(dengar, "Fire"));
        scn.DSUseCardAction(dengar, "Fire");
        scn.DSChooseCard(spy);
        scn.PassForceUseResponses();
        assertTrue(scn.LSCardActionAvailable(sabotage, "Cancel"));
        scn.LSUseCardAction(sabotage, "Cancel");
        scn.PassAllResponses();

        assertFalse(spy.isHit());
        assertInZone(Zone.LOST_PILE, sabotage);
    }
}
