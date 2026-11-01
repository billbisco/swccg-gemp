package com.gempukku.swccgo.cards.set1106.light;

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

public class Card_1106_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("hand", "1106_4");
                    put("luke", "1_19");
                    put("lando", "5_5");
                    put("alter", "1_71");
                    put("fewManeuvers", "1_70");
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
    public void BionicHandStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("hand").getBlueprint();

        assertEquals("Bionic Hand", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.DEVICE);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BionicHandDeploysOnLukeOnlyAndAddsPowerAndImmunity() {
        var scn = GetScenario();
        var hand = scn.GetLSCard("hand");
        var luke = scn.GetLSCard("luke");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, lando);
        scn.MoveCardsToLSHand(hand);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        int lukePower = scn.GetPower(luke);
        float lukeImmunity = scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), luke);

        assertTrue(scn.LSDeployAvailable(hand));
        scn.LSDeployCard(hand);
        if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(luke)) {
            assertFalse(scn.LSHasCardChoiceAvailable(lando));
            scn.LSChooseCard(luke);
        }
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(luke, hand));
        assertEquals(lukePower + 2, scn.GetPower(luke));
        assertEquals(lukeImmunity + 2, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), luke), scn.epsilon);
    }

    @Test
    public void BionicHandExchangesACardInHandWithAnInterruptInLostPileDuringBattle() {
        var scn = GetScenario();
        var hand = scn.GetLSCard("hand");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var alter = scn.GetLSCard("alter");
        var fewManeuvers = scn.GetLSCard("fewManeuvers");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.AttachCardsTo(luke, hand);
        scn.MoveCardsToLocation(site, trooper);
        scn.MoveCardsToTopOfLSLostPile(fewManeuvers);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.MoveCardsToLSHand(alter);
        scn.LSInitiateBattle(site);
        for (int i = 0; i < 10 && !scn.LSCardActionAvailable(hand, "Exchange card in hand for card in Lost Pile"); i++) {
            if (scn.DSGetDecision() != null) { scn.DSPass(); continue; }
            break;
        }
        assertTrue(scn.LSCardActionAvailable(hand, "Exchange card in hand for card in Lost Pile"));
        scn.LSUseCardAction(hand, "Exchange card in hand for card in Lost Pile");
        for (int i = 0; i < 12 && fewManeuvers.getZone() != Zone.HAND; i++) {
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(alter)) { scn.LSChooseCard(alter); continue; }
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(fewManeuvers)) { scn.LSChooseCard(fewManeuvers); continue; }
            if (scn.DSGetDecision() != null) { scn.DSPass(); continue; }
            if (scn.LSGetDecision() != null) { scn.LSPass(); continue; }
            break;
        }
        assertEquals(Zone.HAND, fewManeuvers.getZone());
        assertTrue(alter.getZone() == Zone.LOST_PILE || alter.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
