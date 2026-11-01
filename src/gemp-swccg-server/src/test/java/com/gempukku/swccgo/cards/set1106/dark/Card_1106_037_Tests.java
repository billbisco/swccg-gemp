package com.gempukku.swccgo.cards.set1106.dark;

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

public class Card_1106_037_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("scoundrel", "13_27");
                    put("lando", "5_5");
                }},
                new HashMap<>() {{
                    put("nochoice", "1106_37");
                    put("trooper", "1_194");
                    put("lost", "1_194");
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
    public void IHadNoChoiceStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("nochoice").getBlueprint();

        assertEquals("I Had No Choice", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.IMMEDIATE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_6, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void IHadNoChoiceDeploysDuringOpponentsDeployPhaseOnMaintenanceLandoAndCancelsHisGameText() {
        var scn = GetScenario();
        var nochoice = scn.GetDSCard("nochoice");
        var scoundrel = scn.GetLSCard("scoundrel");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToLocation(site, scoundrel, lando);
        scn.MoveCardsToDSHand(nochoice);
        scn.EnsureDSForcePile(5);
        scn.LSPass();

        assertTrue(scn.DSCardPlayAvailable(nochoice));
        scn.DSPlayCard(nochoice);
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(scoundrel)) {
            assertFalse(scn.DSHasCardChoiceAvailable(lando));
            scn.DSChooseCard(scoundrel);
        }
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(scoundrel, nochoice));
        assertTrue(scn.game().getModifiersQuerying().getCardsMarkingGameTextCanceled(scn.gameState(), scoundrel).contains(nochoice));
    }

    @Test
    public void IHadNoChoiceMayNotDeployOutsideADeployPhase() {
        var scn = GetScenario();
        var nochoice = scn.GetDSCard("nochoice");
        var scoundrel = scn.GetLSCard("scoundrel");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToLocation(site, scoundrel);
        scn.MoveCardsToDSHand(nochoice);
        scn.EnsureDSForcePile(5);

        assertFalse(scn.DSCardPlayAvailable(nochoice));
    }

    @Test
    public void IHadNoChoiceRandomlyRetrievesOneForceDuringYourControlPhase() {
        var scn = GetScenario();
        var nochoice = scn.GetDSCard("nochoice");
        var scoundrel = scn.GetLSCard("scoundrel");
        var lost = scn.GetDSCard("lost");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, scoundrel);
        scn.AttachCardsTo(scoundrel, nochoice);
        scn.MoveCardsToTopOfDSLostPile(lost);
        scn.SkipToDSTurn(Phase.CONTROL);

        assertTrue(scn.DSCardActionAvailable(nochoice, "Randomly retrieve 1 Force"));
        scn.DSUseCardAction(nochoice, "Randomly retrieve 1 Force");
        scn.PassAllResponses();

        assertTrue(lost.getZone() == Zone.USED_PILE || lost.getZone() == Zone.TOP_OF_USED_PILE);
        if (scn.DSGetDecision() != null) {
            assertFalse(scn.DSCardActionAvailable(nochoice, "Randomly retrieve 1 Force"));
        }
    }
}
