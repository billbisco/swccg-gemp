package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.set1116.dark.Card1116_057;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RestoreCardToNormalEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;

/** Virtual Block 5: Mandalorian Armor (V). */
public class Card1250_015 extends Card1116_057 {
    public Card1250_015() {
        setLore("Contains wrist lasers, rocket dart launchers, a flame projector, a projectable grappling hook, a concussion grenade launcher, four kinds of sensors and a broad band antenna.");
        setGameText("Deploy on your alien of ability < 4. Character gains bounty hunter skill and immunity to attrition < 4. If character just hit, may place this device in Lost Pile (Used Pile if on Jango) to restore it to normal.");
        addIcons(Icon.EPISODE_I, Icon.CLOUD_CITY, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        PhysicalCard wearer = self.getAttachedTo();
        if (wearer != null && TriggerConditions.justHit(game, effectResult, wearer)) {
            var action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Restore wearer to normal");
            if (Filters.Jango_Fett.accepts(game, wearer))
                action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, self));
            else
                action.appendCost(new PlaceCardInLostPileFromTableEffect(action, self));
            action.appendEffect(new RestoreCardToNormalEffect(action, wearer));
            return Collections.singletonList(action);
        }
        return null;
    }
}
