package com.gempukku.swccgo.cards.set2230.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** Original Lord Vader (V) (Virtual Block 3 sheet, Dec 2009), a version of 1230_13. */
public class Card_2230_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1_21");
                    put("jedisaber", "1_155"); // Jedi Lightsaber
                }},
                new HashMap<>() {{
                    put("original", "2230_13");
                    put("current", "1230_13");
                    put("trooper", "1_194");
                }},
                20, 20,
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
    public void OriginalLordVaderIsTheSameCardWithTheSheetText() {
        var scn = GetScenario();
        var card = scn.GetDSCard("original").getBlueprint();

        assertEquals("Lord Vader", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(6, card.getDeployCost(), scn.epsilon);
        assertEquals(6, card.getPower(), scn.epsilon);
        assertEquals(6, card.getAbility(), scn.epsilon);
        assertEquals(8, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.IMPERIAL); }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{ add(Persona.VADER); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL); add(Icon.PILOT); add(Icon.WARRIOR); add(Icon.DEATH_STAR_II); add(Icon.LEGACY_BLOCK_3);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertEquals("Adds 3 to power of anything he pilots. May deploy an opponent's lightsaber just lost from same site on him (for free); may then use two weapons. While The Quick And Easy Path on table, Jedi Escape and What Was It? may not cancel duels. Immune to attrition < 5.",
                card.getGameText());
        var version = card.getLegacyVersion();
        assertEquals("1230_13", version.currentPrintingId());
        assertEquals("", version.label());
        assertEquals(LocalDate.of(2009, 12, 7), version.printedOn());
        assertEquals(LocalDate.of(2013, 4, 27), version.supersededOn());
        assertEquals("Lord Vader (V)", GameUtils.getFullName(card));
    }

    private boolean dsLoses(VirtualTableScenario scn, PhysicalCardImpl card) {
        var action = new TopLevelGameTextAction(scn.GetDSCard("trooper"), scn.DS, scn.GetDSCard("trooper").getCardId());
        action.appendEffect(new LoseCardFromTableEffect(action, card));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
        // pass the responses until the Dark Side is offered Vader's response to the just-lost lightsaber (or give up)
        var saber = scn.GetLSCard("jedisaber");
        for (int i = 0; i < 12; i++) {
            if (scn.DSGetDecision() != null && scn.GetDSAvailableActions().stream().anyMatch(x -> x.contains("Steal") || x.contains("on Vader")))
                return true;
            if (scn.LSGetDecision() != null && scn.LSHasCardChoiceAvailable(saber)) scn.LSChooseCard(saber);
            else if (scn.LSGetDecision() != null) scn.LSPass();
            else if (scn.DSGetDecision() != null) scn.DSPass();
        }
        return false;
    }

    @Test
    public void OriginalLordVaderDeploysAnOpponentsLightsaberJustLostFromSameSiteOnHimForFree() {
        //test1: the old clause is offered ("Deploy ... on Vader"), not a steal
        //test2: the lightsaber is deployed on him for free
        var scn = GetScenario();
        var vader = scn.GetDSCard("original");
        var obiwan = scn.GetLSCard("obiwan");
        var saber = scn.GetLSCard("jedisaber");
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), vader, obiwan);
        scn.AttachCardsTo(obiwan, saber);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int force = scn.GetDSForcePileCount();

        dsLoses(scn, saber);
        assertInZone(Zone.LOST_PILE, saber);
        assertFalse(scn.DSCardActionAvailable(vader, "Steal")); //test1
        assertTrue(scn.DSCardActionAvailable(vader, "Deploy")); //test1
        scn.DSUseCardAction(vader, "Deploy");
        if (scn.DSGetDecision() != null && scn.DSHasCardChoiceAvailable(saber))
            scn.DSChooseCard(saber);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(vader, saber)); //test2
        assertEquals(force, scn.GetDSForcePileCount()); //test2
        assertTrue(scn.game().getModifiersQuerying().grantedToUseWeapon(scn.gameState(), vader, saber)); //test2
    }

    @Test
    public void OriginalLordVaderIsTheSameUniqueCardAsTheCurrentVersion() {
        for (boolean currentOnTable : List.of(true, false)) {
            for (boolean otherOut : List.of(false, true)) {
                var scn = GetScenario();
                var onTable = scn.GetDSCard(currentOnTable ? "current" : "original");
                var inHand = scn.GetDSCard(currentOnTable ? "original" : "current");
                scn.StartGame();
                if (otherOut)
                    scn.MoveCardsToLocation(scn.GetDSStartingLocation(), onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.EnsureDSForcePile(10);
                scn.SkipToDSTurn(Phase.DEPLOY);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSDeployAvailable(inHand));
            }
        }
    }
}
