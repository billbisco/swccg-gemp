package com.gempukku.swccgo.cards.set1272.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("trophy", "1272_6");
                    put("bossk", "4_92");
                    put("blaster", "1_317");
                    put("trooper", "1_194");
                    put("trooperBlaster", "1_317");
                    put("trooper2", "1_194");
                    put("trooper3", "1_194");
                    put("trooper4", "1_194");
                    put("influence", "7_231");
                    put("tattoo", "7_225");
                    put("jabba", "6_109");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private static boolean inLostPile(PhysicalCardImpl card) {
        return card.getZone() == Zone.LOST_PILE || card.getZone() == Zone.TOP_OF_LOST_PILE;
    }

    @Test
    public void TrophyOfABountyHunterStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("trophy").getBlueprint();

        assertEquals("Trophy Of A Bounty Hunter", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.JABBAS_PALACE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TrophyOfABountyHunterStacksCardHitByYourBountyHunterWhenItLeavesTable() {
        var scn = GetScenario();
        var trophy = scn.GetDSCard("trophy");
        var bossk = scn.GetDSCard("bossk");
        var blaster = scn.GetDSCard("blaster");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        //test1: opponent's character hit by your bounty hunter is stacked here face down when it leaves table
        //test2: your bounty hunters are then power and defense value +1
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(trophy);
        scn.MoveCardsToLocation(site, bossk, luke);
        scn.AttachCardsTo(bossk, blaster);
        int bosskPower = scn.GetPower(bossk);
        int bosskDefense = scn.GetDefense(bossk);
        scn.EnsureDSForcePile(4);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.PrepareDSDestiny(6);
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(luke.isHit());
        scn.PassWeaponsSegmentActions();
        scn.SkipToDamageSegment(false);

        assertTrue(scn.LSDecisionAvailable("forfeit"));
        scn.LSChooseCard(luke);
        scn.PassAllResponses();

        assertEquals(Zone.STACKED_FACE_DOWN, luke.getZone()); //test1
        assertTrue(scn.IsStackedOn(trophy, luke)); //test1
        assertEquals(bosskPower + 1, scn.GetPower(bossk)); //test2
        assertEquals(bosskDefense + 1, scn.GetDefense(bossk)); //test2
    }

    @Test
    public void TrophyOfABountyHunterIgnoresCardHitByNonBountyHunter() {
        var scn = GetScenario();
        var trophy = scn.GetDSCard("trophy");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("trooperBlaster");
        var trooper2 = scn.GetDSCard("trooper2");
        var trooper3 = scn.GetDSCard("trooper3");
        var trooper4 = scn.GetDSCard("trooper4");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(trophy);
        scn.MoveCardsToLocation(site, trooper, trooper2, trooper3, trooper4, luke);
        scn.AttachCardsTo(trooper, blaster);
        scn.EnsureDSForcePile(4);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(6);
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(luke.isHit());
        scn.PassWeaponsSegmentActions();
        scn.SkipToDamageSegment(false);

        assertTrue(scn.LSDecisionAvailable("forfeit"));
        scn.LSChooseCard(luke);
        scn.PassAllResponses();

        assertTrue(inLostPile(luke));
        assertEquals(0, scn.GetStackedCards(trophy).size());
    }

    @Test
    public void TrophyOfABountyHunterAddsStackedCountToYourBountyHuntersOnly() {
        var scn = GetScenario();
        var trophy = scn.GetDSCard("trophy");
        var bossk = scn.GetDSCard("bossk");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetDSStartingLocation();

        //test1: your bounty hunters are power and defense value +X, X = cards stacked here
        //test2: your non-bounty hunters are unaffected
        scn.StartGame();
        scn.MoveCardsToLocation(site, bossk, trooper);
        int bosskPower = scn.GetPower(bossk);
        int bosskDefense = scn.GetDefense(bossk);
        int trooperPower = scn.GetPower(trooper);
        scn.MoveCardsToDSSideOfTable(trophy);
        assertEquals(bosskPower, scn.GetPower(bossk));

        scn.StackCardsFaceDownOn(trophy, luke, leia);
        assertEquals(bosskPower + 2, scn.GetPower(bossk)); //test1
        assertEquals(bosskDefense + 2, scn.GetDefense(bossk)); //test1
        assertEquals(trooperPower, scn.GetPower(trooper)); //test2
    }

    @Test
    public void TrophyOfABountyHunterDeploysJabbasInfluenceFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var trophy = scn.GetDSCard("trophy");
        var bossk = scn.GetDSCard("bossk");
        var jabba = scn.GetDSCard("jabba");
        var influence = scn.GetDSCard("influence");
        var tattoo = scn.GetDSCard("tattoo");
        var site = scn.GetDSStartingLocation();

        //test1: may deploy Jabba's Influence (or Desilijic Tattoo) from Reserve Deck
        //test2: only once per game
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(trophy);
        scn.MoveCardsToLocation(site, bossk, jabba);
        scn.EnsureDSForcePile(6);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(tattoo, influence);

        assertTrue(scn.DSCardActionAvailable(trophy, "Deploy Desilijic Tattoo or Jabba's Influence"));
        scn.DSUseCardAction(trophy, "Deploy Desilijic Tattoo or Jabba's Influence");
        assertTrue(scn.DSHasCardChoiceAvailable(tattoo)); //test1
        assertTrue(scn.DSHasCardChoiceAvailable(influence)); //test1
        scn.DSChooseCard(influence);
        if (scn.DSHasCardChoiceAvailable(bossk)) {
            scn.DSChooseCard(bossk);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(bossk, influence)); //test1

        scn.PassAllResponses();
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertEquals(Zone.RESERVE_DECK, tattoo.getZone());
        assertFalse(scn.DSCardActionAvailable(trophy, "Deploy Desilijic Tattoo or Jabba's Influence")); //test2
    }
}
