package com.gempukku.swccgo.cards.set2250.dark;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.cards.LegacyVersionAssertions.assertLegacyVersion;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Pre-errata Mandalorian Armor (V) (Dec 2009 sheet), a version of 1250_15. */
public class Card_2250_015_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{ put("luke", "1_19"); put("rifle", "1_153"); }},
                new HashMap<>() {{ put("armor", "2250_15"); put("current", "1250_15"); put("alien", "1_172"); put("jango", "201_25"); put("mandalorian", "1115_66"); put("trooper", "1_194"); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void MandalorianArmorOriginalIdentity() {
        var scn = scenario();
        assertLegacyVersion(scn.GetDSCard("armor").getBlueprint(), scn.GetDSCard("current").getBlueprint(), "1250_15", "",
                LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27),
                "Deploy on your alien of ability < 4. Character gains bounty hunter skill and immunity to attrition < 4. If character just hit, may place this device in Lost Pile (Used Pile if on The Mandalorian) to restore it to normal.");
    }

    private void restore(String wearerName, Zone pile) {
        var scn = scenario();
        var site = scn.GetDSStartingLocation();
        var wearer = scn.GetDSCard(wearerName);
        var armor = scn.GetDSCard("armor");
        scn.StartGame();
        scn.MoveCardsToLocation(site, wearer, scn.GetDSCard("trooper"), scn.GetLSCard("luke"));
        scn.AttachCardsTo(wearer, armor);
        scn.AttachCardsTo(scn.GetLSCard("luke"), scn.GetLSCard("rifle"));
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(7);
        scn.LSUseCardAction(scn.GetLSCard("rifle"), "Fire");
        scn.LSChooseCard(wearer);
        scn.PassForceUseResponses();
        scn.PassResponses("Fire ");
        scn.PassDestinyDrawResponses();
        scn.PassResponses("ABOUT_TO_BE_HIT");
        assertTrue(wearer.isHit());
        assertTrue(scn.DSCardActionAvailable(armor, "Restore wearer to normal"));
        scn.DSUseCardAction(armor, "Restore wearer to normal");
        scn.PassAllResponses();
        assertFalse(wearer.isHit());
        assertInZone(pile, armor);
    }

    @Test
    public void MandalorianArmorOriginalGoesToUsedPileOnTheMandalorian() {
        restore("mandalorian", Zone.USED_PILE);
    }

    @Test
    public void MandalorianArmorOriginalGoesToLostPileOnJango() {
        restore("jango", Zone.LOST_PILE);
    }

    @Test
    public void MandalorianArmorOriginalGoesToLostPileOnOtherAlien() {
        restore("alien", Zone.LOST_PILE);
    }

    @Test
    public void MandalorianArmorOriginalIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : java.util.List.of(true, false)) {
            for (boolean otherOut : java.util.List.of(false, true)) {
                var scn = scenario();
                var onTable = scn.GetDSCard(currentOnTable ? "current" : "armor");
                var inHand = scn.GetDSCard(currentOnTable ? "armor" : "current");
                var alien = scn.GetDSCard("alien");
                scn.StartGame();
                scn.MoveCardsToLocation(scn.GetDSStartingLocation(), alien, scn.GetDSCard("mandalorian"));
                if (otherOut)
                    scn.AttachCardsTo(alien, onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToDSTurn(Phase.DEPLOY);
                scn.EnsureDSForcePile(10);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSCardPlayAvailable(inHand));
            }
        }
    }
}
