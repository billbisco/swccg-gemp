package com.gempukku.swccgo.cards.set2240.light;

import com.gempukku.swccgo.cards.LegacyVersionAsserts;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.DeckInvalidException;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.formats.DefaultSwccgFormat;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** Original Anger, Fear, Aggression (V) (Dec 2009 sheet, up to 12 cards), a version of 1240_78. */
public class Card_2240_078_Tests {
    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    private VirtualTableScenario GetScenario(String effectId, int shieldCount) {
        var mine = new HashMap<String, String>();
        mine.put("effect", effectId);
        var theirs = new HashMap<String, String>();
        theirs.put("filler", "1_194");
        var shields = new StartingSetup() {
            @Override public HashMap<String, String> Cards() {
                var cards = new HashMap<String, String>();
                for (int i = 0; i < shieldCount; i++)
                    cards.put("s" + i, SHIELDS[i]);
                return cards;
            }
            @Override public void Setup(VirtualTableScenario scn) { }
        };
        return new VirtualTableScenario(mine, theirs, 20, 20,
                StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                shields, StartingSetup.NoDSShields, VirtualTableScenario.Open);
    }

    private static SwccgDeck deck(String effectId, int shields) {
        SwccgDeck deck = new SwccgDeck("ood");
        deck.addCard(effectId);
        for (int i = 0; i < shields; i++)
            deck.addCardOutsideDeck(SHIELDS[i]);
        return deck;
    }

    private static final String[] SHIELDS = {"13_1", "13_3", "13_4", "13_6", "13_8", "13_15", "13_16", "13_22", "13_30", "13_35", "13_37", "13_38", "13_44"};

    @Test
    public void AngerFearAggressionIsTheSameCardWithTheOldText() {
        LegacyVersionAsserts.assertVersionOf(library.getSwccgoCardBlueprint("1240_78"), library.getSwccgoCardBlueprint("2240_78"),
                "Deploy on table with up to 12 cards from outside your deck face-down under here. Four times per game, may play a Defensive Shield from here (as if from hand). Unless canceling your Interrupt, opponent may not play Uncertain Is The Future until the end of your first turn.",
                "1240_78", "", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27));
    }

    private static int stackedUnder(String effectId, int shieldCount) {
        var scn = new Card_2240_078_Tests().GetScenario(effectId, shieldCount);
        scn.StartGame();
        var effect = scn.GetLSCard("effect");
        assertInZone(Zone.SIDE_OF_TABLE, effect);
        return scn.gameState().getStackedCards(effect).size();
    }

    @Test
    public void OriginalAngerFearAggressionDeploysWithUpToTwelveCardsFromOutsideTheDeck() {
        //test1: 12 Defensive Shields outside the deck all go under the original
        //test2: with 13 (more than the original allows; the deck is invalid) none are stacked
        //test3: the current card takes all 13
        assertEquals(12, stackedUnder("2240_78", 12)); //test1
        assertEquals(0, stackedUnder("2240_78", 13)); //test2
        assertEquals(13, stackedUnder("1240_78", 13)); //test3
    }

    @Test
    public void OriginalAngerFearAggressionDeckMayHaveAtMostTwelveOutsideDeckShields() throws DeckInvalidException {
        //test1: 12 is legal, 13 is not; the current card allows 15
        DefaultSwccgFormat.validateOriginalStartingEffectOutsideDeckShieldCap(library, deck("2240_78", 12)); //test1
        try {
            DefaultSwccgFormat.validateOriginalStartingEffectOutsideDeckShieldCap(library, deck("2240_78", 13));
            fail("13 shields with the original AngerFearAggression");
        } catch (DeckInvalidException expected) {
            assertTrue(expected.getMessage(), expected.getMessage().contains("More defensive shields in outside of deck area"));
        }
        DefaultSwccgFormat.validateOriginalStartingEffectOutsideDeckShieldCap(library, deck("1240_78", 13)); //test1
    }
}
