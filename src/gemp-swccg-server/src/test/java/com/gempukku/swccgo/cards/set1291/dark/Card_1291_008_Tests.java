package com.gempukku.swccgo.cards.set1291.dark;

import com.gempukku.swccgo.common.CardSubtype;
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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1291_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                    put("rebeltrooper", "1_28");
                }},
                new HashMap<>() {{
                    put("pillars", "1291_8");
                    put("petranaki", "227_10"); // Geonosis: Petranaki Arena
                    put("trooper", "1_194"); // power 1, not [Separatist]
                    put("droideka", "200_80"); // [Separatist] droid with power
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
    public void ArenaPillarsStatsAndIconsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("pillars").getBlueprint();
        assertEquals("Arena Pillars", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_9);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_9_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ArenaPillarsDeploysOnPetranakiArena() {
        var scn = GetScenario();
        var pillars = scn.GetDSCard("pillars");
        var petranaki = scn.GetDSCard("petranaki");
        scn.StartGame();
        scn.MoveLocationToTable(petranaki);
        scn.MoveCardsToHand(pillars);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSDeployAvailable(pillars));
        scn.DSDeployCard(pillars);
        if (scn.DSHasCardChoiceAvailable(petranaki)) {
            scn.DSChooseCard(petranaki);
        }
        scn.PassAllResponses();
        assertTrue(scn.IsAttachedTo(petranaki, pillars));
    }

    private void setUpCaptiveAtArena(VirtualTableScenario scn) {
        var pillars = scn.GetDSCard("pillars");
        var petranaki = scn.GetDSCard("petranaki");
        var luke = scn.GetLSCard("luke");
        scn.StartGame();
        scn.MoveLocationToTable(petranaki);
        scn.AttachCardsTo(petranaki, pillars);
        scn.gameState().imprisonCharacter(scn.game(), luke, petranaki);
    }

    @Test
    public void ArenaPillarsExecutesCaptiveAndOpponentLoses2ForceIfYourTotalHigher() {
        var scn = GetScenario();
        var pillars = scn.GetDSCard("pillars");
        var petranaki = scn.GetDSCard("petranaki");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var droideka = scn.GetDSCard("droideka");
        var rebeltrooper = scn.GetLSCard("rebeltrooper");
        setUpCaptiveAtArena(scn);
        // Dark: destiny 1 + 1 (Stormtrooper) + 2 ([Separatist] Droideka) = 4
        // Light: destiny 2 + 1 (Rebel Trooper) = 3
        scn.MoveCardsToLocation(petranaki, trooper, droideka, rebeltrooper);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(2);
        scn.PrepareDSDestiny(1);
        int lifeForce = scn.GetLSLifeForceRemaining();
        assertTrue(scn.DSCardActionAvailable(pillars, "execute"));
        scn.DSUseCardAction(pillars, "execute");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        if (scn.AwaitingLSForceLossPayment()) {
            scn.LSPayRemainingForceLossFromReserveDeck();
            scn.PassAllResponses();
        }
        assertInZone(Zone.LOST_PILE, luke);
        assertEquals(lifeForce - 2, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void ArenaPillarsDoesNotExecuteIfYourTotalIsNotHigher() {
        var scn = GetScenario();
        var pillars = scn.GetDSCard("pillars");
        var petranaki = scn.GetDSCard("petranaki");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var rebeltrooper = scn.GetLSCard("rebeltrooper");
        setUpCaptiveAtArena(scn);
        // Dark: destiny 2 + 1 (Stormtrooper) = 3; Light: destiny 2 + 1 (Rebel Trooper) = 3
        scn.MoveCardsToLocation(petranaki, trooper, rebeltrooper);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.PrepareLSDestiny(2);
        scn.PrepareDSDestiny(2);
        int lifeForce = scn.GetLSLifeForceRemaining();
        scn.DSUseCardAction(pillars, "execute");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();
        assertTrue(luke.isCaptive());
        assertEquals(lifeForce, scn.GetLSLifeForceRemaining());
    }

    @Test
    public void ArenaPillarsMayOnlyBeUsedDuringYourControlPhase() {
        var scn = GetScenario();
        var pillars = scn.GetDSCard("pillars");
        setUpCaptiveAtArena(scn);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertFalse(scn.DSCardActionAvailable(pillars, "execute"));
    }
}
