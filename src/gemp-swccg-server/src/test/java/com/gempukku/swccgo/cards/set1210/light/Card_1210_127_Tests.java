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

public class Card_1210_127_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("icbhg", "1210_127");
                    put("obi", "1_21");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
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
    public void ICantBelieveHesGoneStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("icbhg").getBlueprint();

        assertEquals("I Can't Believe He's Gone", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.TATOOINE);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ICantBelieveHesGoneDeploysWhenObiWanIsOutOfPlay() {
        var scn = GetScenario();
        var icbhg = scn.GetLSCard("icbhg");
        var obi = scn.GetLSCard("obi");

        scn.StartGame();
        scn.MoveCardsToLSHand(icbhg);
        scn.MoveOutOfPlay(obi);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(icbhg));
        scn.LSPlayCard(icbhg);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, icbhg);
        assertEquals(Zone.OUT_OF_PLAY, obi.getZone());
    }

    @Test
    public void ICantBelieveHesGoneDoesNotDeployWhenObiWanIsInPlay() {
        var scn = GetScenario();
        var icbhg = scn.GetLSCard("icbhg");

        scn.StartGame();
        scn.MoveCardsToLSHand(icbhg);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSCardPlayAvailable(icbhg));
        assertEquals(Zone.HAND, icbhg.getZone());
    }

    @Test
    public void ICantBelieveHesGoneAddsFiveTotalPowerOncePerBattle() {
        var scn = GetScenario();
        var icbhg = scn.GetLSCard("icbhg");
        var obi = scn.GetLSCard("obi");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveOutOfPlay(obi);
        scn.MoveCardsToLSSideOfTable(icbhg);
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.LSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(icbhg, "Add 5"));
        scn.LSUseCardAction(icbhg, "Add 5");
        scn.PassForceUseResponses();
        scn.PassWeaponsSegmentActions();
        scn.SkipBattleDestinyDraws(false);

        assertEquals(scn.GetPower(luke) + 5, scn.GetLSTotalPower());
    }

    @Test
    public void ICantBelieveHesGoneActivatesOneWhenLukeIsACaptive() {
        var scn = GetScenario();
        var icbhg = scn.GetLSCard("icbhg");
        var obi = scn.GetLSCard("obi");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveOutOfPlay(obi);
        scn.MoveCardsToLSSideOfTable(icbhg);
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.CaptureCardWith(trooper, luke);
        scn.SkipToLSTurn(Phase.CONTROL);
        int forceBefore = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(icbhg, "Activate 1"));
        scn.LSUseCardAction(icbhg, "Activate 1");
        scn.PassAllResponses();

        assertEquals(forceBefore + 1, scn.GetLSForcePileCount());
    }
}
