package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_047_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("nien", "1291_47");
                    put("xwing", "1_146"); // Red 5
                    put("han", "1_11");
                    put("luke", "1_19");
                    put("obiwan", "1_21");
                }},
                new HashMap<>() {{
                }},
                10, 10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void NienNunbSullustanSmugglerStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("nien").getBlueprint();
        assertEquals("Nien Nunb, Sullustan Smuggler", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        assertEquals(Species.SULLUSTAN, card.getSpecies());
        assertTrue(card.hasPersona(Persona.NIEN_NUNB));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void NienNunbSullustanSmugglerAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var nien = scn.GetLSCard("nien");
        var xwing = scn.GetLSCard("xwing");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), xwing);
        int power = scn.GetPower(xwing);
        scn.BoardAsPilot(xwing, nien);
        assertEquals(power + 2, scn.GetPower(xwing));
    }

    @Test
    public void NienNunbSullustanSmugglerPlacesSameCardTypeOutOfPlayToRetrieveOnceDuringYourTurn() {
        var scn = GetScenario();
        var nien = scn.GetLSCard("nien");
        var xwing = scn.GetLSCard("xwing");
        var han = scn.GetLSCard("han");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), xwing);
        scn.BoardAsPilot(xwing, nien);
        scn.MoveCardsToHand(han);
        scn.MoveCardsToTopOfLSLostPile(luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardActionAvailable(nien, "retrieve 1 Force"));
        scn.LSUseCardAction(nien, "retrieve 1 Force");
        scn.PassAllResponses();
        Vb91LightTestSupport.reach(scn, () -> scn.LSDecisionAvailable("Choose where to retrieve"));
        scn.LSChoose("Hand");
        scn.PassAllResponses();
        assertInZone(Zone.OUT_OF_PLAY, han);
        assertInZone(Zone.HAND, luke);
        scn.MoveCardsToHand(scn.GetLSCard("obiwan"));
        scn.MoveCardsToTopOfLSLostPile(luke);
        Vb91LightTestSupport.reach(scn, scn::AwaitingLSDeployPhaseActions);
        assertFalse(scn.LSCardActionAvailable(nien, "retrieve 1 Force"));
    }

    @Test
    public void NienNunbSullustanSmugglerMayNotPlaceObiWanOutOfPlay() {
        var scn = GetScenario();
        var nien = scn.GetLSCard("nien");
        var xwing = scn.GetLSCard("xwing");
        var obiwan = scn.GetLSCard("obiwan");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), xwing);
        scn.BoardAsPilot(xwing, nien);
        scn.MoveCardsToHand(obiwan);
        scn.MoveCardsToTopOfLSLostPile(luke);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardActionAvailable(nien, "retrieve 1 Force"));
    }

    @Test
    public void NienNunbSullustanSmugglerNeedsSameCardTypeAsTopOfLostPile() {
        var scn = GetScenario();
        var nien = scn.GetLSCard("nien");
        var xwing = scn.GetLSCard("xwing");
        var han = scn.GetLSCard("han");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), xwing);
        scn.BoardAsPilot(xwing, nien);
        scn.MoveCardsToHand(han);
        scn.MoveCardsToTopOfLSLostPile(scn.GetLSCard("ls-destiny-3"));
        scn.SkipToLSTurn(Phase.DEPLOY);
        boolean topIsCharacter = scn.GetTopOfLSLostPile().getBlueprint().getCardCategory() == han.getBlueprint().getCardCategory();
        assertEquals(topIsCharacter, scn.LSCardActionAvailable(nien, "retrieve 1 Force"));
    }
}
