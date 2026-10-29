package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.PhysicalCardImpl;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_045_Tests {
    private VirtualTableScenario scenario() {return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_45","obi","1_21","saber","1_157","away","15_64","next","1_133")),new HashMap<>(Map.of("trooper","1_194","vader","1_168")));}
    private void trigger(VirtualTableScenario scn,PhysicalCardImpl combo,String label) {for(int i=0;i<24;i++){if(scn.LSGetDecision()!=null && scn.LSCardActionAvailable(combo,label))break;if(scn.LSGetDecision()!=null)scn.LSPass();else scn.DSPass();}scn.LSUseCardAction(combo,label);}
    @Test public void ObiWansCapeAndLightsaberProficiencyStatsAndKeywordsAreCorrect() {
        var scn=scenario();var card=scn.GetLSCard("combo").getBlueprint();assertEquals("Obi-Wan's Cape & Lightsaber Proficiency",card.getTitle());assertEquals(List.of("Obi-Wan's Cape",Title.Lightsaber_Proficiency),card.getTitles());assertEquals(Uniqueness.UNIQUE,card.getUniqueness());assertEquals(3f,card.getDestiny(),scn.epsilon);assertEquals(0,card.getCredits());assertEquals("",card.getLore());assertFalse(card.isHorizontal());assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet());assertEquals(Rarity.UNKNOWN,card.getRarity());scn.BlueprintCardTypeCheck(card,List.of(CardType.EFFECT));scn.BlueprintIconCheck(card,List.of(Icon.EFFECT,Icon.REFLECTIONS_GOLD));scn.BlueprintKeywordCheck(card,List.of(Keyword.DEPLOYS_ON_CHARACTERS));scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test public void ObiWansCapeAndLightsaberProficiencyCostsOneRequiresLightsaberAndAddsBattlePowerAndImmunity() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var obi=scn.GetLSCard("obi");var saber=scn.GetLSCard("saber");var site=scn.GetDSStartingLocation();scn.StartGame();scn.MoveCardsToLocation(site,obi,scn.GetDSCard("vader"));scn.MoveCardsToHand(combo);scn.AttachCardsTo(obi,saber);scn.SkipToLSTurn(Phase.DEPLOY);int force=scn.GetLSForcePileCount();scn.LSPlayCard(combo);scn.LSChooseCard(obi);scn.PassAllResponses();assertEquals(force-1,scn.GetLSForcePileCount());assertEquals(5f,scn.game().getModifiersQuerying().getImmunityToAttritionLessThan(scn.gameState(),obi),scn.epsilon);int power=scn.GetPower(obi);scn.SkipToPhase(Phase.BATTLE);scn.LSInitiateBattle(site);scn.PassAllResponses();assertEquals(power+3,scn.GetPower(obi));
    }
    @Test public void ObiWansCapeAndLightsaberProficiencyCannotDeployWithoutLightsaber() {
        var scn=scenario();scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),scn.GetLSCard("obi"));scn.MoveCardsToHand(scn.GetLSCard("combo"));scn.SkipToLSTurn(Phase.DEPLOY);assertFalse(scn.LSCardPlayAvailable(scn.GetLSCard("combo")));
    }
    @Test public void ObiWansCapeAndLightsaberProficiencyAddsOneToForceDrain() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var obi=scn.GetLSCard("obi");var site=scn.GetDSStartingLocation();scn.StartGame();scn.MoveCardsToLocation(site,obi);scn.AttachCardsTo(obi,scn.GetLSCard("saber"),combo);scn.SkipToLSTurn(Phase.CONTROL);scn.LSForceDrainAt(site);trigger(scn,combo,"Add 1");scn.PassAllResponses();assertEquals(2,scn.GetForceDrainTotal());
    }
    private void moveOrLose(boolean move) {
        var scn=scenario();var combo=scn.GetLSCard("combo");var obi=scn.GetLSCard("obi");var trooper=scn.GetDSCard("trooper");var site=scn.GetDSStartingLocation();var next=scn.GetLSCard("next");scn.StartGame();scn.MoveLocationToTable(next);scn.MoveCardsToLocation(site,obi,trooper);scn.AttachCardsTo(obi,scn.GetLSCard("saber"),combo);scn.SkipToLSTurn(Phase.BATTLE);scn.LSInitiateBattle(site);int force=scn.GetLSForcePileCount();int ds=scn.GetDSForcePileCount();trigger(scn,combo,"Make a character");scn.LSChooseCard(trooper);scn.PassAllResponses();if(move){scn.DSChooseYes();}else scn.DSChooseNo();scn.PassAllResponses();assertEquals(force-1,scn.GetLSForcePileCount());assertEquals(ds,scn.GetDSForcePileCount());if(move)assertEquals(next,trooper.getAtLocation());else assertTrue(scn.GetDSLostPile().contains(trooper));
    }
    @Test public void ObiWansCapeAndLightsaberProficiencyMovesAbilityOneCharacterForFree() {moveOrLose(true);}
    @Test public void ObiWansCapeAndLightsaberProficiencyLosesCharacterWhoDeclinesMove() {moveOrLose(false);}
    @Test public void ObiWansCapeAndLightsaberProficiencyIsLostWhenLightsaberLeavesCharacter() {
        var scn=scenario();var combo=scn.GetLSCard("combo");var obi=scn.GetLSCard("obi");var saber=scn.GetLSCard("saber");var away=scn.GetLSCard("away");scn.StartGame();scn.MoveCardsToLocation(scn.GetDSStartingLocation(),obi);scn.AttachCardsTo(obi,saber,combo);scn.MoveCardsToHand(away);scn.SkipToLSTurn(Phase.CONTROL);scn.LSPlayCard(away,"Place devices");scn.LSChooseCard(saber);scn.PassAllResponses();assertTrue(scn.GetLSUsedPile().contains(saber));assertTrue(scn.GetLSLostPile().contains(combo));
    }
}
