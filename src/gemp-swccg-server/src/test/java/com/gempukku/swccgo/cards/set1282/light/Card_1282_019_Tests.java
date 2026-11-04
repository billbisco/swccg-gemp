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
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_019_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rennek", "1282_19");
                    put("laugh", "1282_21"); // I Know That Laugh
                    put("ithorian", "6_20"); // alien
                    put("devaronian", "6_10"); // alien
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
    public void RennekVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rennek").getBlueprint();

        assertEquals("Rennek", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RennekVMayBeStackedFaceUpOnIKnowThatLaughFromHandUsing2Force() {
        var scn = GetScenario();
        var rennek = scn.GetLSCard("rennek");
        var laugh = scn.GetLSCard("laugh");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.MoveCardsToLSHand(rennek);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(2);

        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(rennek, "Stack"));
        scn.LSUseCardAction(rennek, "Stack");
        scn.PassAllResponses();

        assertTrue(scn.IsStackedOn(laugh, rennek));
        assertEquals(Zone.STACKED, rennek.getZone());
        assertEquals(force - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void RennekVCannotBeStackedWithoutIKnowThatLaugh() {
        var scn = GetScenario();
        var rennek = scn.GetLSCard("rennek");

        scn.StartGame();
        scn.MoveCardsToLSHand(rennek);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(2);

        assertFalse(scn.LSCardActionAvailable(rennek, "Stack"));
    }

    private void battleWithRennekStacked(VirtualTableScenario scn) {
        var rennek = scn.GetLSCard("rennek");
        var laugh = scn.GetLSCard("laugh");
        var ithorian = scn.GetLSCard("ithorian");
        var devaronian = scn.GetLSCard("devaronian");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.StackCardsOn(laugh, rennek);
        scn.MoveCardsToLocation(site, ithorian, devaronian, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }

    @Test
    public void RennekVResetsAParticipatingCharactersForfeitTo0WhenDestinyLessThanYourAliensInBattle() {
        var scn = GetScenario();
        var rennek = scn.GetLSCard("rennek");
        var trooper = scn.GetDSCard("trooper");

        battleWithRennekStacked(scn);
        assertTrue(scn.GetForfeit(trooper) > 0);
        scn.PrepareLSDestiny(1); // 1 < two aliens in battle
        assertTrue(scn.LSCardActionAvailable(rennek, "Draw destiny"));
        scn.LSUseCardAction(rennek, "Draw destiny");
        scn.PassAllResponses();
        assertTrue(scn.LSHasCardChoiceAvailable(trooper));
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();

        assertEquals(0, scn.GetForfeit(trooper));
        // Once per battle
        if (!scn.LSAnyDecisionsAvailable()) {
            scn.DSPass();
        }
        assertFalse(scn.LSCardActionAvailable(rennek, "Draw destiny"));
    }

    @Test
    public void RennekVDoesNothingWhenDestinyIsNotLessThanYourAliensInBattle() {
        var scn = GetScenario();
        var rennek = scn.GetLSCard("rennek");
        var trooper = scn.GetDSCard("trooper");

        battleWithRennekStacked(scn);
        int forfeit = scn.GetForfeit(trooper);
        scn.PrepareLSDestiny(2); // 2 is not < two aliens in battle
        scn.LSUseCardAction(rennek, "Draw destiny");
        scn.PassAllResponses();

        assertFalse(scn.LSHasCardChoiceAvailable(trooper));
        assertEquals(forfeit, scn.GetForfeit(trooper));
    }
}
