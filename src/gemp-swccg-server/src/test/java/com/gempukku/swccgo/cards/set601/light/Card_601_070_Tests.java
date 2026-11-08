package com.gempukku.swccgo.cards.set601.light;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Yavin Sentry (V) (Legacy shields, 1200_14 / 601_70); text per the Virtual Cards Master List (2014) and card image. */
public class Card_601_070_Tests {

    private VirtualTableScenario scenario(boolean episodeOneSaber) {
        var light = new HashMap<String, String>();
        light.put("sentry", "601_70");
        light.put("obi", episodeOneSaber ? "13_33" : "1_21");
        light.put("saber", episodeOneSaber ? "13_34" : "1_157");
        var dark = new HashMap<String, String>();
        dark.put("vader", "1_168");
        return new VirtualTableScenario(light, dark, 30, 30,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void YavinSentryVTextIsTheMasterListText() {
        var b = scenario(true).GetLSCard("sentry").getBlueprint();
        assertEquals("Yavin Sentry", b.getTitle());
        assertTrue(b.hasVirtualSuffix());
        assertEquals(Side.LIGHT, b.getSide());
        assertEquals("Plays on table. Colo Claw Fish is canceled. Opponent must first use X Force to deploy a non-unique card (except a Jawa or Tusken Raider) to a location, where X = the number of copies of that card at that location. [Episode I] lightsaber weapon destiny draws cannot be increased above their printed value.", b.getGameText());
    }

    /**
     * Obi-Wan swings his lightsaber at Vader (defense value 6) with two weapon destiny draws of 1, while an ad hoc
     * modifier makes each of those weapon destiny draws +3.
     * @return whether Vader was hit
     */
    private boolean saberHitsVaderWithBoostedDraws(boolean sentryOnTable, boolean episodeOneSaber) {
        var scn = scenario(episodeOneSaber);
        var obi = scn.GetLSCard("obi");
        var saber = scn.GetLSCard("saber");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        if (sentryOnTable)
            scn.MoveCardsToLSSideOfTable(scn.GetLSCard("sentry"));
        scn.MoveCardsToLocation(site, obi, vader);
        scn.AttachCardsTo(obi, saber);
        scn.ApplyAdHocModifier(new EachWeaponDestinyModifier(saber, Filters.sameCardId(saber), 3));
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(6);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.LSCardActionAvailable(saber, "Fire"));
        scn.PrepareLSDestiny(1);
        scn.PrepareLSDestiny(1);
        scn.LSUseCardAction(saber, "Fire");
        if (scn.LSHasCardChoiceAvailable(vader))
            scn.LSChooseCard(vader);
        scn.PassAllResponses();
        scn.PassWeaponFireWithDestinyDraw(2);
        scn.PassAllResponses();
        return vader.isHit();
    }

    @Test
    public void YavinSentryVKeepsEpisodeOneLightsaberDrawsAtPrintedValue() {
        // Draws stay 1 + 1 (+1 for targeting a Dark Jedi) = 3, not > 6.
        assertFalse(saberHitsVaderWithBoostedDraws(true, true));
    }

    @Test
    public void WithoutYavinSentryVEpisodeOneLightsaberDrawsAreIncreased() {
        // (1+3) + (1+3) (+1) = 9 > 6.
        assertTrue(saberHitsVaderWithBoostedDraws(false, true));
    }

    @Test
    public void YavinSentryVDoesNotLimitANonEpisodeOneLightsaber() {
        // Premiere Obi-Wan's Lightsaber has no [Episode I] icon: (1+3) + (1+3) = 8 > 6.
        assertTrue(saberHitsVaderWithBoostedDraws(true, false));
    }
}
