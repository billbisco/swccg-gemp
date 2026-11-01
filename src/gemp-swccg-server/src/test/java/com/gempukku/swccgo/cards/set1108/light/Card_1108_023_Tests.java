package com.gempukku.swccgo.cards.set1108.light;

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

public class Card_1108_023_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("ultimatum", "1108_23");
                    put("chamber", "6_81");
                    put("luke", "1_19");
                    put("alter", "1_71");
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
    public void UltimatumStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("ultimatum").getBlueprint();

        assertEquals("Ultimatum", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JABBAS_PALACE);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_8, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void UltimatumMakesYourDestinyPlusOneAndInterruptsUncancelableOncePerTurnInJabbasPalaceBattle() {
        var scn = GetScenario();
        var ultimatum = scn.GetLSCard("ultimatum");
        var chamber = scn.GetLSCard("chamber");
        var luke = scn.GetLSCard("luke");
        var alter = scn.GetLSCard("alter");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(chamber);
        scn.MoveCardsToLSSideOfTable(ultimatum);
        scn.MoveCardsToLocation(chamber, luke, trooper);
        scn.MoveCardsToLSHand(alter);
        scn.SkipToLSTurn(Phase.BATTLE);
        int alterDestiny = (int) scn.game().getModifiersQuerying().getDestiny(scn.gameState(), alter);

        assertFalse(scn.LSCardActionAvailable(ultimatum, "Destiny draws +1; Interrupts may not be canceled"));
        scn.LSInitiateBattle(chamber);
        for (int i = 0; i < 6 && !scn.LSCardActionAvailable(ultimatum, "Destiny draws +1; Interrupts may not be canceled"); i++) {
            if (scn.DSGetDecision() != null) { scn.DSPass(); continue; }
            break;
        }
        assertTrue(scn.LSCardActionAvailable(ultimatum, "Destiny draws +1; Interrupts may not be canceled"));
        scn.LSUseCardAction(ultimatum, "Destiny draws +1; Interrupts may not be canceled");
        for (int i = 0; i < 6 && scn.DSGetDecision() != null; i++) {
            scn.DSPass();
        }

        assertEquals(alterDestiny + 1, (int) scn.game().getModifiersQuerying().getDestiny(scn.gameState(), alter));
        assertTrue(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), alter));
        assertFalse(scn.game().getModifiersQuerying().mayNotBeCanceled(scn.gameState(), trooper));
        assertFalse(scn.LSCardActionAvailable(ultimatum, "Destiny draws +1; Interrupts may not be canceled"));
    }

    @Test
    public void UltimatumPlacesItselfOutOfPlayToMakeAPlayerActivateTwoForce() {
        var scn = GetScenario();
        var ultimatum = scn.GetLSCard("ultimatum");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(ultimatum);
        scn.SkipToLSTurn(Phase.CONTROL);
        int lsForce = scn.GetLSForcePileCount();

        assertTrue(scn.LSCardActionAvailable(ultimatum, "Place out of play to make a player activate 2 Force"));
        scn.LSUseCardAction(ultimatum, "Place out of play to make a player activate 2 Force");
        if (scn.LSDecisionAvailable("Choose player")) {
            scn.LSChoose("Light side");
        }
        scn.PassAllResponses();

        assertEquals(Zone.OUT_OF_PLAY, ultimatum.getZone());
        assertEquals(lsForce + 2, scn.GetLSForcePileCount());
    }
}
