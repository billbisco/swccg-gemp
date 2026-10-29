package com.gempukku.swccgo.cards.set15.light;
import com.gempukku.swccgo.cards.set6.light.Card6_061;
import com.gempukku.swccgo.cards.set6.light.Card6_062;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;

public class Card15_069 extends Card6_061 {
    private static final ChokeActions CHOKE=new ChokeActions();
    public Card15_069() {
        setLore("");addIcon(Icon.JABBAS_PALACE,0);addIcons(Icon.REFLECTIONS_GOLD);
        setTitleComponents(new TitleComponent("Choke",Uniqueness.UNRESTRICTED),new TitleComponent(Title.Blaster_Deflection,Uniqueness.UNIQUE));
        setGameText("USED: Cancel an attempt to use a character weapon to target your character of ability > 4. LOST: If your character of ability > 4 with a lightsaber was just targeted by a blaster, use 3 Force to re-target that blaster to an opponent's character present. OR During your control phase, cancel the game text of one unique (•) alien for remainder of turn. OR If you just forfeited an alien, cancel all remaining attrition against you.");
    }
    @Override public String getTitle() {return "Choke & Blaster Deflection";}
    @Override public Float getDestiny() {return 3f;}
    @Override public ExpansionSet getExpansionSet() {return ExpansionSet.REFLECTIONS_GOLD;}
    @Override public Rarity getRarity() {return Rarity.UNKNOWN;}
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId,SwccgGame game,PhysicalCard self) {
        return asLost(CHOKE.top(playerId,game,self));
    }
    @Override protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) {
        return asLost(CHOKE.after(playerId,game,result,self));
    }
    private List<PlayInterruptAction> asLost(List<PlayInterruptAction> actions) {
        if(actions!=null)for(var action:actions)action.setPlayedAsSubtype(CardSubtype.LOST);return actions;
    }
    private static class ChokeActions extends Card6_062 {
        List<PlayInterruptAction> top(String playerId,SwccgGame game,PhysicalCard self) {return super.getGameTextTopLevelActions(playerId,game,self);}
        List<PlayInterruptAction> after(String playerId,SwccgGame game,EffectResult result,PhysicalCard self) {return super.getGameTextOptionalAfterActions(playerId,game,result,self);}
    }
}
