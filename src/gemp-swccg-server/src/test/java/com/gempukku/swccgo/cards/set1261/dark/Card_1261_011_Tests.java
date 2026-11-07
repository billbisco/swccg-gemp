package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_011_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("han", "1_11");
                    put("blaster", "1_152"); put("barrier", "1_105"); }},
                new HashMap<>() {{ put("kir", "1261_11"); put("trooper", "1_194"); put("vader", "1_168"); }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void KirKanosPrintedStatsIconsKeywordsAndPermanentWeaponMatchSheet() {
        var scn = scenario(); var kir = scn.GetDSCard("kir"); var b = kir.getBlueprint();
        assertEquals("Kir Kanos With Force Pike", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Side.DARK, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness());
        assertEquals(3, b.getDestiny(), scn.epsilon); assertEquals(3, b.getDeployCost(), scn.epsilon);
        assertEquals(4, b.getPower(), scn.epsilon); assertEquals(2, b.getAbility(), scn.epsilon); assertEquals(4, b.getForfeit(), scn.epsilon);
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.IMPERIAL); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.ROYAL_GUARD); add(Keyword.HEROIC); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.IMPERIAL); add(Icon.WARRIOR); add(Icon.PERMANENT_WEAPON);
            add(Icon.REFLECTIONS_II); add(Icon.LEGACY_BLOCK_6); }});
        var weapon = b.getPermanentWeapon(kir); assertEquals("Force Pike", weapon.getTitle(scn.game()));
        assertTrue(weapon.hasKeyword(Keyword.FORCE_PIKE));
    }

    private VirtualTableScenario battle() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("kir"), scn.GetLSCard("obiwan"), scn.GetLSCard("han"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); return scn;
    }

    private void fire(VirtualTableScenario scn, int destiny) {
        var target = scn.GetLSCard("obiwan"); var kir = scn.GetDSCard("kir");
        scn.PrepareDSDestiny(destiny); int reserve = scn.GetDSReserveDeckCount(); int force = scn.GetDSForcePileCount();
        scn.DSUseCardAction(kir, "Fire"); if (scn.DSHasCardChoiceAvailable(target)) scn.DSChooseCard(target);
        scn.PassAllResponses(); assertEquals(force, scn.GetDSForcePileCount()); assertEquals(reserve - 1, scn.GetDSReserveDeckCount());
    }

    @Test
    public void KirKanosActuallyHitsWithOneFreeDestinyAndResetsPowerNotForfeit() {
        var scn = battle(); var target = scn.GetLSCard("obiwan"); int forfeit = scn.GetForfeit(target);
        assertEquals(6, scn.GetDefense(target)); fire(scn, 6); assertTrue(target.isHit());
        assertEquals(0, scn.GetPower(target)); assertEquals(forfeit, scn.GetForfeit(target));
        if (scn.AwaitingLSWeaponsSegmentActions()) scn.LSPass(); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("kir"), "Fire"));
    }

    @Test
    public void KirKanosEqualDefenseDoesNotHitOrResetPower() {
        var scn = battle(); var target = scn.GetLSCard("obiwan"); int power = scn.GetPower(target);
        fire(scn, 5); assertFalse(target.isHit()); assertEquals(power, scn.GetPower(target));
    }

    @Test
    public void KirKanosProtectsOnlyOwnCharactersHereFromAllThreePrintedTitles() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        var kir = scn.GetDSCard("kir"); var trooper = scn.GetDSCard("trooper"); var vader = scn.GetDSCard("vader"); var han = scn.GetLSCard("han");
        scn.MoveCardsToLocation(site, kir, trooper, han); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), vader);
        var q = scn.game().getModifiersQuerying();
        for (String title : new String[] { Title.Cantina_Brawl, Title.Fallen_Portal, Title.Rebel_Barrier }) {
            assertTrue(q.isImmuneToCardTitle(scn.gameState(), kir, title));
            assertTrue(q.isImmuneToCardTitle(scn.gameState(), trooper, title));
            assertFalse(q.isImmuneToCardTitle(scn.gameState(), vader, title));
            assertFalse(q.isImmuneToCardTitle(scn.gameState(), han, title));
        }
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), trooper, Title.Sense));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), kir);
        assertFalse(q.isImmuneToCardTitle(scn.gameState(), trooper, Title.Rebel_Barrier));
        assertTrue(q.isImmuneToCardTitle(scn.gameState(), vader, Title.Rebel_Barrier));
    }

    @Test
    public void KirKanosHeroicActuallyProtectsWeakerCompanionFromWeaponTargeting() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        var kir = scn.GetDSCard("kir"); var trooper = scn.GetDSCard("trooper"); var han = scn.GetLSCard("han"); var blaster = scn.GetLSCard("blaster");
        scn.MoveCardsToLocation(site, kir, trooper, han); scn.AttachCardsTo(han, blaster);
        scn.LSStartBattleAndSkipToWeaponsSegment(site); scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(kir)); assertTrue(scn.LSHasCardChoiceNotAvailable(trooper));
    }

    private VirtualTableScenario deployTrooper(boolean withKir) {
        var scn = scenario(); scn.StartGame();
        scn.MoveCardsToLocation(withKir ? scn.GetDSStartingLocation() : scn.GetLSStartingLocation(), scn.GetDSCard("kir"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToLSHand(scn.GetLSCard("barrier")); scn.MoveCardsToDSHand(scn.GetDSCard("trooper"));
        scn.EnsureLSForcePile(1); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(scn.GetDSCard("trooper")); scn.DSChooseCard(scn.GetDSStartingLocation());
        return scn;
    }

    @Test
    public void KirKanosBlocksActualRebelBarrierResponseAtHisSite() {
        var scn = deployTrooper(true);
        for (int i = 0; i < 20 && !scn.AwaitingDSDeployPhaseActions() && !scn.AwaitingLSDeployPhaseActions(); i++) {
            assertFalse("Barrier must be unavailable in every live deployment response window",
                    scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Prevent")));
            assertTrue("Expected an optional deployment response: " + scn.GetCurrentDecision().getText(), scn.GetCurrentDecision().getText().toLowerCase().contains("optional"));
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        if (scn.AwaitingLSDeployPhaseActions()) scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), scn.GetDSCard("trooper"), Title.Rebel_Barrier));
    }

    @Test
    public void KirKanosElsewhereAllowsActualRebelBarrierResponse() {
        var scn = deployTrooper(false);
        for (int i = 0; i < 8; i++) {
            if (scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Prevent"))) break;
            if (scn.LSGetDecision() != null) scn.LSPass();
            else if (scn.DSGetDecision() != null) scn.DSPass();
        }
        assertTrue(scn.LSCardPlayAvailable(scn.GetLSCard("barrier")));
        scn.LSPlayCard(scn.GetLSCard("barrier")); scn.PassAllResponses();
        com.gempukku.swccgo.framework.Assertions.assertInZone(Zone.USED_PILE, scn.GetLSCard("barrier"));
    }
}
