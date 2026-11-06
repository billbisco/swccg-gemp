package com.gempukku.swccgo.cards.set1230.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertAtLocation;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1230_117_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(new HashMap<>() {{

            put("atst", "1230_117");
            put("chewie", "2_3");
            put("han", "1_11");

            put("luke", "1_19");
            put("door", "8_69");
        }}, new HashMap<>() {{

            put("trooper", "1_194");
            put("vader", "1_168");
            put("atst", "8_172");
            put("walker", "8_170");
        }}, 30, 30, StartingSetup.LSStartingLocation("8_72"), StartingSetup.DefaultDSGroundLocation,
        StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
        StartingSetup.NoLSShields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    @Test
    public void ChewiesATSTStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("atst").getBlueprint();
        assertEquals("Chewie's AT-ST", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(3, card.getLandspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(2, card.getPilotCapacity());
        assertEquals(0, card.getPassengerCapacity());
        assertEquals(0, card.getPilotOrPassengerCapacity());
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.AT_ST);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.LEGACY_BLOCK_3);
            add(Icon.ENDOR);
            add(Icon.SCOMP_LINK);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ChewiesATSTHitsCharacterAndResetsPowerToZero() {
        var scn = BattleReady(true, 0, false);
        assertEquals(6, scn.GetPower(scn.GetDSCard("vader")));
        Target(scn, "vader", 7);
        assertTrue(scn.GetDSCard("vader").isHit());
        assertEquals(0, scn.GetPower(scn.GetDSCard("vader")));
        assertTrue(scn.AwaitingLSWeaponsSegmentActions() || scn.AwaitingDSWeaponsSegmentActions());
    }

    @Test
    public void ChewiesATSTDestinyEqualToCharacterDefenseDoesNotHitOrResetPower() {
        var scn = BattleReady(true, 0, false);
        assertEquals(6, scn.GetDefense(scn.GetDSCard("vader")));
        Target(scn, "vader", 6);
        assertFalse(scn.GetDSCard("vader").isHit());
        assertEquals(6, scn.GetPower(scn.GetDSCard("vader")));
    }

    @Test
    public void ChewiesATSTHitsOpposingATSTAndResetsItsPowerToZero() {
        var scn = BattleReady(true, 1, false);
        assertEquals(4, scn.GetDefense(scn.GetDSCard("atst")));
        Target(scn, "atst", 5);
        assertTrue(scn.GetDSCard("atst").isHit());
        assertEquals(0, scn.GetPower(scn.GetDSCard("atst")));
    }

    @Test
    public void ChewiesATSTDestinyEqualToVehicleArmorDoesNotHit() {
        var scn = BattleReady(true, 1, false);
        int before = scn.GetPower(scn.GetDSCard("atst"));
        Target(scn, "atst", 4);
        assertFalse(scn.GetDSCard("atst").isHit());
        assertEquals(before, scn.GetPower(scn.GetDSCard("atst")));
    }

    @Test
    public void ChewiesATSTCannotUseTargetingAbilityWithoutChewieAboard() {
        var scn = BattleReady(false, 0, false);
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("atst"), "Target character or AT-ST"));
    }

    @Test
    public void ChewiesATSTCannotTargetCharacterAtAdjacentSite() {
        var scn = BattleReady(true, 0, true);
        scn.PrepareLSDestiny(3);
        scn.LSUseCardAction(scn.GetLSCard("atst"), "Target character or AT-ST");
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetDSCard("vader")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSCard("trooper")));
        scn.LSChooseCard(scn.GetDSCard("trooper"));
        scn.PassAllResponses();
        assertTrue(scn.GetDSCard("trooper").isHit());
        assertFalse(scn.GetDSCard("vader").isHit());
    }

    @Test
    public void ChewiesATSTCannotTargetATATAsThoughItWereAnATST() {
        var scn = BattleReady(true, 2, false);
        scn.PrepareLSDestiny(3);
        scn.LSUseCardAction(scn.GetLSCard("atst"), "Target character or AT-ST");
        assertFalse(scn.LSHasCardChoiceAvailable(scn.GetDSCard("walker")));
        assertTrue(scn.LSHasCardChoiceAvailable(scn.GetDSCard("trooper")));
        scn.LSChooseCard(scn.GetDSCard("trooper"));
        scn.PassAllResponses();
        assertTrue(scn.GetDSCard("trooper").isHit());
        assertFalse(scn.GetDSCard("walker").isHit());
    }

    @Test
    public void ChewiesATSTCannotTargetTwiceInOneBattleEvenAfterFailedDraw() {
        var scn = BattleReady(true, 0, false); Target(scn, "trooper", 0);
        assertFalse(scn.GetDSCard("trooper").isHit());
        if (scn.AwaitingDSWeaponsSegmentActions()) scn.DSPass();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.LSCardActionAvailable(scn.GetLSCard("atst"), "Target character or AT-ST"));
    }

    @Test
    public void ChewiesATSTImmunityBelowFourDoesNotRequireChewie() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), scn.GetLSCard("atst"));
        scn.BoardAsPilot(scn.GetLSCard("atst"), scn.GetLSCard("han"));
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), scn.GetLSCard("atst")), scn.epsilon);
    }

    @Test
    public void ChewiesATSTDeploysAwayFromEndorWithoutChewieOrOpposingATST() {
        var scn = GetScenario();
        scn.StartGame();
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(), scn.GetLSCard("han"));
        scn.MoveCardsToLSHand(scn.GetLSCard("atst"));
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.DEPLOY);
        int force = scn.GetLSForcePileCount();
        scn.LSDeployCard(scn.GetLSCard("atst"));
        scn.LSChooseCard(scn.GetDSStartingLocation());
        scn.PassAllResponses();
        assertAtLocation(scn.GetDSStartingLocation(), scn.GetLSCard("atst"));
        assertEquals(force - 2, scn.GetLSForcePileCount());
    }

    private void Target(VirtualTableScenario scn, String target, int destiny) {
        scn.PrepareLSDestiny(destiny);
        scn.LSUseCardAction(scn.GetLSCard("atst"), "Target character or AT-ST");
        scn.LSChooseCard(scn.GetDSCard(target));
        scn.PassAllResponses();
    }

    private VirtualTableScenario BattleReady(boolean chewie, int enemyVehicle, boolean adjacent) {
        var scn = GetScenario();
        scn.StartGame();
        var site = scn.GetLSStartingLocation();
        scn.MoveCardsToLocation(site, scn.GetLSCard("atst"), scn.GetLSCard("luke"), scn.GetDSCard("vader"), scn.GetDSCard("trooper"));
        scn.BoardAsPilot(scn.GetLSCard("atst"), scn.GetLSCard(chewie ? "chewie" : "han"));
        if (enemyVehicle != 0) {
            var vehicle = scn.GetDSCard(enemyVehicle == 1 ? "atst" : "walker");
        scn.MoveCardsToLocation(site, vehicle);
        scn.BoardAsPilot(vehicle, scn.GetDSCard("vader"));
        }
        if (adjacent) { scn.MoveLocationToTable(scn.GetLSCard("door"));
        scn.MoveCardsToLocation(scn.GetLSCard("door"), scn.GetDSCard("vader")); }
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        return scn;
    }
}
