package com.gempukku.swccgo.cards.set1210.light;

import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_134_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hour", "1210_134");
                    put("leia", "1_17");
                    put("highLeia", "5_7");
                    put("guard", "1_26");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }

    @Test
    public void OurMostDesperateHourStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hour").getBlueprint();

        assertEquals("Our Most Desperate Hour", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void OurMostDesperateHourDeploysOnLeiaOfAbilityLessThanFour() {
        var scn = GetScenario();
        var hour = scn.GetLSCard("hour");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        scn.MoveCardsToLSHand(hour);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(hour));
        scn.LSPlayCard(hour);
        scn.LSChooseCard(leia);
        scn.PassAllResponses();
        assertEquals(leia, hour.getAttachedTo());
    }

    @Test
    public void OurMostDesperateHourDoesNotDeployOnHighAbilityLeia() {
        var scn = GetScenario();
        var hour = scn.GetLSCard("hour");
        var highLeia = scn.GetLSCard("highLeia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, highLeia);
        scn.MoveCardsToLSHand(hour);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(hour));
        assertEquals(Zone.HAND, hour.getZone());
    }

    @Test
    public void OurMostDesperateHourMakesLeiaPowerAndDefensePlusTwoAndMoveForFree() {
        var scn = GetScenario();
        var hour = scn.GetLSCard("hour");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        int power = scn.GetPower(leia);
        int defense = scn.GetDefense(leia);
        scn.AttachCardsTo(leia, hour);
        assertEquals(power + 2, scn.GetPower(leia));
        assertEquals(defense + 2, scn.GetDefense(leia));
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MOVES_FREE, leia).isEmpty());
        assertEquals(0, scn.game().getModifiersQuerying().getMoveUsingLandspeedCost(
                scn.gameState(), leia, site, marketplace, false, 0), scn.epsilon);
    }

    @Test
    public void OurMostDesperateHourMakesLeiaDrawOneBattleDestinyIfUnableToOtherwise() {
        var scn = GetScenario();
        var hour = scn.GetLSCard("hour");
        var leia = scn.GetLSCard("leia");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, trooper);
        scn.AttachCardsTo(leia, hour);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PassWeaponsSegmentActions();
        scn.PassResponses("BEFORE_BATTLE_DESTINY_DRAWS");
        assertTrue(scn.LSDecisionAvailable("battle destiny"));
        scn.PrepareLSDestiny(4);
        scn.LSChooseYes();
        scn.PassDestinyDrawResponses();
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        if (scn.DSDecisionAvailable("battle destiny")) {
            scn.DSChooseNo();
            scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_PLAYER");
        }
        scn.PassResponses("BATTLE_DESTINY_DRAWS_COMPLETE_FOR_BOTH_PLAYERS");
        assertEquals(4, scn.GetLSTotalDestiny());
    }

    @Test
    public void OurMostDesperateHourReducesRebelDeployHereToAMinimumOfOneWhenLeiaHasSpySkill() {
        var scn = GetScenario();
        var hour = scn.GetLSCard("hour");
        var leia = scn.GetLSCard("leia");
        var guard = scn.GetLSCard("guard");
        var site = scn.GetLSStartingLocation();
        var marketplace = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        scn.AttachCardsTo(leia, hour);
        assertEquals(1, deployCostTo(scn, guard, site), scn.epsilon);
        assertEquals(2, deployCostTo(scn, guard, marketplace), scn.epsilon);
    }

    @Test
    public void OurMostDesperateHourPlacesLeiaAndThisEffectInUsedPileInsteadOfLost() {
        var scn = GetScenario();
        var hour = scn.GetLSCard("hour");
        var leia = scn.GetLSCard("leia");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        if (scn.DSDecisionAvailable("Choose starting interrupt")) {
            scn.DSPass();
        }
        if (scn.LSDecisionAvailable("Choose starting interrupt")) {
            scn.LSPass();
        }
        scn.MoveCardsToLocation(site, leia, vader);
        scn.AttachCardsTo(leia, hour);
        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(6);
        scn.PrepareLSDestiny(0);
        scn.DSInitiateBattle(site);
        scn.SkipToDamageSegment(true);
        assertTrue(scn.AwaitingLSAttritionPayment());
        scn.LSChooseCard(leia);
        for (int i = 0; i < 12; i++) {
            if (scn.LSDecisionAvailable("Place Leia") || scn.LSDecisionAvailable("Used Pile")
                    || scn.LSDecisionAvailable("forfeit")) {
                if (lsHasCardActions134(scn) && scn.LSCardActionAvailable(hour, "Place Leia")) {
                    scn.LSUseCardAction(hour, "Place Leia");
                } else if (scn.LSGetDecision() != null && !lsHasCardActions134(scn)) {
                    scn.LSPass();
                } else {
                    break;
                }
                continue;
            }
            break;
        }
        scn.PassAllResponses();
        assertTrue(leia.getZone() == Zone.USED_PILE || leia.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(hour.getZone() == Zone.USED_PILE || hour.getZone() == Zone.TOP_OF_USED_PILE);
    }

    private boolean lsHasCardActions134(VirtualTableScenario scn) {
        return scn.LSGetDecision() != null && scn.LSGetADParam("cardId") != null;
    }
}
