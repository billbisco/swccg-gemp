package com.gempukku.swccgo.cards.set1112.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1112_032_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("han", "1_11");
                    put("blaster", "1_154");
                }},
                new HashMap<>() {{
                    put("servitude", "1112_32");
                    put("stormtrooper", "1_194");
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
    public void ForcedServitudeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("servitude").getBlueprint();

        assertEquals("Forced Servitude", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
}});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_12, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ForcedServitudeDeploysOnTable() {
        var scn = GetScenario();
        var servitude = scn.GetDSCard("servitude");

        scn.StartGame();
        scn.MoveCardsToDSHand(servitude);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(4);

        assertTrue(scn.DSCardPlayAvailable(servitude));
        scn.DSPlayCard(servitude);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, servitude);
    }

    @Test
    public void ForcedServitudeMakesYourTroopersHeroic() {
        var scn = GetScenario();
        var servitude = scn.GetDSCard("servitude");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(servitude);
        scn.MoveCardsToLocation(site, stormtrooper);

        assertTrue(scn.game().getModifiersQuerying().hasKeyword(scn.gameState(), stormtrooper, Keyword.HEROIC));
    }

    @Test
    public void ForcedServitudeMayPlaceTroopersInUsedPileWhenOpponentIsAboutToRemoveLastLifeForce() {
        var scn = GetScenario();
        var servitude = scn.GetDSCard("servitude");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(servitude);
        scn.MoveCardsToLocation(site, stormtrooper);
        scn.SkipToDSTurn(Phase.CONTROL);

        while (scn.GetLSLifeForceRemaining() > 1) {
            if (scn.GetLSForcePileCount() > 0) {
                scn.MoveCardsToTopOfLSLostPile((PhysicalCardImpl) scn.GetLSForcePile().getFirst());
            } else if (scn.GetLSUsedPileCount() > 0) {
                scn.MoveCardsToTopOfLSLostPile((PhysicalCardImpl) scn.GetLSUsedPile().getFirst());
            } else {
                scn.MoveCardsToTopOfLSLostPile(scn.GetTopOfLSReserveDeck());
            }
        }
        assertEquals(1, scn.GetLSLifeForceRemaining());
        assertTrue(scn.DSForceDrainAvailable(site));
        scn.DSForceDrainAt(site);
        scn.PassForceDrainStartResponses();

        boolean placed = false;
        for (int i = 0; i < 24; i++) {
            if (dsHasCardActions(scn) && scn.DSCardActionAvailable(servitude, "Place troopers")) {
                scn.DSUseCardAction(servitude, "Place troopers");
                placed = true;
                break;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null && !dsHasCardActions(scn)) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(placed);
        scn.PassAllResponses();
        assertInZone(Zone.USED_PILE, stormtrooper);
    }

    @Test
    public void ForcedServitudeAddsTwoToOpponentsWeaponDestinyTargetingYourHeroicCards() {
        var scn = GetScenario();
        var servitude = scn.GetDSCard("servitude");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var han = scn.GetLSCard("han");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(servitude);
        scn.MoveCardsToLocation(site, stormtrooper, han);
        scn.AttachCardsTo(han, blaster);
        scn.EnsureLSForcePile(1);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        if (scn.AwaitingDSWeaponsSegmentActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.PrepareLSDestiny(1);
        scn.LSUseCardAction(blaster, "Fire");
        if (scn.LSHasCardChoiceAvailable(stormtrooper)) {
            scn.LSChooseCard(stormtrooper);
        }
        scn.PassResponses("Fire ");
        scn.PassResponses("COST_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("ABOUT_TO_DRAW_DESTINY_CARD");
        scn.PassResponses("DESTINY_DRAWN");

        assertEquals(4f, scn.game().getModifiersQuerying().getTotalWeaponDestiny(scn.gameState(), scn.LS, 1f), scn.epsilon);
        scn.PassAllResponses();
    }

    private boolean dsHasCardActions(VirtualTableScenario scn) {
        return scn.DSGetDecision() != null && scn.DSGetADParam("cardId") != null;
    }
}
