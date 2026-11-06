package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

public class Card_1250_086_Tests {
    private VirtualTableScenario scenario() {
        return scenario(false);
    }

    private VirtualTableScenario scenario(boolean specialEditionWookiee) {
        return new VirtualTableScenario(new HashMap<>() {{
            put("bowcaster", "1250_86"); put("wookiee", specialEditionWookiee ? "7_50" : "8_2"); put("rebel", "1_28");
            put("forest", "1116_16"); put("haven", "1116_17");
            put("guard1", "1_28"); put("guard2", "1_28");
        }}, new HashMap<>() {{
            put("vader", "1_168"); put("snoova", "10_48"); put("scout", "8_171");
            put("trooper", "1_194");
        }}, 10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void BowcasterMetadataMatchesPrintedSource() {
        var scn = scenario();
        var card = scn.GetLSCard("bowcaster").getBlueprint();
        assertEquals("Bowcaster", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.WEAPON); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON); add(Icon.A_NEW_HOPE); add(Icon.EPISODE_I); add(Icon.LEGACY_BLOCK_5);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.BOWCASTER); }});
    }

    @Test
    public void BowcasterDeploysOnYourWookiee() {
        var scn = scenario();
        var wookiee = scn.GetLSCard("wookiee");
        var weapon = scn.GetLSCard("bowcaster");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), wookiee);
        scn.MoveCardsToHand(weapon);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(weapon);
        scn.LSChooseCard(wookiee);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(wookiee, weapon));
    }

    @Test
    public void BowcasterCannotDeployOnNonWookieeOrOpponentsWookiee() {
        var scn = scenario();
        var weapon = scn.GetLSCard("bowcaster");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("rebel"), scn.GetDSCard("snoova"));
        scn.MoveCardsToHand(weapon);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(weapon));
    }

    @Test
    public void BowcasterMissesCharacterWhenDestinyPlusOneControlledSiteEqualsDefense() {
        checkShot(false, 1, false, 5, false);
    }

    @Test
    public void BowcasterHitsCharacterWithTwoControlledSitesAndMayTakeDestinyIntoHand() {
        checkShot(false, 2, false, 5, true);
    }

    @Test
    public void BowcasterContestedKashyyykSiteDoesNotAddToWeaponDestiny() {
        checkShot(false, 2, true, 5, false);
    }

    @Test
    public void BowcasterNoControlledKashyyykSitesStillAllowsNaturalDestinyHit() {
        checkShot(false, 0, false, 7, false);
    }

    @Test
    public void BowcasterMissesVehicleWhenDestinyEqualsArmor() {
        checkShot(true, 0, false, 4, false);
    }

    @Test
    public void BowcasterHitsVehicleWhenDestinyExceedsArmor() {
        checkShot(true, 0, false, 5, false);
    }

    @Test
    public void BowcasterSpecialEditionWookieeResetsPrintedXToOne() {
        checkShot(false, 2, false, 5, false, true);
    }

    private String prompt(VirtualTableScenario scn, boolean dark) {
        var decision = dark ? scn.DSGetDecision() : scn.LSGetDecision();
        return decision == null ? "none" : decision.getText();
    }

    private void checkShot(boolean vehicle, int controlledSites, boolean contestSecondSite,
            int destiny, boolean takeIntoHand) {
        checkShot(vehicle, controlledSites, contestSecondSite, destiny, takeIntoHand, false);
    }

    private void checkShot(boolean vehicle, int controlledSites, boolean contestSecondSite,
            int destiny, boolean takeIntoHand, boolean specialEditionWookiee) {
        var scn = scenario(specialEditionWookiee);
        var weapon = scn.GetLSCard("bowcaster");
        var site = scn.GetLSStartingLocation();
        var target = scn.GetDSCard(vehicle ? "scout" : "vader");
        scn.StartGame();
        scn.MoveCardsToLocation(site, scn.GetLSCard("wookiee"), target);
        scn.AttachCardsTo(scn.GetLSCard("wookiee"), weapon);
        if (controlledSites > 0) {
            scn.MoveLocationToTable(scn.GetLSCard("forest"));
            scn.MoveCardsToLocation(scn.GetLSCard("forest"), scn.GetLSCard("guard1"));
        }
        if (controlledSites > 1) {
            scn.MoveLocationToTable(scn.GetLSCard("haven"));
            scn.MoveCardsToLocation(scn.GetLSCard("haven"), scn.GetLSCard("guard2"));
            if (contestSecondSite) {
                scn.MoveCardsToLocation(scn.GetLSCard("haven"), scn.GetDSCard("trooper"));
            }
        }
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(destiny);
        var destinyCard = scn.GetLSDestiny(destiny);
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(weapon, "Fire");
        scn.LSChooseCard(target);
        if (takeIntoHand) {
            scn.PassResponses("Fire ");
            scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
            scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
            if (scn.DSDecisionAvailable("DESTINY_DRAWN")) {
                scn.DSPass();
            }
            assertTrue("Expected destiny response; LS=" + prompt(scn, false) + "; DS=" + prompt(scn, true),
                    scn.LSCardActionAvailable(weapon, "Take destiny card into hand"));
            scn.LSUseCardAction(weapon, "Take destiny card into hand");
            scn.PassAllResponses();
            assertTrue(scn.GetLSHand().contains(destinyCard));
        }
        else {
            scn.PassWeaponFireWithDestinyDraw();
            assertFalse(scn.GetLSHand().contains(destinyCard));
            scn.PassAllResponses();
            assertTrue("Expected resolved destiny in Used Pile; LS=" + prompt(scn, false) + "; DS=" + prompt(scn, true),
                    scn.GetLSUsedPile().contains(destinyCard));
        }
        scn.PassAllResponses();
        int bonus = specialEditionWookiee ? 1 : controlledSites - (contestSecondSite ? 1 : 0);
        assertEquals(destiny + bonus > (vehicle ? 4 : 6), target.isHit());
        assertEquals(force, scn.GetLSForcePileCount());
    }
}
