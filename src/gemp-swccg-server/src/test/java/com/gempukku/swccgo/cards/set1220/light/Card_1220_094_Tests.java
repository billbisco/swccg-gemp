package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1220_094_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("lando", "1220_94");
                    put("falcon", "1_143");
                    put("xwing", "1_146");
                    put("tatooine", "1_127");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("darkLando", "5_99");
                    put("trooper", "1_194");
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
    public void LandoCalrissianStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("lando").getBlueprint();

        assertEquals("Lando Calrissian", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(1, card.getDestiny(), scn.epsilon);
        assertEquals(3, card.getDeployCost(), scn.epsilon);
        assertEquals(3, card.getPower(), scn.epsilon);
        assertEquals(3, card.getAbility(), scn.epsilon);
        assertEquals(6, card.getForfeit(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.ALIEN);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.GAMBLER);
        }});
        scn.BlueprintPersonaCheck(card, new ArrayList<>() {{
            add(Persona.LANDO);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.ALIEN);
            add(Icon.PILOT);
            add(Icon.WARRIOR);
            add(Icon.CLOUD_CITY);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LandoCalrissianAddsTwoToPowerOfAnythingHePilots() {
        var scn = GetScenario();
        var lando = scn.GetLSCard("lando");
        var xwing = scn.GetLSCard("xwing");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, xwing);
        scn.BoardAsPilot(xwing, lando);
        assertEquals(xwing.getBlueprint().getPower().intValue() + 2, scn.GetPower(xwing));
    }

    @Test
    public void LandoCalrissianAddsThreeToPowerOfFalconHePilots() {
        var scn = GetScenario();
        var lando = scn.GetLSCard("lando");
        var falcon = scn.GetLSCard("falcon");
        var tatooine = scn.GetLSCard("tatooine");

        scn.StartGame();
        scn.MoveLocationToTable(tatooine);
        scn.MoveCardsToLocation(tatooine, falcon);
        scn.BoardAsPilot(falcon, lando);
        assertEquals(falcon.getBlueprint().getPower().intValue() + 3, scn.GetPower(falcon));
    }

    @Test
    public void LandoCalrissianIsHeroicWhileWithLeia() {
        var scn = GetScenario();
        var lando = scn.GetLSCard("lando");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        assertFalse(Filters.and(Keyword.HEROIC).accepts(scn.game(), lando));
        scn.MoveCardsToLocation(site, leia);
        assertTrue(Filters.and(Keyword.HEROIC).accepts(scn.game(), lando));
    }

    @Test
    public void LandoCalrissianConvertsOpponentsLandoPresentAtASite() {
        var scn = GetScenario();
        var lando = scn.GetLSCard("lando");
        var darkLando = scn.GetDSCard("darkLando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, darkLando);
        scn.SkipToLSTurn(Phase.CONTROL);
        assertTrue(scn.LSCardActionAvailable(lando, "Convert opponent's Lando"));
        scn.LSUseCardAction(lando, "Convert opponent's Lando");
        scn.LSChooseCard(darkLando);
        scn.PassAllResponses();
        assertEquals(scn.LS, darkLando.getOwner());
    }

    @Test
    public void LandoCalrissianCancelsGameTextOfOpponentsAbilityLessThanFiveCharacterDuringBattle() {
        var scn = GetScenario();
        var lando = scn.GetLSCard("lando");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, trooper);
        scn.EnsureLSForcePile(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        assertTrue(scn.LSCardActionAvailable(lando, "Cancel a character's game text"));
        scn.LSUseCardAction(lando, "Cancel a character's game text");
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), trooper));
    }
}
