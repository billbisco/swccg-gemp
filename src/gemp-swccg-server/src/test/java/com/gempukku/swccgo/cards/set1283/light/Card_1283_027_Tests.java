package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_027_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("blaster", "1283_27");
                    put("kyle", "1281_28");
                    put("han", "1_11");
                    put("boshek", "1_4");
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

    @Test
    public void BlackMarketBlasterStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("blaster").getBlueprint();

        assertEquals("Black Market Blaster", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BlackMarketBlasterDeploysOnKyleOrYourSmugglerOnly() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var kyle = scn.GetLSCard("kyle");
        var han = scn.GetLSCard("han");
        var boshek = scn.GetLSCard("boshek");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, kyle, han, boshek);
        scn.MoveCardsToLSHand(blaster);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(blaster));
        scn.LSDeployCard(blaster);
        assertTrue(scn.LSHasCardChoiceAvailable(kyle));
        assertTrue(scn.LSHasCardChoiceAvailable(han));
        assertFalse(scn.LSHasCardChoiceAvailable(boshek));
    }

    @Test
    public void BlackMarketBlasterHitsAndTargetMayNotSatisfyAttritionWhenDestinyPlusTwoExceedsDefenseValue() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(han, blaster);
        int defense = scn.GetDefense(trooper);
        WeaponTestSupport.lsBattleAndFire(scn, site, blaster, trooper, defense - 1);
        assertTrue(trooper.isHit());
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_SATISFY_ATTRITION, trooper).isEmpty());
    }

    @Test
    public void BlackMarketBlasterMissesWhenDestinyPlusTwoDoesNotExceedDefenseValue() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(han, blaster);
        int defense = scn.GetDefense(trooper);
        WeaponTestSupport.lsBattleAndFire(scn, site, blaster, trooper, defense - 2);
        assertFalse(trooper.isHit());
    }

    @Test
    public void BlackMarketBlasterFiredByKyleMakesHisImmunityToAttritionPlusOneForRemainderOfTurn() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var kyle = scn.GetLSCard("kyle");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, kyle, trooper);
        scn.AttachCardsTo(kyle, blaster);
        float before = scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), kyle);
        WeaponTestSupport.lsBattleAndFire(scn, site, blaster, trooper, 0);
        assertEquals(before + 1, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), kyle), scn.epsilon);
    }

    @Test
    public void BlackMarketBlasterFiredByHanDoesNotChangeImmunity() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(han, blaster);
        WeaponTestSupport.lsBattleAndFire(scn, site, blaster, trooper, 0);
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.IMMUNITY_TO_ATTRITION_CHANGE, han).isEmpty());
    }
}
