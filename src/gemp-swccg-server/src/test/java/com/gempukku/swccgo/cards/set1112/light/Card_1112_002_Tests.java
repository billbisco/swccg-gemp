package com.gempukku.swccgo.cards.set1112.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1112_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("peace", "1112_2");
                    put("hut", "4_089");
                    put("debnoli", "1112_7");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("dianoga", "2_110");
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
    public void AtPeaceStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("peace").getBlueprint();

        assertEquals(Title.At_Peace, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_LOCATION);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.DAGOBAH);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void AtPeaceDeploysOnYodasHut() {
        var scn = GetScenario();
        var peace = scn.GetLSCard("peace");
        var hut = scn.GetLSCard("hut");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(hut);
        scn.MoveCardsToLSHand(peace);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(peace));
        scn.LSPlayCard(peace);
        scn.LSChooseCard(hut);
        scn.PassAllResponses();
        assertEquals(hut, peace.getAttachedTo());
    }

    @Test
    public void AtPeacePreventsDeployingPermanentWeaponCards() {
        var scn = GetScenario();
        var peace = scn.GetLSCard("peace");
        var hut = scn.GetLSCard("hut");
        var debnoli = scn.GetLSCard("debnoli");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(hut);
        scn.MoveCardsToLSHand(peace, debnoli);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(peace);
        scn.LSChooseCard(hut);
        scn.PassAllResponses();
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(debnoli));
        assertEquals(Zone.HAND, debnoli.getZone());
        assertEquals(site, scn.GetLSStartingLocation());
    }

    @Test
    public void AtPeaceCancelsAnAttackByLosingTwoForce() {
        var scn = GetScenario();
        var peace = scn.GetLSCard("peace");
        var hut = scn.GetLSCard("hut");
        var luke = scn.GetLSCard("luke");
        var dianoga = scn.GetDSCard("dianoga");

        scn.StartGame();
        scn.MoveLocationToTable(hut);
        scn.MoveCardsToLSHand(peace);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(peace);
        scn.LSChooseCard(hut);
        scn.PassAllResponses();
        scn.MoveCardsToLocation(hut, luke, dianoga);
        scn.SkipToDSTurn(Phase.BATTLE);
        assertTrue(scn.DSCardActionAvailable(dianoga, "Initiate attack"));
        int force = scn.GetLSForcePileCount();
        scn.DSUseCardAction(dianoga, "Initiate attack");
        for (int i = 0; i < 8 && !(scn.LSAnyDecisionsAvailable() && scn.LSGetADParam("actionText") != null
                && scn.LSCardActionAvailable(peace, "Cancel an attack")); i++) {
            if (scn.GetCurrentDecision() == null) {
                break;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        assertTrue(scn.LSCardActionAvailable(peace, "Cancel an attack"));
        scn.LSUseCardAction(peace, "Cancel an attack");
        for (int i = 0; i < 6 && !scn.AwaitingLSForceLossPayment(); i++) {
            if (scn.GetCurrentDecision() == null) {
                break;
            }
            scn.PlayerPass(scn.GetDecidingPlayer());
        }
        scn.LSPayForceLossFromForcePile();
        scn.LSPayForceLossFromForcePile();
        scn.PassAllResponses();
        assertEquals(force - 2, scn.GetLSForcePileCount());
        assertFalse(scn.gameState().isDuringAttack());
    }
}
