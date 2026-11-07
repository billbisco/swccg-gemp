package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_1261_085_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>() {{
            put("return", "1261_85"); put("luke", "1_19"); put("leia", "1_17"); put("obi", "1_21");
            put("remote", "1_136"); put("normalImmediate", "1106_11"); put("system", "2_65");
        }}, new HashMap<>() {{
            put("sidious", "14_78"); put("emperor", "1230_19"); put("ordinaryEmperor", "9_109"); put("trooper", "1_194");
            put("lightning", "9_136"); put("gravel", "1_247"); put("mos", "1_295"); put("control", "4_139"); put("tie", "1_304");
        }}, 60, 60, StartingSetup.LSStartingLocation("1_129"), StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }
    private VirtualTableScenario started() {
        var scn = scenario(); scn.StartGame(); scn.MoveCardsToLSHand(scn.GetLSCard("return"));
        scn.MoveLocationToTable(scn.GetLSCard("remote")); scn.MoveLocationToTable(scn.GetDSCard("mos")); return scn;
    }
    @Test public void ReturnUsesNewTitleAndImmediateEffectSubtype() {
        var scn = scenario(); var b = scn.GetLSCard("return").getBlueprint();
        assertEquals(Title.Return_Of_The_Jedi, b.getTitle()); assertEquals(4, b.getDestiny(), scn.epsilon);
        assertEquals(Side.LIGHT, b.getSide()); assertEquals(Uniqueness.UNRESTRICTED, b.getUniqueness()); assertFalse(b.hasVirtualSuffix());
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_1, b.getExpansionSet()); assertEquals(Rarity.V, b.getRarity()); assertEquals(CardSubtype.IMMEDIATE, b.getCardSubtype());
        scn.BlueprintCardTypeCheck(b, new ArrayList<>(Arrays.asList(CardType.EFFECT)));
        scn.BlueprintIconCheck(b, new ArrayList<>(Arrays.asList(Icon.EFFECT, Icon.DEATH_STAR_II, Icon.LEGACY_BLOCK_6)));
        scn.BlueprintKeywordCheck(b, new ArrayList<>());
    }
    private VirtualTableScenario drain() {
        var scn = started(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard("trooper"));
        scn.SkipToDSTurn(Phase.CONTROL); return scn;
    }
    private void cancelDrain(VirtualTableScenario scn) {
        int lost = scn.GetLSLostPileCount(); scn.DSForceDrainAt(scn.GetLSStartingLocation());
        scn.LSPlayCard(scn.GetLSCard("return"), "Deploy to cancel");
        if (scn.LSHasCardChoiceAvailable(scn.GetLSStartingLocation())) scn.LSChooseCard(scn.GetLSStartingLocation());
        scn.PassAllResponses(); assertEquals(scn.GetLSStartingLocation(), scn.GetLSCard("return").getAttachedTo());
        assertEquals(lost, scn.GetLSLostPileCount()); assertNull(scn.gameState().getForceDrainState());
    }
    @Test public void ReturnActuallyCancelsSiteDrainWithoutLosingForce() { cancelDrain(drain()); }
    @Test public void ReturnSiteModeBuffsOnlySkywalkersHereAndExpiresOnOwnEndTurn() {
        var scn = drain(); cancelDrain(scn); var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetLSCard("obi"));
        scn.MoveCardsToLocation(scn.GetLSCard("remote"), scn.GetLSCard("leia"));
        assertEquals(4, scn.GetPower(scn.GetLSCard("luke"))); assertEquals(5, scn.GetPower(scn.GetLSCard("obi"))); assertEquals(3, scn.GetPower(scn.GetLSCard("leia")));
        scn.SkipToLSTurn(Phase.CONTROL); assertEquals(site, scn.GetLSCard("return").getAttachedTo());
        scn.SkipToDSTurn(Phase.CONTROL); assertInZone(Zone.LOST_PILE, scn.GetLSCard("return")); assertEquals(3, scn.GetPower(scn.GetLSCard("luke")));
    }
    @Test public void ReturnCannotDeployOnAnOrdinarySiteWithoutDrain() {
        var scn = started(); scn.SkipToLSTurn(Phase.DEPLOY); assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("return")));
    }
    @Test public void ReturnCannotRespondToDrainAtSystem() {
        var scn = started(); var system = scn.GetLSCard("system"); scn.MoveLocationToTable(system);
        scn.MoveCardsToLocation(system, scn.GetDSCard("tie")); scn.SkipToDSTurn(Phase.CONTROL); scn.DSForceDrainAt(system);
        assertNotNull(scn.gameState().getForceDrainState()); assertEquals(system, scn.gameState().getForceDrainState().getLocation());
        assertFalse(scn.GetLSAvailableActions().stream().anyMatch(x -> x.contains("Deploy to cancel")));
        assertInHand(scn.GetLSCard("return"));
    }
    private VirtualTableScenario host(String key, boolean opponentTurn) {
        var scn = started(); scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetDSCard(key));
        if (opponentTurn) { scn.SkipToDSTurn(Phase.CONTROL); scn.DSPass(); }
        else scn.SkipToLSTurn(Phase.CONTROL);
        return scn;
    }
    private void deployOn(VirtualTableScenario scn, String key) {
        var target = scn.GetDSCard(key); scn.LSDeployCard(scn.GetLSCard("return"));
        if (scn.LSHasCardChoiceAvailable(target)) scn.LSChooseCard(target);
        scn.PassAllResponses(); assertEquals(target, scn.GetLSCard("return").getAttachedTo());
    }
    @Test public void ReturnActuallyDeploysOnSidiousDuringOwnControl() { deployOn(host("sidious", false), "sidious"); }
    @Test public void ReturnActuallyDeploysOnBlockThreeEmperorDuringOpponentsControl() { deployOn(host("emperor", true), "emperor"); }
    @Test public void ReturnDoesNotTreatAnyEmperorAsBlockThreePrinting() {
        var scn = host("ordinaryEmperor", false); assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("return")));
    }
    @Test public void ReturnOnEmperorCancelsActualEmperorInterruptAndPersistsPastOwnTurn() {
        var scn = host("emperor", false); deployOn(scn, "emperor");
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("luke")); scn.MoveCardsToDSHand(scn.GetDSCard("lightning"));
        scn.SkipToDSTurn(Phase.CONTROL); assertEquals(scn.GetDSCard("emperor"), scn.GetLSCard("return").getAttachedTo());
        int defense = scn.GetDefense(scn.GetLSCard("luke")); scn.DSPlayCard(scn.GetDSCard("lightning"), "Reduce target");
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("luke"))) scn.DSChooseCard(scn.GetLSCard("luke"));
        scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, scn.GetDSCard("lightning")); assertEquals(defense, scn.GetDefense(scn.GetLSCard("luke")));
        assertEquals(3, scn.GetPower(scn.GetLSCard("luke")));
    }
    private VirtualTableScenario sidiousLoss(boolean protect) {
        var scn = host("sidious", false); if (protect) deployOn(scn, "sidious");
        scn.MoveCardsToLocation(scn.GetDSCard("mos"), scn.GetLSCard("obi")); scn.MoveCardsToDSHand(scn.GetDSCard("gravel"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.PrepareDSDestiny(7);
        scn.DSPlayCard(scn.GetDSCard("gravel")); scn.DSChooseCard(scn.GetLSCard("obi"));
        scn.PassCardAndForceUseResponses(); scn.PassDestinyDrawResponses(); scn.PassResponses("ABOUT_TO_BE_LOST");
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("obi")); return scn;
    }
    @Test public void ReturnOnSidiousBlocksHisActualJustLostJediOutOfPlayAction() {
        var scn = sidiousLoss(true); assertFalse(scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("out of play")));
        assertInZone(Zone.LOST_PILE, scn.GetLSCard("obi"));
    }
    @Test public void ReturnAbsentSameSidiousCanActuallyPlaceJediOutOfPlay() {
        var scn = sidiousLoss(false); scn.DSUseCardAction(scn.GetDSCard("sidious"), "out of play");
        scn.PassAllResponses(); scn.DSChooseCard(scn.GetTopOfDSReserveDeck());
        scn.PassAllResponses(); assertInZone(Zone.OUT_OF_PLAY, scn.GetLSCard("obi"));
    }
    @Test public void ReturnImmuneToActualControlWithOrdinaryImmediateEffectAsControl() {
        var scn = host("emperor", false); deployOn(scn, "emperor");
        scn.MoveCardsToLSSideOfTable(scn.GetLSCard("normalImmediate")); scn.MoveCardsToDSHand(scn.GetDSCard("control"));
        scn.SkipToDSTurn(Phase.CONTROL); scn.DSPlayCard(scn.GetDSCard("control"));
        assertFalse(scn.DSHasCardChoiceAvailable(scn.GetLSCard("return")));
        if (scn.DSHasCardChoiceAvailable(scn.GetLSCard("normalImmediate"))) scn.DSChooseCard(scn.GetLSCard("normalImmediate"));
        scn.PassAllResponses(); assertInZone(Zone.LOST_PILE, scn.GetLSCard("normalImmediate"));
        assertEquals(scn.GetDSCard("emperor"), scn.GetLSCard("return").getAttachedTo());
    }
}
