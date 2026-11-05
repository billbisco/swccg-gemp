package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
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

public class Card_1263_020_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>(),
                new HashMap<>() {{
                    put("cyclone", "1263_20");
                    put("driver", "3_83");
                    put("driver2", "3_83");
                    put("lennox", "3_84");
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
    public void CycloneWalkerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("cyclone").getBlueprint();

        assertEquals("Cyclone Walker", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(5, card.getDeployCost(), scn.epsilon);
        assertEquals(5, card.getPower(), scn.epsilon);
        assertEquals(7, card.getArmor(), scn.epsilon);
        assertEquals(1, card.getLandspeed(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        assertEquals(8, card.getPassengerCapacity());
        assertTrue(card.getModelTypes().contains(ModelType.AT_AT));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.PILOT);
            add(Icon.SCOMP_LINK);
            add(Icon.SPECIAL_EDITION);
            add(Icon.LEGACY_BLOCK_6);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CycloneWalkerPermanentPilotProvidesAbilityOfOne() {
        var scn = GetScenario();
        var cyclone = scn.GetDSCard("cyclone");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, cyclone);
        assertEquals(1, scn.GetBattleDestinyAbility(cyclone));
    }

    @Test
    public void CycloneWalkerAtAtDriverPilotingBoostsDriverAndWalker() {
        var scn = GetScenario();
        var cyclone = scn.GetDSCard("cyclone");
        var driver = scn.GetDSCard("driver");
        var driver2 = scn.GetDSCard("driver2");
        var site = scn.GetDSStartingLocation();

        //test1: piloting AT-AT Driver is forfeit +2
        //test2: a passenger AT-AT Driver is not forfeit +2
        //test3: walker is power +1 (5 + 3 from driver + 1)
        //test4: walker is immune to attrition < 5
        scn.StartGame();
        scn.MoveCardsToLocation(site, cyclone);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), cyclone), scn.epsilon);

        scn.BoardAsPilot(cyclone, driver);
        scn.BoardAsPassenger(cyclone, driver2);
        assertEquals(4, scn.GetForfeit(driver)); //test1
        assertEquals(2, scn.GetForfeit(driver2)); //test2
        assertEquals(9, scn.GetPower(cyclone)); //test3
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), cyclone), scn.epsilon); //test4
    }

    @Test
    public void CycloneWalkerOtherPilotDoesNotGetDriverBonuses() {
        var scn = GetScenario();
        var cyclone = scn.GetDSCard("cyclone");
        var lennox = scn.GetDSCard("lennox");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, cyclone);
        scn.BoardAsPilot(cyclone, lennox);
        assertEquals(5, scn.GetForfeit(lennox));
        assertEquals(7, scn.GetPower(cyclone));
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), cyclone), scn.epsilon);
    }
}
