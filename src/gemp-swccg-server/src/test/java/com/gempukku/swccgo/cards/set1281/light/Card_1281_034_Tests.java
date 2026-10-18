package com.gempukku.swccgo.cards.set1281.light;

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

public class Card_1281_034_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("bravado", "1281_34");
                    put("lando", "601_50");
                }},
                new HashMap<>() {{
                    put("trooper", "1_194");
                }},
                10,
                10,
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
    public void ScoundrelsBravadoStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("bravado").getBlueprint();

        assertEquals("Scoundrel's Bravado", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(3, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_CHARACTERS);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.SCOUNDREL);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_8_1, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void ScoundrelsBravadoCannotDeployFromHand() {
        var scn = GetScenario();
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        scn.MoveCardsToLSHand(bravado);
        scn.SkipToLSTurn(Phase.DEPLOY);

        assertFalse(scn.LSDeployAvailable(bravado));
        assertEquals(Zone.HAND, bravado.getZone());
    }

    @Test
    public void ScoundrelsBravadoAddsTwoDefenseValueAndImmunityToAttritionLessThanFive() {
        var scn = GetScenario();
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando);
        int defense = scn.GetDefense(lando);
        assertEquals(0, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), lando), scn.epsilon);

        scn.AttachCardsTo(lando, bravado);
        scn.SkipToPhase(Phase.CONTROL);

        assertTrue(scn.IsAttachedTo(lando, bravado));
        assertEquals(defense + 2, scn.GetDefense(lando));
        assertEquals(5, scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(), lando), scn.epsilon);
    }

    @Test
    public void ScoundrelsBravadoMayAddABattleDestinyDuringBattle() {
        var scn = GetScenario();
        var bravado = scn.GetLSCard("bravado");
        var lando = scn.GetLSCard("lando");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLocation(site, lando, trooper);
        scn.AttachCardsTo(lando, bravado);
        scn.LSActivateForceCheat(3);
        scn.LSStartBattleAndSkipToWeaponsSegment(site);

        assertTrue(scn.LSCardActionAvailable(bravado, "Add a battle destiny"));
    }
}
