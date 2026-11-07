package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_050_Tests {
    private static final String TAKE_EFFECT = "Take card into hand from Reserve Deck";
    private static final String DEPLOY_ALIEN = "Deploy a unique alien from hand";

    private VirtualTableScenario scenario(boolean objective) {
        var dark = new HashMap<>(Map.ofEntries(
                Map.entry("spy", "1_177"), Map.entry("trooper", "1_194"),
                Map.entry("twilek", "6_160"), Map.entry("neimoidian", "12_154"),
                Map.entry("block3", "601_121"), Map.entry("monnok", "2_135"),
                Map.entry("effect", "4_134"), Map.entry("vader", "1_168"),
                Map.entry("droidAgent", "1_163"), Map.entry("yularen", "1_166"),
                Map.entry("agent", "7_170"), Map.entry("scoutAgent", "7_195"),
                Map.entry("battleground", "7_283")));
        if (objective) dark.put("office", "1240_50");
        return new VirtualTableScenario(new HashMap<>(Map.of(
                "blount", "9_21", "han", "108_1", "mess", "2_57", "revolution", "1_62")),
                dark, 10, 10, StartingSetup.DefaultLSGroundLocation,
                objective ? isbOperations() : StartingSetup.DSStartingLocation("1240_50"),
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private StartingSetup isbOperations() {
        return new StartingSetup() {
            @Override
            public HashMap<String, String> Cards() {
                return new HashMap<>(Map.of("operations", "7_299"));
            }

            @Override
            public void Setup(VirtualTableScenario scn) {
                if (scn.DSDecisionAvailable("starting")) scn.DSChooseCard(scn.GetDSCard("operations"));
                if (scn.DSDecisionAvailable("Choose Coruscant location")) scn.DSChooseCard(scn.GetDSCard("office"));
            }
        };
    }

    @Test
    public void CentralOfficeHasPrintedSiteStatsAndSeparateSideTexts() {
        var scn = scenario(false);
        var card = scn.GetDSCard("starting-location").getBlueprint();
        assertEquals("Coruscant: ISB Central Office", card.getTitle());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(CardType.LOCATION, card.getCardType());
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertFalse(card.hasVirtualSuffix());
        assertEquals("Unless Blount present, Empire's Sinister Agents may not flip. Immune to Revolution.",
                card.getLocationLightSideGameText());
        assertEquals("If your spy here, you may play non-[Virtual Block 3] Interrupts with 'advisor' in title for 1 Force.",
                card.getLocationDarkSideGameText());
        scn.BlueprintIconCheck(card, new ArrayList<>(List.of(Icon.DARK_FORCE, Icon.INTERIOR_SITE,
                Icon.PLANET, Icon.EPISODE_I, Icon.THEED_PALACE, Icon.LEGACY_BLOCK_4)));
    }

    @Test
    public void OwnSpyAtOfficeLetsOriginalTwilekAdvisorUseExactlyOneForce() {
        var scn = prepareAdvisor("own spy here", 1);
        var advisor = scn.GetDSCard("twilek");
        var effect = scn.GetDSCard("effect");
        scn.DSPlayCard(advisor, TAKE_EFFECT);
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(effect);
        scn.PassAllResponses();
        assertEquals(0, scn.GetDSForcePileCount());
        assertInHand(effect);
        assertInZone(Zone.USED_PILE, advisor);
    }

    @Test
    public void OwnSpyAtOfficeCannotPlayAdvisorForZeroForce() {
        var scn = prepareAdvisor("own spy here", 0);
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("twilek"), TAKE_EFFECT));
    }

    @Test
    public void OwnSpyElsewhereOrAnOpposingSpyDoesNotLowerAdvisorCost() {
        for (String spy : List.of("own spy elsewhere", "opposing spy here", "no spy")) {
            var scn = prepareAdvisor(spy, 2);
            assertFalse(spy, scn.DSCardPlayAvailable(scn.GetDSCard("twilek"), TAKE_EFFECT));
        }
        var scn = prepareAdvisor("no spy", 3);
        var advisor = scn.GetDSCard("twilek");
        scn.DSPlayCard(advisor, TAKE_EFFECT);
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(scn.GetDSCard("effect"));
        scn.PassAllResponses();
        assertEquals(0, scn.GetDSForcePileCount());
        assertInHand(scn.GetDSCard("effect"));
    }

    @Test
    public void BlockThreeAdvisorKeepsItsNativeThreeForceCost() {
        var scn = prepareAdvisor("own spy here", 1);
        assertFalse(scn.DSCardPlayAvailable(scn.GetDSCard("block3"), DEPLOY_ALIEN));
        scn = prepareAdvisor("own spy here", 3);
        var legacyAdvisor = scn.GetDSCard("block3");
        var chyler = scn.GetDSCard("agent");
        scn.DSPlayCard(legacyAdvisor, DEPLOY_ALIEN);
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(chyler);
        scn.PassAllResponses();
        scn.DSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertTrue(scn.CardsAtLocation(scn.GetDSStartingLocation(), chyler));
        assertEquals(0, scn.GetDSForcePileCount());
    }

    @Test
    public void DifferentOriginalAdvisorAlsoUsesOneForceButMonnokIsUnaffected() {
        var scn = prepareAdvisor("own spy here", 1);
        assertFalse(scn.DSPlayLostInterruptAvailable(scn.GetDSCard("monnok")));
        var neimoidian = scn.GetDSCard("neimoidian");
        var location = scn.GetDSCard("battleground");
        scn.DSPlayCard(neimoidian, "Deploy unique battleground from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(location);
        scn.PassAllResponses();
        if (scn.DSDecisionAvailable("On which side")) scn.DSChoose("Left");
        scn.PassAllResponses();
        assertInZone(Zone.LOCATIONS, location);
        assertEquals(0, scn.GetDSForcePileCount());
    }

    private VirtualTableScenario prepareAdvisor(String spy, int force) {
        var scn = scenario(false);
        scn.StartGame();
        var office = scn.GetDSStartingLocation();
        if (spy.equals("own spy here")) scn.MoveCardsToLocation(office, scn.GetDSCard("spy"));
        if (spy.equals("own spy elsewhere")) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("spy"));
        if (spy.equals("opposing spy here")) scn.MoveCardsToLocation(office, scn.GetLSCard("blount"));
        if (spy.equals("no spy")) scn.MoveCardsToLocation(office, scn.GetDSCard("trooper"));
        scn.MoveCardsToLSHand(scn.GetLSCard("han"));
        scn.MoveCardsToDSHand(scn.GetDSCard("twilek"), scn.GetDSCard("neimoidian"),
                scn.GetDSCard("block3"), scn.GetDSCard("monnok"), scn.GetDSCard("agent"));
        for (String key : List.of("effect", "battleground")) {
            var card = scn.GetDSCard(key);
            scn.MoveOutOfPlay(card);
            scn.MoveCardsToTopOfDSReserveDeck(card);
        }
        scn.SkipToDSTurn(Phase.CONTROL);
        while (scn.GetDSForcePileCount() > force) scn.MoveCardsToTopOfDSUsedPile(scn.GetTopOfDSForcePile());
        while (scn.GetDSForcePileCount() < force) scn.MoveCardsToTopOfDSForcePile(scn.GetTopOfDSReserveDeck());
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(force, scn.GetDSForcePileCount());
        return scn;
    }

    @Test
    public void RevolutionCannotSelectTheOfficeButCanRotateAnOrdinarySite() {
        var scn = scenario(false);
        var revolution = scn.GetLSCard("revolution");
        scn.StartGame();
        scn.MoveCardsToLSHand(revolution);
        scn.SkipToLSTurn(Phase.CONTROL);
        while (scn.GetLSForcePileCount() < 3) scn.MoveCardsToTopOfLSForcePile(scn.GetTopOfLSReserveDeck());
        scn.SkipToPhase(Phase.DEPLOY);
        scn.LSDeployCard(revolution);
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetDSStartingLocation()));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation()));
        scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(scn.GetLSStartingLocation(), revolution));
    }

    @Test
    public void BackOfActualIsbObjectiveStaysUpWithoutBlountPresentAtTheOffice() {
        verifyObjective("absent", Title.Empires_Sinister_Agents);
        verifyObjective("elsewhere", Title.Empires_Sinister_Agents);
    }

    @Test
    public void BlountPresentAtContestedOfficeLetsActualIsbObjectiveFlipBack() {
        verifyObjective("here", Title.ISB_Operations);
    }

    private void verifyObjective(String blount, String after) {
        var scn = scenario(true);
        var objective = scn.GetDSCard("operations");
        var office = scn.GetDSCard("office");
        var yularen = scn.GetDSCard("yularen");
        var han = scn.GetLSCard("han");
        var mess = scn.GetLSCard("mess");
        scn.StartGame();
        assertInZone(Zone.LOCATIONS, office);
        assertEquals(Title.ISB_Operations, objective.getBlueprint().getTitle());
        scn.MoveCardsToLocation(office, scn.GetDSCard("agent"), scn.GetDSCard("scoutAgent"),
                scn.GetDSCard("droidAgent"), scn.GetDSCard("vader"));
        scn.MoveCardsToDSHand(yularen);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSDeployCard(yularen);
        scn.DSChooseCard(office);
        scn.PassAllResponses();
        assertEquals(Title.Empires_Sinister_Agents, objective.getBlueprint().getTitle());

        // Keep the last agent and make the real final loss through original Han's weapon.
        for (String key : List.of("agent", "scoutAgent", "droidAgent")) scn.MoveOutOfPlay(scn.GetDSCard(key));
        scn.MoveCardsToLocation(office, han);
        if (blount.equals("here")) scn.MoveCardsToLocation(office, scn.GetLSCard("blount"));
        if (blount.equals("elsewhere")) scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("blount"));
        scn.MoveCardsToLSHand(mess);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(Title.Empires_Sinister_Agents, objective.getBlueprint().getTitle());
        scn.PrepareLSDestiny(4);
        scn.LSPlayCard(mess, "Fire a weapon");
        scn.LSChooseCard(han);
        scn.PassAllResponses();
        scn.LSChooseCard(yularen);
        scn.PassAllResponses();
        assertInZone(Zone.TOP_OF_LOST_PILE, yularen);
        assertEquals(after, objective.getBlueprint().getTitle());
    }
}
