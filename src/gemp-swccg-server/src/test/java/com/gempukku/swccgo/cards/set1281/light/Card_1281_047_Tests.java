package com.gempukku.swccgo.cards.set1281.light;

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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1281_047_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("rifle", "1281_47");
                    put("kyle", "1281_28");
                    put("luck", "1281_37");
                    put("han", "1281_27");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("tempest", "8_171"); // Tempest Scout: combat vehicle, armor 4
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
    public void KyleKatarnsBlasterRifleStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("rifle").getBlueprint();

        assertEquals("Kyle Katarn's Blaster Rifle", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER_RIFLE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void KyleKatarnsBlasterRifleDeploysOnKyleKatarn() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var kyle = scn.GetLSCard("kyle");
        var han = scn.GetLSCard("han");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, kyle, han);
        scn.MoveCardsToLSHand(rifle);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(rifle));
        scn.LSDeployCard(rifle);
        assertTrue(scn.LSHasCardChoiceAvailable(kyle));
        assertFalse(scn.LSHasCardChoiceAvailable(han));
        scn.LSChooseCard(kyle);
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(kyle, rifle));
    }

    @Test
    public void KyleKatarnsBlasterRifleHitsACharacterAndSetsForfeitToZero() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var kyle = scn.GetLSCard("kyle");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, kyle, trooper);
        scn.AttachCardsTo(kyle, rifle);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(5);

        assertTrue(scn.LSCardActionAvailable(rifle, "Fire"));
        scn.LSUseCardAction(rifle, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertTrue(trooper.isHit());
        assertEquals(0, scn.GetForfeit(trooper));
    }

    @Test
    public void KyleKatarnsBlasterRifleDrawsTwoDestiniesWhenScoundrelsLuckIsOnTable() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var kyle = scn.GetLSCard("kyle");
        var luck = scn.GetLSCard("luck");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, kyle, trooper);
        scn.AttachCardsTo(kyle, rifle);
        scn.MoveCardsToLSSideOfTable(luck);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(1);
        scn.PrepareLSDestiny(1);

        assertTrue(scn.LSCardActionAvailable(rifle, "Fire"));
        scn.LSUseCardAction(rifle, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassWeaponFireWithDestinyDraw(2);
        scn.PassAllResponses();

        assertTrue(trooper.isHit());
        assertEquals(0, scn.GetForfeit(trooper));
    }

    @Test
    public void KyleKatarnsBlasterRifleHitsAVehicleIfDestinyMinusTwoIsGreaterThanDefenseValue() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var kyle = scn.GetLSCard("kyle");
        var trooper = scn.GetDSCard("trooper");
        var tempest = scn.GetDSCard("tempest");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, kyle, trooper, tempest);
        scn.AttachCardsTo(kyle, rifle);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(7 /* 7 - 2 = 5 > 4 */);

        assertEquals(4, scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(), tempest), scn.epsilon);
        // Two fire actions: vs. character/creature (first) and vs. vehicle (second).
        assertEquals(2, scn.GetLSAvailableActions().stream().filter(a -> a.startsWith("Fire Kyle Katarn's Blaster Rifle")).count());
        scn.LSDecided(scn.LSGetADParamAsList("actionId").get(scn.GetLSAvailableActions().lastIndexOf("Fire Kyle Katarn's Blaster Rifle")));
        scn.LSChooseCard(tempest);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertTrue(tempest.isHit());
    }

    @Test
    public void KyleKatarnsBlasterRifleMissesAVehicleIfDestinyMinusTwoIsNotGreaterThanDefenseValue() {
        var scn = GetScenario();
        var rifle = scn.GetLSCard("rifle");
        var kyle = scn.GetLSCard("kyle");
        var trooper = scn.GetDSCard("trooper");
        var tempest = scn.GetDSCard("tempest");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, kyle, trooper, tempest);
        scn.AttachCardsTo(kyle, rifle);
        scn.LSActivateForceCheat(2);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareLSDestiny(5 /* 5 > 4, but 5 - 2 = 3 */);

        assertEquals(4, scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(), tempest), scn.epsilon);
        // Two fire actions: vs. character/creature (first) and vs. vehicle (second).
        assertEquals(2, scn.GetLSAvailableActions().stream().filter(a -> a.startsWith("Fire Kyle Katarn's Blaster Rifle")).count());
        scn.LSDecided(scn.LSGetADParamAsList("actionId").get(scn.GetLSAvailableActions().lastIndexOf("Fire Kyle Katarn's Blaster Rifle")));
        scn.LSChooseCard(tempest);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();

        assertFalse(tempest.isHit());
    }
}
