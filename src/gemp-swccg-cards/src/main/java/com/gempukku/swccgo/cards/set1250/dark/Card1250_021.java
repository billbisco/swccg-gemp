package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.set1115.dark.Card1115_067;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 5: Trained In The Jedi Arts. */
public class Card1250_021 extends Card1115_067 {
    public Card1250_021() {
        setVirtualSuffix(false);
        setGameText("Deploy on table. Your [Permanent Weapon] and [Maintenance] cards are lost. 'Vader' on your Interrupts may be treated as 'Maul or Mara Jade.' Wherever you have an Effect with 'skill' in its game text, your Force drains and battle destiny draws are +1. (Immune to Alter.)");
        addIcons(Icon.EPISODE_I, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
                                                                                  PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isTableChanged(game, result)) return null;

        var cardsToLose = Filters.filterAllOnTable(game,
                Filters.and(Filters.your(self), Filters.or(Icon.PERMANENT_WEAPON, Icon.MAINTENANCE)));
        if (cardsToLose.isEmpty()) return null;

        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setText("Lose your permanent weapon and Maintenance cards");
        action.appendEffect(new LoseCardsFromTableEffect(action, cardsToLose, true));
        return List.of(action);
    }
}
