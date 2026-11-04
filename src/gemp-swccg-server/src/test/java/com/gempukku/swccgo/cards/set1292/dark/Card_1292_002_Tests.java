package com.gempukku.swccgo.cards.set1292.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1292_002_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("jarjar", "11_4");
                    put("trooper", "1_28");
                    put("beggar", "1_44");
                    put("thiefskill", "2_36");
                }},
                new HashMap<>() {{
                    put("toonbuck", "1292_2");
                    put("council", "1291_19");
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
    public void ToonbuckTooraVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("toonbuck").getBlueprint();

        assertEquals("Toonbuck Toora", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getPolitics(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.FEMALE);
            add(Keyword.SENATOR);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.EPISODE_I);
            add(Icon.SEPARATIST);
            add(Icon.CORUSCANT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ToonbuckTooraVAtCouncilRoomJarJarMayNotDeployThere() {
        var scn = GetScenario();
        var toonbuck = scn.GetDSCard("toonbuck");
        var council = scn.GetDSCard("council");
        var jarjar = scn.GetLSCard("jarjar");
        var trooper = scn.GetLSCard("trooper");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(council);
        scn.MoveCardsToLocation(council, toonbuck, trooper);
        scn.MoveCardsToLSHand(jarjar);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(10);

        assertTrue(scn.LSDeployAvailable(jarjar));
        scn.LSDeployCard(jarjar);
        assertTrue(scn.LSHasCardChoiceAvailable(lsSite));
        assertFalse(scn.LSHasCardChoiceAvailable(council));
    }

    @Test
    public void ToonbuckTooraVElsewhereJarJarMayDeployToCouncilRoom() {
        var scn = GetScenario();
        var toonbuck = scn.GetDSCard("toonbuck");
        var council = scn.GetDSCard("council");
        var jarjar = scn.GetLSCard("jarjar");
        var trooper = scn.GetLSCard("trooper");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(council);
        scn.MoveCardsToLocation(council, trooper);
        scn.MoveCardsToLocation(dsSite, toonbuck);
        scn.MoveCardsToLSHand(jarjar);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(10);

        assertTrue(scn.LSDeployAvailable(jarjar));
        scn.LSDeployCard(jarjar);
        assertTrue(scn.LSHasCardChoiceAvailable(council));
    }

    @Test
    public void ToonbuckTooraVAtCouncilRoomSuspendsOpponentsNonImmuneEffectsOnThatSite() {
        var scn = GetScenario();
        var toonbuck = scn.GetDSCard("toonbuck");
        var council = scn.GetDSCard("council");
        var beggar = scn.GetLSCard("beggar");
        var thiefskill = scn.GetLSCard("thiefskill");

        scn.StartGame();
        scn.MoveLocationToTable(council);
        scn.AttachCardsTo(council, beggar, thiefskill);
        scn.MoveCardsToLocation(council, toonbuck);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(beggar.isSuspended());
        assertFalse(thiefskill.isSuspended());
    }

    @Test
    public void ToonbuckTooraVElsewhereDoesNotSuspendEffectsOnCouncilRoom() {
        var scn = GetScenario();
        var toonbuck = scn.GetDSCard("toonbuck");
        var council = scn.GetDSCard("council");
        var beggar = scn.GetLSCard("beggar");
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(council);
        scn.AttachCardsTo(council, beggar);
        scn.MoveCardsToLocation(dsSite, toonbuck);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertFalse(beggar.isSuspended());
    }
}
