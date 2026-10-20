package com.gempukku.swccgo.cards.set1281.light;

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

public class Card_1281_039_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rest", "1281_39");
                    put("narshaddaa", "200_58");
                    put("leia", "1_17");
                    put("lando", "601_50");
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
    public void NarShaddaaScoundrelsRestStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rest").getBlueprint();

        assertEquals("Nar Shaddaa: Scoundrel's Rest", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void NarShaddaaScoundrelsRestDeploysOnNarShaddaa() {
        var scn = GetScenario();
        var rest = scn.GetLSCard("rest");
        var narshaddaa = scn.GetLSCard("narshaddaa");

        scn.StartGame();
        scn.MoveCardsToLSHand(narshaddaa, rest);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(narshaddaa));
        scn.LSDeployLocation(narshaddaa);
        scn.PassAllResponses();
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.LSDeployAvailable(rest));
        scn.LSDeployLocation(rest);
        scn.PassAllResponses();

        assertEquals(Zone.LOCATIONS, rest.getZone());
        assertEquals("Nar Shaddaa", rest.getPartOfSystem());
    }

    @Test
    public void NarShaddaaScoundrelsRestCharactersAreDeployPlusOneExceptScoundrels() {
        var scn = GetScenario();
        var rest = scn.GetLSCard("rest");
        var narshaddaa = scn.GetLSCard("narshaddaa");
        var leia = scn.GetLSCard("leia");
        var lando = scn.GetLSCard("lando");

        scn.StartGame();
        scn.MoveLocationToTable(narshaddaa);
        scn.MoveLocationToTable(rest);
        scn.MoveCardsToLSHand(leia, lando);
        scn.SkipToLSTurn(Phase.DEPLOY);

        int leiaPrinted = Math.round(leia.getBlueprint().getDeployCost());
        int landoPrinted = Math.round(lando.getBlueprint().getDeployCost());
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSDeployAvailable(leia));
        scn.LSDeployCard(leia);
        scn.LSChooseCard(rest);
        scn.PassAllResponses();
        scn.DSPass();
        assertEquals(force - (leiaPrinted + 1), scn.GetLSForcePileCount());
        assertEquals(rest, leia.getAtLocation());

        force = scn.GetLSForcePileCount();
        assertTrue(scn.LSDeployAvailable(lando));
        scn.LSDeployCard(lando);
        scn.LSChooseCard(rest);
        scn.PassAllResponses();
        assertEquals(force - landoPrinted, scn.GetLSForcePileCount());
        assertEquals(rest, lando.getAtLocation());
    }

    @Test
    public void NarShaddaaScoundrelsRestDarkOccupyCancelsOpponentsGameText() {
        var scn = GetScenario();
        var rest = scn.GetLSCard("rest");
        var narshaddaa = scn.GetLSCard("narshaddaa");
        var leia = scn.GetLSCard("leia");
        var trooper = scn.GetDSCard("trooper");

        scn.StartGame();
        scn.MoveLocationToTable(narshaddaa);
        scn.MoveLocationToTable(rest);
        scn.MoveCardsToLocation(rest, trooper);
        scn.MoveCardsToLSHand(leia);
        scn.SkipToLSTurn(Phase.DEPLOY);

        int leiaPrinted = Math.round(leia.getBlueprint().getDeployCost());
        int force = scn.GetLSForcePileCount();
        assertTrue(scn.LSDeployAvailable(leia));
        scn.LSDeployCard(leia);
        scn.LSChooseCard(rest);
        scn.PassAllResponses();
        assertEquals(force - leiaPrinted, scn.GetLSForcePileCount());
    }
}
