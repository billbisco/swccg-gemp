package com.gempukku.swccgo.cards.set1103.dark;

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

public class Card_1103_041_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("han", "1_11");
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("oota", "1103_41");
                    put("greedo", "1210_11");
                    put("boba", "1210_2");
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
    public void OotaGootaSoloStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("oota").getBlueprint();

        assertEquals("Oo-ta Goo-ta, Solo?", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.A_NEW_HOPE);
            add(Icon.INTERRUPT);
        }});
        assertEquals(ExpansionSet.VIRTUAL_ORIGINAL_SET_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void OotaGootaSoloUsedLimitsOpponentToOneBattleDestiny() {
        var scn = GetScenario();
        var oota = scn.GetDSCard("oota");
        var greedo = scn.GetDSCard("greedo");
        var han = scn.GetLSCard("han");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, greedo, han, luke);
        scn.MoveCardsToDSHand(oota);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertTrue(scn.DSPlayUsedInterruptAvailable(oota));
        scn.DSPlayUsedInterrupt(oota);
        scn.PassAllResponses();

        assertEquals(1, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(), scn.LS, true, false));
        assertInZone(Zone.USED_PILE, oota);
    }

    @Test
    public void OotaGootaSoloLostAddsOneBattleDestiny() {
        var scn = GetScenario();
        var oota = scn.GetDSCard("oota");
        var greedo = scn.GetDSCard("greedo");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, greedo, luke);
        scn.MoveCardsToDSHand(oota);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertTrue(scn.DSPlayLostInterruptAvailable(oota));
        scn.DSPlayLostInterrupt(oota);
        scn.PassAllResponses();

        assertEquals(1, scn.game().getModifiersQuerying().getNumBattleDestinyDraws(scn.gameState(), scn.DS, false, true));
        assertInZone(Zone.LOST_PILE, oota);
    }

    @Test
    public void OotaGootaSoloUsedIsNotOfferedIfOnlyBobaFett() {
        var scn = GetScenario();
        var oota = scn.GetDSCard("oota");
        var boba = scn.GetDSCard("boba");
        var han = scn.GetLSCard("han");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boba, han, luke);
        scn.MoveCardsToDSHand(oota);
        scn.SkipToDSTurn(Phase.BATTLE);
        scn.DSInitiateBattle(site);
        scn.PassAllResponses();

        assertFalse(scn.DSPlayUsedInterruptAvailable(oota));
        assertTrue(scn.DSPlayLostInterruptAvailable(oota));
    }
}
