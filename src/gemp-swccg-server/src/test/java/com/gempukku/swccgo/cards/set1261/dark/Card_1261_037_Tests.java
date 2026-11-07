package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_037_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("jawa", "1_12"); put("alter", "1_71"); }},
                new HashMap<>() {{ put("vow", "1261_37"); put("jawa1", "1_182"); put("jawa2", "1_182"); put("bossk", "4_92");
                    put("dengar", "4_100"); put("officer", "1_166"); put("vader", "1_168"); put("chamber", "6_162"); put("molator", "1_225"); }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test public void TdoshokHuntingVowPrintedMetadataAndAlterImmunityMatchSource() {
        var scn = scenario(); var b = scn.GetDSCard("vow").getBlueprint();
        assertEquals("T'doshok Hunting Vow", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertEquals(Side.DARK, b.getSide());
        assertEquals(4, b.getDestiny(), scn.epsilon); assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        assertTrue(b.isImmuneToCardTitle(Title.Alter));
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.EFFECT); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.EFFECT); add(Icon.DAGOBAH); add(Icon.LEGACY_BLOCK_6); }});
    }

    @Test public void TdoshokHuntingVowActuallyDeploysOnTable() {
        var scn = scenario(); scn.StartGame(); var vow = scn.GetDSCard("vow"); scn.MoveCardsToDSHand(vow); scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(vow); scn.PassAllResponses(); assertEquals(Zone.SIDE_OF_TABLE, vow.getZone());
    }

    private VirtualTableScenario battle(int ownNonUnique, boolean uniqueInstead, boolean enemyInstead, boolean remote, boolean naturalDestiny) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation(); scn.MoveCardsToDSSideOfTable(scn.GetDSCard("vow"));
        scn.MoveCardsToLocation(site, scn.GetLSCard("obiwan"));
        var alienSite = remote ? scn.GetLSStartingLocation() : site;
        if (ownNonUnique > 0) scn.MoveCardsToLocation(alienSite, scn.GetDSCard("jawa1"));
        if (ownNonUnique > 1) scn.MoveCardsToLocation(alienSite, scn.GetDSCard("jawa2"));
        if (uniqueInstead) scn.MoveCardsToLocation(site, scn.GetDSCard("bossk"));
        if (enemyInstead) scn.MoveCardsToLocation(site, scn.GetLSCard("jawa"));
        if (remote) scn.MoveCardsToLocation(site, scn.GetDSCard("officer"));
        if (naturalDestiny) scn.MoveCardsToLocation(site, scn.GetDSCard("vader"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); return scn;
    }

    @Test public void TdoshokHuntingVowTwoOwnNonUniqueAliensGrantOneDestinyAndReduceOnlyOpponentPowerByThree() {
        var scn = battle(2, false, false, false, false);
        assertEquals(1, scn.GetDSBattleDestinyCount()); assertEquals(2, scn.GetLSTotalPower()); assertEquals(2, scn.GetDSTotalPower());
    }

    @Test public void TdoshokHuntingVowDoesNotAddExtraDestinyWhenAlreadyAble() {
        assertEquals(1, battle(2, false, false, false, true).GetDSBattleDestinyCount());
    }

    @Test public void TdoshokHuntingVowOneAlienOrUniqueCompanionDoesNotQualify() {
        var one = battle(1, false, false, false, false); assertEquals(0, one.GetDSBattleDestinyCount()); assertEquals(5, one.GetLSTotalPower());
        var unique = battle(1, true, false, false, false); assertEquals(0, unique.GetDSBattleDestinyCount()); assertEquals(5, unique.GetLSTotalPower());
    }

    @Test public void TdoshokHuntingVowOpponentsAlienAndRemoteAliensDoNotQualify() {
        var enemy = battle(1, false, true, false, false); assertEquals(0, enemy.GetDSBattleDestinyCount()); assertEquals(6, enemy.GetLSTotalPower());
        var remote = battle(2, false, false, true, false); assertEquals(0, remote.GetDSBattleDestinyCount()); assertEquals(5, remote.GetLSTotalPower());
    }

    @Test public void TdoshokHuntingVowBonusesRevokeWhenEffectLeavesPlay() {
        var scn = battle(2, false, false, false, false); scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("vow"));
        assertEquals(0, scn.GetDSBattleDestinyCount()); assertEquals(5, scn.GetLSTotalPower());
    }

    @Test public void TdoshokHuntingVowBosskAtAnyNonChamberSiteBoostsTrandoshansButNotOtherSpecies() {
        var scn = scenario(); scn.StartGame(); var bossk = scn.GetDSCard("bossk"); var dengar = scn.GetDSCard("dengar");
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("vow")); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), bossk, dengar);
        assertEquals(5, scn.GetPower(bossk)); assertEquals(5, scn.GetForfeit(bossk)); assertEquals(3, scn.GetForfeit(dengar));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), bossk); assertEquals(5, scn.GetPower(bossk)); assertEquals(5, scn.GetForfeit(bossk));
        scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("vow")); assertEquals(4, scn.GetPower(bossk)); assertEquals(3, scn.GetForfeit(bossk));
    }

    @Test public void TdoshokHuntingVowBosskInAudienceChamberOrOffTableDoesNotGrantBonuses() {
        var scn = scenario(); scn.StartGame(); var bossk = scn.GetDSCard("bossk"); scn.MoveLocationToTable(scn.GetDSCard("chamber"));
        scn.MoveCardsToDSSideOfTable(scn.GetDSCard("vow")); scn.MoveCardsToLocation(scn.GetDSCard("chamber"), bossk);
        assertEquals(4, scn.GetPower(bossk)); assertEquals(3, scn.GetForfeit(bossk));
        scn.MoveCardsToDSHand(bossk); assertEquals(4, scn.GetPower(bossk)); assertEquals(3, scn.GetForfeit(bossk));
    }

    @Test public void TdoshokHuntingVowActuallyDeploysOriginalMolatorAndReshuffles() {
        var scn = scenario(); scn.StartGame(); var vow = scn.GetDSCard("vow"); var molator = scn.GetDSCard("molator");
        scn.MoveCardsToDSSideOfTable(vow); scn.SkipToDSTurn(Phase.DEPLOY);
        // Registered minimal Block6 counterpart pins only the printed exclusion; no unimplemented real card behavior is simulated.
        var block6 = com.gempukku.swccgo.cards.legacy.MissionTestSupport.putInHand(scn, 9037, scn.DS,
                new com.gempukku.swccgo.cards.AbstractNormalEffect(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE,
                        "Molator", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V) {{ addIcon(Icon.LEGACY_BLOCK_6); }});
        assertEquals(1, block6.getBlueprint().getIconCount(Icon.LEGACY_BLOCK_6));
        scn.MoveCardsToTopOfDSReserveDeck(molator, block6, scn.GetDSCard("officer"));
        var remaining = scn.GetDSReserveDeck().stream().filter(c -> c != molator).findFirst().orElseThrow();
        scn.gameState().markCardsRevealedFromPile(java.util.Collections.singletonList(remaining));
        int force = scn.GetDSForcePileCount(); scn.DSUseCardAction(vow, "Deploy non-Block-6 Molator from Reserve Deck");
        scn.PassAllResponses(); assertTrue(selectable(scn, molator)); assertFalse(selectable(scn, block6));
        assertFalse(selectable(scn, scn.GetDSCard("officer"))); scn.DSChooseCard(molator); scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, molator.getZone()); assertEquals(force, scn.GetDSForcePileCount());
        assertFalse(scn.gameState().isCardRevealedFromPile(remaining));
    }

    private boolean selectable(VirtualTableScenario scn, com.gempukku.swccgo.game.PhysicalCardImpl card) {
        var blueprints = scn.GetADParam(scn.DS, "blueprintId"); var ids = scn.GetADParam(scn.DS, "cardId");
        var flags = scn.GetADParam(scn.DS, "selectable"); int size = blueprints != null ? blueprints.length : ids.length;
        for (int i = 0; i < size; i++) {
            boolean same = blueprints != null ? blueprints[i].equals(card.getBlueprintId(true)) : ids[i].equals(String.valueOf(card.getCardId()));
            if (same) return flags == null || Boolean.parseBoolean(flags[i]);
        }
        return false;
    }

    @Test public void TdoshokHuntingVowSearchIsNormalDeployPhaseOnly() {
        var scn = scenario(); scn.StartGame(); var vow = scn.GetDSCard("vow"); scn.MoveCardsToDSSideOfTable(vow);
        scn.MoveCardsToTopOfDSReserveDeck(scn.GetDSCard("molator")); scn.SkipToDSTurn(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(vow, "Deploy non-Block-6 Molator from Reserve Deck"));
    }

    @Test public void TdoshokHuntingVowCannotBeTargetedByActualAlter() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToDSSideOfTable(scn.GetDSCard("vow"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("obiwan")); scn.MoveCardsToLSHand(scn.GetLSCard("alter"));
        scn.SkipToLSTurn(Phase.CONTROL); assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("alter")));
        var control = scenario(); control.StartGame(); control.MoveCardsToDSSideOfTable(control.GetDSCard("molator"));
        control.MoveCardsToLocation(control.GetLSStartingLocation(), control.GetLSCard("obiwan")); control.MoveCardsToLSHand(control.GetLSCard("alter"));
        control.SkipToLSTurn(Phase.CONTROL); assertTrue(control.LSCardPlayAvailable(control.GetLSCard("alter")));
    }
}
