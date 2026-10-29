package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1220_084_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("rifle", "1220_84");
                    put("boba", "1220_2");
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
    public void BobaFettsBlasterRifleStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("rifle").getBlueprint();

        assertEquals("Boba Fett's Blaster Rifle", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.BOBA_FETTS_BLASTER_RIFLE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER_RIFLE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.CLOUD_CITY);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void BobaFettsBlasterRifleCapturesWhenDestinyPlusTwoExceedsDefenseValue() {
        var scn = GetScenario();
        var rifle = scn.GetDSCard("rifle");
        var boba = scn.GetDSCard("boba");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boba, luke);
        scn.AttachCardsTo(boba, rifle);
        scn.EnsureDSForcePile(1);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(rifle, "Fire"));
        scn.PrepareDSDestiny(4);
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        scn.DSChooseSeizeCaptive();
        scn.PassAllResponses();
        assertTrue(luke.isCaptive());
        assertEquals(boba, luke.getEscort());
    }

    @Test
    public void BobaFettsBlasterRifleDoesNotCaptureWhenDestinyPlusTwoDoesNotExceedDefenseValue() {
        var scn = GetScenario();
        var rifle = scn.GetDSCard("rifle");
        var boba = scn.GetDSCard("boba");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boba, luke);
        scn.AttachCardsTo(boba, rifle);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(1);
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertFalse(luke.isCaptive());
    }

    @Test
    public void BobaFettsBlasterRifleForceDrainsArePlusOneHereWhileACaptiveAtSameSite() {
        var scn = GetScenario();
        var rifle = scn.GetDSCard("rifle");
        var boba = scn.GetDSCard("boba");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boba, luke);
        scn.AttachCardsTo(boba, rifle);
        float before = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), site, scn.DS);
        scn.CaptureCardWith(boba, luke);
        assertTrue(luke.isCaptive());
        assertEquals(boba, luke.getEscort());
        assertEquals(before + 1, scn.game().getModifiersQuerying().getForceDrainAmount(
                scn.gameState(), site, scn.DS), scn.epsilon);
    }

    @Test
    public void BobaFettsBlasterRifleForceDrainsAreUnchangedWhenNoCaptiveAtSameSite() {
        var scn = GetScenario();
        var rifle = scn.GetDSCard("rifle");
        var boba = scn.GetDSCard("boba");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boba, luke);
        scn.AttachCardsTo(boba, rifle);
        float amount = scn.game().getModifiersQuerying().getForceDrainAmount(scn.gameState(), site, scn.DS);
        assertEquals(1, amount, scn.epsilon);
    }
}
