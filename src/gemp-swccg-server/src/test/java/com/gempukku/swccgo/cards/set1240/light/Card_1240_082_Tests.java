package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_082_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("disarming", "1240_82");
                    put("skull", "1240_97");
                    put("bones", "1240_90");
                    put("ittsboto", "3_43");
                    put("worrt", "6_48");
                    put("nudj", "4_5");
                    put("tauntaun", "3_70");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
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
    public void DisarmingCreatureVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("disarming").getBlueprint();

        assertEquals(Title.Disarming_Creature, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.HOTH);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void DisarmingCreatureVSkullIsDestinyPlus2() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var skull = scn.GetLSCard("skull");

        scn.StartGame();
        scn.MoveCardsToLSHand(skull);
        assertEquals(4, scn.GetDestiny(skull), scn.epsilon);
        scn.MoveCardsToLSSideOfTable(disarming);
        assertEquals(6, scn.GetDestiny(skull), scn.epsilon);
    }

    @Test
    public void DisarmingCreatureVDeploysTauntaunBonesFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var bones = scn.GetLSCard("bones");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(disarming);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.LS, 6);
        scn.MoveCardsToTopOfLSReserveDeck(bones);

        assertTrue(scn.LSCardActionAvailable(disarming, "Deploy Tauntaun Bones"));
        scn.LSUseCardAction(disarming, "Deploy Tauntaun Bones");
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(bones)) {
            scn.LSChooseCard(bones);
        }
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, bones.getZone());

        scn.MoveCardsToTopOfLSReserveDeck(bones);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(disarming, "Deploy Tauntaun Bones"));
    }

    @Test
    public void DisarmingCreatureVLetsIThoughtTheySmelledBadTakeACreatureForOneForce() {
        var scn = GetScenario();
        var disarming = scn.GetLSCard("disarming");
        var ittsboto = scn.GetLSCard("ittsboto");
        var worrt = scn.GetLSCard("worrt");
        var nudj = scn.GetLSCard("nudj");

        var scn0 = GetScenario();
        scn0.StartGame();
        scn0.MoveCardsToLSHand(scn0.GetLSCard("ittsboto"));
        scn0.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn0.LSCardActionAvailable(scn0.GetLSCard("ittsboto"), "Lose 1 Force to take card into hand"));

        scn.StartGame();
        scn.MoveCardsToLSHand(ittsboto);
        scn.MoveCardsToLSSideOfTable(disarming);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int lost = scn.GetLSLostPileCount();
        assertTrue(scn.LSCardActionAvailable(ittsboto, "Lose 1 Force to take card into hand"));
        scn.LSUseCardAction(ittsboto, "Lose 1 Force to take card into hand");
        for (int i = 0; i < 6; i++) {
            if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayForceLossFromReserveDeck();
            }
            else if (scn.LSGetADParamAsList("blueprintId") == null) {
                scn.PassAllResponses();
            }
        }
        int nudjIndex = scn.LSGetADParamAsList("blueprintId").indexOf(nudj.getBlueprintId(true));
        if (nudjIndex >= 0) {
            assertFalse(Boolean.parseBoolean(scn.LSGetADParam("selectable")[nudjIndex]));
        }
        scn.LSChooseCard(worrt);
        scn.PassAllResponses();
        assertEquals(Zone.HAND, worrt.getZone());
        assertEquals(lost + 1, scn.GetLSLostPileCount());
        assertTrue(ittsboto.getZone() == Zone.USED_PILE || ittsboto.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
