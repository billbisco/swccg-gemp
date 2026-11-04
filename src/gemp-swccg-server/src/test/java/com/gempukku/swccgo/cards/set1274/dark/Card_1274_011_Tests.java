package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1274_011_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("sabers", "1274_11");
                    put("grievous", "203_27");  // General Grievous (Grievous persona; Cyborg Commander, Hunter Of Jedi is not in GEMP yet)
                    put("vader", "1_168");
                }},
                30, 30,
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
    public void CyborgCommandersLightsabersStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("sabers").getBlueprint();

        assertEquals("Cyborg Commander's Lightsabers", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.WEAPON); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.LIGHTSABER); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EPISODE_I);
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void CyborgCommandersLightsabersDeployOnlyOnCyborgCommander() {
        var scn = GetScenario();
        var sabers = scn.GetDSCard("sabers");
        var grievous = scn.GetDSCard("grievous");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, grievous, vader);
        scn.MoveCardsToDSHand(sabers);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(sabers));
        scn.DSPlayCard(sabers);
        if (scn.DSDecisionAvailable("Choose")) {
            assertFalse(scn.DSHasCardChoiceAvailable(vader));
            scn.DSChooseCard(grievous);
        }
        scn.PassAllResponses();
        assertEquals(grievous, sabers.getAttachedTo());
    }

    @Test
    public void CyborgCommandersLightsabersMayAddOneToForceDrainWherePresent() {
        var scn = GetScenario();
        var sabers = scn.GetDSCard("sabers");
        var grievous = scn.GetDSCard("grievous");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, grievous);
        scn.AttachCardsTo(grievous, sabers);
        scn.EnsureLSForcePile(5);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSForceDrainAt(site);
        scn.LSPass();
        assertTrue(scn.DSCardActionAvailable(sabers, "Add 1 to Force drain"));
        scn.DSUseCardAction(sabers, "Add 1 to Force drain");
        scn.PassAllResponses();
        scn.PassForceDrainStartResponses();
        assertTrue(scn.IsActiveForceDrain());
        assertEquals(scn.GetIconCount(site, Icon.LIGHT_FORCE) + 1, scn.GetForceDrainTotal());
    }

    @Test
    public void CyborgCommandersLightsabersHitTargetMayNotSatisfyAttritionOrBeRemovedFromLostPileThisTurn() {
        var scn = GetScenario();
        var sabers = scn.GetDSCard("sabers");
        var grievous = scn.GetDSCard("grievous");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, grievous, luke);
        scn.AttachCardsTo(grievous, sabers);
        scn.EnsureDSForcePile(5);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(6);
        scn.PrepareDSDestiny(6);
        assertTrue(scn.DSCardActionAvailable(sabers, "Fire"));
        scn.DSUseCardAction(sabers, "Fire");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();

        assertTrue(luke.isHit());
        assertTrue(scn.game().getModifiersQuerying().mayNotRemoveJustLostCardFromLostPile(scn.gameState(), luke));
    }
}
