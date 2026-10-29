package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set1.dark.Card1_253;
import com.gempukku.swccgo.cards.set1.dark.Card1_264;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.List;

public class Card15_257 extends Card1_253 {
    private static final ComponentActions COMPONENT = new ComponentActions();
    public Card15_257() {
        setLore(""); setGameText("Use 1 Force to target opponent's starfighter with maneuver at a system or sector where a battle just finished. Draw destiny. Starfighter lost if destiny > maneuver. OR If a Dark Jedi is present at a battle you have just won, use 1 Force to attempt to choke an opposing character present. Draw destiny. If destiny > target's ability, target is lost.");
        setCardSubtype(CardSubtype.USED); setAlternateDestiny(5);
        addComboCardTitles("I've Got A Problem Here","Physical Choke");
        addIcons(Icon.REFLECTIONS_GOLD);
    }
    @Override public String getTitle() { return "I've Got A Problem Here & Physical Choke"; }
    @Override public Float getDestiny() { return 5f; }
    @Override public ExpansionSet getExpansionSet() { return ExpansionSet.REFLECTIONS_GOLD; }
    @Override public Rarity getRarity() { return Rarity.UNKNOWN; }
    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
        List<PlayInterruptAction> actions = new ArrayList<>();
        List<PlayInterruptAction> first = super.getGameTextOptionalAfterActions(playerId, game, result, self);
        if(first != null) actions.addAll(first);
        List<PlayInterruptAction> second = COMPONENT.actions(playerId, game, result, self);
        if(second != null) actions.addAll(second);
        actions.forEach(action -> action.setPlayedAsSubtype(CardSubtype.USED));
        return actions;
    }
    private static final class ComponentActions extends Card1_264 {
        List<PlayInterruptAction> actions(String playerId, SwccgGame game, EffectResult result, PhysicalCard self) {
            return super.getGameTextOptionalAfterActions(playerId, game, result, self);
        }
    }
}
