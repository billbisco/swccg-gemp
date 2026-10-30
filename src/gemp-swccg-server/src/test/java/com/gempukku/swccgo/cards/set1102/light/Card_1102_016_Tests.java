package com.gempukku.swccgo.cards.set1102.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1102_016_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("cape", "1102_16");
                    put("obi", "1_21");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317");
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
    public void ObiWansCapeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("cape").getBlueprint();

        assertEquals("Obi-Wan's Cape", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ObiWansCapeDeploysOnObiWan() {
        var scn = GetScenario();
        var cape = scn.GetLSCard("cape");
        var obi = scn.GetLSCard("obi");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, obi);
        scn.MoveCardsToLSHand(cape);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(cape));
        scn.LSPlayCard(cape);
        scn.LSChooseCard(obi);
        scn.PassAllResponses();
        assertEquals(obi, cape.getAttachedTo());
    }

    @Test
    public void ObiWansCapeAddsOneDefenseValueToObiWan() {
        var scn = GetScenario();
        var cape = scn.GetLSCard("cape");
        var obi = scn.GetLSCard("obi");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, obi);
        int before = scn.GetDefense(obi);
        scn.AttachCardsTo(obi, cape);
        assertEquals(before + 1, scn.GetDefense(obi));
        assertTrue(scn.game().getModifiersQuerying().mayNotBeExcludedFromBattle(scn.gameState(), obi));
    }

    @Test
    public void ObiWansCapeMayCancelJustDrawnWeaponDestinyHereDuringOpponentsTurn() {
        var scn = GetScenario();
        var cape = scn.GetLSCard("cape");
        var obi = scn.GetLSCard("obi");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, obi, trooper);
        scn.AttachCardsTo(obi, cape);
        scn.AttachCardsTo(trooper, blaster);
        scn.EnsureDSForcePile(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(7);
        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(obi);
        scn.PassForceUseResponses();
        boolean canceled = false;
        for (int i = 0; i < 16; i++) {
            if (lsHasCardActions(scn) && (scn.LSCardActionAvailable(cape, "Cancel destiny") || scn.LSActionAvailable("Cancel destiny"))) {
                scn.LSChooseAction("Cancel destiny");
                canceled = true;
                break;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertTrue(canceled);
        scn.PassAllResponses();
        assertFalse(obi.isHit());
        assertTrue(cape.getZone() == Zone.LOST_PILE || cape.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void ObiWansCapeDoesNotOfferToCancelWeaponDestinyDuringYourTurn() {
        var scn = GetScenario();
        var cape = scn.GetLSCard("cape");
        var obi = scn.GetLSCard("obi");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, obi, trooper);
        scn.AttachCardsTo(obi, cape);
        scn.AttachCardsTo(trooper, blaster);
        scn.EnsureDSForcePile(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.LSPass();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        scn.PrepareDSDestiny(7);
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(obi);
        scn.PassForceUseResponses();
        boolean offered = false;
        for (int i = 0; i < 16; i++) {
            if (lsHasCardActions(scn) && (scn.LSCardActionAvailable(cape, "Cancel destiny") || scn.LSActionAvailable("Cancel destiny"))) {
                offered = true;
                break;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            break;
        }
        assertFalse(offered);
        assertEquals(Zone.ATTACHED, cape.getZone());
    }

    private boolean lsHasCardActions(VirtualTableScenario scn) {
        return scn.LSGetDecision() != null && scn.LSGetADParam("cardId") != null;
    }
}
