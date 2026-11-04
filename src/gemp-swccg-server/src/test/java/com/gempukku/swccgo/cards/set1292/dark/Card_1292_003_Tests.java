package com.gempukku.swccgo.cards.set1292.dark;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1292_003_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                    put("trooper", "1_28");
                }},
                new HashMap<>() {{
                    put("ihyn", "1292_3");
                    put("vader", "1_168");
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
    public void IHaveYouNowVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("ihyn").getBlueprint();

        assertEquals("I Have You Now", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void startBattleWithVaderAgainst(VirtualTableScenario scn, PhysicalCardImpl opponentsCharacter) {
        var vader = scn.GetDSCard("vader");
        var ihyn = scn.GetDSCard("ihyn");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, opponentsCharacter);
        scn.MoveCardsToDSHand(ihyn);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(4);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
    }

    private void finishDestinyDraws(VirtualTableScenario scn) {
        for (int i = 0; i < 20; i++) {
            String text = scn.GetCurrentDecision().getText();
            if (!text.contains("DESTINY")) {
                break;
            }
            if (text.contains("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS")) {
                break;
            }
            scn.PassResponses();
        }
    }

    @Test
    public void IHaveYouNowVAddsOneBattleDestinyWhenVaderInBattleWithCharacterOfAbilityMoreThanTwo() {
        var scn = GetScenario();
        var ihyn = scn.GetDSCard("ihyn");
        var leia = scn.GetLSCard("leia");

        startBattleWithVaderAgainst(scn, leia);
        assertTrue(scn.DSCardPlayAvailable(ihyn));
        scn.DSPlayCard(ihyn);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, ihyn);

        scn.PassWeaponsSegmentActions();
        scn.PrepareDSDestiny(1);
        scn.PrepareDSDestiny(2);
        scn.PrepareDSDestiny(3);
        scn.SkipBattleDestinyDraws(true);
        finishDestinyDraws(scn);
        assertEquals(2, scn.GetDSBattleDestinyCount());
        // 3 + 2, each +1 from Vader's own game text; Leia is not Luke or Anakin, so no further +1
        assertEquals(7, scn.GetDSTotalDestiny());
    }

    @Test
    public void IHaveYouNowVWithLukeMakesAllDestinyDrawsPlusOne() {
        var scn = GetScenario();
        var ihyn = scn.GetDSCard("ihyn");
        var luke = scn.GetLSCard("luke");

        startBattleWithVaderAgainst(scn, luke);
        assertTrue(scn.DSCardPlayAvailable(ihyn));
        scn.DSPlayCard(ihyn);
        scn.PassAllResponses();

        scn.PassWeaponsSegmentActions();
        scn.PrepareDSDestiny(1);
        scn.PrepareDSDestiny(2);
        scn.PrepareDSDestiny(3);
        scn.SkipBattleDestinyDraws(true);
        finishDestinyDraws(scn);
        assertEquals(2, scn.GetDSBattleDestinyCount());
        // 3 + 2, each +1 from Vader and +1 from I Have You Now (V)
        assertEquals(9, scn.GetDSTotalDestiny());
    }

    @Test
    public void IHaveYouNowVNotPlayableAgainstCharacterOfAbilityTwoOrLess() {
        var scn = GetScenario();
        var ihyn = scn.GetDSCard("ihyn");
        var trooper = scn.GetLSCard("trooper");

        startBattleWithVaderAgainst(scn, trooper);
        assertFalse(scn.DSCardPlayAvailable(ihyn));
    }
}
