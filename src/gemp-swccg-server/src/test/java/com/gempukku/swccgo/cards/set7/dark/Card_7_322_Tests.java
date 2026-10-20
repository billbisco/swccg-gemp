package com.gempukku.swccgo.cards.set7.dark;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_7_322_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>()
                {{
                    put("corvette", "1_140"); //Corellian Corvette (capital ship)
                    put("xwing", "1_146"); //X-wing (non-capital ship)
                }},
                new HashMap<>()
                {{
                    put("missile", "7_322"); //Intruder Missile
                    put("bomber", "4_173"); //TIE Bomber
                }},
                10,
                10,
                StartingSetup.DefaultLSSpaceSystem,
                StartingSetup.DefaultDSSpaceSystem,
                StartingSetup.NoLSStartingInterrupts,
                StartingSetup.NoDSStartingInterrupts,
                StartingSetup.NoLSShields,
                StartingSetup.NoDSShields,
                VirtualTableScenario.Open
        );
    }

    @Test
    public void IntruderMissileStatsAndKeywordsAreCorrect() {
        /**
         * Title: Intruder Missile
         * Uniqueness: Unrestricted
         * Side: Dark
         * Type: Weapon
         * Subtype: Starship
         * Destiny: 7
         * Icons: Weapon, Special Edition
         * Keywords: Missile
         * Game Text: Deploy on your TIE Bomber, TIE Defender or freighter. May target a capital starship for free.
         *      Draw destiny. Add 3 if that capital starship was targeted by another weapon this turn.
         *      Target hit if total destiny > defense value. After firing, place Missile in Used Pile.
         * Lore: Produced by Sienar for TIE bombers and TIE defenders, using plans stolen from Slayn & Korpil
         *      by ISB agents. Often sold to bounty hunters and mercenaries by Black Sun.
         * Set: Special Edition
         * Rarity: F
         */

        var scn = GetScenario();

        var card = scn.GetDSCard("missile").getBlueprint();

        assertEquals("Intruder Missile", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNRESTRICTED, card.getUniqueness());
        assertEquals(Side.DARK, card.getSide());
        assertFalse(card.hasAlternateImageSuffix());
        assertEquals(7, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        assertEquals(CardSubtype.STARSHIP, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.MISSILE);
            add(Keyword.STARSHIP_WEAPON_THAT_DEPLOYS_ON_STARFIGHTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.SPECIAL_EDITION);
        }});
        assertEquals(ExpansionSet.SPECIAL_EDITION,card.getExpansionSet());
        assertEquals(Rarity.F,card.getRarity());
    }

    @Test
    public void IntruderMissileMayTargetCapitalStarship() {
        var scn = GetScenario();

        var corvette = scn.GetLSCard("corvette");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetDSStartingLocation();

        var bomber = scn.GetDSCard("bomber");
        var missile = scn.GetDSCard("missile");

        scn.StartGame();

        scn.MoveCardsToLocation(system, corvette, xwing, bomber);
        scn.AttachCardsTo(bomber, missile);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);

        scn.LSPass();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(missile));
        scn.DSUseCardAction(missile);

        assertTrue(scn.DSHasCardChoiceAvailable(corvette));
        assertFalse(scn.DSHasCardChoiceAvailable(xwing));
    }

    @Test
    public void IntruderMissileStaysAttachedIfTargetingCanceled() {
        var scn = GetScenario();

        var corvette = scn.GetLSCard("corvette");
        var xwing = scn.GetLSCard("xwing");
        var system = scn.GetDSStartingLocation();

        var bomber = scn.GetDSCard("bomber");
        var missile = scn.GetDSCard("missile");

        scn.StartGame();

        scn.MoveCardsToLocation(system, corvette, xwing, bomber);
        scn.AttachCardsTo(bomber, missile);

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);

        scn.LSPass();
        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(missile));
        scn.DSUseCardAction(missile);

        assertTrue(scn.DSHasCardChoiceAvailable(corvette));
        scn.DSPass(); //Done (cancel targeting)

        assertTrue(scn.AwaitingDSWeaponsSegmentActions());
        assertTrue(scn.DSCardActionAvailable(missile));
        assertTrue(scn.IsAttachedTo(bomber, missile));
    }

    @Test
    public void IntruderMissileSentToUsedPileAfterFiring() {
        var scn = GetScenario();

        var corvette = scn.GetLSCard("corvette");
        var system = scn.GetDSStartingLocation();

        var bomber = scn.GetDSCard("bomber");
        var missile = scn.GetDSCard("missile");

        scn.StartGame();

        scn.MoveCardsToLocation(system, corvette, bomber);
        scn.AttachCardsTo(bomber, missile);

        scn.PrepareDSDestiny(0); //miss

        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(system);

        scn.LSPass();
        scn.DSUseCardAction(missile);
        scn.DSChooseCard(corvette);
        scn.PassAllResponses();

        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertFalse(scn.IsAttachedTo(bomber, missile));
        assertEquals(Zone.TOP_OF_USED_PILE, missile.getZone());
    }
}
