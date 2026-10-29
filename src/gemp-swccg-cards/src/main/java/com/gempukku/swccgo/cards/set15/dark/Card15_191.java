package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set2.dark.Card2_121;
import com.gempukku.swccgo.cards.set2.dark.Card2_122;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.List;
public class Card15_191 extends Card2_121 {
    private static final ComponentActions COMPONENT=new ComponentActions();
    public Card15_191() {
        setLore(""); setGameText("If opponent is generating at least 4 more Force than you, deploy on a location to cancel all Force generation at that location. OR Deploy on a location, it is now immune to Revolution. Also, each opponent's destiny draw, you do the following: (0) Put top used card in hand; (1) lose 1 Force; (2) activate 1 Force; (3) lose this Effect; (4) retrieve 1 Force; (5) deploy a character for free; (6+) nothing.");
        setTitleComponents(new TitleComponent(Title.Krayt_Dragon_Bones,Uniqueness.UNIQUE),new TitleComponent("Imperial Justice",Uniqueness.UNRESTRICTED));
        addIcon(Icon.A_NEW_HOPE,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Krayt Dragon Bones & Imperial Justice"; }
    @Override public Float getDestiny() { return 2f; }
    @Override public Uniqueness getUniqueness() { return Uniqueness.UNIQUE; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,EffectResult result,PhysicalCard self,int textId) { return COMPONENT.actions(game,result,self,textId); }
    private static final class ComponentActions extends Card2_122 {
        List<RequiredGameTextTriggerAction> actions(SwccgGame game,EffectResult result,PhysicalCard self,int textId) { return super.getGameTextRequiredAfterTriggers(game,result,self,textId); }
    }
}
