package com.gempukku.swccgo.cards.set1282.dark;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19"); // Luke Skywalker, ability 4
                    put("leia", "1_17"); // Leia Organa, ability 3
                    put("cantina", "1_128"); // Tatooine: Cantina
                    put("db94", "1_129"); // Tatooine: Docking Bay 94
                }},
                new HashMap<>() {{
                    put("rifle", "1282_11");
                    put("dannik", "2_84"); // Dannik Jerriko (assassin)
                    put("trooper", "1_194"); // Stormtrooper (not an assassin)
                    put("bounty", "6_144"); // Hutt Bounty
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

    static void passUntil(VirtualTableScenario scn, BooleanSupplier done) {
        for (int i = 0; i < 40; ++i) {
            if (done.getAsBoolean())
                return;
            if (scn.LSAnyDecisionsAvailable())
                scn.LSPass();
            else if (scn.DSAnyDecisionsAvailable())
                scn.DSPass();
            else
                return;
        }
    }

    private static boolean dsHasAction(VirtualTableScenario scn, PhysicalCardImpl card, String text) {
        return scn.DSAnyDecisionsAvailable() && !scn.GetDSAvailableActions().isEmpty() && scn.DSCardActionAvailable(card, text);
    }

    @Test
    public void AssassinsBlasterRifleStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("rifle").getBlueprint();
        assertEquals("Assassin's Blaster Rifle", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_2, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER_RIFLE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AssassinsBlasterRifleDeploysOnlyOnYourAssassin() {
        var scn = GetScenario();
        var rifle = scn.GetDSCard("rifle");
        var dannik = scn.GetDSCard("dannik");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, dannik, trooper);
        scn.MoveCardsToHand(rifle);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSDeployAvailable(rifle));
        scn.DSDeployCard(rifle);
        assertTrue(scn.DSHasCardChoiceAvailable(dannik));
        assertFalse(scn.DSHasCardChoiceAvailable(trooper));
        scn.DSChooseCard(dannik);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(dannik, rifle));
    }

    @Test
    public void AssassinsBlasterRifleMakesAssassinPowerPlusOne() {
        var scn = GetScenario();
        var rifle = scn.GetDSCard("rifle");
        var dannik = scn.GetDSCard("dannik");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), dannik);
        int power = scn.GetPower(dannik);
        scn.AttachCardsTo(dannik, rifle);
        assertEquals(power + 1, scn.GetPower(dannik));
    }

    /**
     * Battle at Docking Bay 94: Dannik (armed with the rifle) vs. Leia; Luke waits at the adjacent Cantina.
     * The rifle's range is same or adjacent site, but during a battle GEMP lets weapons target only battle
     * participants (engine-wide rule), so Luke at the adjacent site is not a valid target here.
     */
    private void battleAgainstLeia(VirtualTableScenario scn) {
        var cantina = scn.GetLSCard("cantina");
        var db94 = scn.GetLSCard("db94");
        var dannik = scn.GetDSCard("dannik");
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(db94, dannik, scn.GetLSCard("leia"));
        scn.MoveCardsToLocation(cantina, scn.GetLSCard("luke"));
        scn.AttachCardsTo(dannik, scn.GetDSCard("rifle"));
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureDSForcePile(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(db94);
    }

    private void fireAtLeia(VirtualTableScenario scn, int destiny) {
        var rifle = scn.GetDSCard("rifle");
        scn.PrepareDSDestiny(destiny);
        int forcePile = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(rifle, "Fire"));
        scn.DSUseCardAction(rifle, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("leia")));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("luke")));
        scn.DSChooseCard(scn.GetLSCard("leia"));
        scn.PassWeaponFireWithDestinyDraw();
        // fired for free
        assertEquals(forcePile, scn.GetDSForcePileCount());
    }

    @Test
    public void AssassinsBlasterRifleHitsCharacterIfDestinyPlusOneExceedsDefenseValue() {
        var scn = GetScenario();
        var leia = scn.GetLSCard("leia");
        battleAgainstLeia(scn);
        fireAtLeia(scn, 3 /* 3 + 1 = 4 > 3 */);
        scn.PassAllResponses();

        assertTrue(leia.isHit());
        assertFalse(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_TARGET_TO_BE_CAPTURED, leia).isEmpty());
    }

    @Test
    public void AssassinsBlasterRifleMissesIfDestinyPlusOneDoesNotExceedDefenseValue() {
        var scn = GetScenario();
        var leia = scn.GetLSCard("leia");
        battleAgainstLeia(scn);
        fireAtLeia(scn, 2 /* 2 + 1 = 3, not > 3 */);
        scn.PassAllResponses();

        assertFalse(leia.isHit());
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_TARGET_TO_BE_CAPTURED, leia).isEmpty());
    }

    @Test
    public void AssassinsBlasterRifleMayPlaceBountyOnHitTargetInUsedPileToRetrieveTwoForce() {
        var scn = GetScenario();
        var rifle = scn.GetDSCard("rifle");
        var bounty = scn.GetDSCard("bounty");
        var leia = scn.GetLSCard("leia");
        battleAgainstLeia(scn);
        scn.AttachCardsTo(leia, bounty);
        var lost1 = scn.GetDSFiller(1);
        var lost2 = scn.GetDSFiller(2);
        scn.MoveCardToZone(scn.DS, lost1, Zone.LOST_PILE);
        scn.MoveCardToZone(scn.DS, lost2, Zone.LOST_PILE);
        fireAtLeia(scn, 3);
        passUntil(scn, () -> dsHasAction(scn, rifle, "Place bounty in Used Pile"));

        assertTrue(leia.isHit());
        assertTrue(dsHasAction(scn, rifle, "Place bounty in Used Pile"));
        int lostPile = scn.GetDSLostPileCount();
        scn.DSUseCardAction(rifle, "Place bounty in Used Pile");
        if (scn.DSDecisionAvailable("Choose bounty")) {
            scn.DSChooseCard(bounty);
        }
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, bounty);
        assertEquals(lostPile - 2, scn.GetDSLostPileCount());
    }
}
