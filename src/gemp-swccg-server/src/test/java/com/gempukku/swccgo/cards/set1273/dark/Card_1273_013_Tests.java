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

    @Test
    public void InsidiousPrisonerOpponentMayDeployJediAndLightsaberHereWhenDeployed() {
        var scn = GetScenario();
        var prisoner = scn.GetDSCard("prisoner");
        var quarters = scn.GetDSCard("quarters");
        var obiwan = scn.GetLSCard("obiwan");
        scn.StartGame();
        scn.MoveLocationToTable(quarters);
        MissionTestSupport.putOnTable(scn, 7314, scn.DS, stunningMove());
        scn.MoveCardsToDSHand(prisoner);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.LSActivateForceCheat(6);
        scn.MoveCardsToTopOfLSReserveDeck(obiwan);
        scn.DSDeployCard(prisoner);
        LegacyVb73DarkTestSupport.passUntilAction(scn, scn.LS, "Deploy a Jedi here");
        scn.LSChooseAction("Deploy a Jedi here");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> obiwan.getZone() == Zone.AT_LOCATION);
        assertEquals(quarters, obiwan.getAtLocation());

        var saber = scn.GetLSCard("saber");
        scn.MoveCardsToTopOfLSReserveDeck(saber);
        LegacyVb73DarkTestSupport.passUntilAction(scn, scn.LS, "Deploy a lightsaber here");
        scn.LSChooseAction("Deploy a lightsaber here");
        LegacyVb73DarkTestSupport.resolveUntil(scn, () -> saber.getZone() == Zone.ATTACHED);
        assertTrue(scn.IsAttachedTo(obiwan, saber));
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
