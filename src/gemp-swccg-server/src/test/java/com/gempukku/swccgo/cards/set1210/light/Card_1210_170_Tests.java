package com.gempukku.swccgo.cards.set1210.light;

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

public class Card_1210_170_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("blaster", "1210_170");
                    put("han", "1_11");
                    put("beckett", "213_42");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("rifle", "1_311");
                    put("bast", "1_165");
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
    public void HansHeavyBlasterPistolStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("blaster").getBlueprint();

        assertEquals("Han's Heavy Blaster Pistol", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void HansHeavyBlasterPistolDeploysOnHan() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han);
        scn.MoveCardsToLSHand(blaster);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(blaster));
        scn.LSDeployCard(blaster);
        scn.LSChooseCard(han);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(han, blaster));
    }

    @Test
    public void HansHeavyBlasterPistolDeploysOnBeckett() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var beckett = scn.GetLSCard("beckett");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, beckett);
        scn.MoveCardsToLSHand(blaster);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(blaster));
        scn.LSDeployCard(blaster);
        scn.LSChooseCard(beckett);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(beckett, blaster));
    }

    @Test
    public void HansHeavyBlasterPistolHitsTargetAndSetsForfeitToZero() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(han, blaster);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        assertTrue(trooper.isHit());
        assertEquals(0, scn.GetForfeit(trooper));
    }

    @Test
    public void HansHeavyBlasterPistolFiresDuringControlPhaseIfOnHan() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(han, blaster);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.AwaitingLSControlPhaseActions());
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire during control phase"));
        scn.LSUseCardAction(blaster, "Fire during control phase");
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(trooper)) {
            scn.LSChooseCard(trooper);
        }
        scn.PrepareLSDestiny(5);
        for (int i = 0; i < 8; i++) {
            if (scn.GetCurrentDecision() == null) {
                break;
            }
            String text = scn.GetCurrentDecision().getText().toLowerCase();
            if (text.contains("optional")) {
                scn.PassResponses("optional");
            } else if (text.contains("required")) {
                scn.PassResponses("required");
            } else if (scn.LSHasCardChoiceAvailable(trooper)) {
                scn.LSChooseCard(trooper);
            } else {
                break;
            }
        }
        scn.PassAllResponses();
        String pending = "hit=" + trooper.isHit()
                + " zone=" + trooper.getZone()
                + " LS=" + (scn.LSGetDecision() == null ? "none" : scn.LSGetDecision().getText());
        assertTrue(pending, trooper.isHit()
                || trooper.getZone() == Zone.LOST_PILE
                || trooper.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void HansHeavyBlasterPistolPlacesInUsedPileToCancelAWeaponDestinyTargetingHan() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var han = scn.GetLSCard("han");
        var trooper = scn.GetDSCard("trooper");
        var rifle = scn.GetDSCard("rifle");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, han, trooper);
        scn.AttachCardsTo(han, blaster);
        scn.AttachCardsTo(trooper, rifle);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(5);
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(rifle, "Fire"));
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(han);
        scn.PassForceUseResponses();
        for (int i = 0; i < 8; i++) {
            if (scn.LSDecisionAvailable("optional") || scn.LSDecisionAvailable("DESTINY")) {
                try {
                    scn.LSChooseAction("Place in Used Pile to cancel weapon destiny");
                    break;
                } catch (RuntimeException ignored) {
                    scn.PassResponses("optional");
                }
            } else if (scn.GetCurrentDecision() != null
                    && scn.GetCurrentDecision().getText().toLowerCase().contains("optional")) {
                scn.PassResponses("optional");
            } else {
                break;
            }
        }
        scn.PassAllResponses();
        scn.PassAllResponses();
        assertFalse(han.isHit());
        assertTrue(blaster.getZone() == Zone.USED_PILE || blaster.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
