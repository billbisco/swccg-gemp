package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_048_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("voolvif", "1291_48");
                    put("geonosis", "1291_57"); // Geonosis: Forward Command Center
                    put("luke", "1_19");
                    put("obiwan", "1_21"); // another Jedi (ability 6)
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("vader", "1_168");
                    put("saber", "1_312"); // Dark Jedi Lightsaber
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
    public void VoolvifMonnStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("voolvif").getBlueprint();
        assertEquals("Voolvif Monn", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        assertEquals("Shistavanen.", card.getLore());
        assertEquals(Species.SHISTAVANEN, card.getSpecies());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REPUBLIC);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
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
    public void VoolvifMonnMayDeployMinusTwoToGeonosisAsReactToBattle() {
        var scn = GetScenario();
        var voolvif = scn.GetLSCard("voolvif");
        var geonosis = scn.GetLSCard("geonosis");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        scn.StartGame();
        scn.MoveLocationToTable(geonosis);
        scn.MoveCardsToLocation(geonosis, luke, trooper);
        scn.MoveCardsToHand(voolvif);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(6);
        scn.DSInitiateBattle(geonosis);
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsAction(scn, voolvif, "react"));
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(voolvif, "react");
        if (Vb91LightTestSupport.lsCardChoiceCount(scn) > 0) {
            scn.LSChooseCard(geonosis);
        }
        scn.PassAllResponses();
        assertAtLocation(geonosis, voolvif);
        assertEquals(force - 4, scn.GetLSForcePileCount());
    }

    @Test
    public void VoolvifMonnMayNotReactToNonGeonosisSite() {
        var scn = GetScenario();
        var voolvif = scn.GetLSCard("voolvif");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, trooper);
        scn.MoveCardsToHand(voolvif);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(6);
        scn.DSInitiateBattle(site);
        assertFalse(Vb91LightTestSupport.advanceUntil(scn, () -> Vb91LightTestSupport.lsAction(scn, voolvif, "react")));
    }

    @Test
    public void VoolvifMonnIsPowerPlusTwoAndImmuneToAttritionDuringBattleWithoutAnotherJedi() {
        var scn = GetScenario();
        var voolvif = scn.GetLSCard("voolvif");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, voolvif, trooper);
        assertEquals(4, scn.GetPower(voolvif));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), voolvif), scn.epsilon);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        assertEquals(6, scn.GetPower(voolvif));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), voolvif), scn.epsilon);
    }

    @Test
    public void VoolvifMonnGetsNoBonusDuringBattleWithAnotherJediHere() {
        var scn = GetScenario();
        var voolvif = scn.GetLSCard("voolvif");
        var obiwan = scn.GetLSCard("obiwan");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, voolvif, obiwan, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        assertEquals(4, scn.GetPower(voolvif));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), voolvif), scn.epsilon);
    }

    private void vaderSwingsAtVoolvif(VirtualTableScenario scn) {
        var voolvif = scn.GetLSCard("voolvif");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, voolvif, vader);
        scn.AttachCardsTo(vader, saber);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.EnsureLSForcePile(4);
        scn.DSInitiateBattle(site);
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.dsAction(scn, saber, "Fire"));
        scn.DSUseCardAction(saber, "Fire");
        if (scn.DSDecisionAvailable("Choose")) {
            scn.DSChooseCard(voolvif);
        }
        scn.PrepareDSDestiny(6);
    }

    @Test
    public void VoolvifMonnMayLoseTwoForceToCancelWeaponDestinyTargetingHim() {
        var scn = GetScenario();
        var voolvif = scn.GetLSCard("voolvif");
        vaderSwingsAtVoolvif(scn);
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsAction(scn, voolvif, "Lose 2 Force to cancel weapon destiny"));
        assertTrue(scn.LSCardActionAvailable(voolvif, "Use 3 Force to cancel weapon destiny"));
        int lost = scn.GetLSLostPileCount();
        scn.LSUseCardAction(voolvif, "Lose 2 Force to cancel weapon destiny");
        for (int i = 0; i < 4 && scn.GetLSLostPileCount() < lost + 2; ++i) {
            Vb91LightTestSupport.advanceUntil(scn, () -> scn.AwaitingLSForceLossPayment() || scn.GetLSLostPileCount() >= lost + 2);
            if (scn.AwaitingLSForceLossPayment()) {
                scn.LSPayForceLossFromReserveDeck();
            }
        }
        scn.PassAllResponses();
        assertEquals(lost + 2, scn.GetLSLostPileCount());
        assertAtLocation(scn.GetDSStartingLocation(), voolvif);
        assertFalse(voolvif.isHit());
    }

    @Test
    public void VoolvifMonnMayUseThreeForceToCancelWeaponDestinyTargetingHim() {
        var scn = GetScenario();
        var voolvif = scn.GetLSCard("voolvif");
        vaderSwingsAtVoolvif(scn);
        Vb91LightTestSupport.reach(scn, () -> Vb91LightTestSupport.lsAction(scn, voolvif, "Use 3 Force to cancel weapon destiny"));
        int force = scn.GetLSForcePileCount();
        scn.LSUseCardAction(voolvif, "Use 3 Force to cancel weapon destiny");
        scn.PassAllResponses();
        assertEquals(force - 3, scn.GetLSForcePileCount());
        assertFalse(voolvif.isHit());
    }
}
