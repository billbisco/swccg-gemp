package com.gempukku.swccgo.cards.set1281.dark;

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

public class Card_1281_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("cantina", "1_128"); // Tatooine: Cantina
                    put("db94", "1_129"); // Tatooine: Docking Bay 94 (adjacent to the Cantina)
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("jetpack", "1281_8");
                    put("bossk", "110_5"); // Bossk With Mortar Gun: alien warrior
                    put("jodo", "110_9"); // Jodo Kast: alien warrior
                    put("rtic", "1281_6"); // R'tic H'weei: alien, not a warrior
                    put("rifle", "1_312"); // Blaster Rifle
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
    public void JetPackVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("jetpack").getBlueprint();
        assertEquals("Jet Pack", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.DEVICE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEVICE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void JetPackVUsesTwoForceToDeployOnYourAlienWarriorOnly() {
        var scn = GetScenario();
        var jetpack = scn.GetDSCard("jetpack");
        var bossk = scn.GetDSCard("bossk");
        var rtic = scn.GetDSCard("rtic");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, bossk, rtic);
        scn.MoveCardsToHand(jetpack);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(4);
        int force = scn.GetDSForcePile().size();
        assertTrue(scn.DSCardPlayAvailable(jetpack));
        scn.DSDeployCard(jetpack);
        assertTrue(scn.DSHasCardChoiceAvailable(bossk));
        assertFalse(scn.DSHasCardChoiceAvailable(rtic));
        scn.DSChooseCard(bossk);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(bossk, jetpack));
        assertEquals(force - 2, scn.GetDSForcePile().size());
    }

    @Test
    public void JetPackVDeploysFreeOnJodoKast() {
        var scn = GetScenario();
        var jetpack = scn.GetDSCard("jetpack");
        var jodo = scn.GetDSCard("jodo");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, jodo);
        scn.MoveCardsToHand(jetpack);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.DSActivateForceCheat(4);
        int force = scn.GetDSForcePile().size();
        scn.DSDeployCard(jetpack);
        if (scn.DSHasCardChoiceAvailable(jodo)) {
            scn.DSChooseCard(jodo);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(jodo, jetpack));
        assertEquals(force, scn.GetDSForcePile().size());
    }

    @Test
    public void JetPackVCharacterFliesAndMayFireWeaponsForFree() {
        var scn = GetScenario();
        var jetpack = scn.GetDSCard("jetpack");
        var bossk = scn.GetDSCard("bossk");
        var rifle = scn.GetDSCard("rifle");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, bossk);
        scn.AttachCardsTo(bossk, rifle);
        assertFalse(scn.game().getModifiersQuerying().mayFireWeaponFiredByForFree(scn.gameState(), bossk, rifle));
        assertEquals(0, scn.GetManeuver(bossk));
        scn.AttachCardsTo(bossk, jetpack);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(3, scn.GetLandspeed(bossk));
        assertEquals(6, scn.GetManeuver(bossk));
        assertTrue(scn.game().getModifiersQuerying().mayFireWeaponFiredByForFree(scn.gameState(), bossk, rifle));
    }

    @Test
    public void JetPackVCharacterMayMoveAsAReactForPlusOneForce() {
        var scn = GetScenario();
        var jetpack = scn.GetDSCard("jetpack");
        var bossk = scn.GetDSCard("bossk");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var cantina = scn.GetLSCard("cantina");
        var db94 = scn.GetLSCard("db94");
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        scn.MoveLocationToTable(db94);
        scn.MoveCardsToLocation(cantina, bossk);
        scn.AttachCardsTo(bossk, jetpack);
        scn.MoveCardsToLocation(db94, luke, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.DSActivateForceCheat(4);
        int force = scn.GetDSForcePile().size();
        scn.LSInitiateBattle(db94);
        assertTrue(scn.DSDecisionAvailable("Battle just initiated"));
        assertTrue(scn.DSCardActionAvailable(jetpack, "Move as a 'react'"));
        scn.DSUseCardAction(jetpack, "Move as a 'react'");
        if (scn.DSHasCardChoiceAvailable(db94)) {
            scn.DSChooseCard(db94);
        }
        scn.PassAllResponses();
        assertEquals(db94, bossk.getAtLocation());
        assertEquals(force - 2, scn.GetDSForcePile().size());
    }
}
