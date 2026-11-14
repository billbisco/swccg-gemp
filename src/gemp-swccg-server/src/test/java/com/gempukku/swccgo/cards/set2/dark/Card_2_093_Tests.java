package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.framework.StartingSetup;
import org.junit.Test;

import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.*;
import static org.junit.Assert.*;

public class Card_2_093_Tests
{
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("chewie", "2_3");
                    put("threepio", "1_5");
                    put("slave_leia", "6_32");
                }},
                new HashMap<>()
                {{
                    put("it-o", "2_093");
                    put("boba", "5_91");

                }},
                10,
                10,
                StartingSetup.DefaultLSGroundLocation,
                new StartingSetup.StartingLocation("1_284"), //Detention Block Corridor
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void ITOStatsAndKeywordsAreCorrect() {
        /**
         * Title: IT-O (Eyetee-Oh)
         * Uniqueness: UNIQUE
         * Side: Dark
         * Type: Character
         * Subtype: Droid
         * Destiny: 2
         * Deploy: 3
         * Power: 4
         * Forfeit: 1
         * Icons: Warrior
         * Game Text: When at Detention Block Corridor, adds X to your Force drains here, where X = number of captives here.
         *         Immune to Restraining Bolt.
         * Lore: Floating prisoner interrogation droid. Uses probes and needles to dispense truth drugs and perform 'surgery.'
         *         Sensors determine subject's pain threshold and truthfulness.
         * Set: A New Hope
         * Rarity: R1
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("it-o").getBlueprint();

        assertEquals(Title.IT0, card.getTitle());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertTrue(card.isCardType(CardType.DROID));
        //assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(0, card.getAbility(), scn.epsilon);
        assertEquals(1, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getIconCount(Icon.A_NEW_HOPE));
        assertTrue(card.getModelTypes().contains(ModelType.INTERROGATOR));
    }

    @Test
    public void ITOAdds3ToForceDrainsAtDetentionBlockCorridorWhenPresentWith3Captives() {
        var scn = GetScenario();

        var chewie = scn.GetLSCard("chewie");
        var leia = scn.GetLSCard("slave_leia");
        var threepio = scn.GetLSCard("threepio");

        var ito = scn.GetDSCard("it-o");
        var boba = scn.GetDSCard("boba");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();

        scn.MoveCardsToLocation(site, ito, boba);
        scn.CaptureCardWith(boba, chewie);
        scn.CaptureCardWith(boba, leia);
        scn.CaptureCardWith(boba, threepio);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertTrue(scn.DSForceDrainAvailable(site));

        assertEquals(0, scn.GetLSIconsOnLocation(site));
        assertAtLocation(site, boba);
        assertEquals(boba, chewie.getEscort());
        assertEquals(boba, leia.getEscort());
        assertEquals(boba, threepio.getEscort());

        scn.DSForceDrainAt(site);
        scn.PassForceDrainStartResponses();
        //0 from the site, +1 for each of the captive Chewie, Leia, Threepio
        assertEquals(3, scn.GetForceDrainTotal());
    }


    private VirtualTableScenario GetDrainScenario() {
        var scn = new VirtualTableScenario(
                new HashMap<>() {{
                    put("captive", "1_19");
                    put("captive2", "1_5");
                    put("purchaser", "1_33");
                }},
                new HashMap<>() {{
                    put("ito", "2_93");
                    put("hypo", "2_112");
                    put("guard", "1_194");
                    put("farm", "1_294");
                    put("otherPrison", "6_164");
                }},
                20, 20,
                StartingSetup.DefaultLSGroundLocation,
                new StartingSetup.StartingLocation("1_284"),
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("farm"));
        scn.MoveLocationToTable(scn.GetDSCard("otherPrison"));
        return scn;
    }

    private VirtualTableScenario GetPurchasedDrainScenario(boolean withHypo, boolean withCaptive) {
        var scn = GetDrainScenario();
        var site = scn.GetDSStartingLocation();
        var ito = scn.GetDSCard("ito");
        var purchaser = scn.GetLSCard("purchaser");
        scn.MoveCardsToLocation(site, ito, purchaser);
        if (withHypo) {
            scn.AttachCardsTo(ito, scn.GetDSCard("hypo"));
        }
        if (withCaptive) {
            scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("captive"), site);
        }
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.EnsureLSForcePile(8);
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(purchaser, "'Purchase' transport vehicle or droid"));
        scn.LSUseCardAction(purchaser, "'Purchase' transport vehicle or droid");
        scn.LSChooseCard(ito);
        scn.PassAllResponses();
        if (scn.AwaitingDSControlPhaseActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSControlPhaseActions());
        assertEquals(4, scn.GetLSForcePileCount());
        assertEquals(scn.gameState().getLightPlayer(), ito.getOwner());
        assertAtLocation(site, ito);
        assertAtLocation(site, purchaser);
        if (withCaptive) {
            assertTrue(scn.GetLSCard("captive").isImprisoned());
            assertFalse(scn.GetLSCard("captive").isFrozen());
            assertEquals(site, scn.GetLSCard("captive").getAttachedTo());
        }
        if (withHypo) {
            assertEquals(scn.gameState().getLightPlayer(), scn.GetDSCard("hypo").getOwner());
            assertEquals(ito, scn.GetDSCard("hypo").getAttachedTo());
        }
        return scn;
    }

    private void AssertLightDrainTotal(VirtualTableScenario scn, int expected) {
        var site = scn.GetDSStartingLocation();
        assertEquals(1, scn.GetDSIconsOnLocation(site));
        assertTrue(scn.LSForceDrainAvailable(site));
        scn.LSForceDrainAt(site);
        scn.DSPass();
        assertTrue(scn.IsActiveForceDrain());
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertEquals(expected, scn.GetForceDrainTotal());
    }

    @Test
    public void ITOPurchasedByLightCannotIncreaseForceDrain() {
        AssertLightDrainTotal(GetPurchasedDrainScenario(false, true), 2);
    }

    @Test
    public void ITOPurchasedByLightWithHypoCannotIncreaseForceDrain() {
        AssertLightDrainTotal(GetPurchasedDrainScenario(true, true), 2);
    }

    @Test
    public void ITOPurchasedByLightWithoutCaptiveRetainsNativeDrain() {
        AssertLightDrainTotal(GetPurchasedDrainScenario(false, false), 2);
    }

    private VirtualTableScenario GetDarkDrainScenario(int captives, boolean withHypo,
                                                    boolean captiveElsewhere, boolean itoElsewhere) {
        var scn = GetDrainScenario();
        var site = scn.GetDSStartingLocation();
        var itoSite = itoElsewhere ? scn.GetDSCard("farm") : site;
        scn.MoveCardsToLocation(itoSite, scn.GetDSCard("ito"));
        scn.MoveCardsToLocation(site, scn.GetDSCard("guard"));
        if (withHypo) {
            scn.AttachCardsTo(scn.GetDSCard("ito"), scn.GetDSCard("hypo"));
        }
        var prison = captiveElsewhere ? scn.GetDSCard("otherPrison") : site;
        if (captives > 0) {
            scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("captive"), prison);
        }
        if (captives > 1) {
            scn.gameState().imprisonCharacter(scn.game(), scn.GetLSCard("captive2"), prison);
        }
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingDSControlPhaseActions());
        assertEquals(scn.gameState().getDarkPlayer(), scn.GetDSCard("ito").getOwner());
        return scn;
    }

    private void AssertDarkDrainTotal(VirtualTableScenario scn, int expected) {
        var site = scn.GetDSStartingLocation();
        assertEquals(0, scn.GetLSIconsOnLocation(site));
        assertTrue(scn.DSForceDrainAvailable(site));
        int lostBefore = scn.GetLSLostPileCount();
        int handBefore = scn.GetLSHandCount();
        int lifeForceBefore = scn.GetLSReserveDeckCount() + scn.GetLSForcePileCount() + scn.GetLSUsedPileCount();
        scn.DSForceDrainAt(site);
        scn.LSPass();
        assertTrue(scn.IsActiveForceDrain());
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        if (expected == 0) {
            assertFalse(scn.IsActiveForceDrain());
            assertEquals(lostBefore, scn.GetLSLostPileCount());
            assertEquals(handBefore, scn.GetLSHandCount());
            assertEquals(lifeForceBefore, scn.GetLSReserveDeckCount() + scn.GetLSForcePileCount() + scn.GetLSUsedPileCount());
        } else {
            assertEquals(expected, scn.GetForceDrainTotal());
        }
    }

    @Test
    public void ITOAddsOneForDarkCaptiveAtDetentionBlockCorridor() {
        AssertDarkDrainTotal(GetDarkDrainScenario(1, false, false, false), 1);
    }

    @Test
    public void ITOAddsTwoForDarkCaptivesAtDetentionBlockCorridor() {
        AssertDarkDrainTotal(GetDarkDrainScenario(2, false, false, false), 2);
    }

    @Test
    public void ITOWithHypoRetainsDarkCaptiveBonus() {
        AssertDarkDrainTotal(GetDarkDrainScenario(1, true, false, false), 1);
    }

    @Test
    public void ITODoesNotCountCaptiveAtAnotherPrison() {
        AssertDarkDrainTotal(GetDarkDrainScenario(1, false, true, false), 0);
    }

    @Test
    public void ITODoesNotModifyDrainWhenAtAnotherSite() {
        AssertDarkDrainTotal(GetDarkDrainScenario(1, false, false, true), 0);
    }

    @Test
    public void ITODoesNotModifyDarkDrainWithoutCaptives() {
        AssertDarkDrainTotal(GetDarkDrainScenario(0, false, false, false), 0);
    }
}

