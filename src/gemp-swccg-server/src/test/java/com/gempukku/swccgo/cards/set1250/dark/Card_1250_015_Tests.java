package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1250_015_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("luke", "1_19"); put("rifle", "1_153"); put("alien", "1_31"); }},
                new HashMap<>() {{ put("armor", "1250_15"); put("alien", "1_172"); put("four", "1_171"); put("jango", "201_25"); put("mandalorian", "1115_66"); put("trooper", "1_194"); put("vader", "1_168"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test public void MandalorianArmorStatsIconsAndKeywordsMatchPrintedImage() {
        var scn = scenario(); var bp = scn.GetDSCard("armor").getBlueprint();
        assertEquals("Mandalorian Armor", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(3, bp.getDestiny(), scn.epsilon); assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.DEVICE); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.DEVICE); add(Icon.EPISODE_I); add(Icon.CLOUD_CITY); add(Icon.LEGACY_BLOCK_5); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.DEPLOYS_ON_CHARACTERS); }});
    }
    @Test public void MandalorianArmorDeploysOnlyOnOwnAlienBelowFourAbility() {
        var scn = scenario(); var site = scn.GetDSStartingLocation(); var armor = scn.GetDSCard("armor");
        scn.StartGame(); scn.MoveCardsToLocation(site, scn.GetDSCard("alien"), scn.GetDSCard("four"), scn.GetDSCard("jango"), scn.GetDSCard("trooper"), scn.GetLSCard("alien"));
        scn.MoveCardsToDSHand(armor); scn.SkipToDSTurn(Phase.DEPLOY); scn.DSDeployCard(armor);
        assertTrue(scn.DSHasCardChoicesAvailable(scn.GetDSCard("alien"), scn.GetDSCard("jango")));
        assertTrue(scn.DSHasCardChoicesNotAvailable(scn.GetDSCard("four"), scn.GetDSCard("trooper"), scn.GetLSCard("alien")));
        scn.DSChooseCard(scn.GetDSCard("alien")); scn.PassAllResponses(); assertEquals(scn.GetDSCard("alien"), armor.getAttachedTo());
    }
    @Test public void MandalorianArmorGrantsBountyHunterAndImmunityOnlyToWearer() {
        var scn = scenario(); var wearer = scn.GetDSCard("alien"); var armor = scn.GetDSCard("armor");
        scn.StartGame(); scn.MoveCardsToLocation(scn.GetDSStartingLocation(), wearer, scn.GetDSCard("trooper"));
        scn.AttachCardsTo(wearer, armor); scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.HasKeyword(wearer, Keyword.BOUNTY_HUNTER)); assertFalse(scn.HasKeyword(scn.GetDSCard("trooper"), Keyword.BOUNTY_HUNTER));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), wearer), scn.epsilon);
        scn.MoveCardsToDSHand(armor); assertFalse(scn.HasKeyword(wearer, Keyword.BOUNTY_HUNTER));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), wearer), scn.epsilon);
    }
    private VirtualTableScenario hit(String wearerName, String targetName, int destiny) {
        var scn = scenario(); var site = scn.GetDSStartingLocation();
        scn.StartGame(); scn.MoveCardsToLocation(site, scn.GetDSCard(wearerName), scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.AttachCardsTo(scn.GetDSCard(wearerName), scn.GetDSCard("armor"));
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("rifle"));
        scn.LSStartBattleAndSkipToWeaponsSegment(site); scn.PrepareLSDestiny(destiny);
        scn.LSUseCardAction(scn.GetLSCard("rifle"), "Fire"); scn.LSChooseCard(scn.GetDSCard(targetName));
        scn.PassResponses("Fire "); scn.PassDestinyDrawResponses(); scn.PassResponses("ABOUT_TO_BE_HIT");
        System.out.println("ARMOR_FIRE_STATE " + scn.GetCurrentDecision().getText());
        return scn;
    }
    private void restore(String wearerName, Zone pile) {
        var scn = hit(wearerName, wearerName, 7); var wearer = scn.GetDSCard(wearerName); var armor = scn.GetDSCard("armor");
        assertTrue(wearer.isHit()); assertTrue(scn.DSCardActionAvailable(armor, "Restore wearer to normal"));
        scn.DSUseCardAction(armor, "Restore wearer to normal"); scn.PassAllResponses();
        assertFalse(wearer.isHit()); assertEquals(Zone.AT_LOCATION, wearer.getZone()); assertInZone(pile, armor);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), wearer), scn.epsilon);
    }
    @Test public void MandalorianArmorRestoresJustHitWearerAtLostPileCost() { restore("alien", Zone.LOST_PILE); }
    @Test public void MandalorianArmorRestoresJangoAtUsedPileCost() { restore("jango", Zone.USED_PILE); }
    @Test public void MandalorianArmorUsesLostPileForTheMandalorianInsteadOfJango() { restore("mandalorian", Zone.LOST_PILE); }
    @Test public void MandalorianArmorMayDeclineRestorationAndRemainAttached() {
        var scn = hit("alien", "alien", 7); var armor = scn.GetDSCard("armor");
        assertTrue(scn.DSCardActionAvailable(armor)); scn.PassAllResponses();
        assertTrue(scn.GetDSCard("alien").isHit()); assertEquals(scn.GetDSCard("alien"), armor.getAttachedTo());
    }
    @Test public void MandalorianArmorDoesNotRestoreAnotherJustHitCharacter() {
        var scn = hit("alien", "trooper", 7); assertTrue(scn.GetDSCard("trooper").isHit());
        assertTrue(scn.AnyDecisionsAvailable(scn.DS)); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("armor")));
    }
    @Test public void MandalorianArmorDoesNotRespondToMissedWeaponShot() {
        var scn = hit("jango", "jango", 1); assertFalse(scn.GetDSCard("jango").isHit());
        assertTrue(scn.AnyDecisionsAvailable(scn.DS)); assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("armor")));
    }
    private void attrition(int destiny, boolean immune) {
        var scn = scenario(); var site = scn.GetDSStartingLocation(); var wearer = scn.GetDSCard("alien");
        scn.StartGame(); scn.MoveCardsToLocation(site, wearer, scn.GetDSCard("vader"), scn.GetLSCard("luke"));
        scn.AttachCardsTo(wearer, scn.GetDSCard("armor")); scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(destiny); scn.PrepareDSDestiny(7);
        scn.DSInitiateBattle(site); scn.SkipToDamageSegment(true); scn.PassDamageSegmentActions();
        assertEquals(0, scn.GetUnpaidDSBattleDamage()); assertEquals(destiny, scn.GetUnpaidDSAttrition());
        if (immune) {
            assertTrue(scn.DSDecisionAvailable("Choose a card from battle to forfeit (if desired)")); scn.DSPass();
            assertEquals(Zone.AT_LOCATION, wearer.getZone()); assertEquals(wearer, scn.GetDSCard("armor").getAttachedTo());
        }
        else {
            assertTrue(scn.DSDecisionAvailable("Choose a card from battle to forfeit")); assertFalse(scn.DSDecisionAvailable("(if desired)"));
            scn.DSChooseCard(wearer); scn.PassAllResponses();
            System.out.println("ARMOR_FORFEIT_STATE " + scn.GetCurrentDecision().getText());
            assertInZone(Zone.LOST_PILE, wearer);
        }
    }
    @Test public void MandalorianArmorWearerSurvivesAttritionBelowFour() { attrition(3, true); }
    @Test public void MandalorianArmorDoesNotProtectFromAttritionEqualToFour() { attrition(4, false); }
    @Test public void MandalorianArmorDoesNotProtectFromAttritionAboveFour() { attrition(5, false); }
}
