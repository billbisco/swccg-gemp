package com.gempukku.swccgo.cards.set1273.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.cards.legacy.MissionTestSupport;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1273_013_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("obiwan", "1_21"); // Obi-Wan Kenobi (Jedi)
                    put("saber", "1_155"); // Jedi Lightsaber
                    put("prison", "1116_7"); // Blockade Flagship: Prison
                    put("palpatine", "12_28"); // Senator Palpatine
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("prisoner", "1273_13");
                    put("quarters", "1273_17");
                    put("platform", "601_150"); // Coruscant: Private Platform (Docking Bay)
                    put("trooper", "1_194");
                    put("original", "2273_13");
                    put("current", "1273_13");
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

    /** Stand-in for [Virtual Block 5] We Have A Plan (not coded yet): matched by title + [Virtual Block 5] icon. */
    private static AbstractObjective vb5WeHaveAPlan() {
        return new AbstractObjective(Side.LIGHT, 0, Title.We_Have_A_Plan, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V) {
        };
    }

    /** Stand-in titled A Stunning Move (the deploy requirement matches the title only). */
    private static AbstractNormalEffect stunningMove() {
        return new AbstractNormalEffect(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.A_Stunning_Move, Uniqueness.UNIQUE, ExpansionSet.PREMIERE, Rarity.C1) {
        };
    }

    @Test
    public void InsidiousPrisonerStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("prisoner").getBlueprint();
        assertEquals("Insidious Prisoner", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EPIC_EVENT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPIC_EVENT);
            add(Icon.EPISODE_I);
            add(Icon.CORUSCANT);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    private boolean deployAvailable(boolean stunningMoveOnTable) {
        var scn = GetScenario();
        var prisoner = scn.GetDSCard("prisoner");
        scn.StartGame();
        scn.MoveLocationToTable(scn.GetDSCard("quarters"));
        if (stunningMoveOnTable) {
            MissionTestSupport.putOnTable(scn, 7314, scn.DS, stunningMove());
        }
        scn.MoveCardsToDSHand(prisoner);
        scn.SkipToDSTurn(Phase.DEPLOY);
        return scn.DSDeployAvailable(prisoner);
    }

    @Test
    public void InsidiousPrisonerDeploysOnlyIfAStunningMoveOnTable() {
        assertTrue(deployAvailable(true));
        assertFalse(deployAvailable(false));
    }

    /** Puts A Stunning Move and Palpatine's Quarters on table and skips to the Dark Side deploy phase. */
    protected VirtualTableScenario stunningMoveSetup(String... dsHand) {
        var scn = GetScenario();
        scn.StartGame();
        for (String key : dsHand)
            scn.MoveCardsToDSHand(scn.GetDSCard(key));
        scn.MoveLocationToTable(scn.GetDSCard("quarters"));
        MissionTestSupport.putOnTable(scn, 7314, scn.DS, stunningMove());
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(6);
        return scn;
    }

    /**
     * Deploys Insidious Prisoner from Reserve Deck on Palpatine's Quarters during the start-of-game phase, as
     * A Stunning Move does before starting hands are drawn (the phase is replayed for this one action).
     */
    private void deployPrisonerAtStartOfGame(VirtualTableScenario scn) {
        var prisoner = scn.GetDSCard("prisoner");
        scn.MoveCardsToTopOfOwnReserveDeck(prisoner);
        var action = new TopLevelGameTextAction(prisoner, scn.DS, prisoner.getCardId());
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                game.getGameState().setCurrentPhase(Phase.PLAY_STARTING_CARDS);
            }
        });
        action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action, scn.DS, Filters.sameCardId(prisoner),
                Filters.title("Coruscant: Palpatine's Quarters"), true, null, null, false));
        scn.carryOutEffectInPhaseActionByPlayer(scn.DS, action);
    }

    /** Resolves decisions (choosing the first selectable cards) until the player is offered the action, or gives up. */
    protected static boolean offeredAction(VirtualTableScenario scn, String player, String text) {
        for (int i = 0; i < 40; i++) {
            var mine = scn.GetAwaitingDecision(player);
            String[] texts = mine == null ? null : mine.getDecisionParameters().get("actionText");
            if (texts != null && java.util.Arrays.stream(texts).anyMatch(t -> t != null && t.contains(text)))
                return true;
            String decider = scn.GetDecidingPlayer();
            var d = decider == null ? null : scn.GetAwaitingDecision(decider);
            if (d == null)
                return false;
            var params = d.getDecisionParameters();
            if (params.get("min") != null && params.get("cardId") != null && params.get("actionText") == null) {
                String[] ids = params.get("cardId");
                String[] selectable = params.get("selectable");
                int max = Integer.parseInt(params.get("max")[0]);
                java.util.List<String> chosen = new java.util.ArrayList<>();
                for (int j = 0; j < ids.length && chosen.size() < max; j++)
                    if (selectable == null || "true".equals(selectable[j]))
                        chosen.add(ids[j]);
                scn.PlayerDecided(decider, String.join(",", chosen));
            } else if (params.get("results") != null) {
                scn.PlayerDecided(decider, "0");
            } else {
                scn.PlayerPass(decider);
            }
        }
        return false;
    }

    @Test
    public void InsidiousPrisonerOpponentMayDeployJediAndLightsaberHereBeforeDrawingStartingHand() {
        //test1: deployed at start of game: opponent may deploy a Jedi here from Reserve Deck
        //test2: and a lightsaber on that Jedi
        var scn = stunningMoveSetup();
        var quarters = scn.GetDSCard("quarters");
        var obiwan = scn.GetLSCard("obiwan");
        var saber = scn.GetLSCard("saber");
        scn.MoveCardsToTopOfLSReserveDeck(obiwan);
        deployPrisonerAtStartOfGame(scn);
        assertTrue(offeredAction(scn, scn.LS, "Deploy a Jedi here")); //test1
        scn.LSChooseAction("Deploy a Jedi here");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> obiwan.getZone() == Zone.AT_LOCATION);
        assertEquals(quarters, obiwan.getAtLocation()); //test1

        scn.MoveCardsToTopOfLSReserveDeck(saber);
        assertTrue(offeredAction(scn, scn.LS, "Deploy a lightsaber here")); //test2
        scn.LSChooseAction("Deploy a lightsaber here");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> saber.getZone() == Zone.ATTACHED);
        assertTrue(scn.IsAttachedTo(obiwan, saber)); //test2
    }

    @Test
    public void InsidiousPrisonerDeployedDuringATurnLetsOpponentDeployNothing() {
        // Master List: only "before drawing a starting hand" (the sheet text had no timing)
        var scn = stunningMoveSetup("prisoner");
        var prisoner = scn.GetDSCard("prisoner");
        scn.MoveCardsToTopOfLSReserveDeck(scn.GetLSCard("obiwan"));
        scn.DSDeployCard(prisoner);
        assertFalse(offeredAction(scn, scn.LS, "Deploy a Jedi here"));
        assertTrue(scn.IsAttachedTo(scn.GetDSCard("quarters"), prisoner));
    }

    @Test
    public void InsidiousPrisonerHasTheMasterListText() {
        var card = GetScenario().GetDSCard("current").getBlueprint();
        assertEquals("If A Stunning Move on table, deploy on Palpatine's Quarters (if [Virtual Block 5] We Have A Plan on table, this card always relocates to Palpatine if possible (even if a captive)); before drawing a starting hand opponent may deploy a Jedi and/or a lightsaber here from Reserve Deck; reshuffle. Evil Is Everywhere: Opponent may not Force drain here. Adds one [Dark Side] at same site. If just lost, relocate to Blockade Flagship: Prison. Once per turn, if a player controls this site, they may have this card (unless on Palpatine) follow their first character to move from here using landspeed or docking bay transit to a non-shielded site.",
                card.getGameText());
        assertEquals(null, card.getLegacyVersion());
    }

    @Test
    public void InsidiousPrisonerAndItsOriginalVersionAreTheSameUniqueCard() {
        for (boolean currentOnTable : java.util.List.of(true, false)) {
            for (boolean otherOut : java.util.List.of(false, true)) {
                var scn = GetScenario();
                var onTable = scn.GetDSCard(currentOnTable ? "current" : "original");
                var inHand = scn.GetDSCard(currentOnTable ? "original" : "current");
                scn.StartGame();
                scn.MoveLocationToTable(scn.GetDSCard("quarters"));
                MissionTestSupport.putOnTable(scn, 7314, scn.DS, stunningMove());
                if (otherOut)
                    scn.AttachCardsTo(scn.GetDSCard("quarters"), onTable);
                scn.MoveCardsToDSHand(inHand);
                scn.SkipToDSTurn(Phase.DEPLOY);
                assertEquals("current on table=" + currentOnTable + ", other version out=" + otherOut,
                        !otherOut, scn.DSDeployAvailable(inHand));
            }
        }
    }

    @Test
    public void InsidiousPrisonerOpponentMayNotForceDrainHereAndAddsDarkIcon() {
        var scn = GetScenario();
        var quarters = scn.GetDSCard("quarters");
        var prisoner = scn.GetDSCard("prisoner");
        scn.StartGame();
        scn.MoveLocationToTable(quarters);
        scn.MoveCardsToLocation(quarters, scn.GetLSCard("obiwan"));
        int darkIcons = scn.GetIconCount(quarters, Icon.DARK_FORCE);
        scn.AttachCardsTo(quarters, prisoner);
        assertEquals(darkIcons + 1, scn.GetIconCount(quarters, Icon.DARK_FORCE));
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSForceDrainAvailable(quarters));
    }

    @Test
    public void InsidiousPrisonerRelocatesToBlockadeFlagshipPrisonIfLost() {
        var scn = GetScenario();
        var quarters = scn.GetDSCard("quarters");
        var prisoner = scn.GetDSCard("prisoner");
        var prison = scn.GetLSCard("prison");
        scn.StartGame();
        scn.MoveLocationToTable(quarters);
        scn.MoveLocationToTable(prison);
        scn.AttachCardsTo(quarters, prisoner);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSExecuteAdHocEffect(quarters, new LoseCardFromTableEffect(new TopLevelGameTextAction(quarters, scn.DS, quarters.getCardId()), prisoner));
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> prisoner.getAttachedTo() == prison);
        assertTrue(scn.IsAttachedTo(prison, prisoner));
    }

    @Test
    public void InsidiousPrisonerAlwaysRelocatesToPalpatineWhileVirtualBlock5WeHaveAPlanOnTable() {
        var scn = GetScenario();
        var quarters = scn.GetDSCard("quarters");
        var prisoner = scn.GetDSCard("prisoner");
        var palpatine = scn.GetLSCard("palpatine");
        scn.StartGame();
        scn.MoveLocationToTable(quarters);
        scn.AttachCardsTo(quarters, prisoner);
        scn.MoveCardsToLocation(scn.GetLSStartingLocation(), palpatine);
        MissionTestSupport.putOnTable(scn, 7313, scn.LS, vb5WeHaveAPlan());
        scn.SkipToDSTurn(Phase.CONTROL);
        assertTrue(scn.IsAttachedTo(palpatine, prisoner));
    }

    @Test
    public void InsidiousPrisonerFollowsFirstCharacterMovingFromHereByLandspeed() {
        var scn = GetScenario();
        var quarters = scn.GetDSCard("quarters");
        var platform = scn.GetDSCard("platform");
        var prisoner = scn.GetDSCard("prisoner");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(quarters);
        scn.MoveLocationToTable(platform);
        scn.AttachCardsTo(quarters, prisoner);
        scn.MoveCardsToLocation(quarters, luke);
        assertTrue(scn.IsAdjacentTo(quarters, platform));
        scn.SkipToLSTurn(Phase.MOVE);
        scn.LSMoveCard(luke, platform);
        LegacyVb73DarkTestSupport.passUntilAction(scn, scn.LS, "Follow character");
        scn.LSChooseAction("Follow character");
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(platform, prisoner));
    }
}
