package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.*;

/** One Beautiful Thing (1261_46, class 601_92): Virtual Cards Master List text, Used Interrupt, cards "there". */
public class Card_601_092_Tests {
    private static final String BATTLE = "Add destiny to power and reset attrition";

    private VirtualTableScenario scenario(String obtId) {
        return new VirtualTableScenario(new HashMap<>(Map.of("luke", "1_19")),
                new HashMap<>(Map.of("obt", obtId, "juno", "1117_12", "male", "1_174", "third", "1_176")),
                20, 20, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void TextIsTheMasterListTextAndItIsAUsedInterrupt() {
        var bp = scenario("1261_46").GetDSCard("obt").getBlueprint();
        assertEquals("Lose 1 Force to ▲ (or retrieve) Juno Eclipse. OR During battle where you have no Dark Jedi or droids and only two cards with ability there (a male and a female), add one destiny to total power (and attrition against you = 0).", bp.getGameText());
        assertFalse(bp.getGameText().contains("participating"));
        assertEquals(CardSubtype.USED, bp.getCardSubtype());
        assertNull(bp.getLegacyVersion());
    }

    @Test
    public void TakingJunoPlacesTheInterruptInUsedPile() {
        var scn = scenario("1261_46");
        var obt = scn.GetDSCard("obt");
        var juno = scn.GetDSCard("juno");
        scn.StartGame();
        scn.MoveCardsToDSHand(obt);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(5);
        scn.MoveCardsToBottomOfDSReserveDeck(juno);
        assertTrue(scn.DSCardPlayAvailable(obt, "Take Juno Eclipse into hand"));
        scn.DSPlayCard(obt, "Take Juno Eclipse into hand");
        for (int i = 0; i < 8 && juno.getZone() != Zone.HAND; i++) {
            if (scn.DSAnyDecisionsAvailable() && scn.DSHasCardChoiceAvailable(juno)) scn.DSChooseCard(juno);
            else if (scn.DSAnyDecisionsAvailable() && scn.DSDecisionAvailable("Force to lose"))
                scn.DSChoose(new String[]{scn.DSGetCardChoices().getFirst()});
            else scn.PassAllResponses();
        }
        assertInZone(Zone.HAND, juno);
        scn.PassAllResponses();
        // Printed as a Used Interrupt (GEMP used to code it as Lost).
        assertInZone(Zone.USED_PILE, obt);
    }

    /** Battle at Luke's site with your male and female; optionally a third character of yours with ability is there. */
    boolean battleActionAvailable(String obtId, boolean thirdThere) {
        var scn = scenario(obtId);
        var obt = scn.GetDSCard("obt");
        scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("luke"), scn.GetDSCard("juno"), scn.GetDSCard("male"));
        if (thirdThere)
            scn.MoveCardsToLocation(site, scn.GetDSCard("third"));
        scn.MoveCardsToDSHand(obt);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.IsBattleStarted());
        return scn.DSCardPlayAvailable(obt, BATTLE);
    }

    @Test
    public void BattleClauseWithExactlyAMaleAndAFemaleThere() {
        assertTrue(battleActionAvailable("1261_46", false));
    }

    @Test
    public void BattleClauseIsNotOfferedWithAThirdCardWithAbilityThere() {
        // A third character of yours with ability at the battle site: not "only two". (GEMP treats your cards at the
        // battle location that do not participate as inactive, so "there" and the PC-E1 "participating" count the
        // same cards here; 2261_1046 keeps the participating filter literally.)
        assertFalse(battleActionAvailable("1261_46", true));
        assertTrue(battleActionAvailable("2261_1046", false));
    }
}
