package com.gempukku.swccgo.cards.set2291.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_2291_058_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("petranaki", "2291_58");
                    put("current", "1291_58");
                    put("amidala", "12_22");
                    put("sabe", "12_25");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("dspetranaki", "1291_17");
                    put("vader", "1_168");
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
    public void GeonosisPetranakiArenaLightStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("petranaki").getBlueprint();

        assertEquals("Geonosis: Petranaki Arena", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    /**
     * Light deploys the given character to the given site, asserting the amount of Force used.
     */
    private void lightDeploysUsing(VirtualTableScenario scn, PhysicalCardImpl character, PhysicalCardImpl site, int forceUsed) {
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(character));
        int forceBefore = scn.GetLSForcePileCount();
        scn.LSDeployCard(character);
        assertTrue(scn.LSDecisionAvailable("Choose where to deploy"));
        scn.LSChooseCard(site);
        assertTrue(scn.DSDecisionAvailable("Use " + forceUsed + " Force - Optional responses"));
        scn.PassAllResponses();
        assertAtLocation(site, character);
        assertEquals(forceBefore - forceUsed, scn.GetLSForcePileCount());
    }

    @Test
    public void GeonosisPetranakiArenaLightAmidalaDeploysMinus1Here() {
        var scn = GetScenario();
        var petranaki = scn.GetLSCard("petranaki");
        var amidala = scn.GetLSCard("amidala");

        scn.StartGame();
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToLSHand(amidala);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(3, amidala.getBlueprint().getDeployCost(), scn.epsilon);

        lightDeploysUsing(scn, amidala, petranaki, 2);
    }

    @Test
    public void GeonosisPetranakiArenaLightAmidalaPaysFullCostElsewhere() {
        var scn = GetScenario();
        var petranaki = scn.GetLSCard("petranaki");
        var amidala = scn.GetLSCard("amidala");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToLSHand(amidala);
        scn.SkipToLSTurn(Phase.DEPLOY);

        lightDeploysUsing(scn, amidala, site, 3);
    }

    @Test
    public void GeonosisPetranakiArenaLightOtherCharactersPayFullCostHere() {
        var scn = GetScenario();
        var petranaki = scn.GetLSCard("petranaki");
        var sabe = scn.GetLSCard("sabe");

        scn.StartGame();
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToLSHand(sabe);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertEquals(2, sabe.getBlueprint().getDeployCost(), scn.epsilon);

        lightDeploysUsing(scn, sabe, petranaki, 2);
    }

    /**
     * Dark puts its Petranaki Arena on table and Light deploys its copy on top (converting the Dark one).
     */
    private void convertDarkPetranakiWithLightCopy(VirtualTableScenario scn) {
        var petranaki = scn.GetLSCard("petranaki");
        var dsPetranaki = scn.GetDSCard("dspetranaki");

        scn.MoveLocationToTable(dsPetranaki);
        assertEquals(Zone.LOCATIONS, dsPetranaki.getZone());
        scn.MoveCardsToLSHand(petranaki);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(petranaki));
        scn.LSDeployCard(petranaki);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, petranaki.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, dsPetranaki.getZone());
    }

    @Test
    public void GeonosisPetranakiArenaLightDarkSideMayRaiseConvertedPetranakiArenaWhenControlling() {
        var scn = GetScenario();
        var petranaki = scn.GetLSCard("petranaki");
        var dsPetranaki = scn.GetDSCard("dspetranaki");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        convertDarkPetranakiWithLightCopy(scn);
        scn.MoveCardsToLocation(petranaki, vader);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());

        assertTrue(scn.DSCardActionAvailable(petranaki, "Raise converted Petranaki Arena"));
        scn.DSUseCardAction(petranaki, "Raise converted Petranaki Arena");
        assertTrue(scn.DSHasCardChoiceAvailable(petranaki));
        scn.DSChooseCard(petranaki);
        scn.PassAllResponses();

        assertEquals(Zone.LOCATIONS, dsPetranaki.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, petranaki.getZone());
    }

    @Test
    public void GeonosisPetranakiArenaLightDarkSideMayNotRaiseConvertedPetranakiArenaWhenNotControlling() {
        var scn = GetScenario();
        var petranaki = scn.GetLSCard("petranaki");
        var dsPetranaki = scn.GetDSCard("dspetranaki");
        var vader = scn.GetDSCard("vader");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        convertDarkPetranakiWithLightCopy(scn);
        scn.MoveCardsToLocation(petranaki, vader, luke);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());

        assertFalse(scn.DSCardActionAvailable(petranaki, "Raise converted Petranaki Arena"));
        assertEquals(Zone.LOCATIONS, petranaki.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, dsPetranaki.getZone());
    }

    @Test
    public void OriginalPetranakiArenaIdentityAndText() {
        var scn = GetScenario();
        var card = scn.GetLSCard("petranaki").getBlueprint();

        assertEquals("Geonosis: Petranaki Arena", card.getTitle());
        assertTrue(card.isLegacy());
        assertEquals("Light: Amidala, Anakin, and Obi-Wan deploy -1 here. Dark: Once per turn, if you control, may raise your converted Petranaki Arena to the top.",
                card.getGameText());
        var version = card.getLegacyVersion();
        org.junit.Assert.assertNotNull(version);
        assertEquals("1291_58", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(java.time.LocalDate.of(2014, 1, 10), version.printedOn());
        assertEquals(java.time.LocalDate.of(2014, 2, 4), version.supersededOn());
        assertEquals("Geonosis: Petranaki Arena", com.gempukku.swccgo.logic.GameUtils.getFullName(card));
    }

    /**
     * Dark Side raises its converted Petranaki Arena with the given Light Side copy's Dark Side text during its control
     * phase. Returns whether that copy's raise is still unused this turn afterwards.
     */
    private boolean raiseStillUnusedAfterRaising(VirtualTableScenario scn, String lightCopy) {
        var petranaki = scn.GetLSCard(lightCopy);
        var dsPetranaki = scn.GetDSCard("dspetranaki");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(dsPetranaki);
        scn.MoveCardsToLSHand(petranaki);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(petranaki);
        scn.PassAllResponses();
        assertEquals(Zone.CONVERTED_LOCATIONS, dsPetranaki.getZone());
        scn.MoveCardsToLocation(petranaki, vader);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSUseCardAction(petranaki, "Raise converted Petranaki Arena");
        scn.DSChooseCard(petranaki);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, dsPetranaki.getZone());
        assertEquals(Zone.CONVERTED_LOCATIONS, petranaki.getZone());
        // The usage is recorded against the card the game hands the action as its source (here the Dark Side copy
        // beneath), so check both copies.
        return com.gempukku.swccgo.cards.GameConditions.isOncePerTurn(scn.game(), petranaki, scn.DS, petranaki.getCardId())
                && com.gempukku.swccgo.cards.GameConditions.isOncePerTurn(scn.game(), dsPetranaki, scn.DS, dsPetranaki.getCardId());
    }

    @Test
    public void OriginalPetranakiArenaDarkSideRaiseIsOncePerTurn() {
        assertFalse(raiseStillUnusedAfterRaising(GetScenario(), "petranaki"));
    }

    @Test
    public void CurrentPetranakiArenaDarkSideRaiseHasNoPerTurnLimit() {
        assertTrue(raiseStillUnusedAfterRaising(GetScenario(), "current"));
    }
}
