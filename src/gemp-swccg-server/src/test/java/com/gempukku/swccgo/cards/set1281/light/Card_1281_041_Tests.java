package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
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

public class Card_1281_041_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("undercity", "1281_41");
                    put("narshaddaa", "200_58");
                    put("rest", "1281_39");
                    put("guild", "1263_32"); // Spaceport Scoundrels Guild
                }},
                new HashMap<>() {{
                    put("boba", "5_91");
                    put("trooper", "1_194");
                    put("noescape", "112_016"); // No Escape: Force generation +1 at opponent's generic sites
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
    public void NarShaddaaUndercityStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("undercity").getBlueprint();

        assertEquals("Nar Shaddaa: Undercity", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<Keyword>());
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.UNDERGROUND);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_8);
        }});
        assertEquals(0, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(2, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void NarShaddaaUndercityBountyHuntersArePowerPlusOneOnNarShaddaa() {
        var scn = GetScenario();
        var undercity = scn.GetLSCard("undercity");
        var rest = scn.GetLSCard("rest");
        var narshaddaa = scn.GetLSCard("narshaddaa");
        var boba = scn.GetDSCard("boba");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boba);
        scn.SkipToPhase(Phase.CONTROL);
        int offPlanet = scn.GetPower(boba);

        scn.MoveLocationToTable(narshaddaa);
        scn.MoveLocationToTable(undercity);
        scn.MoveLocationToTable(rest);
        scn.MoveCardsToLocation(rest, boba);
        scn.SkipToPhase(Phase.DEPLOY);

        assertEquals(offPlanet + 1, scn.GetPower(boba));
        scn.MoveCardsToLocation(undercity, boba);
        scn.SkipToPhase(Phase.CONTROL);
        assertEquals(offPlanet + 1, scn.GetPower(boba));
    }

    @Test
    public void NarShaddaaUndercityScoundrelsGuildIsImmuneToNoEscapeAndYourForceGenerationThereIsPlusOne() {
        var scn = GetScenario();
        var undercity = scn.GetLSCard("undercity");
        var narshaddaa = scn.GetLSCard("narshaddaa");
        var guild = scn.GetLSCard("guild");
        var noescape = scn.GetDSCard("noescape");

        scn.StartGame();
        scn.MoveLocationToTable(guild);
        scn.MoveCardsToDSSideOfTable(noescape);
        scn.SkipToPhase(Phase.CONTROL);

        var mq = scn.game().getModifiersQuerying();
        float lsBase = mq.getForceGenerationFromLocation(scn.gameState(), guild, scn.LS);
        float dsWithNoEscape = mq.getForceGenerationFromLocation(scn.gameState(), guild, scn.DS);

        // Control: No Escape adds 1 to the Dark Side's generation at the Guild (an opponent's generic site).
        scn.MoveOutOfPlay(noescape);
        scn.SkipToPhase(Phase.DEPLOY);
        assertEquals(dsWithNoEscape - 1, mq.getForceGenerationFromLocation(scn.gameState(), guild, scn.DS), scn.epsilon);
        scn.MoveCardsToDSSideOfTable(noescape);

        scn.MoveLocationToTable(narshaddaa);
        scn.MoveLocationToTable(undercity);
        scn.SkipToPhase(Phase.BATTLE);

        // With Undercity: the Guild is immune to No Escape, and the Light Side's generation there is +1.
        assertEquals(dsWithNoEscape - 1, mq.getForceGenerationFromLocation(scn.gameState(), guild, scn.DS), scn.epsilon);
        assertEquals(lsBase + 1, mq.getForceGenerationFromLocation(scn.gameState(), guild, scn.LS), scn.epsilon);
    }
}
