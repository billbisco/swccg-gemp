package com.gempukku.swccgo.cards.set1210.dark;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1210_009_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("nightfall", "1_56");
                }},
                new HashMap<>() {{
                    put("djas", "1210_9");
                    put("rifle", "1_312");
                    put("farm", "1_294");
                    put("tatooine", "1_289");
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
    public void DjasPuhrStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("djas").getBlueprint();

        assertEquals("Djas Puhr", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(4, card.getAbility(), scn.epsilon);
        assertEquals(4, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BOUNTY_HUNTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.WARRIOR);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DjasPuhrPowerPlusThreeUnderNighttimeConditions() {
        var scn = GetScenario();
        var djas = scn.GetDSCard("djas");
        var farm = scn.GetDSCard("farm");
        var tatooine = scn.GetDSCard("tatooine");
        var nightfall = scn.GetLSCard("nightfall");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, djas);
        assertEquals(1, scn.GetPower(djas));
        scn.AttachCardsTo(tatooine, nightfall);
        assertEquals(4, scn.GetPower(djas));
    }

    @Test
    public void DjasPuhrBlasterRifleDeploysFreeAndIsMatchingWeapon() {
        var scn = GetScenario();
        var djas = scn.GetDSCard("djas");
        var rifle = scn.GetDSCard("rifle");
        var farm = scn.GetDSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, djas);
        scn.MoveCardsToDSHand(rifle);
        scn.EnsureDSForcePile(1);
        scn.SkipToDSTurn(Phase.DEPLOY);
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.DSCardPlayAvailable(rifle));
        scn.DSPlayCard(rifle);
        scn.DSChooseCard(djas);
        scn.PassAllResponses();
        assertEquals(forceBefore, scn.GetDSForcePileCount());
        assertEquals(djas, rifle.getAttachedTo());
        assertTrue(scn.game().getModifiersQuerying().isMatchingPair(scn.gameState(), djas, rifle));
    }

    @Test
    public void DjasPuhrImmuneToAttritionLessThanThreeUnarmedAndFiveArmed() {
        var scn = GetScenario();
        var djas = scn.GetDSCard("djas");
        var rifle = scn.GetDSCard("rifle");
        var farm = scn.GetDSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, djas);
        assertEquals(3, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), djas), scn.epsilon);
        scn.AttachCardsTo(djas, rifle);
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), djas), scn.epsilon);
    }

    @Test
    public void DjasPuhrBlasterRifleFiresForFree() {
        var scn = GetScenario();
        var djas = scn.GetDSCard("djas");
        var rifle = scn.GetDSCard("rifle");
        var luke = scn.GetLSCard("luke");
        var farm = scn.GetDSCard("farm");

        scn.StartGame();
        scn.MoveLocationToTable(farm);
        scn.MoveCardsToLocation(farm, djas, luke);
        scn.AttachCardsTo(djas, rifle);
        scn.EnsureDSForcePile(1);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(farm);
        scn.PassAllResponses();
        int forceBefore = scn.GetDSForcePileCount();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(rifle, "Fire"));
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertEquals(forceBefore, scn.GetDSForcePileCount());
        assertEquals(Zone.ATTACHED, rifle.getZone());
    }
}
