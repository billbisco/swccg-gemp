package com.gempukku.swccgo.cards;

import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Legacy batches B23 + B24 (single-version Virtual Block 1-4 cards with an open defect, ai-team-skills
 * shared/gemp-legacy-cards/versions/batches.json): each current card shows its latest text (icons in GEMP notation,
 * published typos kept), and printed stats/uniqueness/lore match the image. Eleven of them have a later PC website card
 * list text (PC-E1: Forced Servitude, Local Trouble, Han Solo, Precision Targeting, Arica, Krayt Dragon Bones, C-3PO,
 * Frostbite, Imperial Stormtrooper, LE-BO2D9, Diversionary Tactics): the current card has it, and the release-sheet text
 * is the 22xx original (LegacyBatchB23B24VersionsTests).
 */
public class LegacyBatchB23B24CurrentCardsTests {
    private static final Map<String, String> PRINTED_TEXT = Map.ofEntries(
            Map.entry("1210_36", "Deploy on table. Your troopers are Heroic. Opponent's total weapon destiny targeting your Heroic cards is +2. When opponent is about to remove their last card from Life Force, you may first place all your troopers in Used Pile (last card must still be removed). (Immune to Alter.)"),
            Map.entry("1210_63", "At the beginning of your battle phase, use 1 Force to initate a battle between two Imperials and an opponent's character present at same interior site. For remainder of turn, no other battles may occur here and opponent takes no battle damage here."),
            Map.entry("1210_92", "During a battle at a site with R2-D2, may not be targeted by weapons, and may draw one destiny; subtract that amount from opponent's total power. Once per game, may place all your non-[Immune to Alter] Effects on table in Used Pile."),
            Map.entry("1210_101", "[Pilot] 3. Cards with 'Han' in title are destiny +3. May use 1 Force to cancel Boba Fett or Jabba's game text for remainder of turn. If opponent just initiated battle here, may use 1 Force to take the first weapons segment action."),
            Map.entry("1220_21", "* Ferocity = 3 + destiny. Habitat: Hoth sites. Deploys only to marker sites. Characters defeated by this Wampa may be relocated to Wampa Cave instead of being eaten (opponent of victim chooses)."),
            Map.entry("1220_43", "Deploy on table. Your capital starship weapon destiny draws are +1. During your deploy phase, may use 1 Force to [download] a starship weapon (for free) on you capital starship. If a starship was just hit, may place this Effect in Lost Pile to make that starship forfeit = 0."),
            Map.entry("1220_123", "Deploy on the Hoth system if you control two exterior Hoth sites. During your control phase, may retrieve 1 Force for each Hoth site you control with a unique (•) combat vehicle. If opponent controls this system, this Effect lost. While you occupy 1st marker, [Immune to Alter]"),
            Map.entry("1220_138", "If you deployed only 1 location (unless it is [Coruscant], [Reflections III], or [Special Edition]), play instead of a Starting Interrupt. [download] Battle Plan, Do, Or Do Not, and an [Immune to Alter] Effect. Revolution, insert cards, and your [Maintenance] and [Permanent Weapon] cards are lost. You go first. Choose one: Jedi: Deploy on table. At start of your turn, turn all cards here face up. During your turn, may use 1 Force for each card stacked here to stack a card from hand face up here. Instead of using X Force, may turn X cards here face down. Agent of Evil: [upload] Luke and a site. Deploy on opponent's side of table; their total battle and duel destiny is +2."),
            Map.entry("1220_161", "Light: During your deploy phase, may [download] a medical droid here. Dark: If you control, with an Imperial present, Force drain +1 here."),
            Map.entry("1230_8", "While with your alien leader, Jabba is immune to attrition and may not be targeted by Interrupts or weapons. May [download] one Scum And Villainy. During battle, may use 1 Force to double the power of one other non-[Maintenance] alien present."),
            Map.entry("1230_16", "Whenever opponent Force drains at a non-battleground, may retrieve 1 Force. During your deploy phase, may [download] an Effect with 'matching' in game text. Once per turn, may place Juri Juice from hand on Force Pile to cancel a Force drain."),
            Map.entry("1230_78", "* Deploys free only as an escorted captive of Jabba or a bounty hunter. Your Strangle destiny draws are +1. 'Escort alone' on Strangle is treated as 'escort.' When released, escort is lost and you may retrieve any three cards (except Strangle)."),
            Map.entry("1230_87", "Deploy on table. Once per character, when you deploy Chewie, Lando, Leia, or Luke to a Tatooine site, may take any one card from Used Pile into hand; reshuffle. While Han is frozen, Rebels are immune to None Shall Pass. Once per game, may [download] a farm. [Immune to Alter]"),
            Map.entry("1230_102", "USED: Target one piloted starship at a system. That starship is hyperspeed -1 for remainder of turn. LOST: If you have 2 unique (•) piloted Rebel (or [Republic]) starfighters in battle at a system, draw destiny. If destiny < 4, exclude one starship from that battle."),
            Map.entry("1240_1", "Power +1 on Tatooine. Once per game, during your move phase, may relocate Arica to Luke's location. During your control phase, if present at a battleground (and Luke is not), may retrieve 1 Force. Immune to Undercover and attrition < 4."),
            Map.entry("1240_9", "Clone. Blaster Rifle deploys (and fires) for free on this trooper. When this trooper fires Blaster Rifle, may draw two weapon destiny and choose one (may take other into hand). Immune to Rebel Barrier."),
            Map.entry("1240_29", "Deploy on table. Womp Rats are not [Selective Creature]. Once per turn, may [download] a creature (except a parasite) or a dewback. Your ferocious creatures are deploy -2 and ferocity +1. Whenever you retrieve Force because a creatue has eaten a card, you need only use X-2 Force for Aim High (to a minimum of 1). (Immune to Alter.)"),
            Map.entry("1240_33", "Deploy on table. Unique (•) Imperial-Class Star Destroyers draw one battle destiny if not able to otherwise and are immune to attrition < 4. During your deploy phase, may use 2 Force to [download] a battleground system. We're In Attack Position Now is canceled. (Immune to Alter.)"),
            Map.entry("1240_68", "May be battled. If with Dash Rendar, adds one destiny to attrition only. Once per turn, if at a battleground, you may peek at the top card of opponent's Reserve Deck; may place that card on top of opponent's Used Pile."));

    private static SwccgCardBlueprintLibrary library;

    @BeforeClass
    public static void setUpClass() {
        library = new SwccgCardBlueprintLibrary();
    }

    private static SwccgCardBlueprint card(String id) {
        SwccgCardBlueprint card = library.getSwccgoCardBlueprint(id);
        assertNotNull(id, card);
        assertNull(id, card.getLegacyVersion());
        return card;
    }

    @Test
    public void CurrentCardsShowThePrintedText() {
        assertEquals(19, PRINTED_TEXT.size());
        PRINTED_TEXT.forEach((id, text) -> assertEquals(id, text, card(id).getGameText()));
    }

    @Test
    public void ImperialEntanglementsShowsTheDownloadIcon() {
        //test1: front prints "may [download] a Tatooine battleground site" (no "from Reserve Deck; reshuffle")
        //test2: back unchanged ("[Virtual Block 1] Local Trouble")
        String text = card("1210_68").getGameText();
        assertTrue(text.contains("{While} this side up, once per turn, may [download] a Tatooine battleground site. Opponent loses no Force")); //test1
        assertTrue(text.contains("[Virtual Block 1] Local Trouble may be played at exterior Tatooine sites.")); //test2
        assertEquals(text, card("1210_68_BACK").getGameText());
    }

    @Test
    public void LocationTextsMatchTheImages() {
        //test1: Echo Med Lab light side prints the download icon
        //test2: Corellia (V) keeps its dark side text (printed upside down on the PC image)
        assertEquals("During your deploy phase, may [download] a medical droid here.", card("1220_161").getLocationLightSideGameText()); //test1
        assertEquals("If you control, with an Imperial present, Force drain +1 here.", card("1220_161").getLocationDarkSideGameText());
        assertEquals("Each of your starships are hyperspeed +1 when moving from here.", card("1240_98").getLocationDarkSideGameText()); //test2
        assertEquals("Once per turn, if you occupy with a Corellian, may activate 1 Force.", card("1240_98").getLocationLightSideGameText());
    }

    @Test
    public void LoreMatchesTheImages() {
        assertEquals("'Looks like somebody's beginning to take an interest in your handiwork.' Imperial stormtroopers coerce local residents to assist them in the apprehension of Rebel scum.", card("1210_63").getLore());
        assertEquals("Cybot Galactica 3PO human-cyborg relations droid. Fluent in over six million forms of communication. 112 years old. Has never been memory-wiped... as far as he knows.", card("1210_92").getLore());
        assertEquals("One of countless elite shock troops totally loyal to the Emperor. Unquestioningly follow orders. Willing to sacrifice their lives to accomplish a mission. First-strike forces.", card("1240_9").getLore());
        assertEquals("Company that produces current generation of Star Destroyers, as well as Nebulon-B Frigate. Ship yards are extremely well defended.", card("1240_33").getLore());
    }

    @Test
    public void LocalTroubleAndPrecisionTargetingArePrintedWithoutUniquenessDot() {
        assertEquals(Uniqueness.UNRESTRICTED, card("1210_63").getUniqueness());
        assertEquals(Uniqueness.UNRESTRICTED, card("1220_43").getUniqueness());
    }

    @Test
    public void SecondPrecisionTargetingMayDeployWhileOneIsOnTable() {
        var scn = new VirtualTableScenario(
                new HashMap<>(),
                new HashMap<>() {{
                    put("pt1", "1220_43");
                    put("pt2", "1220_43");
                }},
                10, 10,
                StartingSetup.DefaultLSGroundLocation,
                StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open);
        var pt1 = scn.GetDSCard("pt1");
        var pt2 = scn.GetDSCard("pt2");
        scn.StartGame();
        scn.MoveCardsToDSSideOfTable(pt1);
        scn.MoveCardsToDSHand(pt2);
        scn.SkipToDSTurn(Phase.DEPLOY);
        assertInZone(Zone.SIDE_OF_TABLE, pt1);
        assertTrue(scn.DSCardPlayAvailable(pt2));
        scn.DSPlayCard(pt2);
        scn.PassAllResponses();
        assertInZone(Zone.SIDE_OF_TABLE, pt2);
    }
}
