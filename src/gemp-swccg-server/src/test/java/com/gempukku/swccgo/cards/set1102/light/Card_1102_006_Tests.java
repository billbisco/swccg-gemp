package com.gempukku.swccgo.cards.set1102.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1102_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("pod", "1102_6");
                    put("dejarik", "1_53");
                    put("holoboard", "2_63");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void EscapePodStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("pod").getBlueprint();

        assertEquals(Title.Escape_Pod, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(6, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EscapePodTakesADejarikIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var pod = scn.GetLSCard("pod");
        var dejarik = scn.GetLSCard("dejarik");

        scn.StartGame();
        scn.MoveCardsToLSHand(pod);
        assertEquals(Zone.HAND, pod.getZone());
        scn.SkipToLSTurn(Phase.CONTROL);
        assertEquals(Zone.HAND, pod.getZone());
        scn.EnsureLSForcePile(2);
        scn.MoveCardsToTopOfLSReserveDeck(dejarik);
        assertTrue(scn.LSCardPlayAvailable(pod, "Take card into hand from Reserve Deck")
                || scn.LSCardPlayAvailable(pod));
        if (scn.LSCardPlayAvailable(pod, "Take card into hand from Reserve Deck")) {
            scn.LSPlayCard(pod, "Take card into hand from Reserve Deck");
        } else {
            scn.LSPlayCard(pod);
        }
        scn.PassCardAndForceUseResponses();
        settleUsedInterrupt(scn, pod, dejarik);
        assertEquals(Zone.HAND, dejarik.getZone());
        assertTrue(pod.getZone() == Zone.USED_PILE || pod.getZone() == Zone.TOP_OF_USED_PILE);
    }

    @Test
    public void EscapePodCancelsAForceDrainAtAHolosite() {
        var scn = GetScenario();
        var pod = scn.GetLSCard("pod");
        var holoboard = scn.GetLSCard("holoboard");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(holoboard);
        scn.MoveCardsToLocation(holoboard, vader);
        scn.MoveCardsToLSHand(pod);
        assertEquals(Zone.HAND, pod.getZone());
        scn.SkipToDSTurn(Phase.CONTROL);
        assertEquals(Zone.HAND, pod.getZone());
        assertTrue(scn.DSForceDrainAvailable(holoboard));
        scn.DSForceDrainAt(holoboard);
        if (scn.DSGetDecision() != null) {
            scn.DSPass();
        }
        assertTrue(scn.LSCardPlayAvailable(pod, "Cancel Force drain")
                || scn.LSCardActionAvailable(pod, "Cancel Force drain")
                || scn.LSActionAvailable("Cancel Force drain"));
        if (scn.LSCardPlayAvailable(pod, "Cancel Force drain")) {
            scn.LSPlayCard(pod, "Cancel Force drain");
        } else if (scn.LSCardActionAvailable(pod, "Cancel Force drain")) {
            scn.LSUseCardAction(pod, "Cancel Force drain");
        } else {
            scn.LSChooseAction("Cancel Force drain");
        }
        scn.PassCardAndForceUseResponses();
        settleUsedInterrupt(scn, pod, null);
        assertTrue(pod.getZone() == Zone.USED_PILE || pod.getZone() == Zone.TOP_OF_USED_PILE);
        for (int i = 0; i < 16 && scn.IsActiveForceDrain(); i++) {
            String lsText = scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    ? scn.LSGetDecision().getText().toLowerCase() : "";
            String dsText = scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                    ? scn.DSGetDecision().getText().toLowerCase() : "";
            if (lsText.contains("optional") || lsText.contains("or pass")) {
                scn.LSPass();
                continue;
            }
            if (dsText.contains("optional") || dsText.contains("or pass")) {
                scn.DSPass();
                continue;
            }
            scn.PassAllResponses();
            scn.PassForceDrainEndResponses();
        }
        assertFalse(scn.IsActiveForceDrain());
    }

    private void settleUsedInterrupt(VirtualTableScenario scn, PhysicalCardImpl interrupt,
                                     PhysicalCardImpl chooseIfPresent) {
        for (int i = 0; i < 24; i++) {
            if (interrupt.getZone() == Zone.USED_PILE || interrupt.getZone() == Zone.TOP_OF_USED_PILE) {
                return;
            }
            if (chooseIfPresent != null && scn.LSHasCardChoiceAvailable(chooseIfPresent)) {
                scn.LSChooseCard(chooseIfPresent);
                continue;
            }
            if (scn.LSDecisionAvailable("Verify")) {
                scn.LSPass();
                continue;
            }
            if (scn.DSDecisionAvailable("Verify")) {
                scn.DSPass();
                continue;
            }
            String lsText = scn.LSGetDecision() != null && scn.LSGetDecision().getText() != null
                    ? scn.LSGetDecision().getText().toLowerCase() : "";
            String dsText = scn.DSGetDecision() != null && scn.DSGetDecision().getText() != null
                    ? scn.DSGetDecision().getText().toLowerCase() : "";
            if (lsText.contains("optional") || lsText.contains("or pass")) {
                scn.LSPass();
                continue;
            }
            if (dsText.contains("optional") || dsText.contains("or pass")) {
                scn.DSPass();
                continue;
            }
            if (lsText.contains("playing") || dsText.contains("playing")) {
                scn.PassCardPlayResponses();
                continue;
            }
            scn.PassAllResponses();
            if (interrupt.getZone() == Zone.USED_PILE || interrupt.getZone() == Zone.TOP_OF_USED_PILE) {
                return;
            }
            if (scn.LSGetDecision() != null && (lsText.contains("force") || lsText.contains("yes"))) {
                var results = scn.LSGetMultipleChoices();
                if (results != null && !results.isEmpty()) {
                    String pick = results.get(0);
                    for (String option : results) {
                        if (option.equalsIgnoreCase("Yes")) {
                            pick = option;
                            break;
                        }
                    }
                    scn.LSChoose(pick);
                    continue;
                }
            }
            return;
        }
    }
}
