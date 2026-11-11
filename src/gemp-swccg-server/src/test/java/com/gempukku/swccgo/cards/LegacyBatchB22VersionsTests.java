package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Legacy batch B22 (Virtual Block 8 and Virtual Shields, 10 families; ai-team-skills shared/gemp-legacy-cards/versions/batches.json):
 * the current card carries the latest version's text and every older version is its own 22xx card. Fanfare (V), There Is
 * Another (V) and Death Star Sentry (V) have one published text (the plan's Virtual Shields "errata" were PDF misreads), so
 * their current cards play the printed sheet text: Fanfare keeps its Lost In The Wilderness sentence, There Is Another
 * affects only opponent's [Death Star II] cards. Images are pinned in cardImageUrlTests.js.
 */
public class LegacyBatchB22VersionsTests {
    private static final Map<String, String> CURRENT_TEXT = Map.ofEntries(
            Map.entry("1282_14", "Adds 3 to power of anything he pilots. May reveal from hand to ▲ Lady Luck and deploy both simultaneously. While piloting (or with a female), adds one battle destiny. During battle, may draw destiny. If destiny < twice the number of scoundrels here, reset a participating card's forfeit to 0."),
            Map.entry("1282_15", "Adds 1 to power of anything he pilots. While aboard your starship, its total weapon destiny is +1. Whenever you hit a starship here, opponent loses 1 Force. During battle, may lose 1 Force to cancel [Coruscant] Watch Your Back!."),
            Map.entry("1282_19", "May be stacked on I Know That Laugh from hand, using 2 Force. While stacked on I Know That Laugh, stack face-up and during any battle may draw destiny. If destiny < number of your aliens in battle, reset a participating character's forfeit to 0."),
            Map.entry("1282_26", "When you play this Interrupt, may place a Rep on your Objective in Used Pile. Lose X cards stacked on I Know That Laugh to reduce a weapon or battle destiny total by X. OR Use 1 Force to take a card stacked on I Know That Laugh into hand (or stack top card of Lost Pile on it)."),
            Map.entry("1283_3", "Your total battle destiny here is +1. Unless Jabba on table, 'Jabba' in the game text of your cards (except Hutt Bounty and Jabba's Prize) may be treated as 'Lady Valarian.' Once per turn, may target a Mistryl of ability > 2 here; target is Heroic until end of turn."),
            Map.entry("1283_12", "Dark: Your docking bay transit from here requires 2 Force. Your starfighters deploy -1 (or -2 if freighter) here. Immune to [Reflections III] Ounee Ta. Light: May not be deployed to Bespin, Dagobah, Endor, Hoth or Yavin 4. Your docking bay transit from here requires 3 Force. Your pilots deploy +1 here."),
            Map.entry("1200_1", "Plays on table. If opponent moves from a location you occupy during your turn, they lose 2 Force. At end of opponent's turn, if you control two battlegrounds (a site and system) and opponent deployed a card with ability and did not initiate a battle, may retrieve 1 Force."),
            Map.entry("1200_5", "Plays on table. Cancels Order To Engage and Scramble. Once per game, may [upload] an Immediate Effect. While opponent occupies no battleground systems, Staging Areas is suspended. 'Missing' on Lost In The Wilderness is treated as 'landspeed = 0 for remainder of turn.'"),
            Map.entry("1272_9", "Plays on table. Cancels Order To Engage and Scramble. Once per game, may [upload] an Immediate Effect. While opponent occupies no battleground systems, Staging Areas is suspended. 'Missing' on Lost In The Wilderness is treated as 'landspeed = 0 for remainder of turn.'"),
            Map.entry("1200_11", "Unless you have deployed a Skywalker this game, plays on table. 'Luke' and 'Son Of Vader' on opponent's [Death Star II] cards are treated as 'Leia' and 'Lady Vader,' respectively. Once during your turn, opponent may activate 1 Force."),
            Map.entry("1200_17", "Plays on table. Colo Claw Fish is canceled. Opponent must first use X Force to deploy a non-unique card (except a Jawa) to a location, where X = the number of copies of that card at that location."));
    /** version id -> {current printing id, label, game text, printed on} */
    private static final Map<String, Object[]> VERSIONS = Map.ofEntries(
            Map.entry("2282_14", new Object[]{"1282_14", "", "Adds 3 to power of anything he pilots. May reveal from hand to ▲ Luxury Yacht and deploy both simultaneously. While piloting (or with a female), adds one battle destiny. During battle, may draw destiny. If destiny < twice the number of scoundrels here, reset a participating card's forfeit to 0.", LocalDate.of(2012, 9, 14)}),
            Map.entry("2282_1014", new Object[]{"1282_14", "PC-E1", "Adds 3 to anything he pilots. May reveal from hand to take Luxury Yacht into hand from Reserve Deck; reshuffle; and deploy both simultaneously. While piloting (or with a female), adds one battle destiny. During battle, may target a card here. Draw destiny. If destiny < twice the number of scoundrels here, target is forfeit = 0.", LocalDate.of(2012, 12, 12)}),
            Map.entry("2282_15", new Object[]{"1282_15", "", "Adds 1 to power of anything he pilots. While aboard your starship, its total weapon destiny is +1. Whenever you hit a starship here, opponent loses 1 Force. During battle, may lose 1 Force to cancel [Coruscant] Watch Your Back!.", LocalDate.of(2012, 9, 14)}),
            Map.entry("2282_19", new Object[]{"1282_19", "", "May be stacked on I Know That Laugh from hand, using 2 Force. While stacked on I Know That Laugh, stack face-up and during any battle may draw destiny. If destiny < number of your aliens in battle, reset a participating character's forfeit to 0.", LocalDate.of(2012, 9, 14)}),
            Map.entry("2282_26", new Object[]{"1282_26", "", "When you play this Interrupt, may place a Rep on your Objective in Used Pile. Lose X cards stacked on I Know That Laugh to reduce a weapon or battle destiny total by X. OR Use 1 Force to take a card stacked on I Know That Laugh into hand (or stack top card of Lost Pile on it).", LocalDate.of(2012, 9, 14)}),
            Map.entry("2283_3", new Object[]{"1283_3", "", "Your total battle destiny here is +1. Unless Jabba on table, 'Jabba' in the game text of your cards (except Hutt Bounty) may be treated as 'Lady Valarian.' Once per turn, may target a Mistryl of ability > 2 here; target gains Heroic until end of turn.", LocalDate.of(2013, 3, 28)}),
            Map.entry("2283_12", new Object[]{"1283_12", "", "Dark: Your docking bay transit from here requires 2 Force. Your starfighters deploy -1 (or -2 if freighter) here. Immune to [Reflections III] Ounee Ta. Light: May not be deployed to Bespin, Dagobah, Endor, Hoth or Yavin 4. Your docking bay transit from here requires 3 Force.", LocalDate.of(2013, 3, 28)}),
            Map.entry("2200_1", new Object[]{"1200_1", "", "Plays on table. If opponent moves from a location you occupy during your turn, they lose 2 Force. At end of opponent's turn, if you control two battlegrounds (a site and system) and opponent deployed a card with ability and did not initiate a battle, you may retrieve 1 Force.", LocalDate.of(2012, 7, 21)}));


    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    @Test
    public void CurrentCardsCarryTheLatestText() {
        assertEquals(11, CURRENT_TEXT.size());
        CURRENT_TEXT.forEach((id, text) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertNull(id, card.getLegacyVersion());
            assertEquals(id, text, card.getGameText());
        });
    }

    @Test
    public void OlderVersionsAreTheirOwnCards() {
        assertEquals(8, VERSIONS.size());
        VERSIONS.forEach((id, v) -> {
            SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
            assertNotNull(id, card);
            assertEquals(id, id, library.getBaseBlueprintId(id));
            LegacyVersion version = card.getLegacyVersion();
            assertNotNull(id, version);
            assertEquals(id, v[0], version.currentPrintingId());
            assertEquals(id, v[1], version.label());
            assertEquals(id, v[2], card.getGameText());
            assertEquals(id, v[3], version.printedOn());
        });
    }

    @Test
    public void FanfarePrintingsShareOneCard() {
        // B35 (Bill 2026-11-09): 1200_5 (Reflections III) plays 601_35; 1272_9 (Tatooine) is its own card extending it.
        assertEquals("601_35", library.getBaseBlueprintId("1200_5"));
        assertEquals("1272_9", library.getBaseBlueprintId("1272_9"));
    }

    @Test
    public void LandoCurrentLoreIsAsPrinted() {
        assertEquals("Ever since Lando's 'little' maneuver at the Battle of Taanab, his piloting skills had become legendary. Scoundrel and gambler.",
                library.getSwccgoCardBlueprint("1282_14").getLore());
    }

    @Test
    public void RennekHasNoWarriorIcon() {
        assertFalse(library.getSwccgoCardBlueprint("1282_19").hasIcon(com.gempukku.swccgo.common.Icon.WARRIOR));
    }
}
