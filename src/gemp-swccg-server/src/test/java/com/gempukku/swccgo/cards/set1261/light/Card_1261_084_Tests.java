package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_084_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("logistics", "1261_84"); put("plans", "1117_2"); put("plans2", "1117_2"); put("jawa", "1_12"); put("talz", "1_31");
            put("luke", "1_19"); put("leia", "1271_34"); put("han", "108_1"); put("falcon", "13_21"); put("red1", "1_144");
            put("garrison", "1261_110"); put("xwing", "1_146"); put("traffic", "1_66"); put("traffic2", "1_66");
        }}, new HashMap<>() {{ put("trooper", "1_194"); put("vader", "1_168"); put("alter", "1_234"); }},
                60, 60, StartingSetup.LSStartingLocation("1_129"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    private VirtualTableScenario started(boolean deployed) {
        var scn = scenario(); scn.StartGame();
        if (deployed) scn.MoveCardsToLSSideOfTable(scn.GetLSCard("logistics"));
        else scn.MoveCardsToLSHand(scn.GetLSCard("logistics"));
        return scn;
    }
    @Test public void RepublicLogisticsUsesNewTitleRatherThanVirtualSuffix() {
        var scn = scenario(); var b = scn.GetLSCard("logistics").getBlueprint();
        assertEquals("Republic Logistics", b.getTitle()); assertEquals(5, b.getDestiny(), scn.epsilon);
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNIQUE, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity()); assertEquals(CardSubtype.NORMAL, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.EFFECT, Icon.EPISODE_I, Icon.CORUSCANT, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }
    @Test public void RepublicLogisticsActuallyDeploysOnOwnTableForFree() {
        var scn = started(false); scn.SkipToLSTurn(Phase.DEPLOY); int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(scn.GetLSCard("logistics")); scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("logistics")); assertEquals(force, scn.GetLSForcePileCount());
    }
    @Test public void RepublicLogisticsActuallyDeploysPlansFromReserveAndReshuffles() {
        var scn = started(true); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLSHand(scn.GetLSCard("plans2"));
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("plans"), scn.GetLSCard("traffic"));
        scn.gameState().markCardsRevealedFromPile(Collections.singletonList(scn.GetLSCard("traffic")));
        scn.LSUseCardAction(scn.GetLSCard("logistics"), "Deploy A Jedi's Plans"); scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard("plans"))) scn.LSChooseCard(scn.GetLSCard("plans"));
        if (scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation())) scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses(); assertEquals(scn.GetLSStartingLocation(), scn.GetLSCard("plans").getAttachedTo());
        assertInZone(Zone.RESERVE_DECK, scn.GetLSCard("traffic")); assertFalse(scn.gameState().isCardRevealedFromPile(scn.GetLSCard("traffic")));
    }
    @Test public void RepublicLogisticsDoesNotOfferDeploymentWhilePlansAlreadyOnTable() {
        var scn = started(true); scn.AttachCardsTo(scn.GetLSStartingLocation(), scn.GetLSCard("plans"));
        scn.SkipToLSTurn(Phase.DEPLOY); scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("plans2"));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("logistics"), "Deploy A Jedi's Plans"));
    }
    @Test public void RepublicLogisticsCannotDeployNormalEffectOutsideDeployPhase() {
        var scn = started(true); scn.SkipToLSTurn(Phase.CONTROL); scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("plans"));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("logistics"), "Deploy A Jedi's Plans"));
    }
    private VirtualTableScenario lost(String... keys) {
        var scn = started(true);
        for (String key : keys) scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard(key));
        scn.SkipToLSTurn(Phase.CONTROL); return scn;
    }
    private void chooseCost(VirtualTableScenario scn, String cost) {
        scn.LSUseCardAction(scn.GetLSCard("logistics"), "Place a lost card");
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard(cost))) scn.LSChooseCard(scn.GetLSCard(cost));
        scn.PassAllResponses(); assertInZone(Zone.OUT_OF_PLAY, scn.GetLSCard(cost));
    }
    private void chooseRetrieved(VirtualTableScenario scn, String target) {
        if (scn.LSHasCardChoiceAvailable(scn.GetLSCard(target))) scn.LSChooseCard(scn.GetLSCard(target));
        scn.PassAllResponses(); assertInZone(Zone.USED_PILE, scn.GetLSCard(target));
    }
    private boolean selectable(VirtualTableScenario scn, String key) {
        int index = scn.LSGetBPChoices().indexOf(scn.GetLSCard(key).getBlueprintId(true));
        return index >= 0 && Boolean.parseBoolean(scn.LSGetADParamAsList("selectable").get(index));
    }
    @Test public void RepublicLogisticsPaysLostJawaAndRetrievesActualAbilityFourCharacter() {
        var scn = lost("jawa", "luke"); chooseCost(scn, "jawa"); chooseRetrieved(scn, "luke");
    }
    @Test public void RepublicLogisticsCanRetrieveUniqueStarshipWithNoAbility() {
        var scn = lost("jawa", "red1"); chooseCost(scn, "jawa"); chooseRetrieved(scn, "red1");
    }
    @Test public void RepublicLogisticsCanRetrieveUniqueVehicleWithPermanentPilots() {
        var scn = lost("talz", "garrison"); chooseCost(scn, "talz"); chooseRetrieved(scn, "garrison");
    }
    @Test public void RepublicLogisticsCostMustHaveAbilityAndBeNonUnique() {
        var scn = lost("traffic", "xwing", "luke"); scn.MoveCardsToTopOfDSLostPile(scn.GetDSCard("trooper"));
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("logistics"), "Place a lost card"));
    }
    @Test public void RepublicLogisticsRetrievalExcludesAbilityFiveWeaponMaintenanceAndNonUnique() {
        var scn = lost("jawa", "luke", "red1", "leia", "han", "falcon", "xwing", "traffic");
        chooseCost(scn, "jawa");
        assertTrue(selectable(scn, "luke")); assertTrue(selectable(scn, "red1"));
        for (String key : new String[]{"leia", "han", "falcon", "xwing", "traffic"}) assertFalse(key, selectable(scn, key));
        chooseRetrieved(scn, "luke"); assertInZone(Zone.LOST_PILE, scn.GetLSCard("red1"));
    }
    @Test public void RepublicLogisticsOncePerTurnAndResetsOnOpponentTurn() {
        var scn = lost("jawa", "talz", "luke", "red1"); chooseCost(scn, "jawa"); chooseRetrieved(scn, "luke"); scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("logistics"), "Place a lost card"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.DSPass(); chooseCost(scn, "talz"); chooseRetrieved(scn, "red1");
    }
    @Test public void RepublicLogisticsCannotBeCanceledByActualAlter() {
        var scn = started(true); scn.MoveCardsToLSSideOfTable(scn.GetLSCard("traffic"), scn.GetLSCard("traffic2"));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("vader")); scn.MoveCardsToDSHand(scn.GetDSCard("alter"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(0); scn.DSPlayCard(scn.GetDSCard("alter"));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("logistics"))); assertTrue(scn.DSHasCardChoiceAvailable(scn.GetLSCard("traffic")));
        scn.DSChooseCard(scn.GetLSCard("traffic")); scn.DSChooseCard(scn.GetDSCard("vader"));
        scn.PassAllResponses(); scn.PassDestinyDrawResponses(); scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, scn.GetLSCard("logistics")); assertInZone(Zone.LOST_PILE, scn.GetLSCard("traffic"));
    }
}
