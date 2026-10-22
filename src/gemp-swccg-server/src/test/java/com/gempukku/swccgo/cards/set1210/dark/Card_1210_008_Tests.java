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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_008_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("gunner", "1210_8");
                    put("detention", "1_284");
                    put("superlaser", "2_161");
                    put("cpi", "2_130");
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
    public void DeathStarGunnerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("gunner").getBlueprint();

        assertEquals("Death Star Gunner", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.RESTRICTED_3, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(2, card.getDestiny(), scn.epsilon);
        assertEquals(2, card.getDeployCost(), scn.epsilon);
        assertEquals(1, card.getPower(), scn.epsilon);
        assertEquals(1, card.getAbility(), scn.epsilon);
        assertEquals(2, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.IMPERIAL);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GUNNER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.IMPERIAL);
            add(Icon.A_NEW_HOPE);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DeathStarGunnerDoesNotCancelBonusesWhenNotOnDeathStar() {
        var scn = GetScenario();
        var gunner = scn.GetDSCard("gunner");
        var detention = scn.GetDSCard("detention");
        var luke = scn.GetLSCard("luke");
        var otherSite = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(detention);
        scn.MoveCardsToLocation(detention, luke);
        scn.MoveCardsToLocation(otherSite, gunner);
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSForceDrainAvailable(detention));
        scn.LSForceDrainAt(detention);
        scn.DSPass();
        scn.PassForceDrainStartResponses();
        assertEquals(2, scn.GetForceDrainTotal());
    }

    @Test
    public void DeathStarGunnerCancelsForceDrainBonusesFromDeathStarSites() {
        var scn = GetScenario();
        var gunner = scn.GetDSCard("gunner");
        var detention = scn.GetDSCard("detention");
        var luke = scn.GetLSCard("luke");

        scn.StartGame();
        scn.MoveLocationToTable(detention);
        scn.MoveCardsToLocation(detention, gunner, luke);
        scn.EnsureLSForcePile(5);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSForceDrainAvailable(detention));
    }

    @Test
    public void DeathStarGunnerAddsTwoToAnhEpicEventTotal() {
        var scn = GetScenario();
        var gunner = scn.GetDSCard("gunner");
        var detention = scn.GetDSCard("detention");
        var cpi = scn.GetDSCard("cpi");

        scn.StartGame();
        scn.MoveLocationToTable(detention);
        scn.MoveCardsToLocation(detention, gunner);
        scn.MoveCardsToDSSideOfTable(cpi);
        assertEquals(2, scn.game().getModifiersQuerying().getEpicEventCalculationTotal(scn.gameState(), cpi, 0), scn.epsilon);
    }

    @Test
    public void DeathStarGunnerPlacesSelfInUsedPileToTakeSuperlaser() {
        var scn = GetScenario();
        var gunner = scn.GetDSCard("gunner");
        var detention = scn.GetDSCard("detention");
        var superlaser = scn.GetDSCard("superlaser");

        scn.StartGame();
        scn.MoveLocationToTable(detention);
        scn.MoveCardsToLocation(detention, gunner);
        scn.SkipToDSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfDSReserveDeck(superlaser);
        assertTrue(scn.DSCardActionAvailable(gunner, "Take Superlaser into hand"));
        scn.DSUseCardAction(gunner, "Take Superlaser into hand");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        scn.DSChooseCard(superlaser);
        scn.PassAllResponses();
        assertTrue(gunner.getZone() == Zone.USED_PILE || gunner.getZone() == Zone.TOP_OF_USED_PILE);
        assertEquals(Zone.HAND, superlaser.getZone());
    }
}
