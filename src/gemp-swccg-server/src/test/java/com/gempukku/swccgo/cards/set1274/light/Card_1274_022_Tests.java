package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_022_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("yavin", "1274_22");
                    put("oldyavin", "1_135");
                    put("restore", "1271_46");
                    put("foothold", "1274_20");
                    put("foothold2", "1274_20");
                    put("xwing", "1_146");
                }},
                new HashMap<>() {{
                    put("lateral", "1_222");
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
    public void Yavin4VStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("yavin").getBlueprint();

        assertEquals("Yavin 4", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getParsec());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SYSTEM, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.PLANET);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Yavin4VDeploysEffectOnYavin4FromReserveDeckOncePerGameWithCriticalMissionHere() {
        var scn = GetScenario();
        var yavin = scn.GetLSCard("yavin");
        var restore = scn.GetLSCard("restore");
        var foothold = scn.GetLSCard("foothold");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.AttachCardsTo(yavin, restore);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfOwnReserveDeck(foothold);
        assertTrue(scn.LSCardActionAvailable(yavin, "Deploy an Effect from Reserve Deck"));
        scn.LSUseCardAction(yavin, "Deploy an Effect from Reserve Deck");
        scn.LSChooseCard(foothold);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(yavin, foothold));

        // Once per game
        scn.MoveCardsToTopOfOwnReserveDeck(scn.GetLSCard("foothold2"));
        if (scn.AwaitingDSDeployPhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(yavin, "Deploy an Effect from Reserve Deck"));
    }

    @Test
    public void Yavin4VMayNotDeployEffectWithoutCriticalMissionHere() {
        var scn = GetScenario();
        var yavin = scn.GetLSCard("yavin");
        var foothold = scn.GetLSCard("foothold");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfOwnReserveDeck(foothold);
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(yavin, "Deploy an Effect from Reserve Deck"));
    }

    @Test
    public void Yavin4VAndStarshipsHereAreImmuneToLateralDamage() {
        var scn = GetScenario();
        var yavin = scn.GetLSCard("yavin");
        var xwing = scn.GetLSCard("xwing");
        var lateral = scn.GetDSCard("lateral");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLocation(yavin, xwing);
        scn.MoveCardsToHand(lateral);
        var querying = scn.game().getModifiersQuerying();
        assertTrue(querying.isImmuneToCardTitle(scn.gameState(), yavin, Title.Lateral_Damage));
        assertTrue(querying.isImmuneToCardTitle(scn.gameState(), xwing, Title.Lateral_Damage));

        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertFalse(scn.DSDeployAvailable(lateral));
    }

    @Test
    public void Yavin4ControlLateralDamageMayDeployOnNonVYavin4() {
        var scn = GetScenario();
        var oldYavin = scn.GetLSCard("oldyavin");
        var xwing = scn.GetLSCard("xwing");
        var lateral = scn.GetDSCard("lateral");

        scn.StartGame();
        scn.MoveLocationToTable(oldYavin);
        scn.MoveCardsToLocation(oldYavin, xwing);
        scn.MoveCardsToHand(lateral);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.AwaitingDSDeployPhaseActions());
        assertTrue(scn.DSDeployAvailable(lateral));
    }
}
