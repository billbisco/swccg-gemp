package com.gempukku.swccgo.cards.set1282.light;

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

public class Card_1282_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("corridor", "1282_28");
                    put("hoth", "3_55"); // Hoth system
                    put("generators", "3_61"); // Hoth: Main Power Generators (1st Marker)
                    put("rieekan", "3_10"); // General Carlist Rieekan: a [Hoth] general
                    put("chasm", "5_19"); // Chasm (Cloud City Effect)
                    put("shieldchasm", "1200_7"); // Chasm (V) Defensive Shield
                }},
                new HashMap<>() {{
                    put("command", "1230_39"); // Well-earned Command (V): deploy on a battleground site
                    put("image", "1104_38"); // Image Of The Dark Lord (V): deploy on a planet site
                }},
                10, 30,
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
    public void HothEchoCorridorVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("corridor").getBlueprint();

        assertEquals("Hoth: Echo Corridor", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.UNDERGROUND);
            add(Icon.INTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SCOMP_LINK);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(0, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HothEchoCorridorVOncePerGameDeploysAHothGeneralHereFromReserveDeck() {
        var scn = GetScenario();
        var corridor = scn.GetLSCard("corridor");
        var hoth = scn.GetLSCard("hoth");
        var rieekan = scn.GetLSCard("rieekan");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveLocationToTable(corridor);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(6);
        scn.MoveCardsToBottomOfLSReserveDeck(rieekan);

        assertTrue(scn.LSCardActionAvailable(corridor, "general"));
        scn.LSUseCardAction(corridor, "general");
        scn.PassAllResponses();
        if (scn.LSAnyDecisionsAvailable() && scn.LSHasCardChoiceAvailable(rieekan)) {
            scn.LSChooseCard(rieekan);
        }
        scn.PassAllResponses();

        assertEquals(corridor, rieekan.getAtLocation());
        assertFalse(LegacyVb82TestSupport.passUntilLSCardAction(scn, corridor, "general"));
    }

    @Test
    public void HothEchoCorridorVCancelsNonDefensiveShieldChasmOnTable() {
        var scn = GetScenario();
        var corridor = scn.GetLSCard("corridor");
        var hoth = scn.GetLSCard("hoth");
        var chasm = scn.GetLSCard("chasm");
        var shieldchasm = scn.GetLSCard("shieldchasm");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveCardsToLSSideOfTable(chasm, shieldchasm);
        scn.MoveCardsToLSHand(corridor);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(corridor));
        scn.LSDeployLocation(corridor);
        scn.PassAllResponses();

        assertEquals(Zone.LOST_PILE, chasm.getZone() == Zone.TOP_OF_LOST_PILE ? Zone.LOST_PILE : chasm.getZone());
        assertEquals(Zone.SIDE_OF_TABLE, shieldchasm.getZone());
    }

    @Test
    public void HothEchoCorridorVCancelsChasmBeingDeployed() {
        var scn = GetScenario();
        var corridor = scn.GetLSCard("corridor");
        var hoth = scn.GetLSCard("hoth");
        var chasm = scn.GetLSCard("chasm");

        scn.StartGame();
        scn.MoveLocationToTable(hoth);
        scn.MoveLocationToTable(corridor);
        scn.MoveCardsToLSHand(chasm);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(chasm));
        scn.LSDeployCard(chasm);
        scn.PassAllResponses();

        assertFalse(chasm.getZone() == Zone.SIDE_OF_TABLE);
    }

    private boolean grantedToDeployHere(VirtualTableScenario scn, String dsEffectKey) {
        return scn.game().getModifiersQuerying().isGrantedToDeployTo(scn.gameState(), scn.GetDSCard(dsEffectKey), scn.GetLSCard("corridor"), null);
    }

    private void setUpHoth(VirtualTableScenario scn, boolean firstMarkerOnTable) {
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("hoth"));
        if (firstMarkerOnTable) {
            scn.MoveLocationToTable(scn.GetLSCard("generators"));
        }
        scn.MoveLocationToTable(scn.GetLSCard("corridor"));
        scn.MoveCardsToDSHand(scn.GetDSCard("image"), scn.GetDSCard("command"));
    }

    @Test
    public void HothEchoCorridorVWhile1stMarkerOnTableLetsOpponentsEffectsThatMayDeployOnItDeployHere() {
        var scn = GetScenario();
        setUpHoth(scn, true);
        // Image Of The Dark Lord (V) may deploy on a planet site, so on the 1st Marker
        assertTrue(grantedToDeployHere(scn, "image"));
    }

    @Test
    public void HothEchoCorridorVDoesNotGrantEffectsThatMayNotDeployOn1stMarker() {
        var scn = GetScenario();
        setUpHoth(scn, true);
        // Well-earned Command (V) deploys only on a battleground site; a shielded 1st Marker is not one
        assertFalse(grantedToDeployHere(scn, "command"));
    }

    @Test
    public void HothEchoCorridorVGrantsNothingWithout1stMarkerOnTable() {
        var scn = GetScenario();
        setUpHoth(scn, false);
        assertFalse(grantedToDeployHere(scn, "image"));
    }

    @Test
    public void HothEchoCorridorVOpponentDeploysTheGrantedEffectHere() {
        var scn = GetScenario();
        var corridor = scn.GetLSCard("corridor");
        var image = scn.GetDSCard("image");

        setUpHoth(scn, true);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(6);
        assertTrue(scn.DSDeployAvailable(image));
        scn.DSDeployCard(image);
        assertTrue(scn.DSHasCardChoiceAvailable(corridor));
        scn.DSChooseCard(corridor);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(corridor, image));
    }
}
