package com.gempukku.swccgo.cards.set1108.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1108_046_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("pistol", "1_154");
                }},
                new HashMap<>() {{
                    put("urorru", "1108_46");
                    put("tusken", "1_196");
                    put("trooper", "1_194");
                    put("handcard", "1_317");
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
    public void URoRRuRRRStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("urorru").getBlueprint();

        assertEquals("URoRRuR'R'R", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LEADER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.A_NEW_HOPE);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void URoRRuRRRAddsFourForfeitToOtherTuskenRaidersHere() {
        var scn = GetScenario();
        var urorru = scn.GetDSCard("urorru");
        var tusken = scn.GetDSCard("tusken");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, urorru, tusken);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(5, scn.GetForfeit(tusken));
    }

    @Test
    public void URoRRuRRRSubtractsTwoFromOpponentsWeaponDestinyHere() {
        var scn = GetScenario();
        var urorru = scn.GetDSCard("urorru");
        var tusken = scn.GetDSCard("tusken");
        var luke = scn.GetLSCard("luke");
        var pistol = scn.GetLSCard("pistol");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, urorru, tusken, luke);
        scn.AttachCardsTo(luke, pistol);
        scn.PrepareLSDestiny(1);
        scn.LSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(pistol, "Fire"));
        scn.LSUseCardAction(pistol, "Fire");
        scn.LSChooseCard(tusken);
        scn.PassAllResponses();

        assertFalse(tusken.isHit());
    }

    @Test
    public void URoRRuRRRPlacesCardOnForcePileToDraw() {
        var scn = GetScenario();
        var urorru = scn.GetDSCard("urorru");
        var tusken = scn.GetDSCard("tusken");
        var handcard = scn.GetDSCard("handcard");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, urorru, tusken);
        scn.MoveCardsToDSHand(handcard, trooper);
        scn.SkipToDSTurn(Phase.CONTROL);
        int forceBefore = scn.GetDSForcePileCount();
        int handBefore = scn.GetDSHandCount();
        assertTrue(scn.DSCardActionAvailable(urorru, "Place card on Force Pile"));
        scn.DSUseCardAction(urorru, "Place card on Force Pile");
        assertTrue(scn.DSDecisionAvailable("Choose card"));
        scn.DSChooseCard(handcard);
        scn.PassAllResponses();

        assertEquals(forceBefore + 1, scn.GetDSForcePileCount());
        assertEquals(handBefore, scn.GetDSHandCount());
    }
}
