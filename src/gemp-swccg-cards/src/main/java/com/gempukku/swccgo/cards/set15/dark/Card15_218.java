package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set9.dark.Card9_134;
import com.gempukku.swccgo.cards.set9.dark.Card9_127;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

public class Card15_218 extends Card9_134 {
    private static final RebellionRules REBELLION=new RebellionRules();
    public Card15_218() {
        setLore(""); setGameText("Deploy on Bring Him Before Me. When Vader is present at a battleground site, at start of your turn opponent loses 3 Force unless Luke is captured, out of play or present at a battleground site. Luke is immune to Responsibility Of Command. Also, each time you win a battle, opponent loses 1 Force (cannot be reduced) and stacks lost card here face down. When attempting to cross Luke over (except with Epic Duel), add 3 to total destiny for each card in stack. (Immune to Alter.)");
        addComboCardTitles(Title.Your_Destiny,Title.Insignificant_Rebellion);
        addIcon(Icon.DEATH_STAR_II,0); addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Your Destiny & Insignificant Rebellion"; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game,PhysicalCard self) {
        List<Modifier> modifiers=new ArrayList<>(super.getGameTextWhileActiveInPlayModifiers(game,self));
        modifiers.addAll(REBELLION.modifiers(game,self));
        return modifiers;
    }
    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,EffectResult result,PhysicalCard self,int textId) {
        List<RequiredGameTextTriggerAction> actions=new ArrayList<>();
        List<RequiredGameTextTriggerAction> destiny=super.getGameTextRequiredAfterTriggers(game,result,self,textId);
        if(destiny!=null) actions.addAll(destiny);
        List<RequiredGameTextTriggerAction> rebellion=REBELLION.triggers(game,result,self,textId);
        if(rebellion!=null) actions.addAll(rebellion);
        return actions;
    }
    private static final class RebellionRules extends Card9_127 {
        List<Modifier> modifiers(SwccgGame game,PhysicalCard self) {
            return super.getGameTextWhileActiveInPlayModifiers(game,self);
        }
        List<RequiredGameTextTriggerAction> triggers(SwccgGame game,EffectResult result,PhysicalCard self,int textId) {
            return super.getGameTextRequiredAfterTriggers(game,result,self,textId);
        }
    }
}
