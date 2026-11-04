package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_026_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("chewie", "1281_26");
                    put("gold1", "1_141");
                    put("tatooine", "1_127");
                    put("threepio", "1_5");
                    put("trooper", "1_28");
                }},
                new HashMap<>() {{
                    put("saber", "1281_25");
                    put("mara", "110_10");
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
    public void ChewbaccaWalkingCarpetStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("chewie").getBlueprint();

        assertEquals("Chewbacca, Walking Carpet", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SCOUNDREL);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.CHEWIE);
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
    public void ChewbaccaWalkingCarpetAddsTwoToPowerAndManeuverOfStarfighterHePilots() {
        var scn = GetScenario();
        var chewie = scn.GetLSCard("chewie");
        var gold1 = scn.GetLSCard("gold1");

        scn.StartGame();
        var system = scn.GetLSCard("tatooine");
        scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, gold1);
        float power = gold1.getBlueprint().getPower();
        float maneuver = gold1.getBlueprint().getManeuver();
        scn.BoardAsPilot(gold1, chewie);
        assertEquals(power + 2, scn.game().getModifiersQuerying().getPower(scn.gameState(), gold1), scn.epsilon);
        assertEquals(maneuver + 2, scn.game().getModifiersQuerying().getManeuver(scn.gameState(), gold1), scn.epsilon);
    }

    @Test
    public void ChewbaccaWalkingCarpetYourTotalAbilityHereMayNotBeReduced() {
        var scn = GetScenario();
        var chewie = scn.GetLSCard("chewie");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        assertFalse(scn.game().getModifiersQuerying().isProhibitedFromHavingTotalAbilityReduced(scn.gameState(), site, scn.LS));
        scn.MoveCardsToLocation(site, chewie);
        assertTrue(scn.game().getModifiersQuerying().isProhibitedFromHavingTotalAbilityReduced(scn.gameState(), site, scn.LS));
        assertFalse(scn.game().getModifiersQuerying().isProhibitedFromHavingTotalAbilityReduced(scn.gameState(), site, scn.DS));
    }

    @Test
    public void ChewbaccaWalkingCarpetOncePerGameLosesOneForceToDeployADroidFromLostPile() {
        var scn = GetScenario();
        var chewie = scn.GetLSCard("chewie");
        var threepio = scn.GetLSCard("threepio");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, chewie);
        scn.MoveCardsToTopOfLSLostPile(threepio);
        scn.LSActivateForceCheat(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int lostPile = scn.GetLSLostPileCount();

        assertTrue(scn.LSCardActionAvailable(chewie, "Deploy a droid from Lost Pile"));
        scn.LSUseCardAction(chewie, "Deploy a droid from Lost Pile");
        Vb81RestTestSupport.payForceLossFromReserveDeck(scn);
        if (scn.LSDecisionAvailable("Choose") && scn.LSHasCardChoiceAvailable(threepio)) {
            scn.LSChooseCard(threepio);
        }
        if (scn.LSDecisionAvailable("Choose") && scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        scn.PassAllResponses();
        assertAtLocation(site, threepio);
        assertEquals(lostPile, scn.GetLSLostPileCount());
        assertFalse(Vb81RestTestSupport.lsCardActionAvailable(scn, chewie, "Deploy a droid from Lost Pile"));
    }

    @Test
    public void ChewbaccaWalkingCarpetRestoresJustHitCharactersForfeitToNormal() {
        var scn = GetScenario();
        var chewie = scn.GetLSCard("chewie");
        var trooper = scn.GetLSCard("trooper");
        var saber = scn.GetDSCard("saber");
        var mara = scn.GetDSCard("mara");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, chewie, trooper, mara);
        scn.AttachCardsTo(mara, saber);
        int forfeit = scn.GetForfeit(trooper);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(3);
        scn.PrepareDSDestiny(3);
        scn.DSUseCardAction(saber, "Fire");
        scn.DSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw(2);
        // The trooper is 'hit' and its forfeit then reset to 0; Chewbacca restores it once it was reduced.
        assertTrue(Vb81RestTestSupport.passUntil(scn, () -> scn.GetForfeit(trooper) == 0 && scn.LSCardActionAvailable(chewie, "Restore")));
        scn.LSUseCardAction(chewie, "Restore");
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();

        assertTrue(trooper.isHit());
        assertEquals(forfeit, scn.GetForfeit(trooper));
    }
}
