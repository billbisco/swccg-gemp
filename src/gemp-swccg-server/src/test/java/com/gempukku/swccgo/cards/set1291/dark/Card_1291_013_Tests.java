package com.gempukku.swccgo.cards.set1291.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19"); // a Skywalker, defense value 4
                    put("blaster", "1_152"); // Blaster
                }},
                new HashMap<>() {{
                    put("joinme", "1291_13");
                    put("vader", "1_168");
                    put("saber", "1_324"); // Vader's Lightsaber: draw two destiny, hit if total > defense value
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
    public void JoinMeVStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("joinme").getBlueprint();
        assertEquals("Join Me!", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private void battleVaderAndLuke(VirtualTableScenario scn, boolean armVader) {
        battleVaderAndLuke(scn, armVader, false);
    }

    private void battleVaderAndLuke(VirtualTableScenario scn, boolean armVader, boolean armLuke) {
        var joinme = scn.GetDSCard("joinme");
        var vader = scn.GetDSCard("vader");
        var saber = scn.GetDSCard("saber");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        scn.StartGame();
        scn.MoveCardsToLocation(site, vader, luke);
        if (armVader) {
            scn.AttachCardsTo(vader, saber);
        }
        if (armLuke) {
            scn.AttachCardsTo(luke, scn.GetLSCard("blaster"));
        }
        scn.MoveCardsToHand(joinme);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
    }

    @Test
    public void JoinMeVLoses1ForceToMakeSkywalkerPresentWithLightsaberCharacterPowerZero() {
        var scn = GetScenario();
        var joinme = scn.GetDSCard("joinme");
        var luke = scn.GetLSCard("luke");
        battleVaderAndLuke(scn, true);
        assertTrue(scn.DSCardPlayAvailable(joinme, "power = 0"));
        scn.DSPlayCard(joinme, "power = 0");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        if (scn.AwaitingDSForceLossPayment()) {
            scn.DSPayForceLossFromForcePile();
            scn.PassAllResponses();
        }
        assertEquals(0, scn.GetPower(luke));
        assertInZone(Zone.LOST_PILE, joinme);
    }

    @Test
    public void JoinMeVPowerZeroNeedsYourCharacterArmedWithALightsaber() {
        var scn = GetScenario();
        var joinme = scn.GetDSCard("joinme");
        battleVaderAndLuke(scn, false);
        assertFalse(scn.DSCardPlayAvailable(joinme, "power = 0"));
    }

    /**
     * Vader fires his lightsaber at Luke (destiny 3 + 4 = 7 > defense value 4) and the responses to the firing pass
     * until the response window to Luke's 'hit', where Dark may play Join Me!.
     */
    private void vaderHitsLukeWithLightsaber(VirtualTableScenario scn) {
        vaderHitsLukeWithLightsaber(scn, false);
    }

    private void vaderHitsLukeWithLightsaber(VirtualTableScenario scn, boolean armLuke) {
        var saber = scn.GetDSCard("saber");
        var joinme = scn.GetDSCard("joinme");
        var luke = scn.GetLSCard("luke");
        battleVaderAndLuke(scn, true, armLuke);
        scn.PrepareDSDestiny(3);
        scn.PrepareDSDestiny(4);
        scn.DSUseCardAction(saber, "Fire");
        scn.DSChooseCard(luke);
        for (int i = 0; i < 30 && !(luke.isHit() && scn.DSGetDecision() != null && scn.DSCardPlayAvailable(joinme)); i++) {
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
            } else {
                scn.LSPass();
            }
        }
        assertTrue(luke.isHit());
        assertTrue(scn.DSCardPlayAvailable(joinme));
    }

    @Test
    public void JoinMeVAfterLightsaberHitOpponentMayChooseToLose3Force() {
        var scn = GetScenario();
        var joinme = scn.GetDSCard("joinme");
        var luke = scn.GetLSCard("luke");
        vaderHitsLukeWithLightsaber(scn);
        int lifeForce = scn.GetLSLifeForceRemaining();
        scn.DSPlayCard(joinme);
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose effect"));
        scn.LSChoose("Lose 3 Force");
        scn.PassAllResponses();
        if (scn.AwaitingLSForceLossPayment()) {
            scn.LSPayRemainingForceLossFromReserveDeck();
            scn.PassAllResponses();
        }
        assertEquals(lifeForce - 3, scn.GetLSLifeForceRemaining());
        assertTrue(luke.getZone().isInPlay());
    }

    @Test
    public void JoinMeVAfterLightsaberHitOpponentMayPlaceSkywalkerAndCardsOnHimInUsedPile() {
        var scn = GetScenario();
        var joinme = scn.GetDSCard("joinme");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        vaderHitsLukeWithLightsaber(scn, true);
        assertTrue(scn.IsAttachedTo(luke, blaster));
        scn.DSPlayCard(joinme);
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose effect"));
        scn.LSChoose("Place Luke Skywalker in Used Pile");
        scn.PassAllResponses();
        assertTrue(scn.LSDecisionAvailable("Choose card to put on Used Pile"));
        scn.LSChooseCard(blaster);
        scn.PassAllResponses();
        assertTrue(luke.getZone() == Zone.USED_PILE || luke.getZone() == Zone.TOP_OF_USED_PILE);
        assertTrue(blaster.getZone() == Zone.USED_PILE || blaster.getZone() == Zone.TOP_OF_USED_PILE);
    }
}
