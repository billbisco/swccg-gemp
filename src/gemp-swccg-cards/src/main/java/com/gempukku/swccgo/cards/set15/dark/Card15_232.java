package com.gempukku.swccgo.cards.set15.dark;
import com.gempukku.swccgo.cards.set1.dark.Card1_277;
import com.gempukku.swccgo.cards.set1.dark.Card1_240;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

public class Card15_232 extends Card1_277 {
    private static final ComponentActions COMPONENT = new ComponentActions();
    public Card15_232() {
        setLore(""); setGameText("If Vader is defending a battle alone at a site, add 1 to power and add one battle destiny. OR If any other Imperial with ability > 2 is defending a battle alone at a site, add one battle destiny. OR If one of your Dark Jedi is present during any battle, use 1 Force to cause all other Imperials there to battle at double power."); setAlternateDestiny(4);
        setTitleComponents(new TitleComponent("Dark Jedi Presence",Uniqueness.UNRESTRICTED),new TitleComponent("Vader's Eye",Uniqueness.UNIQUE));
        addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "Dark Jedi Presence & Vader's Eye"; }
    @Override public Float getDestiny() { return 4f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions=new ArrayList<>();
        List<PlayInterruptAction> first=super.getGameTextTopLevelActions(playerId, game, self);
        if(first!=null) actions.addAll(first);
        List<PlayInterruptAction> second=COMPONENT.actions(playerId, game, self);
        if(second!=null) actions.addAll(second);
        actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.LOST));
        return actions;
    }
    private static final class ComponentActions extends Card1_240 {
        List<PlayInterruptAction> actions(String playerId, SwccgGame game, PhysicalCard self) { return super.getGameTextTopLevelActions(playerId, game, self); }
    }
}
