package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1281_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("han", "1281_27");
                    put("blaster", "1281_46");
                    put("falcon", "1_143");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("dsblaster", "1_312");
                    put("vader", "1_168");
                    put("saber", "1_324"); // Vader's Lightsaber: draw two destiny
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
    public void HanSoloInnocentScoundrelStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("han").getBlueprint();

        assertEquals("Han Solo, Innocent Scoundrel", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        assertTrue(card.hasSpecialDefenseValueAttribute());
        assertEquals(5, card.getSpecialDefenseValue(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.HAN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HanSoloInnocentScoundrelDefenseValueIsLuckFive() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(3, scn.GetAbility(han));
        assertEquals(5, scn.GetDefense(han));
    }

    @Test
    public void HanSoloInnocentScoundrelAddsThreeToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var falcon = scn.GetLSCard("falcon");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, falcon);
        scn.BoardAsPilot(falcon, han);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(6, scn.GetPower(falcon));
    }

    @Test
    public void HanSoloInnocentScoundrelDrawsBattleDestinyIfUnableToOtherwise() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.SkipToPowerSegment();

        assertTrue(scn.LSDecisionAvailable("battle destiny?"));
        scn.LSChooseYes();
        scn.PassAllResponses();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void HanSoloInnocentScoundrelFiresAVirtualBlock8BlasterTwiceDuringBattle() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(han, blaster);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(5);

        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetTopOfDSForcePile());
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
    }

    @Test
    public void HanSoloInnocentScoundrelIsImmuneToAttritionLessThanThreeAndFourIfArmed() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), han), scn.epsilon);

        scn.AttachCardsTo(han, blaster);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), han), scn.epsilon);
    }

    @Test
    public void HanSoloInnocentScoundrelOpponentDrawsTwoWeaponDestiniesAndHanPlayerChoosesOne() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var dsblaster = scn.GetDSCard("dsblaster");
        var site = scn.GetLSStartingLocation();
        var lowDestiny = scn.GetDSDestiny(1);
        var highDestiny = scn.GetDSDestiny(6);

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(trooper, dsblaster);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        // Top of Reserve: 6 (would hit: 6+1 > 5), then 1 (would miss: 1+1 < 5).
        scn.MoveCardsToTopOfDSReserveDeck(lowDestiny);
        scn.MoveCardsToTopOfDSReserveDeck(highDestiny);

        assertTrue(scn.DSCardActionAvailable(dsblaster, "Fire"));
        scn.DSUseCardAction(dsblaster, "Fire");
        scn.DSChooseCard(han);
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.DSPass();

        assertTrue(scn.LSCardActionAvailable(han, "Opponent draws two weapon destinies"));
        scn.LSUseCardAction(han, "Opponent draws two weapon destinies");
        scn.PassAllResponses();

        // Han's player (not the drawer) chooses which destiny counts.
        assertFalse(scn.DSDecisionAvailable("Choose 1 destiny"));
        assertTrue(scn.LSDecisionAvailable("Choose 1 destiny"));
        scn.LSChooseCard(lowDestiny);
        scn.PassAllResponses();

        assertFalse(han.isHit());
    }

    @Test
    public void HanSoloInnocentScoundrelChosenWeaponDestinyIsTheOneUsed() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var dsblaster = scn.GetDSCard("dsblaster");
        var site = scn.GetLSStartingLocation();
        var lowDestiny = scn.GetDSDestiny(1);
        var highDestiny = scn.GetDSDestiny(6);

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(trooper, dsblaster);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        // Top of Reserve: 6 (would hit: 6+1 > 5), then 1 (would miss: 1+1 < 5).
        scn.MoveCardsToTopOfDSReserveDeck(lowDestiny);
        scn.MoveCardsToTopOfDSReserveDeck(highDestiny);

        assertTrue(scn.DSCardActionAvailable(dsblaster, "Fire"));
        scn.DSUseCardAction(dsblaster, "Fire");
        scn.DSChooseCard(han);
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.DSPass();

        assertTrue(scn.LSCardActionAvailable(han, "Opponent draws two weapon destinies"));
        scn.LSUseCardAction(han, "Opponent draws two weapon destinies");
        scn.PassAllResponses();

        // Han's player (not the drawer) chooses which destiny counts.
        assertFalse(scn.DSDecisionAvailable("Choose 1 destiny"));
        assertTrue(scn.LSDecisionAvailable("Choose 1 destiny"));
        scn.LSChooseCard(highDestiny);
        scn.PassAllResponses();

        assertTrue(han.isHit());
    }

    /**
     * Vader fires Vader's Lightsaber (draw two destiny, hit if total > 5) at Han during battle, with the three given
     * cards on top of Dark Side's Reserve Deck. Stops at Han's player's response window before the first weapon destiny.
     */
    private void vaderFiresLightsaberAtHan(VirtualTableScenario scn, int topDestiny, int nextDestiny, int thirdDestiny) {
        var han = scn.GetLSCard("han");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, vader);
        scn.AttachCardsTo(vader, saber);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(thirdDestiny));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(nextDestiny));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(topDestiny));

        assertTrue(scn.DSCardActionAvailable(saber, "Fire"));
        scn.DSUseCardAction(saber, "Fire");
        scn.DSChooseCard(han);
        scn.PassResponses("Fire ");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
    }

    /**
     * Both players pass the responses of one weapon destiny card being drawn, up to the next card's "about to draw".
     */
    private void passOneDestinyCardDrawn(VirtualTableScenario scn) {
        scn.PassResponses("DESTINY_DRAWN");
        scn.PassResponses("COMPLETE_DESTINY_DRAW");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
    }

    @Test
    public void HanSoloInnocentScoundrelReplacesOnlyTheFirstOfTwoWeaponDestiniesWhenUsedBeforeIt() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var first = scn.GetDSDestiny(1);
        var second = scn.GetDSDestiny(3);
        var third = scn.GetDSDestiny(4);
        //test1: before the first weapon destiny, Han's player is offered the draw-two-choose-one
        //test2: no second offer while the two cards for that destiny are drawn
        //test3: Han's player (not the drawer) chooses between the two cards drawn for the first destiny
        //test4: the second weapon destiny is drawn normally with no offer and added to the chosen card (3 + 4 = 7 > 5: hit)
        //test5: three cards left Reserve Deck in total

        vaderFiresLightsaberAtHan(scn, 1, 3, 4);
        int reserveBefore = scn.GetDSReserveDeckCount();

        assertTrue(scn.LSDecisionAvailable("About to draw weapon destiny")); //test1
        assertTrue(scn.LSCardActionAvailable(han, "Opponent draws two weapon destinies")); //test1
        scn.LSUseCardAction(han, "Opponent draws two weapon destinies");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        passOneDestinyCardDrawn(scn);

        assertTrue(scn.LSDecisionAvailable("ABOUT_TO_DRAW_DESTINY_CARD")); //test2
        assertFalse(scn.LSCardActionAvailable(han, "Opponent draws two weapon destinies")); //test2
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("DESTINY_DRAWN");
        scn.PassResponses("COMPLETE_DESTINY_DRAW");

        assertFalse(scn.DSDecisionAvailable("Choose 1 destiny")); //test3
        assertTrue(scn.LSDecisionAvailable("Choose 1 destiny")); //test3
        assertTrue(scn.LSHasCardChoiceAvailable(first)); //test3
        assertTrue(scn.LSHasCardChoiceAvailable(second)); //test3
        scn.LSChooseCard(second);

        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.LSDecisionAvailable("ABOUT_TO_DRAW_DESTINY_CARD")); //test4
        assertFalse(scn.LSCardActionAvailable(han, "Opponent draws two weapon destinies")); //test4
        scn.PassAllResponses();

        assertTrue(han.isHit()); //test4
        assertEquals(3, reserveBefore - scn.GetDSReserveDeckCount()); //test5
    }

    @Test
    public void HanSoloInnocentScoundrelChosenFirstDestinyPlusNormalSecondDestinyCanStillMiss() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var first = scn.GetDSDestiny(1);
        //test1: Han's player chooses the 1 for the first destiny; only it counts (1 + 4 = 5, not > 5: miss)

        vaderFiresLightsaberAtHan(scn, 1, 3, 4);
        scn.LSUseCardAction(han, "Opponent draws two weapon destinies");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        passOneDestinyCardDrawn(scn);
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("DESTINY_DRAWN");
        scn.PassResponses("COMPLETE_DESTINY_DRAW");

        assertTrue(scn.LSDecisionAvailable("Choose 1 destiny"));
        scn.LSChooseCard(first);
        scn.PassAllResponses();

        assertFalse(han.isHit()); //test1
    }

    @Test
    public void HanSoloInnocentScoundrelIsOfferedAgainBeforeTheSecondWeaponDestinyAfterDecliningTheFirst() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var second = scn.GetDSDestiny(1);
        var third = scn.GetDSDestiny(4);
        //test1: Han's player declines before the first weapon destiny; it is drawn normally (2)
        //test2: Han's player is offered again before the second weapon destiny and uses it
        //test3: Han's player chooses between the two cards drawn for the second destiny; 2 + 4 = 6 > 5: hit
        //test4: three cards left Reserve Deck in total

        vaderFiresLightsaberAtHan(scn, 2, 1, 4);
        int reserveBefore = scn.GetDSReserveDeckCount();

        assertTrue(scn.LSCardActionAvailable(han, "Opponent draws two weapon destinies")); //test1
        scn.LSPass();
        scn.DSPass();
        passOneDestinyCardDrawn(scn);

        assertTrue(scn.LSDecisionAvailable("About to draw weapon destiny")); //test2
        assertTrue(scn.LSCardActionAvailable(han, "Opponent draws two weapon destinies")); //test2
        scn.LSUseCardAction(han, "Opponent draws two weapon destinies");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        passOneDestinyCardDrawn(scn);
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("DESTINY_DRAWN");
        scn.PassResponses("COMPLETE_DESTINY_DRAW");

        assertFalse(scn.DSDecisionAvailable("Choose 1 destiny")); //test3
        assertTrue(scn.LSDecisionAvailable("Choose 1 destiny")); //test3
        assertTrue(scn.LSHasCardChoiceAvailable(second)); //test3
        assertTrue(scn.LSHasCardChoiceAvailable(third)); //test3
        scn.LSChooseCard(third);
        scn.PassAllResponses();

        assertTrue(han.isHit()); //test3
        assertEquals(3, reserveBefore - scn.GetDSReserveDeckCount()); //test4
    }

    @Test
    public void HanSoloInnocentScoundrelChosenSecondDestinyIsTheOneAddedToTheFirst() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var second = scn.GetDSDestiny(1);
        //test1: after declining the first (2), Han's player uses it on the second and chooses the 1: 2 + 1 = 3, miss

        vaderFiresLightsaberAtHan(scn, 2, 1, 4);
        scn.LSPass();
        scn.DSPass();
        passOneDestinyCardDrawn(scn);
        scn.LSUseCardAction(han, "Opponent draws two weapon destinies");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        passOneDestinyCardDrawn(scn);
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("DESTINY_DRAWN");
        scn.PassResponses("COMPLETE_DESTINY_DRAW");

        assertTrue(scn.LSDecisionAvailable("Choose 1 destiny"));
        scn.LSChooseCard(second);
        scn.PassAllResponses();

        assertFalse(han.isHit()); //test1
    }

    @Test
    public void HanSoloInnocentScoundrelDeclinedForBothWeaponDestiniesDrawsTwoNormally() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var third = scn.GetDSDestiny(7);
        //test1: offered before each of the two weapon destinies and declined both times
        //test2: exactly two cards are drawn (2 + 3 = 5, not > 5: miss); the 7 underneath stays in Reserve Deck

        vaderFiresLightsaberAtHan(scn, 2, 3, 7);
        int reserveBefore = scn.GetDSReserveDeckCount();

        assertTrue(scn.LSCardActionAvailable(han, "Opponent draws two weapon destinies")); //test1
        scn.LSPass();
        scn.DSPass();
        passOneDestinyCardDrawn(scn);

        assertTrue(scn.LSCardActionAvailable(han, "Opponent draws two weapon destinies")); //test1
        scn.LSPass();
        scn.DSPass();
        scn.PassAllResponses();

        assertFalse(scn.LSDecisionAvailable("Choose 1 destiny")); //test2
        assertFalse(han.isHit()); //test2
        assertEquals(2, reserveBefore - scn.GetDSReserveDeckCount()); //test2
        assertInZone(Zone.RESERVE_DECK, third); //test2
    }

    @Test
    public void HanSoloInnocentScoundrelIsNotOfferedForASecondWeaponFiredAtHimThisTurn() {
        var scn = GetScenario();
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var dsblaster = scn.GetDSCard("dsblaster");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");
        var site = scn.GetLSStartingLocation();
        //test1: Han's player uses the ability on Vader's Lightsaber's first destiny and chooses the 1 (1 + 2 = 3: miss)
        //test2: the trooper's Blaster Rifle then fires at Han in the same battle: no offer before its weapon destiny

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, vader, trooper);
        scn.AttachCardsTo(vader, saber);
        scn.AttachCardsTo(trooper, dsblaster);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(2));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(6));
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSDestiny(1));

        scn.DSUseCardAction(saber, "Fire");
        scn.DSChooseCard(han);
        scn.PassResponses("Fire ");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.LSCardActionAvailable(han, "Opponent draws two weapon destinies")); //test1
        scn.LSUseCardAction(han, "Opponent draws two weapon destinies");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        passOneDestinyCardDrawn(scn);
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("DESTINY_DRAWN");
        scn.PassResponses("COMPLETE_DESTINY_DRAW");
        scn.LSChooseCard(scn.GetDSDestiny(1));
        scn.PassAllResponses();
        assertFalse(han.isHit()); //test1

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(dsblaster, "Fire"));
        scn.DSUseCardAction(dsblaster, "Fire");
        scn.DSChooseCard(han);
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        assertTrue(scn.LSDecisionAvailable("ABOUT_TO_DRAW_DESTINY_CARD")); //test2
        assertFalse(scn.LSCardActionAvailable(han, "Opponent draws two weapon destinies")); //test2
    }
}
