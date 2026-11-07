package com.gempukku.swccgo.cards.set1240.light;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_087_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("insurrection", "1240_87");
                    put("nod", "4_28");
                    put("talon", "1240_75");  // ability 3
                    put("boshek", "1_4");     // ability 4
                    put("yavin", "1_135");    // battleground system
                }},
                new HashMap<>() {{
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

    @Test
    public void InsurrectionVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("insurrection").getBlueprint();

        assertEquals("Insurrection", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ENDOR);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
    }

    @Test
    public void InsurrectionVDeploysNoDisintegrationsFromReserveDeckOncePerTurn() {
        var scn = GetScenario();
        var insurrection = scn.GetLSCard("insurrection");
        var nod = scn.GetLSCard("nod");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(insurrection);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.LS, 8);
        scn.MoveCardsToTopOfLSReserveDeck(nod);

        assertTrue(scn.LSCardActionAvailable(insurrection, "Deploy No Disintegrations!"));
        scn.LSUseCardAction(insurrection, "Deploy No Disintegrations!");
        scn.PassAllResponses();
        if (scn.LSHasCardChoiceAvailable(nod)) {
            scn.LSChooseCard(nod);
        }
        scn.PassAllResponses();
        assertEquals(Zone.SIDE_OF_TABLE, nod.getZone());

        scn.DSPass();
        assertFalse(scn.LSCardActionAvailable(insurrection, "Deploy No Disintegrations!"));
    }

    @Test
    public void InsurrectionVAbility3CharacterUsesDefenseValueOfOtherCharacterPresentWhenTargetedByWeapons() {
        var scn = GetScenario();
        var insurrection = scn.GetLSCard("insurrection");
        var talon = scn.GetLSCard("talon");
        var boshek = scn.GetLSCard("boshek");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, talon, boshek, trooper);
        scn.MoveCardsToLSSideOfTable(insurrection);
        var mq = scn.game().getModifiersQuerying();
        assertEquals(3, mq.getDefenseValue(scn.gameState(), talon), scn.epsilon);

        // Targeted by a weapon: uses BoShek's defense value (ability 4).
        scn.gameState().beginWeaponFiring(trooper, null);
        scn.gameState().getWeaponFiringState().setTarget(talon);
        assertEquals(4, mq.getDefenseValue(scn.gameState(), talon), scn.epsilon);
        // BoShek (ability 4) is not affected.
        assertEquals(4, mq.getDefenseValue(scn.gameState(), boshek), scn.epsilon);
        scn.gameState().finishWeaponFiring();

        // Not targeted: back to its own defense value.
        assertEquals(3, mq.getDefenseValue(scn.gameState(), talon), scn.epsilon);
    }

    @Test
    public void InsurrectionVNoDefenseValueChangeWithoutOtherCharacterPresent() {
        var scn = GetScenario();
        var insurrection = scn.GetLSCard("insurrection");
        var talon = scn.GetLSCard("talon");
        var boshek = scn.GetLSCard("boshek");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();
        var other = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, talon, trooper);
        scn.MoveCardsToLocation(other, boshek);
        scn.MoveCardsToLSSideOfTable(insurrection);
        scn.gameState().beginWeaponFiring(trooper, null);
        scn.gameState().getWeaponFiringState().setTarget(talon);
        assertEquals(3, scn.game().getModifiersQuerying().getDefenseValue(scn.gameState(), talon), scn.epsilon);
        scn.gameState().finishWeaponFiring();
    }

    @Test
    public void InsurrectionVTreatsBattlegroundSystemOnBlock4ObjectiveAsUniqueBattlegroundSite() {
        var scn = GetScenario();
        var insurrection = scn.GetLSCard("insurrection");
        var yavin = scn.GetLSCard("yavin");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        // The filter is used by Hidden Base (V) / Systems Will Slip Through Your Fingers (V) for "battleground system".
        var objectiveFilter = Card1240_087.objectiveBattlegroundSystem(site);
        assertTrue(objectiveFilter.accepts(scn.game(), yavin));

        scn.MoveCardsToLSSideOfTable(insurrection);
        assertFalse(objectiveFilter.accepts(scn.game(), yavin));
        boolean siteIsUniqueBattleground = com.gempukku.swccgo.filters.Filters.and(
                com.gempukku.swccgo.filters.Filters.unique, com.gempukku.swccgo.filters.Filters.battleground_site).accepts(scn.game(), site);
        assertEquals(siteIsUniqueBattleground, objectiveFilter.accepts(scn.game(), site));
    }
}
