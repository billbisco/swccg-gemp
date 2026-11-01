package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1115_042_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("cyclone", "1115_42");
                    put("sandtrooper", "1210_21");
                    put("stormtrooper", "1_194");
                    put("cannon", "3_158");
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
    public void Cyclone1StatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("cyclone").getBlueprint();

        assertEquals("Cyclone 1", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(7, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(7, card.getArmor(), scn.epsilon);
        assertEquals(1, card.getLandspeed(), scn.epsilon);
        assertEquals(7, card.getForfeit(), scn.epsilon);
        assertEquals(7, card.getPassengerCapacity());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.AT_AT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.PILOT);
            add(Icon.SCOMP_LINK);
            add(Icon.SPECIAL_EDITION);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void Cyclone1PermanentPilotsProvideAbilityOfTwoAndIsImmuneToAttritionLessThanFour() {
        var scn = GetScenario();
        var cyclone = scn.GetDSCard("cyclone");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, cyclone);
        assertEquals(2, scn.GetBattleDestinyAbility(cyclone));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), cyclone), scn.epsilon);
    }

    @Test
    public void Cyclone1IsPowerPlusOneForEachSandtrooperPresent() {
        var scn = GetScenario();
        var cyclone = scn.GetDSCard("cyclone");
        var sandtrooper = scn.GetDSCard("sandtrooper");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, cyclone);
        assertEquals(5, scn.GetPower(cyclone));
        scn.MoveCardsToLocation(site, stormtrooper);
        assertEquals(5, scn.GetPower(cyclone));
        scn.MoveCardsToLocation(site, sandtrooper);
        assertEquals(6, scn.GetPower(cyclone));
    }

    @Test
    public void Cyclone1DeploysAtAtCannonFromReserveDeckOncePerGame() {
        var scn = GetScenario();
        var cyclone = scn.GetDSCard("cyclone");
        var cannon = scn.GetDSCard("cannon");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, cyclone);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.EnsureDSForcePile(2);
        scn.MoveCardsToTopOfDSReserveDeck(cannon);
        assertTrue(scn.DSCardActionAvailable(cyclone, "Deploy vehicle site or vehicle weapon"));
        scn.DSUseCardAction(cyclone, "Deploy vehicle site or vehicle weapon");
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(cannon);
        scn.PassAllResponses();
        assertEquals(cyclone, cannon.getAttachedTo());
    }
}
