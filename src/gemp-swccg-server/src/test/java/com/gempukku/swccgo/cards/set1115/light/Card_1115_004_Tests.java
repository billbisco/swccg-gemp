package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1115_004_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("artoo", "1115_4");
                    put("plans", "1_046");
                    put("trash", "1_125");
                    put("ds", "7_117");
                    put("undercover", "2_40");
                }},
                new HashMap<>() {{
                    put("emperor", "9_109");
                    put("stormtrooper", "1_194");
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
    public void ArtooStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("artoo").getBlueprint();

        assertEquals("Artoo", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getAlternateDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.SPY);
            add(Keyword.HEROIC);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.NAV_COMPUTER);
            add(Icon.JABBAS_PALACE);
}});
        assertTrue(card.getModelTypes().contains(ModelType.ASTROMECH));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ArtooMakesDeathStarPlansIgnoreObjectiveAndDrawOneDestiny() {
        var scn = GetScenario();
        var artoo = scn.GetLSCard("artoo");
        var plans = scn.GetLSCard("plans");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, artoo);
        scn.AttachCardsTo(site, plans);
        assertTrue(scn.game().getModifiersQuerying().hasGameTextModification(scn.gameState(), plans,
                ModifyGameTextType.LEGACY__ARTOO_V__DEATH_STAR_PLANS_IGNORES_OBJECTIVE_DRAWS_ONE));
    }

    @Test
    public void ArtooDeploysTrashCompactorFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var artoo = scn.GetLSCard("artoo");
        var trash = scn.GetLSCard("trash");
        var ds = scn.GetLSCard("ds");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, artoo);
        scn.MoveLocationToTable(ds);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(trash);
        assertTrue(scn.LSCardActionAvailable(artoo, "Deploy Trash Compactor from Reserve Deck"));
        scn.LSUseCardAction(artoo, "Deploy Trash Compactor from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.LSChooseCard(trash);
        scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS, trash.getZone());
    }

    @Test
    public void ArtooIsImmuneToUndercover() {
        var scn = GetScenario();
        var artoo = scn.GetLSCard("artoo");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, artoo);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), artoo, Title.Undercover));
    }
}
