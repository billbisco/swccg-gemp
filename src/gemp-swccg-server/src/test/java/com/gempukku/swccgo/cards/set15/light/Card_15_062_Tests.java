package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_062_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_62","han","1_11","gun","1_154","luke","1_19","obiwan","1_21","saber","1_157")),new HashMap<>(Map.of("vader","1_168","comlink","1_201","circle","1_271")));
    }
    @Test public void ArmedAndDangerousAndAmbushStatsAndKeywordsAreCorrect() {
        var scn=scenario();var card=scn.GetLSCard("combo").getBlueprint();
        assertEquals("Armed And Dangerous & Ambush",card.getTitle());
        assertEquals(List.of(new TitleComponent("Armed And Dangerous",Uniqueness.UNRESTRICTED),new TitleComponent(Title.Ambush,Uniqueness.UNIQUE)),card.getTitleComponents());
        assertEquals(3f,card.getDestiny(),scn.epsilon);assertEquals(CardSubtype.LOST,card.getCardSubtype());assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());
        scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test public void ArmedAndDangerousAndAmbushPlacesEnemyAndAttachedDeviceInUsedPile() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var enemy=scn.GetDSFiller(1);var device=scn.GetDSCard("comlink");var site=scn.GetDSStartingLocation();
        scn.StartGame();scn.MoveCardsToHand(combo);scn.MoveCardsToLocation(site,scn.GetLSCard("luke"),enemy);scn.AttachCardsTo(enemy,device);scn.SkipToLSTurn(Phase.CONTROL);
        scn.LSPlayCard(combo,"Place opponent");scn.LSChooseCard(site);scn.PassAllResponses();
        if(scn.DSGetDecision()!=null)scn.DSChooseCard(device);else scn.LSChooseCard(device);scn.PassAllResponses();
        assertTrue(scn.GetDSUsedPile().contains(device));assertTrue(scn.GetDSUsedPile().contains(enemy));assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void ArmedAndDangerousAndAmbushRequiresMoreThanDoublePower() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToHand(scn.GetLSCard("combo"));
        scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetLSFiller(1),scn.GetLSFiller(2),scn.GetDSFiller(1));scn.SkipToLSTurn(Phase.CONTROL);
        assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("combo"),"Place opponent"));
    }
    private void weapon(boolean outsideBattle,boolean fromHand) {
        var scn=scenario();var combo=scn.GetLSCard("combo");var han=scn.GetLSCard("han");var gun=scn.GetLSCard("gun");var site=scn.GetDSStartingLocation();
        scn.StartGame();scn.MoveCardsToHand(combo);scn.MoveCardsToLocation(site,scn.GetLSCard("luke"),scn.GetDSCard("vader"));
        scn.MoveCardsToLocation(outsideBattle?scn.GetLSStartingLocation():site,han);scn.EnsureLSForcePile(8);scn.SkipToLSTurn(Phase.BATTLE);scn.MoveCardsToTopOfLSReserveDeck(gun);
        if(fromHand)scn.MoveCardsToHand(gun);scn.LSInitiateBattle(site);
        for(int i=0;i<24;i++) {
            if(scn.LSGetDecision()!=null && scn.LSCardPlayAvailable(combo,"Deploy matching"))break;
            if(scn.LSGetDecision()!=null)scn.LSPass();else if(scn.DSGetDecision()!=null)scn.DSPass();
        }
        scn.MoveCardsToTopOfLSUsedPile(scn.GetLSForcePile().toArray(new com.gempukku.swccgo.game.PhysicalCardImpl[0]));
        assertEquals(0,scn.GetLSForcePileCount());scn.LSPlayCard(combo,fromHand?"from hand":"from Reserve Deck");
        if(outsideBattle) { assertFalse(scn.LSHasCardChoiceAvailable(han)); return; }
        scn.LSChooseCard(han);scn.PassAllResponses();if(!fromHand)scn.LSChooseCard(gun);scn.PassAllResponses();
        assertEquals(han,gun.getAttachedTo());assertEquals(0,scn.GetLSForcePileCount());assertTrue(scn.GetLSLostPile().contains(combo));
    }
    @Test public void ArmedAndDangerousAndAmbushDeploysMatchingWeaponFromReserveForFreeWithNoForce() {weapon(false,false);}
    @Test public void ArmedAndDangerousAndAmbushDeploysMatchingWeaponFromHandForFreeWithNoForce() {weapon(false,true);}
    @Test public void ArmedAndDangerousAndAmbushCannotTargetCharacterOutsideBattle() {weapon(true,false);}

    @Test public void ArmedAndDangerousAndAmbushDeploysMatchingDuelistsWeaponAndExcludesBystander() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var obiwan=scn.GetLSCard("obiwan");var han=scn.GetLSCard("han");var vader=scn.GetDSCard("vader");var saber=scn.GetLSCard("saber");
        scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),obiwan,han,vader);scn.MoveCardsToHand(combo,scn.GetDSCard("circle"));scn.MoveCardsToBottomOfLSReserveDeck(saber);scn.SkipToDSTurn(Phase.CONTROL);
        scn.DSPlayCard(scn.GetDSCard("circle"));scn.DSChooseCard(obiwan);scn.DSChooseCard(vader);
        for(int i=0;i<32;i++) {
            if(scn.LSGetDecision()!=null && scn.LSCardPlayAvailable(combo,"Deploy matching"))break;
            if(scn.LSGetDecision()!=null)scn.LSPass();else if(scn.DSGetDecision()!=null)scn.DSPass();
        }
        scn.LSPlayCard(combo,"Deploy matching");assertFalse(scn.LSHasCardChoiceAvailable(han));scn.LSChooseCard(obiwan);scn.PassAllResponses();scn.LSChooseCard(saber);scn.PassAllResponses();
        assertEquals(obiwan,saber.getAttachedTo());assertTrue(scn.GetLSLostPile().contains(combo));
    }
}
