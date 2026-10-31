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

public class Card_1109_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("saber", "1109_12");
                    put("mace", "1109_14");
                    put("luke", "1_19");
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
    public void JediLightsaberStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("saber").getBlueprint();
        assertEquals("Jedi Lightsaber", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.LIGHTSABER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_9, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JediLightsaberDeploysOnMace() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var mace = scn.GetLSCard("mace");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, mace);
        scn.MoveCardsToLSHand(saber);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(saber));
        scn.LSDeployCard(saber);
        scn.LSChooseCard(mace);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(mace, saber));
    }

    @Test
    public void JediLightsaberDoesNotDeployOnLuke() {
        var scn = GetScenario();
        var saber = scn.GetLSCard("saber");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(saber);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSDeployAvailable(saber));
    }
}
