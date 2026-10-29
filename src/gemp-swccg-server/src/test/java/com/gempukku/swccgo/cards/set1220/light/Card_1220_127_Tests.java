package com.gempukku.swccgo.cards.set1220.light;

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
import static org.junit.Assert.assertTrue;

public class Card_1220_127_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("alderaan", "1220_127");
                    put("leia", "1_17");
                }},
                new HashMap<>() {{
                    put("motti", "1_164");
                    put("vader", "1_168");
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
    public void LeiaOfAlderaanStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("alderaan").getBlueprint();

        assertEquals("Leia Of Alderaan", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.CLOUD_CITY);
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_2);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LeiaOfAlderaanDeploysOnLeiaAndMakesHerPowerPlusTwo() {
        var scn = GetScenario();
        var alderaan = scn.GetLSCard("alderaan");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        int before = scn.GetPower(leia);
        scn.MoveCardsToLSHand(alderaan);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(alderaan));
        scn.LSPlayCard(alderaan);
        scn.LSChooseCard(leia);
        scn.PassAllResponses();
        assertEquals(leia, alderaan.getAttachedTo());
        assertEquals(before + 2, scn.GetPower(leia));
    }

    @Test
    public void LeiaOfAlderaanCancelsGameTextOfOpponentLeaderAbilityLessThanFourDuringBattle() {
        var scn = GetScenario();
        var alderaan = scn.GetLSCard("alderaan");
        var leia = scn.GetLSCard("leia");
        var motti = scn.GetDSCard("motti");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, motti);
        scn.AttachCardsTo(leia, alderaan);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.LSCardActionAvailable(alderaan, "Cancel a leader's game text"));
        scn.LSUseCardAction(alderaan, "Cancel a leader's game text");
        scn.LSChooseCard(motti);
        scn.PassAllResponses();
        assertTrue(scn.game().getModifiersQuerying().isGameTextCanceled(scn.gameState(), motti));
    }
}
