package com.gempukku.swccgo.cards.set1291.light;

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

public class Card_1291_037_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("sniper", "1291_37");
                    put("a280", "8_84"); // A280 Sharpshooter Rifle
                    put("cantina", "1_128"); // Tatooine: Cantina (adjacent to Marketplace)
                    put("satm", "2_57"); // Sorry About The Mess: fire a weapon during your control phase
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("trooper2", "1_194");
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
    public void CloneSniperStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("sniper").getBlueprint();
        assertEquals("Clone Sniper", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals("Clone trooper and scout.", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.CLONE_TROOPER);
            add(Keyword.SCOUT);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REPUBLIC);
            add(Icon.WARRIOR);
            add(Icon.EPISODE_I);
            add(Icon.CLONE_ARMY);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CloneSniperWhenDeployedMayDeployA280FromReserveDeck() {
        var scn = GetScenario();
        var sniper = scn.GetLSCard("sniper");
        var a280 = scn.GetLSCard("a280");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToHand(sniper);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(6);
        scn.MoveCardsToTopOfOwnReserveDeck(a280);
        scn.LSDeployCard(sniper);
        scn.LSChooseCard(site);
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsAction(scn, sniper, "Deploy A280" ));
        scn.LSUseCardAction(sniper, "Deploy A280");
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsCardChoiceCount(scn) > 0);
        scn.LSChooseCard(a280);
        if (Vb91LightTestSupport.lsCardChoiceCount(scn) > 0 && scn.LSHasCardChoiceAvailable(sniper)) {
            scn.LSChooseCard(sniper);
        }
        Vb91LightTestSupport.reach(scn, () -> scn.IsAttachedTo(sniper, a280));
    }

    @Test
    public void CloneSniperA280MayDeployOnHim() {
        var scn = GetScenario();
        var sniper = scn.GetLSCard("sniper");
        var a280 = scn.GetLSCard("a280");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), sniper);
        scn.MoveCardsToHand(a280);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(a280));
        scn.LSDeployCard(a280);
        if (scn.LSDecisionAvailable("Choose")) {
            scn.LSChooseCard(sniper);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(sniper, a280));
    }

    private void fireA280At(VirtualTableScenario scn, String targetKey) {
        var sniper = scn.GetLSCard("sniper");
        var a280 = scn.GetLSCard("a280");
        var cantina = scn.GetLSCard("cantina");
        var trooper = scn.GetDSCard("trooper");
        var trooper2 = scn.GetDSCard("trooper2");
        var marketplace = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveLocationToTable(cantina);
        assertTrue(scn.IsAdjacentTo(marketplace, cantina));
        scn.MoveCardsToLocation(marketplace, sniper, trooper);
        scn.MoveCardsToLocation(cantina, trooper2);
        scn.AttachCardsTo(sniper, a280);
        var satm = scn.GetLSCard("satm");
        scn.MoveCardsToHand(satm);
        scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPlayCard(satm);
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsCardChoiceCount(scn) > 0);
        if (Vb91LightTestSupport.lsCardChoiceCount(scn) > 0 && scn.LSHasCardChoiceAvailable(a280)) {
            scn.LSChooseCard(a280);
        }
        var target = scn.GetDSCard(targetKey);
        Vb91LightTestSupport.advanceUntil(scn, () -> Vb91LightTestSupport.lsCardChoiceCount(scn) > 0 && scn.LSHasCardChoiceAvailable(target));
        assertTrue(Vb91LightTestSupport.decisions(scn) + " choices=" + scn.LSGetCardChoices(), scn.LSHasCardChoiceAvailable(scn.GetDSCard(targetKey)));
        scn.LSChooseCard(scn.GetDSCard(targetKey));
    }

    @Test
    public void CloneSniperMayDrawTwoWeaponDestinyWhenFiringRifleAtAdjacentSite() {
        var scn = GetScenario();
        var sniper = scn.GetLSCard("sniper");
        fireA280At(scn, "trooper2");
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsAction(scn, sniper, "Draw two weapon destiny"));
        scn.LSUseCardAction(sniper, "Draw two weapon destiny");
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsCardChoiceCount(scn) == 2);
    }

    @Test
    public void CloneSniperMayNotDrawTwoWeaponDestinyWhenFiringAtSameSite() {
        var scn = GetScenario();
        var sniper = scn.GetLSCard("sniper");
        fireA280At(scn, "trooper");
        assertFalse(Vb91LightTestSupport.advanceUntil(scn,
                () -> Vb91LightTestSupport.lsAction(scn, sniper, "Draw two weapon destiny")));
    }

    @Test
    public void CloneSniperIsImmuneToUndercover() {
        var scn = GetScenario();
        var sniper = scn.GetLSCard("sniper");
        scn.StartGame();
        assertFalse(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), sniper, Title.Undercover));
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), sniper);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), sniper, Title.Undercover));
    }
}
