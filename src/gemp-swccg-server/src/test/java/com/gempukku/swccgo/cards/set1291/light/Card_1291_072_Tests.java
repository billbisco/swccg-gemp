package com.gempukku.swccgo.cards.set1291.light;

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
import com.gempukku.swccgo.common.Zone;
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

public class Card_1291_072_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("atte", "1291_72");
                    put("clonepilot", "1116_8");
                    put("biggs", "1_3");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("rifle", "1210_83");
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
    public void ATTEStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("atte").getBlueprint();

        assertEquals("AT-TE", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(4, card.getDeployCost(), scn.epsilon);
        assertEquals(4, card.getPower(), scn.epsilon);
        assertEquals(4, card.getArmor(), scn.epsilon);
        assertEquals(1, card.getLandspeed(), scn.epsilon);
        assertEquals(5, card.getForfeit(), scn.epsilon);
        assertEquals(1, card.getPilotCapacity());
        assertEquals(4, card.getPassengerCapacity());
        assertEquals("Enclosed.", card.getLore());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.VEHICLE);
        }});
        assertEquals(CardSubtype.COMBAT, card.getCardSubtype());
        scn.BlueprintModelTypeCheck(card, new ArrayList<>() {{
            add(ModelType.AT_TE);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.ENCLOSED);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.VEHICLE);
            add(Icon.EPISODE_I);
            add(Icon.CLONE_ARMY);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ATTEDrawsTwoBattleDestinyWhilePilotedByAClone() {
        var scn = GetScenario();
        var atte = scn.GetLSCard("atte");
        var clonepilot = scn.GetLSCard("clonepilot");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, atte, trooper);
        scn.BoardAsPilot(atte, clonepilot);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertEquals(2, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void ATTENoExtraBattleDestinyWhilePilotedByANonClone() {
        var scn = GetScenario();
        var atte = scn.GetLSCard("atte");
        var biggs = scn.GetLSCard("biggs");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, atte, trooper);
        scn.BoardAsPilot(atte, biggs);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertEquals(0, scn.GetLSBattleDestinyCount());
    }

    @Test
    public void ATTEIsHitInsteadOfLostBeforeTheDamageSegment() {
        var scn = GetScenario();
        var atte = scn.GetLSCard("atte");
        var clonepilot = scn.GetLSCard("clonepilot");
        var trooper = scn.GetDSCard("trooper");
        var rifle = scn.GetDSCard("rifle");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, atte, trooper);
        scn.BoardAsPilot(atte, clonepilot);
        scn.AttachCardsTo(trooper, rifle);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.DSCardActionAvailable(rifle, "Fire"));
        // destiny 4 + 1 > armor 4: Assault Rifle makes its target lost
        scn.PrepareDSDestiny(4);
        scn.DSUseCardAction(rifle, "Fire");
        scn.DSChooseCard(atte);
        scn.PassWeaponFireWithDestinyDraw();
        scn.PassAllResponses();
        assertAtLocation(site, atte);
        assertTrue(atte.isHit());
    }

    @Test
    public void ATTEImmuneToAttritionLessThanFour() {
        var scn = GetScenario();
        var atte = scn.GetLSCard("atte");
        var clonepilot = scn.GetLSCard("clonepilot");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, atte);
        scn.BoardAsPilot(atte, clonepilot);
        assertEquals(4, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), atte), scn.epsilon);
    }
}
