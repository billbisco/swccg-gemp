package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;

public class Card_1261_064_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("obiwan", "1_21"); put("han", "1_11"); put("worrt", "6_48"); }},
                new HashMap<>() {{ put("pistol", "1261_64"); put("tarkin", "1_179"); put("yularen", "1_166");
                    put("vader", "1_168"); }}, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ISBBlasterPistolPrintedStatsIconsAndKeywordsMatchSource() {
        var scn = scenario(); var b = scn.GetDSCard("pistol").getBlueprint();
        assertEquals("ISB Blaster Pistol", b.getTitle()); assertFalse(b.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness()); assertEquals(Side.DARK, b.getSide());
        assertEquals(5, b.getDestiny(), scn.epsilon); assertEquals(CardSubtype.CHARACTER, b.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>() {{ add(CardType.WEAPON); }});
        scn.BlueprintKeywordCheck(b, new ArrayList<>() {{ add(Keyword.BLASTER); }});
        scn.BlueprintIconCheck(b, new ArrayList<>() {{ add(Icon.WEAPON); add(Icon.CLOUD_CITY); add(Icon.LEGACY_BLOCK_6); }});
    }

    private VirtualTableScenario deployment() {
        var scn = scenario(); scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("tarkin"), scn.GetDSCard("yularen"), scn.GetDSCard("vader"));
        // Apply after placement: removing a card from its old zone unregisters its modifiers.
        scn.ApplyAdHocModifier(new KeywordModifier(scn.GetDSCard("yularen"), Keyword.ISB_AGENT));
        assertTrue(scn.HasKeyword(scn.GetDSCard("yularen"), Keyword.ISB_AGENT));
        scn.MoveCardsToDSHand(scn.GetDSCard("pistol")); scn.SkipToDSTurn(Phase.DEPLOY); return scn;
    }

    private void deploy(String bearer) {
        var scn = deployment(); var pistol = scn.GetDSCard("pistol"); int force = scn.GetDSForcePileCount();
        scn.DSDeployCard(pistol); scn.DSChooseCard(scn.GetDSCard(bearer)); scn.PassAllResponses();
        assertSame(scn.GetDSCard(bearer), pistol.getAttachedTo()); assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test public void ISBBlasterPistolActuallyDeploysOnNonWarriorTarkinForFree() { deploy("tarkin"); }
    @Test public void ISBBlasterPistolActuallyDeploysOnISBAgentForFree() { deploy("yularen"); }

    @Test
    public void ISBBlasterPistolCannotDeployOnOrdinaryWarrior() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(scn.GetDSCard("pistol")); scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("pistol")));
    }

    @Test
    public void ISBBlasterPistolMatchingCharacterIsTarkinOnly() {
        var scn = deployment(); var filter = scn.GetDSCard("pistol").getBlueprint().getMatchingCharacterFilter();
        assertTrue(filter.accepts(scn.game(), scn.GetDSCard("tarkin")));
        assertFalse(filter.accepts(scn.game(), scn.GetDSCard("yularen")));
    }

    private VirtualTableScenario battle(String bearer) {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard(bearer), scn.GetLSCard("obiwan"), scn.GetLSCard("han"));
        scn.ApplyAdHocModifier(new KeywordModifier(scn.GetDSCard("yularen"), Keyword.ISB_AGENT));
        assertTrue(scn.HasKeyword(scn.GetDSCard("yularen"), Keyword.ISB_AGENT));
        scn.AttachCardsTo(scn.GetDSCard(bearer), scn.GetDSCard("pistol"));
        scn.DSStartBattleAndSkipToWeaponsSegment(site); return scn;
    }

    private void fire(VirtualTableScenario scn, int destiny) {
        var target = scn.GetLSCard("obiwan"); var pistol = scn.GetDSCard("pistol");
        int force = scn.GetDSForcePileCount(); scn.PrepareDSDestiny(destiny); int reserve = scn.GetDSReserveDeckCount();
        scn.DSUseCardAction(pistol, "Fire"); if (scn.DSHasCardChoiceAvailable(target)) scn.DSChooseCard(target);
        scn.PassAllResponses(); assertEquals(force, scn.GetDSForcePileCount()); assertEquals(reserve - 1, scn.GetDSReserveDeckCount());
    }

    @Test
    public void ISBBlasterPistolTarkinBonusHitsAtSixPlusOneAndResetsForfeit() {
        var scn = battle("tarkin"); assertEquals(6, scn.GetDefense(scn.GetLSCard("obiwan")));
        fire(scn, 6); assertTrue(scn.GetLSCard("obiwan").isHit()); assertEquals(0, scn.GetForfeit(scn.GetLSCard("obiwan")));
    }

    @Test
    public void ISBBlasterPistolTarkinEqualDefenseMissesAndPreservesForfeit() {
        var scn = battle("tarkin"); int forfeit = scn.GetForfeit(scn.GetLSCard("obiwan"));
        fire(scn, 5); assertFalse(scn.GetLSCard("obiwan").isHit()); assertEquals(forfeit, scn.GetForfeit(scn.GetLSCard("obiwan")));
    }

    @Test
    public void ISBBlasterPistolISBAgentGetsNoTarkinBonus() {
        var scn = battle("yularen"); fire(scn, 6); assertFalse(scn.GetLSCard("obiwan").isHit());
        assertEquals(9, scn.GetForfeit(scn.GetLSCard("obiwan")));
    }

    @Test
    public void ISBBlasterPistolISBAgentHitsAboveDefenseAndResetsForfeit() {
        var scn = battle("yularen"); fire(scn, 7); assertTrue(scn.GetLSCard("obiwan").isHit());
        assertEquals(0, scn.GetForfeit(scn.GetLSCard("obiwan")));
    }

    @Test
    public void ISBBlasterPistolActuallyFiresAtCreatureForFree() {
        var scn = scenario(); scn.StartGame(); var site = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("tarkin"), scn.GetLSCard("worrt"));
        scn.AttachCardsTo(scn.GetDSCard("tarkin"), scn.GetDSCard("pistol"));
        scn.DSActivateMaxForceAndPass(); scn.SkipToPhase(Phase.BATTLE);
        scn.DSUseCardAction(site, "Initiate attack on creature"); scn.PassAllResponses(); assertTrue(scn.gameState().isDuringAttack());
        int force = scn.GetDSForcePileCount(); scn.PrepareDSDestiny(5);
        scn.DSUseCardAction(scn.GetDSCard("pistol"), "Fire");
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("worrt"))) scn.DSChooseCard(scn.GetLSCard("worrt"));
        scn.PassAllResponses();
        assertTrue(scn.GetLSCard("worrt").isHit()); assertEquals(force, scn.GetDSForcePileCount());
    }

    @Test
    public void ISBBlasterPistolActuallyForfeitsForThreeIntoUsedAndLeavesBearer() {
        var scn = battle("yularen"); var pistol = scn.GetDSCard("pistol"); var bearer = scn.GetDSCard("yularen");
        scn.SkipToDamageSegment(false); int before = scn.GetUnpaidDSBattleDamage();
        scn.DSUseCardAction(pistol, "Forfeit ISB Blaster Pistol"); scn.PassAllResponses();
        assertEquals(Zone.USED_PILE, pistol.getZone()); assertEquals(Zone.AT_LOCATION, bearer.getZone());
        assertEquals(before - 3, scn.GetUnpaidDSBattleDamage());
    }
}
