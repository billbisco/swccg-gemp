package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1250_006_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("blaster", "1_154");
                }},
                new HashMap<>() {{
                    put("elite", "1250_6");
                    put("trooper", "1_194");
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
    public void EliteSquadronStormtrooperStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("elite").getBlueprint();

        assertEquals("Elite Squadron Stormtrooper", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(2, card.getAbility(), scn.epsilon);
        assertEquals(3, card.getForfeit(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.STORMTROOPER);
            add(Keyword.GUARD);
            add(Keyword.CLONE_TROOPER);
            add(Keyword.HEROIC);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.WARRIOR);
            add(Icon.PERMANENT_WEAPON);
            add(Icon.EPISODE_I);
            add(Icon.ENDOR);
            add(Icon.LEGACY_BLOCK_5);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_5, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EliteSquadronStormtrooperMustBeTargetedByWeaponsBeforeNonHeroic() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var elite = scn.GetDSCard("elite");
        var trooper = scn.GetDSCard("trooper");
        var luke = scn.GetLSCard("luke");
        var blaster = scn.GetLSCard("blaster");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, elite, trooper, luke);
        scn.AttachCardsTo(luke, blaster);
        scn.LSActivateForceCheat(4);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        assertTrue(scn.LSHasCardChoiceAvailable(elite));
        assertTrue(scn.LSHasCardChoiceNotAvailable(trooper));
    }

    @Test
    public void EliteSquadronStormtrooperDeploysMinusOneIfDarkJediAtSenate() {
        var scn = GetSenateScenario();
        var elite = scn.GetDSCard("elite");
        var vader = scn.GetDSCard("vader");
        var senate = scn.GetDSCard("senate");

        scn.StartGame();
        assertEquals(3, scn.GetDeployCost(elite));

        scn.MoveLocationToTable(senate);
        scn.MoveCardsToLocation(senate, vader);
        assertEquals(2, scn.GetDeployCost(elite));
    }

    @Test
    public void EliteSquadronStormtrooperMakesJediHereHeroic() {
        var scn = GetJediScenario();
        var elite = scn.GetDSCard("elite");
        var yoda = scn.GetLSCard("yoda");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, yoda);
        assertFalse(scn.HasKeyword(yoda, Keyword.HEROIC));

        scn.MoveCardsToLocation(site, elite);
        assertTrue(scn.HasKeyword(yoda, Keyword.HEROIC));
    }

    @Test
    public void EliteSquadronStormtrooperPermanentRifleFiresForFree() throws DecisionResultInvalidException {
        var scn = GetScenario();
        var elite = scn.GetDSCard("elite");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, elite, luke);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);

        int force = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardActionAvailable(elite, "Fire"));
        scn.DSUseCardAction(elite, "Fire");
        assertTrue(scn.DSHasCardChoiceAvailable(luke));
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(force, scn.GetDSForcePileCount());
    }

    private VirtualTableScenario GetSenateScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("elite", "1250_6");
                    put("vader", "1_168");
                    put("senate", "12_167");
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

    private VirtualTableScenario GetJediScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("yoda", "4_2");
                }},
                new HashMap<>() {{
                    put("elite", "1250_6");
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
}
