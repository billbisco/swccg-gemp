package com.gempukku.swccgo.cards.set1111.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
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
import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1111_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("meditation", "1111_9");
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
    public void MeditationStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("meditation").getBlueprint();

        assertEquals("Meditation", card.getTitle());
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
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_11, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MeditationDeploysOnTableForTwoForce() {
        var scn = GetScenario();
        var meditation = scn.GetLSCard("meditation");

        scn.StartGame();
        scn.MoveCardsToLSHand(meditation);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(4);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardPlayAvailable(meditation) || scn.LSDeployAvailable(meditation));
        if (scn.LSDeployAvailable(meditation)) {
            scn.LSDeployCard(meditation);
        } else {
            scn.LSPlayCard(meditation);
        }
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, meditation);
        assertEquals(force - 2, scn.GetLSForcePileCount());
    }

    @Test
    public void MeditationPlacesInLostPileToActivateOneForce() {
        var scn = GetScenario();
        var meditation = scn.GetLSCard("meditation");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(meditation);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(2);
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSCardActionAvailable(meditation, "Place in Lost Pile to activate 1 Force"));
        scn.LSUseCardAction(meditation, "Place in Lost Pile to activate 1 Force");
        scn.PassAllResponses();
        assertTrue(meditation.getZone() == Zone.LOST_PILE || meditation.getZone() == Zone.TOP_OF_LOST_PILE);
        assertEquals(force + 1, scn.GetLSForcePileCount());
    }
}
