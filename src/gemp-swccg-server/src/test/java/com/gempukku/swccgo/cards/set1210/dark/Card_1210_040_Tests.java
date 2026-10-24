package com.gempukku.swccgo.cards.set1210.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_040_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("influence", "1210_40");
                    put("greedo", "2_89");
                    put("vigo", "10_53");
                    put("boba", "5_91");
                    put("jabba", "7_182");
                    put("xizor", "10_45");
                    put("scum", "6_149");
                    put("emperor", "9_109");
                    put("labria", "1_184");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DSStartingLocation("6_162"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void JabbasInfluenceStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("influence").getBlueprint();

        assertEquals("Jabba's Influence", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JabbasInfluenceDeploysOnTable() {
        var scn = GetScenario();
        var influence = scn.GetDSCard("influence");

        scn.StartGame();
        scn.MoveCardsToDSHand(influence);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(3);

        assertTrue(scn.DSCardPlayAvailable(influence));
        scn.DSPlayCard(influence);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, influence);
    }

    @Test
    public void JabbasInfluenceMakesYourGangstersInformationBrokers() {
        var scn = GetScenario();
        var influence = scn.GetDSCard("influence");
        var vigo = scn.GetDSCard("vigo");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(influence);
        scn.MoveCardsToLocation(site, vigo);

        assertTrue(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), vigo, Keyword.INFORMATION_BROKER));
    }

    @Test
    public void JabbasInfluenceMakesYourBountyHuntersExceptBobaFettPowerPlusOne() {
        var scn = GetScenario();
        var influence = scn.GetDSCard("influence");
        var greedo = scn.GetDSCard("greedo");
        var boba = scn.GetDSCard("boba");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, greedo, boba);
        int greedoBefore = scn.GetPower(greedo);
        int bobaBefore = scn.GetPower(boba);
        scn.MoveCardsToDSSideOfTable(influence);

        assertEquals(greedoBefore + 1, scn.GetPower(greedo));
        assertEquals(bobaBefore, scn.GetPower(boba));
    }

    @Test
    public void JabbasInfluenceDeploysANonBountyHunterAlienFromReserveDeckIfJabbaAtAudienceChamber() {
        var scn = GetScenario();
        var influence = scn.GetDSCard("influence");
        var jabba = scn.GetDSCard("jabba");
        var labria = scn.GetDSCard("labria");
        var vigo = scn.GetDSCard("vigo");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(influence);
        scn.MoveCardsToLocation(site, jabba);
        scn.MoveCardsToDSHand(vigo);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(2);
        scn.MoveCardsToTopOfDSReserveDeck(labria);

        assertTrue(scn.DSCardActionAvailable(influence, "Deploy non-bounty hunter alien"));
        scn.DSUseCardAction(influence, "Deploy non-bounty hunter alien");
        for (int i = 0; i < 20; i++) {
            if (labria.getZone() == Zone.AT_LOCATION) {
                break;
            }
            if (Boolean.TRUE.equals(scn.DSChoiceAvailable("Yes"))) {
                scn.DSChooseYes();
                continue;
            }
            if (scn.DSHasCardChoiceAvailable(labria)) {
                scn.DSChooseCard(labria);
                continue;
            }
            if (scn.DSHasCardChoiceAvailable(site)) {
                scn.DSChooseCard(site);
                continue;
            }
            if (scn.LSGetDecision() != null && (scn.LSChoiceAvailable("Pass")
                    || scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional"))) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && (scn.DSChoiceAvailable("Pass")
                    || scn.DSDecisionAvailable("optional") || scn.DSDecisionAvailable("Optional"))) {
                scn.DSPass();
                continue;
            }
            break;
        }

        assertAtLocation(site, labria);
    }

    @Test
    public void JabbasInfluenceMakesScumAndVillainyIgnoreEmperorWhileXizorIsOnTable() {
        var scn = GetScenario();
        var influence = scn.GetDSCard("influence");
        var scum = scn.GetDSCard("scum");
        var jabba = scn.GetDSCard("jabba");
        var emperor = scn.GetDSCard("emperor");
        var xizor = scn.GetDSCard("xizor");
        var labria = scn.GetDSCard("labria");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.AttachCardsTo(site, scum);
        scn.MoveCardsToLocation(site, jabba, emperor);
        scn.MoveCardsToDSHand(labria);
        int withEmperor = scn.GetDeployCost(labria);
        scn.MoveCardsToLocation(site, xizor);
        scn.MoveCardsToDSSideOfTable(influence);

        assertEquals(withEmperor - 1, scn.GetDeployCost(labria));
    }

    private boolean dsHasCardActions(VirtualTableScenario scn) {
        return scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null;
    }

    private void reenterDsPhaseActions(VirtualTableScenario scn) {
        for (int i = 0; i < 6; i++) {
            if (dsHasCardActions(scn) && scn.DSCardActionAvailable(scn.GetDSCard("influence"), "Deploy non-bounty hunter alien")) {
                return;
            }
            if (scn.LSGetDecision() != null && (scn.LSChoiceAvailable("Pass")
                    || scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("Optional"))) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && (scn.DSChoiceAvailable("Pass")
                    || scn.DSDecisionAvailable("optional") || scn.DSDecisionAvailable("Optional"))) {
                scn.DSPass();
                continue;
            }
            break;
        }
    }
}
