package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.logic.GameUtils;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

/**
 * Hoth and Dagobah text states whose text differs from the current card only in wording (docs/legacy-versions.md,
 * "Text states"): printed (o) states from the plain wiki.swccg.com articles and Decipher errata (d) states from the
 * (Errata) articles. Each extends its bare class and changes only the text and text-state information, so the tests
 * check the exact text, the text state and that it is the same card as the bare printing (same stats and game text
 * code; see each class's javadoc for why the wording plays the same).
 */
public class Card_3_4_Wording_TextStates_Tests {
    /** id, title, text, light side, label. */
    private static final Object[][] CARDS = {
        {"3_100o", "Death Squadron", "Deploy on any system. Adds X to total power of your starships at the system, where X = the number of your starships present. Your troopers and combat vehicles may be shuttled to related sites for free.", false, "Printed"},
        {"3_69o", "Snowspeeder", "May add 1 pilot or passenger. Permanent pilot aboard provides ability of 1. May move as a 'react' only to Hoth sites.", true, "Printed"},
        {"3_152o", "Stalker", "May add 6 pilots, 8 passengers, 2 vehicles and 4 TIEs. Has ship-docking capability. Permanent pilot aboard provides ability of 1. Probe Droids deploy free to related sites at same system.", false, "Printed"},
        {"4_169o", "IG-2000", "May add 1 pilot (must be a smuggler or bounty hunter) and 2 passengers. Maneuver +3 and immune to attrition < 3 if IG-88 piloting. Ion Cannon may deploy here.", false, "Printed"},
        {"4_171o", "Punishing One", "May add 1 pilot (must be a smuggler or bounty hunter) and 1 passenger. Immune to attrition < 3 if Dengar piloting. Deploy -3 when deploying with Dengar. Boosted TIE Cannon may deploy here.", false, "Printed"},
        {"4_172o", "TIE Avenger", "Deploy -1 to same system as any Imperial capital starship. May add 1 pilot. Boosted TIE Cannon may deploy here.", false, "Printed"},
        {"4_143o", "HoloNet Transmission", "USED: Cancel Transmission Terminated. LOST: Search your Used Pile and take one Imperial or Visage Of The Emperor into hand. Shuffle, cut and replace.", false, "Printed"},
        {"3_70o", "Tauntaun", "May carry 1 passenger. Deploy only on Hoth, but may move elsewhere. Ability = 1/4. May move as a 'react' from a battle. May sacrifice (lose) to make passenger immune to Exposure this turn.", true, "Printed"},
        {"3_21o", "Tauntaun Handler", "Adds 2 to power of any creature vehicle he drives. On a tauntaun, draws one battle destiny if not able to otherwise.", true, "Printed"},
        {"3_97o", "Probe Antennae", "Deploy on any Probe Droid. Adds 2 to X. OR Use 1 Force to deploy on one of your other droids. When at a site you control, once each turn during your control phase, you may glance at one card randomly selected from opponent's hand.", false, "Printed"},
        {"3_13o", "Major Bren Derlin", "While on Hoth, may use 1 Force to cancel Breached Defenses and opponent may not 'react' to any Echo site. While at Cantina, power +1 and may use 1 Force to cancel Local Trouble.", true, "Printed"},
        {"3_47o", "One More Pass", "Add ability of pilot to Power Harpoon weapon destiny (before weapon destiny is drawn).", true, "Printed"},
        {"4_121o", "Field Promotion", "Deploy on an Imperial of ability < 5 present with Vader, Emperor Palpatine or one of your admirals, generals or moffs. Imperial gains '''leader''' skill, is power +1 and is immune to Demotion, Report To Lord Vader and What Is Thy Bidding, My Master?. (Immune to Alter.)", false, "Printed"},
        {"4_42o", "What Is Thy Bidding, My Master?", "Deploy on Emperor Palpatine or any system. Target one character whose lore uses the word 'Emperor.' Target cannot use ability to draw battle destiny and is excluded from being the \"highest-ability character.\" Utinni Effect canceled when reached by target.", true, "Printed"},
        {"4_125o", "Knowledge And Defense", "Insert in opponent's Reserve Deck. When Effect reaches top it is lost, but opponent may not initiate any battles for remainder of turn. (Immune to Alter.)", false, "Printed"},
        {"3_157o", "Blizzard Walker", "May add 2 pilots and 8 passengers. Immune to attrition < 4.", false, "Printed"},
        {"3_154o", "Blizzard 1", "May add 2 pilots and 8 passengers. Immune to attrition < 4. When using AT-AT Cannon to Target The Main Generator, adds 1 to total.", false, "Printed"},
        {"3_155o", "Blizzard 2", "May add 1 pilot and 8 passengers. Immune to attrition < 4. Permanent pilot aboard provides ability of 2.", false, "Printed"},
        {"4_125d", "Knowledge And Defense", "Insert in opponent's Reserve Deck. When Effect reaches top it is lost and opponent may not initiate any battles for remainder of turn. (Immune to Alter.)", false, "D-E1"},
        {"3_71d", "Anakin's Lightsaber", "Deploy on your Skywalker of ability > 3. May add 1 to Force drain where present. May target a character or creature for free. Draw two destiny. Target hit, and its forfeit = 0, if total destiny > defense value. Anakin's Lightsaber is a matching weapon for any persona of a Skwalker, such as Luke or Leia, that meets the deploy conditions. It may only deploy upon Vader if he has \"crossed over\" to the Light Side, once again answering to the name of Anakin.", true, "D-E1"},
        {"3_29o", "Echo Trooper Backpack", "Deploy on any trooper. May carry and use any number of weapons and devices. Trooper is immune to attrition < 3 when at a planet site.", true, "Printed"},
        {"3_35o", "Evacuation Control", "Deploy on one of your war rooms. Once per turn, your Planet Defender Ion Cannon at same planet may fire during your move phase. Also, your Medium Transports at same planet are hyperspeed +2, are immune to attrition < 3 and may move for free.", true, "Printed"},
    };

    private static SwccgCardBlueprint blueprint(String id, boolean light) {
        var scn = new VirtualTableScenario(
                light ? new HashMap<>() {{ put("card", id); }} : new HashMap<>(),
                light ? new HashMap<>() : new HashMap<>() {{ put("card", id); }},
                10, 10, StartingSetup.DefaultLSGroundLocation, StartingSetup.DefaultDSGroundLocation,
                StartingSetup.NoLSStartingInterrupts, StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields, StartingSetup.NoDSShields, "anything_goes");
        return (light ? scn.GetLSCard("card") : scn.GetDSCard("card")).getBlueprint();
    }

    @Test
    public void WordingTextStatesHaveTheirTextAndAreTheSameCard() {
        for (Object[] c : CARDS) {
            String id = (String) c[0];
            boolean light = (Boolean) c[3];
            String bareId = id.substring(0, id.length() - 1);
            var state = blueprint(id, light);
            var bare = blueprint(bareId, light);
            assertEquals(id, c[2], state.getGameText());
            assertNotEquals(id, bare.getGameText(), state.getGameText());
            TextState ts = state.getTextState();
            assertEquals(id, bareId, ts.currentId());
            assertEquals(id, c[4], ts.label());
            assertEquals(id, bare.getTitle() + " [" + c[4] + "]", GameUtils.getFullName(state));
            assertEquals(id, bare.getTitle(), state.getTitle());
            assertEquals(id, bare.getUniqueness(), state.getUniqueness());
            assertEquals(id, bare.getPersonas(), state.getPersonas());
            assertEquals(id, bare.getLore(), state.getLore());
            assertEquals(id, bare.getSide(), state.getSide());
            assertEquals(id, bare.getCardCategory(), state.getCardCategory());
            assertEquals(id, bare.getRarity(), state.getRarity());
            assertEquals(id, bare.getExpansionSet(), state.getExpansionSet());
            assertEquals(id, bare.getClass(), state.getClass().getSuperclass());
            assertNull(id, bare.getTextState());
        }
    }
}
