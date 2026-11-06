package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;

public class Card_1262_013_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("luke", "1_19"); put("rifle", "1_153"); put("saber", "1_155"); put("han", "108_1"); }},
                new HashMap<>() {{ put("room", "1262_13"); put("trooper", "1_194"); put("reinforcement", "1_194"); put("rifle", "1_317"); put("artillery", "3_159"); put("device", "1_201"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    @Test public void CloudCityDiningRoomStatsIconsAndKeywordsMatchPrintedImage() {
        var scn = scenario(); var bp = scn.GetDSCard("room").getBlueprint();
        assertEquals("Cloud City: Dining Room", bp.getTitle()); assertTrue(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide()); assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(0, bp.getDestiny(), scn.epsilon); assertNull(bp.getDeployCost());
        assertEquals(1, bp.getIconCount(Icon.DARK_FORCE)); assertEquals(1, bp.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_2, bp.getExpansionSet()); assertEquals(Rarity.V, bp.getRarity());
        assertEquals(CardSubtype.SITE, bp.getCardSubtype()); assertEquals(Title.Bespin, bp.getSystemName());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>() {{ add(CardType.LOCATION); }});
        scn.BlueprintIconCheck(bp, new ArrayList<>() {{ add(Icon.DARK_FORCE); add(Icon.LIGHT_FORCE); add(Icon.CLOUD_CITY); add(Icon.INTERIOR_SITE); add(Icon.MOBILE); add(Icon.SCOMP_LINK); add(Icon.LEGACY_BLOCK_6); }});
        scn.BlueprintKeywordCheck(bp, new ArrayList<>() {{ add(Keyword.CLOUD_CITY_LOCATION); }});
    }
    private void force(VirtualTableScenario scn, boolean dark, int amount) {
        String player = dark ? scn.DS : scn.LS;
        if (dark) scn.MoveCardsToTopOfDSReserveDeck(scn.gameState().getForcePile(player).toArray(PhysicalCardImpl[]::new));
        else scn.MoveCardsToTopOfLSReserveDeck(scn.gameState().getForcePile(player).toArray(PhysicalCardImpl[]::new));
        if (dark) scn.EnsureDSForcePile(amount); else scn.EnsureLSForcePile(amount);
    }
    private VirtualTableScenario reactBattle(boolean here, int force) {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("room"));
        var site = here ? scn.GetDSCard("room") : scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(scn.GetDSCard("reinforcement"), scn.GetDSCard("rifle"), scn.GetDSCard("artillery"), scn.GetDSCard("device"));
        scn.SkipToLSTurn(Phase.BATTLE); force(scn, true, force); scn.LSInitiateBattle(site); return scn;
    }
    private boolean selectable(VirtualTableScenario scn, String blueprint) {
        String[] cards = scn.GetADParam(scn.DS, "blueprintId");
        String[] selectable = scn.GetADParam(scn.DS, "selectable");
        if (cards == null) return false;
        for (int i = 0; i < cards.length; i++) {
            if (blueprint.equals(cards[i]) && (selectable == null || Boolean.parseBoolean(selectable[i]))) return true;
        }
        return false;
    }
    private void deployReact(VirtualTableScenario scn, String cardName) {
        var room = scn.GetDSCard("room"); var card = scn.GetDSCard(cardName);
        assertTrue(scn.DSCardActionAvailable(room, "Deploy card as a 'react'"));
        scn.DSUseCardAction(room, "Deploy card as a 'react'");
        assertTrue(scn.DSHasCardChoiceAvailable(card));
        assertFalse(selectable(scn, "1_201"));
        scn.DSChooseCard(card);
        scn.PassAllResponses();
        scn.DSChooseCard(cardName.equals("rifle") ? scn.GetDSCard("trooper") : room);
        scn.PassAllResponses();
        assertEquals(room, scn.game().getModifiersQuerying().getLocationThatCardIsAt(scn.gameState(), card));
    }
    @Test public void CloudCityDiningRoomDeploysCharacterAsBattleReactAtNormalCost() {
        var scn = reactBattle(true, 5); int force = scn.GetDSForcePileCount(); deployReact(scn, "reinforcement");
        assertEquals(force - 1, scn.GetDSForcePileCount()); assertEquals(Zone.AT_LOCATION, scn.GetDSCard("reinforcement").getZone());
    }
    @Test public void CloudCityDiningRoomDeploysCharacterWeaponAsBattleReact() {
        var scn = reactBattle(true, 5); int force = scn.GetDSForcePileCount(); deployReact(scn, "rifle");
        assertEquals(force - 1, scn.GetDSForcePileCount()); assertEquals(scn.GetDSCard("trooper"), scn.GetDSCard("rifle").getAttachedTo());
    }
    @Test public void CloudCityDiningRoomAlsoDeploysArtilleryWeaponAsReact() {
        var scn = reactBattle(true, 5); int force = scn.GetDSForcePileCount(); deployReact(scn, "artillery");
        assertEquals(force - 3, scn.GetDSForcePileCount()); assertEquals(scn.GetDSCard("room"), scn.GetDSCard("artillery").getAttachedTo());
    }
    @Test public void CloudCityDiningRoomMayDeclineBattleReact() {
        var scn = reactBattle(true, 5); assertTrue(scn.DSCardActionAvailable(scn.GetDSCard("room")));
        scn.PassAllResponses(); assertInZone(Zone.HAND, scn.GetDSCard("reinforcement")); assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }
    @Test public void CloudCityDiningRoomDoesNotOfferUnaffordableReacts() {
        var scn = reactBattle(true, 0); assertFalse(scn.AnyDecisionsAvailable(scn.DS)); assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }
    @Test public void CloudCityDiningRoomDoesNotGrantReactsToAnotherSite() {
        var scn = reactBattle(false, 5); assertFalse(scn.AnyDecisionsAvailable(scn.DS)); assertTrue(scn.AwaitingLSWeaponsSegmentActions());
    }
    @Test public void CloudCityDiningRoomCharacterReactCanStopForceDrain() {
        var scn = scenario(); var room = scn.GetDSCard("room");
        scn.StartGame(); scn.MoveLocationToTable(room); scn.MoveCardsToLocation(room, scn.GetLSCard("luke"));
        scn.MoveCardsToDSHand(scn.GetDSCard("reinforcement"), scn.GetDSCard("device")); scn.SkipToLSTurn(Phase.CONTROL);
        int life = scn.GetDSLifeForceRemaining(); scn.LSForceDrainAt(room); deployReact(scn, "reinforcement");
        assertEquals(life, scn.GetDSLifeForceRemaining());
    }
    @Test public void CloudCityDiningRoomDoesNotGrantDarkSideReactPermissionToLight() {
        var scn = scenario(); var room = scn.GetDSCard("room");
        scn.StartGame(); scn.MoveLocationToTable(room); scn.MoveCardsToLocation(room, scn.GetLSCard("luke"), scn.GetDSCard("trooper"));
        scn.MoveCardsToLSHand(scn.GetLSCard("han")); scn.SkipToDSTurn(Phase.BATTLE); scn.DSInitiateBattle(room);
        assertFalse(scn.AnyDecisionsAvailable(scn.LS)); assertTrue(scn.AwaitingDSWeaponsSegmentActions());
    }
    private VirtualTableScenario weapon(boolean here, boolean dark, int amount, String weapon) {
        var scn = scenario(); scn.StartGame(); scn.MoveLocationToTable(scn.GetDSCard("room"));
        var site = here ? scn.GetDSCard("room") : scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("trooper"));
        scn.AttachCardsTo(dark ? scn.GetDSCard("trooper") : scn.GetLSCard("luke"), dark ? scn.GetDSCard("rifle") : scn.GetLSCard(weapon));
        if (dark) { scn.SkipToLSTurn(Phase.BATTLE); force(scn, true, amount); scn.LSInitiateBattle(site); scn.PassAllResponses(); scn.LSPass(); }
        else { scn.SkipToDSTurn(Phase.BATTLE); force(scn, false, amount); scn.DSInitiateBattle(site); scn.PassAllResponses(); scn.DSPass(); }
        return scn;
    }
    @Test public void CloudCityDiningRoomLightBlasterFiresAndHitsWithZeroForce() {
        var scn = weapon(true, false, 0, "rifle"); var rifle = scn.GetLSCard("rifle");
        assertTrue(scn.LSCardActionAvailable(rifle, "Fire")); scn.PrepareLSDestiny(5);
        scn.LSUseCardAction(rifle, "Fire"); scn.LSChooseCard(scn.GetDSCard("trooper")); scn.PassForceUseResponses(); scn.PassWeaponFireWithDestinyDraw();
        assertEquals(0, scn.GetLSForcePileCount()); assertTrue(scn.GetDSCard("trooper").isHit());
    }
    @Test public void CloudCityDiningRoomLightBlasterAtAnotherSitePaysNormalFiringCost() {
        var scn = weapon(false, false, 2, "rifle"); scn.PrepareLSDestiny(5);
        scn.LSUseCardAction(scn.GetLSCard("rifle"), "Fire"); scn.LSChooseCard(scn.GetDSCard("trooper")); scn.PassForceUseResponses(); scn.PassWeaponFireWithDestinyDraw();
        assertEquals(0, scn.GetLSForcePileCount()); assertTrue(scn.GetDSCard("trooper").isHit());
    }
    @Test public void CloudCityDiningRoomDarkBlasterPaysNormalFiringCost() {
        var scn = weapon(true, true, 1, "rifle"); scn.PrepareDSDestiny(5);
        scn.DSUseCardAction(scn.GetDSCard("rifle"), "Fire"); scn.DSChooseCard(scn.GetLSCard("luke")); scn.PassForceUseResponses(); scn.PassWeaponFireWithDestinyDraw();
        assertEquals(0, scn.GetDSForcePileCount()); assertTrue(scn.GetLSCard("luke").isHit());
    }
    @Test public void CloudCityDiningRoomDoesNotLetDarkBlasterFireWithZeroForce() {
        var scn = weapon(true, true, 0, "rifle"); assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(scn.GetDSCard("rifle"), "Fire"));
    }
    @Test public void CloudCityDiningRoomDoesNotLetLightsaberFireWithZeroForce() {
        var scn = weapon(true, false, 0, "saber"); assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("saber"), "Fire"));
    }
    @Test public void CloudCityDiningRoomFreeFireModifierExcludesPermanentBlaster() {
        var scn = scenario(); var room = scn.GetDSCard("room"); var han = scn.GetLSCard("han"); var rifle = scn.GetLSCard("rifle");
        scn.StartGame(); scn.MoveLocationToTable(room); scn.MoveCardsToLocation(room, han, scn.GetLSCard("luke"));
        scn.AttachCardsTo(scn.GetLSCard("luke"), rifle); scn.SkipToDSTurn(Phase.CONTROL);
        var q = scn.game().getModifiersQuerying(); var modifiers = q.getModifiers(scn.gameState(), ModifierType.FIRE_WEAPON_FIRED_BY_FOR_FREE).stream().filter(m -> m.getSource(scn.gameState()) == room).toList();
        assertEquals(1, modifiers.size()); assertTrue(modifiers.getFirst().isAffectedTarget(scn.gameState(), q, rifle));
        assertFalse(modifiers.getFirst().isAffectedTarget(scn.gameState(), q, han.getBlueprint().getPermanentWeapon(han)));
    }
}
