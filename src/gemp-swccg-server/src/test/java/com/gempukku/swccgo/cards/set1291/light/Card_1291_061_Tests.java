package com.gempukku.swccgo.cards.set1291.light;

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

public class Card_1291_061_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("muunilinst", "1291_60");
                    put("harnaidan", "1291_61");
                    put("rogue", "3_66");
                    put("luke", "1_19");
                    put("cannon", "1220_168");
                }},
                new HashMap<>() {{
                    put("vader", "1_168");
                    put("grievous", "203_27");
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
    public void MuunilinstCityOfHarnaidanStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("harnaidan").getBlueprint();

        assertEquals("Muunilinst: City Of Harnaidan", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
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
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }


    @Test
    public void MuunilinstCityOfHarnaidanLightCombatVehiclesHereArePowerPlus1() {
        var scn = GetScenario();
        var harnaidan = scn.GetLSCard("harnaidan");
        var rogue = scn.GetLSCard("rogue");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("muunilinst"));
        scn.MoveLocationToTable(harnaidan);
        scn.MoveCardsToLocation(site, rogue);
        scn.BoardAsPilot(rogue, luke);
        int off = scn.GetPower(rogue);
        scn.MoveCardsToLocation(harnaidan, rogue);

        assertEquals(off + 1, scn.GetPower(rogue));
    }

    @Test
    public void MuunilinstCityOfHarnaidanLightCombatVehiclesHereAdd1ToWeaponDestiny() {
        var scn = GetScenario();
        var harnaidan = scn.GetLSCard("harnaidan");
        var rogue = scn.GetLSCard("rogue");
        var luke = scn.GetLSCard("luke");
        var cannon = scn.GetLSCard("cannon");
        var vader = scn.GetDSCard("vader");

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("muunilinst"));
        scn.MoveLocationToTable(harnaidan);
        scn.MoveCardsToLocation(harnaidan, rogue, vader);
        scn.BoardAsPilot(rogue, luke);
        scn.AttachCardsTo(rogue, cannon);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(harnaidan);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        // Dual Laser Cannon hits a character if destiny + 2 > defense value; Vader's is 6.
        scn.PrepareLSDestiny(4);
        scn.LSUseCardAction(cannon, "Fire");
        scn.LSChooseCard(vader);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertEquals(0, scn.GetForfeit(vader));
    }

    @Test
    public void MuunilinstCityOfHarnaidanWeaponDestinyBonusOnlyHere() {
        var scn = GetScenario();
        var harnaidan = scn.GetLSCard("harnaidan");
        var rogue = scn.GetLSCard("rogue");
        var luke = scn.GetLSCard("luke");
        var cannon = scn.GetLSCard("cannon");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("muunilinst"));
        scn.MoveLocationToTable(harnaidan);
        scn.MoveCardsToLocation(site, rogue, vader);
        scn.BoardAsPilot(rogue, luke);
        scn.AttachCardsTo(rogue, cannon);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.PrepareLSDestiny(4);
        scn.LSUseCardAction(cannon, "Fire");
        scn.LSChooseCard(vader);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertTrue(scn.GetForfeit(vader) > 0);
    }

    @Test
    public void MuunilinstCityOfHarnaidanDarkSeparatistCardsMoveToAndFromHereForFree() {
        var scn = GetScenario();
        var harnaidan = scn.GetLSCard("harnaidan");
        var grievous = scn.GetDSCard("grievous");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();
        var mq = scn.game().getModifiersQuerying();

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("muunilinst"));
        scn.MoveLocationToTable(harnaidan);
        scn.MoveCardsToLocation(site, grievous, vader);

        assertEquals(0, Math.round(mq.getMoveUsingLandspeedCost(scn.gameState(), grievous, site, harnaidan, false, 0)));
        assertEquals(0, Math.round(mq.getMoveUsingLandspeedCost(scn.gameState(), grievous, harnaidan, site, false, 0)));
        assertEquals(1, Math.round(mq.getMoveUsingLandspeedCost(scn.gameState(), vader, site, harnaidan, false, 0)));
    }

    @Test
    public void MuunilinstCityOfHarnaidanDarkFreeMovementEndsWhenOpponentControls() {
        var scn = GetScenario();
        var harnaidan = scn.GetLSCard("harnaidan");
        var grievous = scn.GetDSCard("grievous");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();
        var mq = scn.game().getModifiersQuerying();

        scn.StartGame();
        scn.MoveLocationToTable(scn.GetLSCard("muunilinst"));
        scn.MoveLocationToTable(harnaidan);
        scn.MoveCardsToLocation(site, grievous);
        scn.MoveCardsToLocation(harnaidan, luke);

        assertEquals(1, Math.round(mq.getMoveUsingLandspeedCost(scn.gameState(), grievous, site, harnaidan, false, 0)));
    }
}
