package com.gempukku.swccgo.cards.set1103.light;

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

public class Card_1103_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("solomahal", "1103_21");
                    put("scout", "1_30");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("blaster", "1_317");
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
    public void SolomahalStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("solomahal").getBlueprint();

        assertEquals("Solomahal", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.EFFECT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SolomahalDeploysOnYourScoutExceptLuke() {
        var scn = GetScenario();
        var solomahal = scn.GetLSCard("solomahal");
        var scout = scn.GetLSCard("scout");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, scout, luke);
        scn.MoveCardsToLSHand(solomahal);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(solomahal));
        scn.LSPlayCard(solomahal);
        assertTrue(scn.LSHasCardChoiceAvailable(scout));
        assertFalse(scn.LSHasCardChoiceAvailable(luke));
        scn.LSChooseCard(scout);
        scn.PassAllResponses();
        assertEquals(scout, solomahal.getAttachedTo());
    }

    @Test
    public void SolomahalDoesNotDeployOnLuke() {
        var scn = GetScenario();
        var solomahal = scn.GetLSCard("solomahal");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(solomahal);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(solomahal));
        assertEquals(Zone.HAND, solomahal.getZone());
    }

    @Test
    public void SolomahalCancelsWeaponTargetingOnTheScoutByPlacingThisEffectInLostPile() {
        var scn = GetScenario();
        var solomahal = scn.GetLSCard("solomahal");
        var scout = scn.GetLSCard("scout");
        var trooper = scn.GetDSCard("trooper");
        var blaster = scn.GetDSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, scout, trooper);
        scn.AttachCardsTo(scout, solomahal);
        scn.AttachCardsTo(trooper, blaster);
        scn.EnsureDSForcePile(2);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        assertTrue(scn.DSCardActionAvailable(blaster, "Fire"));
        scn.DSUseCardAction(blaster, "Fire");
        scn.DSChooseCard(scout);
        scn.PassForceUseResponses();
        boolean canceled = false;
        for (int i = 0; i < 12; i++) {
            if (scn.LSCardActionAvailable(solomahal, "Cancel weapon targeting")
                    || scn.LSActionAvailable("Cancel weapon targeting")) {
                scn.LSChooseAction("Cancel weapon targeting");
                canceled = true;
                break;
            }
            if (scn.LSDecisionAvailable("optional")) {
                scn.LSPass();
                continue;
            }
            if (scn.DSDecisionAvailable("optional")) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(canceled);
        scn.PassAllResponses();
        assertFalse(scout.isHit());
        assertTrue(solomahal.getZone() == Zone.LOST_PILE || solomahal.getZone() == Zone.TOP_OF_LOST_PILE);
    }
}
