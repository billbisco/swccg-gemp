package com.gempukku.swccgo.cards.set1115.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1115_018_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("learn", "1115_18");
                    put("courage", "1_119");
                    put("roar", "2_58");
                    put("chewie", "2_3");
                    put("luke", "1_19");
                    put("obi", "1_21");
                    put("boshek", "1_4");
                    put("ellorrs", "1_49");
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
    public void LearnAboutTheForceLukeStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("learn").getBlueprint();

        assertEquals("Learn About The Force, Luke", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
}});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_15, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LearnAboutTheForceLukeDeploysOnTable() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");

        scn.StartGame();
        scn.MoveCardsToLSHand(learn);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSCardPlayAvailable(learn) || scn.LSDeployAvailable(learn));
        scn.LSPlayCard(learn);
        scn.PassAllResponses();

        assertInZone(Zone.SIDE_OF_TABLE, learn);
    }

    @Test
    public void LearnAboutTheForceLukeMakesNonJabbasPalaceAloneInterruptsUsed() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");
        var courage = scn.GetLSCard("courage");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveCardsToLSSideOfTable(learn);
        scn.MoveCardsToLocation(site, luke, vader);
        scn.MoveCardsToLSHand(courage);
        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassBattleStartResponses();
        if (scn.AwaitingDSWeaponsSegmentActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSWeaponsSegmentActions() || lsHasCardActions(scn));
        assertTrue(!scn.game().getModifiersQuerying()
                .getModifiersAffectingCard(scn.gameState(), com.gempukku.swccgo.logic.modifiers.ModifierType.USED_INTERRUPT, courage)
                .isEmpty());
        assertTrue(scn.LSPlayUsedInterruptAvailable(courage)
                || scn.LSCardActionAvailable(courage, "Add one battle destiny")
                || scn.LSCardPlayAvailable(courage));
        if (scn.LSPlayUsedInterruptAvailable(courage)) {
            scn.LSPlayUsedInterrupt(courage);
        } else if (scn.LSCardActionAvailable(courage, "Add one battle destiny")) {
            scn.LSUseCardAction(courage, "Add one battle destiny");
        } else {
            scn.LSPlayCard(courage);
        }
        scn.PassAllResponses();

        assertInZone(Zone.USED_PILE, courage);
    }

    @Test
    public void LearnAboutTheForceLukeLimitsBattleDestinyToTwoWhenAloneInterruptPlayedDuringBattle() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");
        var roar = scn.GetLSCard("roar");
        var chewie = scn.GetLSCard("chewie");
        var luke = scn.GetLSCard("luke");
        var obi = scn.GetLSCard("obi");
        var boshek = scn.GetLSCard("boshek");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        passStartingInterrupts(scn);
        scn.MoveCardsToLSSideOfTable(learn);
        scn.MoveCardsToLocation(site, chewie, vader);
        scn.MoveCardsToLSHand(roar);
        scn.EnsureLSForcePile(4);
        scn.SkipToLSTurn();
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassBattleStartResponses();
        if (scn.AwaitingDSWeaponsSegmentActions()) {
            scn.DSPass();
        }
        assertTrue(scn.AwaitingLSWeaponsSegmentActions() || lsHasCardActions(scn));
        assertTrue(scn.LSCardActionAvailable(roar, "Add two battle destiny")
                || scn.LSPlayUsedInterruptAvailable(roar)
                || scn.LSCardPlayAvailable(roar, "Add two battle destiny"));
        if (scn.LSCardActionAvailable(roar, "Add two battle destiny")) {
            scn.LSUseCardAction(roar, "Add two battle destiny");
        } else if (scn.LSPlayUsedInterruptAvailable(roar)) {
            scn.LSPlayUsedInterrupt(roar);
        } else {
            scn.LSPlayCard(roar, "Add two battle destiny");
        }
        scn.PassAllResponses();

        assertEquals(2, scn.GetLSBattleDestinyLimit());
    }

    @Test
    public void LearnAboutTheForceLukeAddsOneToForceDrainsWhereYouHaveACardWithSkillInGameText() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");
        var luke = scn.GetLSCard("luke");
        var ellorrs = scn.GetLSCard("ellorrs");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(learn);
        scn.MoveCardsToLocation(site, luke);
        scn.AttachCardsTo(luke, ellorrs);
        scn.EnsureDSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSForceDrainAvailable(site));
        scn.LSForceDrainAt(site);
        scn.DSPass();
        scn.PassForceDrainStartResponses();

        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void LearnAboutTheForceLukeAddsOneToBattleDestinyDrawsWhereYouHaveACardWithSkillInGameText() {
        var scn = GetScenario();
        var learn = scn.GetLSCard("learn");
        var luke = scn.GetLSCard("luke");
        var ellorrs = scn.GetLSCard("ellorrs");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(learn);
        scn.MoveCardsToLocation(site, luke, vader);
        scn.AttachCardsTo(luke, ellorrs);
        scn.PrepareLSDestiny(4);
        scn.PrepareDSDestiny(0);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        scn.SkipToEndOfPowerSegment(true);

        assertEquals(5, scn.GetLSTotalDestiny());
    }

    private boolean lsHasCardActions(VirtualTableScenario scn) {
        return scn.LSGetDecision() != null && scn.LSGetADParam("cardId") != null;
    }

    private void passStartingInterrupts(VirtualTableScenario scn) {
        if (scn.DSDecisionAvailable("Choose starting interrupt")) {
            scn.DSPass();
        }
        if (scn.LSDecisionAvailable("Choose starting interrupt")) {
            scn.LSPass();
        }
    }
}
