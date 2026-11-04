package com.gempukku.swccgo.cards.set1274.dark;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
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

public class Card_1274_012_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("saber", "1274_12");
                    put("sidious", "14_79");   // Darth Sidious
                    put("vader", "1_168");
                    put("corridor", "1_286");  // Death Star: Level 4 Military Corridor (no Light Side icon: not a battleground)
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
    public void SidiousLightsaberStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("saber").getBlueprint();

        assertEquals("Sidious' Lightsaber", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{ add(CardType.WEAPON); }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{ add(Keyword.LIGHTSABER); }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{ add(Persona.SIDIOUS_LIGHTSABER); }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.DEATH_STAR_II);
            add(Icon.EPISODE_I);
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SidiousLightsaberDeploysOnlyOnSidious() {
        var scn = GetScenario();
        var saber = scn.GetDSCard("saber");
        var sidious = scn.GetDSCard("sidious");
        var vader = scn.GetDSCard("vader");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sidious, vader);
        scn.MoveCardsToDSHand(saber);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertTrue(scn.DSCardPlayAvailable(saber));
        scn.DSPlayCard(saber);
        if (scn.DSDecisionAvailable("Choose")) {
            assertFalse(scn.DSHasCardChoiceAvailable(vader));
            scn.DSChooseCard(sidious);
        }
        scn.PassAllResponses();
        assertEquals(sidious, saber.getAttachedTo());
    }

    @Test
    public void SidiousLightsaberMayDeployAsAReact() {
        var scn = GetScenario();
        var saber = scn.GetDSCard("saber");
        var sidious = scn.GetDSCard("sidious");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sidious, luke);
        scn.MoveCardsToDSHand(saber);
        scn.EnsureDSForcePile(5);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        assertTrue(scn.DSDecisionAvailable("Battle just initiated"));
        assertTrue(scn.DSCardActionAvailable(saber));
        scn.DSUseCardAction(saber);
        if (scn.DSDecisionAvailable("Choose")) {
            scn.DSChooseCard(sidious);
        }
        scn.PassAllResponses();
        assertEquals(sidious, saber.getAttachedTo());
    }

    @Test
    public void SidiousLightsaberAddsOneLightSideIconWhileAtBattlegroundSite() {
        var scn = GetScenario();
        var saber = scn.GetDSCard("saber");
        var sidious = scn.GetDSCard("sidious");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sidious);
        int before = scn.GetIconCount(site, Icon.LIGHT_FORCE);
        scn.AttachCardsTo(sidious, saber);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(before + 1, scn.GetIconCount(site, Icon.LIGHT_FORCE));
    }

    @Test
    public void SidiousLightsaberAddsNoIconAtNonBattlegroundSite() {
        var scn = GetScenario();
        var saber = scn.GetDSCard("saber");
        var sidious = scn.GetDSCard("sidious");
        var corridor = scn.GetDSCard("corridor");

        scn.StartGame();
        scn.MoveLocationToTable(corridor);
        scn.MoveCardsToLocation(corridor, sidious);
        int before = scn.GetIconCount(corridor, Icon.LIGHT_FORCE);
        scn.AttachCardsTo(sidious, saber);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertEquals(before, scn.GetIconCount(corridor, Icon.LIGHT_FORCE));
    }

    @Test
    public void SidiousLightsaberHitTargetMayNotSatisfyAttritionOrBeRemovedFromLostPileThisTurn() {
        var scn = GetScenario();
        var saber = scn.GetDSCard("saber");
        var sidious = scn.GetDSCard("sidious");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, sidious, luke);
        scn.AttachCardsTo(sidious, saber);
        scn.EnsureDSForcePile(5);
        scn.DSStartBattleAndSkipToWeaponsSegment(site);
        scn.PrepareDSDestiny(6);
        scn.PrepareDSDestiny(6);
        assertTrue(scn.DSCardActionAvailable(saber, "Fire"));
        scn.DSUseCardAction(saber, "Fire");
        scn.DSChooseCard(luke);
        scn.PassAllResponses();

        assertTrue(luke.isHit());
        assertTrue(scn.game().getModifiersQuerying().mayNotRemoveJustLostCardFromLostPile(scn.gameState(), luke));
    }
}
