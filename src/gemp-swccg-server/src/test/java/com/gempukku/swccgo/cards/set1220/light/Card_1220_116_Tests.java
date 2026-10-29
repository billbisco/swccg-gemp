package com.gempukku.swccgo.cards.set1220.light;

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
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_116_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("disorder", "1220_116");
                    put("barrier", "1_105");
                    put("palpatine", "12_28");
                    put("luke", "1_19");
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
    public void CivilDisorderStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("disorder").getBlueprint();

        assertEquals("Civil Disorder", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CivilDisorderPreventsPlayingRebelBarrier() {
        var scn = GetScenario();
        var disorder = scn.GetLSCard("disorder");
        var barrier = scn.GetLSCard("barrier");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(disorder);
        scn.MoveCardsToLSHand(barrier);
        scn.MoveCardsToDSHand(vader);
        scn.EnsureDSForcePile(6);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(vader));
        scn.DSPlayCard(vader);
        scn.DSChooseCard(site);
        scn.PassAllResponses();
        assertFalse(scn.LSPlayUsedInterruptAvailable(barrier));
    }

    @Test
    public void CivilDisorderMakesSenatorsDeployPlusTwo() {
        var scn = GetScenario();
        var disorder = scn.GetLSCard("disorder");
        var palpatine = scn.GetLSCard("palpatine");

        scn.StartGame();
        float printed = palpatine.getBlueprint().getDeployCost();
        scn.MoveCardsToLSSideOfTable(disorder);
        assertEquals(printed + 2, scn.GetDeployCost(palpatine), scn.epsilon);
    }

    @Test
    public void CivilDisorderRetrievesTopmostCharacterWhenYouInitiateBattle() {
        var scn = GetScenario();
        var disorder = scn.GetLSCard("disorder");
        var luke = scn.GetLSCard("luke");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(disorder);
        scn.MoveCardsToTopOfLSLostPile(luke);
        scn.MoveCardsToLocation(site, scn.GetLSFiller(1), vader);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        if (scn.LSDecisionAvailable("Retrieve topmost character")) {
            scn.LSChooseYes();
            scn.PassAllResponses();
            assertInZone(Zone.USED_PILE, luke);
        }
    }

    private float deployCostTo(VirtualTableScenario scn, PhysicalCardImpl card, PhysicalCardImpl target) {
        return scn.game().getModifiersQuerying().getDeployCost(
                scn.gameState(), card, card, target, false, null, false, 0, null, false);
    }
}
