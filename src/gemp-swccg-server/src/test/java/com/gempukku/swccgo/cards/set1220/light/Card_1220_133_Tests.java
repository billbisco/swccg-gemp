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
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_133_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("infantry", "1220_133");
                    put("scout", "1220_99");
                    put("rifle", "1_153");
                }},
                new HashMap<>() {{
                    put("choke", "1_264");
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
    public void RebelInfantryStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("infantry").getBlueprint();

        assertEquals("Rebel Infantry", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.SPECIAL_EDITION);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertTrue(card.isImmuneToCardTitle(Title.Alter));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void RebelInfantryDeploysOnTable() {
        var scn = GetScenario();
        var infantry = scn.GetLSCard("infantry");

        scn.StartGame();
        scn.MoveCardsToLSHand(infantry);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(infantry));
        scn.LSDeployCard(infantry);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, infantry);
    }

    @Test
    public void RebelInfantryMakesRebelScoutsExceptPilotsTroopersWithArmorFour() {
        var scn = GetScenario();
        var infantry = scn.GetLSCard("infantry");
        var scout = scn.GetLSCard("scout");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, scout);
        assertFalse(scn.HasKeyword(scout, Keyword.TROOPER));
        scn.MoveCardsToLSSideOfTable(infantry);
        assertTrue(scn.HasKeyword(scout, Keyword.TROOPER));
        assertEquals(4, scn.GetArmor(scout));
    }

    @Test
    public void RebelInfantryDeploysBlasterRifleOnJustDeployedRebelTrooperFromReserveDeck() {
        var scn = GetScenario();
        var infantry = scn.GetLSCard("infantry");
        var scout = scn.GetLSCard("scout");
        var rifle = scn.GetLSCard("rifle");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(infantry);
        scn.MoveCardsToLSHand(scout);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(rifle);
        assertTrue(scn.LSCardPlayAvailable(scout) || scn.LSDeployAvailable(scout));
        if (scn.LSDeployAvailable(scout)) {
            scn.LSDeployCard(scout);
        }
        else {
            scn.LSPlayCard(scout);
        }
        if (scn.LSHasCardChoiceAvailable(site)) {
            scn.LSChooseCard(site);
        }
        int safety = 0;
        while (safety++ < 20 && scn.LSGetDecision() != null
                && !String.valueOf(scn.LSGetDecision().getText()).toLowerCase().contains("blaster")) {
            scn.LSPass();
        }
        if (scn.LSGetDecision() != null
                && String.valueOf(scn.LSGetDecision().getText()).toLowerCase().contains("blaster")) {
            scn.LSChooseAction("Blaster Rifle");
            safety = 0;
            while (safety++ < 16 && !scn.LSHasCardChoiceAvailable(rifle)) {
                if (scn.DSGetDecision() != null) {
                    scn.DSPass();
                }
                else if (scn.LSGetDecision() != null) {
                    scn.LSPass();
                }
                else {
                    break;
                }
            }
            if (scn.LSHasCardChoiceAvailable(rifle)) {
                scn.LSChooseCard(rifle);
            }
            scn.PassAllResponses();
        }
        else {
            scn.PassAllResponses();
        }
        assertTrue(rifle.getAttachedTo() == scout || rifle.getZone() == Zone.RESERVE_DECK
                || rifle.getZone() == Zone.TOP_OF_RESERVE_DECK);
    }
}
