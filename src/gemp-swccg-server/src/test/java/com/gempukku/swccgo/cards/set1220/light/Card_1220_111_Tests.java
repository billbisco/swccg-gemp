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

public class Card_1220_111_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("advantage", "1220_111");
                    put("luke", "1_19");
                    put("saber", "1_155");
                }},
                new HashMap<>() {{
                    put("stormtrooper", "1_194");
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
    public void AdvantageStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("advantage").getBlueprint();

        assertEquals("Advantage", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(4, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_LOCATION);
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
    public void AdvantageDeploysOnYourBattleground() {
        var scn = GetScenario();
        var advantage = scn.GetLSCard("advantage");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke);
        scn.MoveCardsToLSHand(advantage);
        scn.SkipToLSTurn(Phase.DEPLOY);
        assertTrue(scn.LSCardPlayAvailable(advantage));
        scn.LSPlayCard(advantage);
        scn.LSChooseCard(site);
        scn.PassAllResponses();
        assertEquals(site, advantage.getAttachedTo());
    }

    @Test
    public void AdvantageReducesOpponentTotalPowerByTwoWhenHitByALightsaber() {
        var scn = GetScenario();
        var advantage = scn.GetLSCard("advantage");
        var luke = scn.GetLSCard("luke");
        var saber = scn.GetLSCard("saber");
        var stormtrooper = scn.GetDSCard("stormtrooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, luke, stormtrooper);
        scn.AttachCardsTo(site, advantage);
        scn.AttachCardsTo(luke, saber);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.LSInitiateBattle(site);
        scn.PassBattleStartResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.PrepareLSDestiny(7);
        scn.PrepareLSDestiny(7);
        scn.LSUseCardAction(saber, "Fire");
        scn.LSChooseCard(stormtrooper);
        scn.PassForceUseResponses();
        scn.PassWeaponFireWithDestinyDraw(2);
        if (scn.GetLSAvailableActions().toString().contains("Make opponent's total power")) {
            scn.LSChooseAction("Make opponent's total power");
        }
        scn.PassAllResponses();
        assertTrue(stormtrooper.isHit());
    }
}
