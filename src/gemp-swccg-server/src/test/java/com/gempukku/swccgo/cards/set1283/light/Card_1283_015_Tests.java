package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("aayla", "1283_15");
                    put("boshek", "1_4");
                    put("momaw", "1_20");
                }},
                new HashMap<>() {{
                    put("trooper1", "1_194");
                    put("trooper2", "1_194");
                    put("rifle", "1_312");
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
    public void AaylaSecuraStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("aayla").getBlueprint();

        assertEquals("Aayla Secura", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        assertEquals(Species.TWILEK, card.getSpecies());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FEMALE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AaylaIsPowerPlusOneForEachOpponentsCharacterHere() {
        var scn = GetScenario();
        var aayla = scn.GetLSCard("aayla");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aayla);
        assertEquals(4, scn.GetPower(aayla));
        scn.MoveCardsToLocation(site, trooper1, trooper2);
        assertEquals(6, scn.GetPower(aayla));
    }

    @Test
    public void AaylaAddsOneBattleDestinyOnlyWhenWithTwoOfYourAliens() {
        var scn = GetScenario();
        var aayla = scn.GetLSCard("aayla");
        var boshek = scn.GetLSCard("boshek");
        var momaw = scn.GetLSCard("momaw");
        var trooper1 = scn.GetDSCard("trooper1");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aayla, boshek, momaw, trooper1);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(2, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void AaylaAddsNoBattleDestinyWithOnlyOneOtherAlien() {
        var scn = GetScenario();
        var aayla = scn.GetLSCard("aayla");
        var boshek = scn.GetLSCard("boshek");
        var trooper1 = scn.GetDSCard("trooper1");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aayla, boshek, trooper1);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(1, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void AaylaMayNotBeTargetedByWeaponsWhileAnotherOfYourAliensPresentIsNotHit() {
        var scn = GetScenario();
        var aayla = scn.GetLSCard("aayla");
        var boshek = scn.GetLSCard("boshek");
        var trooper1 = scn.GetDSCard("trooper1");
        var rifle = scn.GetDSCard("rifle");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aayla, boshek, trooper1);
        scn.AttachCardsTo(trooper1, rifle);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.DSUseCardAction(rifle, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(boshek));
        assertFalse(scn.DSHasCardChoiceAvailable(aayla));
    }

    @Test
    public void AaylaMayBeTargetedByWeaponsWhenNoOtherOfYourAliensArePresent() {
        var scn = GetScenario();
        var aayla = scn.GetLSCard("aayla");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");
        var rifle = scn.GetDSCard("rifle");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aayla, trooper1, trooper2);
        scn.AttachCardsTo(trooper1, rifle);
        scn.EnsureDSForcePile(5);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.DSUseCardAction(rifle, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(aayla)
                || scn.gameState().getWeaponFiringState().getTargets().contains(aayla));
    }

    @Test
    public void AaylaIsImmuneToYouAreBeatenAndAttritionLessThanFive() {
        var scn = GetScenario();
        var aayla = scn.GetLSCard("aayla");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aayla);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), aayla, Title.You_Are_Beaten));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), aayla), scn.epsilon);
    }
}
