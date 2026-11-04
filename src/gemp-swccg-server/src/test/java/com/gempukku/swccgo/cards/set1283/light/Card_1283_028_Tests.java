package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1283_028_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("blaster", "1283_28");
                    put("boshek", "1_4");
                    put("cctrooper", "5_3");
                    put("han", "1_11");
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
    public void CloudCityBlasterVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("blaster").getBlueprint();

        assertEquals("Cloud City Blaster", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CloudCityBlasterDeploysOnlyOnYourAlien() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var boshek = scn.GetLSCard("boshek");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boshek, han);
        scn.MoveCardsToLSHand(blaster);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSDeployCard(blaster);
        assertTrue(scn.LSHasCardChoiceAvailable(boshek));
        assertFalse(scn.LSHasCardChoiceAvailable(han));
    }

    private VirtualTableScenario fire(String carrier, int destiny) {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        PhysicalCardImpl bearer = scn.GetLSCard(carrier);
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, bearer, vader);
        scn.AttachCardsTo(bearer, blaster);
        WeaponTestSupport.lsBattleAndFire(scn, site, blaster, vader, destiny);
        return scn;
    }

    @Test
    public void CloudCityBlasterHitsWhenDestinyPlusOneExceedsDefenseValue() {
        var scn = fire("boshek", 6);
        assertTrue(scn.GetDSCard("vader").isHit());
    }

    @Test
    public void CloudCityBlasterMissesWhenDestinyPlusOneDoesNotExceedDefenseValue() {
        var scn = fire("boshek", 5);
        assertFalse(scn.GetDSCard("vader").isHit());
    }

    @Test
    public void CloudCityBlasterMakesYourTotalPowerPlusOneWhenFiredInBattle() {
        var scn = fire("boshek", 0);
        assertEquals(scn.GetPower(scn.GetLSCard("boshek")) + 1, scn.GetLSTotalPower());
    }

    @Test
    public void CarriedByATrooperOneIsTreatedAsTwoForDestinyAndTotalPower() {
        var scn = fire("cctrooper", 5);
        assertTrue(scn.GetDSCard("vader").isHit());

        var scn2 = fire("cctrooper", 0);
        assertEquals(scn2.GetPower(scn2.GetLSCard("cctrooper")) + 2, scn2.GetLSTotalPower());
    }
}
