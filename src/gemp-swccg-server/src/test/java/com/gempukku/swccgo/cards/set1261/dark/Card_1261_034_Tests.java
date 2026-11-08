package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class Card_1261_034_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(
                new HashMap<>(Map.of("corvette", "1_140", "han", "1_11", "luke", "1_19")),
                new HashMap<>(Map.of("protocols", "1261_34", "destroyer", "1_302", "other", "2_155",
                        "executor", "4_167", "launch", "4_165", "unique", "4_160", "ground", "1_292",
                        "trooper", "1_194", "officer", "1_192", "launch2", "4_165")),
                20, 20, StartingSetup.DefaultLSSpaceSystem, StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void printingUsesItsOwnTitleAndPrintedEffectMetadata() {
        var scn = scenario();
        var bp = scn.GetDSCard("protocols").getBlueprint();
        assertEquals("Fleet Security Protocols", bp.getTitle());
        assertFalse(bp.hasVirtualSuffix());
        assertEquals(Side.DARK, bp.getSide());
        assertEquals(3, bp.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNIQUE, bp.getUniqueness());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, bp.getExpansionSet());
        assertEquals(Rarity.V, bp.getRarity());
        assertEquals(CardSubtype.NORMAL, bp.getCardSubtype());
        assertEquals(PlayCardZoneOption.YOUR_SIDE_OF_TABLE, bp.getSinglePlayCardZoneOption());
        scn.BlueprintCardTypeCheck(bp, new ArrayList<>(List.of(CardType.EFFECT)));
        scn.BlueprintIconCheck(bp, new ArrayList<>(List.of(Icon.EFFECT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(bp, new ArrayList<>());
        assertTrue(bp.isImmuneToCardTitle(Title.Alter));
    }

    @Test(timeout = 10000)
    public void actualDownloadOnlyOffersNonUniqueVesselSitesAndIsOncePerTurn() {
        var scn = scenario();
        var effect = scn.GetDSCard("protocols");
        var launch = scn.GetDSCard("launch");
        var launch2 = scn.GetDSCard("launch2");
        var unique = scn.GetDSCard("unique");
        var ground = scn.GetDSCard("ground");
        var ship = scn.GetDSCard("destroyer");
        scn.StartGame();
        var system = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(system, ship, scn.GetDSCard("other"), scn.GetDSCard("executor"));
        scn.MoveCardsToDSHand(effect);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);
        scn.DSDeployCard(effect);
        scn.PassAllResponses();
        if (scn.LSGetDecision() != null)
            scn.LSPass();
        assertEquals(Zone.SIDE_OF_TABLE, effect.getZone());
        scn.MoveCardsToTopOfDSReserveDeck(launch, unique, ground, launch2);
        assertTrue(scn.DSCardActionAvailable(effect, "Deploy non-unique starship or vehicle site"));
        scn.DSUseCardAction(effect, "Deploy non-unique starship or vehicle site");
        scn.PassAllResponses();
        var choices = scn.DSGetDecision().getDecisionParameters();
        var ids = choices.get("blueprintId");
        var selectable = choices.get("selectable");
        int legal = 0;
        for (int i = 0; i < ids.length; i++) {
            if ("true".equals(selectable[i])) {
                assertEquals(launch.getBlueprintId(true), ids[i]);
                legal++;
            }
        }
        assertTrue("At least one legal launch bay", legal > 0);
        scn.DSChooseCard(launch);
        for (int i = 0; i < 16 && launch.getZone() != Zone.LOCATIONS && launch2.getZone() != Zone.LOCATIONS; i++) {
            var decision = scn.GetCurrentDecision();
            String prompt = decision.getText().toLowerCase();
            if (prompt.contains("optional response")) {
                if (scn.DSGetDecision() != null)
                    scn.DSPass();
                else
                    scn.LSPass();
            } else if (scn.DSHasCardChoiceAvailable(ship)) {
                scn.DSChooseCard(ship);
            } else if (scn.DSGetDecision() != null && prompt.contains("on which side")) {
                scn.ChooseOption(scn.DS, scn.DSGetMultipleChoices().getFirst());
            } else {
                fail("Unresolved site placement: " + decision.getText()
                        + " " + decision.getDecisionParameters().keySet());
            }
        }
        assertTrue("Exactly one of the identical copies was downloaded",
                (launch.getZone() == Zone.LOCATIONS) != (launch2.getZone() == Zone.LOCATIONS));
        var deployed = launch.getZone() == Zone.LOCATIONS ? launch : launch2;
        scn.PassAllResponses();
        assertNotNull(deployed.getRelatedStarshipOrVehicle());
        if (scn.LSGetDecision() != null)
            scn.LSPass();
        assertFalse(scn.DSCardActionAvailable(effect, "Deploy non-unique starship or vehicle site"));
    }

    private static boolean inUsed(com.gempukku.swccgo.game.PhysicalCard c) {
        return c.getZone() == Zone.USED_PILE || c.getZone() == Zone.TOP_OF_USED_PILE;
    }

    private static boolean inLost(com.gempukku.swccgo.game.PhysicalCard c) {
        return c.getZone() == Zone.LOST_PILE || c.getZone() == Zone.TOP_OF_LOST_PILE;
    }

    /** Destroyer and Victory at the DS system with a Corvette; Launch Bay related to the Destroyer holds two DS
     * characters and Luke. */
    private VirtualTableScenario spaceSetup(boolean effectOnTable) {
        var scn = scenario();
        var launch = scn.GetDSCard("launch");
        var ship = scn.GetDSCard("destroyer");
        var corvette = scn.GetLSCard("corvette");
        scn.StartGame();
        var system = scn.GetDSStartingLocation();
        scn.MoveCardsToLocation(system, ship, scn.GetDSCard("other"), corvette);
        scn.BoardAsPilot(corvette, scn.GetLSCard("han"));
        var placement = scn.gameState().getLocationPlacement(scn.game(), launch, null, null).stream()
                .filter(p -> p.getParentStarshipOrVehicleCard() == ship).findFirst().orElseThrow();
        scn.MoveLocationToTable(launch, placement);
        assertSame(ship, launch.getRelatedStarshipOrVehicle());
        scn.MoveCardsToLocation(launch, scn.GetDSCard("trooper"), scn.GetDSCard("officer"),
                scn.GetLSCard("luke"));
        if (effectOnTable)
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard("protocols"));
        return scn;
    }

    private int dsTotalPowerInSpaceBattle(boolean effectOnTable) {
        var scn = spaceSetup(effectOnTable);
        var ship = scn.GetDSCard("destroyer");
        var shipPower = scn.GetPower(ship);
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());
        assertEquals("Vessel power itself is not modified", shipPower, scn.GetPower(ship), scn.epsilon);
        scn.SkipToEndOfPowerSegment(false);
        return scn.GetDSTotalPower();
    }

    @Test(timeout = 10000)
    public void ownCharactersAtRelatedStarshipSiteEachAddTwoToTotalPower() {
        int without = dsTotalPowerInSpaceBattle(false);
        int with = dsTotalPowerInSpaceBattle(true);
        // Two DS characters at the related Launch Bay; Luke (opponent's) does not count.
        assertEquals(without + 4, with);
    }

    @Test(timeout = 10000)
    public void actualVehicleBattleAddsTwoToTotalPowerForCharacterAtRelatedVehicleSite() {
        int without = vehicleBattleTotalPower(false);
        int with = vehicleBattleTotalPower(true);
        assertEquals(without + 2, with);
    }

    private int vehicleBattleTotalPower(boolean effectOnTable) {
        var scn = new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("protocols", "1261_34", "walker", "3_157",
                        "platform", "1220_67", "trooper", "1_194", "veers", "3_87")),
                18, 18, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
        scn.StartGame();
        var ground = scn.GetDSStartingLocation();
        var walker = scn.GetDSCard("walker");
        var platform = scn.GetDSCard("platform");
        scn.MoveCardsToLocation(ground, walker, scn.GetLSCard("luke"));
        scn.BoardAsPilot(walker, scn.GetDSCard("veers"));
        var placement = scn.gameState().getLocationPlacement(scn.game(), platform, null, null).stream()
                .filter(p -> p.getParentStarshipOrVehicleCard() == walker).findFirst().orElseThrow();
        scn.MoveLocationToTable(platform, placement);
        assertSame(walker, platform.getRelatedStarshipOrVehicle());
        scn.MoveCardsToLocation(platform, scn.GetDSCard("trooper"));
        if (effectOnTable)
            scn.MoveCardsToDSSideOfTable(scn.GetDSCard("protocols"));
        var walkerPower = scn.GetPower(walker);
        scn.DSStartBattleAndSkipToWeaponsSegment(ground);
        assertTrue(scn.IsActiveBattle());
        assertEquals(walkerPower, scn.GetPower(walker), scn.epsilon);
        scn.SkipToEndOfPowerSegment(false);
        return scn.GetDSTotalPower();
    }

    @Test(timeout = 10000)
    public void characterAtRelatedSiteMayBeForfeitedToReduceBattleDamage() {
        var scn = spaceSetup(true);
        var effect = scn.GetDSCard("protocols");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var corvette = scn.GetLSCard("corvette");
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.PowerModifier(corvette, corvette, 40));
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());
        scn.SkipToDamageSegment(false);
        scn.PassDamageSegmentActions();
        int damage = scn.GetUnpaidDSBattleDamage();
        assertTrue("DS lost the battle", damage > 2);
        assertTrue(scn.DSCardActionAvailable(effect, "Forfeit character at related site"));
        scn.DSUseCardAction(effect, "Forfeit character at related site");
        assertTrue(scn.DSHasCardChoiceAvailable(trooper));
        assertFalse("Opponent's character is not offered", scn.DSHasCardChoiceAvailable(luke));
        scn.DSChooseCard(trooper);
        scn.PassAllResponses();
        assertTrue(inLost(trooper));
        assertEquals("Stormtrooper forfeit 2 reduces battle damage", damage - 2, scn.GetUnpaidDSBattleDamage());
    }

    @Test(timeout = 10000)
    public void forfeitOptionIsNotOfferedWithoutBattleDamageOrAttrition() {
        var scn = spaceSetup(true);
        var effect = scn.GetDSCard("protocols");
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());
        scn.SkipToDamageSegment(false);
        assertEquals(0, scn.GetUnpaidDSBattleDamage());
        assertEquals(0, scn.GetUnpaidDSAttrition());
        assertTrue(scn.DSGetDecision() == null || scn.GetDSAvailableActions() == null
                || !scn.GetDSAvailableActions().toString().contains("Forfeit character at related site"));
    }

    /** Answers pending decisions (picking the first card for pile-order choices, passing otherwise) until done. */
    private static void resolveUntil(VirtualTableScenario scn, java.util.function.BooleanSupplier done) {
        for (int i = 0; i < 40 && !done.getAsBoolean(); i++) {
            var decision = scn.GetCurrentDecision();
            if (decision == null)
                break;
            String player = scn.DSGetDecision() != null ? scn.DS : scn.LS;
            var ids = decision.getDecisionParameters().get("cardId");
            var min = decision.getDecisionParameters().get("min");
            if (ids != null && ids.length > 0 && min != null && !"0".equals(min[0])) {
                scn.PlayerDecided(player, ids[0]);
            } else if (player.equals(scn.DS)) {
                scn.DSPass();
            } else {
                scn.LSPass();
            }
        }
        if (!done.getAsBoolean()) {
            var decision = scn.GetCurrentDecision();
            fail("Unresolved: " + (decision == null ? "no decision" : decision.getText()));
        }
    }

    private void loseDestroyer(VirtualTableScenario scn) {
        var ship = scn.GetDSCard("destroyer");
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSExecuteAdHocEffect(ship, new com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect(
                new com.gempukku.swccgo.logic.actions.TopLevelGameTextAction(ship, ship.getCardId()), ship));
        resolveUntil(scn, () -> inLost(ship) && scn.GetDSCard("launch").getZone() != Zone.LOCATIONS
                && scn.GetDSCard("launch").getZone() != Zone.VOID);
    }

    @Test(timeout = 10000)
    public void starshipSiteLeavingTablePlacesItAndAllCardsThereInOwnersUsedPile() {
        var scn = spaceSetup(true);
        loseDestroyer(scn);
        assertTrue(inUsed(scn.GetDSCard("launch")));
        assertTrue(inUsed(scn.GetDSCard("trooper")));
        assertTrue(inUsed(scn.GetDSCard("officer")));
        var luke = scn.GetLSCard("luke");
        assertTrue("Opponent's card there goes to its owner's Used Pile", inUsed(luke));
        assertTrue(scn.gameState().getUsedPile(scn.LS).contains(luke));
    }

    @Test(timeout = 10000)
    public void withoutTheEffectStarshipSiteAndCardsThereAreLost() {
        var scn = spaceSetup(false);
        loseDestroyer(scn);
        assertTrue(inLost(scn.GetDSCard("launch")));
        assertTrue(inLost(scn.GetDSCard("trooper")));
        assertTrue(inLost(scn.GetLSCard("luke")));
    }

    @Test(timeout = 10000)
    public void characterLostFromRelatedSiteThatStaysOnTableStillGoesToLostPile() {
        var scn = spaceSetup(true);
        var trooper = scn.GetDSCard("trooper");
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSExecuteAdHocEffect(trooper, new com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect(
                new com.gempukku.swccgo.logic.actions.TopLevelGameTextAction(trooper, trooper.getCardId()), trooper));
        scn.PassAllResponses();
        assertTrue(inLost(trooper));
        assertEquals(Zone.LOCATIONS, scn.GetDSCard("launch").getZone());
    }

    @Test(timeout = 10000)
    public void forfeitedStarshipTakesItsSiteAndCardsThereToUsedPile() {
        var scn = spaceSetup(true);
        var ship = scn.GetDSCard("destroyer");
        var corvette = scn.GetLSCard("corvette");
        scn.ApplyAdHocModifier(new com.gempukku.swccgo.logic.modifiers.PowerModifier(corvette, corvette, 40));
        scn.DSStartBattleAndSkipToWeaponsSegment(scn.GetDSStartingLocation());
        scn.SkipToDamageSegment(false);
        scn.PassDamageSegmentActions();
        // Decline the optional forfeit of a character at the related site first.
        if (!scn.AwaitingDSBattleDamagePayment() && scn.DSGetDecision() != null)
            scn.DSPass();
        assertTrue(scn.AwaitingDSBattleDamagePayment());
        scn.DSChooseCard(ship);
        resolveUntil(scn, () -> inLost(ship) && scn.GetDSCard("launch").getZone() != Zone.LOCATIONS
                && scn.GetDSCard("launch").getZone() != Zone.VOID);
        assertTrue(inUsed(scn.GetDSCard("launch")));
        assertTrue(inUsed(scn.GetDSCard("trooper")));
        assertTrue(inUsed(scn.GetLSCard("luke")));
    }

    @org.junit.Test
    public void CurrentTextIsTheLatestVersion() {
        // PC card list (PC-E2, by 2012-07-21) rewrite is the latest text (Bill option b); restored from before PR #732.
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(new java.util.HashMap<>(), new java.util.HashMap<>(java.util.Map.of("c", "1261_34")), 10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation, com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts, com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields, com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var text = scn.GetDSCard("c").getBlueprint().getGameText();
        org.junit.Assert.assertEquals("Deploy on table. Once per turn, may deploy a non-unique starship (or vehicle) site from Reserve Deck; reshuffle. During battle where you have a starship or vehicle, characters at related starship (or vehicle) sites cumulatively add 2 to your total power and may be forfeited to reduce battle damage or attrition. When a starship (or vehicle) site leaves table, place it and all cards there in owner's Used Pile. (Immune to Alter.)", text);
        org.junit.Assert.assertFalse(text.contains("for each of your characters at its related sites"));
        org.junit.Assert.assertNull(scn.GetDSCard("c").getBlueprint().getLegacyVersion());
    }
}
