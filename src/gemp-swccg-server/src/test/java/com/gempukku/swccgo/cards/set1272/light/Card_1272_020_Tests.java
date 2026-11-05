package com.gempukku.swccgo.cards.set1272.light;

import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.framework.StartingSetup;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.gempukku.swccgo.framework.Assertions.assertInZone;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Card_1272_020_Tests {
    protected VirtualTableScenario GetScenario() {
        return new VirtualTableScenario(
                new HashMap<>() {{
                    put("declaration", "1272_20");
                    put("leia", "1_17");
                    put("bail", "204_2");
                    put("mon", "8_22");
                    put("luke", "1_19");
                    put("rebel", "1_28");
                    put("klorslug", "1_53");
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
    public void DeclarationOfRebellionStatsAndKeywordsAreCorrect() {
        var scn = GetScenario();
        var card = scn.GetLSCard("declaration").getBlueprint();

        assertEquals("Declaration Of Rebellion", card.getTitle());
        assertFalse(card.hasVirtualSuffix());
        assertEquals(Uniqueness.UNIQUE, card.getUniqueness());
        assertEquals(Side.LIGHT, card.getSide());
        assertEquals(5, card.getDestiny(), scn.epsilon);
        scn.BlueprintCardTypeCheck(card, new ArrayList<>() {{
            add(CardType.EFFECT);
        }});
        scn.BlueprintKeywordCheck(card, new ArrayList<>() {{
        }});
        scn.BlueprintIconCheck(card, new ArrayList<>() {{
            add(Icon.EFFECT);
            add(Icon.DEATH_STAR_II);
            add(Icon.LEGACY_BLOCK_7);
        }});
        assertEquals(ExpansionSet.VIRTUAL_BLOCK_7_2, card.getExpansionSet());
        assertEquals(Rarity.V, card.getRarity());
    }

    @Test
    public void DeclarationOfRebellionIsImmuneToAlter() {
        var scn = GetScenario();
        var declaration = scn.GetLSCard("declaration");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(declaration);
        assertTrue(scn.game().getModifiersQuerying().isImmuneToCardTitle(scn.gameState(), declaration, Title.Alter));
    }

    @Test
    public void DeclarationOfRebellionBailLeiaAndMonMothmaAreInsurgents() {
        var scn = GetScenario();
        var declaration = scn.GetLSCard("declaration");
        var leia = scn.GetLSCard("leia");
        var mon = scn.GetLSCard("mon");
        var bail = scn.GetLSCard("bail");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetLSStartingLocation();

        scn.StartGame();
        //test1: Bail, Leia and Mon Mothma are Insurgents
        //test2: other characters are not
        scn.MoveCardsToLocation(site, leia, mon, bail, luke);
        assertFalse(scn.HasKeyword(leia, Keyword.INSURGENT));
        scn.MoveCardsToLSSideOfTable(declaration);
        assertTrue(scn.HasKeyword(leia, Keyword.INSURGENT)); //test1
        assertTrue(scn.HasKeyword(mon, Keyword.INSURGENT)); //test1
        assertTrue(scn.HasKeyword(bail, Keyword.INSURGENT)); //test1
        assertFalse(scn.HasKeyword(luke, Keyword.INSURGENT)); //test2
    }

    private void battleWithLeiaAndLukeAtMarketplace(VirtualTableScenario scn, boolean declarationOnTable) {
        var declaration = scn.GetLSCard("declaration");
        var leia = scn.GetLSCard("leia");
        var luke = scn.GetLSCard("luke");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        if (declarationOnTable)
            scn.MoveCardsToLSSideOfTable(declaration);
        scn.MoveCardsToLocation(site, leia, luke, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(3);
        scn.PrepareDSDestiny(1);
        scn.LSInitiateBattle(site);
        scn.SkipToEndOfPowerSegment(true);
    }

    @Test
    public void DeclarationOfRebellionTotalBattleDestinyPlusOneWhileInsurgentPresentAtBattleground() {
        var scn = GetScenario();
        //test1: one battle destiny of 3 makes a total battle destiny of 4
        assertTrue(Filters.battleground.accepts(scn.game(), scn.GetDSStartingLocation()));
        battleWithLeiaAndLukeAtMarketplace(scn, true);
        assertEquals(1, scn.GetLSBattleDestinyCount()); //test1
        assertEquals(4, scn.GetLSTotalDestiny()); //test1
    }

    @Test
    public void TotalBattleDestinyIsNotModifiedWithoutDeclarationOfRebellion() {
        var scn = GetScenario();
        battleWithLeiaAndLukeAtMarketplace(scn, false);
        assertEquals(1, scn.GetLSBattleDestinyCount());
        assertEquals(3, scn.GetLSTotalDestiny());
    }

    @Test
    public void DeclarationOfRebellionTotalBattleDestinyIsNotModifiedWithoutInsurgentPresent() {
        var scn = GetScenario();
        var declaration = scn.GetLSCard("declaration");
        var luke = scn.GetLSCard("luke");
        var rebel = scn.GetLSCard("rebel");
        var trooper = scn.GetDSCard("trooper");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(declaration);
        scn.MoveCardsToLocation(site, luke, rebel, trooper);
        scn.SkipToLSTurn(Phase.BATTLE);
        scn.PrepareLSDestiny(3);
        scn.PrepareDSDestiny(1);
        scn.LSInitiateBattle(site);
        scn.SkipToEndOfPowerSegment(true);
        assertEquals(1, scn.GetLSBattleDestinyCount());
        assertEquals(3, scn.GetLSTotalDestiny());
    }

    @Test
    public void DeclarationOfRebellionOneIsTreatedAsTwoWhenGameTextIsModified() {
        var scn = GetScenario();
        var declaration = scn.GetLSCard("declaration");

        scn.ApplyAdHocModifier(new ModifyGameTextModifier(declaration, Filters.title("Declaration Of Rebellion"),
                ModifyGameTextType.LEGACY__DECLARATION_OF_REBELLION__ONE_IS_TWO));
        //test1: one battle destiny of 3 makes a total battle destiny of 5
        battleWithLeiaAndLukeAtMarketplace(scn, true);
        assertEquals(1, scn.GetLSBattleDestinyCount()); //test1
        assertEquals(5, scn.GetLSTotalDestiny()); //test1
    }

    @Test
    public void DeclarationOfRebellionHitInsurgentWithYourNonUniqueCharacterMayForfeitForFour() {
        var scn = GetScenario();
        var declaration = scn.GetLSCard("declaration");
        var leia = scn.GetLSCard("leia");
        var rebel = scn.GetLSCard("rebel");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(declaration);
        scn.MoveCardsToLocation(site, leia, rebel);
        //test1: a hit Insurgent of forfeit 3 with your non-unique character may forfeit for 4
        scn.ApplyAdHocModifier(new ForfeitModifier(leia, leia, -3));
        assertEquals(3, scn.game().getModifiersQuerying().getForfeitWhenForfeiting(scn.gameState(), leia), scn.epsilon);
        leia.setHit(true);
        assertEquals(4, scn.game().getModifiersQuerying().getForfeitWhenForfeiting(scn.gameState(), leia), scn.epsilon); //test1
    }

    @Test
    public void DeclarationOfRebellionHitInsurgentWithoutNonUniqueCharacterKeepsForfeit() {
        var scn = GetScenario();
        var declaration = scn.GetLSCard("declaration");
        var leia = scn.GetLSCard("leia");
        var luke = scn.GetLSCard("luke");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(declaration);
        scn.MoveCardsToLocation(site, leia, luke);
        scn.ApplyAdHocModifier(new ForfeitModifier(leia, leia, -3));
        leia.setHit(true);
        assertEquals(3, scn.game().getModifiersQuerying().getForfeitWhenForfeiting(scn.gameState(), leia), scn.epsilon);
    }

    @Test
    public void DeclarationOfRebellionDoesNotLowerHigherForfeit() {
        var scn = GetScenario();
        var declaration = scn.GetLSCard("declaration");
        var mon = scn.GetLSCard("mon");
        var rebel = scn.GetLSCard("rebel");
        var site = scn.GetDSStartingLocation();

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(declaration);
        scn.MoveCardsToLocation(site, mon, rebel);
        mon.setHit(true);
        assertEquals(8, scn.game().getModifiersQuerying().getForfeitWhenForfeiting(scn.gameState(), mon), scn.epsilon);
    }

    @Test
    public void DeclarationOfRebellionOncePerGameMayTakeKlorslugIntoHandFromReserveDeck() {
        var scn = GetScenario();
        var declaration = scn.GetLSCard("declaration");
        var klorslug = scn.GetLSCard("klorslug");

        scn.StartGame();
        scn.MoveCardsToLSSideOfTable(declaration);
        scn.SkipToLSTurn(Phase.DEPLOY);
        scn.MoveCardsToTopOfLSReserveDeck(klorslug);
        //test1: K'lor'slug goes to hand
        //test2: once per game
        assertTrue(scn.LSCardActionAvailable(declaration, "Take K'lor'slug"));
        scn.LSUseCardAction(declaration, "Take K'lor'slug");
        assertTrue(scn.LSHasCardChoiceAvailable(klorslug));
        scn.LSChooseCard(klorslug);
        scn.PassAllResponses();
        assertInZone(Zone.HAND, klorslug); //test1
        scn.DSPass();
        assertTrue(scn.AwaitingLSDeployPhaseActions());
        assertFalse(scn.LSCardActionAvailable(declaration, "Take K'lor'slug")); //test2
    }
}
