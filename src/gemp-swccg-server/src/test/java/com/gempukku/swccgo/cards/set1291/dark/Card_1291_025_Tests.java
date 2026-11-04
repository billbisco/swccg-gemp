package com.gempukku.swccgo.cards.set1291.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_025_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("bruckman", "8_28");
                }},
                new HashMap<>() {{
                    put("outpost", "1291_25");
                    put("stormtrooper", "1_194");
                    put("vader", "1_168");
                    put("expand", "1_215");
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
    public void TatooineImperialOutpostStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("outpost").getBlueprint();

        assertEquals("Tatooine: Imperial Outpost", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }


    @Test
    public void TatooineImperialOutpostDarkMayActivate1ForceOncePerTurnIfImperialHere() {
        var scn = GetScenario();
        var outpost = scn.GetDSCard("outpost");
        var stormtrooper = scn.GetDSCard("stormtrooper");

        scn.StartGame();
        scn.MoveLocationToTable(outpost);
        scn.MoveCardsToLocation(outpost, stormtrooper);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardActionAvailable(outpost, "Activate 1 Force"));
        int before = scn.GetDSForcePileCount();
        scn.DSUseCardAction(outpost, "Activate 1 Force");
        scn.PassAllResponses();
        assertEquals(before + 1, scn.GetDSForcePileCount());

        scn.LSPass();
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSCardActionAvailable(outpost, "Activate 1 Force"));
    }

    @Test
    public void TatooineImperialOutpostDarkMayNotActivateWithoutImperialHere() {
        var scn = GetScenario();
        var outpost = scn.GetDSCard("outpost");
        var stormtrooper = scn.GetDSCard("stormtrooper");

        scn.StartGame();
        scn.MoveLocationToTable(outpost);
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), stormtrooper);
        scn.SkipToDSTurn(Phase.DEPLOY);

        assertFalse(scn.DSCardActionAvailable(outpost, "Activate 1 Force"));
    }

    @Test
    public void TatooineImperialOutpostIsImmuneToExpandTheEmpire() {
        var scn = GetScenario();
        var outpost = scn.GetDSCard("outpost");
        var expand = scn.GetDSCard("expand");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(outpost);
        scn.MoveCardsToHand(expand);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(expand));
        scn.DSDeployCard(expand);

        assertTrue(scn.DSHasCardChoiceAvailable(site));
        assertFalse(scn.DSHasCardChoiceAvailable(outpost));
    }

    @Test
    public void TatooineImperialOutpostLightCharacterLandspeedMovementFromHereRequiresPlus1ExceptScouts() {
        var scn = GetScenario();
        var outpost = scn.GetDSCard("outpost");
        var luke = scn.GetLSCard("luke");
        var bruckman = scn.GetLSCard("bruckman");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();
        var mq = scn.game().getModifiersQuerying();

        scn.StartGame();
        scn.MoveLocationToTable(outpost);
        scn.MoveCardsToLocation(outpost, luke, bruckman, vader);

        assertEquals(2, Math.round(mq.getMoveUsingLandspeedCost(scn.gameState(), luke, outpost, site, false, 0)));
        assertEquals(1, Math.round(mq.getMoveUsingLandspeedCost(scn.gameState(), bruckman, outpost, site, false, 0)));
        assertEquals(1, Math.round(mq.getMoveUsingLandspeedCost(scn.gameState(), vader, outpost, site, false, 0)));
    }
}
