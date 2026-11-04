package com.gempukku.swccgo.cards.set1281.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_001_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19"); // Luke Skywalker, ability 4
                    put("droid", "1_1"); // 2X-3KPR
                    put("fault", "1_117"); // This Is All Your Fault (re-targets a weapon to your droid)
                }},
                new HashMap<>() {{
                    put("aurra", "1281_1");
                    put("rifle", "13_53"); // Aurra Sing's Blaster Rifle [Reflections III]
                    put("comlink", "1_201"); // Comlink (device)
                    put("comlink2", "1_201");
                    put("blaster", "1_312"); // Blaster Rifle
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
    public void AurraSingDeadlyAssassinStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("aurra").getBlueprint();
        assertEquals("Aurra Sing, Deadly Assassin", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FEMALE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AurraSingDeadlyAssassinDeploysADeviceOnHerFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var aurra = scn.GetDSCard("aurra");
        var comlink = scn.GetDSCard("comlink");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, aurra);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(3);
        scn.MoveCardsToTopOfOwnReserveDeck(comlink);
        assertTrue(scn.DSCardActionAvailable(aurra, "Deploy a device on Aurra"));
        scn.DSUseCardAction(aurra, "Deploy a device on Aurra");
        if (scn.DSHasCardChoiceAvailable(comlink)) {
            scn.DSChooseCard(comlink);
        }
        if (scn.DSHasCardChoiceAvailable(aurra)) {
            scn.DSChooseCard(aurra);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(aurra, comlink));
        scn.MoveCardsToTopOfOwnReserveDeck(scn.GetDSCard("comlink2"));
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(aurra, "Deploy a device on Aurra"));
    }

    @Test
    public void AurraSingDeadlyAssassinAddsOneToTotalWeaponDestinyWhileArmedWithReflectionsIIIRifle() {
        var scn = GetScenario();
        var aurra = scn.GetDSCard("aurra");
        var rifle = scn.GetDSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, aurra, luke);
        scn.AttachCardsTo(aurra, rifle);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        // Rifle: hit if destiny +1 > defense value 4. A drawn 3 only hits with Aurra's +1.
        scn.PrepareDSDestiny(3);
        assertTrue(scn.DSCardActionAvailable(rifle, "Fire"));
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(luke.isHit());
    }

    @Test
    public void AurraSingDeadlyAssassinCharactersSheHitsMayNotSatisfyAttrition() {
        var scn = GetScenario();
        var aurra = scn.GetDSCard("aurra");
        var rifle = scn.GetDSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, aurra, luke);
        scn.AttachCardsTo(aurra, rifle);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(6);
        assertFalse(scn.game().getModifiersQuerying().cannotSatisfyAttrition(scn.gameState(), luke));
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertTrue(luke.isHit());
        assertTrue(scn.game().getModifiersQuerying().cannotSatisfyAttrition(scn.gameState(), luke));
    }

    @Test
    public void AurraSingDeadlyAssassinReflectionsIIIRifleMayNotBeRetargeted() {
        var scn = GetScenario();
        var aurra = scn.GetDSCard("aurra");
        var rifle = scn.GetDSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var droid = scn.GetLSCard("droid");
        var fault = scn.GetLSCard("fault");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, aurra, luke, droid);
        scn.AttachCardsTo(aurra, rifle);
        scn.MoveCardsToHand(fault);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(2);
        scn.LSActivateForceCheat(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(6);
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(luke);
        if (!scn.LSCardPlayAvailable(fault)) {
            scn.DSPass();
        }
        assertTrue(scn.LSCardPlayAvailable(fault));
        scn.LSPlayCard(fault);
        if (scn.LSHasCardChoiceAvailable(luke)) {
            scn.LSChooseCard(luke);
        }
        scn.LSChooseCard(droid);
        scn.PassAllResponses();
        assertTrue(luke.isHit());
        assertFalse(droid.isHit());
    }

    @Test
    public void AurraSingDeadlyAssassinReflectionsIIIRifleMayNotBeStolen() {
        var scn = GetScenario();
        var aurra = scn.GetDSCard("aurra");
        var rifle = scn.GetDSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, aurra, luke);
        scn.AttachCardsTo(aurra, rifle);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.game().getModifiersQuerying().canBeTargetedBy(scn.gameState(), rifle, luke, Collections.singleton(TargetingReason.TO_BE_STOLEN)));
    }

    @Test
    public void AurraSingDeadlyAssassinIsImmuneToAttritionLessThan3OrLessThan4WhileArmedWithARifle() {
        var scn = GetScenario();
        var aurra = scn.GetDSCard("aurra");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, aurra);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), aurra), scn.epsilon);
        scn.AttachCardsTo(aurra, blaster);
        scn.DSPass();
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), aurra), scn.epsilon);
    }
}
