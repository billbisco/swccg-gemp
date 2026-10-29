package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.framework.VirtualTableScenario;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class ReflectionsGoldLightParallelSecondStatsTests {
    private final SwccgCardBlueprintLibrary library=new SwccgCardBlueprintLibrary();
    private final VirtualTableScenario scenario=new VirtualTableScenario(new HashMap<>(),new HashMap<>());
    private void check(int id,int destiny,CardSubtype subtype,boolean episode,int credits,boolean horizontal,Uniqueness... uniqueness) {
        var card=library.getSwccgoCardBlueprint("15_"+id);
        assertEquals(Side.LIGHT,card.getSide()); assertEquals(destiny,card.getDestiny(),0.001);
        assertEquals(subtype,card.getCardSubtype()); assertEquals(credits,card.getCredits()); assertEquals(horizontal,card.isHorizontal());
        assertEquals(ExpansionSet.REFLECTIONS_GOLD,card.getExpansionSet()); assertEquals(Rarity.UNKNOWN,card.getRarity()); assertEquals("",card.getLore());
        scenario.BlueprintCardTypeCheck(card,List.of(CardType.INTERRUPT)); scenario.BlueprintKeywordCheck(card,List.of()); scenario.BlueprintPersonaCheck(card,List.of());
        var icons=new ArrayList<>(List.of(Icon.INTERRUPT,Icon.REFLECTIONS_GOLD)); if(episode)icons.add(Icon.EPISODE_I); scenario.BlueprintIconCheck(card,icons);
        assertEquals(Arrays.asList(uniqueness),card.getTitleComponents().stream().map(TitleComponent::uniqueness).toList());
    }
    @Test public void NiceOfYouGuysAndVisoredVisionHasRestrictedTwoSecondComponent() { check(111,3,CardSubtype.USED_OR_LOST,false,0,false,Uniqueness.UNRESTRICTED,Uniqueness.RESTRICTED_2); }
    @Test public void NoGibenAndGimmeALiftHasTwoUniqueComponents() { check(113,5,CardSubtype.LOST,true,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); }
    @Test public void OnTheEdgeAndSurpriseAssaultAreBothUnrestricted() { check(117,3,CardSubtype.LOST,false,0,false,Uniqueness.UNRESTRICTED,Uniqueness.UNRESTRICTED); }
    @Test public void PodracePrepAndTooCloseIsLostOrStarting() { check(121,3,CardSubtype.LOST_OR_STARTING,true,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); }
    @Test public void RebelLeadershipAndThrowMeAnotherChargeBothUnique() { check(124,5,CardSubtype.USED,false,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); }
    @Test public void SabotageAndSlightWeaponsMalfunctionBothUnique() { check(127,5,CardSubtype.USED,false,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); }
    @Test public void SignalAndJediMindTrickHasPrintedUsedOrLostSubtype() { check(139,4,CardSubtype.USED_OR_LOST,false,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); }
    @Test public void WeWishToBoardAndBrainDeadHasPrintedUsedOrLostSubtype() { check(149,4,CardSubtype.USED_OR_LOST,true,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); }
    @Test public void WereYouLookingAndIKnowHasPrintedUsedOrLostSubtype() { check(150,4,CardSubtype.USED_OR_LOST,false,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); }
    @Test public void TakeMeToJabbaAndStrangleHasPrintedUsedOrLostSubtype() { check(154,5,CardSubtype.USED_OR_LOST,false,0,false,Uniqueness.UNIQUE,Uniqueness.UNIQUE); }
    @Test public void NoGibenPreservesCanonicalComponentIdentityDespitePrintedPunctuation() {
        var card=library.getSwccgoCardBlueprint("15_113");assertEquals("No Giben Up, General Jar Jar & Gimme A Lift!",card.getTitle());assertEquals(List.of("No Giben Up, General Jar Jar!","Gimme A Lift!"),card.getTitleComponents().stream().map(TitleComponent::name).toList());
    }
}
