package com.gempukku.swccgo.cards.set1210.light;

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

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1210_171_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("blaster", "1210_171");
                    put("leia", "1_17");
                    put("boushh", "110_1");
                }},
                new HashMap<>() {{
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
    public void LeiasSportingBlasterStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("blaster").getBlueprint();

        assertEquals("Leia's Sporting Blaster", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.CHARACTER, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.WEAPON);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.BLASTER);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.WEAPON);
            add(Icon.LEGACY_BLOCK_1);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void LeiasSportingBlasterDeploysOnLeia() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var leia = scn.GetLSCard("leia");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia);
        scn.MoveCardsToLSHand(blaster);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertTrue(scn.LSDeployAvailable(blaster));
        scn.LSDeployCard(blaster);
        scn.LSChooseCard(leia);
        scn.PassAllResponses();

        assertTrue(scn.IsAttachedTo(leia, blaster));
    }

    @Test
    public void LeiasSportingBlasterFiresAndMakesTargetLost() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var leia = scn.GetLSCard("leia");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, leia, trooper);
        scn.AttachCardsTo(leia, blaster);
        scn.LSActivateForceCheat(2);
        scn.SkipToLSTurn(Phase.BATTLE);
        assertTrue(scn.AwaitingLSBattlePhaseActions());
        scn.LSInitiateBattle(site);
        scn.PassAllResponses();
        assertTrue(scn.AwaitingLSWeaponsSegmentActions());
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(blaster, "Fire"));
        scn.LSUseCardAction(blaster, "Fire");
        scn.LSChooseCard(trooper);
        scn.PassAllResponses();

        assertInZone(Zone.LOST_PILE, trooper);
    }

    @Test
    public void LeiasSportingBlasterBreaksCoverThenFires() {
        var scn = GetScenario();
        var blaster = scn.GetLSCard("blaster");
        var boushh = scn.GetLSCard("boushh");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, boushh, trooper);
        scn.MakeCardGoUndercover(boushh);
        scn.AttachCardsTo(boushh, blaster);
        scn.SkipToDSTurn(Phase.MOVE);
        assertTrue(scn.AwaitingDSMovePhaseActions());
        scn.DSPass();
        assertTrue(boushh.isUndercover());
        scn.PrepareLSDestiny(5);
        assertTrue(scn.LSCardActionAvailable(blaster, "Break cover"));
        scn.LSUseCardAction(blaster, "Break cover");
        scn.PassAllResponses();

        assertFalse(boushh.isUndercover());
        assertInZone(Zone.LOST_PILE, trooper);
    }
}
