package com.gempukku.swccgo.cards.set1103.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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

public class Card_1103_037_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("ito", "1103_37");
                    put("escort", "1_194");
                    put("hypo", "2_112");
                    put("bolt", "1_205");
                    put("farm", "1_294");
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
    public void ITOStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("ito").getBlueprint();

        assertEquals("IT-O (Eyetee-Oh)", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(0, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DROID);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DROID);
            add(Icon.A_NEW_HOPE);
        }});
        assertTrue(card.getModelTypes().contains(ModelType.INTERROGATOR));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ITOAddsTwoToForceDrainsWhereverYouHaveACaptive() {
        var scn = GetScenario();
        var ito = scn.GetDSCard("ito");
        var escort = scn.GetDSCard("escort");
        var luke = scn.GetLSCard("luke");
        var farm = scn.GetDSCard("farm");
        var otherSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(otherSite, ito);
        scn.MoveCardsToLocation(farm, escort);
        scn.CaptureCardWith(escort, luke);
        scn.EnsureLSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(farm));
        scn.DSForceDrainAt(farm);
        scn.LSPass();
        scn.PassForceDrainStartResponses();
        assertEquals(4, scn.GetForceDrainTotal());
    }

    @Test
    public void ITODeploysHypoFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var ito = scn.GetDSCard("ito");
        var hypo = scn.GetDSCard("hypo");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, ito);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(hypo);

        assertTrue(scn.DSCardActionAvailable(ito, "Deploy Hypo from Reserve Deck"));
        scn.DSUseCardAction(ito, "Deploy Hypo from Reserve Deck");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(hypo);
        scn.PassAllResponses();
        assertEquals(ito, hypo.getAttachedTo());
        assertFalse(scn.GetDSAvailableActions().toString().contains("Deploy Hypo from Reserve Deck"));
    }

    @Test
    public void ITOIsImmuneToRestrainingBoltAndAttritionLessThanThree() {
        var scn = GetScenario();
        var ito = scn.GetDSCard("ito");
        var bolt = scn.GetDSCard("bolt");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, ito);
        scn.MoveCardsToDSHand(bolt);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), ito), scn.epsilon);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardPlayAvailable(bolt));
    }
}
