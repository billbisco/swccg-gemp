package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1282_015_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("verrack", "1282_15");
                    put("xwing", "1_146"); // X-wing
                    put("torpedoes", "1_158"); // Proton Torpedoes: hit if destiny > defense value
                    put("tatooine", "1_127"); // Tatooine system
                }},
                new HashMap<>() {{
                    put("tie", "1_304"); // TIE Fighter, maneuver 3
                    put("vader", "1_168");
                    put("watch", "12_158"); // [Coruscant] Watch Your Back!
                    put("sewatch", "7_268"); // [Special Edition] Watch Your Back!
                    put("slave", "5_177"); // a unique unpiloted starfighter for Watch Your Back!'s USED text
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
    public void CaptainVerrackVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("verrack").getBlueprint();

        assertEquals("Captain Verrack", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals("Mon Calamari computer technician. Expert in power systems. His skills are desperately needed by Rebel fleet. Gunner.", card.getLore());
        assertEquals(Species.MON_CALAMARI, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GUNNER);
            add(Keyword.CAPTAIN);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CaptainVerrackVAdds1ToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var verrack = scn.GetLSCard("verrack");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        int power = scn.GetPower(xwing);
        scn.BoardAsPilot(xwing, verrack);
        assertEquals(power + 1, scn.GetPower(xwing));
    }

    private int fireTorpedoesWithDestiny3(VirtualTableScenario scn, boolean verrackAboard) {
        var verrack = scn.GetLSCard("verrack");
        var xwing = scn.GetLSCard("xwing");
        var torpedoes = scn.GetLSCard("torpedoes");
        var tatooine = scn.GetLSCard("tatooine");
        var tie = scn.GetDSCard("tie");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing, tie);
        scn.AttachCardsTo(xwing, torpedoes);
        scn.BoardAsPilot(tie, vader);
        if (verrackAboard) {
            scn.BoardAsPilot(xwing, verrack);
        }
        scn.EnsureLSForcePile(3);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.PrepareLSDestiny(3);
        int dsLostBefore = scn.GetDSLostPileCount();
        scn.LSUseCardAction(torpedoes, "Fire");
        scn.LSChooseCard(tie);
        scn.PassForceUseResponses();
        scn.PassWeaponFireWithDestinyDraw(1);
        scn.PassAllResponses();
        if (scn.AwaitingDSForceLossPayment()) {
            scn.DSPayForceLossFromReserveDeck();
            scn.PassAllResponses();
        }
        return dsLostBefore;
    }

    @Test
    public void CaptainVerrackVAboardAddsOneToTotalWeaponDestinyAndOpponentLosesForceOnHit() {
        var scn = GetScenario();
        var tie = scn.GetDSCard("tie");

        int dsLostBefore = fireTorpedoesWithDestiny3(scn, true);
        // destiny 3 + 1 > defense value 3
        assertTrue(tie.isHit());
        assertEquals(dsLostBefore + 1, scn.GetDSLostPileCount());
    }

    @Test
    public void CaptainVerrackVTorpedoesMissWithoutHim() {
        var scn = GetScenario();
        var tie = scn.GetDSCard("tie");

        int dsLostBefore = fireTorpedoesWithDestiny3(scn, false);
        assertFalse(tie.isHit());
        assertEquals(dsLostBefore, scn.GetDSLostPileCount());
    }

    @Test
    public void CaptainVerrackVMayLose1ForceToCancelCoruscantWatchYourBackDuringBattle() {
        var scn = GetScenario();
        var verrack = scn.GetLSCard("verrack");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");
        var tie = scn.GetDSCard("tie");
        var vader = scn.GetDSCard("vader");
        var watch = scn.GetDSCard("watch");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing, tie);
        scn.BoardAsPilot(xwing, verrack);
        scn.BoardAsPilot(tie, vader);
        scn.MoveCardsToDSHand(watch);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(tatooine);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.LSPass();

        assertTrue(scn.DSPlayUsedInterruptAvailable(watch));
        scn.DSPlayUsedInterrupt(watch);
        int lsLostBefore = scn.GetLSLostPileCount();
        assertTrue(scn.LSCardActionAvailable(verrack, "cancel"));
        scn.LSUseCardAction(verrack, "cancel");
        LegacyVb82TestSupport.lsPayForceLoss(scn);
        scn.PassAllResponses();

        // Watch Your Back! was canceled: no starfighter taken into hand
        assertFalse(scn.GetDSCard("slave").getZone() == Zone.HAND);
        assertFalse(watch.getZone() == Zone.USED_PILE);
        assertEquals(lsLostBefore + 1, scn.GetLSLostPileCount());
    }

    @Test
    public void CaptainVerrackVDoesNotCancelWatchYourBackOutsideBattle() {
        var scn = GetScenario();
        var verrack = scn.GetLSCard("verrack");
        var site = scn.GetLSStartingLocation();
        var watch = scn.GetDSCard("watch");

        scn.StartGame();
        scn.MoveCardsToLocation(site, verrack);
        scn.MoveCardsToDSHand(watch);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertTrue(scn.DSPlayUsedInterruptAvailable(watch));
        scn.DSPlayUsedInterrupt(watch);
        assertFalse(scn.LSCardActionAvailable(verrack, "cancel"));
    }
}
