package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_026_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("camp", "1272_26");
                    put("luke", "1_19");
                    put("obiwan", "1_21");
                    put("saber", "1_155");
                    put("klorslug", "1_53");
                    put("aces", "1271_45");
                    put("junk", "1_28");
                }},
                new HashMap<>() {{
                    put("maul", "14_77");
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

    protected VirtualTableScenario GetWatchYourStepScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("camp", "1272_26");
                }},
                new HashMap<>() {{
                }},
                10, 10,
                new StartingSetup() {
                    @Override
                    public HashMap<String, String> Cards() {
                        return new HashMap<>() {{
                            put("watch", "10_26");
                            put("cantina", "1_128");
                            put("db94", "1_129");
                            put("tatooine", "1_127");
                        }};
                    }

                    @Override
                    public void Setup(VirtualTableScenario scn) {
                        while (scn.LSDecisionAvailable("On which side")) {
                            scn.LSChoose("Left");
                        }
                    }
                },
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void TheCampVStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("camp").getBlueprint();

        assertEquals(Title.The_Camp, card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.EPISODE_I);
            add(Icon.TATOOINE);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void TheCampVIsImmuneToAlter() {
        var scn = GetScenario();
        var camp = scn.GetLSCard("camp");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(camp);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), camp, Title.Alter));
    }

    @Test
    public void TheCampVMayDeployWithoutYourReflectionsIIObjective() {
        var scn = GetScenario();
        var camp = scn.GetLSCard("camp");

        scn.StartGame();
        scn.MoveCardsToLSHand(camp);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        assertTrue(scn.LSDeployAvailable(camp));
    }

    @Test
    public void TheCampVMayNotDeployWhileYourReflectionsIIObjectiveIsOnTable() {
        var scn = GetWatchYourStepScenario();
        var camp = scn.GetLSCard("camp");
        var watch = scn.GetLSCard("watch");

        scn.StartGame();
        assertInZone(Zone.SIDE_OF_TABLE, watch);
        scn.MoveCardsToLSHand(camp);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureLSForcePile(5);
        assertFalse(scn.LSDeployAvailable(camp));
    }

    private void startBattleAgainstMaul(VirtualTableScenario scn, PhysicalCardImpl jedi) {
        var camp = scn.GetLSCard("camp");
        var saber = scn.GetLSCard("saber");
        var maul = scn.GetDSCard("maul");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(camp);
        scn.MoveCardsToLocation(site, jedi, maul);
        scn.AttachCardsTo(jedi, saber);
        scn.DSActivateForceCheat(4);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
    }

    @Test
    public void TheCampVLightsabersMayNotBeSwungTwiceWhenMaulIsInBattleAtASite() {
        var scn = GetScenario();
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var maul = scn.GetDSCard("maul");

        //test1: lightsabers there may fire only once
        //test2: Maul's permanent weapon lightsaber may fire only once
        //test3: Luke of ability = 4 does not stop it
        startBattleAgainstMaul(scn, luke);
        assertTrue(scn.IsBattleStarted());
        assertTrue(scn.game().getModifiersQuerying().mayFireOnlyOncePerBattle(scn.gameState(), saber)); //test1 //test3
        assertTrue(scn.game().getModifiersQuerying().mayFireOnlyOncePerBattle(scn.gameState(), maul)); //test2
    }

    @Test
    public void TheCampVLightsabersMaySwingTwiceWithYourUniqueCharacterOfAbilityMoreThanThreeThere() {
        var scn = GetScenario();
        var obiwan = scn.GetLSCard("obiwan");
        var saber = scn.GetLSCard("saber");
        var maul = scn.GetDSCard("maul");

        startBattleAgainstMaul(scn, obiwan);
        assertTrue(scn.IsBattleStarted());
        assertFalse(scn.game().getModifiersQuerying().mayFireOnlyOncePerBattle(scn.gameState(), saber));
        assertFalse(scn.game().getModifiersQuerying().mayFireOnlyOncePerBattle(scn.gameState(), maul));
    }

    @Test
    public void TheCampVDoesNotLimitLightsabersOutsideBattle() {
        var scn = GetScenario();
        var camp = scn.GetLSCard("camp");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var maul = scn.GetDSCard("maul");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(camp);
        scn.MoveCardsToLocation(site, luke, maul);
        scn.AttachCardsTo(luke, saber);
        assertFalse(scn.game().getModifiersQuerying().mayFireOnlyOncePerBattle(scn.gameState(), saber));
    }

    @Test
    public void TheCampVOncePerGameMayTakeKlorslugOrSetupCardIntoHand() {
        var scn = GetScenario();
        var camp = scn.GetLSCard("camp");
        var klorslug = scn.GetLSCard("klorslug");
        var aces = scn.GetLSCard("aces");
        var junk = scn.GetLSCard("junk");

        //test1: K'lor'slug or a [Setup] card may be chosen
        //test2: other cards may not be chosen
        //test3: once per game
        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(camp);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(klorslug, aces, junk);
        assertTrue(scn.LSCardActionAvailable(camp, "Take"));
        scn.LSUseCardAction(camp, "Take");
        scn.PassAllResponses();
        assertTrue(lsReserveSearchSelectable(scn, klorslug)); //test1
        assertTrue(lsReserveSearchSelectable(scn, aces)); //test1
        assertFalse(lsReserveSearchSelectable(scn, junk)); //test2
        scn.ChooseCardBPFromSelection(scn.LS, aces);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, aces); //test1
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(camp, "Take")); //test3
    }

    private boolean lsReserveSearchSelectable(VirtualTableScenario scn, PhysicalCardImpl card) {
        String[] blueprintIds = scn.LSGetADParam("blueprintId");
        String[] selectable = scn.LSGetADParam("selectable");
        if (blueprintIds == null || selectable == null)
            return false;
        String id = card.getBlueprintId(true);
        for (int i = 0; i < blueprintIds.length; i++) {
            if (id.equals(blueprintIds[i]) && "true".equals(selectable[i]))
                return true;
        }
        return false;
    }
}
