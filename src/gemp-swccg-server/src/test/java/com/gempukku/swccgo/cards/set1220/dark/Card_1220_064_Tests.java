package com.gempukku.swccgo.cards.set1220.dark;

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

import static com.gempukku.swccgo.framework.Assertions.assertInHand;
import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Card_1220_064_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("luke", "1_19");
                }},
                new HashMap<>() {{
                    put("anger", "1220_64");
                    put("powers", "1_280");
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
    public void ReleaseYourAngerStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetDSCard("anger").getBlueprint();

        assertEquals("Release Your Anger", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.USED_OR_LOST, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.INTERRUPT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.INTERRUPT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ReleaseYourAngerTakesYourPowersAreWeakOldManIntoHand() {
        var scn = GetScenario();
        var anger = scn.GetDSCard("anger");
        var powers = scn.GetDSCard("powers");

        scn.StartGame();
        scn.MoveCardsToDSHand(anger);
        scn.SkipToDSTurn(Phase.CONTROL);
        scn.MoveCardsToTopOfDSReserveDeck(powers);

        assertTrue(scn.DSCardPlayAvailable(anger, "Take Your Powers Are Weak, Old Man into hand"));
        scn.DSPlayCard(anger, "Take Your Powers Are Weak, Old Man into hand");
        scn.PassAllResponses();
        scn.PassCardAndForceUseResponses();
        if (scn.DSHasCardChoiceAvailable(powers)) {
            scn.DSChooseCard(powers);
        }
        scn.PassAllResponses();
        assertInHand(powers);
        assertInZone(Zone.USED_PILE, anger);
    }

    @org.junit.Test
    public void ReleaseYourAngerHasTheLatestText() {
        var scn = new com.gempukku.swccgo.framework.VirtualTableScenario(
                new java.util.HashMap<String, String>() {{  }},
                new java.util.HashMap<String, String>() {{ put("latest", "1220_64"); }},
                10, 10,
                com.gempukku.swccgo.framework.StartingSetup.DefaultLSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.DefaultDSGroundLocation,
                com.gempukku.swccgo.framework.StartingSetup.NoLSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoDSStartingInterrupts,
                com.gempukku.swccgo.framework.StartingSetup.NoLSShields,
                com.gempukku.swccgo.framework.StartingSetup.NoDSShields,
                com.gempukku.swccgo.framework.VirtualTableScenario.Open);
        var card = scn.GetDSCard("latest").getBlueprint();
        org.junit.Assert.assertEquals("Destiny +2 when drawn for lightsaber combat or weapon destiny. USED: Take Your Powers Are Weak, Old Man into hand from Reserve Deck; reshuffle. LOST: If you just lost a character armed with a non-[Permanent Weapon] lightsaber, lose 1 Force to take that character (or lightsaber) into hand.",
                card.getGameText());
        org.junit.Assert.assertNull(card.getLegacyVersion());
        org.junit.Assert.assertFalse(card.getGameText().contains("weapon or lightsaber"));
    }
}
