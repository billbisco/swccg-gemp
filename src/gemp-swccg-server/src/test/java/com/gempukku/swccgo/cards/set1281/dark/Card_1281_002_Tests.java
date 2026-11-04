package com.gempukku.swccgo.cards.set1281.dark;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("tatooine", "1_127"); // Tatooine system
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("bane", "1281_2");
                    put("tie", "1_304"); // TIE Fighter
                    put("trooper", "1_194");
                    put("handcard", "1_194");
                    put("blaster", "1_312"); // Blaster Rifle
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
    public void BaneMalarSpiceAddictStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("bane").getBlueprint();
        assertEquals("Bane Malar, Spice Addict", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLACK_SUN_AGENT);
            add(Keyword.ASSASSIN);
            add(Keyword.BOUNTY_HUNTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BaneMalarSpiceAddictAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var bane = scn.GetDSCard("bane");
        var tie = scn.GetDSCard("tie");
        var tatooine = scn.GetLSCard("tatooine");
        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, tie);
        scn.BoardAsPilot(tie, bane);
        assertEquals(3, scn.GetPower(tie));
    }

    @Test
    public void BaneMalarSpiceAddictMindscanMakesHimPowerPlusTwoAndFireAWeaponTwice() {
        var scn = GetScenario();
        var bane = scn.GetDSCard("bane");
        var handcard = scn.GetDSCard("handcard");
        var blaster = scn.GetDSCard("blaster");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), bane);
        scn.AttachCardsTo(bane, blaster);
        scn.MoveCardsToHand(handcard);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(2, scn.GetPower(bane));
        assertFalse(scn.game().getModifiersQuerying().mayFireWeaponTwicePerBattle(scn.gameState(), bane, blaster));
        assertTrue(scn.DSCardActionAvailable(bane, "'Mindscan'"));
        scn.DSUseCardAction(bane, "'Mindscan'");
        if (scn.DSHasCardChoiceAvailable(handcard)) {
            scn.DSChooseCard(handcard);
        }
        scn.PassAllResponses();
        scn.DSChoose("Power +2 and may fire a weapon twice per battle");
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, handcard);
        assertEquals(4, scn.GetPower(bane));
        assertTrue(scn.game().getModifiersQuerying().mayFireWeaponTwicePerBattle(scn.gameState(), bane, blaster));
    }

    @Test
    public void BaneMalarSpiceAddictMindscanMayPeekAtTopCardsOfForcePileAndIsOncePerTurn() {
        var scn = GetScenario();
        var bane = scn.GetDSCard("bane");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var handcard = scn.GetDSCard("handcard");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), bane, trooper, luke);
        scn.MoveCardsToHand(handcard);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSUseCardAction(bane, "'Mindscan'");
        if (scn.DSHasCardChoiceAvailable(handcard)) {
            scn.DSChooseCard(handcard);
        }
        scn.PassAllResponses();
        scn.DSChoose("Peek at top X cards of Force Pile");
        // X = 3 characters here
        assertEquals(3, scn.DSGetCardChoices().size());
        scn.DSDismissRevealedCards();
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, handcard);
        assertEquals(2, scn.GetPower(bane));
        scn.MoveCardsToHand(scn.GetDSCard("trooper"));
        if (scn.AwaitingLSDeployPhaseActions()) {
            scn.LSPass();
        }
        assertFalse(scn.DSCardActionAvailable(bane, "'Mindscan'"));
    }
}
