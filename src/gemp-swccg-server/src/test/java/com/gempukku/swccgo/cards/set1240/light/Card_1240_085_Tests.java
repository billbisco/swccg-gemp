package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.legacy.EyesInTheDarkDejarikTestBase;
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
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1240_085_Tests extends EyesInTheDarkDejarikTestBase {
    @Override
    protected String eyesBlueprintId() {
        return "1240_85";
    }

    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("eyes", "1240_85");
                    put("holo", "2_63");
                    put("beggar", "1240_89"); // any Effect of yours (Scrambled Transmission (V))
                }},
                new HashMap<>() {{
                    put("wipe9", "1109_50");
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
    public void EyesInTheDarkVStatsIconsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("eyes").getBlueprint();

        assertEquals("Eyes In The Dark", card.getTitle());
        assertTrue(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        assertEquals(CardSubtype.NORMAL, card.getCardSubtype());
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
            add(Keyword.DEPLOYS_ON_SITE);
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.LEGACY_BLOCK_4);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_4, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void EyesInTheDarkVDeploysOnlyOnAHolosite() {
        var scn = GetScenario();
        var eyes = scn.GetLSCard("eyes");

        scn.StartGame();
        scn.MoveCardsToLSHand(eyes);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.EnsureForcePile(scn.LS, 8);
        assertFalse(scn.LSCardPlayAvailable(eyes));

        var scn2 = GetScenario();
        var eyes2 = scn2.GetLSCard("eyes");
        var holo2 = scn2.GetLSCard("holo");
        scn2.StartGame();
        scn2.MoveLocationToTable(holo2);
        scn2.MoveCardsToLSHand(eyes2);
        scn2.SkipToLSTurn(Phase.DEPLOY);
        scn2.EnsureForcePile(scn2.LS, 8);
        assertTrue(scn2.LSCardPlayAvailable(eyes2));
        scn2.LSDeployCard(eyes2);
        if (scn2.LSHasCardChoiceAvailable(holo2)) {
            scn2.LSChooseCard(holo2);
        }
        scn2.PassAllResponses();
        assertEquals(holo2, eyes2.getAttachedTo());
    }

    @Test
    public void EyesInTheDarkVYourEffectsHereMayNotBeSuspended() {
        var scn = GetScenario();
        var eyes = scn.GetLSCard("eyes");
        var holo = scn.GetLSCard("holo");
        var beggar = scn.GetLSCard("beggar");

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        scn.AttachCardsTo(holo, eyes);
        var mq = scn.game().getModifiersQuerying();
        assertFalse(mq.getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_SUSPENDED, eyes).isEmpty());

        // An Effect of yours that is not here is not protected.
        scn.MoveCardsToLSSideOfTable(beggar);
        assertTrue(mq.getModifiersAffectingCard(scn.gameState(), ModifierType.MAY_NOT_BE_SUSPENDED, beggar).isEmpty());
    }

    @Test
    public void EyesInTheDarkVDoesNotIgnoreOriginalVirtualWipeThemOut() {
        var scn = GetScenario();
        var eyes = scn.GetLSCard("eyes");
        var holo = scn.GetLSCard("holo");
        var wipe9 = scn.GetDSCard("wipe9");

        scn.StartGame();
        scn.MoveLocationToTable(holo);
        scn.AttachCardsTo(holo, eyes);
        scn.AttachCardsTo(holo, wipe9);
        assertTrue(scn.game().getModifiersQuerying().getModifiersAffectingCard(scn.gameState(), ModifierType.CANCEL_GAME_TEXT, wipe9).isEmpty());
    }
}
