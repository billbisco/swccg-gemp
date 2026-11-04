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

public class Card_1283_029_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("saber", "1283_29");
                    put("aayla", "1283_15");
                    put("mace", "1274_15");
                    put("oldmace", "12_13");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void GuardiansLightsaberStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("saber").getBlueprint();

        assertEquals("Guardian's Lightsaber", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LIGHTSABER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void GuardiansLightsaberDeploysOnAaylaOrVirtualBlock7Mace() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var aayla = scn.GetLSCard("aayla");
        var mace = scn.GetLSCard("mace");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aayla, mace);
        scn.MoveCardsToLSHand(saber);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(saber);
        assertTrue(scn.LSHasCardChoiceAvailable(aayla));
        assertTrue(scn.LSHasCardChoiceAvailable(mace));
    }

    @Test
    public void GuardiansLightsaberDoesNotDeployOnOtherMace() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var oldmace = scn.GetLSCard("oldmace");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, oldmace);
        scn.MoveCardsToLSHand(saber);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(saber));
    }

    @Test
    public void GuardiansLightsaberMakesOpponentLoseOneForceOnceDuringYourControlPhaseAtABattlegroundYouControl() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var aayla = scn.GetLSCard("aayla");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aayla);
        scn.AttachCardsTo(aayla, saber);
        scn.SkipToLSTurn(Phase.CONTROL);
        int dsLifeForce = scn.GetDSLifeForceRemaining();
        assertTrue(scn.LSCardActionAvailable(saber, "lose 1 Force"));
        scn.LSUseCardAction(saber, "lose 1 Force");
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("Choose Force to lose")) {
            scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
            scn.PassAllResponses();
        }
        assertEquals(dsLifeForce - 1, scn.GetDSLifeForceRemaining());
        if (scn.DSAnyDecisionsAvailable()) {
            scn.DSPass();
        }
        assertFalse(scn.LSCardActionAvailable(saber, "lose 1 Force"));
    }

    @Test
    public void GuardiansLightsaberMakesNoForceLossWhereYouDoNotControl() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var aayla = scn.GetLSCard("aayla");
        var trooper = scn.GetDSCard("trooper");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aayla, trooper, vader);
        scn.AttachCardsTo(aayla, saber);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(saber, "lose 1 Force"));
    }

    @Test
    public void GuardiansLightsaberHitsAndTargetMayNotSatisfyAttritionWhenTotalDestinyExceedsDefenseValue() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var aayla = scn.GetLSCard("aayla");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aayla, vader);
        scn.AttachCardsTo(aayla, saber);
        WeaponTestSupport.lsBattleAndFire(scn, site, saber, vader, 4, 3);
        assertTrue(vader.isHit());
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_SATISFY_ATTRITION, vader).isEmpty());
    }

    @Test
    public void GuardiansLightsaberMissesWhenTotalDestinyDoesNotExceedDefenseValue() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var aayla = scn.GetLSCard("aayla");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, aayla, vader);
        scn.AttachCardsTo(aayla, saber);
        WeaponTestSupport.lsBattleAndFire(scn, site, saber, vader, 3, 3);
        assertFalse(vader.isHit());
    }
}
