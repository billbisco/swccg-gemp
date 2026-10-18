package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1230_072_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("laudica", "1230_72");
                    put("rayc", "6_34");
                    put("squadron", "9_39");
                    put("junk", "1_17");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("tonnika", "1_195");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                new StartingSetup.StartingLocation("1_284"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void LaudicaStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("laudica").getBlueprint();

        assertEquals("Laudica", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(1, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FEMALE);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.LAUDICA);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.JABBAS_PALACE);
            add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LaudicaPowerPlusThreeWhileRaycOnTable() {
        var scn = GetScenario();
        var laudica = scn.GetLSCard("laudica");
        var rayc = scn.GetLSCard("rayc");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, laudica);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(2, scn.GetPower(laudica));

        scn.MoveCardsToLocation(site, rayc);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(5, scn.GetPower(laudica));
    }

    @Test
    public void LaudicaPowerPlusThreeWhileTonnikaSistersOnTable() {
        var scn = GetScenario();
        var laudica = scn.GetLSCard("laudica");
        var tonnika = scn.GetDSCard("tonnika");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, laudica);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(2, scn.GetPower(laudica));
        scn.MoveCardsToLocation(site, tonnika);
        scn.SkipToPhase(Phase.CONTROL);

        assertEquals(5, scn.GetPower(laudica));
    }

    @Test
    public void LaudicaRetrievesForceWhenOpponentDrainsAtNonBattleground() {
        var scn = GetScenario();
        var laudica = scn.GetLSCard("laudica");
        var junk = scn.GetLSCard("junk");
        var trooper = scn.GetDSCard("trooper");
        var detention = scn.GetDSStartingLocation();
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, laudica);
        scn.MoveCardsToLocation(detention, trooper);
        scn.MoveCardsToTopOfLSLostPile(junk);
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.DSForceDrainAvailable(detention));
        scn.DSForceDrainAt(detention);
        assertTrue(scn.LSCardActionAvailable(laudica, "Retrieve"));
        scn.LSUseCardAction(laudica, "Retrieve");
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, junk);
    }

    @Test
    public void LaudicaDeploysMatchingEffectFromReserveOncePerPhase() {
        var scn = GetScenario();
        var laudica = scn.GetLSCard("laudica");
        var squadron = scn.GetLSCard("squadron");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, laudica);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(squadron);
        assertTrue(scn.LSCardActionAvailable(laudica, "Deploy Effect"));
        scn.LSUseCardAction(laudica, "Deploy Effect");
        assertTrue(scn.LSDecisionAvailable("Choose card"));
        scn.LSChooseCard(squadron);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, squadron);
    }
}
