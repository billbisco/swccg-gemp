package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.set1281.dark.Vb81RestTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1273_030_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("wes", "1273_30");
                    put("pistol", "7_157");
                    put("pistol2", "7_157");
                    put("hobbie", "3_005");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
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
    public void WesJansonRogueVeteranStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("wes").getBlueprint();

        assertEquals("Wes Janson, Rogue Veteran", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(2, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.REBEL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ROGUE_SQUADRON);
            add(Keyword.GUNNER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.WES);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.REBEL);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void WesJansonRogueVeteranOncePerTurnDeploysDisruptorPistolFromReserveDeckForFree() {
        var scn = GetScenario();
        var wes = scn.GetLSCard("wes");
        var pistol = scn.GetLSCard("pistol");
        var pistol2 = scn.GetLSCard("pistol2");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, wes);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(pistol);
        scn.MoveCardsToLSHand(pistol2);
        int force = scn.GetLSForcePileCount();

        assertTrue(scn.LSCardActionAvailable(wes, "Deploy Disruptor Pistol from Reserve Deck"));
        scn.LSUseCardAction(wes, "Deploy Disruptor Pistol from Reserve Deck");
        for (int i = 0; i < 6 && !scn.IsAttachedTo(wes, pistol); ++i) {
            if (scn.LSHasCardChoiceAvailable(pistol)) {
                scn.LSChooseCard(pistol);
            }
            else if (scn.LSHasCardChoiceAvailable(wes)) {
                scn.LSChooseCard(wes);
            }
            else {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        Vb81RestTestSupport.passUntil(scn, () -> scn.AwaitingLSDeployPhaseActions());
        assertTrue(scn.IsAttachedTo(wes, pistol));
        assertEquals(force, scn.GetLSForcePileCount());
        assertFalse(Vb81RestTestSupport.lsCardActionAvailable(scn, wes, "Deploy Disruptor Pistol from hand"));
    }

    @Test
    public void WesJansonRogueVeteranMayDeployDisruptorPistolFromHandForFree() {
        var scn = GetScenario();
        var wes = scn.GetLSCard("wes");
        var pistol = scn.GetLSCard("pistol");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, wes);
        scn.MoveCardsToLSHand(pistol);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();

        assertTrue(scn.LSCardActionAvailable(wes, "Deploy Disruptor Pistol from hand"));
        scn.LSUseCardAction(wes, "Deploy Disruptor Pistol from hand");
        for (int i = 0; i < 6 && !scn.IsAttachedTo(wes, pistol); ++i) {
            if (scn.LSHasCardChoiceAvailable(pistol)) {
                scn.LSChooseCard(pistol);
            }
            else if (scn.LSHasCardChoiceAvailable(wes)) {
                scn.LSChooseCard(wes);
            }
            else {
                scn.PlayerPass(scn.GetDecidingPlayer());
            }
        }
        assertTrue(scn.IsAttachedTo(wes, pistol));
        assertEquals(force, scn.GetLSForcePileCount());
    }

    @Test
    public void WesJansonRogueVeteranFiresDisruptorPistolForOneForce() {
        var scn = GetScenario();
        var wes = scn.GetLSCard("wes");
        var pistol = scn.GetLSCard("pistol");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, wes);
        scn.AttachCardsTo(wes, pistol);
        assertEquals(1, scn.game().getModifiersQuerying().getFireWeaponCost(scn.gameState(), pistol, wes, null, 2), scn.epsilon);
    }

    @Test
    public void WesJansonRogueVeteranPowerAndForfeitPlusOneWithHobbie() {
        var scn = GetScenario();
        var wes = scn.GetLSCard("wes");
        var hobbie = scn.GetLSCard("hobbie");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, wes);
        assertEquals(2, scn.GetPower(wes));
        assertEquals(4, scn.GetForfeit(wes));
        scn.MoveCardsToLocation(site, hobbie);
        assertEquals(3, scn.GetPower(wes));
        assertEquals(5, scn.GetForfeit(wes));
    }

    @Test
    public void WesJansonRogueVeteranCardsOnWesGoToUsedPileWhenHeLeavesTable() {
        var scn = GetScenario();
        var wes = scn.GetLSCard("wes");
        var pistol = scn.GetLSCard("pistol");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, wes, vader);
        scn.AttachCardsTo(wes, pistol);
        scn.SkipToPhase(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToEndOfPowerSegment(false);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSBattleDamagePayment());
        scn.LSChooseCard(wes);
        scn.PassAllResponses();
        assertTrue(Vb81RestTestSupport.inZone(pistol, Zone.USED_PILE));
        assertTrue(Vb81RestTestSupport.inZone(wes, Zone.LOST_PILE));
    }
}
