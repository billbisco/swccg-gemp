package com.gempukku.swccgo.cards.set1263.dark;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1263_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("walker", "1263_21");
                    put("walker2", "1263_21");
                    put("mtt", "14_122");
                    put("cannon", "3_158");
                    put("atat", "3_155");
                    put("lin", "1_186");
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
    public void DroidWalkerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("walker").getBlueprint();

        assertEquals("Droid Walker", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(2, card.getLandspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.PILOT);
            add(Icon.PRESENCE);
            add(Icon.EPISODE_I);
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_6);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DroidWalkerDeploysMinusOneToSameSiteAsYourMTT() {
        var scn = GetScenario();
        var walker = scn.GetDSCard("walker");
        var mtt = scn.GetDSCard("mtt");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, mtt);
        scn.MoveCardsToDSHand(walker);
        scn.EnsureDSForcePile(8);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCardAndPassResponses(walker, site);
        assertAtLocation(site, walker);
        assertEquals(force - 2, scn.GetDSForcePileCount());
    }

    @Test
    public void DroidWalkerDeploysForThreeWithoutYourMTT() {
        var scn = GetScenario();
        var walker = scn.GetDSCard("walker");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSHand(walker);
        scn.EnsureDSForcePile(8);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();
        scn.DSDeployCardAndPassResponses(walker, site);
        assertAtLocation(site, walker);
        assertEquals(force - 3, scn.GetDSForcePileCount());
    }

    @Test
    public void DroidWalkerAllowsAtAtCannonToDeployAboard() {
        var scn = GetScenario();
        var walker = scn.GetDSCard("walker");
        var cannon = scn.GetDSCard("cannon");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, walker);
        scn.MoveCardsToDSHand(cannon);
        scn.EnsureDSForcePile(8);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(cannon));
        scn.DSDeployCardAndPassResponses(cannon, walker);
        assertEquals(walker, cannon.getAttachedTo());
    }

    @Test
    public void DroidWalkerAtAtCannonTargetsHitMayNotSatisfyAttrition() {
        var scn = GetScenario();
        var walker = scn.GetDSCard("walker");
        var cannon = scn.GetDSCard("cannon");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, walker, luke);
        scn.AttachCardsTo(walker, cannon);
        scn.EnsureDSForcePile(8);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.DSCardActionAvailable(cannon, "Fire"));
        scn.PrepareDSDestiny(5);
        scn.DSUseCardAction(cannon, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(luke.isHit());
        assertTrue(scn.game().getModifiersQuerying().cannotSatisfyAttrition(scn.gameState(), luke));
    }

    @Test
    public void DroidWalkerDoesNotAffectTargetsHitByAtAtCannonOnAnotherVehicle() {
        var scn = GetScenario();
        var walker = scn.GetDSCard("walker");
        var atat = scn.GetDSCard("atat");
        var cannon = scn.GetDSCard("cannon");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, walker, atat, luke);
        scn.AttachCardsTo(atat, cannon);
        scn.EnsureDSForcePile(8);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(5);
        scn.DSUseCardAction(cannon, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(luke.isHit());
        assertFalse(scn.game().getModifiersQuerying().cannotSatisfyAttrition(scn.gameState(), luke));
    }

    @Test
    public void DroidWalkerDrawsOneBattleDestinyUnlessAlone() {
        var scn = GetScenario();
        var walker = scn.GetDSCard("walker");
        var lin = scn.GetDSCard("lin");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, walker, lin, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(1, scn.GetDSBattleDestinyCount());
    }

    @Test
    public void DroidWalkerDrawsNoBattleDestinyWhenAlone() {
        var scn = GetScenario();
        var walker = scn.GetDSCard("walker");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, walker, luke);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassWeaponsSegmentActions();
        assertEquals(0, scn.GetDSBattleDestinyCount());
    }
}
