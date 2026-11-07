package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_035_Tests {
    private static final String SHIELD = "Play a Defensive Shield";

    private static StartingSetup outsideDeck(int count) {
        return new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                var all = List.of("13_84", "13_86", "13_95", "13_96", "1_194", "13_99");
                var cards = new HashMap<String, String>();
                for (int i = 0; i < count; i++)
                    cards.put("outside" + i, all.get(i));
                return cards;
            }

            @Override
            public void Setup(VirtualTableScenario scn) { }
        };
    }

    private VirtualTableScenario scenario(boolean startingEffect, int outsideCount) {
        var dark = new HashMap<>(Map.of("deal", "109_12", "occupation", "7_223",
                "bespin", "5_164", "platform", "5_169", "trooperA", "1_194", "trooperB", "1_194"));
        if (startingEffect)
            dark.put("jedi", "1261_35");
        return new VirtualTableScenario(new HashMap<>(Map.of("saber", "13_34", "obi", "13_33", "blaster", "1_148")),
                dark, 20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, outsideDeck(outsideCount), VirtualTableScenario.Open);
    }

    @Test
    public void printedStartingEffectIdentity() {
        var scn = scenario(true, 5);
        var bp = scn.GetDSCard("jedi").getBlueprint();
        assertEquals("He's No Jedi", bp.getTitle());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(5, bp.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(CardSubtype.STARTING, bp.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertFalse(bp.hasVirtualSuffix());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.JABBAS_PALACE, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
    }

    @Test
    public void realPregameStacksFiveFaceDownIncludingNonShieldsAndAllowsOnlyTwoShieldPlays() {
        var scn = scenario(true, 5);
        var effect = scn.GetDSCard("jedi");
        startWithoutObjective(scn);
        assertEquals(Zone.SIDE_OF_TABLE, effect.getZone());
        var stacked = scn.GetStackedCards(effect);
        assertEquals(5, stacked.size());
        assertTrue(stacked.stream().allMatch(c -> c.getZone() == Zone.STACKED_FACE_DOWN));
        var nonShield = stacked.stream().filter(c -> c.getBlueprint().getCardCategory() != CardCategory.DEFENSIVE_SHIELD)
                .findFirst().orElseThrow();

        scn.SkipToDSTurn(Phase.CONTROL);
        for (int i = 0; i < 2; i++) {
            assertTrue(scn.DSCardActionAvailable(effect, SHIELD));
            scn.DSUseCardAction(effect, SHIELD);
            assertFalse(scn.DSHasCardChoiceAvailable(nonShield));
            var shield = scn.GetStackedCards(effect).stream().filter(scn::DSHasCardChoiceAvailable)
                    .findFirst().orElseThrow();
            scn.DSChooseCard(shield);
            scn.PassAllResponses();
            assertEquals(Zone.SIDE_OF_TABLE, shield.getZone());
        }
        assertEquals(3, scn.GetStackedCards(effect).size());
        assertEquals(Zone.STACKED_FACE_DOWN, nonShield.getZone());
        assertFalse(scn.DSCardActionAvailable(effect, SHIELD));
    }

    @Test
    public void pregameDoesNotSelectAnArbitrarySubsetOrSilentlyStackFewerThanFive() {
        for (int count : List.of(4, 6)) {
            var scn = scenario(true, count);
            var effect = scn.GetDSCard("jedi");
            startWithoutObjective(scn);
            assertTrue(scn.GetStackedCards(effect).isEmpty());
        }
    }

    @Test
    public void lightsaberDrainRuleStartsWithDealAndPersistsAfterBothSourcesLeave() {
        var scn = scenario(true, 5);
        var deal = scn.GetDSCard("deal");
        var effect = scn.GetDSCard("jedi");
        var obi = scn.GetLSCard("obi");
        var saber = scn.GetLSCard("saber");
        var blaster = scn.GetLSCard("blaster");
        startWithoutObjective(scn);
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, obi);
        scn.AttachCardsTo(obi, saber, blaster);
        var mods = scn.game().getModifiersQuerying();
        assertFalse(mods.isForceDrainModifierCanceled(scn.gameState(), site, saber, scn.LS, scn.LS, 1));
        scn.MoveCardsToDSSideOfTable(deal);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(mods.isForceDrainModifierCanceled(scn.gameState(), site, saber, scn.LS, scn.LS, 1));
        assertFalse(mods.isForceDrainModifierCanceled(scn.gameState(), site, obi, scn.LS, scn.LS, 1));
        assertFalse(mods.isForceDrainModifierCanceled(scn.gameState(), site, blaster, scn.LS, scn.LS, 1));
        assertFalse(mods.mayFireOnlyOncePerBattle(scn.gameState(), saber));

        scn.MoveCardsToTopOfDSLostPile(deal, effect);
        assertTrue(mods.isForceDrainModifierCanceled(scn.gameState(), site, saber, scn.LS, scn.LS, 1));
    }

    @Test
    public void cloudCityOccupationIsCanceledEvenWithoutTheDealObjective() {
        for (boolean startingEffect : List.of(false, true)) {
            var scn = scenario(startingEffect, startingEffect ? 5 : 0);
            var occupation = scn.GetDSCard("occupation");
            var bespin = scn.GetDSCard("bespin");
            var platform = scn.GetDSCard("platform");
            startWithoutObjective(scn);
            scn.MoveLocationToTable(bespin);
            scn.MoveLocationToTable(platform);
            scn.MoveCardsToLocation(platform, scn.GetDSCard("trooperA"));
            scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("trooperB"));
            scn.MoveCardsToDSHand(occupation);
            scn.SkipToDSTurn(Phase.DEPLOY);
            scn.EnsureDSForcePile(6);
            assertTrue(scn.DSDeployAvailable(occupation));
            scn.DSDeployCard(occupation);
            if (scn.DSHasCardChoiceAvailable(bespin))
                scn.DSChooseCard(bespin);
            scn.PassAllResponses();
            if (startingEffect)
                assertTrue(scn.GetDSLostPile().contains(occupation));
            else
                assertSame(bespin, occupation.getAttachedTo());
        }
    }

    private void startWithoutObjective(VirtualTableScenario scn) {
        scn.MoveCardsToDSHand(scn.GetDSCard("deal"));
        scn.StartGame();
    }
}
