package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_020_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("blaster", "1_152");
                }},
                new HashMap<>() {{
                    put("recon", "1240_20");
                    put("trooper1", "1_194");
                    put("trooper2", "1_194");
                    put("swilla", "2_126");
                }},
                10,
                10,
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
    public void DarkReconnaissanceStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("recon").getBlueprint();

        assertEquals("Dark Reconnaissance", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardCategory.EFFECT, card.getCardCategory());
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_LOCATION);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.EFFECT);
            add(Icon.TATOOINE);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DarkReconnaissanceDeploysOnlyOnOpponentsLocation() {
        var scn = GetScenario();
        var recon = scn.GetDSCard("recon");
        var lsSite = scn.GetLSStartingLocation();
        var dsSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToHand(recon);
        scn.SkipToPhase(Phase.DEPLOY);
        scn.EnsureDSForcePile(10);

        assertTrue(scn.DSDeployAvailable(recon));
        scn.DSDeployCard(recon);
        assertTrue(scn.DSHasCardChoiceAvailable(lsSite));
        assertFalse(scn.DSHasCardChoiceAvailable(dsSite));
        scn.DSChooseCard(lsSite);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(lsSite, recon));
    }

    private int drainAtLsSite(VirtualTableScenario scn, int mistrylCount) {
        var recon = scn.GetDSCard("recon");
        var trooper1 = scn.GetDSCard("trooper1");
        var trooper2 = scn.GetDSCard("trooper2");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.AttachCardsTo(lsSite, recon);
        scn.MoveCardsToLocation(lsSite, trooper1, trooper2);
        if (mistrylCount >= 1)
            scn.ApplyAdHocModifier(new KeywordModifier(recon, Filters.sameCardId(trooper1), Keyword.MISTRYL));
        if (mistrylCount >= 2)
            scn.ApplyAdHocModifier(new KeywordModifier(recon, Filters.sameCardId(trooper2), Keyword.MISTRYL));

        scn.SkipToPhase(Phase.CONTROL);
        int lifeBefore = scn.GetLSLifeForceRemaining();
        scn.DSForceDrainAt(lsSite);
        scn.PassAllResponses();
        if (scn.AwaitingLSForceLossPayment()) {
            scn.LSPayRemainingForceLossFromReserveDeck();
        }
        scn.PassAllResponses();
        return lifeBefore - scn.GetLSLifeForceRemaining();
    }

    @Test
    public void DarkReconnaissanceTwoMistrylMakeForceDrainPlusOne() {
        var withOne = GetScenario();
        int lossWithOne = drainAtLsSite(withOne, 1);
        var withTwo = GetScenario();
        int lossWithTwo = drainAtLsSite(withTwo, 2);
        assertTrue(lossWithOne >= 1);
        assertEquals(lossWithOne + 1, lossWithTwo);
    }

    @Test
    public void DarkReconnaissanceLetsSwillaCoreyTargetWeaponAndPlaceItInOwnersUsedPile() {
        var scn = GetScenario();
        var recon = scn.GetDSCard("recon");
        var trooper1 = scn.GetDSCard("trooper1");
        var swilla = scn.GetDSCard("swilla");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.AttachCardsTo(lsSite, recon);
        scn.MoveCardsToLocation(lsSite, trooper1, luke);
        scn.AttachCardsTo(luke, blaster);
        scn.AttachCardsTo(trooper1, swilla);
        swilla.setPlayCardOptionId(PlayCardOptionId.PLAY_CARD_OPTION_1);

        scn.SkipToPhase(Phase.CONTROL);
        assertTrue(scn.DSCardActionAvailable(swilla, "Steal device or weapon"));
        scn.PrepareDSDestiny(0);
        int usedBefore = scn.GetLSUsedPileCount();
        scn.DSUseCardAction(swilla, "Steal device or weapon");
        scn.DSChooseCard(blaster);
        scn.PassAllResponses();

        assertTrue(scn.GetLSUsedPile().contains(blaster));
        assertEquals(usedBefore + 1, scn.GetLSUsedPileCount());
        assertFalse(scn.IsAttachedTo(trooper1, blaster));
    }

    @Test
    public void SwillaCoreyCannotTargetWeaponWithoutDarkReconnaissance() {
        var scn = GetScenario();
        var trooper1 = scn.GetDSCard("trooper1");
        var swilla = scn.GetDSCard("swilla");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var lsSite = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(lsSite, trooper1, luke);
        scn.AttachCardsTo(luke, blaster);
        scn.AttachCardsTo(trooper1, swilla);
        swilla.setPlayCardOptionId(PlayCardOptionId.PLAY_CARD_OPTION_1);

        scn.SkipToPhase(Phase.CONTROL);
        assertFalse(scn.DSCardActionAvailable(swilla, "Steal"));
    }
}
