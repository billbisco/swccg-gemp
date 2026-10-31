package com.gempukku.swccgo.cards.set1109.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1109_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("retort", "1109_4");
                    put("laudica", "1230_72");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317");
                    put("gravel", "1_247");
                    put("moseisley", "1_295");
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
    public void CorellianRetortStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("retort").getBlueprint();

        assertEquals("Corellian Retort", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.INTERRUPT);        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CorellianRetortCancelsWeaponTargetingYourCorellian() {
        var scn = GetScenario();
        var retort = scn.GetLSCard("retort");
        var laudica = scn.GetLSCard("laudica");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, laudica, trooper);
        scn.AttachCardsTo(trooper, blaster);
        scn.MoveCardsToLSHand(retort);
        scn.DSActivateForceCheat(4);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingDSBattlePhaseActions());
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(laudica);
        scn.LSPass();
        scn.DSPass();
        assertTrue(scn.LSPlayUsedInterruptAvailable(retort));
        scn.LSPlayUsedInterrupt(retort);
        scn.PassAllResponses();

        assertAtLocation(site, laudica);
        assertFalse(laudica.isHit());
        assertInZone(Zone.USED_PILE, retort);
    }

    @Test
    public void CorellianRetortRetrievesJustLostCorellianIntoHand() {
        var scn = GetScenario();
        var retort = scn.GetLSCard("retort");
        var laudica = scn.GetLSCard("laudica");
        var gravel = scn.GetDSCard("gravel");
        var moseisley = scn.GetDSCard("moseisley");

        scn.StartGame();
        scn.MoveLocationToTable(moseisley);
        scn.MoveCardsToLocation(moseisley, laudica);
        scn.MoveCardsToLSHand(retort);
        scn.MoveCardsToDSHand(gravel);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareDSDestiny(5);

        assertTrue(scn.DSCardPlayAvailable(gravel));
        scn.DSPlayCard(gravel);
        assertTrue(scn.DSDecisionAvailable("Choose"));
        scn.DSChooseCard(laudica);
        scn.PassCardAndForceUseResponses();
        scn.PassResponses("COST_TO_DRAW_DESTINY");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY");
        scn.PassResponses("DESTINY_DRAWN");
        scn.PassResponses("COMPLETE_DESTINY_DRAW");
        scn.PassResponses("DRAWING_DESTINY_COMPLETE");
        scn.PassResponses("ABOUT_TO_BE_LOST");
        assertTrue(scn.LSPlayLostInterruptAvailable(retort));
        scn.LSPlayLostInterrupt(retort);
        scn.PassAllResponses();

        assertInHand(laudica);
        assertInZone(Zone.LOST_PILE, retort);
    }
}
