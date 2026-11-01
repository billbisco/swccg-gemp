package com.gempukku.swccgo.cards.set1113.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.gempukku.swccgo.common.Zone;

public class Card_1113_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("platform", "1113_14");
                    put("blizzard", "1220_80");
                    put("veers", "3_87");
                    put("walker", "104_5");
                    put("gear", "8_119");
                    put("rack", "1_211");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ATATDeploymentPlatformStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("platform").getBlueprint();

        assertEquals("AT-AT Deployment Platform", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.VEHICLE_SITE);
            add(Icon.SCOMP_LINK);
            add(Icon.DAGOBAH);
            add(Icon.DARK_FORCE);
}});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(0, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_13, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ATATDeploymentPlatformDeploysBikerScoutGearHereFromReserveDeck() {
        var scn = GetScenario();
        var platform = scn.GetDSCard("platform");
        var blizzard = scn.GetDSCard("blizzard");
        var veers = scn.GetDSCard("veers");
        var gear = scn.GetDSCard("gear");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard);
        scn.BoardAsPilot(blizzard, veers);
        scn.MoveCardsToHand(platform);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(platform);
        scn.PassAllResponses();
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(gear);

        assertTrue(scn.DSCardActionAvailable(platform, "Deploy Biker Scout Gear from Reserve Deck"));
        scn.DSUseCardAction(platform, "Deploy Biker Scout Gear from Reserve Deck");
        if (scn.DSHasCardChoiceAvailable(gear)) {
            scn.DSChooseCard(gear);
        }
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, gear);
    }

    @Test
    public void ATATDeploymentPlatformWhileBikerScoutGearHereDeploysANamedCardFromReserveDeck() {
        var scn = GetScenario();
        var platform = scn.GetDSCard("platform");
        var blizzard = scn.GetDSCard("blizzard");
        var veers = scn.GetDSCard("veers");
        var gear = scn.GetDSCard("gear");
        var rack = scn.GetDSCard("rack");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard);
        scn.BoardAsPilot(blizzard, veers);
        scn.MoveCardsToHand(platform);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(platform);
        scn.PassAllResponses();
        scn.MoveCardsToLocation(platform, gear);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(rack);

        assertTrue(scn.DSCardActionAvailable(platform, "Deploy a Biker Scout Gear card from Reserve Deck for free"));
        scn.DSUseCardAction(platform, "Deploy a Biker Scout Gear card from Reserve Deck for free");
        if (scn.DSHasCardChoiceAvailable(rack)) {
            scn.DSChooseCard(rack);
        }
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, rack);
    }

    @Test
    public void ATATDeploymentPlatformDoesNotOfferNamedCardSearchWithoutBikerScoutGearHere() {
        var scn = GetScenario();
        var platform = scn.GetDSCard("platform");
        var blizzard = scn.GetDSCard("blizzard");
        var veers = scn.GetDSCard("veers");
        var rack = scn.GetDSCard("rack");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard);
        scn.BoardAsPilot(blizzard, veers);
        scn.MoveCardsToHand(platform);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(platform);
        scn.PassAllResponses();
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(rack);

        assertFalse(scn.DSCardActionAvailable(platform, "Deploy a Biker Scout Gear card from Reserve Deck for free"));
    }

    @Test
    public void ATATDeploymentPlatformUniqueATATImmunityIsPlusOneUnlessLightOccupies() {
        var scn = GetScenario();
        var platform = scn.GetDSCard("platform");
        var blizzard = scn.GetDSCard("blizzard");
        var veers = scn.GetDSCard("veers");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard);
        scn.BoardAsPilot(blizzard, veers);
        scn.MoveCardsToHand(platform);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(platform);
        scn.PassAllResponses();

        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), blizzard), scn.epsilon);

        scn.MoveCardsToLocation(platform, luke);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), blizzard), scn.epsilon);
    }

    @Test
    public void ATATDeploymentPlatformNonUniqueATATIsImmuneToAttritionUnlessLightOccupies() {
        var scn = GetScenario();
        var platform = scn.GetDSCard("platform");
        var walker = scn.GetDSCard("walker");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, walker);
        scn.MoveCardsToHand(platform);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(platform);
        scn.PassAllResponses();

        assertEquals(Float.MAX_VALUE, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), walker), scn.epsilon);

        scn.MoveCardsToLocation(platform, luke);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), walker), scn.epsilon);
    }
}
