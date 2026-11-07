package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 6.1 Protocol Failure. */
public class Card1261_036 extends AbstractImmediateEffect {
    public Card1261_036() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, "Protocol Failure", Uniqueness.UNRESTRICTED,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("'Oh my! Oh, uh, I'm, I'm terribly sorry. I. . . I didn't mean to intrude. No, no, no, please don't get up. No!'");
        setGameText("Deploy on a site to cancel a Force drain there. Vader is deploy -1 here. At end of your turn, place this Effect in Lost Pile. OR At any time, target a unique (•) [Episode I] droid. Opponent chooses: deploy on droid (its game text is canceled), or place this Effect in Lost Pile (you may take any one card into hand from Reserve Deck; reshuffle). (Immune to Control.)");
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
            PlayCardOptionId id, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.forceDrainInitiated(game, result) || !GameConditions.canCancelForceDrain(game, self))
            return null;
        var drain = game.getGameState().getForceDrainState();
        if (drain == null || !Filters.site.accepts(game, drain.getLocation()))
            return null;
        var action = getPlayCardAction(playerId, game, self, self, false, 0, null, null,
                null, null, null, false, 0, Filters.sameLocationId(drain.getLocation()), null);
        return action == null ? null : List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var attachment = self.getAttachedTo();
        if (attachment == null || !Filters.site.accepts(game, attachment))
            return null;
        return List.of(new DeployCostToLocationModifier(self, Filters.Vader, -1, Filters.sameSite(self)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        var attachment = self.getAttachedTo();
        if (attachment == null || !Filters.site.accepts(game, attachment))
            return null;
        var drain = game.getGameState().getForceDrainState();
        if (TriggerConditions.justDeployed(game, result, self) && drain != null
                && Filters.sameLocationId(attachment).accepts(game, drain.getLocation())
                && GameConditions.canCancelForceDrain(game, self)) {
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            action.appendEffect(new CancelForceDrainEffect(action));
            return List.of(action);
        }
        if (TriggerConditions.isEndOfYourTurn(game, result, self)) {
            var action = new RequiredGameTextTriggerAction(self, sourceId);
            action.appendEffect(new LoseCardFromTableEffect(action, self));
            return List.of(action);
        }
        return null;
    }
}
