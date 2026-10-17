package com.gempukku.swccgo.rules.heroic;

import com.gempukku.swccgo.cards.AbstractCharacterWeapon;
import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Statistic;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.FireWeaponAction;
import com.gempukku.swccgo.logic.actions.FireWeaponActionBuilder;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByWeaponsModifier;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HeroicTests {

    private static class TestHeroicRebel extends AbstractRebel {
        TestHeroicRebel() {
            super(Side.LIGHT, 2, 3, 3, 3, 5, "Test Heroic Rebel", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1);
            addIcons(Icon.WARRIOR);
            addKeywords(Keyword.HEROIC);
        }
    }

    /** Line A hits only Heroic characters. Line B hits only non-Heroic characters. */
    private static class TestTwoLineWeapon extends AbstractCharacterWeapon {
        TestTwoLineWeapon() {
            super(Side.DARK, 1, "Test Two Line Weapon", Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C1);
        }

        @Override
        protected List<FireWeaponAction> getGameTextFireWeaponActions(String playerId, SwccgGame game, PhysicalCard self, boolean forFree, int extraForceRequired, PhysicalCard sourceCard, boolean repeatedFiring, Filter targetedAsCharacter, Float defenseValueAsCharacter, Filter fireAtTargetFilter, boolean ignorePerAttackOrBattleLimit) {
            FireWeaponActionBuilder actionBuilder = FireWeaponActionBuilder.startBuildPrep(playerId, game, sourceCard, self, forFree, extraForceRequired, repeatedFiring, targetedAsCharacter, defenseValueAsCharacter, fireAtTargetFilter, ignorePerAttackOrBattleLimit)
                    .targetUsingForce(Filters.and(Filters.character, Keyword.HEROIC), 1, TargetingReason.TO_BE_HIT)
                    .targetUsingForce(Filters.and(Filters.character, Filters.not(Filters.and(Keyword.HEROIC))), 1, TargetingReason.TO_BE_HIT)
                    .finishBuildPrep();
            if (actionBuilder != null) {
                return Collections.singletonList(actionBuilder.buildFireWeaponWithHitAction(1, Statistic.DEFENSE_VALUE));
            }
            return null;
        }
    }

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("blasterLS", "1_152");
                    put("skyhopper", "2_75");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
                    put("blaster", "1_317");
                    put("blaster2", "1_317");
                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    private void grantHeroic(VirtualTableScenario scn, PhysicalCardImpl card) {
        scn.ApplyAdHocModifier(new KeywordModifier(card, Keyword.HEROIC));
    }

    @SuppressWarnings("unchecked")
    private PhysicalCardImpl attachTwoLineWeapon(VirtualTableScenario scn, PhysicalCardImpl bearer) {
        String blueprintId = "2000_9001";
        try {
            Field field = SwccgCardBlueprintLibrary.class.getDeclaredField("_blueprintMap");
            field.setAccessible(true);
            Map<String, SwccgCardBlueprint> map = (Map<String, SwccgCardBlueprint>) field.get(VirtualTableScenario._cardLibrary);
            map.put(blueprintId, new TestTwoLineWeapon());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        PhysicalCardImpl weapon = (PhysicalCardImpl) scn.gameState().createPhysicalCard("Dark Side Player", VirtualTableScenario._cardLibrary, blueprintId);
        scn.gameState().addCardToZone(weapon, Zone.HAND, "Dark Side Player", true, false, false);
        scn.AttachCardsTo(bearer, weapon);
        return weapon;
    }

    private int lastTotalWeaponDestiny(VirtualTableScenario scn) {
        Integer last = null;
        String log = String.join("\n", scn.gameState().getLastMessages());
        for (String msg : scn.gameState().getLastMessages()) {
            String lower = msg.toLowerCase();
            for (String prefix : new String[] {"total weapon destiny is ", "total destiny: "}) {
                int idx = lower.lastIndexOf(prefix);
                if (idx >= 0) {
                    String rest = msg.substring(idx + prefix.length()).trim();
                    last = (int) Float.parseFloat(rest.split("[^0-9.]")[0]);
                }
            }
        }
        assertTrue("Expected a total weapon destiny log, got: " + log, last != null);
        return last;
    }

    @Test
    public void HeroicHumanReadableName() {
        assertEquals("Heroic", Keyword.HEROIC.getHumanReadable());
        assertTrue(Keyword.HEROIC.isCharacteristic());
        assertTrue(Keyword.HEROIC.isInfoDisplayable());
    }

    @Test
    public void TestRebelReportsHeroicKeyword() {
        var rebel = new TestHeroicRebel();
        assertTrue(rebel.hasKeyword(Keyword.HEROIC));
    }

    @Test
    public void KeywordModifierGrantsHeroicAndFilterAcceptsIt() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        assertFalse(Filters.and(Keyword.HEROIC).accepts(scn.game(), luke));

        grantHeroic(scn, luke);
        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(Filters.and(Keyword.HEROIC).accepts(scn.game(), luke));
    }

    @Test
    public void InBattleWeaponMustTargetHeroicBeforeNonHeroic() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var rebel = scn.GetLSFiller(1);
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, rebel, trooper);
        scn.AttachCardsTo(trooper, blaster);
        grantHeroic(scn, luke);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue("Fire not offered. actions=" + scn.GetDSAvailableActions(),
                scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");

        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        assertTrue(scn.DSHasCardChoiceNotAvailable(rebel));
    }

    @Test
    public void AfterHeroicIsHitNonHeroicMayBeTargeted() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var rebel = scn.GetLSFiller(1);
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var blaster = scn.GetDSCard("blaster");
        var blaster2 = scn.GetDSCard("blaster2");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, rebel, trooper, trooper2);
        scn.AttachCardsTo(trooper, blaster);
        scn.AttachCardsTo(trooper2, blaster2);
        grantHeroic(scn, luke);
        scn.DSActivateForceCheat(6);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(7);

        scn.DSUseCardAction(blaster, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertTrue("Luke should be hit. log=" + String.join("\n", scn.gameState().getLastMessages()), luke.isHit());

        assertTrue(scn.AwaitingDSWeaponsSegmentActions() || scn.AwaitingLSWeaponsSegmentActions());
        if (scn.AwaitingLSWeaponsSegmentActions()) {
            scn.LSPass();
            scn.PassAllResponses();
        }
        assertTrue(scn.DSCardActionAvailable(blaster2, "Fire"));
        scn.DSUseCardAction(blaster2, "Fire");

        assertTrue(scn.DSHasCardChoiceAvailable(rebel));
    }

    @Test
    public void WeaponThatCannotTargetHeroicMayStillTargetNonHeroic() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var rebel = scn.GetLSFiller(1);
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, rebel, trooper);
        scn.AttachCardsTo(trooper, blaster);
        grantHeroic(scn, luke);
        scn.ApplyAdHocModifier(new MayNotBeTargetedByWeaponsModifier(luke));
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        scn.DSUseCardAction(blaster, "Fire");

        assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
        assertTrue(scn.DSHasCardChoiceAvailable(rebel));
    }

    @Test
    public void FiringAtHeroicAddsOneToTotalWeaponDestiny() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.AttachCardsTo(trooper, blaster);
        grantHeroic(scn, luke);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(3);

        scn.DSUseCardAction(blaster, "Fire");
        if (scn.DSHasCardChoiceAvailable(luke)) {
            scn.DSChooseCard(luke);
        }
        scn.PassAllResponses();

        assertEquals(4, lastTotalWeaponDestiny(scn));

        var control = GetScenario();
        var nonHeroic = control.GetLSFiller(1);
        var controlTrooper = control.GetDSCard("trooper");
        var controlBlaster = control.GetDSCard("blaster");
        var controlSite = control.GetDSStartingLocation();

        control.StartGame();
        control.MoveCardsToLocation(controlSite, nonHeroic, controlTrooper);
        control.AttachCardsTo(controlTrooper, controlBlaster);
        control.DSActivateForceCheat(4);
        control.DSStartBattleAndSkipToWeaponsSegment(controlSite);
        control.PrepareDSDestiny(3);

        control.DSUseCardAction(controlBlaster, "Fire");
        if (control.DSHasCardChoiceAvailable(nonHeroic)) {
            control.DSChooseCard(nonHeroic);
        }
        control.PassAllResponses();

        assertEquals(3, lastTotalWeaponDestiny(control));
    }

    @Test
    public void TwoLineWeaponDoesNotOfferNonHeroicWhileHeroicMatchesOtherLine() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var rebel = scn.GetLSFiller(1);
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, rebel, trooper);
        var weapon = attachTwoLineWeapon(scn, trooper);
        grantHeroic(scn, luke);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue("Fire not offered. actions=" + scn.GetDSAvailableActions(),
                scn.DSCardActionAvailable(weapon, "Fire"));
        scn.DSUseCardAction(weapon, "Fire");

        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        assertTrue(scn.DSHasCardChoiceNotAvailable(rebel));
    }

    @Test
    public void OwnHeroicDoesNotRestrictFiringAtOpponentsNonHeroic() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var blasterLS = scn.GetLSCard("blasterLS");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper, trooper2);
        scn.AttachCardsTo(luke, blasterLS);
        grantHeroic(scn, luke);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue("Fire not offered. actions=" + scn.GetLSAvailableActions(),
                scn.LSCardActionAvailable(blasterLS, "Fire"));
        scn.LSUseCardAction(blasterLS, "Fire");

        assertTrue(scn.LSHasCardChoiceAvailable(trooper));
        assertTrue(scn.LSHasCardChoiceAvailable(trooper2));
    }

    @Test
    public void EnclosedHeroicDoesNotProtectCharactersAtTheSite() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var rebel = scn.GetLSFiller(1);
        var skyhopper = scn.GetLSCard("skyhopper");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, rebel, skyhopper, trooper);
        scn.BoardAsPassenger(skyhopper, luke);
        scn.AttachCardsTo(trooper, blaster);
        grantHeroic(scn, luke);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        scn.DSUseCardAction(blaster, "Fire");

        assertTrue(scn.DSHasCardChoiceNotAvailable(luke));
        assertTrue(scn.DSHasCardChoiceAvailable(rebel));
    }
}
