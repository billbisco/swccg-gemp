package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.*;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_087_Tests {
    private VirtualTableScenario scenario(boolean outOfRange) {
        return new VirtualTableScenario(new HashMap<>(Map.of("escape","15_87","ywing","1_147","second","1_147","clouds","5_85")),new HashMap<>(Map.of("tie","1_306","pilot","1_168")),40,40,outOfRange?StartingSetup.LSStartingLocation("1_126"):StartingSetup.DefaultLSSpaceSystem,outOfRange?StartingSetup.DSStartingLocation("12_165"):StartingSetup.DefaultDSSpaceSystem,StartingSetup.NoLSStartingInterrupts,StartingSetup.NoDSStartingInterrupts,StartingSetup.NoLSShields,StartingSetup.NoDSShields,VirtualTableScenario.Open);
    }
    @Test public void HyperEscapeStatsAndKeywordsAreCorrect() {
        var scn=scenario(false);var card=scn.GetLSCard("escape").getBlueprint();assertEquals(Title.Hyper_Escape,card.getTitle());assertEquals(Side.LIGHT,card.getSide());assertEquals(Uniqueness.UNRESTRICTED,card.getUniqueness());assertEquals(5f,card.getDestiny(),scn.epsilon);assertEquals(CardSubtype.USED,card.getCardSubtype());assertEquals(3,card.getCredits());assertEquals("",card.getLore());assertTrue(card.isHorizontal());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());scn.BlueprintCardTypeCheck(card,List.of(CardType.INTERRUPT));scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of());scn.BlueprintPersonaCheck(card,List.of());
    }
    private void escape(boolean outOfRange,boolean two) {
        var scn=scenario(outOfRange);var escape=scn.GetLSCard("escape");var ship=scn.GetLSCard("ywing");var second=scn.GetLSCard("second");var source=scn.GetLSStartingLocation();var destination=scn.GetDSStartingLocation();scn.StartGame();scn.MoveCardsToHand(escape);scn.MoveCardsToLocation(source,ship,scn.GetDSCard("tie"));if(two)scn.MoveCardsToLocation(source,second);scn.BoardAsPilot(scn.GetDSCard("tie"),scn.GetDSCard("pilot"));scn.SkipToDSTurn(Phase.BATTLE);scn.DSInitiateBattle(source);for(int i=0;i<24;i++){if(scn.LSGetDecision()!=null && scn.LSCardPlayAvailable(escape))break;if(scn.LSGetDecision()!=null)scn.LSPass();else scn.DSPass();}int force=scn.GetLSForcePileCount();scn.LSPlayCard(escape);scn.PassAllResponses();if(two){scn.LSChooseCard(ship);scn.PassAllResponses();}
        if(outOfRange){assertEquals(source,ship.getAtLocation());assertEquals(force,scn.GetLSForcePileCount());assertTrue(scn.gameState().isDuringBattle());}else{assertEquals(destination,ship.getAtLocation());if(two)assertEquals(destination,second.getAtLocation());assertEquals(force-(two?2:1),scn.GetLSForcePileCount());assertFalse(scn.gameState().isDuringBattle());}assertTrue(scn.GetLSUsedPile().contains(escape));
    }
    @Test public void HyperEscapeMovesShipPaysNormalMovementAndEndsBattle() {escape(false,false);}
    @Test public void HyperEscapeMovesAllShipsAndPaysForEach() {escape(false,true);}
    @Test public void HyperEscapeDoesNotCancelBattleWhenShipCannotReachDestination() {escape(true,false);}
    @Test public void HyperEscapeDoesNotApplyAtSector() {
        var scn=scenario(false);var escape=scn.GetLSCard("escape");var clouds=scn.GetLSCard("clouds");scn.StartGame();scn.MoveLocationToTable(clouds);scn.MoveCardsToHand(escape);scn.MoveCardsToLocation(clouds,scn.GetLSCard("ywing"),scn.GetDSCard("tie"));scn.BoardAsPilot(scn.GetDSCard("tie"),scn.GetDSCard("pilot"));scn.SkipToDSTurn(Phase.BATTLE);scn.DSInitiateBattle(clouds);scn.DSPass();assertFalse(scn.LSCardPlayAvailable(escape));
    }
}
