package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
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

public class Card_1263_032_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("guild", "1263_32");
                    put("laudica", "1230_72");
                    put("han", "1_11");
                    put("corran", "10_5");
                    put("junk", "1_17");
                    put("luke", "1_19");
                    put("yavin", "1_135");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                    put("boba", "5_91");
                    put("lando", "5_99");
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
    public void SpaceportScoundrelsGuildStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("guild").getBlueprint();

        assertEquals("Spaceport Scoundrels Guild", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.DIAMOND_1, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(0, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.LOCATION);
        }});
        assertEquals(CardSubtype.SITE, card.getCardSubtype());
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EXTERIOR_SITE);
            add(Icon.PLANET);
            add(Icon.SPECIAL_EDITION);
            add(Icon.DARK_FORCE);
            add(Icon.LIGHT_FORCE);
            add(Icon.LEGACY_BLOCK_6);
        }});
        assertEquals(1, card.getIconCount(Icon.DARK_FORCE));
        assertEquals(1, card.getIconCount(Icon.LIGHT_FORCE));
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_6_3, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void SpaceportScoundrelsGuildOncePerGameExchangesTopLostPileCorellianExceptCorranOrHan() {
        var scn = GetScenario();
        var guild = scn.GetLSCard("guild");
        var laudica = scn.GetLSCard("laudica");
        var junk = scn.GetLSCard("junk");

        scn.StartGame();
        scn.MoveCardsToLSHand(guild);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(guild));
        scn.LSDeployLocation(guild);
        scn.PassAllResponses();
        scn.MoveCardsToTopOfLSLostPile(laudica);
        scn.MoveCardsToLSHand(junk, scn.GetLSCard("luke"));
        scn.SkipToLSTurn(Phase.CONTROL);

        assertTrue(scn.LSCardActionAvailable(guild, "Exchange top of Lost Pile"));
        scn.LSUseCardAction(guild, "Exchange top of Lost Pile");
        assertTrue(scn.LSHasCardChoiceAvailable(junk));
        scn.LSChooseCard(junk);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, laudica);
        assertTrue(junk.getZone() == Zone.LOST_PILE || junk.getZone() == Zone.TOP_OF_LOST_PILE);
    }

    @Test
    public void SpaceportScoundrelsGuildDoesNotOfferExchangeWhenTopLostPileIsHan() {
        var scn = GetScenario();
        var guild = scn.GetLSCard("guild");
        var han = scn.GetLSCard("han");
        var junk = scn.GetLSCard("junk");

        scn.StartGame();
        scn.MoveLocationToTable(guild);
        scn.MoveCardsToTopOfLSLostPile(han);
        scn.MoveCardsToLSHand(junk);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(guild, "Exchange top of Lost Pile"));
    }

    @Test
    public void SpaceportScoundrelsGuildDoesNotOfferExchangeWhenTopLostPileIsCorran() {
        var scn = GetScenario();
        var guild = scn.GetLSCard("guild");
        var corran = scn.GetLSCard("corran");
        var junk = scn.GetLSCard("junk");

        scn.StartGame();
        scn.MoveLocationToTable(guild);
        scn.MoveCardsToTopOfLSLostPile(corran);
        scn.MoveCardsToLSHand(junk);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardActionAvailable(guild, "Exchange top of Lost Pile"));
    }

    @Test
    public void SpaceportScoundrelsGuildDarkMayNotDeployToYavin4() {
        var scn = GetScenario();
        var guild = scn.GetLSCard("guild");
        var yavin = scn.GetLSCard("yavin");

        scn.StartGame();
        scn.MoveLocationToTable(yavin);
        scn.MoveCardsToLSHand(guild);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSDeployAvailable(guild));
        scn.LSDeployLocation(guild);
        scn.PassAllResponses();
        assertFalse("Yavin 4".equals(guild.getPartOfSystem()));
    }

    @Test
    public void SpaceportScoundrelsGuildCharactersExceptLandoBountyHuntersCorelliansAndSmugglersArePowerAndForfeitMinusOne() {
        var scn = GetScenario();
        var guild = scn.GetLSCard("guild");
        var laudica = scn.GetLSCard("laudica");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var boba = scn.GetDSCard("boba");
        var lando = scn.GetDSCard("lando");

        scn.StartGame();
        scn.MoveLocationToTable(guild);
        scn.MoveCardsToLocation(guild, laudica, luke, trooper, boba, lando);

        assertEquals(trooper.getBlueprint().getPower() - 1, scn.GetPower(trooper), scn.epsilon);
        assertEquals(trooper.getBlueprint().getForfeit() - 1, scn.GetForfeit(trooper), scn.epsilon);
        assertEquals(luke.getBlueprint().getPower() - 1, scn.GetPower(luke), scn.epsilon);
        assertEquals(luke.getBlueprint().getForfeit() - 1, scn.GetForfeit(luke), scn.epsilon);
        assertEquals(laudica.getBlueprint().getPower(), scn.GetPower(laudica), scn.epsilon);
        assertEquals(boba.getBlueprint().getPower(), scn.GetPower(boba), scn.epsilon);
        assertEquals(lando.getBlueprint().getPower(), scn.GetPower(lando), scn.epsilon);
    }
}
