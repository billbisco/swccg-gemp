package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class Card_15_133_Tests {
    private VirtualTableScenario scenario() {
        return new VirtualTableScenario(new HashMap<>(Map.of("combo","15_133","chamber","12_76","senate","12_75","mas","12_15","sei","12_27","yoda","12_35")),new HashMap<>(Map.of("senator","12_98")));
    }
    @Test public void SpeakWithTheJediCouncilAndVoteNowStatsAndKeywordsAreCorrect() {
        var scn=scenario(); var card=scn.GetLSCard("combo").getBlueprint();
        assertEquals("Speak With The Jedi Council & Vote Now!",card.getTitle());
        assertEquals(List.of("Speak With The Jedi Council","Vote Now!"),card.getTitles());
        assertEquals(Uniqueness.UNIQUE,card.getUniqueness()); assertEquals(CardSubtype.USED_OR_LOST,card.getCardSubtype());
        assertEquals(4f,card.getDestiny(),scn.epsilon); assertEquals(0,card.getCredits());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet()); assertEquals(Rarity.UNKNOWN,card.getRarity());
        assertEquals("",card.getLore()); assertFalse(card.isHorizontal());
        scn.BlueprintIconCheck(card,List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD,Icon.EPISODE_I));
        scn.BlueprintKeywordCheck(card,List.of()); scn.BlueprintPersonaCheck(card,List.of());
    }
    @Test public void SpeakWithTheJediCouncilAndVoteNowDeploysChamberAsUsed() {
        var scn=scenario(); var combo=scn.GetLSCard("combo"); var chamber=scn.GetLSCard("chamber");
        scn.StartGame(); scn.MoveCardsToHand(combo); scn.MoveCardsToBottomOfLSReserveDeck(chamber); scn.SkipToLSTurn(Phase.DEPLOY);
        scn.LSPlayCard(combo,"Deploy Jedi Council Chamber"); scn.PassAllResponses(); scn.LSChooseCard(chamber); scn.PassAllResponses();
        assertEquals(Zone.LOCATIONS,chamber.getZone()); assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void SpeakWithTheJediCouncilAndVoteNowDeploysCouncilMemberAsLost() {
        var scn=scenario(); var combo=scn.GetLSCard("combo"); var chamber=scn.GetLSCard("chamber"); var yoda=scn.GetLSCard("yoda");
        scn.StartGame(); scn.MoveLocationToTable(chamber); scn.MoveCardsToHand(combo); scn.MoveCardsToBottomOfLSReserveDeck(yoda); scn.EnsureLSForcePile(10); scn.SkipToLSTurn(Phase.DEPLOY);
        assertFalse(scn.LSCardPlayAvailable(combo,"Deploy Jedi Council Chamber"));
        scn.LSPlayCard(combo,"Deploy a Jedi Council member"); scn.PassAllResponses(); scn.LSChooseCard(yoda);
        scn.PassAllResponses();
        assertEquals(chamber,yoda.getAtLocation()); assertTrue(scn.GetLSLostPile().contains(combo));
    }
    private void vote(int destiny, boolean secondSenator, boolean opponent, int expectedDraw) {
        var scn=scenario(); var combo=scn.GetLSCard("combo"); var senate=scn.GetLSCard("senate");
        scn.StartGame(); scn.MoveLocationToTable(senate); scn.MoveCardsToLocation(senate,scn.GetLSCard("mas"));
        if(secondSenator) scn.MoveCardsToLocation(senate,scn.GetLSCard("sei"));
        if(opponent) scn.MoveCardsToLocation(senate,scn.GetDSCard("senator"));
        scn.MoveCardsToHand(combo); scn.SkipToLSTurn(Phase.CONTROL); scn.PrepareLSDestiny(destiny);
        int before=scn.GetLSHandCount();
        scn.LSPlayCard(combo,"Draw destiny"); scn.PassCardPlayResponses(); scn.PassDestinyDrawResponses();
        if(expectedDraw>0) scn.LSDecided(Integer.toString(expectedDraw));
        scn.PassAllResponses();
        assertEquals(before-1+expectedDraw,scn.GetLSHandCount()); assertTrue(scn.GetLSUsedPile().contains(combo));
    }
    @Test public void SpeakWithTheJediCouncilAndVoteNowSubtractsOpponentsPolitics() { vote(3,false,true,1); }
    @Test public void SpeakWithTheJediCouncilAndVoteNowCapsDrawAtFour() { vote(3,true,false,4); }
    @Test public void SpeakWithTheJediCouncilAndVoteNowDoesNotDrawOnDestinyTwo() { vote(2,false,false,0); }
}
