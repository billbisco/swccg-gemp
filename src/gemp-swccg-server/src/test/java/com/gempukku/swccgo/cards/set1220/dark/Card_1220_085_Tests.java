package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardSubtype;
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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_085_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("inhibitor", "1220_85");
                    put("ig88", "601_185");
                    put("dengar", "1220_10");
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
    public void IG88sNeuralInhibitorStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("inhibitor").getBlueprint();

        assertEquals("IG-88's Neural Inhibitor", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.DAGOBAH);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void IG88sNeuralInhibitorDeploysFreeOnIG88AndCostsOneOnOtherBountyHunter() {
        var scn = GetScenario();
        var inhibitor = scn.GetDSCard("inhibitor");
        var ig88 = scn.GetDSCard("ig88");
        var dengar = scn.GetDSCard("dengar");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, ig88, dengar);
        assertEquals(0, deployCostTo(scn, inhibitor, ig88), scn.epsilon);
        assertEquals(1, deployCostTo(scn, inhibitor, dengar), scn.epsilon);
    }

    @Test
    public void IG88sNeuralInhibitorCapturesWhenDestinyPlusOneExceedsDefenseValue() {
        var scn = GetScenario();
        var inhibitor = scn.GetDSCard("inhibitor");
        var ig88 = scn.GetDSCard("ig88");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, ig88, luke);
        scn.AttachCardsTo(ig88, inhibitor);
        scn.EnsureDSForcePile(1);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(inhibitor, "Fire"));
        scn.PrepareDSDestiny(5);
        scn.DSUseCardAction(inhibitor, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        scn.DSChooseSeizeCaptive();
        scn.PassAllResponses();
        assertTrue(luke.isCaptive());
        assertEquals(ig88, luke.getEscort());
    }

    @Test
    public void IG88sNeuralInhibitorDoesNotMakeOpponentLoseTwoForceWhenTargetIsNeitherMaintenanceNorPermanentWeapon() {
        var scn = GetScenario();
        var inhibitor = scn.GetDSCard("inhibitor");
        var ig88 = scn.GetDSCard("ig88");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, ig88, luke);
        scn.AttachCardsTo(ig88, inhibitor);
        scn.EnsureDSForcePile(1);
        scn.SkipToDSTurn(Phase.BATTLE);
        int lsForceBefore = scn.GetLSForcePileCount();
        int lsReserveBefore = scn.GetLSReserveDeckCount();
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareDSDestiny(5);
        scn.DSUseCardAction(inhibitor, "Fire");
        scn.DSChooseCard(luke);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        scn.DSChooseSeizeCaptive();
        scn.PassAllResponses();
        assertTrue(luke.isCaptive());
        assertEquals(lsForceBefore, scn.GetLSForcePileCount());
        assertEquals(lsReserveBefore, scn.GetLSReserveDeckCount());
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
