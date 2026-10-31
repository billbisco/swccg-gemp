package com.gempukku.swccgo.cards.set1109.light;

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
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1109_014_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("mace", "1109_14");
                    put("deflection", "6_61");
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
    public void MaceWinduStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("mace").getBlueprint();
        assertEquals("Mace Windu", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(7, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(7, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.JEDI_MASTER);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.JEDI_COUNCIL_MEMBER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.MACE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.JEDI_MASTER);
            add(Icon.CORUSCANT);
            add(Icon.EPISODE_I);
            add(Icon.WARRIOR);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void MaceWinduMaySitAtASite() {
        var scn = GetScenario();
        var mace = scn.GetLSCard("mace");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, mace);
        assertAtLocation(site, mace);
    }

    @Test
    public void MaceWinduTakesBlasterDeflectionFromReserveDeck() {
        var scn = GetScenario();
        var mace = scn.GetLSCard("mace");
        var deflection = scn.GetLSCard("deflection");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, mace);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfLSReserveDeck(deflection);
        assertTrue(scn.LSCardActionAvailable(mace, "Take Blaster Deflection into hand from Reserve Deck"));
        scn.LSUseCardAction(mace, "Take Blaster Deflection into hand from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        if (scn.LSHasCardChoiceAvailable(deflection)) {
            scn.LSChooseCard(deflection);
        }
        scn.PassAllResponses();
        assertEquals(Zone.HAND, deflection.getZone());
    }
}
