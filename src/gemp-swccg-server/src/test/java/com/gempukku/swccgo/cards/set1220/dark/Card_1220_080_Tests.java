package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.common.CardSubtype;
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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_080_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("blizzard", "1220_80");
                    put("veers", "3_87");
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
    public void Blizzard1StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("blizzard").getBlueprint();

        assertEquals("Blizzard 1", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(7, card.getPower(), scn.epsilon);
        assertEquals(7, card.getArmor(), scn.epsilon);
        assertEquals(1, card.getLandspeed(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.BLIZZARD_1);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.HOTH);
            add(Icon.SCOMP_LINK);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Blizzard1DuringPowerSegmentMayLoseCardsFromHandToAddAttrition() {
        var scn = GetScenario();
        var blizzard = scn.GetDSCard("blizzard");
        var veers = scn.GetDSCard("veers");
        var luke = scn.GetLSCard("luke");
        var filler1 = scn.GetDSFiller(1);
        var filler2 = scn.GetDSFiller(2);
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard, luke);
        scn.BoardAsPilot(blizzard, veers);
        scn.MoveCardsToDSHand(filler1, filler2);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.PrepareDSDestiny(0);
        scn.DSInitiateBattle(site);
        scn.PassBattleStartResponses();
        scn.PassWeaponsSegmentActions();
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), blizzard), scn.epsilon);

        boolean offered = false;
        for (int i = 0; i < 24; i++) {
            if (scn.DSGetDecision() != null && scn.DSGetADParam("actionText") != null
                    && scn.DSCardActionAvailable(blizzard, "Lose cards from hand to add attrition")) {
                scn.DSUseCardAction(blizzard, "Lose cards from hand to add attrition");
                offered = true;
                break;
            }
            if (scn.LSGetDecision() != null) {
                scn.LSPass();
                continue;
            }
            if (scn.DSGetDecision() != null) {
                scn.DSPass();
                continue;
            }
            break;
        }
        assertTrue(offered);
        scn.DSDecided(1);
        scn.DSChooseCard(filler1);
        scn.PassAllResponses();
        assertInZone(Zone.LOST_PILE, filler1);
        assertInZone(Zone.HAND, filler2);
        scn.SkipToDamageSegment(true);
        assertEquals(1, scn.GetUnpaidLSAttrition());
    }

    @Test
    public void Blizzard1DoesNotOfferLoseCardsDuringWeaponsSegment() {
        var scn = GetScenario();
        var blizzard = scn.GetDSCard("blizzard");
        var veers = scn.GetDSCard("veers");
        var luke = scn.GetLSCard("luke");
        var filler = scn.GetDSFiller(1);
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, blizzard, luke);
        scn.BoardAsPilot(blizzard, veers);
        scn.MoveCardsToDSHand(filler);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassBattleStartResponses();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertFalse(scn.DSCardActionAvailable(blizzard, "Lose cards from hand to add attrition"));
    }
}
