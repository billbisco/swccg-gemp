package com.gempukku.swccgo.cards.set1282.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_026_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("protection", "1282_26");
                    put("laugh", "1282_21"); // I Know That Laugh
                    put("rennek", "1282_19"); // Rennek (V): stacks face up
                    put("ithorian", "6_20");
                    put("ithorian2", "6_20");
                    put("ithorian3", "6_20");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("vadersaber", "1_324"); // Vader's Lightsaber: two destiny, hit if total > defense value
                }},
                30, 10,
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
    public void UnderJabbasProtectionStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("protection").getBlueprint();

        assertEquals("Under Jabba's Protection", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void UnderJabbasProtectionUses1ForceToTakeACardStackedOnIKnowThatLaughIntoHand() {
        var scn = GetScenario();
        var protection = scn.GetLSCard("protection");
        var laugh = scn.GetLSCard("laugh");
        var ithorian = scn.GetLSCard("ithorian");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.StackCardsFaceDownOn(laugh, ithorian);
        scn.MoveCardsToLSHand(protection);
        scn.SkipToLSTurn(Phase.CONTROL);

        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(protection, "into hand"));
        scn.LSPlayCard(protection, "into hand");
        scn.PassAllResponses();
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(ithorian)) {
            scn.LSChooseCard(ithorian);
            scn.PassAllResponses();
        }

        assertInHand(ithorian);
        assertEquals(force - 1, scn.GetLSForcePileCount());
    }

    @Test
    public void UnderJabbasProtectionStacksTopCardOfLostPileOnIKnowThatLaugh() {
        var scn = GetScenario();
        var protection = scn.GetLSCard("protection");
        var laugh = scn.GetLSCard("laugh");
        var ithorian = scn.GetLSCard("ithorian");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.MoveCardsToTopOfLSLostPile(ithorian);
        scn.MoveCardsToLSHand(protection);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardPlayAvailable(protection, "Lost Pile"));
        scn.LSPlayCard(protection, "Lost Pile");
        scn.PassAllResponses();

        assertTrue(scn.IsStackedOn(laugh, ithorian));
        assertEquals(Zone.STACKED_FACE_DOWN, ithorian.getZone());
    }

    @Test
    public void UnderJabbasProtectionStacksRennekVFaceUp() {
        var scn = GetScenario();
        var protection = scn.GetLSCard("protection");
        var laugh = scn.GetLSCard("laugh");
        var rennek = scn.GetLSCard("rennek");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.MoveCardsToTopOfLSLostPile(rennek);
        scn.MoveCardsToLSHand(protection);
        scn.SkipToLSTurn(Phase.CONTROL);

        scn.LSPlayCard(protection, "Lost Pile");
        scn.PassAllResponses();

        assertTrue(scn.IsStackedOn(laugh, rennek));
        assertEquals(Zone.STACKED, rennek.getZone());
    }

    /** Opponent battles and draws battle destiny 5; returns their total battle destiny once both players are done. */
    private int opponentsTotalBattleDestiny(VirtualTableScenario scn, boolean playUnderJabbasProtection) {
        var protection = scn.GetLSCard("protection");
        var laugh = scn.GetLSCard("laugh");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.StackCardsFaceDownOn(laugh, scn.GetLSCard("ithorian"), scn.GetLSCard("ithorian2"), scn.GetLSCard("ithorian3"));
        scn.MoveCardsToLocation(site, luke, vader);
        scn.MoveCardsToLSHand(protection);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.SkipToPowerSegment();
        scn.PrepareDSDestiny(5);
        assertTrue(scn.DSDecisionAvailable("battle destiny?"));
        scn.DSChooseYes();

        assertTrue(LegacyVb82TestSupport.passUntilLSCardAction(scn, protection, "battle destiny total"));
        if (playUnderJabbasProtection) {
            scn.LSPlayCard(protection, "battle destiny total");
            scn.PassAllResponses();
            assertTrue(scn.LSDecisionAvailable("stacked on I Know That Laugh"));
            scn.LSDecided(2);
            scn.PassAllResponses();
            for (int i = 0; i < 6 && scn.LSAnyDecisionsAvailable() && scn.LSGetDecision().getText().contains("Lost Pile"); i++) {
                for (String key : new String[]{"ithorian", "ithorian2", "ithorian3"}) {
                    if (scn.LSHasCardChoiceAvailable(scn.GetLSCard(key))) {
                        scn.LSChooseCard(scn.GetLSCard(key));
                        break;
                    }
                }
                scn.PassAllResponses();
            }
            assertEquals(1, scn.GetStackedCards(laugh).size());
        }
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("battle destiny?")) {
            scn.LSChooseNo();
        }
        scn.PassAllResponses();
        return scn.GetDSTotalDestiny();
    }

    @Test
    public void UnderJabbasProtectionLosesXStackedCardsToReduceOpponentsBattleDestinyTotalByX() {
        int without = opponentsTotalBattleDestiny(GetScenario(), false);
        int with = opponentsTotalBattleDestiny(GetScenario(), true);
        assertEquals(without - 2, with);
    }

    /** Opponent fires Vader's Lightsaber (destiny 4 and 3) at Luke; returns whether Luke was hit. */
    private boolean lukeHitByVadersLightsaber(VirtualTableScenario scn, boolean playUnderJabbasProtection) {
        var protection = scn.GetLSCard("protection");
        var laugh = scn.GetLSCard("laugh");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var vadersaber = scn.GetDSCard("vadersaber");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(laugh);
        scn.StackCardsFaceDownOn(laugh, scn.GetLSCard("ithorian"), scn.GetLSCard("ithorian2"), scn.GetLSCard("ithorian3"));
        scn.MoveCardsToLocation(site, luke, vader);
        scn.AttachCardsTo(vader, vadersaber);
        scn.MoveCardsToLSHand(protection);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.PrepareDSDestiny(3);
        scn.PrepareDSDestiny(4);
        scn.DSUseCardAction(vadersaber, "Fire");
        scn.DSChooseCard(luke);
        scn.PassForceUseResponses();

        assertTrue(LegacyVb82TestSupport.passUntilLSCardAction(scn, protection, "weapon destiny total"));
        if (playUnderJabbasProtection) {
            scn.LSPlayCard(protection, "weapon destiny total");
            scn.PassAllResponses();
            assertTrue(scn.LSDecisionAvailable("stacked on I Know That Laugh"));
            scn.LSDecided(3);
            scn.PassAllResponses();
            for (int i = 0; i < 6 && scn.LSAnyDecisionsAvailable() && scn.LSGetDecision().getText().contains("Lost Pile"); i++) {
                for (String key : new String[]{"ithorian", "ithorian2", "ithorian3"}) {
                    if (scn.LSHasCardChoiceAvailable(scn.GetLSCard(key))) {
                        scn.LSChooseCard(scn.GetLSCard(key));
                        break;
                    }
                }
                scn.PassAllResponses();
            }
            assertEquals(0, scn.GetStackedCards(laugh).size());
        }
        scn.PassAllResponses();
        return luke.isHit();
    }

    @Test
    public void UnderJabbasProtectionLosesXStackedCardsToReduceOpponentsWeaponDestinyTotalByX() {
        // 4 + 3 > Luke's defense value 4: hit; reduced by 3 to 4 it is not
        assertTrue(lukeHitByVadersLightsaber(GetScenario(), false));
        assertFalse(lukeHitByVadersLightsaber(GetScenario(), true));
    }
}
