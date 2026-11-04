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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_021_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("cody", "200_6");
                    put("snowspeeder", "3_69");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("hq", "1291_21");
                    put("grievous", "203_27");
                    put("droideka", "200_80");
                    put("trench", "212_5");
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
    public void MuunilinstBankingClanHeadquartersStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("hq").getBlueprint();

        assertEquals("Muunilinst: Banking Clan Headquarters", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.PLANET);
            add(Icon.EXTERIOR_SITE);
            add(Icon.LIGHT_FORCE);
            add(Icon.DARK_FORCE);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(2, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }


    @Test
    public void MuunilinstBankingClanHeadquartersUniqueSeparatistDeploysMinus1HereWhenYouControlWithSeparatist() {
        var scn = GetScenario();
        var hq = scn.GetDSCard("hq");
        var grievous = scn.GetDSCard("grievous");
        var droideka = scn.GetDSCard("droideka");

        scn.StartGame();
        scn.MoveLocationToTable(hq);
        scn.MoveCardsToLocation(hq, droideka);
        scn.MoveCardsToHand(grievous);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int before = scn.GetDSForcePileCount();
        scn.DSDeployCardAndPassResponses(grievous, hq);

        assertAtLocation(hq, grievous);
        assertEquals(before - scn.GetDeployCost(grievous) + 1, scn.GetDSForcePileCount());
    }

    @Test
    public void MuunilinstBankingClanHeadquartersNoDeployReductionWithoutSeparatistControl() {
        var scn = GetScenario();
        var hq = scn.GetDSCard("hq");
        var grievous = scn.GetDSCard("grievous");

        scn.StartGame();
        scn.MoveLocationToTable(hq);
        scn.MoveCardsToHand(grievous);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int before = scn.GetDSForcePileCount();
        scn.DSDeployCardAndPassResponses(grievous, hq);

        assertAtLocation(hq, grievous);
        assertEquals(before - scn.GetDeployCost(grievous), scn.GetDSForcePileCount());
    }

    @Test
    public void MuunilinstBankingClanHeadquartersNonUniqueSeparatistDoesNotDeployMinus1() {
        var scn = GetScenario();
        var hq = scn.GetDSCard("hq");
        var grievous = scn.GetDSCard("grievous");
        var droideka = scn.GetDSCard("droideka");

        scn.StartGame();
        scn.MoveLocationToTable(hq);
        scn.MoveCardsToLocation(hq, grievous);
        scn.MoveCardsToHand(droideka);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int before = scn.GetDSForcePileCount();
        scn.DSDeployCardAndPassResponses(droideka, hq);

        assertAtLocation(hq, droideka);
        assertEquals(before - scn.GetDeployCost(droideka), scn.GetDSForcePileCount());
    }

    @Test
    public void MuunilinstBankingClanHeadquartersLightCombatVehiclesAndCloneArmyCardsArePowerPlus1Here() {
        var scn = GetScenario();
        var hq = scn.GetDSCard("hq");
        var cody = scn.GetLSCard("cody");
        var snowspeeder = scn.GetLSCard("snowspeeder");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(hq);
        scn.MoveCardsToLocation(site, cody, snowspeeder, luke);
        int codyOff = scn.GetPower(cody);
        int speederOff = scn.GetPower(snowspeeder);
        int lukeOff = scn.GetPower(luke);
        scn.MoveCardsToLocation(hq, cody, snowspeeder, luke);

        assertEquals(codyOff + 1, scn.GetPower(cody));
        assertEquals(speederOff + 1, scn.GetPower(snowspeeder));
        assertEquals(lukeOff, scn.GetPower(luke));
    }
}
