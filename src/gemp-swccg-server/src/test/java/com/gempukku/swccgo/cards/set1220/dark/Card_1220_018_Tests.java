package com.gempukku.swccgo.cards.set1220.dark;

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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1220_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("mkae", "1220_18");
                    put("devastator", "1_301");
                    put("tatooine", "1_289");
                    put("generators", "3_94");
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
    public void WarrantOfficerMKaeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("mkae").getBlueprint();

        assertEquals("Warrant Officer M'Kae", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.PILOT);
            add(Icon.DAGOBAH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WarrantOfficerMKaeAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var mkae = scn.GetDSCard("mkae");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.BoardAsPilot(devastator, mkae);
        assertEquals(11, scn.GetPower(devastator));
    }

    @Test
    public void WarrantOfficerMKaeDeploysAStarshipDeviceFromReserveDeck() {
        var scn = GetScenario();
        var mkae = scn.GetDSCard("mkae");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");
        var generators = scn.GetDSCard("generators");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), mkae);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(generators);
        assertTrue(scn.DSCardActionAvailable(mkae, "Deploy a starship device from Reserve Deck"));
        scn.DSUseCardAction(mkae, "Deploy a starship device from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(generators);
        scn.PassAllResponses();
        if (scn.DSHasCardChoiceAvailable(devastator)) {
            scn.DSChooseCard(devastator);
            scn.PassAllResponses();
        }
        assertEquals(devastator, generators.getAttachedTo());
    }

    @Test
    public void WarrantOfficerMKaePlacesDevicesAboardInUsedPileWhenStarshipAboutToBeLost() {
        var scn = GetScenario();
        var mkae = scn.GetDSCard("mkae");
        var devastator = scn.GetDSCard("devastator");
        var tatooine = scn.GetDSCard("tatooine");
        var generators = scn.GetDSCard("generators");
        var luke = scn.GetLSCard("luke");
        var xwing = scn.GetLSCard("xwing");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, devastator, xwing);
        scn.BoardAsPilot(devastator, mkae);
        scn.BoardAsPilot(xwing, luke);
        scn.AttachCardsTo(devastator, generators);
        assertEquals(devastator, generators.getAttachedTo());
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSExecuteAdHocEffect(devastator, new LoseCardFromTableEffect(
                new TopLevelGameTextAction(devastator, devastator.getCardId()), devastator));
        if (scn.DSHasCardChoiceAvailable(devastator)) {
            scn.DSChooseCard(devastator);
        }
        int safety = 0;
        while (safety++ < 12 && !scn.GetDSAvailableActions().toString().contains("Place devices aboard in Used Pile")
                && !scn.GetDSAvailableActions().toString().contains("Used Pile")) {
            if (scn.DSDecisionAvailable("optional") || scn.DSDecisionAvailable("Optional")
                    || (scn.DSGetDecision() != null && scn.GetDSAvailableActions().isEmpty())) {
                scn.DSPass();
            } else if (scn.LSGetDecision() != null) {
                scn.LSPass();
            } else {
                break;
            }
        }
        assertTrue("salvage missing. decision=" + scn.DSGetDecision()
                        + " actions=" + scn.GetDSAvailableActions()
                        + " generators zone=" + generators.getZone()
                        + " attachedTo=" + generators.getAttachedTo(),
                scn.GetDSAvailableActions().toString().contains("Place devices aboard in Used Pile")
                        || scn.GetDSAvailableActions().toString().contains("Used Pile"));
        if (scn.GetDSAvailableActions().toString().contains("Place devices aboard in Used Pile")) {
            scn.DSChooseAction("Place devices aboard in Used Pile");
        } else {
            scn.DSChooseAction("Used Pile");
        }
        scn.PassAllResponses();
        assertTrue("generators zone=" + generators.getZone() + " attachedTo=" + generators.getAttachedTo(),
                generators.getZone() == Zone.USED_PILE || generators.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
